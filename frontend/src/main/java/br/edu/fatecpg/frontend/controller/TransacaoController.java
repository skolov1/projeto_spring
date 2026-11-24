package br.edu.fatecpg.frontend.controller;

import br.edu.fatecpg.frontend.dto.ContaDTO;
import br.edu.fatecpg.frontend.dto.DescricaoForm;
import br.edu.fatecpg.frontend.dto.DespesaCategoriaDTO;
import br.edu.fatecpg.frontend.dto.ResumoDTO;
import br.edu.fatecpg.frontend.dto.TipoTransacao;
import br.edu.fatecpg.frontend.dto.TransacaoDTO;
import br.edu.fatecpg.frontend.dto.TransacaoForm;
import br.edu.fatecpg.frontend.exception.ApiException;
import br.edu.fatecpg.frontend.service.ContaApiService;
import br.edu.fatecpg.frontend.service.TransacaoApiService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Extrato da conta: resumo, filtros, lançamento de transações e gráfico de despesas por categoria. */
@Controller
@RequestMapping("/contas/{contaId}/transacoes")
public class TransacaoController {

    private final ContaApiService contaService;
    private final TransacaoApiService transacaoService;

    public TransacaoController(ContaApiService contaService, TransacaoApiService transacaoService) {
        this.contaService = contaService;
        this.transacaoService = transacaoService;
    }

    @GetMapping
    public String extrato(@PathVariable("contaId") Long contaId,
                          @RequestParam(name = "tipo", required = false) String tipo,
                          @RequestParam(name = "descricao", required = false) String descricao,
                          @RequestParam(name = "inicio", required = false)
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                          @RequestParam(name = "fim", required = false)
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                          Model model) {
        carregarExtrato(contaId, tipo, descricao, inicio, fim, model);
        return "transacoes/extrato";
    }

    @PostMapping
    public String criar(@PathVariable("contaId") Long contaId,
                        @Valid @ModelAttribute("transacaoForm") TransacaoForm form,
                        BindingResult resultado, Model model, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            carregarExtrato(contaId, null, null, null, null, model);
            return "transacoes/extrato";
        }
        form.setDescricao(form.getDescricao().trim());
        if (form.getCategoria() == null || form.getCategoria().isBlank()) {
            form.setCategoria(null);
        } else {
            form.setCategoria(form.getCategoria().trim());
        }
        try {
            transacaoService.criar(contaId, form);
            ra.addFlashAttribute("mensagem", "Transação registrada com sucesso!");
            return "redirect:/contas/" + contaId + "/transacoes";
        } catch (ApiException e) {
            model.addAttribute("erro", e.getMensagemCompleta());
            carregarExtrato(contaId, null, null, null, null, model);
            return "transacoes/extrato";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("contaId") Long contaId,
                          @PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            transacaoService.excluir(contaId, id);
            ra.addFlashAttribute("mensagem", "Transação excluída com sucesso!");
        } catch (ApiException e) {
            ra.addFlashAttribute("erro", e.getMensagemCompleta());
        }
        return "redirect:/contas/" + contaId + "/transacoes";
    }

    @GetMapping("/{id}/descricao")
    public String editarDescricao(@PathVariable("contaId") Long contaId,
                                  @PathVariable("id") Long id, Model model) {
        TransacaoDTO transacao = transacaoService.buscar(contaId, id);
        DescricaoForm form = new DescricaoForm();
        form.setNovaDescricao(transacao.getDescricao());
        model.addAttribute("contaId", contaId);
        model.addAttribute("transacao", transacao);
        model.addAttribute("descricaoForm", form);
        return "transacoes/descricao";
    }

    @PostMapping("/{id}/descricao")
    public String salvarDescricao(@PathVariable("contaId") Long contaId,
                                  @PathVariable("id") Long id,
                                  @Valid @ModelAttribute("descricaoForm") DescricaoForm form,
                                  BindingResult resultado, Model model, RedirectAttributes ra) {
        if (resultado.hasErrors()) {
            return voltarParaDescricao(contaId, id, model, null);
        }
        form.setNovaDescricao(form.getNovaDescricao().trim());
        try {
            transacaoService.atualizarDescricao(contaId, id, form);
            ra.addFlashAttribute("mensagem", "Descrição atualizada com sucesso!");
            return "redirect:/contas/" + contaId + "/transacoes";
        } catch (ApiException e) {
            return voltarParaDescricao(contaId, id, model, e.getMensagemCompleta());
        }
    }

    private String voltarParaDescricao(Long contaId, Long id, Model model, String erro) {
        model.addAttribute("contaId", contaId);
        model.addAttribute("transacao", transacaoService.buscar(contaId, id));
        if (erro != null) {
            model.addAttribute("erro", erro);
        }
        return "transacoes/descricao";
    }

    /** Monta tudo que a tela do extrato precisa. Erros dos filtros aparecem na tela sem derrubá-la. */
    private void carregarExtrato(Long contaId, String tipo, String descricao,
                                 LocalDate inicio, LocalDate fim, Model model) {
        ContaDTO conta = contaService.buscar(contaId);
        ResumoDTO resumo = contaService.resumo(contaId);

        String erro = null;
        if ((inicio == null) != (fim == null)) {
            erro = "Para filtrar por período, informe a data inicial e a data final.";
            inicio = null;
            fim = null;
        }

        List<TransacaoDTO> transacoes = new ArrayList<>();
        try {
            transacoes.addAll(transacaoService.listar(contaId, tipo, descricao, inicio, fim));
        } catch (ApiException e) {
            erro = e.getMensagemCompleta();
        }
        transacoes.sort(Comparator
                .comparing(TransacaoDTO::getData, Comparator.nullsLast(Comparator.<LocalDate>reverseOrder()))
                .thenComparing(TransacaoDTO::getId, Comparator.nullsLast(Comparator.<Long>reverseOrder())));

        List<String> categorias = new ArrayList<>();
        List<BigDecimal> totais = new ArrayList<>();
        try {
            for (DespesaCategoriaDTO d : contaService.despesasPorCategoria(contaId)) {
                categorias.add(d.getCategoria() != null && !d.getCategoria().isBlank()
                        ? d.getCategoria() : "Sem categoria");
                totais.add(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
            }
        } catch (ApiException e) {
            // o gráfico é um extra: se falhar, o resto da tela continua funcionando
        }

        if (!model.containsAttribute("transacaoForm")) {
            TransacaoForm form = new TransacaoForm();
            form.setData(LocalDate.now());
            model.addAttribute("transacaoForm", form);
        }
        if (erro != null && !model.containsAttribute("erro")) {
            model.addAttribute("erro", erro);
        }

        model.addAttribute("conta", conta);
        model.addAttribute("receitas", resumo != null && resumo.getReceitas() != null
                ? resumo.getReceitas() : BigDecimal.ZERO);
        model.addAttribute("despesas", resumo != null && resumo.getDespesas() != null
                ? resumo.getDespesas() : BigDecimal.ZERO);
        model.addAttribute("transacoes", transacoes);
        model.addAttribute("tipos", TipoTransacao.values());
        model.addAttribute("catNomes", categorias);
        model.addAttribute("catTotais", totais);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroDescricao", descricao);
        model.addAttribute("filtroInicio", inicio);
        model.addAttribute("filtroFim", fim);
        model.addAttribute("temFiltro", (tipo != null && !tipo.isBlank())
                || (descricao != null && !descricao.isBlank()) || inicio != null);
    }
}
