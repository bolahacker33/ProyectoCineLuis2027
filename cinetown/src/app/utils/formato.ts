/** Convierte minutos en texto, por ejemplo 135 -> "2 h 15 min". */
export function formatearDuracion(minutos: number): string {
  const horas = Math.floor(minutos / 60);
  const resto = minutos % 60;
  return horas > 0 ? `${horas} h ${resto} min` : `${resto} min`;
}

/** Formatea una fecha ISO al estilo español. */
export function formatearFecha(fecha: string): string {
  return new Date(fecha).toLocaleString('es-ES', { dateStyle: 'short', timeStyle: 'short' });
}

/** Una compra se cancela hasta 1 hora antes y si no se ha canjeado el parking. */
export function puedeCancelar(fechaHoraInicio: string, parkingUsado: boolean): boolean {
  const limite = new Date(fechaHoraInicio).getTime() - 60 * 60 * 1000;
  return !parkingUsado && Date.now() < limite;
}
