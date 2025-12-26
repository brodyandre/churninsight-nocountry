package nocountry.churninsight.churn.repository;

import nocountry.churninsight.churn.model.*; // Importa a entidade e os Enums
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClientRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void count_DeveRetornarTotalCorretoDeClientes() {
        // --- Arrange ---
        // Cria dois clientes válidos usando um método auxiliar para não repetir código
        Cliente c1 = criarClienteExemplo();
        Cliente c2 = criarClienteExemplo();
        
        // Persiste no banco em memória
        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.flush(); 

        // --- Act ---
        long total = clientRepository.count();

        // --- Assert ---
        assertThat(total).isEqualTo(2L);
    }

    @Test
    void save_DevePersistirClienteEGerarDataCriacao() {
        // --- Arrange ---
        Cliente cliente = criarClienteExemplo();

        // --- Act ---
        Cliente salvo = clientRepository.save(cliente);

        // --- Assert ---
        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull(); // ID gerado pelo banco
        assertThat(salvo.getDataCriacao()).isNotNull(); // PrePersist funcionou
        assertThat(salvo.getValorTotal()).isEqualTo(1500.00);
    }

    /**
     * Método auxiliar para criar um Cliente com todos os campos obrigatórios preenchidos.
     * Ajuste os valores dos ENUMS conforme a definição real do seu projeto.
     */
    private Cliente criarClienteExemplo() {
        Cliente cliente = new Cliente();
        
        // Campos Obrigatórios (nullable = false)
        // NOTA: Ajuste os valores dos Enums abaixo (ex: MASCULINO, MENSAL) para os que existem no seu projeto
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