export const DESCUENTO_MIERCOLES = 0.3;

/** Indica si la fecha cae en miércoles. */
export function esMiercoles(fecha: string | Date): boolean {
  return new Date(fecha).getDay() === 3;
}

/** Precio de una entrada aplicando el 30 % de descuento los miércoles. */
export function precioEntrada(precioBase: number, fecha: string | Date): number {
  const precio = esMiercoles(fecha) ? precioBase * (1 - DESCUENTO_MIERCOLES) : precioBase;
  return Math.round(precio * 100) / 100;
}

/** Total de una compra a partir del precio unitario y las entradas. */
export function calcularTotal(precioUnitario: number, numEntradas: number): number {
  return Math.round(precioUnitario * numEntradas * 100) / 100;
}
