
package nocountry.churninsight.churn.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;  // ← ADICIONADO
import org.springframework.boot.test.context.SpringBootTest;  // ← ADICIONADO
import org.springframework.context.ApplicationContext;        // ← ADICIONADO
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class AppConfigTests {

    @Test
    @DisplayName("Deve configurar RestTemplate com timeouts corretos")
    void restTemplateConfigurationTest() {
        // 1. Instancia a configuração diretamente (Teste Unitário puro é mais rápido)
        AppConfig appConfig = new AppConfig();

        // 2. Executa o método que cria o bean
        RestTemplate restTemplate = appConfig.restTemplate();

        // 3. Verificações básicas
        assertThat(restTemplate).isNotNull();

        // Verificamos se a fábrica de requisições é do tipo simples (padrão JDK)
        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
        assertThat(requestFactory).isInstanceOf(SimpleClientHttpRequestFactory.class);

        // 4. Verificação Avançada (Reflection)
        // Como 'connectTimeout' e 'readTimeout' são privados na fábrica,
        // usamos o ReflectionTestUtils para garantir que você configurou os 10s e 30s.

        int connectTimeout = (int) ReflectionTestUtils.getField(requestFactory, "connectTimeout");
        int readTimeout = (int) ReflectionTestUtils.getField(requestFactory, "readTimeout");

        // Asserções dos valores exatos (em milissegundos)
        assertThat(connectTimeout).isEqualTo(10_000); // 10 segundos
        assertThat(readTimeout).isEqualTo(30_000);    // 30 segundos
    }
}

@SpringBootTest(classes = AppConfig.class)
class AppConfigIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("O Spring Context deve conter o bean RestTemplate")
    void deveCarregarBeanNoContexto() {
        // Verifica se o bean existe no contexto do Spring
        boolean existeBean = context.containsBean("restTemplate");
        assertThat(existeBean).isTrue();

        // Verifica se conseguimos injetá-lo
        RestTemplate bean = context.getBean(RestTemplate.class);
        assertThat(bean).isNotNull();
    }
}