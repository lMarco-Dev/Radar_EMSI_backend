package com.emsi.usuario.service;

import com.emsi.empresa.model.Empresa;
import com.emsi.empresa.repository.EmpresaRepository;
import com.emsi.security.JwtTokenProvider;
import com.emsi.shared.exception.ResourceNotFoundException;
import com.emsi.usuario.dto.LoginRequestDTO;
import com.emsi.usuario.dto.LoginResponseDTO;
import com.emsi.usuario.dto.UsuarioRequestDTO;
import com.emsi.usuario.dto.UsuarioResponseDTO;
import com.emsi.usuario.model.Usuario;
import com.emsi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getRol().name());
        return LoginResponseDTO.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .rol(usuario.getRol().name())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }
        Empresa empresa = null;
        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
        }
        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .rol(dto.getRol())
                .empresa(empresa)
                .build();
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAllByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO obtenerPorId(Long id) {
        return toDTO(findById(id));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Usuario usuario = findById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private UsuarioResponseDTO toDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rol(u.getRol())
                .activo(u.getActivo())
                .empresaId(u.getEmpresa() != null ? u.getEmpresa().getId() : null)
                .empresaNombre(u.getEmpresa() != null ? u.getEmpresa().getNombre() : null)
                .createdAt(u.getCreatedAt())
                .build();
    }
}
