package nocountry.churninsight.churn.controller;

import jakarta.validation.Valid;
import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para predições de churn de clientes.
 * Responsável por receber dados do cliente e retornar predição de churn.
 */
@RestController
@RequestMapping("/api")
public class ChurnPredictionController {

    @Autowired
    private PredictionService predictionService;

    /**
     * Endpoint para realizar predição de churn.
     * 
     * @param churnData dados do cliente com informações para predição
     * @return ResponseEntity com resultado da predição
     */
    @PostMapping("/predict")
    public ResponseEntity<PredictDTO> predict(@Valid @RequestBody ChurnDataDTO churnData) {
        PredictDTO result = predictionService.predict(churnData);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
