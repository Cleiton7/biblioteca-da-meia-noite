import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Autor } from '../models/autor.model';

/**
 * Serviço responsável por consumir os endpoints REST de autores
 * expostos pelo backend (/autores).
 */
@Injectable({
  providedIn: 'root'
})
export class AutorService {
  private readonly apiUrl = 'http://localhost:8080/autores';

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Autor[]> {
    return this.http.get<Autor[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Autor> {
    return this.http.get<Autor>(`${this.apiUrl}/${id}`);
  }
}
