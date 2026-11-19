package com.bibliotecameianoite.biblioteca.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecameianoite.biblioteca.model.Emprestimo;
import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.Usuario;
import com.bibliotecameianoite.biblioteca.model.enums.StatusEmprestimo;
import com.bibliotecameianoite.biblioteca.service.EmprestimoService;

/**
 * Testes unitários do endpoint de realização de empréstimo exposto pelo
 * {@link EmprestimoController} (POST /emprestimos). O {@link EmprestimoService}
 * é mockado para isolar o comportamento do controller.
 */
@WebMvcTest(EmprestimoController.class)
@DisplayName("EmprestimoController - Realizar Empréstimo")
class EmprestimoControllerTest {

    private static final String CORPO_VALIDO = "{\"usuarioId\": 1, \"livroId\": 3}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmprestimoService emprestimoService;

    private Emprestimo emprestimo;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana Silva");

        Livro livro = new Livro();
        livro.setId(3L);
        livro.setTitulo("Dom Casmurro");

        emprestimo = new Emprestimo();
        emprestimo.setId(9L);
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDate.of(2026, 11, 19));
        emprestimo.setDataPrevistaDevolucao(LocalDate.of(2026, 12, 3));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setQuantidadeRenovacoes(0);
    }

    @Test
    @DisplayName("Deve retornar 201 Created com os dados do empréstimo realizado")
    void deveRetornar201QuandoEmprestimoRealizado() throws Exception {
        when(emprestimoService.realizar(1L, 3L)).thenReturn(emprestimo);

        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.usuarioNome").value("Ana Silva"))
                .andExpect(jsonPath("$.livroTitulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.dataPrevistaDevolucao").value("2026-12-03"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o usuário não é informado")
    void deveRetornar400QuandoUsuarioNaoInformado() throws Exception {
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"livroId\": 3}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o livro não é informado")
    void deveRetornar400QuandoLivroNaoInformado() throws Exception {
        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando o usuário ou o livro não existe")
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        when(emprestimoService.realizar(anyLong(), anyLong()))
                .thenThrow(new NoSuchElementException("Livro não encontrado com o id: 3"));

        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPO_VALIDO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Livro não encontrado com o id: 3"));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando o livro não possui exemplares disponíveis")
    void deveRetornar409QuandoSemExemplares() throws Exception {
        when(emprestimoService.realizar(anyLong(), anyLong()))
                .thenThrow(new IllegalStateException("O livro \"Dom Casmurro\" não possui exemplares disponíveis"));

        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPO_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("O livro \"Dom Casmurro\" não possui exemplares disponíveis"));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando o usuário atingiu o limite de empréstimos ativos")
    void deveRetornar409QuandoLimiteExcedido() throws Exception {
        when(emprestimoService.realizar(anyLong(), anyLong()))
                .thenThrow(new IllegalStateException("O usuário Ana Silva atingiu o limite de 3 empréstimos ativos"));

        mockMvc.perform(post("/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPO_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("O usuário Ana Silva atingiu o limite de 3 empréstimos ativos"));
    }
}
