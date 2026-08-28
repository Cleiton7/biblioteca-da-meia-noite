import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Livro, LivroPayload } from '../models/livro.model';

/**
 * Serviço responsável por consumir os endpoints REST de livros
 * expostos pelo backend (/livros).
 */
@Injectable({
  providedIn: 'root'
})
export class LivroService {
  private readonly apiUrl = 'http://localhost:8080/livros';

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Livro[]> {
    return this.http.get<Livro[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Livro> {
    return this.http.get<Livro>(`${this.apiUrl}/${id}`);
  }

  cadastrar(livro: LivroPayload): Observable<Livro> {
    return this.http.post<Livro>(this.apiUrl, livro);
  }

  atualizar(id: number, livro: LivroPayload): Observable<Livro> {
    return this.http.put<Livro>(`${this.apiUrl}/${id}`, livro);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
