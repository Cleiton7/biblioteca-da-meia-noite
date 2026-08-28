package com.bibliotecameianoite.biblioteca.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.bibliotecameianoite.biblioteca.model.Autor;
import com.bibliotecameianoite.biblioteca.repository.AutorRepository;

/**
 * Camada de serviço responsável pelas regras de negócio relacionadas aos
 * autores, conectando o AutorController ao AutorRepository.
 */
@Service
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    /**
     * Lista todos os autores cadastrados.
     */
    public List<Autor> listar() {
        return autorRepository.findAll();
    }

    /**
     * Busca um autor pelo id, lançando exceção caso não seja encontrado.
     */
    public Autor buscarPorId(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Autor não encontrado com o id: " + id));
    }
}
