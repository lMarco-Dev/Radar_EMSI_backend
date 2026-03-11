package com.emsi.catalogo.service;

import com.emsi.catalogo.dto.CausaRequestDTO;
import com.emsi.catalogo.dto.CausaResponseDTO;
import com.emsi.catalogo.dto.TipoComportamientoRequestDTO;
import com.emsi.catalogo.dto.TipoComportamientoResponseDTO;

import java.util.List;

public interface CatalogoService {
    // --- Tipos de Comportamiento ---
    List<TipoComportamientoResponseDTO> listarTipos();
    TipoComportamientoResponseDTO crearTipo(TipoComportamientoRequestDTO dto);
    TipoComportamientoResponseDTO actualizarTipo(Long id, TipoComportamientoRequestDTO dto);
    void eliminarTipo(Long id);

    // --- Causas ---
    List<CausaResponseDTO> listarCausas();
    CausaResponseDTO crearCausa(CausaRequestDTO dto);
    CausaResponseDTO actualizarCausa(Long id, CausaRequestDTO dto);
    void eliminarCausa(Long id);
}
