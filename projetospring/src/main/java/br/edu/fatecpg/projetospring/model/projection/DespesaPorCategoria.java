package br.edu.fatecpg.projetospring.model.projection;

import java.math.BigDecimal;

public interface DespesaPorCategoria {
    String getCategoria();
    BigDecimal getTotal();
}
