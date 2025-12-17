package nocountry.churninsight.churn.service;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {
    public PredictDTO predict(ChurnDataDTO data) {
        // Valor de retorno provisório enquanto o modelo de IA não é integrado.
        // A lógica abaixo é apenas um exemplo e será substituída.
        if (data.getValorMensal() > 100) {
            return new PredictDTO("Vai cancelar", 0.8);
        } else {
            return new PredictDTO("Vai continuar", 0.47);
        }
    }
}
