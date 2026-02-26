package com.emsi.empresa.repository;

import com.emsi.empresa.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByTokenPublicoAndActivoTrue(String tokenPublico);
    List<Empresa> findAllByActivoTrue();
}
