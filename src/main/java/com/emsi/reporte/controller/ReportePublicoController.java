package com.emsi.reporte.controller;

import com.emsi.catalogo.service.CatalogoService;
import com.emsi.empresa.repository.EmpresaRepository;
import com.emsi.reporte.dto.ReporteRequestDTO;
import com.emsi.reporte.dto.ReporteResponseDTO;
import com.emsi.reporte.service.ReporteService;
import com.emsi.shared.exception.TokenInvalidoException;
import com.emsi.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class ReportePublicoController {

    private final ReporteService reporteService;
    private final CatalogoService catalogoService;
    private final EmpresaRepository empresaRepository;

    @GetMapping("/empresa/{token}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerEmpresaPorToken(@PathVariable String token) {
        var empresa = empresaRepository.findByTokenPublicoAndActivoTrue(token)
                .orElseThrow(() -> new TokenInvalidoException("Token invalido o empresa inactiva"));
        Map<String, Object> data = new HashMap<>();
        data.put("id", empresa.getId());
        data.put("nombre", empresa.getNombre());
        data.put("logoUrl", empresa.getLogoUrl());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/catalogos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerCatalogos() {
        Map<String, Object> catalogos = new HashMap<>();
        catalogos.put("tipos", catalogoService.listarTipos());
        catalogos.put("causas", catalogoService.listarCausas());
        return ResponseEntity.ok(ApiResponse.ok(catalogos));
    }

    @PostMapping("/reportes")
    public ResponseEntity<ApiResponse<ReporteResponseDTO>> crearReporte(
            @RequestHeader("X-Empresa-Token") String token,
            @Valid @RequestBody ReporteRequestDTO dto) {
        ReporteResponseDTO reporte = reporteService.crearPublico(token, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Reporte registrado exitosamente", reporte));
    }
}
