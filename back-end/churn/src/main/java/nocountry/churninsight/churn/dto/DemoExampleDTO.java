package nocountry.churninsight.churn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemoExampleDTO {
    public String id;
    public String label;
    public String risk;
    public String description;
    public ChurnDataDTO payload;
}
