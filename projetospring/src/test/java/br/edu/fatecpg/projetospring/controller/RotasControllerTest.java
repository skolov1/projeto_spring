package br.edu.fatecpg.projetospring.controller;

import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.service.ContaService;
import br.edu.fatecpg.projetospring.service.TransacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ContaController.class, TransacaoController.class, TransacaoGeralController.class})
@ActiveProfiles("test")
class RotasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContaService contaService;

    @MockitoBean
    private TransacaoService transacaoService;

    @Test
    @DisplayName("GET /contas/{id}/transacoes deve ser atendido apenas pela listagem de transações")
    void listagemDeTransacoesNaoDeveTerRotaAmbigua() throws Exception {
        when(transacaoService.listarPorConta(eq(1L), any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/contas/1/transacoes"))
                .andExpect(status().isOk());

        verify(transacaoService).listarPorConta(eq(1L), isNull(), isNull(), isNull(), isNull());
        verifyNoInteractions(contaService);
    }

    @Test
    @DisplayName("GET /contas/{id}/detalhes deve retornar a conta com suas transações")
    void detalhesDaContaDeveUsarContaService() throws Exception {
        mockMvc.perform(get("/contas/1/detalhes"))
                .andExpect(status().isOk());

        verify(contaService).buscarPorIdComTransacoes(1L);
    }

    @Test
    @DisplayName("GET /transacoes deve listar transações de todas as contas com filtros")
    void listagemGeralDeTransacoes() throws Exception {
        mockMvc.perform(get("/transacoes").param("tipo", "DESPESA").param("descricao", "aluguel"))
                .andExpect(status().isOk());

        verify(transacaoService).listarTodas(eq(TipoTransacao.DESPESA), eq("aluguel"), isNull(), isNull());
    }

    @Test
    @DisplayName("GET /contas/{id}/despesas-por-categoria deve usar ContaService")
    void despesasPorCategoria() throws Exception {
        mockMvc.perform(get("/contas/1/despesas-por-categoria"))
                .andExpect(status().isOk());

        verify(contaService).despesasPorCategoria(1L);
    }
}
