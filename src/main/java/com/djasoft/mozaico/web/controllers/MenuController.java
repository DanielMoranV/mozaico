package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.MenuService;
import com.djasoft.mozaico.web.dtos.MenuRequestDTO;
import com.djasoft.mozaico.web.dtos.MenuResponseDTO;
import com.djasoft.mozaico.web.dtos.MenuUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponseDTO>> crearMenu(@Valid @RequestBody MenuRequestDTO menuRequestDTO) {
        MenuResponseDTO menu = menuService.crearMenu(menuRequestDTO);
        return new ResponseEntity<>(ApiResponse.created(menu, "Menú creado exitosamente"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponseDTO>>> obtenerTodosLosMenus() {
        List<MenuResponseDTO> menus = menuService.obtenerTodosLosMenus();
        return ResponseEntity.ok(ApiResponse.success(menus, "Menús obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> obtenerMenuPorId(@PathVariable Integer id) {
        MenuResponseDTO menu = menuService.obtenerMenuPorId(id);
        return ResponseEntity.ok(ApiResponse.success(menu, "Menú encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> actualizarMenu(@PathVariable Integer id, @Valid @RequestBody MenuUpdateDTO menuUpdateDTO) {
        MenuResponseDTO menu = menuService.actualizarMenu(id, menuUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(menu, "Menú actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMenu(@PathVariable Integer id) {
        menuService.eliminarMenu(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Menú eliminado exitosamente"));
    }

    @PostMapping("/{idMenu}/productos/{idProducto}")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> agregarProductoAMenu(@PathVariable Integer idMenu, @PathVariable Long idProducto) {
        MenuResponseDTO menu = menuService.agregarProductoAMenu(idMenu, idProducto);
        return ResponseEntity.ok(ApiResponse.success(menu, "Producto agregado al menú exitosamente"));
    }

    @DeleteMapping("/{idMenu}/productos/{idProducto}")
    public ResponseEntity<ApiResponse<MenuResponseDTO>> eliminarProductoDeMenu(@PathVariable Integer idMenu, @PathVariable Long idProducto) {
        MenuResponseDTO menu = menuService.eliminarProductoDeMenu(idMenu, idProducto);
        return ResponseEntity.ok(ApiResponse.success(menu, "Producto eliminado del menú exitosamente"));
    }
}
