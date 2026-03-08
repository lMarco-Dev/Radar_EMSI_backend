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
import com.emsi.shared.service.CloudinaryService;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final CloudinaryService cloudinaryService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    @Transactional
    public EmpresaResponseDTO crear(EmpresaRequestDTO dto) {
        String logoUrl = null;
        if (empresaRepository.existsByNombreIgnoreCase(dto.getNombre()))
            throw new RuntimeException("El nombre de la empresa ya está registrado.");
        if (empresaRepository.existsByRuc(dto.getRuc()))
            throw new RuntimeException("El RUC ya se encuentra registrado.");

        if (dto.getLogo() != null && !dto.getLogo().isEmpty()) {
            try {
                Map result = cloudinaryService.upload(dto.getLogo(), "logos-empresas");
                logoUrl = (String) result.get("secure_url");
            } catch (Exception e) {
                throw new RuntimeException("Error al subir el logo: " + e.getMessage());
            }
        }

        Empresa empresa = Empresa.builder()
                .nombre(dto.getNombre())
                .ruc(dto.getRuc())
                .logoUrl(logoUrl)
                .build();
        return toDTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public EmpresaResponseDTO actualizar(Long id, EmpresaRequestDTO dto) {
        Empresa empresa = findById(id);

        empresa.setNombre(dto.getNombre());
        empresa.setRuc(dto.getRuc());

        if (empresaRepository.existsByNombreIgnoreCaseAndIdNot(dto.getNombre(), id))
            throw new RuntimeException("El nombre ya pertenece a otra empresa.");
        if (empresaRepository.existsByRucAndIdNot(dto.getRuc(), id))
            throw new RuntimeException("El RUC ya pertenece a otra empresa.");

        if (dto.getLogo() != null && !dto.getLogo().isEmpty()) {
            try {
                Map result = cloudinaryService.upload(dto.getLogo(), "logos-empresas");
                String secureUrl = (String) result.get("secure_url");

                if (secureUrl != null && !secureUrl.isBlank()) {
                    empresa.setLogoUrl(secureUrl);
                } else {
                    empresa.setLogoUrl(null);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar el logo en el servidor");
            }
        }

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
