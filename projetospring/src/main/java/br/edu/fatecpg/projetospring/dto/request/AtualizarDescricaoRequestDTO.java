package br.edu.fatecpg.projetospring.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AtualizarDescricaoRequestDTO {

    @NotBlank(message = "Nova descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String novaDescricao;

    public AtualizarDescricaoRequestDTO() {}

    public AtualizarDescricaoRequestDTO(String novaDescricao) {
        this.novaDescricao = novaDescricao;
    }

    public String getNovaDescricao() {
        return novaDescricao;
    }

    public void setNovaDescricao(String novaDescricao) {
        this.novaDescricao = novaDescricao;
    }
}