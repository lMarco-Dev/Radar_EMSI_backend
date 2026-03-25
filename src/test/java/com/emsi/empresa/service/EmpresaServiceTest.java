package com.emsi.empresa.service;

import com.emsi.empresa.dto.EmpresaRequestDTO;
import com.emsi.empresa.repository.EmpresaRepository;
import com.emsi.shared.service.CloudinaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private EmpresaServiceImpl empresaService;

    @Test
    @DisplayName("Debe lanzar excepción si el RUC ya existe al crear una empresa")
    void crearEmpresa_RucDuplicado_LanzaExcepcion() {
        // Arrange: Preparamos el DTO y simulamos que el RUC ya existe en la BD
        EmpresaRequestDTO dto = new EmpresaRequestDTO();
        dto.setNombre("Nueva Empresa");
        dto.setRuc("20123456789");

        when(empresaRepository.existsByNombreIgnoreCase(dto.getNombre())).thenReturn(false);
        when(empresaRepository.existsByRuc(dto.getRuc())).thenReturn(true); // ¡Simulamos el duplicado!

        // Act & Assert: Verificamos que explote con el mensaje correcto
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            empresaService.crear(dto);
        });

        assertEquals("El RUC ya se encuentra registrado.", exception.getMessage());

        // Verificamos que NUNCA se haya llamado al método save()
        verify(empresaRepository, never()).save(any());
    }
}