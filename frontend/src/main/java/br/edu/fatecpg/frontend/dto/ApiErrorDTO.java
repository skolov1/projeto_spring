package br.edu.fatecpg.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * Corpo de erro (JSON) devolvido pelo GlobalExceptionHandler do backend.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiErrorDTO {

    private int status;

    private String error;

    private String message;

    private String timestamp;

    private Map<String, String> details;

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Map<String, String> getDetails() { return details; }
    public void setDetails(Map<String, String> details) { this.details = details; }

}
