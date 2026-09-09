import { Component } from '@angular/core';
import { LivroFormComponent } from '../livro-form/livro-form.component';

/**
 * Página responsável por exibir o formulário de cadastro de livros.
 *
 * A listagem de livros foi temporariamente removida desta tela e está
 * preservada na branch `feature/listagem-livros`, podendo ser reintegrada
 * futuramente reimportando o LivroListComponent.
 */
@Component({
  selector: 'app-livro-page',
  standalone: true,
  imports: [LivroFormComponent],
  templateUrl: './livro-page.component.html',
  styleUrl: './livro-page.component.scss'
})
export class LivroPageComponent {
  onLivroCadastrado(): void {
    // Sem listagem ativa por enquanto; hook mantido para futura reintegração.
  }
}
