package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.UsuarioRepository;
import com.djasoft.mozaico.services.UsuarioService;
import com.djasoft.mozaico.web.dtos.UsuarioRequestDTO;
import com.djasoft.mozaico.web.dtos.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;

import java.util.stream.Collectors;

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
