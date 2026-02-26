package com.emsi.catalogo.service;

import com.emsi.catalogo.dto.CausaDTO;
import com.emsi.catalogo.dto.TipoComportamientoDTO;

import java.util.List;

public interface CatalogoService {
    List<TipoComportamientoDTO> listarTipos();
    List<CausaDTO> listarCausas();
}
