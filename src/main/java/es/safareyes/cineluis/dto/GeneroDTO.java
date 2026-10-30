package es.safareyes.cineluis.dto;

public class GeneroDTO {

    private String nombre;
    private Long numeroPeliculas;

    public GeneroDTO() {
    }

    public GeneroDTO(String nombre, Long numeroPeliculas) {
        this.nombre = nombre;
        this.numeroPeliculas = numeroPeliculas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getNumeroPeliculas() {
        return numeroPeliculas;
    }

    public void setNumeroPeliculas(Long numeroPeliculas) {
        this.numeroPeliculas = numeroPeliculas;
    }
}