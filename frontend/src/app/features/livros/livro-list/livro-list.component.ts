import { Component, Input, OnChanges, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../../shared/material.module';
import { LivroService } from '../../../services/livro.service';
import { Livro } from '../../../models/livro.model';
import { Genero, GENERO_LABELS } from '../../../models/genero.enum';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

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

  constructor(
    private readonly livroService: LivroService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {
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

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Excluir livro',
        mensagem: `Tem certeza que deseja excluir o livro "${livro.titulo}"? Essa ação não pode ser desfeita.`,
        textoConfirmar: 'Excluir'
      }
    });

    dialogRef.afterClosed().subscribe((confirmado) => {
      if (confirmado) {
        this.confirmarExclusao(livro);
      }
    });
  }

  private confirmarExclusao(livro: Livro): void {
    this.mensagemErro.set('');

    this.livroService.deletar(livro.id!).subscribe({
      next: () => {
        this.carregarLivros();
        this.snackBar.open(`Livro "${livro.titulo}" excluído com sucesso.`, 'Fechar', {
          duration: 4000
        });
      },
      error: (erro) => {
        const mensagem = erro?.error?.message ?? 'Não foi possível excluir o livro selecionado.';
        this.mensagemErro.set(mensagem);
        this.snackBar.open(mensagem, 'Fechar', { duration: 5000 });
      }
    });
  }

  labelGenero(genero: Genero): string {
    return GENERO_LABELS[genero];
  }
}
