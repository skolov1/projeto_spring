package br.edu.fatecpg.projetospring.model.projection;

import java.math.BigDecimal;

public interface ResumoFinanceiro {
    BigDecimal getReceitas();
    BigDecimal getDespesas();
}