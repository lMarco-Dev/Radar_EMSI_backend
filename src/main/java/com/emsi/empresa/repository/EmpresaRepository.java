package com.emsi.empresa.repository;

import com.emsi.empresa.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByTokenPublicoAndActivoTrue(String tokenPublico);
    List<Empresa> findAllByActivoTrue();
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByRuc(String ruc);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    boolean existsByRucAndIdNot(String ruc, Long id);
}
