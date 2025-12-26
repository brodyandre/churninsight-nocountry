package nocountry.churninsight.churn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.service.PredictionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PredictController.class)
class PredictControllerTests {

    @Autowired
    private MockMvc mockMvc; // Simula as requisições HTTP

    @MockBean
    private PredictionService predictionService; // "Finge" o serviço

    @Autowired
    private ObjectMapper objectMapper; // Converte Java <-> JSON

    @Test
    @DisplayName("Deve retornar 200 OK e a previsão quando o JSON é válido")
    void deveRetornarSucessoComDadosValidos() throws Exception {
        // 1. DADOS (Cenário)
        ChurnDataDTO inputValido = new ChurnDataDTO();
        // IMPORTANTE: Se o seu DTO tiver validações (@NotNull, @Min), 
        // preencha este objeto com dados corretos aqui.
        // ex: inputValido.setTempoContrato(12);

        PredictDTO respostaEsperada = new PredictDTO("Vai cancelar", 0.95);

        // 2. MOCK (Comportamento)
        when(predictionService.predict(any(ChurnDataDTO.class))).thenReturn(respostaEsperada);

        // 3. AÇÃO E VERIFICAÇÃO
        mockMvc.perform(post("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previsao").value("Vai cancelar"))
                .andExpect(jsonPath("$.probabilidade").value(0.95));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o JSON for inválido (Validação @Valid)")
    void deveRetornarErroComDadosInvalidos() throws Exception {
        // 1. DADOS (Cenário inválido)
        // Enviando um JSON vazio "{}" para forçar erro de validação
        // (Assumindo que ChurnDataDTO tem campos obrigatórios anotados com @NotNull)
        String jsonVazio = "{}";

        // 2. AÇÃO
        mockMvc.perform(post("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonVazio))
                .andExpect(status().isBadRequest()); // Espera o erro 400

        // 3. VERIFICAÇÃO EXTRA
        // Garante que o serviço NEM CHEGOU a ser chamado (economia de processamento)
        verify(predictionService, never()).predict(any());
    }
}