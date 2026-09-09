package com.bibliotecameianoite.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotecameianoite.biblioteca.model.Autor;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
}
