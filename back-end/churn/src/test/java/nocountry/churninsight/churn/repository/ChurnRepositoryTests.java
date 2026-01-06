package nocountry.churninsight.churn.repository;

import nocountry.churninsight.churn.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("tests")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChurnRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChurnRepository churnRepository;

    @BeforeEach
    void cleanDatabase() {
        // Limpa as tabelas na ordem correta (filha primeiro) para evitar erros de FK
        entityManager.getEntityManager().createQuery("DELETE FROM Previsao").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Cliente").executeUpdate();
        entityManager.flush();
    }

    @Test
    @DisplayName("Deve contar apenas previsões com status 'Vai cancelar'")
    void countByPrevisaoChurnTrue_DeveContarApenasComStatusVaiCancelar() {
        // --- Arrange ---
        // 1. Persistir o Cliente primeiro para gerar o ID real no Postgres
        Cliente cliente = criarClienteValido();
        cliente = entityManager.persistAndFlush(cliente);

        // 2. Criar a Previsão vinculada ao cliente persistido
        Previsao previsao = new Previsao();
        previsao.setClienteId(cliente.getId());
        previsao.setPrevisao("Vai cancelar");
        previsao.setProbabilidade(0.85);
        
        entityManager.persistAndFlush(previsao);
        
        // --- Act ---
        Long count = churnRepository.countByPrevisaoChurnTrue();

        // --- Assert ---
        assertThat(count).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Deve retornar zero quando não houver previsões de churn")
    void countByPrevisaoChurnTrue_DeveRetornarZero_QuandoNaoHouverChurn() {
        // --- Arrange ---
        // 1. Salva o cliente primeiro para não dar erro de FK
        Cliente cliente = criarClienteValido();
        cliente = entityManager.persistAndFlush(cliente);

        // 2. Salva a previsão com status que NÃO deve ser contado
        Previsao previsao = new Previsao();
        previsao.setClienteId(cliente.getId());
        previsao.setPrevisao("Vai continuar"); 
        previsao.setProbabilidade(0.15);
        entityManager.persistAndFlush(previsao);

        // --- Act ---
        Long count = churnRepository.countByPrevisaoChurnTrue();

        // --- Assert ---
        assertThat(count).isZero();
    }

    // --- Métodos Auxiliares ---

    private Cliente criarClienteValido() {
        Cliente cliente = new Cliente();
        cliente.setGenero(GeneroEnum.FEMININO);
        cliente.setIdoso(0);
        cliente.setConjuge("Sim");
        cliente.setDependentes("Não");
        cliente.setTipoContrato(TipoContratoEnum.MENSAL);
        cliente.setTempoContrato(12);
        cliente.setServicoTelefone("Sim");
        cliente.setMultiplasLinhasTel("Não");
        cliente.setServicoInternet(ServicoInternetEnum.FIBRA_OTICA);
        cliente.setSegurancaOnline("Sim");
        cliente.setBackupOnline("Sim");
        cliente.setProtecaoDispositivo("Sim");
        cliente.setSuporteTecnico("Sim");
        cliente.setTvStreaming("Sim");
        cliente.setFilmesStreaming("Sim");
        cliente.setFaturaOnline("Sim");
        cliente.setMetodoPagamento(MetodoPagamentoEnum.PIX);
        cliente.setValorMensal(100.0);
        cliente.setValorTotal(1200.0);
        return cliente;
    }

    private Previsao criarPrevisao(Long clienteId, String textoPrevisao, Double probabilidade) {
        Previsao p = new Previsao();
        p.setClienteId(clienteId);
        p.setPrevisao(textoPrevisao);
        p.setProbabilidade(probabilidade);
        return p;
    }
}