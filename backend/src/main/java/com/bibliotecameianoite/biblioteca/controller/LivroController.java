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

    /**
     * Cadastra um novo livro.
     */
    @Operation(summary = "Cadastrar livro", description = "Cadastra um novo livro na biblioteca")
    @PostMapping
    public ResponseEntity<Livro> cadastrar(@RequestBody Livro livro) {
        Livro livroCadastrado = livroService.cadastrar(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(livroCadastrado);
    }

    /**
     * Lista todos os livros cadastrados.
     */
    @Operation(summary = "Listar livros", description = "Retorna a lista de todos os livros cadastrados")
    @GetMapping
    public ResponseEntity<List<Livro>> listar() {
        return ResponseEntity.ok(livroService.listar());
    }

    /**
     * Busca um livro pelo id.
     */
    @Operation(summary = "Buscar livro", description = "Busca um livro cadastrado pelo seu id")
    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    /**
     * Atualiza os dados de um livro já cadastrado.
     */
    @Operation(summary = "Atualizar livro", description = "Atualiza os dados de um livro já cadastrado")
    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @RequestBody Livro livro) {
        return ResponseEntity.ok(livroService.atualizar(id, livro));
    }

    /**
     * Exclui um livro pelo id.
     */
    @Operation(summary = "Excluir livro", description = "Exclui um livro cadastrado pelo seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
