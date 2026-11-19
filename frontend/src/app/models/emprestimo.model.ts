/**
 * Status possíveis de um empréstimo. Espelha o enum StatusEmprestimo do backend.
 */
export type StatusEmprestimo = 'ATIVO' | 'DEVOLVIDO' | 'ATRASADO';

/**
 * Payload utilizado para realizar o empréstimo de um livro.
 */
export interface EmprestimoPayload {
  usuarioId: number;
  livroId: number;
}

/**
 * Representa um empréstimo retornado pela API.
 * Espelha o EmprestimoResponse do backend.
 */
export interface Emprestimo {
  id: number;
  usuarioId: number;
  usuarioNome: string;
  livroId: number;
  livroTitulo: string;
  dataEmprestimo: string;
  dataPrevistaDevolucao: string;
  status: StatusEmprestimo;
  quantidadeRenovacoes: number;
}
