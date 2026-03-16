package com.emsi.usuario.dto;

import com.emsi.shared.enums.RolUsuario;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequestDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$", message = "La clave debe tener 8 caracteres, un número y una mayúscula")
    private String password;

    @NotNull
    private RolUsuario rol;

    private Long empresaId;
}
