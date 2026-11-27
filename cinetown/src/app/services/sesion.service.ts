import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Sesion } from '../models/sesion';

/** Acceso a los endpoints de sesiones. */
@Injectable({ providedIn: 'root' })
export class SesionService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/sesiones`;

  /** Lista las sesiones, opcionalmente de una fecha (yyyy-MM-dd) y/o de una película. */
  listar(fecha?: string, peliculaId?: number): Observable<Sesion[]> {
    let params = new HttpParams();
    if (fecha) params = params.set('fecha', fecha);
    if (peliculaId) params = params.set('peliculaId', peliculaId);
    // La API puede devolver un array o una página: aceptamos las dos formas.
    return this.http
      .get<any>(this.url, { params })
      .pipe(map((r) => (Array.isArray(r) ? r : r.content) as Sesion[]));
  }
}
