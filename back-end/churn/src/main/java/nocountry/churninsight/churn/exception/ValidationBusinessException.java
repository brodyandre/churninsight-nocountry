package nocountry.churninsight.churn.exception;

import java.util.List;

/**
 * Exceção lançada quando há erros de validação de negócio.
 * Utilizada para validações customizadas que vão além das anotações de bean validation.
 */
public class ValidationBusinessException extends RuntimeException {
    
    private final List<String> errors;

    public ValidationBusinessException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
