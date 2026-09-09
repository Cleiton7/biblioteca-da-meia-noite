import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { LivroFormComponent } from './livro-form.component';
import { LivroService } from '../../../services/livro.service';
import { AutorService } from '../../../services/autor.service';
import { Genero } from '../../../models/genero.enum';
import { Autor } from '../../../models/autor.model';
import { Livro } from '../../../models/livro.model';

/**
 * Testes unitários da funcionalidade de Cadastrar Livros no frontend,
 * focados no formulário reativo do LivroFormComponent.
 */
describe('LivroFormComponent - Cadastrar Livro', () => {
  let component: LivroFormComponent;
  let livroServiceMock: { cadastrar: ReturnType<typeof vi.fn> };
  let autorServiceMock: { listar: ReturnType<typeof vi.fn> };

  const autoresMock: Autor[] = [
    { id: 1, nome: 'Machado de Assis', nacionalidade: 'Brasileira', dataNascimento: '1839-06-21' }
  ];

  const livroCadastradoMock: Livro = {
    id: 10,
    titulo: 'Dom Casmurro',
    autor: autoresMock[0],
    genero: Genero.ROMANCE,
    isbn: '978-85-359-0277-5',
    anoPublicacao: 1899,
    quantidadeTotal: 5,
    quantidadeDisponivel: 5
  };

  beforeEach(async () => {
    livroServiceMock = { cadastrar: vi.fn() };
    autorServiceMock = { listar: vi.fn().mockReturnValue(of(autoresMock)) };

    await TestBed.configureTestingModule({
      imports: [LivroFormComponent, HttpClientTestingModule, NoopAnimationsModule],
      providers: [
        { provide: LivroService, useValue: livroServiceMock },
        { provide: AutorService, useValue: autorServiceMock }
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(LivroFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente com sucesso', () => {
    expect(component).toBeTruthy();
  });

  it('deve carregar a lista de autores ao inicializar', () => {
    expect(autorServiceMock.listar).toHaveBeenCalled();
    expect(component.autores()).toEqual(autoresMock);
  });

  it('deve exibir mensagem de erro quando falhar ao carregar autores', async () => {
    autorServiceMock.listar.mockReturnValue(throwError(() => new Error('erro')));

    const fixture = TestBed.createComponent(LivroFormComponent);
    const novoComponente = fixture.componentInstance;
    fixture.detectChanges();

    expect(novoComponente.mensagemErro()).toBe('Não foi possível carregar a lista de autores.');
  });

  it('deve iniciar com o formulário inválido quando os campos obrigatórios estão vazios', () => {
    expect(component.form.invalid).toBe(true);
  });

  it('não deve chamar o serviço de cadastro quando o formulário é inválido', () => {
    component.cadastrar();

    expect(livroServiceMock.cadastrar).not.toHaveBeenCalled();
  });

  it('deve marcar todos os campos como touched ao tentar submeter formulário inválido', () => {
    component.cadastrar();

    expect(component.form.get('titulo')?.touched).toBe(true);
    expect(component.form.get('autorId')?.touched).toBe(true);
    expect(component.form.get('genero')?.touched).toBe(true);
    expect(component.form.get('isbn')?.touched).toBe(true);
  });

  it('deve invalidar o campo isbn quando o formato não corresponde ao padrão esperado', () => {
    const isbnControl = component.form.get('isbn');
    isbnControl?.setValue('isbn-invalido');

    expect(isbnControl?.valid).toBe(false);
  });

  it('deve validar o campo isbn quando o formato é válido', () => {
    const isbnControl = component.form.get('isbn');
    isbnControl?.setValue('978-85-359-0277-5');

    expect(isbnControl?.hasError('pattern')).toBe(false);
  });

  function preencherFormularioValido(): void {
    component.form.setValue({
      titulo: 'Dom Casmurro',
      autorId: 1,
      genero: Genero.ROMANCE,
      isbn: '978-85-359-0277-5',
      anoPublicacao: 1899,
      quantidadeTotal: 5
    });
  }

  it('deve chamar o livroService.cadastrar com o payload correto quando o formulário é válido', () => {
    preencherFormularioValido();
    livroServiceMock.cadastrar.mockReturnValue(of(livroCadastradoMock));

    component.cadastrar();

    expect(livroServiceMock.cadastrar).toHaveBeenCalledWith({
      titulo: 'Dom Casmurro',
      autor: { id: 1 },
      genero: Genero.ROMANCE,
      isbn: '978-85-359-0277-5',
      anoPublicacao: 1899,
      quantidadeTotal: 5
    });
  });

  it('deve resetar o formulário e emitir o evento livroCadastrado após sucesso', () => {
    preencherFormularioValido();
    livroServiceMock.cadastrar.mockReturnValue(of(livroCadastradoMock));

    const emitSpy = vi.spyOn(component.livroCadastrado, 'emit');

    component.cadastrar();

    expect(component.form.getRawValue().titulo).toBe('');
    expect(component.form.getRawValue().autorId).toBeNull();
    expect(component.enviando()).toBe(false);
    expect(emitSpy).toHaveBeenCalled();
  });

  it('deve exibir mensagem de sucesso com o título do livro cadastrado', () => {
    preencherFormularioValido();
    livroServiceMock.cadastrar.mockReturnValue(of(livroCadastradoMock));

    component.cadastrar();

    expect(component.mensagemSucesso()).toContain('Dom Casmurro');
  });

  it('deve exibir mensagem de erro retornada pela API quando o cadastro falha', () => {
    preencherFormularioValido();
    livroServiceMock.cadastrar.mockReturnValue(
      throwError(() => ({ error: { message: 'ISBN já cadastrado' } }))
    );

    component.cadastrar();

    expect(component.mensagemErro()).toBe('ISBN já cadastrado');
    expect(component.enviando()).toBe(false);
  });

  it('deve exibir mensagem de erro genérica quando a API não retorna uma mensagem específica', () => {
    preencherFormularioValido();
    livroServiceMock.cadastrar.mockReturnValue(throwError(() => ({})));

    component.cadastrar();

    expect(component.mensagemErro()).toBe('Erro ao cadastrar o livro. Tente novamente.');
  });

  it('não deve manter o formulário resetado quando ocorre erro no cadastro', () => {
    preencherFormularioValido();
    livroServiceMock.cadastrar.mockReturnValue(throwError(() => ({})));

    component.cadastrar();

    expect(component.form.getRawValue().titulo).toBe('Dom Casmurro');
  });
});
