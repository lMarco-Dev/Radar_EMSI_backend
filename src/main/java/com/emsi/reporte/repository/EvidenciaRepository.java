package com.emsi.reporte.repository;

import com.emsi.reporte.model.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {
    List<Evidencia> findByReporteId(Long reporteId);
}
