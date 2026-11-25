package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.GeneroDTO;
import es.safareyes.cineluis.repositorios.IGeneroRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GeneroService implements IGeneroService {

    private final IGeneroRepository generoRepository;

    @Override
    public List<GeneroDTO> listarGeneros() {
        return generoRepository.buscarGenerosConNumeroPeliculas();
    }
}
