package nocountry.churninsight.churn.controller;

import jakarta.validation.Valid;
import nocountry.churninsight.churn.dto.PrevisaoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PredictController {

    @PostMapping("/predict")
    public ResponseEntity<PrevisaoDTO> predict (@Valid @RequestBody PrevisaoDTO previsao ){
        predict(previsao);
        return ResponseEntity.ok().build();
    }

}
