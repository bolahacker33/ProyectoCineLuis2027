import { DatePipe } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Compra } from '../../models/compra';

/**
 * Lista de compras reutilizable.
 * Recibe los datos con input() y avisa de la cancelación con output().
 */
@Component({
  selector: 'app-tabla-compras',
  imports: [DatePipe, MatCardModule, MatButtonModule],
  template: `
    <div class="lista">
      @for (c of compras(); track c.codigo) {
        <mat-card class="compra" [class.cancelada]="esCancelada(c)">
          <mat-card-content>
            <div class="cab">
              <h3>{{ c.pelicula }}</h3>
              <span class="estado" [class.cancelada]="esCancelada(c)">{{ c.estado }}</span>
            </div>
            <p class="datos">
              {{ c.fechaHoraInicio | date: 'dd/MM/yyyy HH:mm' }} · Sala {{ c.sala }}
            </p>
            <p class="importe">{{ c.numEntradas }} entradas · <strong>{{ c.precioTotal }} €</strong></p>
            <p class="codigo">Código {{ c.codigo }}</p>
            @if (mostrarUsuario()) {
              <p class="datos">Cliente: {{ c.username ?? 'Venta en taquilla' }}</p>
            }
            @if (!esCancelada(c)) {
              <button
                mat-stroked-button
                class="boton-cancelar"
                [disabled]="cancelando() === c.codigo"
                (click)="cancelar.emit(c.codigo)"
              >
                {{ cancelando() === c.codigo ? 'Cancelando…' : 'Cancelar compra' }}
              </button>
            }
          </mat-card-content>
        </mat-card>
      }
    </div>
  `,
  styles: `
    .lista { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; margin: 20px 0; }
    .compra { border-left: 4px solid #1b9e4b; }
    .compra.cancelada { border-left-color: #b3261e; opacity: 0.7; }
    .cab { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
    h3 { margin: 8px 0 4px; font-size: 19px; }
    .estado { background: #e3f4e8; color: #1b6e2d; font-size: 12px; padding: 3px 10px; border-radius: 12px; }
    .estado.cancelada { background: #fbe4e2; color: #b3261e; }
    .datos { margin: 0 0 8px; color: #666b8a; font-size: 14px; }
    .importe { margin: 0 0 4px; }
    .codigo { margin: 0 0 12px; font-size: 12px; color: #666b8a; word-break: break-all; }
    .boton-cancelar { color: #b3261e; }
  `,
})
export class TablaCompras {
  /** Compras a mostrar. */
  compras = input.required<Compra[]>();
  /** Si es true se muestra el cliente de cada compra (vista de taquilla). */
  mostrarUsuario = input(false);
  /** Código de la compra que se está cancelando (para desactivar su botón). */
  cancelando = input<string | null>(null);
  /** Se emite con el código cuando se pulsa "Cancelar compra". */
  cancelar = output<string>();

  /** Indica si la compra ya está cancelada. */
  esCancelada(c: Compra): boolean {
    return c.estado.toUpperCase().startsWith('CANCEL');
  }
}
