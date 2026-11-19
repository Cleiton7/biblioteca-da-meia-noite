import { Component } from '@angular/core';
import { EmprestimoFormComponent } from '../emprestimo-form/emprestimo-form.component';
import { NavMenuComponent } from '../../../shared/nav-menu/nav-menu.component';

/**
 * Página responsável por exibir o formulário de realização de
 * empréstimos de livros.
 *
 * Reaproveita o tema visual "Meia-Noite" da página de livros.
 */
@Component({
  selector: 'app-emprestimo-page',
  standalone: true,
  imports: [EmprestimoFormComponent, NavMenuComponent],
  templateUrl: './emprestimo-page.component.html',
  styleUrls: [
    '../../livros/livro-page/livro-page.component.scss',
    './emprestimo-page.component.scss'
  ]
})
export class EmprestimoPageComponent {}
