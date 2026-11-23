package br.edu.fatecpg.projetospring.repository;

import br.edu.fatecpg.projetospring.model.entity.Conta;
import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.model.projection.ResumoFinanceiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TransacaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private Conta conta;
    private Transacao receita1;
    private Transacao receita2;
    private Transacao despesa1;
    private Transacao despesa2;

    @BeforeEach
    void setUp() {
        conta = new Conta("Conta Teste", new BigDecimal("1000.00"));
        entityManager.persistAndFlush(conta);

        receita1 = new Transacao("Salário Janeiro", new BigDecimal("5000.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 5), "Salário");
        receita2 = new Transacao("Freelance", new BigDecimal("1200.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 20), "Trabalho Extra");
        despesa1 = new Transacao("Aluguel", new BigDecimal("1500.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 10), "Moradia");
        despesa2 = new Transacao("Supermercado", new BigDecimal("800.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 15), "Alimentação");

        conta.adicionarTransacao(receita1);
        conta.adicionarTransacao(receita2);
        conta.adicionarTransacao(despesa1);
        conta.adicionarTransacao(despesa2);

        entityManager.persistAndFlush(receita1);
        entityManager.persistAndFlush(receita2);
        entityManager.persistAndFlush(despesa1);
        entityManager.persistAndFlush(despesa2);
        entityManager.flush();
    }

    @Test
    @DisplayName("Query nativa: deve somar receitas e despesas separadamente")
    void queryNativaDeveSomarReceitasEDespesas() {
        ResumoFinanceiro resumo = transacaoRepository.somarReceitasEDespesas(conta.getId());

        assertNotNull(resumo);
        assertEquals(new BigDecimal("6200.00"), resumo.getReceitas()); // 5000 + 1200
        assertEquals(new BigDecimal("2300.00"), resumo.getDespesas()); // 1500 + 800
    }

    @Test
    @DisplayName("Query nativa: deve retornar zeros para conta sem transações")
    void queryNativaDeveRetornarZerosParaContaSemTransacoes() {
        Conta contaVazia = new Conta("Conta Vazia", BigDecimal.ZERO);
        entityManager.persistAndFlush(contaVazia);

        ResumoFinanceiro resumo = transacaoRepository.somarReceitasEDespesas(contaVazia.getId());

        assertNotNull(resumo);
        assertEquals(BigDecimal.ZERO, resumo.getReceitas());
        assertEquals(BigDecimal.ZERO, resumo.getDespesas());
    }

    @Test
    @DisplayName("Query derivada: deve buscar por tipo RECEITA")
    void queryDerivadaDeveBuscarPorTipoReceita() {
        List<Transacao> receitas = transacaoRepository.findByTipo(TipoTransacao.RECEITA);

        assertEquals(2, receitas.size());
        assertTrue(receitas.stream().allMatch(t -> t.getTipo() == TipoTransacao.RECEITA));
    }

    @Test
    @DisplayName("Query derivada: deve buscar por tipo DESPESA")
    void queryDerivadaDeveBuscarPorTipoDespesa() {
        List<Transacao> despesas = transacaoRepository.findByTipo(TipoTransacao.DESPESA);

        assertEquals(2, despesas.size());
        assertTrue(despesas.stream().allMatch(t -> t.getTipo() == TipoTransacao.DESPESA));
    }

    @Test
    @DisplayName("Query derivada: deve buscar por descrição contendo (ignore case)")
    void queryDerivadaDeveBuscarPorDescricaoContendoIgnoreCase() {
        // Busca por "janeiro" (sem acento) para compatibilidade com H2
        List<Transacao> resultados = transacaoRepository.findByDescricaoContainingIgnoreCase("janeiro");

        assertEquals(1, resultados.size());
        assertEquals("Salário Janeiro", resultados.get(0).getDescricao());
    }

    @Test
    @DisplayName("Query derivada: deve buscar por período de datas")
    void queryDerivadaDeveBuscarPorPeriodoDatas() {
        List<Transacao> resultados = transacaoRepository.findByDataBetween(
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 15)
        );

        assertEquals(2, resultados.size()); // Aluguel (10) e Supermercado (15)
    }

    @Test
    @DisplayName("JPQL: deve listar todas ordenadas por data decrescente")
    void jpqlDeveListarTodasOrdenadasPorDataDesc() {
        List<Transacao> resultados = transacaoRepository.listarOrdenadasPorData();

        assertEquals(4, resultados.size());
        // Primeira deve ser a mais recente: Freelance (20/01)
        assertEquals("Freelance", resultados.get(0).getDescricao());
        // Última deve ser a mais antiga: Salário Janeiro (05/01)
        assertEquals("Salário Janeiro", resultados.get(3).getDescricao());
    }

    @Test
    @DisplayName("Query customizada: deve buscar por conta ordenadas por data")
    void queryCustomizadaDeveBuscarPorContaOrdenadasPorData() {
        List<Transacao> resultados = transacaoRepository.findByContaIdOrderByDataDesc(conta.getId());

        assertEquals(4, resultados.size());
        assertEquals("Freelance", resultados.get(0).getDescricao());
    }

    @Test
    @DisplayName("Query customizada: deve buscar por conta e tipo")
    void queryCustomizadaDeveBuscarPorContaETipo() {
        List<Transacao> receitas = transacaoRepository.findByContaIdAndTipo(conta.getId(), TipoTransacao.RECEITA);
        List<Transacao> despesas = transacaoRepository.findByContaIdAndTipo(conta.getId(), TipoTransacao.DESPESA);

        assertEquals(2, receitas.size());
        assertEquals(2, despesas.size());
    }

    @Test
    @DisplayName("Query customizada: deve buscar por conta e descrição")
    void queryCustomizadaDeveBuscarPorContaEDescricao() {
        List<Transacao> resultados = transacaoRepository.findByContaIdAndDescricaoContaining(conta.getId(), "aluguel");

        assertEquals(1, resultados.size());
        assertEquals("Aluguel", resultados.get(0).getDescricao());
    }

    @Test
    @DisplayName("Query customizada: deve buscar por conta e período")
    void queryCustomizadaDeveBuscarPorContaEPeriodo() {
        List<Transacao> resultados = transacaoRepository.findByContaIdAndDataBetween(
                conta.getId(),
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 15)
        );

        assertEquals(2, resultados.size());
    }

    @Test
    @DisplayName("Query customizada: deve buscar por conta, tipo e período")
    void queryCustomizadaDeveBuscarPorContaTipoEPeriodo() {
        List<Transacao> receitasJaneiro = transacaoRepository.findByContaIdAndTipoAndDataBetween(
                conta.getId(),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );

        assertEquals(2, receitasJaneiro.size());
    }

    @Test
    @DisplayName("Deve isolar transações por conta (não vazar entre contas)")
    void deveIsolarTransacoesPorConta() {
        Conta outraConta = new Conta("Outra Conta", BigDecimal.ZERO);
        entityManager.persistAndFlush(outraConta);

        Transacao t = new Transacao("Outra Transação", new BigDecimal("100"), TipoTransacao.RECEITA, LocalDate.now(), "Outros");
        outraConta.adicionarTransacao(t);
        entityManager.persistAndFlush(t);

        List<Transacao> transacoesContaOriginal = transacaoRepository.findByContaIdOrderByDataDesc(conta.getId());
        List<Transacao> transacoesOutraConta = transacaoRepository.findByContaIdOrderByDataDesc(outraConta.getId());

        assertEquals(4, transacoesContaOriginal.size());
        assertEquals(1, transacoesOutraConta.size());
    }
}