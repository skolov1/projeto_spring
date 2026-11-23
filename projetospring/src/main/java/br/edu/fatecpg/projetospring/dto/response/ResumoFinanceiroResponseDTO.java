package br.edu.fatecpg.projetospring.dto.response;

import java.math.BigDecimal;

public class ResumoFinanceiroResponseDTO {

    private Long contaId;
    private String contaNome;
    private BigDecimal receitas;
    private BigDecimal despesas;

    public ResumoFinanceiroResponseDTO() {}

    public ResumoFinanceiroResponseDTO(Long contaId, String contaNome, BigDecimal receitas, BigDecimal despesas) {
        this.contaId = contaId;
        this.contaNome = contaNome;
        this.receitas = receitas;
        this.despesas = despesas;
    }

    public Long getContaId() { return contaId; }
    public void setContaId(Long contaId) { this.contaId = contaId; }
    public String getContaNome() { return contaNome; }
    public void setContaNome(String contaNome) { this.contaNome = contaNome; }
    public BigDecimal getReceitas() { return receitas; }
    public void setReceitas(BigDecimal receitas) { this.receitas = receitas; }
    public BigDecimal getDespesas() { return despesas; }
    public void setDespesas(BigDecimal despesas) { this.despesas = despesas; }
}