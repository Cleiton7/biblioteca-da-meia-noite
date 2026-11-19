import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

/**
 * Menu de navegação simples entre as páginas da aplicação
 * (Livros e Empréstimos).
 */
@Component({
  selector: 'app-nav-menu',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="nav-menu" aria-label="Navegação principal">
      <a routerLink="/livros" routerLinkActive="ativo">📚 Livros</a>
      <a routerLink="/emprestimos" routerLinkActive="ativo">🔖 Empréstimos</a>
    </nav>
  `,
  styles: [
    `
      :host {
        display: block;
      }

      .nav-menu {
        display: flex;
        justify-content: center;
        gap: 12px;
      }

      a {
        padding: 8px 22px;
        border-radius: 22px;
        color: rgba(245, 246, 255, 0.8);
        text-decoration: none;
        font-weight: 500;
        letter-spacing: 0.02em;
        border: 1px solid rgba(244, 233, 216, 0.25);
        background: rgba(16, 24, 48, 0.5);
        backdrop-filter: blur(8px);
        transition: background 0.18s ease, color 0.18s ease, transform 0.18s ease;
      }

      a:hover {
        color: #f8f4e8;
        transform: translateY(-1px);
      }

      a.ativo {
        color: #16213e;
        font-weight: 700;
        border-color: transparent;
        background: linear-gradient(135deg, #f8f4e8, #e0c9a6);
      }
    `
  ]
})
export class NavMenuComponent {}
