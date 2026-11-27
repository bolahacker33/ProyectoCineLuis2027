package es.safareyes.cineluis.modelos;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "pelicula_genero")
@IdClass(PeliculaGenero.PeliculaGeneroId.class)
public class PeliculaGenero {

    @Id
    @Column(name = "pelicula_id")
    private Integer peliculaId;

    @Id
    @Column(name = "genero_id")
    private Integer generoId;

    public PeliculaGenero() {
    }

    public Integer getPeliculaId() { return peliculaId; }
    public void setPeliculaId(Integer peliculaId) { this.peliculaId = peliculaId; }
    public Integer getGeneroId() { return generoId; }
    public void setGeneroId(Integer generoId) { this.generoId = generoId; }

    public static class PeliculaGeneroId implements Serializable {
        private Integer peliculaId;
        private Integer generoId;

        public PeliculaGeneroId() {
        }

        public PeliculaGeneroId(Integer peliculaId, Integer generoId) {
            this.peliculaId = peliculaId;
            this.generoId = generoId;
        }

        public Integer getPeliculaId() { return peliculaId; }
        public void setPeliculaId(Integer peliculaId) { this.peliculaId = peliculaId; }
        public Integer getGeneroId() { return generoId; }
        public void setGeneroId(Integer generoId) { this.generoId = generoId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PeliculaGeneroId that)) return false;
            return Objects.equals(peliculaId, that.peliculaId)
                    && Objects.equals(generoId, that.generoId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(peliculaId, generoId);
        }
    }
}
