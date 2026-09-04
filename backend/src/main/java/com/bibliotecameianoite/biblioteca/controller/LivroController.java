package com.bibliotecameianoite.biblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.service.LivroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsável por expor os endpoints REST relacionados aos
 * livros. Atua apenas como porta de entrada entre o Angular e o backend,
 * delegando toda a regra de negócio para o LivroService.
 */
@RestController
@RequestMapping("/livros")
@Tag(name = "Livros", description = "Operações de cadastro, consulta, atualização e exclusão de livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    private static final String EXEMPLO_LIVRO_REQUEST = "{"
            + "\"titulo\": \"Dom Casmurro\","
            + "\"autor\": { \"id\": 1 },"
            + "\"genero\": \"ROMANCE\","
            + "\"isbn\": \"978-85-359-0277-5\","
            + "\"anoPublicacao\": 1899,"
            + "\"quantidadeTotal\": 5,"
            + "\"quantidadeDisponivel\": 5"
            + "}";

    private static final String EXEMPLO_LIVRO_RESPONSE = "{"
            + "\"id\": 10,"
            + "\"titulo\": \"Dom Casmurro\","
            + "\"autor\": {"
            + "  \"id\": 1,"
            + "  \"nome\": \"Machado de Assis\","
            + "  \"nacionalidade\": \"Brasileira\","
            + "  \"dataNascimento\": \"1839-06-21\""
            + "},"
            + "\"genero\": \"ROMANCE\","
            + "\"isbn\": \"978-85-359-0277-5\","
            + "\"anoPublicacao\": 1899,"
            + "\"quantidadeTotal\": 5,"
            + "\"quantidadeDisponivel\": 5,"
            + "\"dataCadastro\": \"2026-09-04T02:00:00\""
            + "}";

    private static final String EXEMPLO_LIVRO_LISTA_RESPONSE = "["
            + EXEMPLO_LIVRO_RESPONSE + ","
            + "{"
            + "\"id\": 11,"
            + "\"titulo\": \"A Hora da Estrela\","
            + "\"autor\": {"
            + "  \"id\": 2,"
            + "  \"nome\": \"Clarice Lispector\","
            + "  \"nacionalidade\": \"Brasileira\","
            + "  \"dataNascimento\": \"1920-12-10\""
            + "},"
            + "\"genero\": \"FICCAO_CIENTIFICA\","
            + "\"isbn\": \"978-85-325-2942-3\","
            + "\"anoPublicacao\": 1977,"
            + "\"quantidadeTotal\": 3,"
            + "\"quantidadeDisponivel\": 2,"
            + "\"dataCadastro\": \"2026-09-04T02:05:00\""
            + "}"
            + "]";

    /**
     * Cadastra um novo livro.
     */
    @Operation(summary = "Cadastrar livro", description = "Cadastra um novo livro na biblioteca")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = Livro.class),
                    examples = @ExampleObject(name = "Exemplo de cadastro", value = EXEMPLO_LIVRO_REQUEST)))
    @ApiResponse(
            responseCode = "201",
            description = "Livro cadastrado com sucesso",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_LIVRO_RESPONSE)))
    @PostMapping
    public ResponseEntity<Livro> cadastrar(@RequestBody Livro livro) {
        Livro livroCadastrado = livroService.cadastrar(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(livroCadastrado);
    }

    /**
     * Lista todos os livros cadastrados.
     */
    @Operation(summary = "Listar livros", description = "Retorna a lista de todos os livros cadastrados")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de livros retornada com sucesso",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_LIVRO_LISTA_RESPONSE)))
    @GetMapping
    public ResponseEntity<List<Livro>> listar() {
        return ResponseEntity.ok(livroService.listar());
    }

    /**
     * Busca um livro pelo id.
     */
    @Operation(summary = "Buscar livro", description = "Busca um livro cadastrado pelo seu id")
    @ApiResponse(
            responseCode = "200",
            description = "Livro encontrado",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_LIVRO_RESPONSE)))
    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    /**
     * Atualiza os dados de um livro já cadastrado.
     */
    @Operation(summary = "Atualizar livro", description = "Atualiza os dados de um livro já cadastrado")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = Livro.class),
                    examples = @ExampleObject(name = "Exemplo de atualização", value = EXEMPLO_LIVRO_REQUEST)))
    @ApiResponse(
            responseCode = "200",
            description = "Livro atualizado com sucesso",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_LIVRO_RESPONSE)))
    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @RequestBody Livro livro) {
        return ResponseEntity.ok(livroService.atualizar(id, livro));
    }

    /**
     * Exclui um livro pelo id.
     */
    @Operation(summary = "Excluir livro", description = "Exclui um livro cadastrado pelo seu id")
    @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
