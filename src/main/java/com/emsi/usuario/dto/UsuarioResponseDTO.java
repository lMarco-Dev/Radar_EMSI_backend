package com.emsi.usuario.dto;

import com.emsi.shared.enums.RolUsuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private RolUsuario rol;
    private Boolean activo;
    private Long empresaId;
    private String empresaNombre;
    private LocalDateTime createdAt;
}
