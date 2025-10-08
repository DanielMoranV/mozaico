package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.EmpresaResponseDTO;
import com.djasoft.mozaico.web.dtos.EmpresaUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para gestión de empresas
 */
public interface EmpresaService {

    /**
     * Obtener información de la empresa del usuario autenticado
     */
    EmpresaResponseDTO obtenerMiEmpresa();

    /**
     * Actualizar información de la empresa
     * Solo ADMIN y SUPER_ADMIN pueden ejecutar esta acción
     */
    EmpresaResponseDTO actualizarEmpresa(EmpresaUpdateDTO dto);

    /**
     * Actualizar logo de la empresa
     * Solo ADMIN y SUPER_ADMIN pueden ejecutar esta acción
     */
    EmpresaResponseDTO actualizarLogo(MultipartFile file);

    /**
     * Activar/Desactivar empresa
     * Solo SUPER_ADMIN puede ejecutar esta acción
     */
    EmpresaResponseDTO cambiarEstadoEmpresa(Boolean activa);

    /**
     * Obtener estadísticas de la empresa
     */
    Object obtenerEstadisticas();
}
