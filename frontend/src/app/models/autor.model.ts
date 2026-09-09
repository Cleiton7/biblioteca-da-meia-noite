/**
 * Representa um autor cadastrado no sistema.
 * Espelha a entidade Autor do backend.
 */
export interface Autor {
  id: number;
  nome: string;
  nacionalidade?: string;
  dataNascimento?: string;
}
