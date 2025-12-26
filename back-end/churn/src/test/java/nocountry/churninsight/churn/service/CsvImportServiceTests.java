package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.CsvImportResponseDTO;
import nocountry.churninsight.churn.model.Cliente;
import nocountry.churninsight.churn.model.GeneroEnum;
import nocountry.churninsight.churn.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvImportServiceTests {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private CsvImportService csvImportService;

    // Helper para criar CSVs em memória rapidamente
    private MockMultipartFile criarCsvMock(String conteudo) {
        return new MockMultipartFile(
                "file",
                "clientes.csv",
                "text/csv",
                conteudo.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    @DisplayName("Deve importar CSV válido com sucesso e salvar no repositório")
    void deveImportarComSucesso() {
        // 1. Dados: Cabeçalho + 1 Linha válida (Assumindo que GeneroEnum tem 'FEMININO')
        String csvContent = """
                genero,idoso,conjuge,dependentes,tempo_contrato
                FEMININO,0,Sim,Não,12
                """;

        MockMultipartFile file = criarCsvMock(csvContent);

        // 2. Execução
        CsvImportResponseDTO response = csvImportService.importClientsFromCsv(file);

        // 3. Validação
        assertThat(response.getSuccessCount()).isEqualTo(1);
        assertThat(response.getFailureCount()).isZero();
        
        // Verifica se o repository.save foi chamado 1 vez
        verify(clientRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lidar com falha parcial (1 linha válida, 1 inválida)")
    void deveLidarComFalhaParcial() {
        // Linha 1: Válida (MASCULINO)
        // Linha 2: Inválida (Gênero 'ALIEN' não existe no Enum ou Número inválido)
        String csvContent = """
                genero,idoso,tempo_contrato
                MASCULINO,0,12
                ALIEN,TEXTO_EM_VEZ_DE_NUMERO,5
                """;

        MockMultipartFile file = criarCsvMock(csvContent);

        // Execução
        CsvImportResponseDTO response = csvImportService.importClientsFromCsv(file);

        // Validação
        assertThat(response.getSuccessCount()).isEqualTo(1); // A 1ª linha passou
        assertThat(response.getFailureCount()).isEqualTo(1); // A 2ª linha falhou
        
        // Repository só deve ter sido chamado para a linha válida
        verify(clientRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve ignorar colunas que não existem no CSV sem quebrar")
    void deveIgnorarColunasFaltantes() {
        // CSV faltando a coluna 'conjuge' e 'dependentes', mas tendo as obrigatórias para o teste
        String csvContent = """
                genero,idoso,tempo_contrato
                FEMININO,1,24
                """;

        MockMultipartFile file = criarCsvMock(csvContent);

        CsvImportResponseDTO response = csvImportService.importClientsFromCsv(file);

        assertThat(response.getSuccessCount()).isEqualTo(1);
        assertThat(response.getFailureCount()).isZero();
    }

    @Test
    @DisplayName("Deve retornar erro crítico (-1) se houver IOException ao ler o arquivo")
    void deveRetornarErroCriticoEmFalhaDeLeitura() throws IOException {
        // 1. Mockamos o MultipartFile para forçar uma exceção de IO
        // (MockMultipartFile normal não lança IOException, por isso usamos mock do Mockito)
        MultipartFile fileComDefeito = mock(MultipartFile.class);
        
        when(fileComDefeito.getInputStream()).thenThrow(new IOException("Disco cheio ou arquivo corrompido"));

        // 2. Execução
        CsvImportResponseDTO response = csvImportService.importClientsFromCsv(fileComDefeito);

        // 3. Validação
        assertThat(response.getSuccessCount()).isEqualTo(0);
        assertThat(response.getFailureCount()).isEqualTo(-1); // Código de erro fatal definido no seu Service
        assertThat(response.getMessage()).contains("Error reading CSV file");
    }

    @Test
    @DisplayName("Deve falhar linha se formato numérico estiver incorreto")
    void deveFalharPorNumberFormatException() {
        String csvContent = """
                genero,idoso,tempo_contrato
                FEMININO,0,DOZE_MESES
                """; // 'DOZE_MESES' vai quebrar o Integer.parseInt

        MockMultipartFile file = criarCsvMock(csvContent);

        CsvImportResponseDTO response = csvImportService.importClientsFromCsv(file);

        assertThat(response.getSuccessCount()).isZero();
        assertThat(response.getFailureCount()).isEqualTo(1);
        verify(clientRepository, never()).save(any());
    }
}