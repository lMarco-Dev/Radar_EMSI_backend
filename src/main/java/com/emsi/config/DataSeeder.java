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
        tipoRepository.deleteAll();
        causaRepository.deleteAll();

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
        tipoRepository.saveAll(List.of(
                TipoComportamiento.builder().nombre("Comportamiento Inseguro").descripcion("Accion o comportamiento que puede causar un accidente").activo(true).build(),
                TipoComportamiento.builder().nombre("Reconocimiento").descripcion("Reconocimiento de buena practica de seguridad").activo(true).build(),
                TipoComportamiento.builder().nombre("Accidente").descripcion("Evento que resulto en lesion o dano").activo(true).build(),
                TipoComportamiento.builder().nombre("Casi Accidente").descripcion("Evento que pudo haber resultado en lesion o dano").activo(true).build(),
                TipoComportamiento.builder().nombre("Condicion Insegura").descripcion("Condicion del entorno que representa un peligro").activo(true).build(),
                TipoComportamiento.builder().nombre("Acto Inseguro").descripcion("Acto que viola un procedimiento o norma de seguridad").activo(true).build()
        ));

        causaRepository.saveAll(List.of(
                Causa.builder().nombre("Error Humano").activo(true).build(),
                Causa.builder().nombre("Falta de Recursos Adecuados").activo(true).build(),
                Causa.builder().nombre("Falta de Estandar Seguro").activo(true).build(),
                Causa.builder().nombre("Falta de Capacitacion").activo(true).build(),
                Causa.builder().nombre("Condicion Ambiental").activo(true).build(),
                Causa.builder().nombre("Falla de Equipos").activo(true).build()
        ));
        log.info("✅ Catálogos actualizados con éxito");
    }
}