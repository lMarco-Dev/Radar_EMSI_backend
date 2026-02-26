package com.emsi.empresa.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QrInfoDTO {
    private Long empresaId;
    private String nombreEmpresa;
    private String tokenPublico;
    private String urlFormulario;
}
