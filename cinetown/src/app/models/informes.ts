export interface Recaudacion {
  peliculaId: number;
  titulo: string;
  entradasVendidas: number;
  recaudacionTotal: number;
}

export interface Ocupacion {
  sesionId: number;
  pelicula: string;
  sala: number;
  fechaHoraInicio: string;
  capacidad: number;
  entradasVendidas: number;
  porcentajeOcupacion: number;
}
