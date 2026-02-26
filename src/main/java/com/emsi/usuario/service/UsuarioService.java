package com.emsi.usuario.service;

import com.emsi.usuario.dto.LoginRequestDTO;
import com.emsi.usuario.dto.LoginResponseDTO;
import com.emsi.usuario.dto.UsuarioRequestDTO;
import com.emsi.usuario.dto.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    LoginResponseDTO login(LoginRequestDTO dto);
    UsuarioResponseDTO crear(UsuarioRequestDTO dto);
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO obtenerPorId(Long id);
    void desactivar(Long id);
}
