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
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;

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
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

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
            platosFuertes.setDescripcion("Platos principales y especialidades de la casa");
            categoriaRepository.save(platosFuertes);

            Categoria postres = new Categoria();
            postres.setNombre("Postres");
            postres.setDescripcion("Postres y dulces para endulzar tu día");
            categoriaRepository.save(postres);

            Categoria entradas = new Categoria();
            entradas.setNombre("Entradas");
            entradas.setDescripcion("Aperitivos y entrantes para empezar");
            categoriaRepository.save(entradas);

            Categoria ensaladas = new Categoria();
            ensaladas.setNombre("Ensaladas");
            ensaladas.setDescripcion("Opciones frescas y saludables");
            categoriaRepository.save(ensaladas);

            // Productos
            Producto cocaCola = createProducto("Coca-Cola", "Refresco de cola", new BigDecimal("2.50"), bebidas, false);
            Producto sprite = createProducto("Sprite", "Refresco de lima-limón", new BigDecimal("2.50"), bebidas,
                    false);
            Producto aguaMineral = createProducto("Agua Mineral", "Botella de agua mineral sin gas",
                    new BigDecimal("1.50"), bebidas, false);

            Producto hamburguesa = createProducto("Hamburguesa Clásica",
                    "Hamburguesa de carne con queso, lechuga, tomate y cebolla", new BigDecimal("12.00"), platosFuertes,
                    true);
            Producto pizzaPepperoni = createProducto("Pizza Pepperoni",
                    "Pizza con salsa de tomate, mozzarella y pepperoni", new BigDecimal("15.50"), platosFuertes, true);
            Producto lomoSaltado = createProducto("Lomo Saltado",
                    "Clásico lomo saltado peruano con papas fritas y arroz", new BigDecimal("18.00"), platosFuertes,
                    true);
            Producto ajiGallina = createProducto("Ají de Gallina",
                    "Plato cremoso de gallina deshilachada con ají amarillo", new BigDecimal("16.00"), platosFuertes,
                    true);

            Producto pastelChocolate = createProducto("Pastel de Chocolate", "Delicioso pastel de chocolate con fudge",
                    new BigDecimal("5.00"), postres, false);
            Producto flan = createProducto("Flan Casero", "Flan de huevo con caramelo", new BigDecimal("4.50"), postres,
                    false);

            Producto tequenos = createProducto("Tequeños con Guacamole",
                    "Dedos de queso frito acompañados de guacamole casero", new BigDecimal("8.00"), entradas, false);
            Producto papasFritas = createProducto("Papas Fritas", "Porción de papas fritas crujientes",
                    new BigDecimal("4.00"), entradas, false);

            Producto ensaladaCesar = createProducto("Ensalada César",
                    "Lechuga romana, crutones, queso parmesano y aderezo César", new BigDecimal("9.50"), ensaladas,
                    false);

            // Mesas
            Mesa mesa1 = createMesa(1, 4, "Ventana");
            Mesa mesa2 = createMesa(2, 2, "Pasillo");
            Mesa mesa3 = createMesa(3, 6, "Terraza");
            Mesa mesa4 = createMesa(4, 8, "Salón Principal");
            Mesa mesa5 = createMesa(5, 2, "Barra");
            Mesa mesa6 = createMesa(6, 4, "Jardín");

            // Usuarios
            Usuario mesero = new Usuario();
            mesero.setNombre("Juan");
            mesero.setUsername("juan.perez");
            mesero.setPasswordHash("123456"); // En un caso real, esto debería estar encriptado
            mesero.setEmail("juan.perez@mozaico.com");
            mesero.setTipoUsuario(TipoUsuario.MESERO);
            mesero.setNumeroDocumentoIdentidad("12345678");
            mesero.setTipoDocumentoIdentidad(TipoDocumentoIdentidad.DNI);
            usuarioRepository.save(mesero);

            Usuario cocinero = new Usuario();
            cocinero.setNombre("Maria");
            cocinero.setUsername("maria.garcia");
            cocinero.setPasswordHash("123456");
            cocinero.setEmail("maria.garcia@mozaico.com");
            cocinero.setTipoUsuario(TipoUsuario.COCINERO);
            cocinero.setNumeroDocumentoIdentidad("87654321");
            cocinero.setTipoDocumentoIdentidad(TipoDocumentoIdentidad.PASAPORTE);
            usuarioRepository.save(cocinero);

            Usuario administrador = new Usuario();
            administrador.setNombre("Pedro");
            administrador.setUsername("pedro.admin");
            administrador.setPasswordHash("admin123");
            administrador.setEmail("pedro.admin@mozaico.com");
            administrador.setTipoUsuario(TipoUsuario.ADMIN);
            administrador.setNumeroDocumentoIdentidad("11223344");
            administrador.setTipoDocumentoIdentidad(TipoDocumentoIdentidad.DNI);
            usuarioRepository.save(administrador);

            // Proveedores
            Proveedor proveedorBebidas = new Proveedor();
            proveedorBebidas.setNombre("Distribuidora de Bebidas S.A.");
            proveedorBebidas.setContacto("Juan Distribuidor");
            proveedorBebidas.setTelefono("123456789");
            proveedorRepository.save(proveedorBebidas);

            Proveedor proveedorAlimentos = new Proveedor();
            proveedorAlimentos.setNombre("Alimentos Frescos S.R.L.");
            proveedorAlimentos.setContacto("Ana Proveedora");
            proveedorAlimentos.setTelefono("987654321");
            proveedorRepository.save(proveedorAlimentos);

            Proveedor proveedorPostres = new Proveedor();
            proveedorPostres.setNombre("Dulces Delicias S.A.C.");
            proveedorPostres.setContacto("Pedro Pastelero");
            proveedorPostres.setTelefono("555112233");
            proveedorRepository.save(proveedorPostres);

            // Inventario
            createInventario(cocaCola, 100, 20, 200, new BigDecimal("0.80"));
            createInventario(sprite, 80, 15, 150, new BigDecimal("0.75"));
            createInventario(aguaMineral, 120, 30, 250, new BigDecimal("0.50"));

            createInventario(hamburguesa, 50, 10, 100, new BigDecimal("5.50"));
            createInventario(pizzaPepperoni, 40, 8, 80, new BigDecimal("7.00"));
            createInventario(lomoSaltado, 30, 5, 60, new BigDecimal("9.00"));
            createInventario(ajiGallina, 35, 7, 70, new BigDecimal("8.50"));

            createInventario(pastelChocolate, 30, 5, 50, new BigDecimal("2.00"));
            createInventario(flan, 25, 5, 40, new BigDecimal("1.80"));

            createInventario(tequenos, 60, 10, 120, new BigDecimal("3.00"));
            createInventario(papasFritas, 90, 15, 180, new BigDecimal("1.50"));

            createInventario(ensaladaCesar, 20, 5, 40, new BigDecimal("4.00"));

            // Inventario con stock bajo para probar alertas
            createInventario(
                    createProducto("Pan de Ajo", "Pan con ajo y perejil", new BigDecimal("3.00"), entradas, false), 3,
                    5, 20, new BigDecimal("1.00"));

            // Menus
            Menu menuDelDia = new Menu();
            menuDelDia.setNombre("Menú del Día");
            menuDelDia.setDescripcion("Incluye una hamburguesa, una Coca-Cola y un postre de chocolate.");
            menuDelDia.setPrecio(new BigDecimal("15.00"));
            menuDelDia.setProductos(Set.of(hamburguesa, cocaCola, pastelChocolate));
            menuRepository.save(menuDelDia);

            Menu menuVegetariano = new Menu();
            menuVegetariano.setNombre("Menú Vegetariano");
            menuVegetariano.setDescripcion("Ensalada César y Agua Mineral.");
            menuVegetariano.setPrecio(new BigDecimal("10.00"));
            menuVegetariano.setProductos(Set.of(ensaladaCesar, aguaMineral));
            menuRepository.save(menuVegetariano);

            Menu menuEjecutivo = new Menu();
            menuEjecutivo.setNombre("Menú Ejecutivo");
            menuEjecutivo.setDescripcion("Lomo Saltado, Sprite y Flan Casero.");
            menuEjecutivo.setPrecio(new BigDecimal("22.00"));
            menuEjecutivo.setProductos(Set.of(lomoSaltado, sprite, flan));
            menuRepository.save(menuEjecutivo);

            // Clientes
            Cliente clienteCarlos = new Cliente();
            clienteCarlos.setNombre("Carlos");
            clienteCarlos.setApellido("Santana");
            clienteCarlos.setEmail("carlos.santana@cliente.com");
            clienteCarlos.setTelefono("987654321");
            clienteRepository.save(clienteCarlos);

            Cliente clienteAna = new Cliente();
            clienteAna.setNombre("Ana");
            clienteAna.setApellido("Gomez");
            clienteAna.setEmail("ana.gomez@cliente.com");
            clienteAna.setTelefono("111222333");
            clienteRepository.save(clienteAna);

            Cliente clientePedro = new Cliente();
            clientePedro.setNombre("Pedro");
            clientePedro.setApellido("Ramirez");
            clientePedro.setEmail("pedro.ramirez@cliente.com");
            clientePedro.setTelefono("444555666");
            clienteRepository.save(clientePedro);

            // Retrieve existing entities for Pedido creation
            // The variables mesero, cocinero, mesa1, mesa2, mesa3, cocaCola, hamburguesa,
            // pizzaPepperoni, lomoSaltado, pastelChocolate, ensaladaCesar, tequenos
            // are already declared and initialized earlier in the run method.
            // We just need to ensure they are accessible or re-fetch if necessary.
            // For now, let's assume they are accessible and remove the duplicate
            // declarations.

            // The variables mesero, cocinero, mesa1, mesa2, mesa3, cocaCola, hamburguesa,
            // pizzaPepperoni, lomoSaltado, pastelChocolate, ensaladaCesar, tequenos,
            // sprite, aguaMineral
            // are already declared and initialized earlier in the run method.
            // We just need to ensure they are accessible or re-fetch if necessary.
            // For now, let's assume they are accessible and remove the duplicate
            // declarations.

            if (mesero != null && cocinero != null && mesa1 != null && mesa2 != null && mesa3 != null &&
                    cocaCola != null && hamburguesa != null && pizzaPepperoni != null && lomoSaltado != null &&
                    pastelChocolate != null && ensaladaCesar != null && tequenos != null && sprite != null
                    && aguaMineral != null) {

                // Pedido con cliente Carlos, en mesa1
                Pedido pedidoCarlosMesa = createPedido(clienteCarlos, mesa1, mesero, TipoServicio.MESA,
                        "Pedido para cliente Carlos en mesa 1");
                createDetallePedido(pedidoCarlosMesa, hamburguesa, 1, hamburguesa.getPrecio());
                createDetallePedido(pedidoCarlosMesa, cocaCola, 1, cocaCola.getPrecio());
                createDetallePedido(pedidoCarlosMesa, pastelChocolate, 1, pastelChocolate.getPrecio());

                // Pedido con cliente Ana, para delivery
                Pedido pedidoAnaDelivery = createPedido(clienteAna, null, mesero, TipoServicio.DELIVERY,
                        "Pedido para delivery de Ana");
                pedidoAnaDelivery.setDireccionDelivery("Av. Siempre Viva 742");
                pedidoRepository.save(pedidoAnaDelivery); // Save again to update delivery address
                createDetallePedido(pedidoAnaDelivery, pizzaPepperoni, 1, pizzaPepperoni.getPrecio());
                createDetallePedido(pedidoAnaDelivery, ensaladaCesar, 1, ensaladaCesar.getPrecio());

                // Pedido sin cliente (anónimo), en mesa2
                Pedido pedidoAnonimoMesa = createPedido(null, mesa2, mesero, TipoServicio.MESA,
                        "Pedido anónimo en mesa 2");
                createDetallePedido(pedidoAnonimoMesa, lomoSaltado, 2, lomoSaltado.getPrecio());
                createDetallePedido(pedidoAnonimoMesa, cocaCola, 2, cocaCola.getPrecio());

                // Otro pedido sin cliente (anónimo), para llevar
                Pedido pedidoAnonimoLlevar = createPedido(null, null, mesero, TipoServicio.LLEVAR,
                        "Pedido anónimo para llevar");
                createDetallePedido(pedidoAnonimoLlevar, tequenos, 1, tequenos.getPrecio());
                createDetallePedido(pedidoAnonimoLlevar, sprite, 1, sprite.getPrecio());

                // Pedido con cliente Pedro, en mesa3, con varias unidades
                Pedido pedidoPedroMesa = createPedido(clientePedro, mesa3, mesero, TipoServicio.MESA,
                        "Pedido para Pedro con varias unidades");
                createDetallePedido(pedidoPedroMesa, hamburguesa, 2, hamburguesa.getPrecio());
                createDetallePedido(pedidoPedroMesa, pizzaPepperoni, 1, pizzaPepperoni.getPrecio());
                createDetallePedido(pedidoPedroMesa, aguaMineral, 3, aguaMineral.getPrecio());
            }

            System.out.println("Data loaded...");
        }
    }

    private Producto createProducto(String nombre, String descripcion, BigDecimal precio, Categoria categoria,
            boolean requierePreparacion) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCategoria(categoria);
        producto.setRequierePreparacion(requierePreparacion);
        producto.setEstado(EstadoProducto.ACTIVO);
        return productoRepository.save(producto);
    }

    private Mesa createMesa(int numero, int capacidad, String ubicacion) {
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(numero);
        mesa.setCapacidad(capacidad);
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setUbicacion(ubicacion);
        return mesaRepository.save(mesa);
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

    private Pedido createPedido(Cliente cliente, Mesa mesa, Usuario empleado, TipoServicio tipoServicio,
            String observaciones) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setMesa(mesa);
        pedido.setEmpleado(empleado);
        pedido.setTipoServicio(tipoServicio);
        pedido.setObservaciones(observaciones);
        // Other fields like subtotal, impuestos, descuento, total will be calculated by
        // service or left as default
        return pedidoRepository.save(pedido);
    }

    private DetallePedido createDetallePedido(Pedido pedido, Producto producto, int cantidad,
            BigDecimal precioUnitario) {
        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setPedido(pedido);
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(cantidad);
        detallePedido.setPrecioUnitario(precioUnitario);
        detallePedido.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
        return detallePedidoRepository.save(detallePedido);
    }
}
