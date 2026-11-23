package br.edu.fatecpg.projetospring.service;

import br.edu.fatecpg.projetospring.dto.request.ContaRequestDTO;
import br.edu.fatecpg.projetospring.dto.response.ContaResponseDTO;
import br.edu.fatecpg.projetospring.dto.response.SaldoResponseDTO;
import br.edu.fatecpg.projetospring.dto.response.ResumoFinanceiroResponseDTO;
import br.edu.fatecpg.projetospring.exception.ContaNaoEncontradaException;
import br.edu.fatecpg.projetospring.model.entity.Conta;
import br.edu.fatecpg.projetospring.model.projection.ResumoFinanceiro;
import br.edu.fatecpg.projetospring.repository.ContaRepository;
import br.edu.fatecpg.projetospring.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ContaService {

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository, TransacaoRepository transacaoRepository) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public List<ContaResponseDTO> listarTodas() {
        return contaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ContaResponseDTO buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
        return toResponseDTO(conta);
    }

    public ContaResponseDTO buscarPorIdComTransacoes(Long id) {
        Conta conta = contaRepository.findByIdWithTransacoes(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
        return toResponseDTOComTransacoes(conta);
    }

    @Transactional
    public ContaResponseDTO criar(ContaRequestDTO request) {
        if (contaRepository.existsByNome(request.getNome())) {
            throw new br.edu.fatecpg.projetospring.exception.BusinessException(
                    "Já existe uma conta com este nome", "DUPLICATE_ENTRY");
        }

        Conta conta = new Conta(request.getNome(), request.getSaldoInicial());
        Conta salva = contaRepository.save(conta);
        return toResponseDTO(salva);
    }

    public SaldoResponseDTO calcularSaldo(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));

        return new SaldoResponseDTO(
                conta.getId(),
                conta.getNome(),
                conta.getSaldoInicial(),
                conta.calcularTotalReceitas(),
                conta.calcularTotalDespesas(),
                conta.calcularSaldo()
        );
    }

    public ResumoFinanceiroResponseDTO obterResumo(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));

        ResumoFinanceiro resumo = transacaoRepository.somarReceitasEDespesas(id);

        BigDecimal receitas = resumo.getReceitas() != null ? resumo.getReceitas() : BigDecimal.ZERO;
        BigDecimal despesas = resumo.getDespesas() != null ? resumo.getDespesas() : BigDecimal.ZERO;

        return new ResumoFinanceiroResponseDTO(conta.getId(), conta.getNome(), receitas, despesas);
    }

    private ContaResponseDTO toResponseDTO(Conta conta) {
        BigDecimal saldoAtual = conta.calcularSaldo();
        return new ContaResponseDTO(conta.getId(), conta.getNome(), conta.getSaldoInicial(), saldoAtual);
    }

    private ContaResponseDTO toResponseDTOComTransacoes(Conta conta) {
        BigDecimal saldoAtual = conta.calcularSaldo();
        List<ContaResponseDTO.TransacaoResumoDTO> transacoesDTO = conta.getTransacoes().stream()
                .map(t -> new ContaResponseDTO.TransacaoResumoDTO(
                        t.getId(), t.getDescricao(), t.getValor(), t.getTipo(), t.getData(), t.getCategoria()))
                .collect(Collectors.toList());
        return new ContaResponseDTO(conta.getId(), conta.getNome(), conta.getSaldoInicial(), saldoAtual, transacoesDTO);
    }
}