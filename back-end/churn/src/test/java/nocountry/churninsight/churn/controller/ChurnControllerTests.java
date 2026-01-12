package nocountry.churninsight.churn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.dto.StatsDTO;
import nocountry.churninsight.churn.exception.InvalidChurnDataException;
import nocountry.churninsight.churn.service.PredictionService;
import nocountry.churninsight.churn.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest foca apenas neste Controller específico
@WebMvcTest(ChurnController.class)
class ChurnControllerTests {

    @Autowired
    private MockMvc mockMvc; // Simula o cliente HTTP (Postman)

    @MockBean
    private PredictionService predictionService; // "Finge" o serviço de previsão

    @MockBean
    private StatsService statsService; // "Finge" o serviço de estatísticas

    @Autowired
    private ObjectMapper objectMapper; // Converte Objetos Java <-> JSON

    // --- TESTES DE PREVISÃO INDIVIDUAL (/predict) ---

    @Test
    @DisplayName("POST /churn/predict - Deve retornar 200 OK e a previsão quando dados são válidos")
    void deveRetornarPrevisaoComSucesso() throws Exception {
        // 1. Preparar o cenário (Given)
        ChurnDataDTO inputDTO = criarDtoValido();

        PredictDTO expectedOutput = new PredictDTO("Vai cancelar", 0.85);

        // Ensinamos o Mock: "Quando chamarem o predict, retorne expectedOutput"
        when(predictionService.predict(any(ChurnDataDTO.class))).thenReturn(expectedOutput);

        // 2. Executar e Validar (When/Then)
        mockMvc.perform(post("/churn/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO))) // Transforma o objeto em JSON String
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previsao").value("Vai cancelar"))
                .andExpect(jsonPath("$.probabilidade").value(0.85));
    }

    @Test
    @DisplayName("POST /churn/predict - Deve retornar 422 quando houver erro de consistência")
    void deveRetornar422QuandoDadosInconsistentes() throws Exception {
        ChurnDataDTO inputDTO = criarDtoValido();

        // Simula uma exceção no serviço
        when(predictionService.predict(any())).thenThrow(new InvalidChurnDataException("Erro de consistência"));

        mockMvc.perform(post("/churn/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /churn/predict - Deve retornar 500 quando o serviço falhar")
    void deveRetornar500QuandoServicoFalhar() throws Exception {
        ChurnDataDTO inputDTO = criarDtoValido(); // DTO completo evita o 400

        when(predictionService.predict(any())).thenThrow(new RuntimeException("Erro catastrófico"));

        mockMvc.perform(post("/churn/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isInternalServerError()); // Espera 500
        }

    // --- TESTES DE ESTATÍSTICAS (/stats) ---

    @Test
    @DisplayName("GET /churn/stats - Deve retornar estatísticas corretamente")
    void deveRetornarStats() throws Exception {
        // Mock do retorno
        StatsDTO stats = StatsDTO.builder()
                .totalClients(100L)
                .totalPredictions(150L)
                .churnRate(0.25)
                .retainedClients(75L)
                .churnedClients(25L)
                .build(); // Ex: 100 avaliados, 25% churn
        
        when(statsService.getBasicStats()).thenReturn(stats);

        mockMvc.perform(get("/churn/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_clientes").value(100))
                .andExpect(jsonPath("$.total_previsoes").value(150))
                .andExpect(jsonPath("$.taxa_churn").value(0.25))
                .andExpect(jsonPath("$.clientes_retidos").value(75))
                .andExpect(jsonPath("$.clientes_churn").value(25));
    }

    // --- TESTES DE UPLOAD CSV (/upload) ---

    @Test
    @DisplayName("POST /churn/upload - Deve processar arquivo CSV válido")
    void deveProcessarUploadCsv() throws Exception {
        // 1. Cria um arquivo falso em memória
        MockMultipartFile csvFile = new MockMultipartFile(
                "file",             // nome do parâmetro no Controller (@RequestParam("file"))
                "clientes.csv",     // nome do arquivo original
                "text/csv",         // tipo de conteúdo
                "id,nome,valor\n1,teste,100".getBytes() // conteúdo do arquivo
        );

        // 2. Mock da resposta do serviço (lista de previsões)
        List<PredictDTO> respostaLista = Arrays.asList(
                new PredictDTO("Vai cancelar", 0.9),
                new PredictDTO("Vai ficar", 0.1)
        );

        when(predictionService.processCsv(any())).thenReturn(respostaLista);

        // 3. Executa o upload multipart
        mockMvc.perform(multipart("/churn/upload").file(csvFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // Verifica se retornou 2 itens
                .andExpect(jsonPath("$[0].previsao").value("Vai cancelar"));
    }

    @Test
    @DisplayName("POST /churn/upload - Deve retornar 400 Bad Request se arquivo estiver vazio")
    void deveRejeitarArquivoVazio() throws Exception {
        // Arquivo sem conteúdo (0 bytes)
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "vazio.csv", "text/csv", new byte[0]);

        mockMvc.perform(multipart("/churn/upload").file(emptyFile))
                .andExpect(status().isBadRequest()); // O seu código tem if (file.isEmpty()) -> badRequest
        }

        private ChurnDataDTO criarDtoValido() {
                ChurnDataDTO dto = new ChurnDataDTO();
                dto.setGenero("Feminino"); // Padrão do Pattern
                dto.setIdoso(0);           // Min 0, Max 1
                dto.setConjuge("Sim");     // Pattern Sim|Não
                dto.setDependentes("Não");
                dto.setTempoContrato(12);  // Max 72
                dto.setServicoTelefone("Sim");
                dto.setMultiplasLinhasTel("Não"); // Note que o setter deve bater com o campo da classe
                dto.setServicoInternet("Fibra Ótica"); // DSL|Fibra Ótica|Nenhum
                dto.setSegurancaOnline("Sim");
                dto.setBackupOnline("Sim");
                dto.setProtecaoDispositivo("Sim");
                dto.setSuporteTecnico("Sim");
                dto.setTvStreaming("Sim");
                dto.setFilmesStreaming("Sim");
                dto.setTipoContrato("Mensal"); // Mensal|Anual|Bianual
                dto.setFaturaOnline("Sim");
                dto.setMetodoPagamento("Pix"); // Pix|Ted|Boleto...
                dto.setValorMensal(100.0);
                dto.setValorTotal(1200.0);
                return dto;
        }
}
