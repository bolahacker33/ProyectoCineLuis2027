package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.modelos.Sala;
import es.safareyes.cineluis.repositorios.ISalaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SalaService implements ISalaService {

    private final ISalaRepository salaRepository;

    @Override
    public List<Sala> listar() {
        return salaRepository.findAll(Sort.by("numero"));
    }
}
