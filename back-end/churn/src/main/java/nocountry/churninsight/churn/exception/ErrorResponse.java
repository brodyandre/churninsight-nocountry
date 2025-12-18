package nocountry.churninsight.churn.exception;

import java.time.LocalDateTime;

public record ErrorResponse (
    LocalDateTime timestamp,
    int status,
    String message,
    Object details
) {}
