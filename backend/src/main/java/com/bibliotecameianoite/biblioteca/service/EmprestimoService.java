package com.bibliotecameianoite.biblioteca.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotecameianoite.biblioteca.model.Emprestimo;
import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.Usuario;
import com.bibliotecameianoite.biblioteca.model.enums.StatusEmprestimo;
import com.bibliotecameianoite.biblioteca.repository.EmprestimoRepository;
import com.bibliotecameianoite.biblioteca.repository.LivroRepository;
import com.bibliotecameianoite.biblioteca.repository.UsuarioRepository;

/**
 * Camada de serviço responsável pelas regras de negócio relacionadas aos
 * empréstimos de livros, conectando o EmprestimoController aos repositórios.
 */
@Service
public class EmprestimoService {

    /** Prazo padrão, em dias, para a devolução de um livro emprestado. */
    public static final int PRAZO_DEVOLUCAO_DIAS = 14;

    /** Quantidade máxima de empréstimos ativos simultâneos por usuário. */
    public static final int LIMITE_EMPRESTIMOS_ATIVOS = 3;

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository,
            UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Realiza o empréstimo de um livro para um usuário, validando as regras
     * de negócio e decrementando a quantidade disponível do livro na mesma
     * transação.
     */
    @Transactional
    public Emprestimo realizar(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o id: " + usuarioId));
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new NoSuchElementException("Livro não encontrado com o id: " + livroId));

        validarDisponibilidade(livro);
        validarSituacaoDoUsuario(usuario);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(PRAZO_DEVOLUCAO_DIAS));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setQuantidadeRenovacoes(0);

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);

        return emprestimoRepository.save(emprestimo);
    }

    private void validarDisponibilidade(Livro livro) {
        if (livro.getQuantidadeDisponivel() == null || livro.getQuantidadeDisponivel() <= 0) {
            throw new IllegalStateException("O livro \"" + livro.getTitulo() + "\" não possui exemplares disponíveis");
        }
    }

    private void validarSituacaoDoUsuario(Usuario usuario) {
        if (emprestimoRepository.existsByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.ATRASADO)) {
            throw new IllegalStateException(
                    "O usuário " + usuario.getNome() + " possui empréstimo em atraso e não pode realizar novos empréstimos");
        }
        long ativos = emprestimoRepository.countByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.ATIVO);
        if (ativos >= LIMITE_EMPRESTIMOS_ATIVOS) {
            throw new IllegalStateException("O usuário " + usuario.getNome() + " atingiu o limite de "
                    + LIMITE_EMPRESTIMOS_ATIVOS + " empréstimos ativos");
        }
    }
}
