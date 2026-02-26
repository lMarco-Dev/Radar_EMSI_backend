package com.emsi.reporte.controller;

import com.emsi.reporte.dto.CambioEstadoDTO;
import com.emsi.reporte.dto.ReporteResponseDTO;
import com.emsi.reporte.service.ReporteService;
import com.emsi.shared.enums.EstadoReporte;
import com.emsi.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReporteResponseDTO>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) EstadoReporte estado,
            @RequestParam(required = false) Long tipoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReporteResponseDTO> result = reporteService.listar(
                empresaId, estado, tipoId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReporteResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(reporteService.obtenerPorId(id)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ReporteResponseDTO>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoDTO dto,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(reporteService.cambiarEstado(id, dto, auth.getName())));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<Map<String, Long>>> estadisticas() {
        return ResponseEntity.ok(ApiResponse.ok(reporteService.obtenerEstadisticas()));
    }
}
