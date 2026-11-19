package com.bibliotecameianoite.biblioteca.dto;

import java.time.LocalDate;

import com.bibliotecameianoite.biblioteca.model.Emprestimo;
import com.bibliotecameianoite.biblioteca.model.enums.StatusEmprestimo;

/**
 * Representação de um empréstimo retornada pela API, sem expor as
 * entidades relacionadas (livro e usuário) por completo.
 */
public record EmprestimoResponse(
        Long id,
        Long usuarioId,
        String usuarioNome,
        Long livroId,
        String livroTitulo,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        StatusEmprestimo status,
        Integer quantidadeRenovacoes) {

    public static EmprestimoResponse de(Emprestimo emprestimo) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getUsuario().getId(),
                emprestimo.getUsuario().getNome(),
                emprestimo.getLivro().getId(),
                emprestimo.getLivro().getTitulo(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataPrevistaDevolucao(),
                emprestimo.getStatus(),
                emprestimo.getQuantidadeRenovacoes());
    }
}
