package nocountry.churninsight.churn.controller;

import nocountry.churninsight.churn.dto.CsvImportResponseDTO;
import nocountry.churninsight.churn.dto.StatsDTO;
import nocountry.churninsight.churn.service.CsvImportService;
import nocountry.churninsight.churn.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsAndImportController.class)
class StatsAndImportControllerTests { // <--- Ajustado para o plural

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    @MockBean
    private CsvImportService csvImportService;

    // --- TESTES DE ESTATÍSTICAS ---

    @Test
    @DisplayName("GET /api/stats - Deve retornar 200 OK e dados estatísticos")
    void deveRetornarEstatisticas() throws Exception {
        StatsDTO statsSimulado = new StatsDTO(150L, 12.5); 
        
        when(statsService.getBasicStats()).thenReturn(statsSimulado);

        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_avaliados").value(150))
                .andExpect(jsonPath("$.taxa_churn").value(12.5));
    }

    // --- TESTES DE IMPORTAÇÃO CSV ---

    @Test
    @DisplayName("POST /import/clients - Deve processar CSV válido com sucesso")
    void deveImportarCsvComSucesso() throws Exception {
        MockMultipartFile arquivoValido = new MockMultipartFile(
                "file", "clientes.csv", "text/csv", "id,nome\n1,Teste".getBytes()
        );

        CsvImportResponseDTO respostaSucesso = CsvImportResponseDTO.builder()
                .successCount(10)
                .failureCount(0)
                .message("Importação concluída")
                .build();

        when(csvImportService.importClientsFromCsv(any())).thenReturn(respostaSucesso);

        mockMvc.perform(multipart("/api/import/clients").file(arquivoValido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(10));
    }

    @Test
    @DisplayName("POST /import/clients - Deve retornar 400 se arquivo estiver vazio")
    void deveRejeitarArquivoVazio() throws Exception {
        MockMultipartFile arquivoVazio = new MockMultipartFile(
                "file", "vazio.csv", "text/csv", new byte[0]);

        mockMvc.perform(multipart("/api/import/clients").file(arquivoVazio))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("File is empty"));
    }

    @Test
    @DisplayName("POST /import/clients - Deve retornar 500 se houver falha crítica no serviço")
    void deveRetornarErroInternoQuandoServicoFalha() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "file", "teste.csv", "text/csv", "dados".getBytes());

        CsvImportResponseDTO respostaErro = CsvImportResponseDTO.builder()
                .successCount(0)
                .failureCount(-1) // Código de erro interno
                .message("Erro fatal")
                .build();

        when(csvImportService.importClientsFromCsv(any())).thenReturn(respostaErro);

        mockMvc.perform(multipart("/api/import/clients").file(arquivo))
                .andExpect(status().isInternalServerError());
    }
}