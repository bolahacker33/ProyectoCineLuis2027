package es.safareyes.cineluis.modelos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cod_compra")
    private Integer codCompra;

    @Column(name = "parking_usado")
    private Boolean parkingUsado;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    @Column(name = "num_entradas")
    private Integer numEntradas;

    @Column(name = "precio_total")
    private BigDecimal precioTotal;

    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_sesion", referencedColumnName = "id")
    private Sesion sesion;

    public Compra() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCodCompra() { return codCompra; }
    public void setCodCompra(Integer codCompra) { this.codCompra = codCompra; }
    public Boolean getParkingUsado() { return parkingUsado; }
    public void setParkingUsado(Boolean parkingUsado) { this.parkingUsado = parkingUsado; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }
    public Integer getNumEntradas() { return numEntradas; }
    public void setNumEntradas(Integer numEntradas) { this.numEntradas = numEntradas; }
    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Sesion getSesion() { return sesion; }
    public void setSesion(Sesion sesion) { this.sesion = sesion; }
}
