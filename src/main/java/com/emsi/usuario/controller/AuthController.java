package com.emsi.usuario.controller;

import com.emsi.shared.response.ApiResponse;
import com.emsi.usuario.dto.LoginRequestDTO;
import com.emsi.usuario.dto.LoginResponseDTO;
import com.emsi.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.login(dto)));
    }
}
