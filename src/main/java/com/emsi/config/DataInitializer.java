package com.emsi.config;

import com.emsi.shared.enums.RolUsuario;
import com.emsi.usuario.model.Usuario;
import com.emsi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.nombre}")
    private String adminNombre;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPass;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = Usuario.builder()
                    .nombre(adminNombre)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPass))
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .build();

            usuarioRepository.save(admin);
            System.out.println("Super Admin creado exitosamente desde .env");
        }
    }
}