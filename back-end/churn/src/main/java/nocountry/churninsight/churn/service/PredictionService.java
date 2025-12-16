package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {
    //aguardando o modelo para integração
    public PredictDTO predict(ChurnDataDTO data) {
        return new PredictDTO("Vai continuar", 0.47);
    }
}
