import { Routes } from '@angular/router';
import { LivroPageComponent } from './features/livros/livro-page/livro-page.component';

export const routes: Routes = [
  { path: '', redirectTo: 'livros', pathMatch: 'full' },
  { path: 'livros', component: LivroPageComponent }
];
