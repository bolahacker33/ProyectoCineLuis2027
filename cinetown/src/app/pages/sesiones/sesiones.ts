import { DatePipe } from '@angular/common';
import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Subject, catchError, merge, map, of, switchMap, tap, timer } from 'rxjs';
import { Sesion } from '../../models/sesion';
import { SeleccionEntradas } from '../../models/seleccion-entradas';
import { CompraService } from '../../services/compra.service';
import { SesionService } from '../../services/sesion.service';
import { esMiercoles } from '../../utils/precios';

interface Mensaje {
  tipo: 'ok' | 'error';
  texto: string;
}

/** Sesiones del día con compra de entradas. Se actualiza sola cada 10 segundos. */
@Component({
  selector: 'app-sesiones',
  imports: [
    DatePipe,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressBarModule,
  ],
  template: `
    <header class="cabecera">
      <h1>Sesiones</h1>
      <p>Compra tus entradas. Los asientos libres se actualizan en directo.</p>
    </header>

    <mat-card class="panel">
      <mat-form-field appearance="outline" subscriptSizing="dynamic">
        <mat-label>Fecha</mat-label>
        <input matInput type="date" [value]="fecha()" (change)="cambiarFecha($any($event.target).value)" />
      </mat-form-field>
      @if (ultimaActualizacion()) {
        <span class="actualizado">Actualizado a las {{ ultimaActualizacion() | date: 'HH:mm:ss' }}</span>
      }
    </mat-card>

    @if (cargando()) {
      <mat-progress-bar mode="indeterminate" />
    }
    @if (error()) {
      <p class="mensaje error">{{ error() }}</p>
    }
    @if (!cargando() && !error() && sesiones().length === 0) {
      <p class="mensaje">No hay sesiones para esa fecha.</p>
    }

    <div class="rejilla">
      @for (s of sesiones(); track s.id) {
        <mat-card class="sesion">
          <mat-card-content>
            <div class="hora">{{ s.fechaHoraInicio | date: 'HH:mm' }}</div>
            <h3>{{ s.pelicula }}</h3>
            <p class="datos">Sala {{ s.sala }} · hasta las {{ s.fechaHoraFin | date: 'HH:mm' }}</p>

            <mat-progress-bar mode="determinate" [value]="ocupacion(s)" />
            <p class="libres" [class.agotada]="s.asientosLibres === 0">
              {{ s.asientosLibres === 0 ? 'Agotada' : s.asientosLibres + ' de ' + s.capacidad + ' asientos libres' }}
            </p>

            <div class="precio">
              {{ seleccion(s).precioUnitario() }} € / entrada
              @if (esMiercoles(s.fechaHoraInicio)) {
                <span class="oferta">-30 % miércoles</span>
              }
            </div>

            @if (s.asientosLibres > 0) {
              <div class="compra">
                <button mat-stroked-button (click)="cambiarEntradas(s, -1)">−</button>
                <strong>{{ seleccion(s).numEntradas }}</strong>
                <button mat-stroked-button (click)="cambiarEntradas(s, 1)">+</button>
                <span class="total">Total: {{ seleccion(s).total() }} €</span>
              </div>
              <button
                mat-flat-button
                class="comprar"
                [disabled]="comprando() === s.id"
                (click)="comprar(s)"
              >
                {{ comprando() === s.id ? 'Comprando…' : 'Comprar entradas' }}
              </button>
            }

            @if (mensajes()[s.id]; as m) {
              <p class="aviso" [class.error]="m.tipo === 'error'">{{ m.texto }}</p>
            }
          </mat-card-content>
        </mat-card>
      }
    </div>
  `,
  styles: `
    .cabecera h1 { margin: 0; font-size: 34px; color: #14162b; }
    .cabecera p { margin: 4px 0 20px; color: #666b8a; }
    .panel { padding: 16px; margin-bottom: 16px; display: flex; flex-direction: row; align-items: center; gap: 16px; }
    .actualizado { color: #666b8a; font-size: 13px; }
    .rejilla { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; margin: 20px 0; }
    .sesion { border-top: 4px solid #e8506a; }
    .hora { font-size: 32px; font-weight: 800; color: #14162b; margin-top: 12px; }
    h3 { margin: 4px 0; font-size: 19px; }
    .datos { margin: 0 0 12px; color: #666b8a; font-size: 14px; }
    .libres { margin: 6px 0 12px; font-size: 14px; color: #1b6e2d; }
    .libres.agotada { color: #b3261e; font-weight: bold; }
    .precio { font-weight: 600; margin-bottom: 12px; }
    .oferta { background: #e8506a; color: #fff; font-size: 12px; padding: 3px 8px; border-radius: 10px; margin-left: 6px; }
    .compra { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
    .total { margin-left: auto; font-weight: 600; }
    .comprar { width: 100%; }
    .aviso { margin: 12px 0 0; color: #1b6e2d; font-size: 14px; }
    .aviso.error, .mensaje.error { color: #b3261e; }
    .mensaje { text-align: center; color: #666b8a; }
  `,
})
export class Sesiones implements OnInit {
  private sesionService = inject(SesionService);
  private compraService = inject(CompraService);
  private destroyRef = inject(DestroyRef);

  sesiones = signal<Sesion[]>([]);
  fecha = signal(new Date().toLocaleDateString('sv-SE')); // yyyy-MM-dd
  cargando = signal(false);
  error = signal('');
  comprando = signal<number | null>(null);
  mensajes = signal<Record<number, Mensaje>>({});
  ultimaActualizacion = signal<Date | null>(null);

  readonly esMiercoles = esMiercoles;

  /** Selección de entradas de cada sesión, por id (arrays/mapas en cliente). */
  private selecciones = new Map<number, SeleccionEntradas>();

  /** Peticiones de recarga: true = mostrar barra de carga, false = recarga silenciosa. */
  private recarga$ = new Subject<boolean>();

  ngOnInit(): void {
    // Recarga cada 10 s y cada vez que se pide manualmente (cambio de fecha o tras comprar).
    merge(timer(0, 10000).pipe(map(() => false)), this.recarga$)
      .pipe(
        tap((mostrarCarga) => this.cargando.set(mostrarCarga)),
        switchMap(() =>
          this.sesionService.listar(this.fecha()).pipe(
            catchError(() => {
              this.error.set('No se pudieron cargar las sesiones');
              return of(null);
            }),
          ),
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((lista) => {
        this.cargando.set(false);
        if (lista) {
          this.error.set('');
          // Ordenadas por hora de inicio.
          this.sesiones.set([...lista].sort((a, b) => a.fechaHoraInicio.localeCompare(b.fechaHoraInicio)));
          this.ultimaActualizacion.set(new Date());
        }
      });
  }

  cambiarFecha(valor: string): void {
    if (!valor) return;
    this.fecha.set(valor);
    this.recarga$.next(true);
  }

  /** Devuelve (o crea) la selección de entradas de una sesión. */
  seleccion(s: Sesion): SeleccionEntradas {
    let sel = this.selecciones.get(s.id);
    if (!sel) {
      sel = new SeleccionEntradas(s);
      this.selecciones.set(s.id, sel);
    }
    return sel;
  }

  cambiarEntradas(s: Sesion, delta: number): void {
    const sel = this.seleccion(s);
    sel.numEntradas = Math.min(sel.numEntradas + delta, s.asientosLibres);
    if (delta < 0) sel.numEntradas = Math.max(1, sel.numEntradas);
  }

  /** Porcentaje de asientos ocupados. */
  ocupacion(s: Sesion): number {
    return Math.round(((s.capacidad - s.asientosLibres) / s.capacidad) * 100);
  }

  comprar(s: Sesion): void {
    this.comprando.set(s.id);
    this.ponerMensaje(s.id, undefined);
    this.compraService.crear(this.seleccion(s).toCrearCompra()).subscribe({
      next: (compra) => {
        this.comprando.set(null);
        this.ponerMensaje(s.id, {
          tipo: 'ok',
          texto: `Compra realizada. Código ${compra.codigo} · ${compra.precioTotal} €`,
        });
        this.recarga$.next(false);
      },
      error: (e) => {
        this.comprando.set(null);
        this.ponerMensaje(s.id, {
          tipo: 'error',
          texto: e.error?.message ?? e.error?.mensaje ?? 'No se pudo realizar la compra',
        });
      },
    });
  }

  private ponerMensaje(id: number, mensaje?: Mensaje): void {
    this.mensajes.update((m) => {
      const copia = { ...m };
      if (mensaje) copia[id] = mensaje;
      else delete copia[id];
      return copia;
    });
  }
}
