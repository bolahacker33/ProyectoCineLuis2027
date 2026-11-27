export interface OpcionMenu {
  texto: string;
  ruta: string;
}

/** Clase base de los usuarios de la aplicación. */
export abstract class Usuario {
  constructor(private _username: string) {}

  get username(): string {
    return this._username;
  }

  abstract get rol(): string;

  /** Cada rol muestra un menú distinto (polimorfismo). */
  abstract opcionesMenu(): OpcionMenu[];
}

export class Cliente extends Usuario {
  get rol(): string {
    return 'CLIENTE';
  }

  opcionesMenu(): OpcionMenu[] {
    return [
      { texto: 'Cartelera', ruta: '/cartelera' },
      { texto: 'Sesiones', ruta: '/sesiones' },
      { texto: 'Mis compras', ruta: '/mis-compras' },
    ];
  }
}

export class Taquilla extends Usuario {
  get rol(): string {
    return 'TAQUILLA';
  }

  opcionesMenu(): OpcionMenu[] {
    return [
      { texto: 'Cartelera', ruta: '/cartelera' },
      { texto: 'Taquilla', ruta: '/taquilla' },
      { texto: 'Compras', ruta: '/compras' },
    ];
  }
}

export class Admin extends Usuario {
  get rol(): string {
    return 'ADMIN';
  }

  opcionesMenu(): OpcionMenu[] {
    return [
      { texto: 'Películas', ruta: '/admin/peliculas' },
      { texto: 'Sesiones', ruta: '/admin/sesiones' },
      { texto: 'Informes', ruta: '/admin/informes' },
    ];
  }
}

/** Crea el usuario concreto según el rol que devuelve la API. */
export function crearUsuario(username: string, rol: string): Usuario {
  if (rol === 'ADMIN') return new Admin(username);
  if (rol === 'TAQUILLA') return new Taquilla(username);
  return new Cliente(username);
}
