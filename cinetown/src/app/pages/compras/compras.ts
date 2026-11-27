import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TablaCompras } from '../../components/tabla-compras/tabla-compras';
import { Compra } from '../../models/compra';
import { CompraService } from '../../services/compra.service';

/**
 * Listado de compras. Sirve para dos rutas, según su "data":
 * /mis-compras (cliente) y /compras (taquilla, muestra también el cliente).
 */
@Component({
  selector: 'app-compras',
  imports: [TablaCompras, MatPaginatorModule, MatProgressBarModule],
  template: `
    <header class="cabecera">
      <h1>{{ titulo }}</h1>
      <p>Consulta tus compras y cancela las que no vayas a usar</p>
    </header>

    @if (cargando()) {
      <mat-progress-bar mode="indeterminate" />
    }
    @if (error()) {
      <p class="mensaje error">{{ error() }}</p>
    }
    @if (aviso()) {
      <p class="mensaje" [class.error]="aviso()!.tipo === 'error'">{{ aviso()!.texto }}</p>
    }
    @if (!cargando() && !error() && compras().length === 0) {
      <p class="mensaje">Todavía no hay compras.</p>
    }

    <app-tabla-compras
      [compras]="compras()"
      [mostrarUsuario]="mostrarUsuario"
      [cancelando]="cancelando()"
      (cancelar)="cancelar($event)"
    />

    <mat-paginator
      [length]="total()"
      [pageSize]="tamanio()"
      [pageIndex]="pagina()"
      [pageSizeOptions]="[5, 10, 20]"
      (page)="cambiarPagina($event)"
    />
  `,
  styles: `
    .cabecera h1 { margin: 0; font-size: 34px; color: #14162b; }
    .cabecera p { margin: 4px 0 20px; color: #666b8a; }
    .mensaje { text-align: center; color: #1b6e2d; }
    .mensaje.error { color: #b3261e; }
  `,
})
export class Compras implements OnInit {
  private servicio = inject(CompraService);
  private datos = inject(ActivatedRoute).snapshot.data;

  titulo: string = this.datos['titulo'] ?? 'Compras';
  mostrarUsuario: boolean = !!this.datos['usuario'];

  compras = signal<Compra[]>([]);
  total = signal(0);
  pagina = signal(0);
  tamanio = signal(10);
  cargando = signal(false);
  error = signal('');
  cancelando = signal<string | null>(null);
  aviso = signal<{ tipo: 'ok' | 'error'; texto: string } | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  cambiarPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.tamanio.set(evento.pageSize);
    this.cargar();
  }

  /** Pide confirmación y cancela la compra; después recarga el listado. */
  cancelar(codigo: string): void {
    if (!confirm('¿Seguro que quieres cancelar esta compra?')) return;
    this.cancelando.set(codigo);
    this.aviso.set(null);
    this.servicio.cancelar(codigo).subscribe({
      next: () => {
        this.cancelando.set(null);
        this.aviso.set({ tipo: 'ok', texto: 'Compra cancelada correctamente' });
        this.cargar();
      },
      error: (e) => {
        this.cancelando.set(null);
        this.aviso.set({
          tipo: 'error',
          texto: e.error?.message ?? e.error?.mensaje ?? 'No se pudo cancelar la compra',
        });
      },
    });
  }

  private cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.servicio.listar(this.pagina(), this.tamanio()).subscribe({
      next: (r) => {
        // Las más recientes primero.
        this.compras.set([...r.content].sort((a, b) => b.fechaCompra.localeCompare(a.fechaCompra)));
        this.total.set(r.total);
        this.cargando.set(false);
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('No se pudieron cargar las compras');
      },
    });
  }
}
