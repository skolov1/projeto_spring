package br.edu.fatecpg.projetospring.service;

import br.edu.fatecpg.projetospring.dto.request.AtualizarDescricaoRequestDTO;
import br.edu.fatecpg.projetospring.dto.request.TransacaoRequestDTO;
import br.edu.fatecpg.projetospring.dto.response.TransacaoResponseDTO;
import br.edu.fatecpg.projetospring.exception.ContaNaoEncontradaException;
import br.edu.fatecpg.projetospring.exception.DescricaoVaziaException;
import br.edu.fatecpg.projetospring.exception.TransacaoNaoEncontradaException;
import br.edu.fatecpg.projetospring.model.entity.Conta;
import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.repository.ContaRepository;
import br.edu.fatecpg.projetospring.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Conta conta;
    private Transacao transacao;

    @BeforeEach
    void setUp() {
        conta = new Conta("Conta Teste", new BigDecimal("1000.00"));
        setId(conta, 1L);

        transacao = new Transacao("Teste", new BigDecimal("100.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 15), "Categoria");
        setId(transacao, 1L);
        conta.adicionarTransacao(transacao);
    }

    private void setId(Object entity, Long id) {
        try {
            java.lang.reflect.Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Deve listar transações sem filtros")
    void deveListarTransacoesSemFiltros() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.findByContaIdOrderByDataDesc(1L)).thenReturn(List.of(transacao));

        List<TransacaoResponseDTO> result = transacaoService.listarPorConta(1L, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Teste", result.get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve listar transações filtrando por tipo")
    void deveListarTransacoesFiltrandoPorTipo() {
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.findByContaIdAndTipo(1L, TipoTransacao.RECEITA)).thenReturn(List.of(transacao));

        List<TransacaoResponseDTO> result = transacaoService.listarPorConta(1L, TipoTransacao.RECEITA, null, null, null);

        assertEquals(1, result.size());
        verify(transacaoRepository).findByContaIdAndTipo(1L, TipoTransacao.RECEITA);
    }

    @Test
    @DisplayName("Deve buscar transação por ID")
    void deveBuscarTransacaoPorId() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        TransacaoResponseDTO result = transacaoService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals("Teste", result.getDescricao());
        assertEquals(new BigDecimal("100.00"), result.getValor());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar transação inexistente")
    void deveLancarExcecaoAoBuscarTransacaoInexistente() {
        when(transacaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TransacaoNaoEncontradaException.class, () -> transacaoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve criar nova transação")
    void deveCriarNovaTransacao() {
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                "Nova Transação",
                new BigDecimal("200.00"),
                TipoTransacao.DESPESA,
                LocalDate.of(2026, 1, 20),
                "Lazer"
        );

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            setId(t, 2L);
            return t;
        });

        TransacaoResponseDTO result = transacaoService.criar(1L, request);

        assertNotNull(result);
        assertEquals("Nova Transação", result.getDescricao());
        assertEquals(new BigDecimal("200.00"), result.getValor());
        assertEquals(TipoTransacao.DESPESA, result.getTipo());
        verify(contaRepository).findById(1L);
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar transação para conta inexistente")
    void deveLancarExcecaoAoCriarTransacaoParaContaInexistente() {
        TransacaoRequestDTO request = new TransacaoRequestDTO(
                "Teste", new BigDecimal("100"), TipoTransacao.RECEITA, LocalDate.now(), "Cat"
        );

        when(contaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class, () -> transacaoService.criar(999L, request));
    }

    @Test
    @DisplayName("Deve atualizar descrição da transação")
    void deveAtualizarDescricaoTransacao() {
        AtualizarDescricaoRequestDTO request = new AtualizarDescricaoRequestDTO("Nova Descrição");

        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);

        TransacaoResponseDTO result = transacaoService.atualizarDescricao(1L, request);

        assertEquals("Nova Descrição", result.getDescricao());
        verify(transacaoRepository).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar descrição para vazia")
    void deveLancarExcecaoAoAtualizarDescricaoVazia() {
        AtualizarDescricaoRequestDTO request = new AtualizarDescricaoRequestDTO("");

        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        assertThrows(DescricaoVaziaException.class, () -> transacaoService.atualizarDescricao(1L, request));
    }

    @Test
    @DisplayName("Deve remover transação")
    void deveRemoverTransacao() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(transacao));

        transacaoService.remover(1L);

        verify(transacaoRepository).delete(transacao);
        assertNull(transacao.getConta());
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover transação inexistente")
    void deveLancarExcecaoAoRemoverTransacaoInexistente() {
        when(transacaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TransacaoNaoEncontradaException.class, () -> transacaoService.remover(999L));
    }
}