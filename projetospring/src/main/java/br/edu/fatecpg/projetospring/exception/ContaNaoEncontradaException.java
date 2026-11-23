package br.edu.fatecpg.projetospring.exception;

public class ContaNaoEncontradaException extends BusinessException {

    public ContaNaoEncontradaException(Long id) {
        super("Conta não encontrada com ID: " + id, "CONTA_NAO_ENCONTRADA");
    }

    public ContaNaoEncontradaException(String nome) {
        super("Conta não encontrada com nome: " + nome, "CONTA_NAO_ENCONTRADA");
    }
}