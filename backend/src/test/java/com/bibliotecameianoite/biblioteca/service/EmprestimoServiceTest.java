package com.bibliotecameianoite.biblioteca.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import com.bibliotecameianoite.biblioteca.model.Emprestimo;
import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.Usuario;
import com.bibliotecameianoite.biblioteca.model.enums.StatusEmprestimo;
import com.bibliotecameianoite.biblioteca.repository.EmprestimoRepository;
import com.bibliotecameianoite.biblioteca.repository.LivroRepository;
import com.bibliotecameianoite.biblioteca.repository.UsuarioRepository;

/**
 * Testes unitários da funcionalidade de Realizar Empréstimo de Livro,
 * focados no método {@link EmprestimoService#realizar(Long, Long)}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmprestimoService - Realizar Empréstimo")
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private EmprestimoService emprestimoService;

    private Usuario usuario;
    private Livro livro;

    @BeforeEach
    void setUp() {
        emprestimoService = new EmprestimoService(emprestimoRepository, livroRepository, usuarioRepository);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana Silva");

        livro = new Livro();
        livro.setId(3L);
        livro.setTitulo("Dom Casmurro");
        livro.setQuantidadeTotal(5);
        livro.setQuantidadeDisponivel(5);
    }

    @Test
    @DisplayName("Deve realizar o empréstimo com sucesso, com status ATIVO e devolução em 14 dias")
    void deveRealizarEmprestimoComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(3L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByUsuarioIdAndStatus(1L, StatusEmprestimo.ATRASADO)).thenReturn(false);
        when(emprestimoRepository.countByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(0L);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(inv -> inv.getArgument(0));

        Emprestimo emprestimo = emprestimoService.realizar(1L, 3L);

        assertThat(emprestimo.getUsuario()).isSameAs(usuario);
        assertThat(emprestimo.getLivro()).isSameAs(livro);
        assertThat(emprestimo.getStatus()).isEqualTo(StatusEmprestimo.ATIVO);
        assertThat(emprestimo.getQuantidadeRenovacoes()).isZero();
        assertThat(emprestimo.getDataPrevistaDevolucao())
                .isEqualTo(LocalDate.now().plusDays(EmprestimoService.PRAZO_DEVOLUCAO_DIAS));
    }

    @Test
    @DisplayName("Deve decrementar a quantidade disponível do livro ao realizar o empréstimo")
    void deveDecrementarQuantidadeDisponivel() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(3L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(inv -> inv.getArgument(0));

        emprestimoService.realizar(1L, 3L);

        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(4);
        verify(livroRepository).save(livro);
    }

    @Test
    @DisplayName("Deve permitir emprestar o último exemplar disponível")
    void devePermitirEmprestarUltimoExemplar() {
        livro.setQuantidadeDisponivel(1);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(3L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(inv -> inv.getArgument(0));

        emprestimoService.realizar(1L, 3L);

        assertThat(livro.getQuantidadeDisponivel()).isZero();
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emprestimoService.realizar(99L, 3L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuário não encontrado com o id: 99");

        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o livro não existe")
    void deveLancarExcecaoQuandoLivroNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emprestimoService.realizar(1L, 99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Livro não encontrado com o id: 99");

        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o livro não possui exemplares disponíveis")
    void deveLancarExcecaoQuandoSemExemplaresDisponiveis() {
        livro.setQuantidadeDisponivel(0);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(3L)).thenReturn(Optional.of(livro));

        assertThatThrownBy(() -> emprestimoService.realizar(1L, 3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("não possui exemplares disponíveis");

        verify(livroRepository, never()).save(any());
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário possui empréstimo em atraso")
    void deveLancarExcecaoQuandoUsuarioEmAtraso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(3L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByUsuarioIdAndStatus(1L, StatusEmprestimo.ATRASADO)).thenReturn(true);

        assertThatThrownBy(() -> emprestimoService.realizar(1L, 3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("empréstimo em atraso");

        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(5);
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário atingiu o limite de empréstimos ativos")
    void deveLancarExcecaoQuandoLimiteExcedido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(3L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByUsuarioIdAndStatus(1L, StatusEmprestimo.ATRASADO)).thenReturn(false);
        when(emprestimoRepository.countByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                .thenReturn((long) EmprestimoService.LIMITE_EMPRESTIMOS_ATIVOS);

        assertThatThrownBy(() -> emprestimoService.realizar(1L, 3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("limite de " + EmprestimoService.LIMITE_EMPRESTIMOS_ATIVOS);

        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(5);
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir o empréstimo quando o usuário está abaixo do limite de ativos")
    void devePermitirQuandoAbaixoDoLimite() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(livroRepository.findById(3L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByUsuarioIdAndStatus(1L, StatusEmprestimo.ATRASADO)).thenReturn(false);
        when(emprestimoRepository.countByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO))
                .thenReturn((long) EmprestimoService.LIMITE_EMPRESTIMOS_ATIVOS - 1);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(inv -> inv.getArgument(0));

        Emprestimo emprestimo = emprestimoService.realizar(1L, 3L);

        assertThat(emprestimo).isNotNull();
        verify(emprestimoRepository).save(any(Emprestimo.class));
    }
}
