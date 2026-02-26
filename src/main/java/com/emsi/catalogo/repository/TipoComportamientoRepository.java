package com.emsi.catalogo.repository;

import com.emsi.catalogo.model.TipoComportamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoComportamientoRepository extends JpaRepository<TipoComportamiento, Long> {
    List<TipoComportamiento> findAllByActivoTrue();
}
