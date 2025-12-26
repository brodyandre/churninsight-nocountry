package nocountry.churninsight.churn.repository;

import nocountry.churninsight.churn.model.Previsao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChurnRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChurnRepository churnRepository;

    @Test
    void countByPrevisaoChurnTrue_DeveContarApenasComStatusVaiCancelar() {
        // --- Arrange ---
        
        // 1. Previsão que CONTA (Vai cancelar)
        Previsao p1 = criarPrevisao(1L, "Vai cancelar", 0.95);
        entityManager.persist(p1);

        // 2. Previsão que NÃO CONTA (Qualquer outra string)
        Previsao p2 = criarPrevisao(2L, "Cliente Fiel", 0.10);
        entityManager.persist(p2);

        // 3. Outra previsão que CONTA
        Previsao p3 = criarPrevisao(3L, "Vai cancelar", 0.80);
        entityManager.persist(p3);

        entityManager.flush(); 

        // --- Act ---
        Long count = churnRepository.countByPrevisaoChurnTrue();

        // --- Assert ---
        assertThat(count).isEqualTo(2L);
    }
    
    @Test
    void countByPrevisaoChurnTrue_DeveRetornarZero_QuandoNaoHouverChurn() {
        // --- Arrange ---
        Previsao p = criarPrevisao(1L, "Mantém serviço", 0.05);
        entityManager.persist(p);
        entityManager.flush();

        // --- Act ---
        Long count = churnRepository.countByPrevisaoChurnTrue();

        // --- Assert ---
        assertThat(count).isEqualTo(0L);
    }

    // Método auxiliar para preencher os campos obrigatórios (nullable = false)
    private Previsao criarPrevisao(Long clienteId, String textoPrevisao, Double probabilidade) {
        Previsao p = new Previsao();
        p.setClienteId(clienteId);       // Obrigatório
        p.setPrevisao(textoPrevisao);    // Obrigatório e alvo do teste
        p.setProbabilidade(probabilidade); // Obrigatório
        // dataCriacao é preenchida automaticamente pelo @PrePersist
        return p;
    }
}