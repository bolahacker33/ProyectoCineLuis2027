import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Solo deja pasar a usuarios con sesión iniciada. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.estaLogueado() ? true : router.createUrlTree(['/login']);
};

/** Solo deja pasar a usuarios con alguno de los roles indicados. */
export function rolGuard(...roles: string[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const usuario = auth.usuario();
    if (!usuario) return router.createUrlTree(['/login']);
    return roles.includes(usuario.rol) ? true : router.createUrlTree(['/cartelera']);
  };
}
