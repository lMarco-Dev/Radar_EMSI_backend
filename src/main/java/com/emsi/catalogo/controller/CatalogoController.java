package com.emsi.catalogo.controller;

import com.emsi.catalogo.dto.CausaDTO;
import com.emsi.catalogo.dto.TipoComportamientoDTO;
import com.emsi.catalogo.service.CatalogoService;
import com.emsi.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/tipos")
    public ResponseEntity<ApiResponse<List<TipoComportamientoDTO>>> listarTipos() {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.listarTipos()));
    }

    @GetMapping("/causas")
    public ResponseEntity<ApiResponse<List<CausaDTO>>> listarCausas() {
        return ResponseEntity.ok(ApiResponse.ok(catalogoService.listarCausas()));
    }
}
