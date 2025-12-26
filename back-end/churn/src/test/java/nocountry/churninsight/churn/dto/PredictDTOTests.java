package nocountry.churninsight.churn.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class PredictDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --- 1. TESTES DE LÓGICA DO CONSTRUTOR (Matemática da Confiança) ---

    @Test
    @DisplayName("Deve calcular confiança automaticamente no construtor de 2 argumentos")
    void deveCalcularConfiancaAutomaticamente() {
        // Cenário: Probabilidade 0.9 (90%)
        // Cálculo esperado: |0.9 - 0.5| * 2 = 0.4 * 2 = 0.8
        PredictDTO dto = new PredictDTO("Vai cancelar", 0.9);

        assertThat(dto.getConfianca()).isEqualTo(0.8, offset(0.0001));
        assertThat(dto.getPrevisao()).isEqualTo("Vai cancelar");
    }

    @ParameterizedTest
    @CsvSource({
        "1.0, 1.0",  // Certeza total (100%) -> Confiança 100%
        "0.0, 1.0",  // Certeza total (0%)   -> Confiança 100%
        "0.5, 0.0",  // Incerteza total (50%)-> Confiança 0%
        "0.75, 0.5"  // Meio termo (75%)     -> Confiança 50%
    })
    @DisplayName("Deve calcular a matemática da confiança corretamente para vários cenários")
    void deveValidarMatematicaDaConfianca(double probabilidade, double confiancaEsperada) {
        PredictDTO dto = new PredictDTO("Vai continuar", probabilidade);
        
        // Usamos 'offset' para comparar doubles com segurança (ponto flutuante)
        assertThat(dto.getConfianca()).isEqualTo(confiancaEsperada, offset(0.0001));
    }

    // --- 2. TESTES DE VALIDAÇÃO (Annotations) ---

    @Test
    @DisplayName("Deve aceitar um DTO válido")
    void devePassarComDadosValidos() {
        PredictDTO dto = new PredictDTO("Vai cancelar", 0.75, 0.5);
        
        Set<ConstraintViolation<PredictDTO>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar previsão que não segue o padrão exato")
    void deveValidarPadraoPrevisao() {
        // Testando valores fora do regex "Vai cancelar|Vai continuar"
        PredictDTO dto = new PredictDTO("Talvez cancele", 0.5);

        Set<ConstraintViolation<PredictDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("Previsão deve ser 'Vai cancelar' ou 'Vai continuar'");
    }

    @Test
    @DisplayName("Deve rejeitar probabilidades fora do intervalo 0.0 - 1.0")
    void deveValidarLimitesProbabilidade() {
        PredictDTO dtoAlto = new PredictDTO("Vai continuar", 1.5); // > 1.0
        PredictDTO dtoBaixo = new PredictDTO("Vai continuar", -0.1); // < 0.0

        Set<ConstraintViolation<PredictDTO>> violationsAlto = validator.validate(dtoAlto);
        Set<ConstraintViolation<PredictDTO>> violationsBaixo = validator.validate(dtoBaixo);

        assertThat(violationsAlto).anyMatch(v -> v.getMessage().contains("Probabilidade não pode ser maior que 1"));
        assertThat(violationsBaixo).anyMatch(v -> v.getMessage().contains("Probabilidade não pode ser negativa"));
    }

    @Test
    @DisplayName("Deve rejeitar confiança inválida (caso seja setada manualmente)")
    void deveValidarLimitesConfianca() {
        // Usando o construtor de 3 argumentos para forçar uma confiança errada
        PredictDTO dto = new PredictDTO("Vai continuar", 0.5, 20.0); 

        Set<ConstraintViolation<PredictDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getMessage().contains("Confiança não pode ser maior que 1"));
    }
}