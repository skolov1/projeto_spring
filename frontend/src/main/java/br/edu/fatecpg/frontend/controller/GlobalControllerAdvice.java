package br.edu.fatecpg.frontend.controller;

import br.edu.fatecpg.frontend.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Qualquer erro da API que o controller não tratou vira uma página de erro amigável. */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public String tratarErroDaApi(ApiException e, Model model, HttpServletResponse resposta) {
        resposta.setStatus(e.getStatus() == 404 ? 404 : 502);
        model.addAttribute("erro", e.getMensagemCompleta());
        return "erro";
    }
}
