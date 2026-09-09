package com.bibliotecameianoite.biblioteca.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bibliotecameianoite.biblioteca.model.Autor;
import com.bibliotecameianoite.biblioteca.model.Emprestimo;
import com.bibliotecameianoite.biblioteca.model.Livro;
import com.bibliotecameianoite.biblioteca.model.Usuario;
import com.bibliotecameianoite.biblioteca.model.enums.Genero;
import com.bibliotecameianoite.biblioteca.model.enums.StatusEmprestimo;
import com.bibliotecameianoite.biblioteca.repository.AutorRepository;
import com.bibliotecameianoite.biblioteca.repository.EmprestimoRepository;
import com.bibliotecameianoite.biblioteca.repository.LivroRepository;
import com.bibliotecameianoite.biblioteca.repository.UsuarioRepository;

/**
 * Popula o banco de dados com dados iniciais para testes,
 * caso ainda não existam registros nas tabelas principais.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;

    public DataLoader(AutorRepository autorRepository,
            LivroRepository livroRepository,
            UsuarioRepository usuarioRepository,
            EmprestimoRepository emprestimoRepository) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Override
    public void run(String... args) {
        if (autorRepository.count() > 0) {
            // Dados já existem, evita duplicação em reinicializações.
            return;
        }

        // ===================== AUTORES =====================
        Autor jorgeAmado = new Autor(null, "Jorge Amado", "Brasileira", LocalDate.of(1912, 8, 10), null);
        Autor machadoDeAssis = new Autor(null, "Machado de Assis", "Brasileira", LocalDate.of(1839, 6, 21), null);
        Autor clariceLispector = new Autor(null, "Clarice Lispector", "Brasileira", LocalDate.of(1920, 12, 10), null);
        Autor georgeOrwell = new Autor(null, "George Orwell", "Britânica", LocalDate.of(1903, 6, 25), null);
        Autor agathaChristie = new Autor(null, "Agatha Christie", "Britânica", LocalDate.of(1890, 9, 15), null);

        List<Autor> autores = autorRepository
                .saveAll(List.of(jorgeAmado, machadoDeAssis, clariceLispector, georgeOrwell, agathaChristie));

        Autor a1 = autores.get(0);
        Autor a2 = autores.get(1);
        Autor a3 = autores.get(2);
        Autor a4 = autores.get(3);
        Autor a5 = autores.get(4);

        // ===================== LIVROS =====================
        Livro livro1 = new Livro(null, "Gabriela, Cravo e Canela", a1, Genero.ROMANCE, "978-8535914849", 1958, 5, 5,
                null, null);
        Livro livro2 = new Livro(null, "Capitães da Areia", a1, Genero.ROMANCE, "978-8535914856", 1937, 4, 4, null,
                null);
        Livro livro3 = new Livro(null, "Dom Casmurro", a2, Genero.ROMANCE, "978-8508162013", 1899, 6, 6, null, null);
        Livro livro4 = new Livro(null, "Memórias Póstumas de Brás Cubas", a2, Genero.ROMANCE, "978-8508162020", 1881,
                3, 3, null, null);
        Livro livro5 = new Livro(null, "A Hora da Estrela", a3, Genero.ROMANCE, "978-8532508365", 1977, 4, 4, null,
                null);
        Livro livro6 = new Livro(null, "Perto do Coração Salvagem", a3, Genero.ROMANCE, "978-8532508372", 1943, 2, 2,
                null, null);
        Livro livro7 = new Livro(null, "1984", a4, Genero.FICCAO_CIENTIFICA, "978-8535914818", 1949, 7, 7, null,
                null);
        Livro livro8 = new Livro(null, "A Revolução dos Bichos", a4, Genero.FICCAO_CIENTIFICA, "978-8535914825", 1945,
                5, 5, null, null);
        Livro livro9 = new Livro(null, "Assassinato no Expresso Oriente", a5, Genero.SUSPENSE, "978-8525056397", 1934,
                4, 4, null, null);
        Livro livro10 = new Livro(null, "E Não Sobrou Nenhum", a5, Genero.SUSPENSE, "978-8525056403", 1939, 3, 3,
                null, null);

        List<Livro> livros = livroRepository.saveAll(List.of(livro1, livro2, livro3, livro4, livro5, livro6, livro7,
                livro8, livro9, livro10));

        // ===================== USUÁRIOS =====================
        Usuario usuario1 = new Usuario(null, "Ana Silva", "ana.silva@email.com", "(11) 91234-5678", null, null);
        Usuario usuario2 = new Usuario(null, "Bruno Costa", "bruno.costa@email.com", "(11) 92345-6789", null, null);
        Usuario usuario3 = new Usuario(null, "Carla Souza", "carla.souza@email.com", "(21) 93456-7890", null, null);
        Usuario usuario4 = new Usuario(null, "Diego Ferreira", "diego.ferreira@email.com", "(31) 94567-8901", null,
                null);
        Usuario usuario5 = new Usuario(null, "Elisa Martins", "elisa.martins@email.com", "(41) 95678-9012", null,
                null);

        List<Usuario> usuarios = usuarioRepository
                .saveAll(List.of(usuario1, usuario2, usuario3, usuario4, usuario5));

        // ===================== EMPRÉSTIMOS =====================
        // Empréstimo 1: ativo, dentro do prazo
        Emprestimo emp1 = new Emprestimo(null, livros.get(0), usuarios.get(0), LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(9), null, StatusEmprestimo.ATIVO, 0);

        // Empréstimo 2: ativo, dentro do prazo
        Emprestimo emp2 = new Emprestimo(null, livros.get(2), usuarios.get(1), LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(12), null, StatusEmprestimo.ATIVO, 0);

        // Empréstimo 3: devolvido dentro do prazo
        Emprestimo emp3 = new Emprestimo(null, livros.get(4), usuarios.get(2), LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(6), LocalDate.now().minusDays(7), StatusEmprestimo.DEVOLVIDO, 0);

        // Empréstimo 4: devolvido com renovação
        Emprestimo emp4 = new Emprestimo(null, livros.get(6), usuarios.get(3), LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(2), LocalDate.now().minusDays(3), StatusEmprestimo.DEVOLVIDO, 1);

        // Empréstimo 5: atrasado, ainda não devolvido
        Emprestimo emp5 = new Emprestimo(null, livros.get(8), usuarios.get(4), LocalDate.now().minusDays(25),
                LocalDate.now().minusDays(11), null, StatusEmprestimo.ATRASADO, 0);

        // Empréstimo 6: atrasado, ainda não devolvido
        Emprestimo emp6 = new Emprestimo(null, livros.get(1), usuarios.get(0), LocalDate.now().minusDays(18),
                LocalDate.now().minusDays(4), null, StatusEmprestimo.ATRASADO, 0);

        // Empréstimo 7: ativo, dentro do prazo, com renovação
        Emprestimo emp7 = new Emprestimo(null, livros.get(3), usuarios.get(1), LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(4), null, StatusEmprestimo.ATIVO, 1);

        // Empréstimo 8: devolvido dentro do prazo
        Emprestimo emp8 = new Emprestimo(null, livros.get(9), usuarios.get(2), LocalDate.now().minusDays(15),
                LocalDate.now().minusDays(1), LocalDate.now().minusDays(2), StatusEmprestimo.DEVOLVIDO, 0);

        emprestimoRepository.saveAll(List.of(emp1, emp2, emp3, emp4, emp5, emp6, emp7, emp8));

        // Ajusta a quantidade disponível dos livros com empréstimos ativos/atrasados
        ajustarQuantidadeDisponivel(livros.get(0), 1);
        ajustarQuantidadeDisponivel(livros.get(2), 1);
        ajustarQuantidadeDisponivel(livros.get(6), 1);
        ajustarQuantidadeDisponivel(livros.get(8), 1);
        ajustarQuantidadeDisponivel(livros.get(1), 1);
        ajustarQuantidadeDisponivel(livros.get(3), 1);
    }

    private void ajustarQuantidadeDisponivel(Livro livro, int quantidadeEmprestada) {
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - quantidadeEmprestada);
        livroRepository.save(livro);
    }
}
