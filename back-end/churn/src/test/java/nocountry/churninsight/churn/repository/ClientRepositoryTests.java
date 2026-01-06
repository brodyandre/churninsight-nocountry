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
class ClientRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        // Limpa os dados antes de cada teste para garantir isolamento no Postgres real
        // A ordem de exclusão evita erros de chave estrangeira
        entityManager.getEntityManager().createQuery("DELETE FROM Previsao").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Cliente").executeUpdate();
        entityManager.flush();
    }

    @Test
    @DisplayName("Deve retornar o total exato de 2 clientes inseridos")
    void count_DeveRetornarTotalCorretoDeClientes() {
        // --- Arrange ---
        entityManager.persist(criarClienteExemplo());
        entityManager.persist(criarClienteExemplo());
        entityManager.flush(); 

        // --- Act ---
        long total = clientRepository.count();

        // --- Assert ---
        assertThat(total).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve persistir cliente e validar campos gerados automaticamente")
    void save_DevePersistirClienteEGerarDataCriacao() {
        // --- Arrange ---
        Cliente cliente = criarClienteExemplo();

        // --- Act ---
        // Usamos save do repository para testar o comportamento real do componente
        Cliente salvo = clientRepository.save(cliente);
        entityManager.flush(); // Garante que o PrePersist e o DB gerem os dados

        // --- Assert ---
        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getDataCriacao()).isNotNull(); 
        assertThat(salvo.getValorTotal()).isEqualTo(1500.00);
    }

    private Cliente criarClienteExemplo() {
        Cliente cliente = new Cliente();
        
        cliente.setGenero(GeneroEnum.FEMININO); 
        cliente.setIdoso(0);
        cliente.setConjuge("Sim");
        cliente.setDependentes("Não");
        
        cliente.setTipoContrato(TipoContratoEnum.MENSAL); 
        cliente.setTempoContrato(12);
        
        cliente.setServicoTelefone("Sim");
        cliente.setMultiplasLinhasTel("Sem serviço telefônico");
        
        cliente.setServicoInternet(ServicoInternetEnum.FIBRA_OTICA);
        cliente.setSegurancaOnline("Não");
        cliente.setBackupOnline("Sim");
        cliente.setProtecaoDispositivo("Não");
        cliente.setSuporteTecnico("Sim");
        cliente.setTvStreaming("Sim");
        cliente.setFilmesStreaming("Sim");
        
        cliente.setFaturaOnline("Sim");
        cliente.setMetodoPagamento(MetodoPagamentoEnum.CARTAO_CREDITO);
        
        cliente.setValorMensal(100.50);
        cliente.setValorTotal(1500.00);
        
        return cliente;
    }
}