package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.*;
import com.djasoft.mozaico.infrastructure.utils.SlugGenerator;
import com.djasoft.mozaico.services.EmpresaService;
import com.djasoft.mozaico.services.storage.FileStorageService;
import com.djasoft.mozaico.web.dtos.EmpresaResponseDTO;
import com.djasoft.mozaico.web.dtos.EmpresaUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponseDTO obtenerMiEmpresa() {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null || currentUser.getEmpresa() == null) {
            throw new IllegalStateException("Usuario no tiene empresa asociada");
        }

        Long idEmpresa = currentUser.getEmpresa().getIdEmpresa();
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalStateException("Empresa no encontrada"));

        return mapToDTO(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponseDTO actualizarEmpresa(EmpresaUpdateDTO dto) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null || currentUser.getEmpresa() == null) {
            throw new IllegalStateException("Usuario no tiene empresa asociada");
        }

        Long idEmpresa = currentUser.getEmpresa().getIdEmpresa();
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalStateException("Empresa no encontrada"));

        log.info("Actualizando empresa {} por usuario {}",
                empresa.getNombre(), currentUser.getUsername());

        // Actualizar campos básicos
        empresa.setNombre(dto.getNombre());
        empresa.setDescripcion(dto.getDescripcion());
        empresa.setDireccion(dto.getDireccion());
        empresa.setTelefono(dto.getTelefono());
        empresa.setEmail(dto.getEmail());
        empresa.setPaginaWeb(dto.getPaginaWeb());

        // Si el logo URL viene en el DTO, actualizarlo
        if (dto.getLogoUrl() != null) {
            empresa.setLogoUrl(dto.getLogoUrl());
        }

        // Actualizar configuración de operación
        empresa.setTipoOperacion(dto.getTipoOperacion());
        empresa.setAplicaIgv(dto.getAplicaIgv());

        if (dto.getPorcentajeIgv() != null) {
            empresa.setPorcentajeIgv(dto.getPorcentajeIgv());
        }

        if (dto.getMoneda() != null) {
            empresa.setMoneda(dto.getMoneda());
        }

        if (dto.getPrefijoTicket() != null) {
            empresa.setPrefijoTicket(dto.getPrefijoTicket());
        }

        // Si el nombre cambió, sugerir actualizar el slug
        String slugSugerido = SlugGenerator.generateSlug(dto.getNombre());
        if (!slugSugerido.equals(empresa.getSlug())) {
            log.info("El nombre cambió. Slug sugerido: {}", slugSugerido);
        }

        empresaRepository.save(empresa);

        return mapToDTO(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponseDTO actualizarLogo(MultipartFile file) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null || currentUser.getEmpresa() == null) {
            throw new IllegalStateException("Usuario no tiene empresa asociada");
        }

        Long idEmpresa = currentUser.getEmpresa().getIdEmpresa();
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalStateException("Empresa no encontrada"));

        log.info("Actualizando logo de empresa {}", empresa.getNombre());

        // Eliminar logo anterior si existe
        if (empresa.getLogoUrl() != null && !empresa.getLogoUrl().isEmpty()) {
            try {
                String oldFilename = empresa.getLogoUrl().substring(
                        empresa.getLogoUrl().lastIndexOf('/') + 1);
                fileStorageService.delete(oldFilename);
            } catch (Exception e) {
                log.warn("No se pudo eliminar logo anterior: {}", e.getMessage());
            }
        }

        // Guardar nuevo logo
        String filename = fileStorageService.store(file);
        String logoUrl = fileStorageService.getFileUrl(filename);

        empresa.setLogoUrl(logoUrl);
        empresaRepository.save(empresa);

        return mapToDTO(empresa);
    }

    @Override
    @Transactional
    public EmpresaResponseDTO cambiarEstadoEmpresa(Boolean activa) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null || currentUser.getEmpresa() == null) {
            throw new IllegalStateException("Usuario no tiene empresa asociada");
        }

        Long idEmpresa = currentUser.getEmpresa().getIdEmpresa();
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalStateException("Empresa no encontrada"));

        log.info("Cambiando estado de empresa {} a: {}",
                empresa.getNombre(), activa ? "ACTIVA" : "INACTIVA");

        empresa.setActiva(activa);
        empresaRepository.save(empresa);

        return mapToDTO(empresa);
    }

    @Override
    @Transactional(readOnly = true)
    public Object obtenerEstadisticas() {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null || currentUser.getEmpresa() == null) {
            throw new IllegalStateException("Usuario no tiene empresa asociada");
        }

        Long idEmpresa = currentUser.getEmpresa().getIdEmpresa();

        // Cargar la empresa completa desde el repositorio
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalStateException("Empresa no encontrada"));

        Map<String, Object> estadisticas = new HashMap<>();

        // Contar productos
        long totalProductos = productoRepository.count();
        estadisticas.put("totalProductos", totalProductos);

        // Contar clientes
        long totalClientes = clienteRepository.count();
        estadisticas.put("totalClientes", totalClientes);

        // Contar usuarios/empleados
        long totalUsuarios = usuarioRepository.count();
        estadisticas.put("totalEmpleados", totalUsuarios);

        // Contar pedidos
        long totalPedidos = pedidoRepository.count();
        estadisticas.put("totalPedidos", totalPedidos);

        // Información de la empresa
        estadisticas.put("empresa", mapToDTO(empresa));

        return estadisticas;
    }

    private EmpresaResponseDTO mapToDTO(Empresa empresa) {
        return EmpresaResponseDTO.builder()
                .idEmpresa(empresa.getIdEmpresa())
                .nombre(empresa.getNombre())
                .slug(empresa.getSlug())
                .descripcion(empresa.getDescripcion())
                .direccion(empresa.getDireccion())
                .telefono(empresa.getTelefono())
                .email(empresa.getEmail())
                .logoUrl(empresa.getLogoUrl())
                .paginaWeb(empresa.getPaginaWeb())
                .activa(empresa.getActiva())
                .tipoOperacion(empresa.getTipoOperacion())
                .aplicaIgv(empresa.getAplicaIgv())
                .porcentajeIgv(empresa.getPorcentajeIgv())
                .moneda(empresa.getMoneda())
                .prefijoTicket(empresa.getPrefijoTicket())
                .correlativoTicket(empresa.getCorrelativoTicket())
                .fechaCreacion(empresa.getFechaCreacion())
                .fechaActualizacion(empresa.getFechaActualizacion())
                .build();
    }
}
