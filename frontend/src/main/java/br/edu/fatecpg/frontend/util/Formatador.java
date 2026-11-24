package br.edu.fatecpg.frontend.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formata dinheiro e datas em pt-BR. Nos templates: ${@fmt.moeda(valor)} e ${@fmt.data(data)}. */
@Component("fmt")
public class Formatador {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String moeda(BigDecimal valor) {
        BigDecimal v = valor != null ? valor : BigDecimal.ZERO;
        return NumberFormat.getCurrencyInstance(PT_BR).format(v);
    }

    public String data(LocalDate data) {
        return data != null ? data.format(DATA) : "";
    }
}
