package com.emsi.reporte.service;

import com.emsi.catalogo.model.Causa;
import com.emsi.catalogo.model.TipoComportamiento;
import com.emsi.catalogo.repository.CausaRepository;
import com.emsi.catalogo.repository.TipoComportamientoRepository;
import com.emsi.empresa.model.Empresa;
import com.emsi.empresa.repository.EmpresaRepository;
import com.emsi.reporte.dto.CambioEstadoDTO;
import com.emsi.reporte.dto.DashboardDTO;
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
import com.emsi.shared.service.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

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
    private final CloudinaryService cloudinaryService;

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
        String folioUnico = "REP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Reporte reporte = Reporte.builder()
                .empresa(empresa)
                .folio(folioUnico)
                .tipoComportamiento(tipo)
                .causa(causa)
                .nombreReportante(dto.getNombreReportante() != null ? HtmlUtils.htmlEscape(dto.getNombreReportante()) : null)
                .area(dto.getArea() != null ? HtmlUtils.htmlEscape(dto.getArea()) : null)
                .turno(dto.getTurno())
                .descripcionComportamiento(HtmlUtils.htmlEscape(dto.getDescripcionComportamiento()))
                .medidaContencion(dto.getMedidaContencion() != null ? HtmlUtils.htmlEscape(dto.getMedidaContencion()) : null)
                .fechaOcurrido(dto.getFechaOcurrido())
                .lugarEspecifico(dto.getLugarEspecifico() != null ? HtmlUtils.htmlEscape(dto.getLugarEspecifico()) : null)
                .camposDinamicos(dto.getCamposDinamicos())
                .build();

        Reporte saved = reporteRepository.save(reporte);

        if (dto.getEvidencias() != null && !dto.getEvidencias().isEmpty()) {
            for (MultipartFile archivo : dto.getEvidencias()) {
                if (!archivo.isEmpty()) {
                    try {
                        Map result = cloudinaryService.upload(archivo, "evidencias-reportes");
                        evidenciaRepository.save(Evidencia.builder()
                                .reporte(saved)
                                .urlCloudinary((String) result.get("secure_url"))
                                .publicIdCloudinary((String) result.get("public_id"))
                                .build());
                    } catch (Exception e) {
                        throw new RuntimeException("Error al subir evidencia: " + e.getMessage());
                    }
                }
            }
        }

        return toDTO(saved, true);
    }

    @Override
    public Page<ReporteResponseDTO> listar(Long empresaId, EstadoReporte estado, Long tipoId, Pageable pageable) {
        return reporteRepository.buscarConFiltros(empresaId, estado, tipoId, pageable)
                .map(r -> toDTO(r, false));
    }

    @Override
    public List<String> obtenerAreasPorEmpresa(Long empresaId) {
        return reporteRepository.findDistinctAreasByEmpresaId(empresaId);
    }

    @Override
    public DashboardDTO obtenerEstadisticasCompletas(String empresaNombre) {
        boolean hayFiltro = empresaNombre != null && !empresaNombre.trim().isEmpty() && !"Todos".equalsIgnoreCase(empresaNombre);

        Map<String, Long> contadores = new HashMap<>();
        contadores.put("total", hayFiltro ? reporteRepository.countByEmpresa_Nombre(empresaNombre) : reporteRepository.count());
        contadores.put("pendientes", hayFiltro ? reporteRepository.countByEstadoAndEmpresa_Nombre(EstadoReporte.PENDIENTE, empresaNombre) : reporteRepository.countByEstado(EstadoReporte.PENDIENTE));
        contadores.put("enRevision", hayFiltro ? reporteRepository.countByEstadoAndEmpresa_Nombre(EstadoReporte.EN_REVISION, empresaNombre) : reporteRepository.countByEstado(EstadoReporte.EN_REVISION));
        contadores.put("solucionados", hayFiltro ? reporteRepository.countByEstadoAndEmpresa_Nombre(EstadoReporte.SOLUCIONADO, empresaNombre) : reporteRepository.countByEstado(EstadoReporte.SOLUCIONADO));

        return DashboardDTO.builder()
                .contadores(contadores)
                .porArea(hayFiltro ? reporteRepository.countByAreaFiltro(empresaNombre) : reporteRepository.countByArea())
                .porEmpresa(reporteRepository.countByEmpresa()) // Nunca se filtra
                .porTipo(hayFiltro ? reporteRepository.countByTipoFiltro(empresaNombre) : reporteRepository.countByTipo())
                .tendencia(hayFiltro ? reporteRepository.getTendenciaMensualFiltro(empresaNombre) : reporteRepository.getTendenciaMensual())
                .build();
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

        String comentarioSeguro = dto.getComentario() != null ? HtmlUtils.htmlEscape(dto.getComentario()) : null;

        historialEstadoRepository.save(HistorialEstado.builder()
                .reporte(reporte)
                .cambiadoPor(usuario)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(dto.getEstado())
                .comentario(comentarioSeguro)
                .build());

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

    @Override
    public ReporteResponseDTO rastrearPorFolio(String folio) {
        Reporte reporte = reporteRepository.findByFolio(folio)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún reporte con el folio: " + folio));

        ReporteResponseDTO dto = toDTO(reporte, true);

        dto.setNombreReportante(null);
        dto.setEvidencias(null);
        dto.setCamposDinamicos(null);

        return dto;
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
                .folio(r.getFolio())
                .empresaId(r.getEmpresa().getId())
                .empresaNombre(r.getEmpresa().getNombre())
                .tipoComportamientoId(r.getTipoComportamiento().getId())
                .tipoComportamientoNombre(r.getTipoComportamiento().getNombre())
                .causaId(r.getCausa() != null ? r.getCausa().getId() : null)
                .causaNombre(r.getCausa() != null ? r.getCausa().getNombre() : null)
                .estado(r.getEstado())
                .nombreReportante(r.getNombreReportante())
                .area(r.getArea())
                .turno(r.getTurno())
                .descripcionComportamiento(r.getDescripcionComportamiento())
                .medidaContencion(r.getMedidaContencion())
                .fechaOcurrido(r.getFechaOcurrido())
                .lugarEspecifico(r.getLugarEspecifico())
                .camposDinamicos(r.getCamposDinamicos())
                .evidencias(evidencias)
                .historial(historial)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}