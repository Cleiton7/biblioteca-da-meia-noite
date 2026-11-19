package com.bibliotecameianoite.biblioteca.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.bibliotecameianoite.biblioteca.model.Usuario;
import com.bibliotecameianoite.biblioteca.repository.UsuarioRepository;

/**
 * Camada de serviço responsável pelas regras de negócio relacionadas aos
 * usuários, conectando o UsuarioController ao UsuarioRepository.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Lista todos os usuários cadastrados.
     */
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca um usuário pelo id, lançando exceção caso não seja encontrado.
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o id: " + id));
    }
}
