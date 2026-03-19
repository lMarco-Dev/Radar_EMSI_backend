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
import java.util.Optional;

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

    @Query("SELECT DISTINCT r.area FROM Reporte r WHERE r.empresa.id = :empresaId AND r.area IS NOT NULL")
    List<String> findDistinctAreasByEmpresaId(@Param("empresaId") Long empresaId);

    Optional<Reporte> findByFolio(String folio);
    long countByEstado(EstadoReporte estado);
    @Query("SELECT MIN(r.createdAt) FROM Reporte r")
    Optional<java.time.LocalDateTime> obtenerFechaPrimerReporte();
    @Query("SELECT COUNT(r) FROM Reporte r WHERE " +
            "(:empresa IS NULL OR r.empresa.nombre = :empresa) AND " +
            "(:anio IS NULL OR YEAR(r.createdAt) = :anio) AND " +
            "(:mes IS NULL OR MONTH(r.createdAt) = :mes)")
    long countDashboardTotal(@Param("empresa") String empresa, @Param("anio") Integer anio, @Param("mes") Integer mes);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.estado = :estado AND " +
            "(:empresa IS NULL OR r.empresa.nombre = :empresa) AND " +
            "(:anio IS NULL OR YEAR(r.createdAt) = :anio) AND " +
            "(:mes IS NULL OR MONTH(r.createdAt) = :mes)")
    long countDashboardByEstado(@Param("estado") EstadoReporte estado, @Param("empresa") String empresa, @Param("anio") Integer anio, @Param("mes") Integer mes);

    @Query("SELECT r.area as name, COUNT(r) as cantidad FROM Reporte r WHERE " +
            "(:empresa IS NULL OR r.empresa.nombre = :empresa) AND " +
            "(:anio IS NULL OR YEAR(r.createdAt) = :anio) AND " +
            "(:mes IS NULL OR MONTH(r.createdAt) = :mes) " +
            "GROUP BY r.area ORDER BY COUNT(r) DESC")
    List<Map<String, Object>> countDashboardByArea(@Param("empresa") String empresa, @Param("anio") Integer anio, @Param("mes") Integer mes);

    @Query("SELECT r.tipoComportamiento.nombre as name, COUNT(r) as value FROM Reporte r WHERE " +
            "(:empresa IS NULL OR r.empresa.nombre = :empresa) AND " +
            "(:anio IS NULL OR YEAR(r.createdAt) = :anio) AND " +
            "(:mes IS NULL OR MONTH(r.createdAt) = :mes) " +
            "GROUP BY r.tipoComportamiento.nombre")
    List<Map<String, Object>> countDashboardByTipo(@Param("empresa") String empresa, @Param("anio") Integer anio, @Param("mes") Integer mes);

    @Query("SELECT r.empresa.nombre as name, COUNT(r) as cantidad FROM Reporte r WHERE " +
            "(:anio IS NULL OR YEAR(r.createdAt) = :anio) AND " +
            "(:mes IS NULL OR MONTH(r.createdAt) = :mes) " +
            "GROUP BY r.empresa.nombre ORDER BY COUNT(r) DESC")
    List<Map<String, Object>> countDashboardByEmpresas(@Param("anio") Integer anio, @Param("mes") Integer mes);

    @Query("SELECT FUNCTION('MONTHNAME', r.createdAt) as mes, r.estado as estado, COUNT(r) as cantidad " +
            "FROM Reporte r " +
            "WHERE (:empresa IS NULL OR r.empresa.nombre = :empresa) " +
            "GROUP BY FUNCTION('MONTH', r.createdAt), FUNCTION('MONTHNAME', r.createdAt), r.estado " +
            "ORDER BY FUNCTION('MONTH', r.createdAt) ASC")
    List<Map<String, Object>> getTendenciaDashboard(@Param("empresa") String empresa);
}