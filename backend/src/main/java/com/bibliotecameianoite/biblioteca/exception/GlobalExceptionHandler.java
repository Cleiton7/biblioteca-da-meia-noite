package com.bibliotecameianoite.biblioteca.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manipulador global de exceções, responsável por padronizar as respostas
 * de erro da API em formato JSON, garantindo que o frontend sempre receba
 * uma mensagem clara sobre o motivo da falha (ex: ISBN duplicado, campo
 * obrigatório não informado, recurso não encontrado, etc.).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata falhas de validação do Bean Validation (@Valid), como campos
     * obrigatórios não informados ou em formato inválido.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarErroDeValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage()));

        String mensagem = erros.values().stream().findFirst().orElse("Dados inválidos");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErro(HttpStatus.BAD_REQUEST, mensagem, erros));
    }

    /**
     * Trata erros de regra de negócio, como cadastro de ISBN duplicado ou
     * autor/id não informado.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErro(HttpStatus.BAD_REQUEST, ex.getMessage(), null));
    }

    /**
     * Trata recursos não encontrados (ex: livro ou autor com id inexistente).
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpoErro(HttpStatus.NOT_FOUND, ex.getMessage(), null));
    }

    /**
     * Trata conflitos de estado, como a exclusão de um livro que possui
     * empréstimos ativos.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> tratarConflitoDeEstado(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpoErro(HttpStatus.CONFLICT, ex.getMessage(), null));
    }

    private Map<String, Object> corpoErro(HttpStatus status, String mensagem, Map<String, String> erros) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("error", status.getReasonPhrase());
        corpo.put("message", mensagem);
        if (erros != null && !erros.isEmpty()) {
            corpo.put("errors", erros);
        }
        return corpo;
    }
}
