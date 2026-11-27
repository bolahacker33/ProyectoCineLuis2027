export interface Sesion {
  id: number;
  pelicula: string;
  sala: number;
  capacidad: number;
  precioBase: number;
  precioDia: number;
  fechaHoraInicio: string;
  fechaHoraFin: string;
  asientosLibres: number;
}

export interface CrearSesion {
  peliculaId: number;
  salaId: number;
  fechaHoraInicio: string;
  precioBase: number;
}
