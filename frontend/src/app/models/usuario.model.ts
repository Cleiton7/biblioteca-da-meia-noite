/**
 * Representa um usuário cadastrado no sistema.
 * Espelha a entidade Usuario do backend.
 */
export interface Usuario {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  dataCadastro?: string;
}
