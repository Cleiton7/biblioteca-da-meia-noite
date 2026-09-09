import { Autor } from './autor.model';
import { Genero } from './genero.enum';

/**
 * Representa um livro cadastrado no sistema.
 * Espelha a entidade Livro do backend.
 */
export interface Livro {
  id?: number;
  titulo: string;
  autor: Autor;
  genero: Genero;
  isbn?: string;
  anoPublicacao?: number;
  quantidadeTotal: number;
  quantidadeDisponivel?: number;
  dataCadastro?: string;
}

/**
 * Payload utilizado para criação/atualização de um livro pelo formulário,
 * onde o autor é referenciado apenas pelo id.
 */
export interface LivroPayload {
  titulo: string;
  autor: { id: number };
  genero: Genero;
  quantidadeTotal: number;
  isbn: string;
  anoPublicacao?: number;
}
