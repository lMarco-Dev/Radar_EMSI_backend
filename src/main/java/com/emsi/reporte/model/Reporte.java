package com.emsi.reporte.model;

import com.emsi.catalogo.model.Causa;
import com.emsi.catalogo.model.TipoComportamiento;
import com.emsi.empresa.model.Empresa;
import com.emsi.shared.enums.EstadoReporte;
import com.emsi.shared.enums.Turno;
import com.emsi.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporte")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_comportamiento_id", nullable = false)
    private TipoComportamiento tipoComportamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "causa_id")
    private Causa causa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    @Column(name = "nombre_reportante", length = 200)
    private String nombreReportante;

    @Column(length = 200)
    private String area;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Turno turno;

    @Column(name = "descripcion_comportamiento", columnDefinition = "TEXT")
    private String descripcionComportamiento;

    @Column(name = "medida_contencion", columnDefinition = "TEXT")
    private String medidaContencion;

    @Column(name = "fecha_ocurrido")
    private LocalDate fechaOcurrido;

    @Column(name = "lugar_especifico", length = 300)
    private String lugarEspecifico;

    @Column(name = "campos_dinamicos", columnDefinition = "JSON")
    private String camposDinamicos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisado_por")
    private Usuario revisadoPor;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
