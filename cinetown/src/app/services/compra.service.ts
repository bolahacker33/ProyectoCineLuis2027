import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Compra, CrearCompra } from '../models/compra';

/** Página de compras ya normalizada para la interfaz. */
export interface PaginaCompras {
  content: Compra[];
  total: number;
}

/** Acceso a los endpoints de compras de entradas. */
@Injectable({ providedIn: 'root' })
export class CompraService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/compras`;

  /** Lista paginada de compras (la API decide cuáles puede ver cada rol). */
  listar(pagina: number, tamanio: number): Observable<PaginaCompras> {
    const params = new HttpParams().set('pagina', pagina).set('tamanio', tamanio);
    return this.http.get<any>(this.url, { params }).pipe(
      map((r) => ({
        content: (r.content ?? []) as Compra[],
        // Aceptamos los nombres habituales del total por si la API usa otro.
        total: r.totalElements ?? r.total ?? r.page?.totalElements ?? r.content?.length ?? 0,
      })),
    );
  }

  /** Compra entradas para una sesión. */
  crear(datos: CrearCompra): Observable<Compra> {
    return this.http.post<Compra>(this.url, datos);
  }

  /** Cancela una compra por su código. */
  cancelar(codigo: string): Observable<Compra> {
    return this.http.patch<Compra>(`${this.url}/${codigo}/cancelar`, {});
  }
}
