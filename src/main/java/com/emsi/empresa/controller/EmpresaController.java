package com.emsi.empresa.controller;

import com.emsi.empresa.dto.EmpresaRequestDTO;
import com.emsi.empresa.dto.EmpresaResponseDTO;
import com.emsi.empresa.dto.QrInfoDTO;
import com.emsi.empresa.service.EmpresaService;
import com.emsi.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpresaResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.listarTodas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> crear(@Valid @RequestBody EmpresaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Empresa creada exitosamente", empresaService.crear(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> actualizar(@PathVariable Long id,
                                                                       @Valid @RequestBody EmpresaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Empresa actualizada", empresaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        empresaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Empresa desactivada", null));
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<ApiResponse<QrInfoDTO>> obtenerQr(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.obtenerQrInfo(id)));
    }
}
