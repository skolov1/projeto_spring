package br.edu.fatecpg.projetospring.model.entity;

import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da conta é obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    @NotNull(message = "Saldo inicial é obrigatório")
    @PositiveOrZero(message = "Saldo inicial deve ser zero ou positivo")
    @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("data DESC, id DESC")
    private List<Transacao> transacoes = new ArrayList<>();

    protected Conta() {}

    public Conta(String nome, BigDecimal saldoInicial) {
        this.nome = nome;
        this.saldoInicial = saldoInicial;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void adicionarTransacao(Transacao transacao) {
        Objects.requireNonNull(transacao, "Transação não pode ser nula");
        transacoes.add(transacao);
        transacao.setConta(this);
    }

    public void removerTransacao(Transacao transacao) {
        Objects.requireNonNull(transacao, "Transação não pode ser nula");
        transacoes.remove(transacao);
        transacao.setConta(null);
    }

    public Transacao buscarTransacao(Long id) {
        return transacoes.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public BigDecimal calcularTotalReceitas() {
        return somarPorTipo(TipoTransacao.RECEITA);
    }

    public BigDecimal calcularTotalDespesas() {
        return somarPorTipo(TipoTransacao.DESPESA);
    }

    public BigDecimal calcularSaldo() {
        return saldoInicial.add(calcularTotalReceitas()).subtract(calcularTotalDespesas());
    }

    private BigDecimal somarPorTipo(TipoTransacao tipo) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == tipo)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void exibirTransacoes() {
        transacoes.forEach(Transacao::exibirInformacoes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conta)) return false;
        Conta conta = (Conta) o;
        return id != null && id.equals(conta.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Conta{id=" + id + ", nome='" + nome + "', saldoInicial=" + saldoInicial + "}";
    }
}