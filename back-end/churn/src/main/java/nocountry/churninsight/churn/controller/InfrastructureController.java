package nocountry.churninsight.churn.controller;

import nocountry.churninsight.churn.dto.DemoExampleDTO;
import nocountry.churninsight.churn.service.DemoDataService;
import nocountry.churninsight.churn.service.SystemHealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/churn")
public class InfrastructureController {

    private final SystemHealthService healthService;
    private final DemoDataService demoDataService;

    public InfrastructureController(SystemHealthService healthService, DemoDataService demoDataService) {
        this.healthService = healthService;
        this.demoDataService = demoDataService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        return ResponseEntity.ok(healthService.getHealth());
    }

    @GetMapping("/ds-health")
    public ResponseEntity<Map<String, Object>> getDSHealth() {
        Map<String,Object> healthInfo = healthService.getDsHealth();

        Object latency = healthInfo.getOrDefault("internal_latency", 0L);
        healthInfo.remove("internal_latency");

        return ResponseEntity.ok()
                .header("X-Proxy-Latency-Ms", String.valueOf(latency))
                .body(healthInfo);
    }

    @GetMapping("/demo-examples")
    public ResponseEntity<List<DemoExampleDTO>> getDemoExamples() {
        return ResponseEntity.ok(demoDataService.getDemoExamples());
    }

}
