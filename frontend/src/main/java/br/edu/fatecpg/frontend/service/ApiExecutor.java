package br.edu.fatecpg.frontend.service;

import br.edu.fatecpg.frontend.dto.ApiErrorDTO;
import br.edu.fatecpg.frontend.exception.ApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

/**
 * Executa uma chamada à API e traduz qualquer falha em ApiException,
 * com a mensagem que o backend mandou (ou uma mensagem amigável).
 */
@Component
public class ApiExecutor {

    public <T> T executar(Supplier<T> chamada) {
        try {
            return chamada.get();
        } catch (RestClientResponseException e) {
            throw traduzir(e);
        } catch (ResourceAccessException e) {
            throw new ApiException(503,
                    "Não consegui falar com a API. Confira se o backend está rodando em http://localhost:8080.",
                    null);
        }
    }

    public void executarSemRetorno(Runnable chamada) {
        executar(() -> {
            chamada.run();
            return null;
        });
    }

    private ApiException traduzir(RestClientResponseException e) {
        int status = e.getStatusCode().value();
        ApiErrorDTO erro = null;
        try {
            erro = e.getResponseBodyAs(ApiErrorDTO.class);
        } catch (Exception ignorado) {
            // corpo vazio ou fora do formato esperado: usa a mensagem padrão abaixo
        }
        String mensagem = (erro != null && erro.getMessage() != null && !erro.getMessage().isBlank())
                ? erro.getMessage()
                : mensagemPadrao(status);
        return new ApiException(status, mensagem, erro != null ? erro.getDetails() : null);
    }

    private String mensagemPadrao(int status) {
        return switch (status) {
            case 400 -> "A API recusou os dados enviados.";
            case 404 -> "Registro não encontrado na API.";
            case 405 -> "A API ainda não tem esse recurso.";
            case 409 -> "Conflito: já existe um registro com esses dados.";
            default -> "A API respondeu com erro (HTTP " + status + ").";
        };
    }
}
