package br.edu.fatecpg.projetospring.controller;

import br.edu.fatecpg.projetospring.dto.response.TransacaoResponseDTO;
import br.edu.fatecpg.projetospring.model.enum_.TipoTransacao;
import br.edu.fatecpg.projetospring.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Consulta de transações de todas as contas, expondo as queries derivadas
 * (findByTipo, findByDescricaoContainingIgnoreCase, findByDataBetween) e a JPQL listarOrdenadasPorData.
 */
@RestController
@RequestMapping("/transacoes")
@Tag(name = "Transações (todas as contas)", description = "Consulta geral de transações usando queries derivadas e JPQL")
public class TransacaoGeralController {

    private final TransacaoService transacaoService;

    public TransacaoGeralController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping
    @Operation(summary = "Listar transações de todas as contas, ordenadas por data, com filtros opcionais")
    public ResponseEntity<List<TransacaoResponseDTO>> listar(
            @Parameter(description = "Filtrar por tipo (RECEITA ou DESPESA)") @RequestParam(required = false) TipoTransacao tipo,
            @Parameter(description = "Filtrar por descrição (busca parcial, sem diferenciar maiúsculas)") @RequestParam(required = false) String descricao,
            @Parameter(description = "Data inicial (yyyy-MM-dd); informar junto com fim")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Data final (yyyy-MM-dd); informar junto com inicio")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(transacaoService.listarTodas(tipo, descricao, inicio, fim));
    }
}
