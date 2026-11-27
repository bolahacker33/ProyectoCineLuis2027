import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Pagina } from '../models/pagina';
import { Pelicula } from '../models/pelicula';

export interface FiltrosPelicula {
  titulo?: string;
  genero?: string;
  clasificacion?: string;
  cartelera?: boolean;
}

/** Acceso a los endpoints de películas. */
@Injectable({ providedIn: 'root' })
export class PeliculaService {
  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/peliculas`;

  /** Lista películas con filtros opcionales y paginación (pagina empieza en 0). */
  listar(filtros: FiltrosPelicula, pagina: number, tamanio: number): Observable<Pagina<Pelicula>> {
    let params = new HttpParams().set('pagina', pagina).set('tamanio', tamanio);
    if (filtros.titulo) params = params.set('titulo', filtros.titulo);
    if (filtros.genero) params = params.set('genero', filtros.genero);
    if (filtros.clasificacion) params = params.set('clasificacion', filtros.clasificacion);
    if (filtros.cartelera !== undefined) params = params.set('cartelera', filtros.cartelera);

    return this.http.get<any>(this.url, { params }).pipe(
      map((r) => ({
        content: r.content as Pelicula[],
        total: (r.totalElements ?? r.page?.totalElements ?? 0) as number,
      })),
    );
  }
}
