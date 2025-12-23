package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.StatsDTO;
import nocountry.churninsight.churn.repository.ChurnRepository;
import nocountry.churninsight.churn.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ChurnRepository churnRepository;

    /**
     * Retrieves basic statistics about clients and churn predictions.
     * @return StatsDTO containing statistics
     */
    public StatsDTO getBasicStats() {
        Long totalClients = clientRepository.count();
        Long totalPredictions = churnRepository.count();
        
        Long churnedClients = churnRepository.countByPrevisaoChurnTrue();
        Long retainedClients = totalClients - churnedClients;
        
        Double churnRate = totalClients > 0 ? (churnedClients.doubleValue() / totalClients) * 100 : 0.0;

        return StatsDTO.builder()
                .totalClients(totalClients)
                .totalPredictions(totalPredictions)
                .churnRate(churnRate)
                .retainedClients(retainedClients)
                .churnedClients(churnedClients)
                .build();
    }
}
