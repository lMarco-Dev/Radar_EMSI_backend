package com.emsi.config;

import com.emsi.catalogo.model.Causa;
import com.emsi.catalogo.model.TipoComportamiento;
import com.emsi.catalogo.repository.CausaRepository;
import com.emsi.catalogo.repository.TipoComportamientoRepository;
import com.emsi.shared.enums.RolUsuario;
import com.emsi.usuario.model.Usuario;
import com.emsi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final TipoComportamientoRepository tipoRepository;
    private final CausaRepository causaRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.nombre}") private String adminNombre;
    @Value("${admin.email}") private String adminEmail;
    @Value("${admin.password}") private String adminPassword;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedCatalogos();
    }

    private void seedAdmin() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .nombre(adminNombre)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(admin);
            log.info("✅ Admin inicial creado: {}", adminEmail);
        }
    }

    private void seedCatalogos() {
        if (tipoRepository.count() == 0) {
            tipoRepository.saveAll(List.of(
                    TipoComportamiento.builder().nombre("Acto Inseguro").activo(true).build(),
                    TipoComportamiento.builder().nombre("Condición Insegura").activo(true).build(),
                    TipoComportamiento.builder().nombre("Comportamiento Seguro").activo(true).build()
            ));
            log.info("✅ Tipos de comportamiento inicializados");
        }

        // Semilla para Causas
        if (causaRepository.count() == 0) {
            causaRepository.saveAll(List.of(
                    Causa.builder().nombre("Falta de EPP").activo(true).build(),
                    Causa.builder().nombre("Distracción").activo(true).build(),
                    Causa.builder().nombre("Exceso de confianza").activo(true).build(),
                    Causa.builder().nombre("Herramienta defectuosa").activo(true).build(),
                    Causa.builder().nombre("Superficie resbaladiza").activo(true).build()
            ));
            log.info("✅ Causas inicializadas");
        }
    }
}