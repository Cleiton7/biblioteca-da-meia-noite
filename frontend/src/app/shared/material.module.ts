import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';

/**
 * Módulo centralizador dos componentes do Angular Material utilizados no projeto.
 *
 * Como os componentes da aplicação são standalone, este módulo pode ser importado
 * diretamente no array `imports` de qualquer componente que precise de recursos
 * visuais do Angular Material, evitando a repetição de múltiplos imports individuais.
 *
 * Exemplo de uso em um componente standalone:
 *
 * @Component({
 *   selector: 'app-livro-form',
 *   standalone: true,
 *   imports: [MaterialModule],
 *   templateUrl: './livro-form.html'
 * })
 */
const MATERIAL_MODULES = [
  MatButtonModule,
  MatInputModule,
  MatFormFieldModule,
  MatTableModule,
  MatCardModule,
  MatToolbarModule,
  MatDialogModule,
  MatSnackBarModule,
  MatProgressSpinnerModule,
  MatMenuModule,
  MatIconModule,
  MatSelectModule,
  MatChipsModule,
  MatPaginatorModule,
  MatSortModule
];

@NgModule({
  imports: [...MATERIAL_MODULES],
  exports: [...MATERIAL_MODULES]
})
export class MaterialModule {}
