package com.emsi.catalogo.service;

import com.emsi.catalogo.dto.CausaDTO;
import com.emsi.catalogo.dto.TipoComportamientoDTO;
import com.emsi.catalogo.repository.CausaRepository;
import com.emsi.catalogo.repository.TipoComportamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final TipoComportamientoRepository tipoRepository;
    private final CausaRepository causaRepository;

    @Override
    public List<TipoComportamientoDTO> listarTipos() {
        return tipoRepository.findAllByActivoTrue().stream()
                .map(t -> TipoComportamientoDTO.builder()
                        .id(t.getId()).nombre(t.getNombre())
                        .descripcion(t.getDescripcion()).activo(t.getActivo()).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CausaDTO> listarCausas() {
        return causaRepository.findAllByActivoTrue().stream()
                .map(c -> CausaDTO.builder().id(c.getId()).nombre(c.getNombre()).activo(c.getActivo()).build())
                .collect(Collectors.toList());
    }
}
