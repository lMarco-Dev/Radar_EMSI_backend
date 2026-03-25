package com.emsi.config;

import com.emsi.usuario.model.Usuario;
import com.emsi.usuario.repository.UsuarioRepository;
import com.emsi.shared.enums.RolUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.nombre}")
    private String adminNombre;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Base de datos vacía. Creando super usuario inicial...");

            Usuario admin = Usuario.builder()
                    .nombre(adminNombre)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .build();

            usuarioRepository.save(admin);
            log.info("Super usuario creado con éxito: {}", adminEmail);
        } else {
            log.info("La base de datos ya contiene registros. Saltando Seeder.");
        }
    }
}