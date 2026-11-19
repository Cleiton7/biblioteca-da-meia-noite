import { Component, EventEmitter, Output, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MaterialModule } from '../../../shared/material.module';
import { UsuarioService } from '../../../services/usuario.service';
import { LivroService } from '../../../services/livro.service';
import { EmprestimoService } from '../../../services/emprestimo.service';
import { Usuario } from '../../../models/usuario.model';
import { Livro } from '../../../models/livro.model';
import { Emprestimo } from '../../../models/emprestimo.model';

/**
 * Componente responsável pelo formulário de realização de empréstimo de livros.
 * Emite o evento `emprestimoRealizado` sempre que um empréstimo é concluído
 * com sucesso.
 */
@Component({
  selector: 'app-emprestimo-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MaterialModule],
  templateUrl: './emprestimo-form.component.html',
  styleUrl: './emprestimo-form.component.scss'
})
export class EmprestimoFormComponent {
  @Output() emprestimoRealizado = new EventEmitter<Emprestimo>();

  usuarios = signal<Usuario[]>([]);
  livros = signal<Livro[]>([]);

  /** Apenas livros com ao menos um exemplar disponível podem ser emprestados. */
  livrosDisponiveis = computed(() =>
    this.livros().filter((livro) => (livro.quantidadeDisponivel ?? 0) > 0)
  );

  enviando = signal(false);
  mensagemErro = signal('');
  mensagemSucesso = signal('');

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly usuarioService: UsuarioService,
    private readonly livroService: LivroService,
    private readonly emprestimoService: EmprestimoService
  ) {
    this.form = this.formBuilder.group({
      usuarioId: [null as number | null, [Validators.required]],
      livroId: [null as number | null, [Validators.required]]
    });
    this.carregarUsuarios();
    this.carregarLivros();
  }

  private carregarUsuarios(): void {
    this.usuarioService.listar().subscribe({
      next: (usuarios) => this.usuarios.set(usuarios),
      error: () => this.mensagemErro.set('Não foi possível carregar a lista de usuários.')
    });
  }

  private carregarLivros(): void {
    this.livroService.listar().subscribe({
      next: (livros) => this.livros.set(livros),
      error: () => this.mensagemErro.set('Não foi possível carregar a lista de livros.')
    });
  }

  realizar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { usuarioId, livroId } = this.form.getRawValue();

    this.enviando.set(true);
    this.mensagemErro.set('');
    this.mensagemSucesso.set('');

    this.emprestimoService.realizar({ usuarioId: usuarioId!, livroId: livroId! }).subscribe({
      next: (emprestimo) => {
        this.enviando.set(false);
        this.form.reset({ usuarioId: null, livroId: null });
        this.exibirMensagemSucesso(emprestimo);
        // Atualiza o saldo de exemplares exibido no select de livros.
        this.carregarLivros();
        this.emprestimoRealizado.emit(emprestimo);
      },
      error: (erro) => {
        this.enviando.set(false);
        this.mensagemErro.set(erro?.error?.message ?? 'Erro ao realizar o empréstimo. Tente novamente.');
        // O estoque pode ter mudado; mantém a lista de livros atualizada.
        this.carregarLivros();
      }
    });
  }

  private exibirMensagemSucesso(emprestimo: Emprestimo): void {
    const devolucao = this.formatarData(emprestimo.dataPrevistaDevolucao);
    this.mensagemSucesso.set(
      `📚 "${emprestimo.livroTitulo}" emprestado para ${emprestimo.usuarioNome}. Devolução prevista em ${devolucao}.`
    );
    setTimeout(() => this.mensagemSucesso.set(''), 6000);
  }

  private formatarData(data: string): string {
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano}`;
  }
}
