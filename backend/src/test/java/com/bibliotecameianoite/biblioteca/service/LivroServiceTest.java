package com.bibliotecameianoite.biblioteca.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bibliotecameianoite.biblioteca.model.Autor;
import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.enums.Genero;
import com.bibliotecameianoite.biblioteca.repository.AutorRepository;
import com.bibliotecameianoite.biblioteca.repository.EmprestimoRepository;
import com.bibliotecameianoite.biblioteca.repository.LivroRepository;

/**
 * Testes unitários da funcionalidade de Cadastrar Livros, focados no método
 * {@link LivroService#cadastrar(Livro)}, que concentra as regras de negócio
 * de validação e persistência.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LivroService - Cadastrar Livro")
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private AutorRepository autorRepository;

    private LivroService livroService;

    private Autor autor;

    @BeforeEach
    void setUp() {
        livroService = new LivroService(livroRepository, emprestimoRepository, autorRepository);

        autor = new Autor();
        autor.setId(1L);
        autor.setNome("Machado de Assis");
    }

    private Livro criarLivroValido() {
        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setAutor(new Autor());
        livro.getAutor().setId(1L);
        livro.setGenero(Genero.ROMANCE);
        livro.setIsbn("978-85-359-0277-5");
        livro.setAnoPublicacao(1899);
        livro.setQuantidadeTotal(5);
        return livro;
    }

    @Test
    @DisplayName("Deve cadastrar um livro com sucesso quando os dados são válidos")
    void deveCadastrarLivroComSucesso() {
        Livro livro = criarLivroValido();

        when(livroRepository.findByIsbn(livro.getIsbn())).thenReturn(Optional.empty());
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Livro livroCadastrado = livroService.cadastrar(livro);

        assertThat(livroCadastrado).isNotNull();
        assertThat(livroCadastrado.getTitulo()).isEqualTo("Dom Casmurro");
        assertThat(livroCadastrado.getAutor()).isEqualTo(autor);
        verify(livroRepository, times(1)).save(livro);
    }

    @Test
    @DisplayName("Deve associar o autor completo buscado no repositório ao livro cadastrado")
    void deveAssociarAutorCompletoAoCadastrar() {
        Livro livro = criarLivroValido();

        when(livroRepository.findByIsbn(livro.getIsbn())).thenReturn(Optional.empty());
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        livroService.cadastrar(livro);

        ArgumentCaptor<Livro> captor = ArgumentCaptor.forClass(Livro.class);
        verify(livroRepository).save(captor.capture());
        assertThat(captor.getValue().getAutor()).isSameAs(autor);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o título do livro é nulo")
    void deveLancarExcecaoQuandoTituloForNulo() {
        Livro livro = criarLivroValido();
        livro.setTitulo(null);

        assertThatThrownBy(() -> livroService.cadastrar(livro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("título");

        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o título do livro está em branco")
    void deveLancarExcecaoQuandoTituloEstiverEmBranco() {
        Livro livro = criarLivroValido();
        livro.setTitulo("   ");

        assertThatThrownBy(() -> livroService.cadastrar(livro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("título");

        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o autor do livro é nulo")
    void deveLancarExcecaoQuandoAutorForNulo() {
        Livro livro = criarLivroValido();
        livro.setAutor(null);

        assertThatThrownBy(() -> livroService.cadastrar(livro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("autor");

        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o autor informado não possui id")
    void deveLancarExcecaoQuandoAutorNaoPossuiId() {
        Livro livro = criarLivroValido();
        livro.getAutor().setId(null);

        assertThatThrownBy(() -> livroService.cadastrar(livro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("autor");

        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o autor informado não existe no banco de dados")
    void deveLancarExcecaoQuandoAutorNaoExiste() {
        Livro livro = criarLivroValido();

        when(livroRepository.findByIsbn(livro.getIsbn())).thenReturn(Optional.empty());
        when(autorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.cadastrar(livro))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Autor não encontrado");

        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ISBN informado já está cadastrado em outro livro")
    void deveLancarExcecaoQuandoIsbnJaCadastrado() {
        Livro livro = criarLivroValido();

        Livro livroExistente = new Livro();
        livroExistente.setId(99L);
        livroExistente.setIsbn(livro.getIsbn());

        when(livroRepository.findByIsbn(livro.getIsbn())).thenReturn(Optional.of(livroExistente));

        assertThatThrownBy(() -> livroService.cadastrar(livro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN");

        verify(livroRepository, never()).save(any());
        verify(autorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve permitir cadastro quando o ISBN não é informado")
    void devePermitirCadastroQuandoIsbnNaoInformado() {
        Livro livro = criarLivroValido();
        livro.setIsbn(null);

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(livroRepository.save(any(Livro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Livro livroCadastrado = livroService.cadastrar(livro);

        assertThat(livroCadastrado).isNotNull();
        verify(livroRepository, never()).findByIsbn(any());
        verify(livroRepository, times(1)).save(livro);
    }
}
