import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { AuthService } from '../../services/auth.service';

/** Pantalla de registro de nuevos clientes. */
@Component({
  selector: 'app-registro',
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
        <mat-card-title>Crear cuenta</mat-card-title>
      </mat-card-header>
      <mat-card-content>
        @if (cargando()) {
          <mat-progress-bar mode="indeterminate" />
        }
        @if (error()) {
          <p class="error">{{ error() }}</p>
        }
        @if (exito()) {
          <p class="exito">{{ exito() }}</p>
        }
        <form [formGroup]="form" (ngSubmit)="registrar()">
          <mat-form-field appearance="outline">
            <mat-label>Usuario</mat-label>
            <input matInput formControlName="username" autocomplete="username" />
            <mat-error>El usuario es obligatorio</mat-error>
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Contraseña</mat-label>
            <input matInput type="password" formControlName="password" autocomplete="new-password" />
            <mat-error>La contraseña debe tener al menos 4 caracteres</mat-error>
          </mat-form-field>
          <button mat-flat-button type="submit" [disabled]="cargando()">Registrarme</button>
        </form>
        <p>¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a></p>
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .tarjeta {
      max-width: 400px;
      margin: 48px auto;
      padding: 16px;
    }
    form {
      display: flex;
      flex-direction: column;
      margin-top: 16px;
    }
    .error {
      color: #b3261e;
    }
    .exito {
      color: #1b6e2d;
    }
  `,
})
export class Registro {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  cargando = signal(false);
  error = signal('');
  exito = signal('');

  form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', [Validators.required, Validators.minLength(4)]],
  });

  /** Crea la cuenta e inicia sesión con ella. */
  registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.error.set('');
    const { username, password } = this.form.getRawValue();
    this.auth
      .registro(username, password)
      .pipe(switchMap(() => this.auth.login(username, password)))
      .subscribe({
        next: () => {
          this.exito.set('Cuenta creada correctamente');
          this.router.navigate(['/cartelera']);
        },
        error: (e) => {
          this.cargando.set(false);
          this.error.set(e.status === 409 ? 'Ese usuario ya existe' : 'No se pudo crear la cuenta');
        },
      });
  }
}
