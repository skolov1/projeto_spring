package br.edu.fatecpg.projetospring.dto.response;

import java.math.BigDecimal;

public class DespesaPorCategoriaResponseDTO {

    private String categoria;
    private BigDecimal total;

    public DespesaPorCategoriaResponseDTO() {}

    public DespesaPorCategoriaResponseDTO(String categoria, BigDecimal total) {
        this.categoria = categoria;
        this.total = total;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
