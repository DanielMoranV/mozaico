package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.MenuRequestDTO;
import com.djasoft.mozaico.web.dtos.MenuResponseDTO;
import com.djasoft.mozaico.web.dtos.MenuUpdateDTO;

import java.util.List;

public interface MenuService {
    MenuResponseDTO crearMenu(MenuRequestDTO menuRequestDTO);
    List<MenuResponseDTO> obtenerTodosLosMenus();
    MenuResponseDTO obtenerMenuPorId(Integer id);
    MenuResponseDTO actualizarMenu(Integer id, MenuUpdateDTO menuUpdateDTO);
    void eliminarMenu(Integer id);
    MenuResponseDTO agregarProductoAMenu(Integer idMenu, Long idProducto);
    MenuResponseDTO eliminarProductoDeMenu(Integer idMenu, Long idProducto);
}
