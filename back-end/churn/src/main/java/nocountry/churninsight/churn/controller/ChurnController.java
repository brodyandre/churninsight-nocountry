package nocountry.churninsight.churn.controller;

import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.service.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/churn")
public class ChurnController {

    private static final Logger logger = LoggerFactory.getLogger(ChurnController.class);

    @Autowired
    private PredictionService predictionService;

    @PostMapping("/predict")
    public ResponseEntity<PredictDTO> predictChurn(@RequestBody ChurnDataDTO data) {
        logger.info("Recebida requisição para previsão de churn.");
        
        try {
            PredictDTO prediction = predictionService.predict(data);
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            logger.error("Erro ao processar a requisição de previsão de churn.", e);
            // Retorna um erro 500 (Internal Server Error) se algo der errado no serviço
            return ResponseEntity.internalServerError().build();
        }
    }
}
