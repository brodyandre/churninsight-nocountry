package nocountry.churninsight.churn.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Status 400 - Erro de Validação
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest req) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Erro sem mensagem definida.",
                        (existingValue, newValue) -> existingValue));

        return buildResponse(HttpStatus.valueOf(status.value()),
                "Erro de validação dos campos.",
                errors);
    }

    // Status 400 - JSON Mal Formado
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest req) {

        String message = (ex.getCause() instanceof InvalidFormatException)
                ? "Erro de formato: valor incompatível com o campo."
                : "JSON malformado ou erro de sintaxe.";

        return buildResponse(HttpStatus.valueOf(status.value()),
                message,
                "Verifique o corpo da requisição.");
    }

    // Status 404 - Recurso Não Encontrado
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND,
                "O recurso solicitado não foi encontrado.",
                ex.getMessage());
    }

    // Status 405 - Método HTTP Errado
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest req) {

        return buildResponse(HttpStatus.valueOf(status.value()),
                "Método HTTP não suportado para este endpoint.",
                "O método correto é " + ex.getSupportedHttpMethods());
    }

    // Status 422 - Erro de Negócio
    @ExceptionHandler(InvalidChurnDataException.class)
    public ResponseEntity<Object> handleInvalidChurnDta(InvalidChurnDataException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT,
                "Dados inconsistentes para análise de churn.",
                ex.getMessage());
    }

    // Status 500 - Erro Genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.",
                "Entre em contato com o suporte.");
    }

    // Status 502 - Erro de Integração
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<Object> handleIntegration(IntegrationException ex) {
        return buildResponse(HttpStatus.BAD_GATEWAY,
                "Serviço de predição indisponível.",
                ex.getMessage());
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message, Object details) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                details
        );

        return new ResponseEntity<>(error, status);
    }

}
