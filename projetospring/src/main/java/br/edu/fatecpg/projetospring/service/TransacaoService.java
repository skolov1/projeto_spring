package br.edu.fatecpg.projetospring.service;

import br.edu.fatecpg.projetospring.dto.request.TransacaoRequestDTO;
import br.edu.fatecpg.projetospring.dto.request.AtualizarDescricaoRequestDTO;
import br.edu.fatecpg.projetospring.dto.response.TransacaoResponseDTO;
import br.edu.fatecpg.projetospring.exception.ContaNaoEncontradaException;
import br.edu.fatecpg.projetospring.exception.TransacaoNaoEncontradaException;
import br.edu.fatecpg.projetospring.exception.DescricaoVaziaException;
import br.edu.fatecpg.projetospring.model.entity.Conta;
import br.edu.fatecpg.projetospring.model.entity.Transacao;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.repository.ContaRepository;
import br.edu.fatecpg.projetospring.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaRepository contaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
    }

    public List<TransacaoResponseDTO> listarPorConta(Long contaId, TipoTransacao tipo, String descricao, LocalDate inicio, LocalDate fim) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException(contaId));

        List<Transacao> transacoes;

        boolean temTipo = tipo != null;
        boolean temDescricao = descricao != null && !descricao.trim().isEmpty();
        boolean temPeriodo = inicio != null && fim != null;

        if (temTipo && temDescricao && temPeriodo) {
            transacoes = transacaoRepository.findByContaIdAndTipoAndDataBetween(contaId, tipo, inicio, fim);
            transacoes = transacoes.stream()
                    .filter(t -> t.getDescricao().toLowerCase().contains(descricao.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (temTipo && temPeriodo) {
            transacoes = transacaoRepository.findByContaIdAndTipoAndDataBetween(contaId, tipo, inicio, fim);
        } else if (temDescricao && temPeriodo) {
            transacoes = transacaoRepository.findByContaIdAndDataBetween(contaId, inicio, fim);
            transacoes = transacoes.stream()
                    .filter(t -> t.getDescricao().toLowerCase().contains(descricao.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (temTipo && temDescricao) {
            transacoes = transacaoRepository.findByContaIdAndTipo(contaId, tipo);
            transacoes = transacoes.stream()
                    .filter(t -> t.getDescricao().toLowerCase().contains(descricao.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (temTipo) {
            transacoes = transacaoRepository.findByContaIdAndTipo(contaId, tipo);
        } else if (temDescricao) {
            transacoes = transacaoRepository.findByContaIdAndDescricaoContaining(contaId, descricao);
        } else if (temPeriodo) {
            transacoes = transacaoRepository.findByContaIdAndDataBetween(contaId, inicio, fim);
        } else {
            transacoes = transacaoRepository.findByContaIdOrderByDataDesc(contaId);
        }

        return transacoes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public TransacaoResponseDTO buscarPorId(Long contaId, Long id) {
        return toResponseDTO(buscarTransacaoDaConta(contaId, id));
    }

    @Transactional
    public TransacaoResponseDTO criar(Long contaId, TransacaoRequestDTO request) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException(contaId));

        Transacao transacao = new Transacao(
                request.getDescricao(),
                request.getValor(),
                request.getTipo(),
                request.getData(),
                request.getCategoria()
        );

        conta.adicionarTransacao(transacao);
        Transacao salva = transacaoRepository.save(transacao);
        return toResponseDTO(salva);
    }

    @Transactional
    public TransacaoResponseDTO atualizarDescricao(Long contaId, Long id, AtualizarDescricaoRequestDTO request) {
        Transacao transacao = buscarTransacaoDaConta(contaId, id);

        try {
            transacao.atualizarDescricao(request.getNovaDescricao());
        } catch (IllegalArgumentException e) {
            throw new DescricaoVaziaException();
        }

        Transacao atualizada = transacaoRepository.save(transacao);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public void remover(Long contaId, Long id) {
        Transacao transacao = buscarTransacaoDaConta(contaId, id);

        Conta conta = transacao.getConta();
        if (conta != null) {
            conta.removerTransacao(transacao);
        }
        transacaoRepository.delete(transacao);
    }

    /**
     * Busca a transação garantindo que ela pertence à conta informada na rota.
     * Conta inexistente gera 404 de conta; transação de outra conta gera 404 de transação.
     */
    private Transacao buscarTransacaoDaConta(Long contaId, Long id) {
        return transacaoRepository.findByIdAndContaId(id, contaId)
                .orElseThrow(() -> contaRepository.existsById(contaId)
                        ? new TransacaoNaoEncontradaException(id)
                        : new ContaNaoEncontradaException(contaId));
    }

    private TransacaoResponseDTO toResponseDTO(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getTipo(),
                transacao.getData(),
                transacao.getCategoria(),
                transacao.getConta() != null ? transacao.getConta().getId() : null
        );
    }
}