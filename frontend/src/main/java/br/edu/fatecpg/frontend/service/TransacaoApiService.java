package br.edu.fatecpg.frontend.service;

import br.edu.fatecpg.frontend.dto.DescricaoForm;
import br.edu.fatecpg.frontend.dto.TransacaoDTO;
import br.edu.fatecpg.frontend.dto.TransacaoForm;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Fala com os endpoints de /contas/{contaId}/transacoes da API. */
@Service
public class TransacaoApiService {

    private final RestClient api;
    private final ApiExecutor executor;

    public TransacaoApiService(RestClient apiClient, ApiExecutor executor) {
        this.api = apiClient;
        this.executor = executor;
    }

    /** Filtros opcionais: tipo, descricao e período (inicio + fim juntos). Vazio ou null = sem filtro. */
    public List<TransacaoDTO> listar(Long contaId, String tipo, String descricao, LocalDate inicio, LocalDate fim) {
        List<TransacaoDTO> lista = executor.executar(() -> api.get()
                .uri(uri -> {
                    Map<String, Object> variaveis = new HashMap<>();
                    variaveis.put("contaId", contaId);
                    UriBuilder b = uri.path("/contas/{contaId}/transacoes");
                    if (tem(tipo)) {
                        b.queryParam("tipo", "{tipo}");
                        variaveis.put("tipo", tipo);
                    }
                    if (tem(descricao)) {
                        b.queryParam("descricao", "{descricao}");
                        variaveis.put("descricao", descricao.trim());
                    }
                    if (inicio != null) {
                        b.queryParam("inicio", "{inicio}");
                        variaveis.put("inicio", inicio.toString());
                    }
                    if (fim != null) {
                        b.queryParam("fim", "{fim}");
                        variaveis.put("fim", fim.toString());
                    }
                    return b.build(variaveis);
                })
                .retrieve()
                .body(new ParameterizedTypeReference<List<TransacaoDTO>>() { }));
        return lista != null ? lista : List.of();
    }

    public TransacaoDTO buscar(Long contaId, Long id) {
        return executor.executar(() -> api.get()
                .uri("/contas/{contaId}/transacoes/{id}", contaId, id)
                .retrieve()
                .body(TransacaoDTO.class));
    }

    public TransacaoDTO criar(Long contaId, TransacaoForm form) {
        return executor.executar(() -> api.post()
                .uri("/contas/{contaId}/transacoes", contaId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(TransacaoDTO.class));
    }

    public TransacaoDTO atualizarDescricao(Long contaId, Long id, DescricaoForm form) {
        return executor.executar(() -> api.patch()
                .uri("/contas/{contaId}/transacoes/{id}/descricao", contaId, id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(TransacaoDTO.class));
    }

    public void excluir(Long contaId, Long id) {
        executor.executarSemRetorno(() -> api.delete()
                .uri("/contas/{contaId}/transacoes/{id}", contaId, id)
                .retrieve()
                .toBodilessEntity());
    }

    private boolean tem(String texto) {
        return texto != null && !texto.isBlank();
    }
}
