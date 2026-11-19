package com.bibliotecameianoite.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecameianoite.biblioteca.dto.EmprestimoRequest;
import com.bibliotecameianoite.biblioteca.dto.EmprestimoResponse;
import com.bibliotecameianoite.biblioteca.model.Emprestimo;
import com.bibliotecameianoite.biblioteca.service.EmprestimoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsável por expor os endpoints REST relacionados aos
 * empréstimos de livros. Atua apenas como porta de entrada, delegando
 * toda a regra de negócio para o EmprestimoService.
 */
@RestController
@RequestMapping("/emprestimos")
@Tag(name = "Empréstimos", description = "Operações de empréstimo de livros")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    private static final String EXEMPLO_EMPRESTIMO_REQUEST = "{"
            + "\"usuarioId\": 1,"
            + "\"livroId\": 3"
            + "}";

    private static final String EXEMPLO_EMPRESTIMO_RESPONSE = "{"
            + "\"id\": 9,"
            + "\"usuarioId\": 1,"
            + "\"usuarioNome\": \"Ana Silva\","
            + "\"livroId\": 3,"
            + "\"livroTitulo\": \"Dom Casmurro\","
            + "\"dataEmprestimo\": \"2026-11-19\","
            + "\"dataPrevistaDevolucao\": \"2026-12-03\","
            + "\"status\": \"ATIVO\","
            + "\"quantidadeRenovacoes\": 0"
            + "}";

    /**
     * Realiza o empréstimo de um livro para um usuário.
     */
    @Operation(
            summary = "Realizar empréstimo",
            description = "Realiza o empréstimo de um livro para um usuário, com devolução prevista em 14 dias. "
                    + "Falha se o livro não tiver exemplares disponíveis, se o usuário tiver empréstimo em atraso "
                    + "ou se já tiver 3 empréstimos ativos.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = EmprestimoRequest.class),
                    examples = @ExampleObject(name = "Exemplo de empréstimo", value = EXEMPLO_EMPRESTIMO_REQUEST)))
    @ApiResponse(
            responseCode = "201",
            description = "Empréstimo realizado com sucesso",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_EMPRESTIMO_RESPONSE)))
    @PostMapping
    public ResponseEntity<EmprestimoResponse> realizar(@jakarta.validation.Valid @RequestBody EmprestimoRequest request) {
        Emprestimo emprestimo = emprestimoService.realizar(request.usuarioId(), request.livroId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EmprestimoResponse.de(emprestimo));
    }
}
