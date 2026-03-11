package com.emsi.catalogo.repository;

import com.emsi.catalogo.model.TipoComportamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoComportamientoRepository extends JpaRepository<TipoComportamiento, Long> {
    List<TipoComportamiento> findAllByActivoTrue();

    //Verificación de duplicados
    boolean existsByNombreIgnoreCase(String nombre);

    //Verifica si existe el nombre en OTRA causa distinta a la que estamos editando
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
