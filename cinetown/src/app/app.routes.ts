import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { Cartelera } from './pages/cartelera/cartelera';
import { Compras } from './pages/compras/compras';
import { Login } from './pages/login/login';
import { Registro } from './pages/registro/registro';
import { Sesiones } from './pages/sesiones/sesiones';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'cartelera', component: Cartelera, canActivate: [authGuard] },
  { path: 'sesiones', component: Sesiones, canActivate: [authGuard] },
  {
    path: 'mis-compras',
    component: Compras,
    canActivate: [authGuard],
    data: { titulo: 'Mis compras', usuario: false },
  },
  {
    path: 'compras',
    component: Compras,
    canActivate: [authGuard],
    data: { titulo: 'Compras', usuario: true },
  },
  // Provisional: la taquilla vende con la misma pantalla de sesiones hasta tener la suya.
  { path: 'taquilla', component: Sesiones, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' },
];
