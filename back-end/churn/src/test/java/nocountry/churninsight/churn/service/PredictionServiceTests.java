package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.exception.IntegrationException;
import nocountry.churninsight.churn.exception.ValidationBusinessException;
import nocountry.churninsight.churn.validator.ChurnDataValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTests {

    @Mock
    private ChurnDataValidator churnDataValidator;

    @InjectMocks
    private PredictionService predictionService;

    // --- TESTES DO MÉTODO PREDICT (Individual) ---

    @Test
    @DisplayName("Deve prever 'Vai cancelar' para clientes com valor mensal > 100")
    void devePreverCancelamentoParaValorAlto() {
        // 1. Cenário
        ChurnDataDTO dto = new ChurnDataDTO();
        dto.setValorMensal(150.00); // Valor acima de 100
        dto.setTipoContrato("Mensal");

        // Mock do validador (não retorna erros)
        when(churnDataValidator.validate(dto)).thenReturn(Collections.emptyList());

        // 2. Execução
        PredictDTO resultado = predictionService.predict(dto);

        // 3. Validação
        assertThat(resultado.getPrevisao()).isEqualTo("Vai cancelar");
        assertThat(resultado.getProbabilidade()).isEqualTo(0.82);
    }

    @Test
    @DisplayName("Deve prever 'Vai continuar' para clientes com valor mensal <= 100")
    void devePreverContinuacaoParaValorBaixo() {
        // 1. Cenário
        ChurnDataDTO dto = new ChurnDataDTO();
        dto.setValorMensal(50.00); // Valor baixo
        dto.setTipoContrato("Anual");

        when(churnDataValidator.validate(dto)).thenReturn(Collections.emptyList());

        // 2. Execução
        PredictDTO resultado = predictionService.predict(dto);

        // 3. Validação
        assertThat(resultado.getPrevisao()).isEqualTo("Vai continuar");
        assertThat(resultado.getProbabilidade()).isEqualTo(0.15);
    }

    @Test
    @DisplayName("Deve lançar ValidationBusinessException se o validador encontrar erros")
    void deveFalharNaValidacaoDeNegocio() {
        // 1. Cenário
        ChurnDataDTO dto = new ChurnDataDTO();
        
        // Mock do validador retornando uma lista de erros
        when(churnDataValidator.validate(dto)).thenReturn(List.of("Erro 1", "Erro 2"));

        // 2. Execução e Validação
        assertThrows(ValidationBusinessException.class, () -> {
            predictionService.predict(dto);
        });
    }

    // --- TESTES DO MÉTODO PROCESS CSV (Lote) ---

    @Test
    @DisplayName("Deve processar CSV válido corretamente")
    void deveProcessarCsvValido() {
        // 1. Criar CSV Mock (Cabeçalho + 1 Linha válida)
        // Nota: Precisamos de 19 colunas conforme seu código valida (fields.length < 19)
        String csvContent = "genero,idoso,conjuge,dependentes,tempo,tel,multi,internet,seguranca,backup,protecao,suporte,tv,filmes,contrato,fatura,pagto,mensal,total\n" +
                            "Fem,0,Sim,Não,12,Sim,Não,Fibra,Sim,Sim,Sim,Sim,Sim,Sim,Mensal,Sim,Pix,200.0,2400.0";
        
        MockMultipartFile file = new MockMultipartFile(
                "file", "teste.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        // Mock do validador (pois o processCsv chama o predict internamente)
        when(churnDataValidator.validate(any())).thenReturn(Collections.emptyList());

        // 2. Execução
        List<PredictDTO> resultados = predictionService.processCsv(file);

        // 3. Validação
        assertThat(resultados).hasSize(1);
        // Como o valor mensal é 200.0 (>100), deve prever cancelamento
        assertThat(resultados.get(0).getPrevisao()).isEqualTo("Vai cancelar");
    }

    @Test
    @DisplayName("Deve ignorar linhas mal formatadas (Número inválido) sem lançar erro")
    void deveIgnorarLinhasComErroDeParse() {
        // Linha com "TEXTO" onde deveria ser o valor mensal (coluna 18)
        String csvContent = "header...\n" +
                            "Fem,0,Sim,Não,12,Sim,Não,Fibra,Sim,Sim,Sim,Sim,Sim,Sim,Mensal,Sim,Pix,TEXTO_INVALIDO,2400.0";

        MockMultipartFile file = new MockMultipartFile("file", csvContent.getBytes(StandardCharsets.UTF_8));

        // Execução
        List<PredictDTO> resultados = predictionService.processCsv(file);

        // Validação: A lista deve estar vazia, pois a linha foi ignorada (logada como warn), mas não quebrou o app
        assertThat(resultados).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar IntegrationException se houver erro de leitura do arquivo")
    void deveLancarExcecaoEmErroDeIO() throws IOException {
        // 1. Mockar um MultipartFile que falha ao ler
        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.getInputStream()).thenThrow(new IOException("Disco corrompido"));

        // 2. Execução e Validação
        assertThrows(IntegrationException.class, () -> {
            predictionService.processCsv(badFile);
        });
    }
}