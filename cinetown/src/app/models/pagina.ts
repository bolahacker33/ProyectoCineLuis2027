/** Página de resultados que devuelve la API. */
export interface Pagina<T> {
  content: T[];
  total: number;
}
