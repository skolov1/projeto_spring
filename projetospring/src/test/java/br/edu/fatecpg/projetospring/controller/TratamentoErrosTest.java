package br.edu.fatecpg.projetospring.controller;

import br.edu.fatecpg.projetospring.service.ContaService;
import br.edu.fatecpg.projetospring.service.TransacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ContaController.class, TransacaoController.class})
@ActiveProfiles("test")
class TratamentoErrosTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContaService contaService;

    @MockitoBean
    private TransacaoService transacaoService;

    @Test
    @DisplayName("Data em formato inválido deve retornar 400 em vez de ignorar o filtro")
    void dataInvalidaDeveRetornar400() throws Exception {
        mockMvc.perform(get("/contas/1/transacoes").param("inicio", "2026-13-45").param("fim", "2026-01-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETRO_INVALIDO"))
                .andExpect(jsonPath("$.message", containsString("inicio")));

        verifyNoInteractions(transacaoService);
    }

    @Test
    @DisplayName("Tipo de transação inexistente deve retornar 400")
    void tipoInvalidoDeveRetornar400() throws Exception {
        mockMvc.perform(get("/contas/1/transacoes").param("tipo", "INVESTIMENTO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETRO_INVALIDO"))
                .andExpect(jsonPath("$.message", containsString("tipo")));
    }

    @Test
    @DisplayName("Erro de integridade não deve expor a mensagem do SQL")
    void erroDeIntegridadeNaoDeveExporSql() throws Exception {
        when(contaService.criar(any())).thenThrow(new DataIntegrityViolationException(
                "could not execute statement [ERROR: valor do campo viola restrição "
                        + "\"conta_saldo_inicial_check\"] [insert into conta (nome,saldo_inicial) values (?,?)]"));

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Conta\", \"saldoInicial\": \"10.00\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DATA_INTEGRITY_VIOLATION"))
                .andExpect(jsonPath("$.message", not(containsString("insert"))))
                .andExpect(jsonPath("$.message", not(containsString("conta_saldo_inicial_check"))));
    }

    @Test
    @DisplayName("Violação de unicidade deve retornar 409 DUPLICATE_ENTRY sem expor o SQL")
    void violacaoDeUnicidadeDeveRetornarDuplicateEntry() throws Exception {
        when(contaService.criar(any())).thenThrow(new DataIntegrityViolationException(
                "could not execute statement [ERROR: duplicate key value violates unique constraint "
                        + "\"conta_nome_key\"] [insert into conta (nome,saldo_inicial) values (?,?)]"));

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Conta\", \"saldoInicial\": \"10.00\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_ENTRY"))
                .andExpect(jsonPath("$.message", not(containsString("insert"))));
    }
}
