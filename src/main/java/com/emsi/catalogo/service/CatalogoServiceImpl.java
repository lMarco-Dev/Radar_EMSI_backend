package com.emsi.catalogo.service;

import com.emsi.catalogo.dto.*;
import com.emsi.catalogo.model.Causa;
import com.emsi.catalogo.model.TipoComportamiento;
import com.emsi.catalogo.repository.CausaRepository;
import com.emsi.catalogo.repository.TipoComportamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final TipoComportamientoRepository tipoRepository;
    private final CausaRepository causaRepository;

    // ==========================================
    // TIPOS DE COMPORTAMIENTO
    // ==========================================

    @Override
    public List<TipoComportamientoResponseDTO> listarTipos() {
        return tipoRepository.findAllByActivoTrue().stream()
                .map(t -> TipoComportamientoResponseDTO.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .descripcion(t.getDescripcion())
                        .activo(t.getActivo())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TipoComportamientoResponseDTO crearTipo(TipoComportamientoRequestDTO dto) {
        if(tipoRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RuntimeException("El tipo de comportamiento ya existe");
        }

        TipoComportamiento tipo = TipoComportamiento.builder()
                .nombre(dto.getNombre().trim())
                .descripcion(dto.getDescripcion())
                .build();

        tipo = tipoRepository.save(tipo);

        return TipoComportamientoResponseDTO.builder()
                .id(tipo.getId())
                .nombre(tipo.getNombre())
                .descripcion(tipo.getDescripcion())
                .activo(tipo.getActivo())
                .build();
    }

    @Override
    @Transactional
    public TipoComportamientoResponseDTO actualizarTipo(Long id, TipoComportamientoRequestDTO dto) {
        TipoComportamiento tipo = tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de comportamiento no encontrado"));

        if(tipoRepository.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(), id)) {
            throw new RuntimeException("El nombre ya está en uso por otro tipo de comportamiento");
        }

        tipo.setNombre(dto.getNombre().trim());
        tipo.setDescripcion(dto.getDescripcion());
        tipo = tipoRepository.save(tipo);

        return TipoComportamientoResponseDTO.builder()
                .id(tipo.getId())
                .nombre(tipo.getNombre())
                .descripcion(tipo.getDescripcion())
                .activo(tipo.getActivo())
                .build();
    }

    @Override
    @Transactional
    public void eliminarTipo(Long id) {
        TipoComportamiento tipo = tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de comportamiento no encontrado"));
        tipo.setActivo(false);
        tipoRepository.save(tipo);
    }

    // ==========================================
    // CAUSAS
    // ==========================================

    @Override
    public List<CausaResponseDTO> listarCausas() {
        return causaRepository.findAllByActivoTrue().stream()
                .map(c -> CausaResponseDTO.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .activo(c.getActivo())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CausaResponseDTO crearCausa(CausaRequestDTO dto) {
        if(causaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RuntimeException("La causa ya existe");
        }

        Causa causa = Causa.builder()
                .nombre(dto.getNombre().trim())
                .build();

        causa = causaRepository.save(causa);

        return CausaResponseDTO.builder()
                .id(causa.getId())
                .nombre(causa.getNombre())
                .activo(causa.getActivo())
                .build();
    }

    @Override
    @Transactional
    public CausaResponseDTO actualizarCausa(Long id, CausaRequestDTO dto) {
        Causa causa = causaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Causa no encontrada"));

        if(causaRepository.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(), id)) {
            throw new RuntimeException("El nombre ya está en uso por otra causa");
        }

        causa.setNombre(dto.getNombre().trim());
        causa = causaRepository.save(causa);

        return CausaResponseDTO.builder()
                .id(causa.getId())
                .nombre(causa.getNombre())
                .activo(causa.getActivo())
                .build();
    }

    @Override
    @Transactional
    public void eliminarCausa(Long id) {
        Causa causa = causaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Causa no encontrada"));
        causa.setActivo(false);
        causaRepository.save(causa);
    }
}