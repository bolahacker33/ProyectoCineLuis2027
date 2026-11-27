import { Component, inject, isDevMode, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AuthService } from '../../services/auth.service';

/** Usuario de prueba para entrar con un clic (solo en desarrollo). */
interface AccesoRapido {
  etiqueta: string;
  username: string;
  password: string;
}

/** Pantalla de inicio de sesión. */
@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule,
  ],
  template: `
    <mat-card class="tarjeta">
      <mat-card-header>
        <mat-card-title>Iniciar sesión</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        @if (cargando()) {
          <mat-progress-bar mode="indeterminate" />
        }
        @if (error()) {
          <p class="error">{{ error() }}</p>
        }
        <form [formGroup]="form" (ngSubmit)="entrar()">
          <mat-form-field appearance="outline">
            <mat-label>Usuario</mat-label>
            <input matInput formControlName="username" autocomplete="username" />
            <mat-error>El usuario es obligatorio</mat-error>
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Contraseña</mat-label>
            <input matInput type="password" formControlName="password" autocomplete="current-password" />
            <mat-error>La contraseña es obligatoria</mat-error>
          </mat-form-field>
          <button mat-flat-button type="submit" [disabled]="cargando()">Entrar</button>
        </form>
        <p>¿No tienes cuenta? <a routerLink="/registro">Regístrate</a></p>

        @if (desarrollo) {
          <div class="rapido">
            <span>Acceso rápido (solo desarrollo)</span>
            <div class="botones">
              @for (a of accesos; track a.username) {
                <button
                  mat-stroked-button
                  type="button"
                  [disabled]="cargando()"
                  (click)="entrarComo(a)"
                >
                  {{ a.etiqueta }}
                </button>
              }
            </div>
          </div>
        }
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .tarjeta { max-width: 400px; margin: 48px auto; padding: 16px; }
    form { display: flex; flex-direction: column; margin-top: 16px; }
    .error { color: #b3261e; }
    .rapido { margin-top: 20px; padding-top: 16px; border-top: 1px dashed #c9cce0; font-size: 13px; color: #666b8a; }
    .botones { display: flex; gap: 8px; margin-top: 8px; flex-wrap: wrap; }
  `,
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  cargando = signal(false);
  error = signal('');

  /** Los accesos rápidos solo existen en desarrollo: quitar antes de entregar. */
  readonly desarrollo = isDevMode();
  readonly accesos: AccesoRapido[] = [
    { etiqueta: 'Admin', username: 'admin', password: 'admin1234' },
    { etiqueta: 'Taquilla', username: 'taquilla', password: '1234' },
    { etiqueta: 'Cliente', username: 'cliente', password: '1234' },
  ];

  form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  /** Rellena el formulario con un usuario de prueba y entra. */
  entrarComo(acceso: AccesoRapido): void {
    this.form.setValue({ username: acceso.username, password: acceso.password });
    this.entrar();
  }

  /** Comprueba las credenciales y entra en la aplicación. */
  entrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.error.set('');
    const { username, password } = this.form.getRawValue();
    this.auth.login(username, password).subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/cartelera']);
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('Usuario o contraseña incorrectos');
      },
    });
  }
}
