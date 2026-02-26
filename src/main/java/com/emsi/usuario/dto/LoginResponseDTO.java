package com.emsi.usuario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private String accessToken;
    private String tokenType;
    private String rol;
    private String nombre;
    private String email;
}
