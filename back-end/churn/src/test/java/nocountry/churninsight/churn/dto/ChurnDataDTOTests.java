package nocountry.churninsight.churn.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChurnDataDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Inicializa o validador padrão do Java (o mesmo que o Spring usa)
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --- HELPER: Cria um objeto 100% válido para usar de base ---
    private ChurnDataDTO criarDtoValido() {
        return new ChurnDataDTO(
                "Masculino", // genero
                0, // idoso
                "Sim", // conjuge
                "Não", // dependentes
                12, // tempoContrato
                "Sim", // servicoTelefone
                "Não", // multiplasLinhasTel
                "Fibra Ótica", // servicoInternet
                "Sim", // segurancaOnline
                "Sim", // backupOnline
                "Sim", // protecaoDispositivo
                "Sim", // suporteTecnico
                "Sim", // tvStreaming
                "Sim", // filmesStreaming
                "Mensal", // tipoContrato
                "Sim", // faturaOnline
                "Pix", // metodoPagamento
                100.00, // valorMensal
                1200.00 // valorTotal
        );
    }

    @Test
    @DisplayName("Deve passar sem erros quando todos os dados são válidos")
    void deveAceitarDtoValido() {
        ChurnDataDTO dto = criarDtoValido();

        Set<ConstraintViolation<ChurnDataDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Não deveria haver violações de validação");
    }

    @Test
    @DisplayName("Deve falhar quando Gênero não corresponde ao padrão (Regex)")
    void deveRejeitarGeneroInvalido() {
        ChurnDataDTO dto = criarDtoValido();
        dto.setGenero("Outro"); // Valor não permitido pelo regex

        Set<ConstraintViolation<ChurnDataDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Gênero deve ser 'Masculino' ou 'Feminino'"));
    }

    @Test
    @DisplayName("Deve falhar quando valores numéricos (Idoso/Tempo) estão fora do limite")
    void deveRejeitarNumerosForaDosLimites() {
        ChurnDataDTO dto = criarDtoValido();
        dto.setIdoso(2); // Máximo é 1
        dto.setTempoContrato(-5); // Mínimo é 0

        Set<ConstraintViolation<ChurnDataDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(2);

        // Verifica mensagens específicas
        List<String> mensagens = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertThat(mensagens).contains("Idoso deve ser 0 ou 1");
        assertThat(mensagens).contains("Tempo de contrato não pode ser negativo");
    }

    @Test
    @DisplayName("Deve falhar quando campos obrigatórios estão em branco ou nulos")
    void deveRejeitarCamposEmBranco() {
        ChurnDataDTO dto = criarDtoValido();
        dto.setServicoInternet(""); // @NotBlank falha aqui
        dto.setMetodoPagamento(null); // @NotBlank falha aqui também

        Set<ConstraintViolation<ChurnDataDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("servicoInternet"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("metodoPagamento"));
    }

    @Test
    @DisplayName("Deve validar regex complexo de Pagamento e Contrato")
    void deveValidarListasDeOpcoes() {
        ChurnDataDTO dto = criarDtoValido();

        // Testando valores que NÃO estão na lista permitida
        dto.setMetodoPagamento("Bitcoin");
        dto.setTipoContrato("Vitalício");

        Set<ConstraintViolation<ChurnDataDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getMessage().contains("Método de Pagamento inválido"));
        // Nota: A mensagem no seu código diz 'Month-to-month', mas o regex aceita
        // 'Mensal'.
        // O teste valida se o REGEX funcionou, independente da mensagem estar em inglês
        // ou português.
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("tipoContrato"));
    }
}