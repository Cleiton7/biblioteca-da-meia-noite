import { Component, EventEmitter, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MaterialModule } from '../../../shared/material.module';
import { AutorService } from '../../../services/autor.service';
import { LivroService } from '../../../services/livro.service';
import { Autor } from '../../../models/autor.model';
import { Genero, GENERO_LABELS } from '../../../models/genero.enum';
import { LivroPayload } from '../../../models/livro.model';

/**
 * Componente responsável pelo formulário de cadastro de livros.
 * Emite o evento `livroCadastrado` sempre que um novo livro é
 * cadastrado com sucesso, permitindo que o componente pai atualize
 * a listagem.
 */
@Component({
  selector: 'app-livro-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MaterialModule],
  templateUrl: './livro-form.component.html',
  styleUrl: './livro-form.component.scss'
})
export class LivroFormComponent {
  @Output() livroCadastrado = new EventEmitter<void>();

  autores = signal<Autor[]>([]);
  generos = Object.values(Genero);

  enviando = signal(false);
  mensagemErro = signal('');

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly autorService: AutorService,
    private readonly livroService: LivroService
  ) {
    this.form = this.formBuilder.group({
      titulo: ['', [Validators.required, Validators.maxLength(200)]],
      autorId: [null as number | null, [Validators.required]],
      genero: [null as Genero | null, [Validators.required]],
      quantidadeTotal: [1, [Validators.required, Validators.min(1)]]
    });
    this.carregarAutores();
  }

  private carregarAutores(): void {
    this.autorService.listar().subscribe({
      next: (autores) => this.autores.set(autores),
      error: () => this.mensagemErro.set('Não foi possível carregar a lista de autores.')
    });
  }

  cadastrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { titulo, autorId, genero, quantidadeTotal } = this.form.getRawValue();

    const payload: LivroPayload = {
      titulo: titulo!,
      autor: { id: autorId! },
      genero: genero!,
      quantidadeTotal: quantidadeTotal!
    };

    this.enviando.set(true);
    this.mensagemErro.set('');

    this.livroService.cadastrar(payload).subscribe({
      next: () => {
        this.enviando.set(false);
        this.form.reset({ titulo: '', autorId: null, genero: null, quantidadeTotal: 1 });
        this.livroCadastrado.emit();
      },
      error: (erro) => {
        this.enviando.set(false);
        this.mensagemErro.set(erro?.error?.message ?? 'Erro ao cadastrar o livro. Tente novamente.');
      }
    });
  }

  labelGenero(genero: Genero): string {
    return GENERO_LABELS[genero];
  }
}
