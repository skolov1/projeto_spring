package br.edu.fatecpg.frontend.dto;

import java.math.BigDecimal;

/** Dados de um card na tela de contas: a conta mais o total de receitas e de despesas dela. */
public class ContaCard {

    private final ContaDTO conta;
    private final BigDecimal receitas;
    private final BigDecimal despesas;

    public ContaCard(ContaDTO conta, BigDecimal receitas, BigDecimal despesas) {
        this.conta = conta;
        this.receitas = receitas;
        this.despesas = despesas;
    }

    public ContaDTO getConta() { return conta; }
    public BigDecimal getReceitas() { return receitas; }
    public BigDecimal getDespesas() { return despesas; }
}
