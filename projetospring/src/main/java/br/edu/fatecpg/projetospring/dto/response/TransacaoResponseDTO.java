package br.edu.fatecpg.projetospring.dto.response;

import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransacaoResponseDTO {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private TipoTransacao tipo;
    private LocalDate data;
    private String categoria;
    private Long contaId;

    public TransacaoResponseDTO() {}

    public TransacaoResponseDTO(Long id, String descricao, BigDecimal valor, TipoTransacao tipo, LocalDate data, String categoria, Long contaId) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
        this.categoria = categoria;
        this.contaId = contaId;
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
    public Long getContaId() { return contaId; }
    public void setContaId(Long contaId) { this.contaId = contaId; }
}