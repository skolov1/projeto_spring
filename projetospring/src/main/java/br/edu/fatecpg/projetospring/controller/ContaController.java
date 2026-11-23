package br.edu.fatecpg.projetospring.controller;

import br.edu.fatecpg.projetospring.dto.request.ContaRequestDTO;
import br.edu.fatecpg.projetospring.dto.response.ContaResponseDTO;
import br.edu.fatecpg.projetospring.dto.response.SaldoResponseDTO;
import br.edu.fatecpg.projetospring.dto.response.ResumoFinanceiroResponseDTO;
import br.edu.fatecpg.projetospring.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
@Tag(name = "Contas", description = "Gerenciamento de contas financeiras")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas")
    public ResponseEntity<List<ContaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(contaService.listarTodas());
    }

    @PostMapping
    @Operation(summary = "Criar nova conta")
    public ResponseEntity<ContaResponseDTO> criar(@Valid @RequestBody ContaRequestDTO request) {
        ContaResponseDTO response = contaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID")
    public ResponseEntity<ContaResponseDTO> buscarPorId(@Parameter(description = "ID da conta") @PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @GetMapping("/{id}/detalhes")
    @Operation(summary = "Buscar conta com transações")
    public ResponseEntity<ContaResponseDTO> buscarComTransacoes(@Parameter(description = "ID da conta") @PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorIdComTransacoes(id));
    }

    @GetMapping("/{id}/saldo")
    @Operation(summary = "Calcular saldo da conta")
    public ResponseEntity<SaldoResponseDTO> calcularSaldo(@Parameter(description = "ID da conta") @PathVariable Long id) {
        return ResponseEntity.ok(contaService.calcularSaldo(id));
    }

    @GetMapping("/{id}/resumo")
    @Operation(summary = "Obter resumo financeiro (receitas e despesas)")
    public ResponseEntity<ResumoFinanceiroResponseDTO> obterResumo(@Parameter(description = "ID da conta") @PathVariable Long id) {
        return ResponseEntity.ok(contaService.obterResumo(id));
    }
}