import { CrearCompra } from './compra';
import { Sesion } from './sesion';
import { calcularTotal, precioEntrada } from '../utils/precios';

/** Entradas que el usuario va a comprar para una sesión. */
export class SeleccionEntradas {
  private _numEntradas = 1;

  constructor(private sesion: Sesion) {}

  get numEntradas(): number {
    return this._numEntradas;
  }

  /** Mantiene el número de entradas entre 1 y 10. */
  set numEntradas(valor: number) {
    this._numEntradas = Math.min(10, Math.max(1, valor));
  }

  precioUnitario(): number {
    return precioEntrada(this.sesion.precioBase, this.sesion.fechaHoraInicio);
  }

  total(): number {
    return calcularTotal(this.precioUnitario(), this._numEntradas);
  }

  toCrearCompra(parkingUsado = false): CrearCompra {
    return { sesionId: this.sesion.id, numEntradas: this._numEntradas, parkingUsado };
  }
}
