package nocountry.churninsight.churn.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvImportResponseDTOTests {

    @Test
    @DisplayName("Deve criar objeto corretamente usando o Builder")
    void deveFuncionarOPadraoBuilder() {
        // 1. Criação usando o Builder (.builder().build())
        CsvImportResponseDTO response = CsvImportResponseDTO.builder()
                .successCount(10)
                .failureCount(2)
                .message("Processamento parcial")
                .build();

        // 2. Verificação
        assertThat(response).isNotNull();
        assertThat(response.getSuccessCount()).isEqualTo(10);
        assertThat(response.getFailureCount()).isEqualTo(2);
        assertThat(response.getMessage()).isEqualTo("Processamento parcial");
    }

    @Test
    @DisplayName("Deve funcionar com Construtor Vazio e Setters (@Data e @NoArgsConstructor)")
    void deveFuncionarGettersESetters() {
        // 1. Criação com construtor vazio
        CsvImportResponseDTO response = new CsvImportResponseDTO();

        // 2. Uso dos Setters
        response.setSuccessCount(50);
        response.setFailureCount(0);
        response.setMessage("Sucesso total");

        // 3. Verificação dos Getters
        assertThat(response.getSuccessCount()).isEqualTo(50);
        assertThat(response.getFailureCount()).isZero();
        assertThat(response.getMessage()).isEqualTo("Sucesso total");
    }

    @Test
    @DisplayName("Deve funcionar o construtor com todos os argumentos (@AllArgsConstructor)")
    void deveFuncionarConstrutorCompleto() {
        // 1. Criação direta via new
        CsvImportResponseDTO response = new CsvImportResponseDTO(100, 5, "Teste");

        // 2. Verificação
        assertThat(response.getSuccessCount()).isEqualTo(100);
        assertThat(response.getFailureCount()).isEqualTo(5);
        assertThat(response.getMessage()).isEqualTo("Teste");
    }

    @Test
    @DisplayName("Deve verificar igualdade corretamente (@Data implementa equals/hashCode)")
    void deveTestarEqualsEHashCode() {
        // Cria dois objetos com os MESMOS valores
        CsvImportResponseDTO obj1 = CsvImportResponseDTO.builder()
                .successCount(1)
                .failureCount(0)
                .message("Msg")
                .build();

        CsvImportResponseDTO obj2 = new CsvImportResponseDTO(1, 0, "Msg");

        // Devem ser considerados iguais pelo Java
        assertThat(obj1).isEqualTo(obj2);
        assertThat(obj1.hashCode()).isEqualTo(obj2.hashCode());
    }
}