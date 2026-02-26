package com.emsi.catalogo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_comportamiento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoComportamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
