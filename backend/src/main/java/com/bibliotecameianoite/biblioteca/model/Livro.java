package com.bibliotecameianoite.biblioteca.model;

import java.time.LocalDateTime;
import java.util.List;

import com.bibliotecameianoite.biblioteca.model.enums.Genero;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "livros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título do livro é obrigatório")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotNull(message = "O autor do livro é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;

    @NotNull(message = "O gênero do livro é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Genero genero;

    @NotBlank(message = "O ISBN é obrigatório")
    @Pattern(
            regexp = "^(97[89]-?\\d{1,5}-?\\d{1,7}-?\\d{1,7}-?\\d|\\d{9}[\\dXx])$",
            message = "O ISBN informado está em um formato inválido")
    @Column(nullable = false, length = 20, unique = true)
    private String isbn;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @NotNull(message = "A quantidade total é obrigatória")
    @Min(value = 0, message = "A quantidade total não pode ser negativa")
    @Column(name = "quantidade_total", nullable = false)
    private Integer quantidadeTotal;

    @Min(value = 0, message = "A quantidade disponível não pode ser negativa")
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Emprestimo> emprestimos;

    @jakarta.persistence.PrePersist
    protected void aoCadastrar() {
        this.dataCadastro = LocalDateTime.now();
        if (this.quantidadeDisponivel == null) {
            this.quantidadeDisponivel = this.quantidadeTotal;
        }
    }
}
