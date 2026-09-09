package com.bibliotecameianoite.biblioteca.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bibliotecameianoite.biblioteca.model.Autor;
import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.enums.Genero;
import com.bibliotecameianoite.biblioteca.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testes unitários do endpoint de cadastro de livros exposto pelo
 * {@link LivroController} (POST /livros). O {@link LivroService} é
 * mockado para isolar o comportamento do controller.
 */
@WebMvcTest(LivroController.class)
@DisplayName("LivroController - Cadastrar Livro")
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private LivroService livroService;

    private Livro livroRequisicao;
    private Livro livroCadastrado;

    @BeforeEach
    void setUp() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Machado de Assis");

        livroRequisicao = new Livro();
        livroRequisicao.setTitulo("Dom Casmurro");
        livroRequisicao.setAutor(autor);
        livroRequisicao.setGenero(Genero.ROMANCE);
        livroRequisicao.setIsbn("978-85-359-0277-5");
        livroRequisicao.setAnoPublicacao(1899);
        livroRequisicao.setQuantidadeTotal(5);

        livroCadastrado = new Livro();
        livroCadastrado.setId(10L);
        livroCadastrado.setTitulo("Dom Casmurro");
        livroCadastrado.setAutor(autor);
        livroCadastrado.setGenero(Genero.ROMANCE);
        livroCadastrado.setIsbn("978-85-359-0277-5");
        livroCadastrado.setAnoPublicacao(1899);
        livroCadastrado.setQuantidadeTotal(5);
        livroCadastrado.setQuantidadeDisponivel(5);
        livroCadastrado.setDataCadastro(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve retornar 201 Created quando o livro é cadastrado com sucesso")
    void deveRetornar201QuandoCadastradoComSucesso() throws Exception {
        when(livroService.cadastrar(any(Livro.class))).thenReturn(livroCadastrado);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequisicao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.isbn").value("978-85-359-0277-5"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o título não é informado")
    void deveRetornar400QuandoTituloNaoInformado() throws Exception {
        livroRequisicao.setTitulo(null);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequisicao)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o autor não é informado")
    void deveRetornar400QuandoAutorNaoInformado() throws Exception {
        livroRequisicao.setAutor(null);

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequisicao)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o ISBN informado já está cadastrado")
    void deveRetornar400QuandoIsbnDuplicado() throws Exception {
        when(livroService.cadastrar(any(Livro.class)))
                .thenThrow(new IllegalArgumentException("Já existe um livro cadastrado com o ISBN: "
                        + livroRequisicao.getIsbn()));

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequisicao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Já existe um livro cadastrado com o ISBN: " + livroRequisicao.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando o autor informado não existe")
    void deveRetornar404QuandoAutorNaoExiste() throws Exception {
        when(livroService.cadastrar(any(Livro.class)))
                .thenThrow(new NoSuchElementException("Autor não encontrado com o id: 1"));

        mockMvc.perform(post("/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(livroRequisicao)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Autor não encontrado com o id: 1"));
    }
}
