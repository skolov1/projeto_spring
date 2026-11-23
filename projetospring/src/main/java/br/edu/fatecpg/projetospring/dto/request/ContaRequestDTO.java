package br.edu.fatecpg.projetospring.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class ContaRequestDTO {

    @NotBlank(message = "Nome da conta é obrigatório")
    private String nome;

    @NotNull(message = "Saldo inicial é obrigatório")
    @PositiveOrZero(message = "Saldo inicial deve ser zero ou positivo")
    private BigDecimal saldoInicial;

    public ContaRequestDTO() {}

    public ContaRequestDTO(String nome, BigDecimal saldoInicial) {
        this.nome = nome;
        this.saldoInicial = saldoInicial;
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
}