import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';

/**
 * Serviço responsável por consumir os endpoints REST de usuários
 * expostos pelo backend (/usuarios).
 */
@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private readonly apiUrl = '/api/usuarios';

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }
}
