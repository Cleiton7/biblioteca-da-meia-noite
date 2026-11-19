import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Emprestimo, EmprestimoPayload } from '../models/emprestimo.model';

/**
 * Serviço responsável por consumir os endpoints REST de empréstimos
 * expostos pelo backend (/emprestimos).
 */
@Injectable({
  providedIn: 'root'
})
export class EmprestimoService {
  private readonly apiUrl = '/api/emprestimos';

  constructor(private readonly http: HttpClient) {}

  realizar(payload: EmprestimoPayload): Observable<Emprestimo> {
    return this.http.post<Emprestimo>(this.apiUrl, payload);
  }
}
