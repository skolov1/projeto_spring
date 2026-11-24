package br.edu.fatecpg.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Resposta de GET /contas/{id}/resumo (query nativa do backend).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumoDTO {

    private Long contaId;

    private String contaNome;

    private BigDecimal receitas;

    private BigDecimal despesas;

    public Long getContaId() { return contaId; }
    public void setContaId(Long contaId) { this.contaId = contaId; }

    public String getContaNome() { return contaNome; }
    public void setContaNome(String contaNome) { this.contaNome = contaNome; }

    public BigDecimal getReceitas() { return receitas; }
    public void setReceitas(BigDecimal receitas) { this.receitas = receitas; }

    public BigDecimal getDespesas() { return despesas; }
    public void setDespesas(BigDecimal despesas) { this.despesas = despesas; }

}
