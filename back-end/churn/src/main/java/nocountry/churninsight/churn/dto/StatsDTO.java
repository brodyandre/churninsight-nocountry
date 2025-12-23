package nocountry.churninsight.churn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDTO {
    private Long totalClients;
    private Long totalPredictions;
    private Double churnRate;
    private Long retainedClients;
    private Long churnedClients;
}
