package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Menu;
import com.djasoft.mozaico.domain.entities.Producto;
import com.djasoft.mozaico.domain.repositories.MenuRepository;
import com.djasoft.mozaico.domain.repositories.ProductoRepository;
import com.djasoft.mozaico.services.MenuService;
import com.djasoft.mozaico.web.dtos.MenuRequestDTO;
import com.djasoft.mozaico.web.dtos.MenuResponseDTO;
import com.djasoft.mozaico.web.dtos.MenuUpdateDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public MenuResponseDTO crearMenu(MenuRequestDTO menuRequestDTO) {
        Set<Producto> productos = new HashSet<>();
        if (menuRequestDTO.getIdProductos() != null && !menuRequestDTO.getIdProductos().isEmpty()) {
            productos = new HashSet<>(productoRepository.findAllById(menuRequestDTO.getIdProductos()));
        }

        Menu nuevoMenu = Menu.builder()
                .nombre(menuRequestDTO.getNombre())
                .descripcion(menuRequestDTO.getDescripcion())
                .precio(menuRequestDTO.getPrecio())
                .disponible(menuRequestDTO.isDisponible())
                .fechaInicio(menuRequestDTO.getFechaInicio())
                .fechaFin(menuRequestDTO.getFechaFin())
                .productos(productos)
                .build();

        Menu menuGuardado = menuRepository.save(nuevoMenu);
        return mapToResponseDTO(menuGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponseDTO> obtenerTodosLosMenus() {
        return menuRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponseDTO obtenerMenuPorId(Integer id) {
        return menuRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Menu no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public MenuResponseDTO actualizarMenu(Integer id, MenuUpdateDTO menuUpdateDTO) {
        Menu menuExistente = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu no encontrado con id: " + id));

        if (menuUpdateDTO.getNombre() != null) {
            menuExistente.setNombre(menuUpdateDTO.getNombre());
        }
        if (menuUpdateDTO.getDescripcion() != null) {
            menuExistente.setDescripcion(menuUpdateDTO.getDescripcion());
        }
        if (menuUpdateDTO.getPrecio() != null) {
            menuExistente.setPrecio(menuUpdateDTO.getPrecio());
        }
        if (menuUpdateDTO.getDisponible() != null) {
            menuExistente.setDisponible(menuUpdateDTO.getDisponible());
        }
        if (menuUpdateDTO.getFechaInicio() != null) {
            menuExistente.setFechaInicio(menuUpdateDTO.getFechaInicio());
        }
        if (menuUpdateDTO.getFechaFin() != null) {
            menuExistente.setFechaFin(menuUpdateDTO.getFechaFin());
        }

        Menu menuActualizado = menuRepository.save(menuExistente);
        return mapToResponseDTO(menuActualizado);
    }

    @Override
    @Transactional
    public void eliminarMenu(Integer id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu no encontrado con id: " + id);
        }
        menuRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MenuResponseDTO agregarProductoAMenu(Integer idMenu, Long idProducto) {
        Menu menu = menuRepository.findById(idMenu)
                .orElseThrow(() -> new ResourceNotFoundException("Menu no encontrado con id: " + idMenu));
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));

        menu.getProductos().add(producto);
        Menu menuActualizado = menuRepository.save(menu);
        return mapToResponseDTO(menuActualizado);
    }

    @Override
    @Transactional
    public MenuResponseDTO eliminarProductoDeMenu(Integer idMenu, Long idProducto) {
        Menu menu = menuRepository.findById(idMenu)
                .orElseThrow(() -> new ResourceNotFoundException("Menu no encontrado con id: " + idMenu));
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + idProducto));

        menu.getProductos().remove(producto);
        Menu menuActualizado = menuRepository.save(menu);
        return mapToResponseDTO(menuActualizado);
    }

    private MenuResponseDTO mapToResponseDTO(Menu menu) {
        Set<ProductoResponseDTO> productosDTO = menu.getProductos().stream()
                .map(producto -> ProductoResponseDTO.builder()
                        .idProducto(producto.getIdProducto())
                        .nombre(producto.getNombre())
                        .precio(producto.getPrecio())
                        .build())
                .collect(Collectors.toSet());

        return MenuResponseDTO.builder()
                .idMenu(menu.getIdMenu())
                .nombre(menu.getNombre())
                .descripcion(menu.getDescripcion())
                .precio(menu.getPrecio())
                .disponible(menu.isDisponible())
                .fechaInicio(menu.getFechaInicio())
                .fechaFin(menu.getFechaFin())
                .productos(productosDTO)
                .build();
    }
}
