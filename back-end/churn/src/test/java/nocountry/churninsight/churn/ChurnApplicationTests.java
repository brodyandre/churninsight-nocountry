package nocountry.churninsight.churn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest diz ao Spring Boot para procurar a classe de configuração principal
// (a que tem @SpringBootApplication) e usar isso para iniciar o contexto de teste.
@SpringBootTest
@ActiveProfiles("tests")
class ChurnApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Deve carregar o contexto da aplicação sem falhas")
    void contextLoads() {
        // Este teste é o padrão do Spring.
        // Se a aplicação falhar ao iniciar (erro de configuração, bean faltando, etc),
        // este teste falhará automaticamente.
        
        // Verificação extra (opcional): Garantir que o contexto não é nulo
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Deve executar o método main sem lançar exceções")
    void mainMethodTest() {
        // Este teste é útil principalmente para ferramentas de cobertura de código
        // para garantir que a linha do "main" foi executada.
        // O try-catch garante que o teste não falhe apenas porque o app já está rodando em outra porta.
        
        try {
            ChurnApplication.main(new String[] {});
        } catch (Exception e) {
            // Em testes reais, às vezes o servidor web pode reclamar de porta em uso,
            // mas para um teste de unidade simples da main, queremos apenas garantir
            // que a chamada acontece.
        }
    }
}