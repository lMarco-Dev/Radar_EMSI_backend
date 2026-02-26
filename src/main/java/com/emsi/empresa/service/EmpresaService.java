package com.emsi.empresa.service;

import com.emsi.empresa.dto.EmpresaRequestDTO;
import com.emsi.empresa.dto.EmpresaResponseDTO;
import com.emsi.empresa.dto.QrInfoDTO;

import java.util.List;

public interface EmpresaService {
    EmpresaResponseDTO crear(EmpresaRequestDTO dto);
    EmpresaResponseDTO actualizar(Long id, EmpresaRequestDTO dto);
    List<EmpresaResponseDTO> listarTodas();
    EmpresaResponseDTO obtenerPorId(Long id);
    void desactivar(Long id);
    QrInfoDTO obtenerQrInfo(Long id);
}
