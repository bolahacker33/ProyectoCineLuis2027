export interface Compra {
  codigo: string;
  sesionId: number;
  pelicula: string;
  sala: number;
  fechaHoraInicio: string;
  numEntradas: number;
  precioTotal: number;
  estado: string;
  parkingUsado: boolean;
  fechaCompra: string;
  username: string | null;
}

export interface CrearCompra {
  sesionId: number;
  numEntradas: number;
  parkingUsado?: boolean;
}
