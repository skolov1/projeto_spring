package br.edu.fatecpg.projetospring.dto.response;

import java.math.BigDecimal;

public class SaldoResponseDTO {

    private Long contaId;
    private String contaNome;
    private BigDecimal saldoInicial;
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal saldoAtual;

    public SaldoResponseDTO() {}

    public SaldoResponseDTO(Long contaId, String contaNome, BigDecimal saldoInicial, BigDecimal totalReceitas, BigDecimal totalDespesas, BigDecimal saldoAtual) {
        this.contaId = contaId;
        this.contaNome = contaNome;
        this.saldoInicial = saldoInicial;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldoAtual = saldoAtual;
    }

    public Long getContaId() { return contaId; }
    public void setContaId(Long contaId) { this.contaId = contaId; }
    public String getContaNome() { return contaNome; }
    public void setContaNome(String contaNome) { this.contaNome = contaNome; }
    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }
    public BigDecimal getTotalReceitas() { return totalReceitas; }
    public void setTotalReceitas(BigDecimal totalReceitas) { this.totalReceitas = totalReceitas; }
    public BigDecimal getTotalDespesas() { return totalDespesas; }
    public void setTotalDespesas(BigDecimal totalDespesas) { this.totalDespesas = totalDespesas; }
    public BigDecimal getSaldoAtual() { return saldoAtual; }
    public void setSaldoAtual(BigDecimal saldoAtual) { this.saldoAtual = saldoAtual; }
}