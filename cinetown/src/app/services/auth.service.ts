import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Usuario, crearUsuario } from '../models/usuario';

export interface RespuestaUsuario {
  username: string;
  rol: string;
}

/** Gestiona el login, el registro y la sesión del usuario. */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private usuarioActual = signal<Usuario | null>(this.cargarUsuario());

  readonly usuario = this.usuarioActual.asReadonly();
  readonly estaLogueado = computed(() => this.usuarioActual() !== null);

  /** Credenciales Basic guardadas, o null si no hay sesión. */
  get token(): string | null {
    return sessionStorage.getItem('token');
  }

  /** Comprueba las credenciales contra la API y guarda la sesión. */
  login(username: string, password: string): Observable<RespuestaUsuario> {
    const token = btoa(`${username}:${password}`);
    return this.http
      .get<RespuestaUsuario>(`${environment.apiUrl}/auth/me`, {
        headers: { Authorization: `Basic ${token}` },
      })
      .pipe(tap((respuesta) => this.guardarSesion(token, respuesta)));
  }

  /** Crea una cuenta nueva con rol CLIENTE. */
  registro(username: string, password: string): Observable<RespuestaUsuario> {
    return this.http.post<RespuestaUsuario>(`${environment.apiUrl}/auth/registro`, {
      username,
      password,
    });
  }

  /** Cierra la sesión. */
  logout(): void {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('sesion');
    this.usuarioActual.set(null);
  }

  private guardarSesion(token: string, respuesta: RespuestaUsuario): void {
    sessionStorage.setItem('token', token);
    sessionStorage.setItem('sesion', JSON.stringify(respuesta));
    this.usuarioActual.set(crearUsuario(respuesta.username, respuesta.rol));
  }

  private cargarUsuario(): Usuario | null {
    const guardado = sessionStorage.getItem('sesion');
    if (!guardado) return null;
    const respuesta = JSON.parse(guardado) as RespuestaUsuario;
    return crearUsuario(respuesta.username, respuesta.rol);
  }
}
