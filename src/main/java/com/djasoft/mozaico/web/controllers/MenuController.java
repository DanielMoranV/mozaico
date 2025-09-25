package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.MenuService;
import com.djasoft.mozaico.web.dtos.MenuRequestDTO;
import com.djasoft.mozaico.web.dtos.MenuResponseDTO;
import com.djasoft.mozaico.web.dtos.MenuUpdateDTO;
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
    public ResponseEntity<MenuResponseDTO> crearMenu(@Valid @RequestBody MenuRequestDTO menuRequestDTO) {
        MenuResponseDTO menu = menuService.crearMenu(menuRequestDTO);
        return new ResponseEntity<>(menu, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponseDTO>> obtenerTodosLosMenus() {
        List<MenuResponseDTO> menus = menuService.obtenerTodosLosMenus();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> obtenerMenuPorId(@PathVariable Integer id) {
        MenuResponseDTO menu = menuService.obtenerMenuPorId(id);
        return ResponseEntity.ok(menu);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> actualizarMenu(@PathVariable Integer id, @Valid @RequestBody MenuUpdateDTO menuUpdateDTO) {
        MenuResponseDTO menu = menuService.actualizarMenu(id, menuUpdateDTO);
        return ResponseEntity.ok(menu);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMenu(@PathVariable Integer id) {
        menuService.eliminarMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idMenu}/productos/{idProducto}")
    public ResponseEntity<MenuResponseDTO> agregarProductoAMenu(@PathVariable Integer idMenu, @PathVariable Long idProducto) {
        MenuResponseDTO menu = menuService.agregarProductoAMenu(idMenu, idProducto);
        return ResponseEntity.ok(menu);
    }

    @DeleteMapping("/{idMenu}/productos/{idProducto}")
    public ResponseEntity<MenuResponseDTO> eliminarProductoDeMenu(@PathVariable Integer idMenu, @PathVariable Long idProducto) {
        MenuResponseDTO menu = menuService.eliminarProductoDeMenu(idMenu, idProducto);
        return ResponseEntity.ok(menu);
    }
}
