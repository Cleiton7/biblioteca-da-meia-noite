import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { EmprestimoFormComponent } from './emprestimo-form.component';
import { UsuarioService } from '../../../services/usuario.service';
import { LivroService } from '../../../services/livro.service';
import { EmprestimoService } from '../../../services/emprestimo.service';
import { Usuario } from '../../../models/usuario.model';
import { Livro } from '../../../models/livro.model';
import { Genero } from '../../../models/genero.enum';
import { Emprestimo } from '../../../models/emprestimo.model';

/**
 * Testes unitários da funcionalidade de Realizar Empréstimo no frontend,
 * focados no formulário reativo do EmprestimoFormComponent.
 */
describe('EmprestimoFormComponent - Realizar Empréstimo', () => {
  let component: EmprestimoFormComponent;
  let usuarioServiceMock: { listar: ReturnType<typeof vi.fn> };
  let livroServiceMock: { listar: ReturnType<typeof vi.fn> };
  let emprestimoServiceMock: { realizar: ReturnType<typeof vi.fn> };

  const usuariosMock: Usuario[] = [{ id: 1, nome: 'Ana Silva', email: 'ana.silva@email.com' }];

  const autorMock = { id: 1, nome: 'Machado de Assis', nacionalidade: 'Brasileira', dataNascimento: '1839-06-21' };

  const livrosMock: Livro[] = [
    {
      id: 3,
      titulo: 'Dom Casmurro',
      autor: autorMock,
      genero: Genero.ROMANCE,
      quantidadeTotal: 5,
      quantidadeDisponivel: 5
    },
    {
      id: 4,
      titulo: 'Esgotado',
      autor: autorMock,
      genero: Genero.ROMANCE,
      quantidadeTotal: 2,
      quantidadeDisponivel: 0
    }
  ];

  const emprestimoMock: Emprestimo = {
    id: 9,
    usuarioId: 1,
    usuarioNome: 'Ana Silva',
    livroId: 3,
    livroTitulo: 'Dom Casmurro',
    dataEmprestimo: '2026-11-19',
    dataPrevistaDevolucao: '2026-12-03',
    status: 'ATIVO',
    quantidadeRenovacoes: 0
  };

  beforeEach(async () => {
    usuarioServiceMock = { listar: vi.fn().mockReturnValue(of(usuariosMock)) };
    livroServiceMock = { listar: vi.fn().mockReturnValue(of(livrosMock)) };
    emprestimoServiceMock = { realizar: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [EmprestimoFormComponent, HttpClientTestingModule, NoopAnimationsModule],
      providers: [
        { provide: UsuarioService, useValue: usuarioServiceMock },
        { provide: LivroService, useValue: livroServiceMock },
        { provide: EmprestimoService, useValue: emprestimoServiceMock }
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(EmprestimoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve carregar usuários e livros ao inicializar', () => {
    expect(usuarioServiceMock.listar).toHaveBeenCalled();
    expect(livroServiceMock.listar).toHaveBeenCalled();
    expect(component.usuarios()).toEqual(usuariosMock);
    expect(component.livros()).toEqual(livrosMock);
  });

  it('deve listar apenas livros com exemplares disponíveis', () => {
    expect(component.livrosDisponiveis().map((l) => l.titulo)).toEqual(['Dom Casmurro']);
  });

  it('deve iniciar com o formulário inválido', () => {
    expect(component.form.invalid).toBe(true);
  });

  it('não deve chamar o serviço quando o formulário é inválido', () => {
    component.realizar();

    expect(emprestimoServiceMock.realizar).not.toHaveBeenCalled();
    expect(component.form.get('usuarioId')?.touched).toBe(true);
    expect(component.form.get('livroId')?.touched).toBe(true);
  });

  it('deve chamar o serviço com o payload correto e emitir o evento em caso de sucesso', () => {
    emprestimoServiceMock.realizar.mockReturnValue(of(emprestimoMock));
    const emitSpy = vi.spyOn(component.emprestimoRealizado, 'emit');
    component.form.setValue({ usuarioId: 1, livroId: 3 });

    component.realizar();

    expect(emprestimoServiceMock.realizar).toHaveBeenCalledWith({ usuarioId: 1, livroId: 3 });
    expect(emitSpy).toHaveBeenCalledWith(emprestimoMock);
    expect(component.enviando()).toBe(false);
  });

  it('deve resetar o formulário e exibir mensagem de sucesso com a data de devolução', () => {
    emprestimoServiceMock.realizar.mockReturnValue(of(emprestimoMock));
    component.form.setValue({ usuarioId: 1, livroId: 3 });

    component.realizar();

    expect(component.form.getRawValue()).toEqual({ usuarioId: null, livroId: null });
    expect(component.mensagemSucesso()).toContain('Dom Casmurro');
    expect(component.mensagemSucesso()).toContain('Ana Silva');
    expect(component.mensagemSucesso()).toContain('03/12/2026');
  });

  it('deve recarregar a lista de livros após o empréstimo para atualizar o saldo', () => {
    emprestimoServiceMock.realizar.mockReturnValue(of(emprestimoMock));
    component.form.setValue({ usuarioId: 1, livroId: 3 });
    livroServiceMock.listar.mockClear();

    component.realizar();

    expect(livroServiceMock.listar).toHaveBeenCalledTimes(1);
  });

  it('deve exibir a mensagem retornada pela API quando o empréstimo falha', () => {
    emprestimoServiceMock.realizar.mockReturnValue(
      throwError(() => ({ error: { message: 'O livro "Dom Casmurro" não possui exemplares disponíveis' } }))
    );
    component.form.setValue({ usuarioId: 1, livroId: 3 });

    component.realizar();

    expect(component.mensagemErro()).toBe('O livro "Dom Casmurro" não possui exemplares disponíveis');
    expect(component.enviando()).toBe(false);
    expect(component.mensagemSucesso()).toBe('');
  });

  it('deve exibir mensagem genérica quando a API não retorna uma mensagem específica', () => {
    emprestimoServiceMock.realizar.mockReturnValue(throwError(() => ({})));
    component.form.setValue({ usuarioId: 1, livroId: 3 });

    component.realizar();

    expect(component.mensagemErro()).toBe('Erro ao realizar o empréstimo. Tente novamente.');
  });

  it('deve renderizar a mensagem de sucesso no template após o empréstimo', () => {
    const fixture = TestBed.createComponent(EmprestimoFormComponent);
    fixture.detectChanges();
    emprestimoServiceMock.realizar.mockReturnValue(of(emprestimoMock));
    fixture.componentInstance.form.setValue({ usuarioId: 1, livroId: 3 });

    fixture.componentInstance.realizar();
    fixture.detectChanges();

    const elemento = (fixture.nativeElement as HTMLElement).querySelector('.mensagem-sucesso');
    expect(elemento?.textContent).toContain('Dom Casmurro');
    expect(elemento?.textContent).toContain('03/12/2026');
  });

  it('deve manter a seleção do formulário quando o empréstimo falha', () => {
    emprestimoServiceMock.realizar.mockReturnValue(throwError(() => ({})));
    component.form.setValue({ usuarioId: 1, livroId: 3 });

    component.realizar();

    expect(component.form.getRawValue()).toEqual({ usuarioId: 1, livroId: 3 });
  });
});
