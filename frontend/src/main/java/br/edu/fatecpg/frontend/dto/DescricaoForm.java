package br.edu.fatecpg.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Corpo do PATCH /contas/{contaId}/transacoes/{id}/descricao.
 */
public class DescricaoForm {

    @NotBlank(message = "Informe a nova descrição")
    @Size(max = 255, message = "A descrição pode ter no máximo 255 caracteres")
    private String novaDescricao;

    public String getNovaDescricao() { return novaDescricao; }
    public void setNovaDescricao(String novaDescricao) { this.novaDescricao = novaDescricao; }

}
