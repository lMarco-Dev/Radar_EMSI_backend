package com.emsi.empresa.service;

import com.emsi.empresa.dto.EmpresaRequestDTO;
import com.emsi.empresa.dto.EmpresaResponseDTO;
import com.emsi.empresa.dto.QrInfoDTO;
import com.emsi.empresa.model.Empresa;
import com.emsi.empresa.repository.EmpresaRepository;
import com.emsi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    @Transactional
    public EmpresaResponseDTO crear(EmpresaRequestDTO dto) {
        Empresa empresa = Empresa.builder()
                .nombre(dto.getNombre())
                .ruc(dto.getRuc())
                .logoUrl(dto.getLogoUrl())
                .build();
        return toDTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public EmpresaResponseDTO actualizar(Long id, EmpresaRequestDTO dto) {
        Empresa empresa = findById(id);
        empresa.setNombre(dto.getNombre());
        empresa.setRuc(dto.getRuc());
        empresa.setLogoUrl(dto.getLogoUrl());
        return toDTO(empresaRepository.save(empresa));
    }

    @Override
    public List<EmpresaResponseDTO> listarTodas() {
        return empresaRepository.findAllByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public EmpresaResponseDTO obtenerPorId(Long id) {
        return toDTO(findById(id));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Empresa empresa = findById(id);
        empresa.setActivo(false);
        empresaRepository.save(empresa);
    }

    @Override
    public QrInfoDTO obtenerQrInfo(Long id) {
        Empresa empresa = findById(id);
        return QrInfoDTO.builder()
                .empresaId(empresa.getId())
                .nombreEmpresa(empresa.getNombre())
                .tokenPublico(empresa.getTokenPublico())
                .urlFormulario(frontendUrl + "/reportar/" + empresa.getTokenPublico())
                .build();
    }

    private Empresa findById(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + id));
    }

    private EmpresaResponseDTO toDTO(Empresa empresa) {
        return EmpresaResponseDTO.builder()
                .id(empresa.getId())
                .nombre(empresa.getNombre())
                .ruc(empresa.getRuc())
                .logoUrl(empresa.getLogoUrl())
                .tokenPublico(empresa.getTokenPublico())
                .activo(empresa.getActivo())
                .createdAt(empresa.getCreatedAt())
                .build();
    }
}
