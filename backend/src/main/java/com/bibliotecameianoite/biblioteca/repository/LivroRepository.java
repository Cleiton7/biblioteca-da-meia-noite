package com.bibliotecameianoite.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.enums.Genero;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByIsbn(String isbn);

    List<Livro> findByGenero(Genero genero);

    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    List<Livro> findByAutorId(Long autorId);
}
