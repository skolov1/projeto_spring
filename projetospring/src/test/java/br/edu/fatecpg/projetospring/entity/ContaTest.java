package br.edu.fatecpg.projetospring.entity;

import br.edu.fatecpg.projetospring.model.entity.Conta;
import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    private Conta conta;
    private Transacao receita1;
    private Transacao receita2;
    private Transacao despesa1;
    private Transacao despesa2;

    @BeforeEach
    void setUp() throws Exception {
        conta = new Conta("Conta Teste", new BigDecimal("1000.00"));
        setId(conta, 1L);

        receita1 = new Transacao("Salário", new BigDecimal("5000.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 5), "Salário");
        receita2 = new Transacao("Freelance", new BigDecimal("1200.00"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 20), "Trabalho Extra");
        despesa1 = new Transacao("Aluguel", new BigDecimal("1500.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 10), "Moradia");
        despesa2 = new Transacao("Supermercado", new BigDecimal("800.00"), TipoTransacao.DESPESA, LocalDate.of(2026, 1, 15), "Alimentação");

        setId(receita1, 10L);
        setId(receita2, 11L);
        setId(despesa1, 12L);
        setId(despesa2, 13L);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    @DisplayName("Deve criar conta com saldo inicial correto")
    void deveCriarContaComSaldoInicial() {
        assertEquals(new BigDecimal("1000.00"), conta.getSaldoInicial());
        assertEquals("Conta Teste", conta.getNome());
        assertTrue(conta.getTransacoes().isEmpty());
    }

    @Test
    @DisplayName("Deve adicionar transação e vincular bidirecionalmente")
    void deveAdicionarTransacaoEVincularBidirecionalmente() {
        conta.adicionarTransacao(receita1);

        assertEquals(1, conta.getTransacoes().size());
        assertEquals(conta, receita1.getConta());
        assertTrue(conta.getTransacoes().contains(receita1));
    }

    @Test
    @DisplayName("Deve remover transação e desvincular")
    void deveRemoverTransacaoEDesvincular() {
        conta.adicionarTransacao(receita1);
        conta.removerTransacao(receita1);

        assertTrue(conta.getTransacoes().isEmpty());
        assertNull(receita1.getConta());
    }

    @Test
    @DisplayName("Deve calcular saldo corretamente: saldoInicial + receitas - despesas")
    void deveCalcularSaldoCorretamente() {
        conta.adicionarTransacao(receita1);  // +5000
        conta.adicionarTransacao(receita2);  // +1200
        conta.adicionarTransacao(despesa1);  // -1500
        conta.adicionarTransacao(despesa2);  // -800

        // 1000 + 5000 + 1200 - 1500 - 800 = 4900
        BigDecimal saldoEsperado = new BigDecimal("4900.00");
        assertEquals(saldoEsperado, conta.calcularSaldo());
    }

    @Test
    @DisplayName("Deve calcular saldo apenas com receitas")
    void deveCalcularSaldoApenasComReceitas() {
        conta.adicionarTransacao(receita1);  // +5000
        conta.adicionarTransacao(receita2);  // +1200

        // 1000 + 5000 + 1200 = 7200
        BigDecimal saldoEsperado = new BigDecimal("7200.00");
        assertEquals(saldoEsperado, conta.calcularSaldo());
    }

    @Test
    @DisplayName("Deve calcular saldo apenas com despesas")
    void deveCalcularSaldoApenasComDespesas() {
        conta.adicionarTransacao(despesa1);  // -1500
        conta.adicionarTransacao(despesa2);  // -800

        // 1000 - 1500 - 800 = -1300
        BigDecimal saldoEsperado = new BigDecimal("-1300.00");
        assertEquals(saldoEsperado, conta.calcularSaldo());
    }

    @Test
    @DisplayName("Deve buscar transação por ID")
    void deveBuscarTransacaoPorId() {
        conta.adicionarTransacao(receita1);
        conta.adicionarTransacao(despesa1);

        Transacao encontrada = conta.buscarTransacao(receita1.getId());
        assertNotNull(encontrada);
        assertEquals(receita1.getId(), encontrada.getId());
        assertEquals("Salário", encontrada.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar null ao buscar transação inexistente")
    void deveRetornarNullAoBuscarTransacaoInexistente() {
        conta.adicionarTransacao(receita1);

        Transacao encontrada = conta.buscarTransacao(999L);
        assertNull(encontrada);
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar transação nula")
    void deveLancarExcecaoAoAdicionarTransacaoNula() {
        assertThrows(NullPointerException.class, () -> conta.adicionarTransacao(null));
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover transação nula")
    void deveLancarExcecaoAoRemoverTransacaoNula() {
        assertThrows(NullPointerException.class, () -> conta.removerTransacao(null));
    }

    @Test
    @DisplayName("Deve manter todas as transações adicionadas")
    void deveManterTodasTransacoesAdicionadas() throws Exception {
        // Datas fora de ordem
        Transacao t1 = new Transacao("T1", new BigDecimal("100"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 10), "Cat");
        Transacao t2 = new Transacao("T2", new BigDecimal("200"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 5), "Cat");
        Transacao t3 = new Transacao("T3", new BigDecimal("300"), TipoTransacao.RECEITA, LocalDate.of(2026, 1, 15), "Cat");

        setId(t1, 100L);
        setId(t2, 101L);
        setId(t3, 102L);

        conta.adicionarTransacao(t1);
        conta.adicionarTransacao(t2);
        conta.adicionarTransacao(t3);

        List<Transacao> transacoes = conta.getTransacoes();
        // Verifica se todas as transações estão na lista (ordem não garantida em memória sem JPA)
        assertEquals(3, transacoes.size());
        assertTrue(transacoes.contains(t1));
        assertTrue(transacoes.contains(t2));
        assertTrue(transacoes.contains(t3));
    }
}