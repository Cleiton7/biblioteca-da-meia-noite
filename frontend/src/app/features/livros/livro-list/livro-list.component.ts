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
 * em uma tabela do Angular Material, com busca de livro por id.
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

  /** Valor digitado no campo "Buscar por ID". */
  idBusca = signal('');

  /** Id da busca em andamento; null quando a listagem completa é exibida. */
  buscaAtiva = signal<number | null>(null);

  colunas = ['titulo', 'autor', 'genero', 'isbn', 'anoPublicacao', 'disponibilidade', 'acoes'];

  constructor(
    private readonly livroService: LivroService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar
  ) {
    this.carregarLivros();
  }

  ngOnChanges(): void {
    // Um novo cadastro invalida qualquer busca em andamento, para que o
    // livro recém-criado não fique escondido atrás de um filtro antigo.
    this.limparBusca();
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

  atualizarIdBusca(valor: string): void {
    this.idBusca.set(valor);
  }

  /**
   * Busca um livro pelo id informado e exibe o resultado na tabela.
   */
  buscarPorId(): void {
    const texto = this.idBusca().trim();
    const id = Number(texto);

    if (!texto || !Number.isInteger(id) || id <= 0) {
      this.buscaAtiva.set(null);
      this.mensagemErro.set('Informe um ID válido (número inteiro positivo).');
      return;
    }

    this.carregando.set(true);
    this.mensagemErro.set('');

    this.buscaAtiva.set(null);
    this.livroService.buscarPorId(id).subscribe({
      next: (livro) => {
        this.livros.set([livro]);
        this.buscaAtiva.set(id);
        this.carregando.set(false);
      },
      error: (erro) => {
        this.mensagemErro.set(
          erro?.status === 404
            ? (erro?.error?.message ?? `Livro não encontrado com o id: ${id}`)
            : 'Não foi possível buscar o livro informado.'
        );
        this.carregando.set(false);
      }
    });
  }

  /**
   * Limpa o campo e a busca ativa, voltando a exibir a listagem completa.
   */
  limparBusca(): void {
    this.idBusca.set('');
    this.buscaAtiva.set(null);
    this.carregarLivros();
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
        this.limparBusca();
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
