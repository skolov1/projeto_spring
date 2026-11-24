package br.edu.fatecpg.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * Formulário de conta. Os nomes dos campos são os mesmos do ContaRequestDTO do backend.
 */
public class ContaForm {

    @NotBlank(message = "Informe o nome da conta")
    private String nome;

    @NotNull(message = "Informe o saldo inicial")
    @PositiveOrZero(message = "O saldo inicial não pode ser negativo")
    private BigDecimal saldoInicial;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }

}
