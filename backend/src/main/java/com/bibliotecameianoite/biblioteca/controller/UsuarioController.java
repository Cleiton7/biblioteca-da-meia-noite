package com.bibliotecameianoite.biblioteca.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotecameianoite.biblioteca.model.Usuario;
import com.bibliotecameianoite.biblioteca.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller responsável por expor os endpoints REST relacionados aos
 * usuários. Utilizado principalmente para popular a seleção de usuário
 * na tela de empréstimos do frontend.
 */
@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Operações de consulta de usuários cadastrados")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private static final String EXEMPLO_USUARIO_RESPONSE = "{"
            + "\"id\": 1,"
            + "\"nome\": \"Ana Silva\","
            + "\"email\": \"ana.silva@email.com\","
            + "\"telefone\": \"(11) 91234-5678\","
            + "\"dataCadastro\": \"2026-09-04T02:00:00\""
            + "}";

    private static final String EXEMPLO_USUARIO_LISTA_RESPONSE = "["
            + EXEMPLO_USUARIO_RESPONSE + ","
            + "{"
            + "\"id\": 2,"
            + "\"nome\": \"Bruno Costa\","
            + "\"email\": \"bruno.costa@email.com\","
            + "\"telefone\": \"(11) 92345-6789\","
            + "\"dataCadastro\": \"2026-09-04T02:05:00\""
            + "}"
            + "]";

    /**
     * Lista todos os usuários cadastrados.
     */
    @Operation(summary = "Listar usuários", description = "Retorna a lista de todos os usuários cadastrados")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de usuários retornada com sucesso",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_USUARIO_LISTA_RESPONSE)))
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    /**
     * Busca um usuário pelo id.
     */
    @Operation(summary = "Buscar usuário", description = "Busca um usuário cadastrado pelo seu id")
    @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado",
            content = @Content(examples = @ExampleObject(value = EXEMPLO_USUARIO_RESPONSE)))
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
}
