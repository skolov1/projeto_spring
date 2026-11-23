package br.edu.fatecpg.projetospring.controller;

import br.edu.fatecpg.projetospring.dto.request.TransacaoRequestDTO;
import br.edu.fatecpg.projetospring.dto.request.AtualizarDescricaoRequestDTO;
import br.edu.fatecpg.projetospring.dto.response.TransacaoResponseDTO;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/contas/{contaId}/transacoes")
@Tag(name = "Transações", description = "Gerenciamento de transações financeiras")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    @Operation(summary = "Listar transações com filtros")
    public ResponseEntity<List<TransacaoResponseDTO>> listar(
            @Parameter(description = "ID da conta") @PathVariable Long contaId,
            @Parameter(description = "Filtrar por tipo (RECEITA ou DESPESA)") @RequestParam(required = false) TipoTransacao tipo,
            @Parameter(description = "Filtrar por descrição (busca parcial)") @RequestParam(required = false) String descricao,
            @Parameter(description = "Data inicial (formato: yyyy-MM-dd)") @RequestParam(required = false) String inicio,
            @Parameter(description = "Data final (formato: yyyy-MM-dd)") @RequestParam(required = false) String fim) {

        LocalDate dataInicio = parseDate(inicio);
        LocalDate dataFim = parseDate(fim);

        List<TransacaoResponseDTO> transacoes = transacaoService.listarPorConta(contaId, tipo, descricao, dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }

    @PostMapping
    @Operation(summary = "Registrar nova transação")
    public ResponseEntity<TransacaoResponseDTO> criar(
            @Parameter(description = "ID da conta") @PathVariable Long contaId,
            @Valid @RequestBody TransacaoRequestDTO request) {
        TransacaoResponseDTO response = transacaoService.criar(contaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID")
    public ResponseEntity<TransacaoResponseDTO> buscarPorId(
            @Parameter(description = "ID da conta") @PathVariable Long contaId,
            @Parameter(description = "ID da transação") @PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/descricao")
    @Operation(summary = "Atualizar descrição da transação")
    public ResponseEntity<TransacaoResponseDTO> atualizarDescricao(
            @Parameter(description = "ID da conta") @PathVariable Long contaId,
            @Parameter(description = "ID da transação") @PathVariable Long id,
            @Valid @RequestBody AtualizarDescricaoRequestDTO request) {
        return ResponseEntity.ok(transacaoService.atualizarDescricao(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover transação")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID da conta") @PathVariable Long contaId,
            @Parameter(description = "ID da transação") @PathVariable Long id) {
        transacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }
}