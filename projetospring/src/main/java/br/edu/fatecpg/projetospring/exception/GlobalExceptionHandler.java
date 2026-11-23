package br.edu.fatecpg.projetospring.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        HttpStatus status = determineStatus(ex.getErrorCode());
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getErrorCode(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            // Erros de classe (ex.: validação entre campos) não são FieldError
            String chave = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            errors.put(chave, error.getDefaultMessage());
        });

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Dados de entrada inválidos",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "CONSTRAINT_VIOLATION",
                "Violação de restrição",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // O detalhe do banco (SQL, nomes de constraints) fica só no log, nunca na resposta
        log.warn("Violação de integridade de dados", ex);

        String detalhe = ex.getMostSpecificCause().getMessage();
        boolean duplicado = detalhe != null
                && (detalhe.toLowerCase().contains("unique") || detalhe.toLowerCase().contains("duplicate"));

        String errorCode = duplicado ? "DUPLICATE_ENTRY" : "DATA_INTEGRITY_VIOLATION";
        String message = duplicado
                ? "Já existe um registro com estes dados"
                : "Os dados enviados violam uma restrição do banco de dados";

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                errorCode,
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "ENTITY_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "MALFORMED_JSON",
                "JSON malformado ou inválido",
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "PARAMETRO_INVALIDO",
                "Valor inválido para o parâmetro '" + ex.getName() + "': '" + ex.getValue() + "'",
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro não tratado", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Erro interno do servidor",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private HttpStatus determineStatus(String errorCode) {
        return switch (errorCode) {
            case "CONTA_NAO_ENCONTRADA", "TRANSACAO_NAO_ENCONTRADA", "ENTITY_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "DUPLICATE_ENTRY" -> HttpStatus.CONFLICT;
            case "DESCRICAO_VAZIA", "PERIODO_INVALIDO", "PARAMETRO_INVALIDO", "VALIDATION_ERROR", "CONSTRAINT_VIOLATION", "ILLEGAL_ARGUMENT", "MALFORMED_JSON" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    public static class ErrorResponse {
        private final int status;
        private final String error;
        private final String message;
        private final LocalDateTime timestamp;
        private final Map<String, String> details;

        public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
            this.details = null;
        }

        public ErrorResponse(int status, String error, String message, LocalDateTime timestamp, Map<String, String> details) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
            this.details = details;
        }

        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Map<String, String> getDetails() { return details; }
    }
}