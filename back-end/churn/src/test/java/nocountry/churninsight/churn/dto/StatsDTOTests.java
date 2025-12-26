package nocountry.churninsight.churn.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StatsDTOJsonTests {

    @Autowired
    private JacksonTester<StatsDTO> json;

    @Test
    @DisplayName("Deve serializar para JSON com os nomes em snake_case corretos")
    void deveSerializarCorretamente() throws IOException {
        // 1. Cria o objeto Java
        StatsDTO stats = StatsDTO.builder()
                .totalClients(100L)
                .churnRate(0.5)
                .build();

        // 2. Serializa para JSON
        var jsonContent = json.write(stats);

        // 3. Verifica se as chaves (Keys) estão como definido no @JsonProperty
        // Verifica se existe "total_clientes" e NÃO "totalClients"
        assertThat(jsonContent).hasJsonPathNumberValue("@.total_clientes");
        assertThat(jsonContent).extractingJsonPathNumberValue("@.total_clientes").isEqualTo(100);
        
        assertThat(jsonContent).hasJsonPathNumberValue("@.taxa_churn");
        assertThat(jsonContent).extractingJsonPathNumberValue("@.taxa_churn").isEqualTo(0.5);

        // Garante que não vazou o nome da variável Java
        assertThat(jsonContent).doesNotHaveJsonPath("$.totalClients");
    }

    @Test
    @DisplayName("Deve desserializar JSON (snake_case) para Objeto Java corretamente")
    void deveDesserializarCorretamente() throws IOException {
        // 1. JSON simulado vindo do front ou de outro serviço
        String jsonEntrada = """
            {
                "total_clientes": 200,
                "total_previsoes": 50,
                "taxa_churn": 0.1,
                "clientes_retidos": 180,
                "clientes_churn": 20
            }
        """;

        // 2. Converte JSON -> Java
        StatsDTO objeto = json.parse(jsonEntrada).getObject();

        // 3. Valida se o Java entendeu o mapeamento
        assertThat(objeto.getTotalClients()).isEqualTo(200L);
        assertThat(objeto.getChurnRate()).isEqualTo(0.1);
        assertThat(objeto.getRetainedClients()).isEqualTo(180L);
    }
}