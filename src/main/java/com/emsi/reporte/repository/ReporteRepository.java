package com.emsi.reporte.repository;

import com.emsi.reporte.model.Reporte;
import com.emsi.shared.enums.EstadoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query("SELECT r FROM Reporte r WHERE " +
            "(:empresaId IS NULL OR r.empresa.id = :empresaId) AND " +
            "(:estado IS NULL OR r.estado = :estado) AND " +
            "(:tipoId IS NULL OR r.tipoComportamiento.id = :tipoId)")
    Page<Reporte> buscarConFiltros(
            @Param("empresaId") Long empresaId,
            @Param("estado") EstadoReporte estado,
            @Param("tipoId") Long tipoId,
            Pageable pageable);

    long countByEstado(EstadoReporte estado);

    @Query("SELECT DISTINCT r.area FROM Reporte r WHERE r.empresa.id = :empresaId AND r.area IS NOT NULL")
    List<String> findDistinctAreasByEmpresaId(@Param("empresaId") Long empresaId);

    // ==========================================
    // GLOBALES (Para cuando se selecciona "Todos")
    // ==========================================
    @Query("SELECT r.area as name, COUNT(r) as cantidad FROM Reporte r GROUP BY r.area ORDER BY COUNT(r) DESC")
    List<Map<String, Object>> countByArea();

    @Query("SELECT r.empresa.nombre as name, COUNT(r) as cantidad FROM Reporte r GROUP BY r.empresa.nombre ORDER BY COUNT(r) DESC")
    List<Map<String, Object>> countByEmpresa();

    @Query("SELECT r.tipoComportamiento.nombre as name, COUNT(r) as value FROM Reporte r GROUP BY r.tipoComportamiento.nombre")
    List<Map<String, Object>> countByTipo();

    @Query("SELECT FUNCTION('MONTHNAME', r.createdAt) as mes, COUNT(r) as incidentes FROM Reporte r GROUP BY FUNCTION('MONTH', r.createdAt), FUNCTION('MONTHNAME', r.createdAt) ORDER BY FUNCTION('MONTH', r.createdAt) ASC")
    List<Map<String, Object>> getTendenciaMensual();

    // ==========================================
    // ESPECÍFICAS (Para cuando se filtra por Empresa)
    // ==========================================
    long countByEmpresa_Nombre(String empresaNombre);

    long countByEstadoAndEmpresa_Nombre(EstadoReporte estado, String empresaNombre);

    @Query("SELECT r.area as name, COUNT(r) as cantidad FROM Reporte r WHERE r.empresa.nombre = :empresaNombre GROUP BY r.area ORDER BY COUNT(r) DESC")
    List<Map<String, Object>> countByAreaFiltro(@Param("empresaNombre") String empresaNombre);

    @Query("SELECT r.tipoComportamiento.nombre as name, COUNT(r) as value FROM Reporte r WHERE r.empresa.nombre = :empresaNombre GROUP BY r.tipoComportamiento.nombre")
    List<Map<String, Object>> countByTipoFiltro(@Param("empresaNombre") String empresaNombre);

    @Query("SELECT FUNCTION('MONTHNAME', r.createdAt) as mes, COUNT(r) as incidentes FROM Reporte r WHERE r.empresa.nombre = :empresaNombre GROUP BY FUNCTION('MONTH', r.createdAt), FUNCTION('MONTHNAME', r.createdAt) ORDER BY FUNCTION('MONTH', r.createdAt) ASC")
    List<Map<String, Object>> getTendenciaMensualFiltro(@Param("empresaNombre") String empresaNombre);
}