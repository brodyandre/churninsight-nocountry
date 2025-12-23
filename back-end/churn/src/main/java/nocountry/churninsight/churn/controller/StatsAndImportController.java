package nocountry.churninsight.churn.controller;

import nocountry.churninsight.churn.dto.CsvImportResponseDTO;
import nocountry.churninsight.churn.dto.StatsDTO;
import nocountry.churninsight.churn.service.CsvImportService;
import nocountry.churninsight.churn.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for statistics and CSV import operations.
 */
@RestController
@RequestMapping("/api")
public class StatsAndImportController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private CsvImportService csvImportService;

    /**
     * GET endpoint to retrieve basic statistics about clients and churn predictions.
     * 
     * @return ResponseEntity containing StatsDTO with statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        StatsDTO stats = statsService.getBasicStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * POST endpoint to import clients from a CSV file.
     * 
     * @param file CSV file containing client data
     * @return ResponseEntity containing CsvImportResponseDTO with import results
     */
    @PostMapping("/import/clients")
    public ResponseEntity<CsvImportResponseDTO> importClientsFromCsv(
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(CsvImportResponseDTO.builder()
                            .successCount(0)
                            .failureCount(0)
                            .message("File is empty")
                            .build());
        }

        CsvImportResponseDTO response = csvImportService.importClientsFromCsv(file);
        
        if (response.getFailureCount() == -1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
