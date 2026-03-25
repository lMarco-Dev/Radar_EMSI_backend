package com.emsi.usuario.service;

import com.emsi.shared.exception.ResourceNotFoundException;
import com.emsi.usuario.dto.LoginRequestDTO;
import com.emsi.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    @DisplayName("Debe lanzar excepción si el usuario está inactivo o no existe al loguearse")
    void login_UsuarioInactivo_LanzaExcepcion() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("inactivo@emsi.com");
        dto.setPassword("password123");

        // Simulamos que el AuthenticationManager no bloquea, pero la BD dice que está inactivo (Optional.empty)
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(usuarioRepository.findByEmailAndActivoTrue(dto.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.login(dto);
        });
    }
}