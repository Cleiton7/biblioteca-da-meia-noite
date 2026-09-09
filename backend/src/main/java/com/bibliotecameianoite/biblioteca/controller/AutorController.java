package com.bibliotecameianoite.biblioteca.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecameianoite.biblioteca.model.Autor;
import com.bibliotecameianoite.biblioteca.service.AutorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsável por expor os endpoints REST relacionados aos
 * autores. Utilizado principalmente para popular seleções de autor nas
 * telas de cadastro de livros do frontend.
 */
@RestController
@RequestMapping("/autores")
@Tag(name = "Autores", description = "Operações de consulta de autores cadastrados")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    private static final String EXEMPLO_AUTOR_RESPONSE = "{"
            + "\"id\": 1,"
            + "\"nome\": \"Machado de Assis\","
            + "\"nacionalidade\": \"Brasileira\","
            + "\"dataNascimento\": \"1839-06-21\""
            + "}";

    private static final String EXEMPLO_AUTOR_LISTA_RESPONSE = "["
            + EXEMPLO_AUTOR_RESPONSE + ","
            + "{"
            + "\"id\": 2,"
            + "\"nome\": \"Clarice Lispector\","
            + "\"nacionalidade\": \"Brasileira\","
            + "\"dataNascimento\": \"1920-12-10\""
            + "}"
            + "]";

    /**
     * Lista todos os autores cadastrados.
     */
    @Operation(summary = "Listar autores", description = "Retorna a lista de todos os autores cadastrados")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de autores retornada com sucesso",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_AUTOR_LISTA_RESPONSE)))
    @GetMapping
    public ResponseEntity<List<Autor>> listar() {
        return ResponseEntity.ok(autorService.listar());
    }

    /**
     * Busca um autor pelo id.
     */
    @Operation(summary = "Buscar autor", description = "Busca um autor cadastrado pelo seu id")
    @ApiResponse(
            responseCode = "200",
            description = "Autor encontrado",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_AUTOR_RESPONSE)))
    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }
}
