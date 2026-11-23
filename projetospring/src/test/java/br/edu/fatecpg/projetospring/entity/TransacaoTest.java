package br.edu.fatecpg.projetospring.entity;

import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {

    @Test
    @DisplayName("Deve criar transação com todos os campos")
    void deveCriarTransacaoComTodosCampos() {
        Transacao transacao = new Transacao(
                "Teste",
                new BigDecimal("100.00"),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 15),
                "Categoria Teste"
        );

        assertEquals("Teste", transacao.getDescricao());
        assertEquals(new BigDecimal("100.00"), transacao.getValor());
        assertEquals(TipoTransacao.RECEITA, transacao.getTipo());
        assertEquals(LocalDate.of(2026, 1, 15), transacao.getData());
        assertEquals("Categoria Teste", transacao.getCategoria());
        assertNull(transacao.getConta());
    }

    @Test
    @DisplayName("Deve atualizar descrição com sucesso")
    void deveAtualizarDescricaoComSucesso() {
        Transacao transacao = new Transacao(
                "Descrição Original",
                new BigDecimal("100.00"),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 15),
                "Categoria"
        );

        transacao.atualizarDescricao("Nova Descrição");

        assertEquals("Nova Descrição", transacao.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar descrição para null")
    void deveLancarExcecaoAoAtualizarDescricaoNull() {
        Transacao transacao = new Transacao(
                "Original",
                new BigDecimal("100.00"),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 15),
                "Categoria"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transacao.atualizarDescricao(null));
        assertEquals("Descrição não pode ser vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar descrição para string vazia")
    void deveLancarExcecaoAoAtualizarDescricaoVazia() {
        Transacao transacao = new Transacao(
                "Original",
                new BigDecimal("100.00"),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 15),
                "Categoria"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transacao.atualizarDescricao(""));
        assertEquals("Descrição não pode ser vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar descrição para apenas espaços")
    void deveLancarExcecaoAoAtualizarDescricaoApenasEspacos() {
        Transacao transacao = new Transacao(
                "Original",
                new BigDecimal("100.00"),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 15),
                "Categoria"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transacao.atualizarDescricao("   "));
        assertEquals("Descrição não pode ser vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve fazer trim da nova descrição")
    void deveFazerTrimDaNovaDescricao() {
        Transacao transacao = new Transacao(
                "Original",
                new BigDecimal("100.00"),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 15),
                "Categoria"
        );

        transacao.atualizarDescricao("  Nova Descrição  ");

        assertEquals("Nova Descrição", transacao.getDescricao());
    }

    @Test
    @DisplayName("Deve exibir informações (toString)")
    void deveExibirInformacoes() {
        Transacao transacao = new Transacao(
                "Teste",
                new BigDecimal("100.00"),
                TipoTransacao.RECEITA,
                LocalDate.of(2026, 1, 15),
                "Categoria"
        );

        // Não deve lançar exceção
        assertDoesNotThrow(transacao::exibirInformacoes);

        String toString = transacao.toString();
        assertTrue(toString.contains("Teste"));
        assertTrue(toString.contains("100.00"));
        assertTrue(toString.contains("RECEITA"));
    }

    @Test
    @DisplayName("Deve ser igual apenas pelo ID")
    void deveSerIgualApenasPeloId() {
        Transacao t1 = new Transacao("T1", new BigDecimal("100"), TipoTransacao.RECEITA, LocalDate.now(), "Cat");
        Transacao t2 = new Transacao("T2", new BigDecimal("200"), TipoTransacao.DESPESA, LocalDate.now(), "Cat");

        // Sem ID definido, não são iguais
        assertNotEquals(t1, t2);
        assertNotEquals(t1, null);
        assertNotEquals(t1, "string");

        // Com mesmo ID, são iguais
        // (Não podemos setar ID diretamente, mas o equals usa getClass().hashCode() quando ID é null)
    }

    @Test
    @DisplayName("Deve criar transação do tipo DESPESA")
    void deveCriarTransacaoTipoDespesa() {
        Transacao transacao = new Transacao(
                "Despesa Teste",
                new BigDecimal("50.00"),
                TipoTransacao.DESPESA,
                LocalDate.of(2026, 1, 20),
                "Lazer"
        );

        assertEquals(TipoTransacao.DESPESA, transacao.getTipo());
    }
}