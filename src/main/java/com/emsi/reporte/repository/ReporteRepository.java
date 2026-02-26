package com.emsi.reporte.repository;

import com.emsi.reporte.model.Reporte;
import com.emsi.shared.enums.EstadoReporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
