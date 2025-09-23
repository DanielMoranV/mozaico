package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.enums.usuario.EstadoUsuario;
import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;
import com.djasoft.mozaico.domain.repositories.UsuarioRepository;
import com.djasoft.mozaico.services.UsuarioService;
import com.djasoft.mozaico.web.dtos.UsuarioRequestDTO;
import com.djasoft.mozaico.web.dtos.UsuarioResponseDTO;
import com.djasoft.mozaico.web.dtos.UsuarioUpdateDTO;
import com.djasoft.mozaico.web.exceptions.ResourceConflictException;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO requestDTO) {
        Usuario nuevoUsuario = Usuario.builder()
                .nombre(requestDTO.getNombre())
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(requestDTO.getPassword())) // Encrypt the password
                .tipoUsuario(requestDTO.getTipoUsuario())
                .tipoDocumentoIdentidad(requestDTO.getTipoDocumentoIdentidad())
                .numeroDocumentoIdentidad(requestDTO.getNumeroDocumento())
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        return mapToResponseDTO(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioUpdateDTO requestDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));

        // Check for username conflict
        if (!usuarioExistente.getUsername().equals(requestDTO.getUsername())) {
            usuarioRepository.findByUsername(requestDTO.getUsername()).ifPresent(u -> {
                throw new ResourceConflictException("El username ya está en uso: " + requestDTO.getUsername());
            });
            usuarioExistente.setUsername(requestDTO.getUsername());
        }

        // Check for email conflict
        if (!usuarioExistente.getEmail().equals(requestDTO.getEmail())) {
            usuarioRepository.findByEmail(requestDTO.getEmail()).ifPresent(u -> {
                throw new ResourceConflictException("El email ya está en uso: " + requestDTO.getEmail());
            });
            usuarioExistente.setEmail(requestDTO.getEmail());
        }

        // Check for document number conflict
        if (!usuarioExistente.getNumeroDocumentoIdentidad().equals(requestDTO.getNumeroDocumento())) {
            usuarioRepository.findByNumeroDocumentoIdentidad(requestDTO.getNumeroDocumento()).ifPresent(u -> {
                throw new ResourceConflictException("El número de documento ya está en uso: " + requestDTO.getNumeroDocumento());
            });
            usuarioExistente.setNumeroDocumentoIdentidad(requestDTO.getNumeroDocumento());
        }

        usuarioExistente.setNombre(requestDTO.getNombre());
        usuarioExistente.setTipoUsuario(requestDTO.getTipoUsuario());
        usuarioExistente.setTipoDocumentoIdentidad(requestDTO.getTipoDocumentoIdentidad());

        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return mapToResponseDTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con el id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarUsuarios(
            String nombre,
            String username,
            String email,
            TipoUsuario tipoUsuario,
            EstadoUsuario estado,
            TipoDocumentoIdentidad tipoDocumentoIdentidad,
            String numeroDocumento,
            String searchTerm, // Nuevo parámetro
            String logic) {
        Specification<Usuario> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Predicados de búsqueda específica
            if (StringUtils.hasText(nombre)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(username)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(email)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"));
            }
            if (tipoUsuario != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoUsuario"), tipoUsuario));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (tipoDocumentoIdentidad != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoDocumentoIdentidad"), tipoDocumentoIdentidad));
            }
            if (StringUtils.hasText(numeroDocumento)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroDocumentoIdentidad")),
                        "%" + numeroDocumento.toLowerCase() + "%"));
            }

            // Predicado de búsqueda global (searchTerm)
            if (StringUtils.hasText(searchTerm)) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroDocumentoIdentidad")),
                                lowerSearchTerm));
                predicates.add(globalSearchPredicate);
            }

            // Si no hay predicados, devolver null para que findAll devuelva todos los
            // usuarios
            if (predicates.isEmpty()) {
                return null;
            }

            // Combinar predicados con AND u OR según el parámetro 'logic'
            if ("OR".equalsIgnoreCase(logic)) {
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            } else { // Default a AND si no se especifica o es diferente de OR
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        return usuarioRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO activarUsuario(Long id) {
        return cambiarEstadoUsuario(id, EstadoUsuario.ACTIVO);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO desactivarUsuario(Long id) {
        return cambiarEstadoUsuario(id, EstadoUsuario.INACTIVO);
    }

    private UsuarioResponseDTO cambiarEstadoUsuario(Long id, EstadoUsuario estado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));
        usuario.setEstado(estado);
        usuarioRepository.save(usuario);
        return mapToResponseDTO(usuario);
    }

    // Private helper method to map Entity to DTO
    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .tipoUsuario(usuario.getTipoUsuario())
                .tipoDocumentoIdentidad(usuario.getTipoDocumentoIdentidad())
                .numeroDocumentoIdentidad(usuario.getNumeroDocumentoIdentidad())
                .estado(usuario.getEstado())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}
