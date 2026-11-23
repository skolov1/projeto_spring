package br.edu.fatecpg.frontend.dto;

import java.math.BigDecimal;

public record ResumoFinanceiroResponseDTO(
        BigDecimal receitas,
        BigDecimal despesas
) {}
