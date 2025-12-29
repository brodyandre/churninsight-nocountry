package nocountry.churninsight.churn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.dto.StatsDTO;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        ChurnDataDTO inputDTO = new ChurnDataDTO(); 
        // (Preencha com dados fictícios se o DTO tiver validação @NotNull)
        
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
    @DisplayName("POST /churn/predict - Deve retornar 500 Erro Interno quando o serviço falhar")
    void deveRetornar500QuandoServicoFalhar() throws Exception {
        // Simula uma exceção no serviço
        when(predictionService.predict(any())).thenThrow(new RuntimeException("Erro de conexão com Python"));

        mockMvc.perform(post("/churn/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    // --- TESTES DE ESTATÍSTICAS (/stats) ---

    @Test
    @DisplayName("GET /churn/stats - Deve retornar estatísticas corretamente")
    void deveRetornarStats() throws Exception {
        // Mock do retorno
        StatsDTO stats = new StatsDTO(100L, 0.25); // Ex: 100 avaliados, 25% churn
        
        when(statsService.getBasicStats()).thenReturn(stats);

        mockMvc.perform(get("/churn/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_avaliados").value(100))
                .andExpect(jsonPath("$.taxa_churn").value(0.25));
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
}