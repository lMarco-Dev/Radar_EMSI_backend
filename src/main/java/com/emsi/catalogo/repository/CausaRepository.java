package com.emsi.catalogo.repository;

import com.emsi.catalogo.model.Causa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CausaRepository extends JpaRepository<Causa, Long> {
    List<Causa> findAllByActivoTrue();
}
