package br.edu.fatecpg.frontend.controller;

import br.edu.fatecpg.frontend.dto.ContaCard;
import br.edu.fatecpg.frontend.dto.ContaDTO;
import br.edu.fatecpg.frontend.dto.ContaForm;
import br.edu.fatecpg.frontend.dto.ResumoDTO;
import br.edu.fatecpg.frontend.exception.ApiException;
import br.edu.fatecpg.frontend.service.ContaApiService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/contas")
public class ContaController {

    private final ContaApiService contaService;

    public ContaController(ContaApiService contaService) {
        this.contaService = contaService;
    }

    /** Lista as contas, com os totais gerais no topo. */
    @GetMapping
    public String listar(Model model) {
        List<ContaDTO> contas = contaService.listar();
        List<ContaCard> cards = new ArrayList<>();
        BigDecimal saldoTotal = BigDecimal.ZERO;
        BigDecimal receitasTotal = BigDecimal.ZERO;
        BigDecimal despesasTotal = BigDecimal.ZERO;

        for (ContaDTO conta : contas) {
            ResumoDTO resumo = contaService.resumo(conta.getId());
            BigDecimal receitas = valor(resumo != null ? resumo.getReceitas() : null);
            BigDecimal despesas = valor(resumo != null ? resumo.getDespesas() : null);
            cards.add(new ContaCard(conta, receitas, despesas));
            saldoTotal = saldoTotal.add(valor(conta.getSaldoAtual()));
            receitasTotal = receitasTotal.add(receitas);
            despesasTotal = despesasTotal.add(despesas);
        }

        model.addAttribute("cards", cards);
        model.addAttribute("saldoTotal", saldoTotal);
        model.addAttribute("receitasTotal", receitasTotal);
        model.addAttribute("despesasTotal", despesasTotal);
        return "contas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        ContaForm form = new ContaForm();
        form.setSaldoInicial(BigDecimal.ZERO);
        model.addAttribute("contaForm", form);
        dadosDoFormulario(model, false, null);
        return "contas/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("contaForm") ContaForm form,
                        BindingResult resultado, Model model, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            dadosDoFormulario(model, false, null);
            return "contas/form";
        }
        form.setNome(form.getNome().trim());
        try {
            contaService.criar(form);
            ra.addFlashAttribute("mensagem", "Conta criada com sucesso!");
            return "redirect:/contas";
        } catch (ApiException e) {
            model.addAttribute("erro", e.getMensagemCompleta());
            dadosDoFormulario(model, false, null);
            return "contas/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model model) {
        ContaDTO conta = contaService.buscar(id);
        ContaForm form = new ContaForm();
        form.setNome(conta.getNome());
        form.setSaldoInicial(conta.getSaldoInicial());
        model.addAttribute("contaForm", form);
        dadosDoFormulario(model, true, id);
        return "contas/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable("id") Long id,
                            @Valid @ModelAttribute("contaForm") ContaForm form,
                            BindingResult resultado, Model model, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            dadosDoFormulario(model, true, id);
            return "contas/form";
        }
        form.setNome(form.getNome().trim());
        try {
            contaService.atualizar(id, form);
            ra.addFlashAttribute("mensagem", "Conta atualizada com sucesso!");
            return "redirect:/contas";
        } catch (ApiException e) {
            model.addAttribute("erro", e.getMensagemCompleta());
            dadosDoFormulario(model, true, id);
            return "contas/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            contaService.excluir(id);
            ra.addFlashAttribute("mensagem", "Conta excluída com sucesso!");
        } catch (ApiException e) {
            ra.addFlashAttribute("erro", e.getMensagemCompleta());
        }
        return "redirect:/contas";
    }

    private void dadosDoFormulario(Model model, boolean editando, Long id) {
        model.addAttribute("editando", editando);
        model.addAttribute("titulo", editando ? "Editar conta" : "Nova conta");
        model.addAttribute("acao", editando ? "/contas/" + id + "/editar" : "/contas");
    }

    private BigDecimal valor(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
