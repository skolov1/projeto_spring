package br.edu.fatecpg.frontend.exception;

import java.util.Map;

/**
 * Erro devolvido pela API (400, 404, 409...) ou falha ao falar com ela.
 * Os controllers capturam esta exceção e mostram a mensagem na tela.
 */
public class ApiException extends RuntimeException {

    private final int status;
    private final Map<String, String> detalhes;

    public ApiException(int status, String mensagem, Map<String, String> detalhes) {
        super(mensagem);
        this.status = status;
        this.detalhes = detalhes;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getDetalhes() {
        return detalhes;
    }

    /** Mensagem principal + detalhes por campo (quando a API manda, ex.: erros de validação). */
    public String getMensagemCompleta() {
        if (detalhes == null || detalhes.isEmpty()) {
            return getMessage();
        }
        StringBuilder sb = new StringBuilder(getMessage()).append(": ");
        boolean primeiro = true;
        for (Map.Entry<String, String> d : detalhes.entrySet()) {
            if (!primeiro) {
                sb.append("; ");
            }
            sb.append(d.getKey()).append(" - ").append(d.getValue());
            primeiro = false;
        }
        return sb.toString();
    }
}
