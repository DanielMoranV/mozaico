package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.Cliente;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.ClienteRepository;
import com.djasoft.mozaico.services.ClienteService;
import com.djasoft.mozaico.web.dtos.ClienteRequestDTO;
import com.djasoft.mozaico.web.dtos.ClienteResponseDTO;
import com.djasoft.mozaico.web.dtos.ClienteUpdateDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import com.djasoft.mozaico.web.exceptions.UnauthorizedException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClienteResponseDTO crearCliente(ClienteRequestDTO clienteRequestDTO) {
        Usuario usuarioActual = JwtAuthenticationFilter.getCurrentUser();
        if (usuarioActual == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        Cliente nuevoCliente = Cliente.builder()
                .nombre(clienteRequestDTO.getNombre())
                .apellido(clienteRequestDTO.getApellido())
                .email(clienteRequestDTO.getEmail())
                .telefono(clienteRequestDTO.getTelefono())
                .direccion(clienteRequestDTO.getDireccion())
                .tipoPersona(clienteRequestDTO.getTipoPersona())
                .tipoDocumento(clienteRequestDTO.getTipoDocumento())
                .numeroDocumento(clienteRequestDTO.getNumeroDocumento())
                .fechaNacimiento(clienteRequestDTO.getFechaNacimiento())
                .preferenciasAlimentarias(clienteRequestDTO.getPreferenciasAlimentarias())
                .razonSocial(clienteRequestDTO.getRazonSocial())
                .nombreComercial(clienteRequestDTO.getNombreComercial())
                .representanteLegal(clienteRequestDTO.getRepresentanteLegal())
                .empresa(usuarioActual.getEmpresa())
                .usuarioCreacion(usuarioActual)
                .build();

        Cliente clienteGuardado = clienteRepository.save(nuevoCliente);
        return mapToResponseDTO(clienteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizarCliente(Integer id, ClienteUpdateDTO clienteUpdateDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el id: " + id));

        if (clienteUpdateDTO.getNombre() != null) {
            clienteExistente.setNombre(clienteUpdateDTO.getNombre());
        }
        if (clienteUpdateDTO.getApellido() != null) {
            clienteExistente.setApellido(clienteUpdateDTO.getApellido());
        }
        if (clienteUpdateDTO.getEmail() != null) {
            clienteExistente.setEmail(clienteUpdateDTO.getEmail());
        }
        if (clienteUpdateDTO.getTelefono() != null) {
            clienteExistente.setTelefono(clienteUpdateDTO.getTelefono());
        }
        if (clienteUpdateDTO.getFechaNacimiento() != null) {
            clienteExistente.setFechaNacimiento(clienteUpdateDTO.getFechaNacimiento());
        }
        if (clienteUpdateDTO.getDireccion() != null) {
            clienteExistente.setDireccion(clienteUpdateDTO.getDireccion());
        }
        if (clienteUpdateDTO.getPreferenciasAlimentarias() != null) {
            clienteExistente.setPreferenciasAlimentarias(clienteUpdateDTO.getPreferenciasAlimentarias());
        }
        if (clienteUpdateDTO.getPuntosFidelidad() != null) {
            clienteExistente.setPuntosFidelidad(clienteUpdateDTO.getPuntosFidelidad());
        }
        if (clienteUpdateDTO.getActivo() != null) {
            clienteExistente.setActivo(clienteUpdateDTO.getActivo());
        }

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        return mapToResponseDTO(clienteActualizado);
    }

    @Override
    @Transactional
    public void eliminarCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con el id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClienteResponseDTO cambiarEstadoCliente(Integer id, boolean estado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el id: " + id));
        cliente.setActivo(estado);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return mapToResponseDTO(clienteActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarClientes(String nombre, String apellido, String email, String telefono, Boolean activo, String searchTerm, String logic) {
        Specification<Cliente> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }
            if (apellido != null && !apellido.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("apellido")), "%" + apellido.toLowerCase() + "%"));
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (telefono != null && !telefono.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("telefono"), "%" + telefono + "%"));
            }
            if (activo != null) {
                predicates.add(criteriaBuilder.equal(root.get("activo"), activo));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("apellido")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), lowerSearchTerm),
                        criteriaBuilder.like(root.get("telefono"), lowerSearchTerm)
                );
                predicates.add(globalSearch);
            }

            if (predicates.isEmpty()) {
                return null;
            }

            if ("OR".equalsIgnoreCase(logic)) {
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            } else {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        return clienteRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ClienteResponseDTO mapToResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .idCliente(cliente.getIdCliente())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .tipoPersona(cliente.getTipoPersona())
                .tipoDocumento(cliente.getTipoDocumento())
                .numeroDocumento(cliente.getNumeroDocumento())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .preferenciasAlimentarias(cliente.getPreferenciasAlimentarias())
                .razonSocial(cliente.getRazonSocial())
                .nombreComercial(cliente.getNombreComercial())
                .representanteLegal(cliente.getRepresentanteLegal())
                .puntosFidelidad(cliente.getPuntosFidelidad())
                .fechaRegistro(cliente.getFechaRegistro())
                .activo(cliente.getActivo())
                .build();
    }
}
