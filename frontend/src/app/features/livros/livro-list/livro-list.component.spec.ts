import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { LivroListComponent } from './livro-list.component';
import { LivroService } from '../../../services/livro.service';
import { Genero } from '../../../models/genero.enum';
import { Autor } from '../../../models/autor.model';
import { Livro } from '../../../models/livro.model';

/**
 * Testes unitários da busca de livros por id no LivroListComponent.
 */
describe('LivroListComponent - Buscar Livro por ID', () => {
  let component: LivroListComponent;
  let livroServiceMock: {
    listar: ReturnType<typeof vi.fn>;
    buscarPorId: ReturnType<typeof vi.fn>;
    deletar: ReturnType<typeof vi.fn>;
  };

  const autorMock: Autor = {
    id: 1,
    nome: 'Machado de Assis',
    nacionalidade: 'Brasileira',
    dataNascimento: '1839-06-21'
  };

  const livrosMock: Livro[] = [
    {
      id: 10,
      titulo: 'Dom Casmurro',
      autor: autorMock,
      genero: Genero.ROMANCE,
      isbn: '978-85-359-0277-5',
      anoPublicacao: 1899,
      quantidadeTotal: 5,
      quantidadeDisponivel: 5
    },
    {
      id: 11,
      titulo: 'Memórias Póstumas de Brás Cubas',
      autor: autorMock,
      genero: Genero.ROMANCE,
      isbn: '978-85-359-0278-2',
      anoPublicacao: 1881,
      quantidadeTotal: 3,
      quantidadeDisponivel: 2
    }
  ];

  beforeEach(async () => {
    livroServiceMock = {
      listar: vi.fn().mockReturnValue(of(livrosMock)),
      buscarPorId: vi.fn(),
      deletar: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [LivroListComponent, HttpClientTestingModule, NoopAnimationsModule],
      providers: [{ provide: LivroService, useValue: livroServiceMock }]
    }).compileComponents();

    const fixture = TestBed.createComponent(LivroListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve carregar a listagem completa ao inicializar', () => {
    expect(livroServiceMock.listar).toHaveBeenCalled();
    expect(component.livros()).toEqual(livrosMock);
    expect(component.buscaAtiva()).toBeNull();
  });

  it('deve exibir apenas o livro encontrado quando a busca por id tem sucesso', () => {
    livroServiceMock.buscarPorId.mockReturnValue(of(livrosMock[0]));
    component.atualizarIdBusca('10');

    component.buscarPorId();

    expect(livroServiceMock.buscarPorId).toHaveBeenCalledWith(10);
    expect(component.livros()).toEqual([livrosMock[0]]);
    expect(component.buscaAtiva()).toBe(10);
    expect(component.mensagemErro()).toBe('');
    expect(component.carregando()).toBe(false);
  });

  it('deve exibir a mensagem da API quando o livro não é encontrado (404)', () => {
    livroServiceMock.buscarPorId.mockReturnValue(
      throwError(() => ({
        status: 404,
        error: { message: 'Livro não encontrado com o id: 99' }
      }))
    );
    component.atualizarIdBusca('99');

    component.buscarPorId();

    expect(component.mensagemErro()).toBe('Livro não encontrado com o id: 99');
    expect(component.buscaAtiva()).toBeNull();
    expect(component.carregando()).toBe(false);
  });

  it('deve zerar a busca ativa quando uma nova busca falha após uma busca bem-sucedida', () => {
    livroServiceMock.buscarPorId.mockReturnValueOnce(of(livrosMock[0]));
    component.atualizarIdBusca('10');
    component.buscarPorId();
    expect(component.buscaAtiva()).toBe(10);

    livroServiceMock.buscarPorId.mockReturnValueOnce(
      throwError(() => ({
        status: 404,
        error: { message: 'Livro não encontrado com o id: 1999' }
      }))
    );
    component.atualizarIdBusca('1999');
    component.buscarPorId();

    expect(component.buscaAtiva()).toBeNull();
    expect(component.mensagemErro()).toBe('Livro não encontrado com o id: 1999');
  });

  it('deve exibir mensagem genérica quando a busca falha por outro motivo', () => {
    livroServiceMock.buscarPorId.mockReturnValue(throwError(() => ({ status: 500 })));
    component.atualizarIdBusca('10');

    component.buscarPorId();

    expect(component.mensagemErro()).toBe('Não foi possível buscar o livro informado.');
  });

  it('não deve chamar o serviço quando o id está vazio', () => {
    component.atualizarIdBusca('');

    component.buscarPorId();

    expect(livroServiceMock.buscarPorId).not.toHaveBeenCalled();
    expect(component.mensagemErro()).toBe('Informe um ID válido (número inteiro positivo).');
  });

  it('não deve chamar o serviço quando o id é zero, negativo ou decimal', () => {
    for (const valor of ['0', '-5', '1.5', 'abc']) {
      component.atualizarIdBusca(valor);
      component.buscarPorId();
    }

    expect(livroServiceMock.buscarPorId).not.toHaveBeenCalled();
    expect(component.mensagemErro()).toBe('Informe um ID válido (número inteiro positivo).');
  });

  it('deve limpar a busca e recarregar a listagem completa', () => {
    livroServiceMock.buscarPorId.mockReturnValue(of(livrosMock[0]));
    component.atualizarIdBusca('10');
    component.buscarPorId();
    livroServiceMock.listar.mockClear();

    component.limparBusca();

    expect(component.idBusca()).toBe('');
    expect(component.buscaAtiva()).toBeNull();
    expect(livroServiceMock.listar).toHaveBeenCalledTimes(1);
    expect(component.livros()).toEqual(livrosMock);
  });

  it('deve limpar a busca ativa quando a listagem é atualizada por um novo cadastro', () => {
    livroServiceMock.buscarPorId.mockReturnValue(of(livrosMock[0]));
    component.atualizarIdBusca('10');
    component.buscarPorId();

    component.ngOnChanges();

    expect(component.buscaAtiva()).toBeNull();
    expect(component.livros()).toEqual(livrosMock);
  });
});
