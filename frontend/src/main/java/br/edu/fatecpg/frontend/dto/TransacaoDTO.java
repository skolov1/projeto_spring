package br.edu.fatecpg.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transação como a API devolve.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransacaoDTO {

    private Long id;

    private String descricao;

    private BigDecimal valor;

    private String tipo;

    private LocalDate data;

    private String categoria;

    private Long contaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Long getContaId() { return contaId; }
    public void setContaId(Long contaId) { this.contaId = contaId; }

    /** Usado nos templates para pintar receita (verde) e despesa (vermelho). */
    public boolean isReceita() { return "RECEITA".equals(tipo); }

}
