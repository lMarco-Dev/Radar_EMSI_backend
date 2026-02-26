package com.emsi.catalogo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "causa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Causa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
