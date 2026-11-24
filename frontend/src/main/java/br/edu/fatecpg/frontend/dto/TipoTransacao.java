package br.edu.fatecpg.frontend.dto;

/** Mesmos valores do enum do backend (RECEITA | DESPESA), com um rótulo para exibir nas telas. */
public enum TipoTransacao {

    RECEITA("Receita"),
    DESPESA("Despesa");

    private final String rotulo;

    TipoTransacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
