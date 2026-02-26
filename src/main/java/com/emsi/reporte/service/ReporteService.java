package com.emsi.reporte.service;

import com.emsi.reporte.dto.CambioEstadoDTO;
import com.emsi.reporte.dto.ReporteRequestDTO;
import com.emsi.reporte.dto.ReporteResponseDTO;
import com.emsi.shared.enums.EstadoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReporteService {
    ReporteResponseDTO crearPublico(String tokenEmpresa, ReporteRequestDTO dto);
    Page<ReporteResponseDTO> listar(Long empresaId, EstadoReporte estado, Long tipoId, Pageable pageable);
    ReporteResponseDTO obtenerPorId(Long id);
    ReporteResponseDTO cambiarEstado(Long id, CambioEstadoDTO dto, String emailUsuario);
    Map<String, Long> obtenerEstadisticas();
}
