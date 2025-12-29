package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.StatsDTO;
import nocountry.churninsight.churn.repository.ChurnRepository;
import nocountry.churninsight.churn.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTests {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ChurnRepository churnRepository;

    // O @InjectMocks vai instanciar o StatsService e injetar os mocks 
    // acima nos campos privados anotados com @Autowired
    @InjectMocks
    private StatsService statsService;

    @Test
    void getBasicStats_ShouldCalculateMetricsCorrectly_WhenClientsExist() {
        // --- Arrange (Preparação) ---
        // Cenário: 1000 clientes totais, 100 previsões feitas, 50 indicados como Churn
        when(clientRepository.count()).thenReturn(1000L);
        when(churnRepository.count()).thenReturn(100L);
        when(churnRepository.countByPrevisaoChurnTrue()).thenReturn(50L);

        // --- Act (Execução) ---
        StatsDTO resultado = statsService.getBasicStats();

        // --- Assert (Verificação) ---
        
        // Verificando contagens simples
        assertEquals(1000L, resultado.getTotalClients());
        assertEquals(100L, resultado.getTotalPredictions());
        assertEquals(50L, resultado.getChurnedClients());

        // Verificando lógica de Retidos (1000 - 50 = 950)
        assertEquals(950L, resultado.getRetainedClients());

        // Verificando lógica de Taxa de Churn ((50 / 1000) * 100 = 5.0%)
        assertEquals(5.0, resultado.getChurnRate(), 0.001);
    }

    @Test
    void getBasicStats_ShouldHandleDivisionByZero_WhenNoClientsExist() {
        // --- Arrange (Preparação) ---
        when(clientRepository.count()).thenReturn(0L);
        when(churnRepository.count()).thenReturn(0L);
        when(churnRepository.countByPrevisaoChurnTrue()).thenReturn(0L);

        // --- Act (Execução) ---
        StatsDTO resultado = statsService.getBasicStats();

        // --- Assert (Verificação) ---
        // Garante que não lança exceção e retorna 0.0
        assertEquals(0.0, resultado.getChurnRate());
        assertEquals(0L, resultado.getRetainedClients());
    }
}