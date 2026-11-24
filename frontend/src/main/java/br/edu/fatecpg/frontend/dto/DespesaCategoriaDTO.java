package br.edu.fatecpg.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Resposta de GET /contas/{id}/despesas-por-categoria (usada no gráfico).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DespesaCategoriaDTO {

    private String categoria;

    private BigDecimal total;

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

}
