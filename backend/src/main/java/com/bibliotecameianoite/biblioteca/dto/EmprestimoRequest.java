package com.bibliotecameianoite.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Dados necessários para realizar o empréstimo de um livro.
 */
public record EmprestimoRequest(
        @NotNull(message = "O usuário é obrigatório") Long usuarioId,
        @NotNull(message = "O livro é obrigatório") Long livroId) {
}
