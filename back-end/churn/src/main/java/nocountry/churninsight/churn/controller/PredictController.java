package nocountry.churninsight.churn.controller;

import jakarta.validation.Valid;
import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.service.PredictionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PredictController {

    private final PredictionService predictionService;

    public PredictController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictDTO> predict (@Valid @RequestBody ChurnDataDTO previsao){
        PredictDTO resultado = predictionService.predict(previsao);
        return ResponseEntity.ok(resultado);
    }

}