package br.edu.fatecpg.frontend.dto;

import java.math.BigDecimal;

public record SaldoResponseDTO(
        Long contaId,
        BigDecimal saldoInicial,
        BigDecimal totalReceitas,
        BigDecimal totalDespesas,
        BigDecimal saldoAtual
) {}
