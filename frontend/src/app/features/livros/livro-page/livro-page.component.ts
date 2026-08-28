import { Component } from '@angular/core';
import { LivroFormComponent } from '../livro-form/livro-form.component';
import { LivroListComponent } from '../livro-list/livro-list.component';

/**
 * Página responsável por integrar o formulário de cadastro e a listagem
 * de livros, conforme o mockup da tela de Biblioteca da Meia Noite.
 */
@Component({
  selector: 'app-livro-page',
  standalone: true,
  imports: [LivroFormComponent, LivroListComponent],
  templateUrl: './livro-page.component.html',
  styleUrl: './livro-page.component.scss'
})
export class LivroPageComponent {
  atualizarListagemEm = 0;

  onLivroCadastrado(): void {
    this.atualizarListagemEm++;
  }
}
