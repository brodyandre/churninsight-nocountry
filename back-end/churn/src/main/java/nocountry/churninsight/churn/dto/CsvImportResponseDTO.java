package nocountry.churninsight.churn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvImportResponseDTO {
    private Integer successCount;
    private Integer failureCount;
    private String message;
}
