package com.djasoft.mozaico.config;

import com.djasoft.mozaico.domain.entities.*;
import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;
import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;
    private final InventarioRepository inventarioRepository;
    private final MenuRepository menuRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() == 0) { // Run only if DB is empty
            // Categorias
            Categoria bebidas = new Categoria();
            bebidas.setNombre("Bebidas");
            bebidas.setDescripcion("Bebidas frías y calientes");
            categoriaRepository.save(bebidas);

            Categoria platosFuertes = new Categoria();
            platosFuertes.setNombre("Platos Fuertes");
            platosFuertes.setDescripcion("Platos principales");
            categoriaRepository.save(platosFuertes);

            Categoria postres = new Categoria();
            postres.setNombre("Postres");
            postres.setDescripcion("Postres y dulces");
            categoriaRepository.save(postres);

            // Productos
            Producto cocaCola = createProducto("Coca-Cola", "Refresco de cola", new BigDecimal("2.50"), bebidas, false);
            Producto hamburguesa = createProducto("Hamburguesa Clásica", "Hamburguesa de carne con queso", new BigDecimal("12.00"), platosFuertes, true);
            Producto pastelChocolate = createProducto("Pastel de Chocolate", "Delicioso pastel de chocolate", new BigDecimal("5.00"), postres, false);

            // Mesas
            createMesa(1, 4, "Ventana");
            createMesa(2, 2, "Pasillo");
            createMesa(3, 6, "Terraza");

            // Usuario (Empleado)
            Usuario empleado = new Usuario();
            empleado.setNombre("Juan");
            empleado.setUsername("juan.perez");
            empleado.setPasswordHash("123456"); // En un caso real, esto debería estar encriptado
            empleado.setEmail("juan.perez@mozaico.com");
            empleado.setTipoUsuario(TipoUsuario.MESERO);
            empleado.setNumeroDocumentoIdentidad("12345678");
            empleado.setTipoDocumentoIdentidad(TipoDocumentoIdentidad.DNI);
            usuarioRepository.save(empleado);

            // Proveedor
            Proveedor proveedorBebidas = new Proveedor();
            proveedorBebidas.setNombre("Distribuidora de Bebidas S.A.");
            proveedorBebidas.setContacto("Juan Distribuidor");
            proveedorBebidas.setTelefono("123456789");
            proveedorRepository.save(proveedorBebidas);

            // Inventario
            createInventario(cocaCola, 100, 20, 200, new BigDecimal("0.80"));
            createInventario(hamburguesa, 50, 10, 100, new BigDecimal("5.50"));
            createInventario(pastelChocolate, 30, 5, 50, new BigDecimal("2.00"));

            // Menu
            Menu menuDelDia = new Menu();
            menuDelDia.setNombre("Menú del Día");
            menuDelDia.setDescripcion("Incluye una hamburguesa, una Coca-Cola y un postre de chocolate.");
            menuDelDia.setPrecio(new BigDecimal("15.00"));
            menuDelDia.setProductos(Set.of(hamburguesa, cocaCola, pastelChocolate));
            menuRepository.save(menuDelDia);

            // Cliente
            Cliente cliente = new Cliente();
            cliente.setNombre("Carlos");
            cliente.setApellido("Santana");
            cliente.setEmail("carlos.santana@cliente.com");
            cliente.setTelefono("987654321");
            clienteRepository.save(cliente);

            System.out.println("Data loaded...");
        }
    }

    private Producto createProducto(String nombre, String descripcion, BigDecimal precio, Categoria categoria, boolean requierePreparacion) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCategoria(categoria);
        producto.setRequierePreparacion(requierePreparacion);
        producto.setEstado(EstadoProducto.ACTIVO);
        return productoRepository.save(producto);
    }

    private void createMesa(int numero, int capacidad, String ubicacion) {
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(numero);
        mesa.setCapacidad(capacidad);
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setUbicacion(ubicacion);
        mesaRepository.save(mesa);
    }

    private void createInventario(Producto producto, int stock, int min, int max, BigDecimal costo) {
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setStockActual(stock);
        inventario.setStockMinimo(min);
        inventario.setStockMaximo(max);
        inventario.setCostoUnitario(costo);
        inventarioRepository.save(inventario);
    }
}
