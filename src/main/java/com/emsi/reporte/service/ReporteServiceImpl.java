package com.emsi.reporte.service;

import com.emsi.catalogo.model.Causa;
import com.emsi.catalogo.model.TipoComportamiento;
import com.emsi.catalogo.repository.CausaRepository;
import com.emsi.catalogo.repository.TipoComportamientoRepository;
import com.emsi.empresa.model.Empresa;
import com.emsi.empresa.repository.EmpresaRepository;
import com.emsi.reporte.dto.CambioEstadoDTO;
import com.emsi.reporte.dto.ReporteRequestDTO;
import com.emsi.reporte.dto.ReporteResponseDTO;
import com.emsi.reporte.model.Evidencia;
import com.emsi.reporte.model.HistorialEstado;
import com.emsi.reporte.model.Reporte;
import com.emsi.reporte.repository.EvidenciaRepository;
import com.emsi.reporte.repository.HistorialEstadoRepository;
import com.emsi.reporte.repository.ReporteRepository;
import com.emsi.shared.enums.EstadoReporte;
import com.emsi.shared.exception.ResourceNotFoundException;
import com.emsi.shared.exception.TokenInvalidoException;
import com.emsi.usuario.model.Usuario;
import com.emsi.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final HistorialEstadoRepository historialEstadoRepository;
    private final EmpresaRepository empresaRepository;
    private final TipoComportamientoRepository tipoRepository;
    private final CausaRepository causaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public ReporteResponseDTO crearPublico(String tokenEmpresa, ReporteRequestDTO dto) {
        Empresa empresa = empresaRepository.findByTokenPublicoAndActivoTrue(tokenEmpresa)
                .orElseThrow(() -> new TokenInvalidoException("Token de empresa invalido o empresa inactiva"));
        return crearReporte(empresa, dto);
    }

    private ReporteResponseDTO crearReporte(Empresa empresa, ReporteRequestDTO dto) {
        TipoComportamiento tipo = tipoRepository.findById(dto.getTipoComportamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de comportamiento no encontrado"));
        Causa causa = null;
        if (dto.getCausaId() != null) {
            causa = causaRepository.findById(dto.getCausaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Causa no encontrada"));
        }
        Reporte reporte = Reporte.builder()
                .empresa(empresa)
                .tipoComportamiento(tipo)
                .causa(causa)
                .nombreReportante(dto.getNombreReportante())
                .area(dto.getArea())
                .turno(dto.getTurno())
                .descripcionComportamiento(dto.getDescripcionComportamiento())
                .medidaContencion(dto.getMedidaContencion())
                .fechaOcurrido(dto.getFechaOcurrido())
                .lugarEspecifico(dto.getLugarEspecifico())
                .camposDinamicos(dto.getCamposDinamicos())
                .build();
        Reporte saved = reporteRepository.save(reporte);
        if (dto.getEvidencias() != null) {
            dto.getEvidencias().forEach(e -> evidenciaRepository.save(
                    Evidencia.builder().reporte(saved)
                            .urlCloudinary(e.getUrlCloudinary())
                            .publicIdCloudinary(e.getPublicIdCloudinary())
                            .build()));
        }
        return toDTO(saved, true);
    }

    @Override
    public Page<ReporteResponseDTO> listar(Long empresaId, EstadoReporte estado, Long tipoId, Pageable pageable) {
        return reporteRepository.buscarConFiltros(empresaId, estado, tipoId, pageable)
                .map(r -> toDTO(r, false));
    }

    @Override
    public ReporteResponseDTO obtenerPorId(Long id) {
        return toDTO(findById(id), true);
    }

    @Override
    @Transactional
    public ReporteResponseDTO cambiarEstado(Long id, CambioEstadoDTO dto, String emailUsuario) {
        Reporte reporte = findById(id);
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        EstadoReporte estadoAnterior = reporte.getEstado();
        reporte.setEstado(dto.getEstado());
        reporte.setRevisadoPor(usuario);
        reporteRepository.save(reporte);
        historialEstadoRepository.save(HistorialEstado.builder()
                .reporte(reporte).cambiadoPor(usuario)
                .estadoAnterior(estadoAnterior).estadoNuevo(dto.getEstado())
                .comentario(dto.getComentario()).build());
        return toDTO(reporte, true);
    }

    @Override
    public Map<String, Long> obtenerEstadisticas() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", reporteRepository.count());
        stats.put("pendientes", reporteRepository.countByEstado(EstadoReporte.PENDIENTE));
        stats.put("enRevision", reporteRepository.countByEstado(EstadoReporte.EN_REVISION));
        stats.put("solucionados", reporteRepository.countByEstado(EstadoReporte.SOLUCIONADO));
        return stats;
    }

    private Reporte findById(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));
    }

    private ReporteResponseDTO toDTO(Reporte r, boolean incluirDetalles) {
        List<ReporteResponseDTO.EvidenciaDTO> evidencias = null;
        List<ReporteResponseDTO.HistorialDTO> historial = null;
        if (incluirDetalles) {
            evidencias = evidenciaRepository.findByReporteId(r.getId()).stream()
                    .map(e -> ReporteResponseDTO.EvidenciaDTO.builder()
                            .id(e.getId()).urlCloudinary(e.getUrlCloudinary())
                            .publicIdCloudinary(e.getPublicIdCloudinary()).build())
                    .collect(Collectors.toList());
            historial = historialEstadoRepository.findByReporteIdOrderByFechaCambioDesc(r.getId()).stream()
                    .map(h -> ReporteResponseDTO.HistorialDTO.builder()
                            .id(h.getId()).estadoAnterior(h.getEstadoAnterior())
                            .estadoNuevo(h.getEstadoNuevo()).comentario(h.getComentario())
                            .cambiadoPorNombre(h.getCambiadoPor() != null ? h.getCambiadoPor().getNombre() : null)
                            .fechaCambio(h.getFechaCambio()).build())
                    .collect(Collectors.toList());
        }
        return ReporteResponseDTO.builder()
                .id(r.getId())
                .empresaId(r.getEmpresa().getId())
                .empresaNombre(r.getEmpresa().getNombre())
                .tipoComportamientoId(r.getTipoComportamiento().getId())
                .tipoComportamientoNombre(r.getTipoComportamiento().getNombre())
                .causaId(r.getCausa() != null ? r.getCausa().getId() : null)
                .causaNombre(r.getCausa() != null ? r.getCausa().getNombre() : null)
                .estado(r.getEstado())
                .nombreReportante(r.getNombreReportante())
                .area(r.getArea()).turno(r.getTurno())
                .descripcionComportamiento(r.getDescripcionComportamiento())
                .medidaContencion(r.getMedidaContencion())
                .fechaOcurrido(r.getFechaOcurrido())
                .lugarEspecifico(r.getLugarEspecifico())
                .camposDinamicos(r.getCamposDinamicos())
                .evidencias(evidencias).historial(historial)
                .createdAt(r.getCreatedAt()).updatedAt(r.getUpdatedAt())
                .build();
    }
}
