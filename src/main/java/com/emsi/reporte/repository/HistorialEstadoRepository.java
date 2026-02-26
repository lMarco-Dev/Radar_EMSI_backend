package com.emsi.reporte.repository;

import com.emsi.reporte.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findByReporteIdOrderByFechaCambioDesc(Long reporteId);
}
