package br.edu.fatecpg.frontend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoRequestDTO(
        String descricao,
        BigDecimal valor,
        String tipo,
        LocalDate data,
        String categoria
) {}
