package com.emsi.catalogo.controller;

import com.emsi.catalogo.dto.*;
import com.emsi.catalogo.service.CatalogoService;
import com.emsi.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    // ==========================================
    // ENDPOINTS TIPOS DE COMPORTAMIENTO
    // ==========================================

    @GetMapping("/tipos")
    public ResponseEntity<ApiResponse<List<TipoComportamientoResponseDTO>>> listarTipos() {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.listarTipos()));
    }

    @PostMapping("/tipos")
    public ResponseEntity<ApiResponse<TipoComportamientoResponseDTO>> crearTipo(@RequestBody @Valid TipoComportamientoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.crearTipo(dto)));
    }

    @PutMapping("/tipos/{id}")
    public ResponseEntity<ApiResponse<TipoComportamientoResponseDTO>> actualizarTipo(@PathVariable Long id, @RequestBody @Valid TipoComportamientoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.actualizarTipo(id, dto)));
    }

    @DeleteMapping("/tipos/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarTipo(@PathVariable Long id) {
        catalogoService.eliminarTipo(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ==========================================
    // ENDPOINTS CAUSAS
    // ==========================================

    @GetMapping("/causas")
    public ResponseEntity<ApiResponse<List<CausaResponseDTO>>> listarCausas() {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.listarCausas()));
    }

    @PostMapping("/causas")
    public ResponseEntity<ApiResponse<CausaResponseDTO>> crearCausa(@RequestBody @Valid CausaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.crearCausa(dto)));
    }

    @PutMapping("/causas/{id}")
    public ResponseEntity<ApiResponse<CausaResponseDTO>> actualizarCausa(@PathVariable Long id, @RequestBody @Valid CausaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.actualizarCausa(id, dto)));
    }

    @DeleteMapping("/causas/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCausa(@PathVariable Long id) {
        catalogoService.eliminarCausa(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}