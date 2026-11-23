package br.edu.fatecpg.projetospring.dto.response;

import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ContaResponseDTO {

    private Long id;
    private String nome;
    private BigDecimal saldoInicial;
    private BigDecimal saldoAtual;
    private List<TransacaoResumoDTO> transacoes;

    public ContaResponseDTO() {}

    public ContaResponseDTO(Long id, String nome, BigDecimal saldoInicial, BigDecimal saldoAtual) {
        this.id = id;
        this.nome = nome;
        this.saldoInicial = saldoInicial;
        this.saldoAtual = saldoAtual;
    }

    public ContaResponseDTO(Long id, String nome, BigDecimal saldoInicial, BigDecimal saldoAtual, List<TransacaoResumoDTO> transacoes) {
        this.id = id;
        this.nome = nome;
        this.saldoInicial = saldoInicial;
        this.saldoAtual = saldoAtual;
        this.transacoes = transacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public List<TransacaoResumoDTO> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoResumoDTO> transacoes) {
        this.transacoes = transacoes;
    }

    public static class TransacaoResumoDTO {
        private Long id;
        private String descricao;
        private BigDecimal valor;
        private TipoTransacao tipo;
        private LocalDate data;
        private String categoria;

        public TransacaoResumoDTO() {}

        public TransacaoResumoDTO(Long id, String descricao, BigDecimal valor, TipoTransacao tipo, LocalDate data, String categoria) {
            this.id = id;
            this.descricao = descricao;
            this.valor = valor;
            this.tipo = tipo;
            this.data = data;
            this.categoria = categoria;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }
        public TipoTransacao getTipo() { return tipo; }
        public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }
        public LocalDate getData() { return data; }
        public void setData(LocalDate data) { this.data = data; }
        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
    }
}