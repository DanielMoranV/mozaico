package com.djasoft.mozaico.config;

import com.djasoft.mozaico.domain.entities.*;
import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;
import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.domain.enums.usuario.EstadoUsuario;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido;
import com.djasoft.mozaico.domain.enums.pago.EstadoPago;
import com.djasoft.mozaico.domain.enums.reserva.EstadoReserva;
import com.djasoft.mozaico.domain.enums.compra.EstadoCompra;
import com.djasoft.mozaico.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;

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

            // Mesas - Testing different states
            Mesa mesa1 = createMesa(1, 4, "Ventana", EstadoMesa.DISPONIBLE);
            Mesa mesa2 = createMesa(2, 2, "Pasillo", EstadoMesa.OCUPADA);
            Mesa mesa3 = createMesa(3, 6, "Terraza", EstadoMesa.DISPONIBLE);
            Mesa mesa4 = createMesa(4, 8, "Salón Principal", EstadoMesa.RESERVADA);
            Mesa mesa5 = createMesa(5, 2, "Barra", EstadoMesa.DISPONIBLE);
            Mesa mesa6 = createMesa(6, 4, "Jardín", EstadoMesa.MANTENIMIENTO);
            Mesa mesa7 = createMesa(7, 6, "Terraza VIP", EstadoMesa.DISPONIBLE);
            Mesa mesa8 = createMesa(8, 10, "Salón Privado", EstadoMesa.RESERVADA);

            // Usuarios - Testing different states and types
            Usuario mesero = createUsuario("Juan", "juan.perez", "juan.perez@mozaico.com",
                TipoUsuario.MESERO, "12345678", TipoDocumentoIdentidad.DNI, EstadoUsuario.ACTIVO);

            Usuario mesero2 = createUsuario("Carlos", "carlos.mesero", "carlos.mesero@mozaico.com",
                TipoUsuario.MESERO, "12345679", TipoDocumentoIdentidad.DNI, EstadoUsuario.ACTIVO);

            Usuario meseroInactivo = createUsuario("Luis", "luis.inactivo", "luis.inactivo@mozaico.com",
                TipoUsuario.MESERO, "12345680", TipoDocumentoIdentidad.DNI, EstadoUsuario.INACTIVO);

            Usuario cocinero = createUsuario("Maria", "maria.garcia", "maria.garcia@mozaico.com",
                TipoUsuario.COCINERO, "87654321", TipoDocumentoIdentidad.PASAPORTE, EstadoUsuario.ACTIVO);

            Usuario cocinero2 = createUsuario("Ana", "ana.cocina", "ana.cocina@mozaico.com",
                TipoUsuario.COCINERO, "87654322", TipoDocumentoIdentidad.DNI, EstadoUsuario.ACTIVO);

            Usuario cocineroSuspendido = createUsuario("Roberto", "roberto.suspendido", "roberto.suspendido@mozaico.com",
                TipoUsuario.COCINERO, "87654323", TipoDocumentoIdentidad.CARNE_EXTRANJERIA, EstadoUsuario.SUSPENDIDO);

            Usuario administrador = createUsuario("Pedro", "pedro.admin", "pedro.admin@mozaico.com",
                TipoUsuario.ADMIN, "11223344", TipoDocumentoIdentidad.DNI, EstadoUsuario.ACTIVO);

            Usuario cajero = createUsuario("Sofia", "sofia.cajero", "sofia.cajero@mozaico.com",
                TipoUsuario.CAJERO, "55667788", TipoDocumentoIdentidad.DNI, EstadoUsuario.ACTIVO);

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

            // Clientes - More diverse test data
            Cliente clienteCarlos = createCliente("Carlos", "Santana", "carlos.santana@cliente.com", "987654321");
            Cliente clienteAna = createCliente("Ana", "Gomez", "ana.gomez@cliente.com", "111222333");
            Cliente clientePedro = createCliente("Pedro", "Ramirez", "pedro.ramirez@cliente.com", "444555666");
            Cliente clienteLucia = createCliente("Lucia", "Torres", "lucia.torres@cliente.com", "555666777");
            Cliente clienteMarco = createCliente("Marco", "Diaz", "marco.diaz@cliente.com", "888999000");
            Cliente clienteVIP = createCliente("Isabella", "Morales", "isabella.morales@vip.com", "999000111");

            // Métodos de Pago
            MetodoPago efectivo = createMetodoPago("Efectivo", "Pago en efectivo");
            MetodoPago tarjetaCredito = createMetodoPago("Tarjeta de Crédito", "Visa, Mastercard, American Express");
            MetodoPago tarjetaDebito = createMetodoPago("Tarjeta de Débito", "Débito bancario");
            MetodoPago yape = createMetodoPago("Yape", "Transferencia móvil Yape");
            MetodoPago plin = createMetodoPago("Plin", "Transferencia móvil Plin");
            MetodoPago transferencia = createMetodoPago("Transferencia Bancaria", "Transferencia entre cuentas");

            // Pedidos con diferentes estados
            // 1. Pedido ABIERTO
            Pedido pedidoAbierto = createPedidoConEstado(clienteCarlos, mesa1, mesero, TipoServicio.MESA,
                    "Pedido abierto - mesa activa", EstadoPedido.ABIERTO);
            createDetallePedidoConEstado(pedidoAbierto, hamburguesa, 1, hamburguesa.getPrecio(), EstadoDetallePedido.PEDIDO);
            createDetallePedidoConEstado(pedidoAbierto, cocaCola, 1, cocaCola.getPrecio(), EstadoDetallePedido.SERVIDO);

            // 2. Pedido ATENDIDO
            Pedido pedidoAtendido = createPedidoConEstado(clienteAna, mesa2, cocinero, TipoServicio.MESA,
                    "Pedido atendido - esperando finalizar", EstadoPedido.ATENDIDO);
            createDetallePedidoConEstado(pedidoAtendido, pizzaPepperoni, 1, pizzaPepperoni.getPrecio(), EstadoDetallePedido.EN_PREPARACION);
            createDetallePedidoConEstado(pedidoAtendido, ensaladaCesar, 1, ensaladaCesar.getPrecio(), EstadoDetallePedido.SERVIDO);

            // 3. Pedido PAGADO
            Pedido pedidoPagado = createPedidoConEstado(clientePedro, mesa3, mesero2, TipoServicio.MESA,
                    "Pedido pagado - mesa libre", EstadoPedido.PAGADO);
            createDetallePedidoConEstado(pedidoPagado, lomoSaltado, 1, lomoSaltado.getPrecio(), EstadoDetallePedido.SERVIDO);
            createDetallePedidoConEstado(pedidoPagado, sprite, 1, sprite.getPrecio(), EstadoDetallePedido.SERVIDO);

            // 4. Pedido DELIVERY PAGADO
            Pedido pedidoDelivery = createPedidoConEstado(clienteLucia, null, mesero, TipoServicio.DELIVERY,
                    "Pedido delivery pagado", EstadoPedido.PAGADO);
            pedidoDelivery.setDireccionDelivery("Jr. Las Flores 123");
            pedidoRepository.save(pedidoDelivery);
            createDetallePedidoConEstado(pedidoDelivery, ajiGallina, 1, ajiGallina.getPrecio(), EstadoDetallePedido.SERVIDO);
            createDetallePedidoConEstado(pedidoDelivery, aguaMineral, 2, aguaMineral.getPrecio(), EstadoDetallePedido.SERVIDO);

            // 5. Pedido CANCELADO
            Pedido pedidoCancelado = createPedidoConEstado(null, null, mesero, TipoServicio.LLEVAR,
                    "Pedido cancelado por cliente", EstadoPedido.CANCELADO);
            createDetallePedidoConEstado(pedidoCancelado, tequenos, 2, tequenos.getPrecio(), EstadoDetallePedido.CANCELADO);

            // 6. Pedido grande para VIP
            Pedido pedidoVIP = createPedidoConEstado(clienteVIP, mesa7, mesero, TipoServicio.MESA,
                    "Pedido VIP - Mesa terraza", EstadoPedido.ABIERTO);
            createDetallePedidoConEstado(pedidoVIP, hamburguesa, 3, hamburguesa.getPrecio(), EstadoDetallePedido.EN_PREPARACION);
            createDetallePedidoConEstado(pedidoVIP, pizzaPepperoni, 2, pizzaPepperoni.getPrecio(), EstadoDetallePedido.EN_PREPARACION);
            createDetallePedidoConEstado(pedidoVIP, pastelChocolate, 3, pastelChocolate.getPrecio(), EstadoDetallePedido.PEDIDO);
            createDetallePedidoConEstado(pedidoVIP, cocaCola, 4, cocaCola.getPrecio(), EstadoDetallePedido.SERVIDO);

            // Reservas con diferentes estados
            createReserva(clienteCarlos, mesa4, LocalDateTime.now().plusDays(1), 4, EstadoReserva.CONFIRMADA, "Cena de aniversario");
            createReserva(clienteAna, mesa8, LocalDateTime.now().plusDays(2), 8, EstadoReserva.PENDIENTE, "Reunión empresarial");
            createReserva(clienteMarco, mesa7, LocalDateTime.now().plusDays(3), 6, EstadoReserva.CANCELADA, "Cumpleaños - cancelado");
            createReserva(clienteVIP, mesa8, LocalDateTime.now().plusHours(3), 10, EstadoReserva.CONFIRMADA, "Evento VIP");

            // Pagos con diferentes estados y métodos
            createPago(pedidoDelivery, efectivo, new BigDecimal("25.00"), EstadoPago.COMPLETADO, "Pago completado en efectivo");
            createPago(pedidoPagado, tarjetaCredito, new BigDecimal("35.50"), EstadoPago.COMPLETADO, "Pago procesado correctamente");
            createPago(pedidoCancelado, yape, new BigDecimal("8.00"), EstadoPago.FALLIDO, "Pedido cancelado - reembolso pendiente");
            createPago(pedidoAtendido, tarjetaDebito, new BigDecimal("45.50"), EstadoPago.PENDIENTE, "Esperando pago de mesa atendida");

            // Compras de inventario
            Compra compra1 = createCompra(proveedorBebidas, administrador, LocalDate.now().minusDays(5), EstadoCompra.RECIBIDA, "Compra de bebidas semanal");
            createDetalleCompra(compra1, cocaCola, 100, new BigDecimal("0.80"));
            createDetalleCompra(compra1, sprite, 80, new BigDecimal("0.75"));
            createDetalleCompra(compra1, aguaMineral, 120, new BigDecimal("0.50"));

            Compra compra2 = createCompra(proveedorAlimentos, administrador, LocalDate.now().minusDays(2), EstadoCompra.PENDIENTE, "Compra de ingredientes frescos");
            createDetalleCompra(compra2, hamburguesa, 50, new BigDecimal("5.50"));
            createDetalleCompra(compra2, lomoSaltado, 30, new BigDecimal("9.00"));

            Compra compra3 = createCompra(proveedorPostres, administrador, LocalDate.now(), EstadoCompra.CANCELADA, "Compra cancelada por calidad");
            createDetalleCompra(compra3, pastelChocolate, 25, new BigDecimal("2.00"));

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

    private Mesa createMesa(int numero, int capacidad, String ubicacion, EstadoMesa estado) {
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(numero);
        mesa.setCapacidad(capacidad);
        mesa.setEstado(estado);
        mesa.setUbicacion(ubicacion);
        return mesaRepository.save(mesa);
    }

    private Usuario createUsuario(String nombre, String username, String email, TipoUsuario tipoUsuario,
            String numeroDocumento, TipoDocumentoIdentidad tipoDocumento, EstadoUsuario estado) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setUsername(username);
        usuario.setPasswordHash("123456"); // En producción debería estar encriptado
        usuario.setEmail(email);
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setNumeroDocumentoIdentidad(numeroDocumento);
        usuario.setTipoDocumentoIdentidad(tipoDocumento);
        usuario.setEstado(estado);
        return usuarioRepository.save(usuario);
    }

    private Cliente createCliente(String nombre, String apellido, String email, String telefono) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        return clienteRepository.save(cliente);
    }

    private MetodoPago createMetodoPago(String nombre, String descripcion) {
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setNombre(nombre);
        metodoPago.setActivo(true);
        return metodoPagoRepository.save(metodoPago);
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

    private Pedido createPedidoConEstado(Cliente cliente, Mesa mesa, Usuario empleado, TipoServicio tipoServicio,
            String observaciones, EstadoPedido estado) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setMesa(mesa);
        pedido.setEmpleado(empleado);
        pedido.setTipoServicio(tipoServicio);
        pedido.setObservaciones(observaciones);
        pedido.setEstado(estado);
        pedido.setFechaPedido(LocalDateTime.now().minusHours((long) (Math.random() * 24)));
        return pedidoRepository.save(pedido);
    }

    private DetallePedido createDetallePedidoConEstado(Pedido pedido, Producto producto, int cantidad,
            BigDecimal precioUnitario, EstadoDetallePedido estado) {
        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setPedido(pedido);
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(cantidad);
        detallePedido.setPrecioUnitario(precioUnitario);
        detallePedido.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
        detallePedido.setEstado(estado);
        return detallePedidoRepository.save(detallePedido);
    }

    private Reserva createReserva(Cliente cliente, Mesa mesa, LocalDateTime fechaHora, int numeroPersonas,
            EstadoReserva estado, String observaciones) {
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setMesa(mesa);
        reserva.setFechaHoraReserva(fechaHora);
        reserva.setNumeroPersonas(numeroPersonas);
        reserva.setEstado(estado);
        reserva.setObservaciones(observaciones);
        return reservaRepository.save(reserva);
    }

    private Pago createPago(Pedido pedido, MetodoPago metodoPago, BigDecimal monto, EstadoPago estado, String referencia) {
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(metodoPago);
        pago.setMonto(monto);
        pago.setEstado(estado);
        pago.setReferencia(referencia);
        return pagoRepository.save(pago);
    }

    private Compra createCompra(Proveedor proveedor, Usuario usuario, LocalDate fechaCompra, EstadoCompra estado, String observaciones) {
        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setFechaCompra(fechaCompra);
        compra.setEstado(estado);
        compra.setObservaciones(observaciones);
        compra.setTotal(BigDecimal.ZERO);
        return compraRepository.save(compra);
    }

    private DetalleCompra createDetalleCompra(Compra compra, Producto producto, int cantidad, BigDecimal precioUnitario) {
        DetalleCompra detalleCompra = new DetalleCompra();
        detalleCompra.setCompra(compra);
        detalleCompra.setProducto(producto);
        detalleCompra.setCantidad(cantidad);
        detalleCompra.setPrecioUnitario(precioUnitario);
        detalleCompra.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
        return detalleCompraRepository.save(detalleCompra);
    }
}
