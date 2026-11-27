import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from './services/auth.service';

/** Componente raíz con la barra de navegación según el rol. */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  auth = inject(AuthService);
  private router = inject(Router);

  salir(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
