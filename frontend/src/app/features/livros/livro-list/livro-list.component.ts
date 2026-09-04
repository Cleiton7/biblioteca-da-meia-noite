import { Component, Input, OnChanges, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../../shared/material.module';
import { LivroService } from '../../../services/livro.service';
import { Livro } from '../../../models/livro.model';
import { Genero, GENERO_LABELS } from '../../../models/genero.enum';

/**
 * Componente responsável por exibir a listagem de livros cadastrados
 * em uma tabela do Angular Material.
 */
@Component({
  selector: 'app-livro-list',
  standalone: true,
  imports: [CommonModule, MaterialModule],
  templateUrl: './livro-list.component.html',
  styleUrl: './livro-list.component.scss'
})
export class LivroListComponent implements OnChanges {
  /**
   * Alterar este valor (ex.: incrementando um contador) força o componente
   * a recarregar a listagem de livros, útil após um novo cadastro.
   */
  @Input() atualizarEm = 0;

  livros = signal<Livro[]>([]);
  carregando = signal(false);
  mensagemErro = signal('');

  colunas = ['titulo', 'autor', 'genero', 'isbn', 'anoPublicacao', 'disponibilidade', 'acoes'];

  constructor(private readonly livroService: LivroService) {
    this.carregarLivros();
  }

  ngOnChanges(): void {
    this.carregarLivros();
  }

  carregarLivros(): void {
    this.carregando.set(true);
    this.mensagemErro.set('');

    this.livroService.listar().subscribe({
      next: (livros) => {
        this.livros.set(livros);
        this.carregando.set(false);
      },
      error: () => {
        this.mensagemErro.set('Não foi possível carregar a lista de livros.');
        this.carregando.set(false);
      }
    });
  }

  excluir(livro: Livro): void {
    if (!livro.id) {
      return;
    }

    this.livroService.deletar(livro.id).subscribe({
      next: () => this.carregarLivros(),
      error: () => this.mensagemErro.set('Não foi possível excluir o livro selecionado.')
    });
  }

  labelGenero(genero: Genero): string {
    return GENERO_LABELS[genero];
  }
}
