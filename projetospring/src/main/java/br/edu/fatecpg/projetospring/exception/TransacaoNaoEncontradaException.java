package br.edu.fatecpg.projetospring.exception;

public class TransacaoNaoEncontradaException extends BusinessException {

    public TransacaoNaoEncontradaException(Long id) {
        super("Transação não encontrada com ID: " + id, "TRANSACAO_NAO_ENCONTRADA");
    }
}