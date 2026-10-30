package es.safareyes.cineluis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SesionDTO {

    private Integer id;
    private String pelicula;
    private Integer sala;
    private Integer capacidad;
    private BigDecimal precioBase;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Integer asientosLibres;

    public SesionDTO() {
    }

    public SesionDTO(Integer id, String pelicula, Integer sala,
                     Integer capacidad, BigDecimal precioBase,
                     LocalDateTime fechaHoraInicio,
                     LocalDateTime fechaHoraFin, Integer asientosLibres) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.asientosLibres = asientosLibres;
    }

    public Integer getId() {
        return id;
    }

    public String getPelicula() {
        return pelicula;
    }

    public Integer getSala() {
        return sala;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public Integer getAsientosLibres() {
        return asientosLibres;
    }
}