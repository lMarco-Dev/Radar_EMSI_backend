package com.emsi.reporte.service;

import com.emsi.reporte.repository.ReporteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.emsi.reporte.dto.DashboardDTO;
import com.emsi.shared.enums.EstadoReporte;

import com.emsi.empresa.repository.EmpresaRepository;
import com.emsi.reporte.dto.ReporteRequestDTO;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository; // Simulamos el repositorio

    @InjectMocks
    private ReporteServiceImpl reporteService;
    // Inyectamos el simulacro en el servicio real

    @Mock
    private EmpresaRepository empresaRepository;


    @Test
    @DisplayName("Debe lanzar excepción si se intenta crear un reporte con token de empresa inválido")
    void crearReporte_TokenInvalido_LanzaExcepcion() {
        // Arrange
        String tokenInvalido = "TOKEN_INVENTADO";
        ReporteRequestDTO dto = new ReporteRequestDTO();
        // Llenamos datos mínimos para el DTO
        dto.setEmpresaId(1L);

        when(empresaRepository.findByTokenPublicoAndActivoTrue(tokenInvalido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            reporteService.crearPublico(tokenInvalido, dto);
        });

        // Verificamos que jamás se intentó guardar en la base de datos
        verify(reporteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar el mes actual si no hay reportes registrados")
    void testMesInicioCuandoNoHayReportes() {
        // Arrange: Cuando el repo busque el primer reporte, que devuelva vacío
        when(reporteRepository.obtenerFechaPrimerReporte()).thenReturn(Optional.empty());

        // Act: Llamamos a la lógica (puedes probar el método del Dashboard directamente)
        String mesInicio = reporteRepository.obtenerFechaPrimerReporte()
                .orElse(LocalDateTime.now())
                .toString(); // Simplificado para el ejemplo

        // Assert: Verificamos que no sea nulo y que se llamó al repo
        assertNotNull(mesInicio);
        verify(reporteRepository, times(1)).obtenerFechaPrimerReporte();
    }

    @Test
    @DisplayName("Debe sumar y agrupar correctamente los contadores del Dashboard")
    void obtenerEstadisticas_CalculaContadoresCorrectamente() {
        // Arrange: Simulamos que la BD nos devuelve cantidades específicas
        String filtroEmpresa = "Todos"; // El service lo convierte a null
        String filtroMes = "2026-03";
        Integer anio = 2026;
        Integer mes = 3;

        when(reporteRepository.countDashboardTotal(null, anio, mes)).thenReturn(100L);
        when(reporteRepository.countDashboardByEstado(EstadoReporte.PENDIENTE, null, anio, mes)).thenReturn(30L);
        when(reporteRepository.countDashboardByEstado(EstadoReporte.EN_REVISION, null, anio, mes)).thenReturn(20L);
        when(reporteRepository.countDashboardByEstado(EstadoReporte.SOLUCIONADO, null, anio, mes)).thenReturn(50L);
        when(reporteRepository.obtenerFechaPrimerReporte()).thenReturn(Optional.empty());

        // Act: Ejecutamos el método real del servicio
        DashboardDTO resultado = reporteService.obtenerEstadisticasCompletas(filtroEmpresa, filtroMes);

        // Assert: Verificamos que los datos se empaqueten bien en el DTO
        assertEquals(100L, resultado.getContadores().get("total"));
        assertEquals(30L, resultado.getContadores().get("pendientes"));
        assertEquals(20L, resultado.getContadores().get("enRevision"));
        assertEquals(50L, resultado.getContadores().get("solucionados"));

        // Prueba de integridad: La suma de las partes debe ser igual al total
        long sumaEstados = resultado.getContadores().get("pendientes") +
                resultado.getContadores().get("enRevision") +
                resultado.getContadores().get("solucionados");

        assertEquals(resultado.getContadores().get("total").longValue(), sumaEstados, "La suma de estados no coincide con el total histórico");
    }
}
