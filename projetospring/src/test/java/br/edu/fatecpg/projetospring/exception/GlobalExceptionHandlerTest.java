package br.edu.fatecpg.projetospring.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @SuppressWarnings("unused")
    private void metodoQualquer(Object body) {}

    private MethodArgumentNotValidException excecaoCom(ObjectError... erros) throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        for (ObjectError erro : erros) {
            bindingResult.addError(erro);
        }
        MethodParameter parametro = new MethodParameter(
                getClass().getDeclaredMethod("metodoQualquer", Object.class), 0);
        return new MethodArgumentNotValidException(parametro, bindingResult);
    }

    @Test
    @DisplayName("Erro de validação que não é de campo deve retornar 400, e não 500")
    void erroGlobalDeValidacaoDeveRetornar400() throws Exception {
        MethodArgumentNotValidException ex = excecaoCom(
                new ObjectError("request", "Período inválido"),
                new FieldError("request", "nome", "Nome é obrigatório"));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resposta = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertEquals("Período inválido", resposta.getBody().getDetails().get("request"));
        assertEquals("Nome é obrigatório", resposta.getBody().getDetails().get("nome"));
    }
}
