package br.edu.fatecpg.frontend.service;

import br.edu.fatecpg.frontend.dto.ContaDTO;
import br.edu.fatecpg.frontend.dto.ContaForm;
import br.edu.fatecpg.frontend.dto.DespesaCategoriaDTO;
import br.edu.fatecpg.frontend.dto.ResumoDTO;
import br.edu.fatecpg.frontend.dto.SaldoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/** Fala com os endpoints de /contas da API (o frontend nunca acessa o banco). */
@Service
public class ContaApiService {

    private final RestClient api;
    private final ApiExecutor executor;

    public ContaApiService(RestClient apiClient, ApiExecutor executor) {
        this.api = apiClient;
        this.executor = executor;
    }

    public List<ContaDTO> listar() {
        List<ContaDTO> contas = executor.executar(() -> api.get()
                .uri("/contas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ContaDTO>>() { }));
        return contas != null ? contas : List.of();
    }

    public ContaDTO buscar(Long id) {
        return executor.executar(() -> api.get()
                .uri("/contas/{id}", id)
                .retrieve()
                .body(ContaDTO.class));
    }

    public ContaDTO criar(ContaForm form) {
        return executor.executar(() -> api.post()
                .uri("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(ContaDTO.class));
    }

    /** Exige PUT /contas/{id} no backend (veja backend-patch/ no .zip). */
    public ContaDTO atualizar(Long id, ContaForm form) {
        return executor.executar(() -> api.put()
                .uri("/contas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(ContaDTO.class));
    }

    /** Exige DELETE /contas/{id} no backend (veja backend-patch/ no .zip). */
    public void excluir(Long id) {
        executor.executarSemRetorno(() -> api.delete()
                .uri("/contas/{id}", id)
                .retrieve()
                .toBodilessEntity());
    }

    public SaldoDTO saldo(Long id) {
        return executor.executar(() -> api.get()
                .uri("/contas/{id}/saldo", id)
                .retrieve()
                .body(SaldoDTO.class));
    }

    public ResumoDTO resumo(Long id) {
        return executor.executar(() -> api.get()
                .uri("/contas/{id}/resumo", id)
                .retrieve()
                .body(ResumoDTO.class));
    }

    public List<DespesaCategoriaDTO> despesasPorCategoria(Long id) {
        List<DespesaCategoriaDTO> lista = executor.executar(() -> api.get()
                .uri("/contas/{id}/despesas-por-categoria", id)
                .retrieve()
                .body(new ParameterizedTypeReference<List<DespesaCategoriaDTO>>() { }));
        return lista != null ? lista : List.of();
    }
}
