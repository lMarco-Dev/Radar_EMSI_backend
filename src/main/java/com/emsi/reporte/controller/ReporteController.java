package com.emsi.reporte.controller;

import com.emsi.reporte.dto.CambioEstadoDTO;
import com.emsi.reporte.dto.DashboardDTO;
import com.emsi.reporte.dto.ReporteResponseDTO;
import com.emsi.reporte.model.Reporte;
import com.emsi.reporte.repository.ReporteRepository;
import com.emsi.reporte.service.ExcelService;
import com.emsi.reporte.service.ReporteService;
import com.emsi.shared.enums.EstadoReporte;
import com.emsi.shared.response.ApiResponse;
import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ExcelService excelService;
    private final ReporteRepository reporteRepository;

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
    public ResponseEntity<ApiResponse<DashboardDTO>> estadisticas(
            @RequestParam(required = false) String empresa) {
        return ResponseEntity.ok(ApiResponse.ok(reporteService.obtenerEstadisticasCompletas(empresa)));
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<Resource> exportarExcel(@RequestParam(required = false) String empresaNombre) {

        // 1. Obtener la lista de reportes (filtrada o completa)
        List<Reporte> reportes;
        if (empresaNombre != null && !empresaNombre.isEmpty() && !"Todos".equalsIgnoreCase(empresaNombre)) {
            // Nota: Si no tienes este método en tu repositorio, puedes buscar por ID o traer todos y filtrar
            reportes = reporteRepository.findAll().stream()
                    .filter(r -> r.getEmpresa().getNombre().equalsIgnoreCase(empresaNombre))
                    .toList();
        } else {
            reportes = reporteRepository.findAll();
        }

        // 2. Generar el Excel
        ByteArrayInputStream in = excelService.exportarReportes(reportes);

        // 3. Configurar las cabeceras para que el navegador lo descargue como archivo
        String filename = "Reportes_SST_" + java.time.LocalDate.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
}
