import { Component } from '@angular/core';
import { LivroFormComponent } from '../livro-form/livro-form.component';
import { LivroListComponent } from '../livro-list/livro-list.component';

/**
 * Página responsável por exibir o formulário de cadastro de livros
 * e a listagem de livros cadastrados.
 */
@Component({
  selector: 'app-livro-page',
  standalone: true,
  imports: [LivroFormComponent, LivroListComponent],
  templateUrl: './livro-page.component.html',
  styleUrl: './livro-page.component.scss'
})
export class LivroPageComponent {
  /**
   * Incrementado a cada novo cadastro para forçar a atualização
   * da listagem de livros.
   */
  atualizarListagemEm = 0;

  onLivroCadastrado(): void {
    this.atualizarListagemEm++;
  }
}
