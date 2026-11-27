export interface Pelicula {
  id: number;
  titulo: string;
  anio: number;
  duracion: number;
  clasificacion: string;
  sinopsis: string;
  cartelera: boolean;
  generos: string[];
}

export interface CrearPelicula {
  titulo: string;
  anio: number;
  duracion: number;
  clasificacion: string;
  sinopsis?: string;
  generos: string[];
}

export type EditarPelicula = CrearPelicula;

export interface CambiarCartelera {
  cartelera: boolean;
}
