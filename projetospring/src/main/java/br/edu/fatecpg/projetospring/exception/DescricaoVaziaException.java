package br.edu.fatecpg.projetospring.exception;

public class DescricaoVaziaException extends BusinessException {

    public DescricaoVaziaException() {
        super("Descrição não pode ser vazia", "DESCRICAO_VAZIA");
    }
}