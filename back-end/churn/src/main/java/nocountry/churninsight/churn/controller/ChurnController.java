package nocountry.churninsight.churn.controller;

import jakarta.validation.Valid;
import nocountry.churninsight.churn.dto.ChurnDataDTO;
import nocountry.churninsight.churn.dto.PredictDTO;
import nocountry.churninsight.churn.dto.StatsDTO;
import nocountry.churninsight.churn.service.PredictionService;
import nocountry.churninsight.churn.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/churn")
public class ChurnController {

    private static final Logger logger = LoggerFactory.getLogger(ChurnController.class);

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private StatsService statsService;

    @PostMapping("/predict")
    public ResponseEntity<PredictDTO> predictChurn(@Valid @RequestBody ChurnDataDTO data) {
        logger.info("Recebida requisição para previsão de churn (individual).");
        
            PredictDTO prediction = predictionService.predict(data);
            return ResponseEntity.ok(prediction);
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        logger.info("Recebida requisição para estatísticas gerais.");
            // Agora usa o StatsService que tem a lógica mais completa
            StatsDTO stats = statsService.getBasicStats();
            return ResponseEntity.ok(stats);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<PredictDTO>> uploadCsv(@RequestParam("file") MultipartFile file) {
        logger.info("Recebida requisição de upload de CSV para processamento em lote.");
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<PredictDTO> predictions = predictionService.processCsv(file);
            return ResponseEntity.ok(predictions);
    }
}
