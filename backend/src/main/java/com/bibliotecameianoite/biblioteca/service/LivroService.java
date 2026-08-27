package com.bibliotecameianoite.biblioteca.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.bibliotecameianoite.biblioteca.model.Emprestimo;
import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.enums.StatusEmprestimo;
import com.bibliotecameianoite.biblioteca.repository.EmprestimoRepository;
import com.bibliotecameianoite.biblioteca.repository.LivroRepository;

/**
 * Camada de serviço responsável por concentrar as regras de negócio
 * relacionadas aos livros, conectando o LivroController ao LivroRepository.
 */
@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    public LivroService(LivroRepository livroRepository, EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    /**
     * Cadastra um novo livro, validando as regras de negócio antes de persistir.
     */
    public Livro cadastrar(Livro livro) {
        validarDadosObrigatorios(livro);
        validarIsbnUnico(livro.getIsbn(), null);
        return livroRepository.save(livro);
    }

    /**
     * Lista todos os livros cadastrados.
     */
    public List<Livro> listar() {
        return livroRepository.findAll();
    }

    /**
     * Busca um livro pelo id, lançando exceção caso não seja encontrado.
     */
    public Livro buscarPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Livro não encontrado com o id: " + id));
    }

    /**
     * Atualiza os dados de um livro já cadastrado.
     */
    public Livro atualizar(Long id, Livro livroAtualizado) {
        Livro livroExistente = buscarPorId(id);

        validarDadosObrigatorios(livroAtualizado);
        validarIsbnUnico(livroAtualizado.getIsbn(), id);

        livroExistente.setTitulo(livroAtualizado.getTitulo());
        livroExistente.setAutor(livroAtualizado.getAutor());
        livroExistente.setGenero(livroAtualizado.getGenero());
        livroExistente.setIsbn(livroAtualizado.getIsbn());
        livroExistente.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
        livroExistente.setQuantidadeTotal(livroAtualizado.getQuantidadeTotal());
        livroExistente.setQuantidadeDisponivel(livroAtualizado.getQuantidadeDisponivel());

        return livroRepository.save(livroExistente);
    }

    /**
     * Exclui um livro, impedindo a exclusão caso existam empréstimos ativos
     * vinculados a ele.
     */
    public void deletar(Long id) {
        Livro livro = buscarPorId(id);
        verificarSeLivroEstaEmprestado(livro.getId());
        livroRepository.deleteById(id);
    }

    private void validarDadosObrigatorios(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O título do livro é obrigatório");
        }
        if (livro.getAutor() == null) {
            throw new IllegalArgumentException("O autor do livro é obrigatório");
        }
    }

    private void validarIsbnUnico(String isbn, Long idAtual) {
        if (isbn == null || isbn.isBlank()) {
            return;
        }
        livroRepository.findByIsbn(isbn).ifPresent(livroExistente -> {
            if (idAtual == null || !livroExistente.getId().equals(idAtual)) {
                throw new IllegalArgumentException("Já existe um livro cadastrado com o ISBN: " + isbn);
            }
        });
    }

    private void verificarSeLivroEstaEmprestado(Long livroId) {
        boolean possuiEmprestimoAtivo = emprestimoRepository.findByLivroId(livroId).stream()
                .map(Emprestimo::getStatus)
                .anyMatch(status -> status == StatusEmprestimo.ATIVO);

        if (possuiEmprestimoAtivo) {
            throw new IllegalStateException("O livro não pode ser excluído pois possui empréstimos ativos");
        }
    }
}
