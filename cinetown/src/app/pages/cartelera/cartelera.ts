import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { Pelicula } from '../../models/pelicula';
import { PeliculaService } from '../../services/pelicula.service';

/** Listado de películas con filtros y paginación. */
@Component({
  selector: 'app-cartelera',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatPaginatorModule,
    MatProgressBarModule,
  ],
  template: `
    <header class="cabecera">
      <h1>Cartelera</h1>
      <p>Elige tu película y reserva tus entradas</p>
    </header>

    <mat-card class="panel">
      <form class="filtros" [formGroup]="form" (ngSubmit)="buscar()">
        <mat-form-field appearance="outline" subscriptSizing="dynamic">
          <mat-label>Título</mat-label>
          <input matInput formControlName="titulo" />
        </mat-form-field>
        <mat-form-field appearance="outline" subscriptSizing="dynamic">
          <mat-label>Género</mat-label>
          <input matInput formControlName="genero" />
        </mat-form-field>
        <mat-form-field appearance="outline" subscriptSizing="dynamic">
          <mat-label>Clasificación</mat-label>
          <mat-select formControlName="clasificacion">
            <mat-option value="">Todas</mat-option>
            <mat-option value="TP">Todos los públicos</mat-option>
            <mat-option value="MAYORES_12">Mayores 12</mat-option>
            <mat-option value="MAYORES_16">Mayores 16</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="outline" subscriptSizing="dynamic">
          <mat-label>Estado</mat-label>
          <mat-select formControlName="cartelera">
            <mat-option value="">Todas</mat-option>
            <mat-option value="true">En cartelera</mat-option>
            <mat-option value="false">Fuera de cartelera</mat-option>
          </mat-select>
        </mat-form-field>
        <button mat-flat-button type="submit">Buscar</button>
        <button mat-stroked-button type="button" (click)="limpiar()">Limpiar</button>
      </form>
    </mat-card>

    @if (cargando()) {
      <mat-progress-bar mode="indeterminate" />
    }
    @if (error()) {
      <p class="mensaje error">{{ error() }}</p>
    }
    @if (!cargando() && !error() && peliculas().length === 0) {
      <p class="mensaje">No hay películas con esos filtros.</p>
    }

    <div class="rejilla">
      @for (p of peliculas(); track p.id) {
        <mat-card class="pelicula">
          <div class="cartel" [style.background]="fondo(p.id)">
            <span class="inicial">{{ p.titulo.charAt(0) }}</span>
            <span class="estado" [class.on]="p.cartelera">
              {{ p.cartelera ? 'En cartelera' : 'Fuera de cartelera' }}
            </span>
          </div>
          <mat-card-content>
            <h3>{{ p.titulo }}</h3>
            <p class="datos">{{ p.anio }} · {{ p.duracion }} min · {{ etiqueta(p.clasificacion) }}</p>
            <div class="generos">
              @for (g of unicos(p.generos); track g) {
                <span class="etiqueta">{{ g }}</span>
              }
            </div>
            <p class="sinopsis">{{ p.sinopsis }}</p>
          </mat-card-content>
        </mat-card>
      }
    </div>

    <mat-paginator
      [length]="total()"
      [pageSize]="tamanio()"
      [pageIndex]="pagina()"
      [pageSizeOptions]="[5, 10, 20]"
      (page)="cambiarPagina($event)"
    />
  `,
  styles: `
    .cabecera h1 {
      margin: 0;
      font-size: 34px;
      color: #14162b;
    }
    .cabecera p {
      margin: 4px 0 20px;
      color: #666b8a;
    }
    .panel {
      padding: 16px;
      margin-bottom: 16px;
    }
    .filtros {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      align-items: center;
    }
    .rejilla {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(290px, 1fr));
      gap: 20px;
      margin: 20px 0;
    }
    .pelicula {
      overflow: hidden;
      transition: transform 0.15s, box-shadow 0.15s;
    }
    .pelicula:hover {
      transform: translateY(-4px);
      box-shadow: 0 10px 24px rgba(20, 22, 43, 0.18);
    }
    .cartel {
      height: 130px;
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .inicial {
      font-size: 72px;
      font-weight: 800;
      color: rgba(255, 255, 255, 0.85);
    }
    .estado {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(0, 0, 0, 0.55);
      color: #fff;
      font-size: 12px;
      padding: 4px 10px;
      border-radius: 12px;
    }
    .estado.on {
      background: #1b9e4b;
    }
    h3 {
      margin: 14px 0 4px;
      font-size: 19px;
    }
    .datos {
      margin: 0 0 10px;
      color: #666b8a;
      font-size: 14px;
    }
    .generos {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 10px;
    }
    .etiqueta {
      background: #e9ebf8;
      color: #2b2f5e;
      font-size: 12px;
      padding: 3px 10px;
      border-radius: 12px;
    }
    .sinopsis {
      margin: 0;
      font-size: 14px;
      line-height: 1.4;
    }
    .mensaje {
      text-align: center;
      color: #666b8a;
    }
    .error {
      color: #b3261e;
    }
  `,
})
export class Cartelera implements OnInit {
  private fb = inject(FormBuilder);
  private servicio = inject(PeliculaService);

  peliculas = signal<Pelicula[]>([]);
  total = signal(0);
  pagina = signal(0);
  tamanio = signal(10);
  cargando = signal(false);
  error = signal('');

  form = this.fb.nonNullable.group({
    titulo: [''],
    genero: [''],
    clasificacion: [''],
    cartelera: ['true'],
  });

  ngOnInit(): void {
    this.cargar();
  }

  /** Aplica los filtros desde la primera página. */
  buscar(): void {
    this.pagina.set(0);
    this.cargar();
  }

  limpiar(): void {
    this.form.reset({ titulo: '', genero: '', clasificacion: '', cartelera: '' });
    this.buscar();
  }

  cambiarPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.tamanio.set(evento.pageSize);
    this.cargar();
  }

  /** Elimina géneros repetidos. */
  unicos(generos: string[]): string[] {
    return [...new Set(generos)];
  }

  etiqueta(clasificacion: string): string {
    return clasificacion === 'TP' ? 'TP' : clasificacion.replace('MAYORES_', '+');
  }

  /** Degradado de color propio para cada película, calculado a partir de su id. */
  fondo(id: number): string {
    const tono = (id * 47) % 360;
    return `linear-gradient(135deg, hsl(${tono}, 60%, 35%), hsl(${(tono + 50) % 360}, 65%, 55%))`;
  }

  private cargar(): void {
    const f = this.form.getRawValue();
    this.cargando.set(true);
    this.error.set('');
    this.servicio
      .listar(
        {
          titulo: f.titulo.trim(),
          genero: f.genero.trim(),
          clasificacion: f.clasificacion,
          cartelera: f.cartelera === '' ? undefined : f.cartelera === 'true',
        },
        this.pagina(),
        this.tamanio(),
      )
      .subscribe({
        next: (r) => {
          this.peliculas.set(r.content);
          this.total.set(r.total);
          this.cargando.set(false);
        },
        error: () => {
          this.cargando.set(false);
          this.error.set('No se pudieron cargar las películas');
        },
      });
  }
}
