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
import com.djasoft.mozaico.domain.enums.empresa.TipoOperacion;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * DataLoader - Cargador de datos de prueba para desarrollo
 *
 * ⚠️ IMPORTANTE - ARQUITECTURA MULTITENANT:
 * - Todas las entidades están aisladas por empresa (id_empresa)
 * - Los constraints UNIQUE se aplican POR EMPRESA (índices compuestos)
 * - Dos empresas PUEDEN tener categorías/mesas/clientes con el mismo nombre/número
 * - La validación de unicidad debe considerar el scope de empresa
 *
 * Este seeder crea UNA empresa de prueba llamada "Restaurante Mozaico"
 * y carga datos de ejemplo solo para esa empresa.
 */
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
        private final EmpresaRepository empresaRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                // Verificar si ya existe la empresa con el slug único
                if (empresaRepository.findBySlug("restaurante-mozaico").isPresent()) {
                        System.out.println("⚠️  Los datos de prueba ya fueron cargados anteriormente.");
                        System.out.println("   Para recargar, elimina la base de datos y vuelve a ejecutar.");
                        return;
                }

                System.out.println("Iniciando carga de datos de prueba...");

                // PASO 0: CONFIGURACIÓN DE EMPRESA - Negocio informal sin RUC
                System.out.println("0. Configurando datos de la empresa...");
                Empresa empresaMozaico = createEmpresaInformal();

                // PASO 4: PERSONAL - Usuarios del sistema (empleados)
                System.out.println("4. Registrando personal del restaurante...");

                        // Super Admin
                        Usuario developer = createUsuario("Daniel Moran Vilchez", "dmoran", "daniel.moranv94@gmail.com",
                                        TipoUsuario.SUPER_ADMIN, "70315050", TipoDocumentoIdentidad.DNI,
                                        EstadoUsuario.ACTIVO, empresaMozaico);

                        // Administración
                        Usuario administrador = createUsuario("Pedro Gonzales", "pedro.admin",
                                        "pedro.admin@mozaico.com",
                                        TipoUsuario.ADMIN, "11223344", TipoDocumentoIdentidad.DNI, EstadoUsuario.ACTIVO,
                                        empresaMozaico);

                        // Personal de caja
                        Usuario cajero = createUsuario("Sofia Ramirez", "sofia.cajero", "sofia.cajero@mozaico.com",
                                        TipoUsuario.CAJERO, "55667788", TipoDocumentoIdentidad.DNI,
                                        EstadoUsuario.ACTIVO, empresaMozaico);

                        // Personal de servicio (meseros)
                        Usuario mesero1 = createUsuario("Juan Perez", "juan.mesero", "juan.perez@mozaico.com",
                                        TipoUsuario.MESERO, "12345678", TipoDocumentoIdentidad.DNI,
                                        EstadoUsuario.ACTIVO, empresaMozaico);
                        Usuario mesero2 = createUsuario("Carlos Rodriguez", "carlos.mesero",
                                        "carlos.mesero@mozaico.com",
                                        TipoUsuario.MESERO, "12345679", TipoDocumentoIdentidad.DNI,
                                        EstadoUsuario.ACTIVO, empresaMozaico);
                        Usuario mesero3 = createUsuario("Luis Martinez", "luis.mesero", "luis.mesero@mozaico.com",
                                        TipoUsuario.MESERO, "12345680", TipoDocumentoIdentidad.DNI,
                                        EstadoUsuario.INACTIVO, empresaMozaico);

                        // Personal de cocina
                        Usuario cocinero1 = createUsuario("Maria Garcia", "maria.cocina", "maria.garcia@mozaico.com",
                                        TipoUsuario.COCINERO, "87654321", TipoDocumentoIdentidad.PASAPORTE,
                                        EstadoUsuario.ACTIVO, empresaMozaico);
                        Usuario cocinero2 = createUsuario("Ana Torres", "ana.cocina", "ana.cocina@mozaico.com",
                                        TipoUsuario.COCINERO, "87654322", TipoDocumentoIdentidad.DNI,
                                        EstadoUsuario.ACTIVO, empresaMozaico);
                        Usuario cocinero3 = createUsuario("Roberto Silva", "roberto.cocina",
                                        "roberto.cocina@mozaico.com",
                                        TipoUsuario.COCINERO, "87654323", TipoDocumentoIdentidad.CARNE_EXTRANJERIA,
                                        EstadoUsuario.SUSPENDIDO, empresaMozaico);

                        // PASO 1: ESTRUCTURA BÁSICA - Categorías de productos
                        System.out.println("1. Creando categorías...");
                        Categoria bebidas = new Categoria();
                        bebidas.setNombre("Bebidas");
                        bebidas.setDescripcion("Bebidas frías y calientes");
                        bebidas.setEmpresa(empresaMozaico);
                        bebidas.setUsuarioCreacion(administrador);
                        categoriaRepository.save(bebidas);

                        Categoria platosFuertes = new Categoria();
                        platosFuertes.setNombre("Platos Fuertes");
                        platosFuertes.setDescripcion("Platos principales y especialidades de la casa");
                        platosFuertes.setEmpresa(empresaMozaico);
                        platosFuertes.setUsuarioCreacion(administrador);
                        categoriaRepository.save(platosFuertes);

                        Categoria postres = new Categoria();
                        postres.setNombre("Postres");
                        postres.setDescripcion("Postres y dulces para endulzar tu día");
                        postres.setEmpresa(empresaMozaico);
                        postres.setUsuarioCreacion(administrador);
                        categoriaRepository.save(postres);

                        Categoria entradas = new Categoria();
                        entradas.setNombre("Entradas");
                        entradas.setDescripcion("Aperitivos y entrantes para empezar");
                        entradas.setEmpresa(empresaMozaico);
                        entradas.setUsuarioCreacion(administrador);
                        categoriaRepository.save(entradas);

                        Categoria ensaladas = new Categoria();
                        ensaladas.setNombre("Ensaladas");
                        ensaladas.setDescripcion("Opciones frescas y saludables");
                        ensaladas.setEmpresa(empresaMozaico);
                        ensaladas.setUsuarioCreacion(administrador);
                        categoriaRepository.save(ensaladas);

                        // PASO 2: CATÁLOGO DE PRODUCTOS - Organizados por categoría
                        System.out.println("2. Creando productos por categoría...");

                        // Bebidas - Sin preparación requerida
                        Producto cocaCola = createProducto("Coca-Cola", "Refresco de cola 500ml",
                                        new BigDecimal("2.50"), bebidas, false);
                        Producto sprite = createProducto("Sprite", "Refresco de lima-limón 500ml",
                                        new BigDecimal("2.50"), bebidas, false);
                        Producto aguaMineral = createProducto("Agua Mineral", "Botella de agua mineral sin gas 500ml",
                                        new BigDecimal("1.50"), bebidas, false);
                        Producto jugoNaranja = createProducto("Jugo de Naranja Natural",
                                        "Jugo de naranja recién exprimido 350ml", new BigDecimal("3.50"), bebidas,
                                        false);

                        // Entradas - Preparación mínima
                        Producto tequenos = createProducto("Tequeños con Guacamole",
                                        "6 dedos de queso frito acompañados de guacamole casero",
                                        new BigDecimal("8.00"), entradas, false);
                        Producto papasFritas = createProducto("Papas Fritas",
                                        "Porción grande de papas fritas crujientes", new BigDecimal("4.00"), entradas,
                                        false);
                        Producto panAjo = createProducto("Pan de Ajo", "Pan artesanal con mantequilla de ajo y perejil",
                                        new BigDecimal("3.00"), entradas, false);

                        // Ensaladas - Preparación fresca
                        Producto ensaladaCesar = createProducto("Ensalada César",
                                        "Lechuga romana, crutones, queso parmesano y aderezo César",
                                        new BigDecimal("9.50"), ensaladas, false);
                        Producto ensaladaCaprese = createProducto("Ensalada Caprese",
                                        "Tomate, mozzarella fresca, albahaca y aceite de oliva",
                                        new BigDecimal("11.00"), ensaladas, false);

                        // Platos Fuertes - Requieren preparación en cocina
                        Producto hamburguesa = createProducto("Hamburguesa Clásica",
                                        "Hamburguesa de carne 150g con queso, lechuga, tomate y cebolla, incluye papas",
                                        new BigDecimal("12.00"), platosFuertes, true);
                        Producto pizzaPepperoni = createProducto("Pizza Pepperoni",
                                        "Pizza familiar con salsa de tomate, mozzarella y pepperoni",
                                        new BigDecimal("15.50"), platosFuertes, true);
                        Producto lomoSaltado = createProducto("Lomo Saltado",
                                        "Clásico lomo saltado peruano con papas fritas y arroz blanco",
                                        new BigDecimal("18.00"), platosFuertes, true);
                        Producto ajiGallina = createProducto("Ají de Gallina",
                                        "Plato cremoso de gallina deshilachada con ají amarillo, papas y arroz",
                                        new BigDecimal("16.00"), platosFuertes, true);
                        Producto cevicheMixto = createProducto("Ceviche Mixto",
                                        "Pescado y mariscos frescos marinados en limón con cebolla y ají",
                                        new BigDecimal("22.00"), platosFuertes, true);

                        // Postres - Sin preparación compleja
                        Producto pastelChocolate = createProducto("Pastel de Chocolate",
                                        "Delicioso pastel de chocolate con fudge y fresas", new BigDecimal("5.00"),
                                        postres, false);
                        Producto flan = createProducto("Flan Casero", "Flan de huevo con caramelo y crema chantilly",
                                        new BigDecimal("4.50"), postres, false);
                        Producto tiramisuCasero = createProducto("Tiramisu Casero",
                                        "Postre italiano con café, mascarpone y cacao", new BigDecimal("6.50"), postres,
                                        false);

                        // PASO 3: INFRAESTRUCTURA - Mesas del restaurante
                        System.out.println("3. Creando distribución de mesas...");
                        // Mesas interiores (1-5)
                        Mesa mesa1 = createMesa(1, 4, "Ventana Principal", EstadoMesa.DISPONIBLE);
                        Mesa mesa2 = createMesa(2, 2, "Junto a Entrada", EstadoMesa.DISPONIBLE);
                        Mesa mesa3 = createMesa(3, 6, "Centro Salón", EstadoMesa.DISPONIBLE);
                        Mesa mesa4 = createMesa(4, 8, "Salón Principal", EstadoMesa.DISPONIBLE);
                        Mesa mesa5 = createMesa(5, 2, "Barra", EstadoMesa.DISPONIBLE);

                        // Mesas exteriores y especiales (6-10)
                        Mesa mesa6 = createMesa(6, 4, "Jardín Exterior", EstadoMesa.MANTENIMIENTO);
                        Mesa mesa7 = createMesa(7, 6, "Terraza VIP", EstadoMesa.DISPONIBLE);
                        Mesa mesa8 = createMesa(8, 10, "Salón Privado", EstadoMesa.DISPONIBLE);
                        Mesa mesa9 = createMesa(9, 4, "Terraza Romántica", EstadoMesa.DISPONIBLE);
                        Mesa mesa10 = createMesa(10, 12, "Salón de Eventos", EstadoMesa.DISPONIBLE);

                        // PASO 5: PROVEEDORES - Cadena de suministro
                        System.out.println("5. Registrando proveedores...");

                        Proveedor proveedorBebidas = new Proveedor();
                        proveedorBebidas.setNombre("Distribuidora Lima Bebidas S.A.C.");
                        proveedorBebidas.setContacto("Juan Carlos Mendoza");
                        proveedorBebidas.setTelefono("01-234-5678");
                        proveedorBebidas.setEmail("ventas@limabebidas.com");
                        proveedorBebidas.setDireccion("Av. Industrial 123, Lima");
                        proveedorBebidas.setActivo(true);
                        proveedorBebidas.setEmpresa(empresaMozaico);
                        proveedorBebidas.setUsuarioCreacion(administrador);
                        proveedorRepository.save(proveedorBebidas);

                        Proveedor proveedorAlimentos = new Proveedor();
                        proveedorAlimentos.setNombre("Alimentos Frescos del Valle S.R.L.");
                        proveedorAlimentos.setContacto("Ana Maria Gutierrez");
                        proveedorAlimentos.setTelefono("01-987-6543");
                        proveedorAlimentos.setEmail("pedidos@alimentosfrescos.com");
                        proveedorAlimentos.setDireccion("Carretera Central Km 25, Ate");
                        proveedorAlimentos.setActivo(true);
                        proveedorAlimentos.setEmpresa(empresaMozaico);
                        proveedorAlimentos.setUsuarioCreacion(administrador);
                        proveedorRepository.save(proveedorAlimentos);

                        Proveedor proveedorPanaderia = new Proveedor();
                        proveedorPanaderia.setNombre("Panadería y Repostería San Martin E.I.R.L.");
                        proveedorPanaderia.setContacto("Pedro Luis Castillo");
                        proveedorPanaderia.setTelefono("01-555-1122");
                        proveedorPanaderia.setEmail("info@panaderiasmmartin.com");
                        proveedorPanaderia.setDireccion("Jr. San Martin 456, Miraflores");
                        proveedorPanaderia.setActivo(true);
                        proveedorPanaderia.setEmpresa(empresaMozaico);
                        proveedorPanaderia.setUsuarioCreacion(administrador);
                        proveedorRepository.save(proveedorPanaderia);

                        // PASO 6: INVENTARIO INICIAL - Control de stock
                        System.out.println("6. Configurando inventario inicial...");

                        // Inventario de bebidas - Alto volumen
                        createInventario(cocaCola, 100, 20, 200, new BigDecimal("0.80"));
                        createInventario(sprite, 80, 15, 150, new BigDecimal("0.75"));
                        createInventario(aguaMineral, 120, 30, 250, new BigDecimal("0.50"));
                        createInventario(jugoNaranja, 60, 15, 120, new BigDecimal("1.20"));

                        // Inventario de entradas
                        createInventario(tequenos, 60, 10, 120, new BigDecimal("3.00"));
                        createInventario(papasFritas, 90, 15, 180, new BigDecimal("1.50"));
                        createInventario(panAjo, 8, 5, 25, new BigDecimal("1.00")); // Stock bajo para alertas

                        // Inventario de ensaladas - Productos frescos
                        createInventario(ensaladaCesar, 20, 5, 40, new BigDecimal("4.00"));
                        createInventario(ensaladaCaprese, 15, 3, 30, new BigDecimal("5.50"));

                        // Inventario de platos fuertes - Costos más altos
                        createInventario(hamburguesa, 50, 10, 100, new BigDecimal("5.50"));
                        createInventario(pizzaPepperoni, 40, 8, 80, new BigDecimal("7.00"));
                        createInventario(lomoSaltado, 30, 5, 60, new BigDecimal("9.00"));
                        createInventario(ajiGallina, 35, 7, 70, new BigDecimal("8.50"));
                        createInventario(cevicheMixto, 25, 5, 50, new BigDecimal("12.00"));

                        // Inventario de postres
                        createInventario(pastelChocolate, 30, 5, 50, new BigDecimal("2.00"));
                        createInventario(flan, 25, 5, 40, new BigDecimal("1.80"));
                        createInventario(tiramisuCasero, 20, 3, 35, new BigDecimal("3.50"));

                        // PASO 7: MENÚS ESPECIALES - Ofertas y combos
                        System.out.println("7. Creando menús especiales...");

                        Menu menuDelDia = new Menu();
                        menuDelDia.setNombre("Menú del Día");
                        menuDelDia.setDescripcion("Hamburguesa Clásica + Coca-Cola + Pastel de Chocolate");
                        menuDelDia.setPrecio(new BigDecimal("15.00")); // Descuento vs precio individual (20.00)
                        menuDelDia.setDisponible(true);
                        menuDelDia.getProductos().add(hamburguesa);
                        menuDelDia.getProductos().add(cocaCola);
                        menuDelDia.getProductos().add(pastelChocolate);
                        menuDelDia.setEmpresa(empresaMozaico);
                        menuDelDia.setUsuarioCreacion(administrador);
                        menuRepository.save(menuDelDia);

                        Menu menuVegetariano = new Menu();
                        menuVegetariano.setNombre("Menú Vegetariano");
                        menuVegetariano.setDescripcion("Ensalada César + Agua Mineral + Tiramisu");
                        menuVegetariano.setPrecio(new BigDecimal("14.50")); // Descuento vs precio individual (17.50)
                        menuVegetariano.setDisponible(true);
                        menuVegetariano.getProductos().add(ensaladaCesar);
                        menuVegetariano.getProductos().add(aguaMineral);
                        menuVegetariano.getProductos().add(tiramisuCasero);
                        menuVegetariano.setEmpresa(empresaMozaico);
                        menuVegetariano.setUsuarioCreacion(administrador);
                        menuRepository.save(menuVegetariano);

                        Menu menuEjecutivo = new Menu();
                        menuEjecutivo.setNombre("Menú Ejecutivo");
                        menuEjecutivo.setDescripcion("Lomo Saltado + Jugo de Naranja + Flan Casero");
                        menuEjecutivo.setPrecio(new BigDecimal("22.00")); // Descuento vs precio individual (26.00)
                        menuEjecutivo.setDisponible(true);
                        menuEjecutivo.getProductos().add(lomoSaltado);
                        menuEjecutivo.getProductos().add(jugoNaranja);
                        menuEjecutivo.getProductos().add(flan);
                        menuEjecutivo.setEmpresa(empresaMozaico);
                        menuEjecutivo.setUsuarioCreacion(administrador);
                        menuRepository.save(menuEjecutivo);

                        Menu menuFamiliar = new Menu();
                        menuFamiliar.setNombre("Menú Familiar");
                        menuFamiliar.setDescripcion("Pizza Pepperoni + 2 Coca-Colas + Ensalada Caprese + Papas Fritas");
                        menuFamiliar.setPrecio(new BigDecimal("32.00")); // Descuento vs precio individual (36.50)
                        menuFamiliar.setDisponible(true);
                        menuFamiliar.getProductos().add(pizzaPepperoni);
                        menuFamiliar.getProductos().add(cocaCola);
                        menuFamiliar.getProductos().add(ensaladaCaprese);
                        menuFamiliar.getProductos().add(papasFritas);
                        menuFamiliar.setEmpresa(empresaMozaico);
                        menuFamiliar.setUsuarioCreacion(administrador);
                        menuRepository.save(menuFamiliar);

                        // PASO 8: BASE DE CLIENTES - Clientes frecuentes
                        System.out.println("8. Registrando clientes...");

                        Cliente clienteCarlos = createCliente("Carlos", "Santana", "carlos.santana@gmail.com",
                                        "987654321");
                        Cliente clienteAna = createCliente("Ana", "Gomez", "ana.gomez@outlook.com", "111222333");
                        Cliente clientePedro = createCliente("Pedro", "Ramirez", "pedro.ramirez@hotmail.com",
                                        "444555666");
                        Cliente clienteLucia = createCliente("Lucia", "Torres", "lucia.torres@yahoo.com", "555666777");
                        Cliente clienteMarco = createCliente("Marco", "Diaz", "marco.diaz@gmail.com", "888999000");
                        Cliente clienteVIP = createCliente("Isabella", "Morales", "isabella.morales@empresarial.com",
                                        "999000111");
                        Cliente clienteJorge = createCliente("Jorge", "Vargas", "jorge.vargas@gmail.com", "777888999");
                        Cliente clienteCarmen = createCliente("Carmen", "Lopez", "carmen.lopez@outlook.com",
                                        "666777888");

                        // PASO 9: MÉTODOS DE PAGO - Opciones disponibles
                        System.out.println("9. Configurando métodos de pago...");

                        MetodoPago efectivo = createMetodoPagoCompleto("Efectivo",
                                        "Pago en efectivo - billetes y monedas");
                        MetodoPago tarjetaCredito = createMetodoPagoCompleto("Tarjeta de Crédito",
                                        "Visa, Mastercard, American Express");
                        MetodoPago tarjetaDebito = createMetodoPagoCompleto("Tarjeta de Débito",
                                        "Débito bancario nacional e internacional");
                        MetodoPago yape = createMetodoPagoCompleto("Yape", "Transferencia móvil Yape BCP");
                        MetodoPago plin = createMetodoPagoCompleto("Plin", "Transferencia móvil Plin Interbank");
                        MetodoPago transferencia = createMetodoPagoCompleto("Transferencia Bancaria",
                                        "Transferencia interbancaria online");

                        // PASO 10: OPERACIÓN DIARIA - Pedidos en diferentes estados
                        System.out.println("10. Simulando operación diaria con pedidos...");

                        // === PEDIDOS ACTIVOS (Estado: ABIERTO) ===

                        // Mesa 1: Cliente Carlos - Pedido recién tomado
                        Pedido pedido1 = createPedidoConFecha(clienteCarlos, mesa1, mesero1, TipoServicio.MESA,
                                        "Cliente regular - pedido del mediodía", EstadoPedido.ABIERTO,
                                        LocalDateTime.now().minusMinutes(10), empresaMozaico, administrador);
                        // Actualizar estado de mesa
                        mesa1.setEstado(EstadoMesa.OCUPADA);
                        mesaRepository.save(mesa1);
                        createDetallePedidoConEstado(pedido1, hamburguesa, 1, hamburguesa.getPrecio(),
                                        EstadoDetallePedido.PEDIDO);
                        createDetallePedidoConEstado(pedido1, cocaCola, 1, cocaCola.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        recalcularTotalesPedido(pedido1);

                        // Mesa 2: Cliente Ana - Pedido en preparación
                        Pedido pedido2 = createPedidoConFecha(clienteAna, mesa2, mesero2, TipoServicio.MESA,
                                        "Mesa para dos - pedido en cocina", EstadoPedido.ABIERTO,
                                        LocalDateTime.now().minusMinutes(25), empresaMozaico, administrador);
                        mesa2.setEstado(EstadoMesa.OCUPADA);
                        mesaRepository.save(mesa2);
                        createDetallePedidoConEstado(pedido2, pizzaPepperoni, 1, pizzaPepperoni.getPrecio(),
                                        EstadoDetallePedido.EN_PREPARACION);
                        createDetallePedidoConEstado(pedido2, ensaladaCesar, 1, ensaladaCesar.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        createDetallePedidoConEstado(pedido2, sprite, 2, sprite.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        recalcularTotalesPedido(pedido2);

                        // Mesa 7: Cliente VIP - Pedido grande en proceso
                        Pedido pedidoVIP = createPedidoConFecha(clienteVIP, mesa7, mesero1, TipoServicio.MESA,
                                        "Evento empresarial VIP", EstadoPedido.ABIERTO,
                                        LocalDateTime.now().minusMinutes(35), empresaMozaico, administrador);
                        mesa7.setEstado(EstadoMesa.OCUPADA);
                        mesaRepository.save(mesa7);
                        createDetallePedidoConEstado(pedidoVIP, cevicheMixto, 2, cevicheMixto.getPrecio(),
                                        EstadoDetallePedido.EN_PREPARACION);
                        createDetallePedidoConEstado(pedidoVIP, lomoSaltado, 2, lomoSaltado.getPrecio(),
                                        EstadoDetallePedido.EN_PREPARACION);
                        createDetallePedidoConEstado(pedidoVIP, jugoNaranja, 4, jugoNaranja.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        createDetallePedidoConEstado(pedidoVIP, tiramisuCasero, 3, tiramisuCasero.getPrecio(),
                                        EstadoDetallePedido.PEDIDO);
                        recalcularTotalesPedido(pedidoVIP);

                        // === PEDIDOS COMPLETADOS HOY ===

                        // Pedido delivery completado hace 2 horas
                        Pedido pedidoDelivery1 = createPedidoConFecha(clienteLucia, null, mesero2,
                                        TipoServicio.DELIVERY,
                                        "Delivery exitoso", EstadoPedido.PAGADO, LocalDateTime.now().minusHours(2),
                                        empresaMozaico, administrador);
                        pedidoDelivery1.setDireccionDelivery("Av. Larco 456, Miraflores");
                        pedidoRepository.save(pedidoDelivery1);
                        createDetallePedidoConEstado(pedidoDelivery1, ajiGallina, 1, ajiGallina.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        createDetallePedidoConEstado(pedidoDelivery1, aguaMineral, 2, aguaMineral.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        createDetallePedidoConEstado(pedidoDelivery1, flan, 1, flan.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        recalcularTotalesPedido(pedidoDelivery1);

                        // Pedido para llevar completado hace 1 hora
                        Pedido pedidoLlevar = createPedidoConFecha(clientePedro, null, cajero, TipoServicio.LLEVAR,
                                        "Pedido para llevar pagado", EstadoPedido.PAGADO,
                                        LocalDateTime.now().minusHours(1), empresaMozaico, administrador);
                        createDetallePedidoConEstado(pedidoLlevar, tequenos, 2, tequenos.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        createDetallePedidoConEstado(pedidoLlevar, papasFritas, 1, papasFritas.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        createDetallePedidoConEstado(pedidoLlevar, cocaCola, 2, cocaCola.getPrecio(),
                                        EstadoDetallePedido.SERVIDO);
                        recalcularTotalesPedido(pedidoLlevar);

                        // === PEDIDOS CANCELADOS ===

                        // Pedido cancelado hace 30 minutos
                        Pedido pedidoCancelado = createPedidoConFecha(clienteMarco, null, mesero1,
                                        TipoServicio.DELIVERY,
                                        "Cliente canceló - problemas de dirección", EstadoPedido.CANCELADO,
                                        LocalDateTime.now().minusMinutes(30), empresaMozaico, administrador);
                        pedidoCancelado.setDireccionDelivery("Dirección incorrecta");
                        pedidoRepository.save(pedidoCancelado);
                        createDetallePedidoConEstado(pedidoCancelado, hamburguesa, 1, hamburguesa.getPrecio(),
                                        EstadoDetallePedido.CANCELADO);
                        createDetallePedidoConEstado(pedidoCancelado, sprite, 1, sprite.getPrecio(),
                                        EstadoDetallePedido.CANCELADO);
                        recalcularTotalesPedido(pedidoCancelado);

                        // PASO 11: SISTEMA DE RESERVAS - Planificación futura
                        System.out.println("11. Configurando reservas...");

                        // Reserva para mañana - Mesa 4 (8 personas)
                        // Nota: La mesa se mantiene DISPONIBLE hasta que llegue el horario de la reserva
                        Reserva reserva1 = createReserva(clienteCarlos, mesa4,
                                        LocalDateTime.now().plusDays(1).withHour(19).withMinute(30),
                                        4, EstadoReserva.CONFIRMADA, "Cena de aniversario - mesa especial",
                                        empresaMozaico, administrador);

                        // Reserva para pasado mañana - Mesa 8 (Salón Privado)
                        // Nota: La mesa se mantiene DISPONIBLE hasta que llegue el horario de la reserva
                        createReserva(clienteAna, mesa8, LocalDateTime.now().plusDays(2).withHour(12).withMinute(0),
                                        8, EstadoReserva.CONFIRMADA, "Almuerzo empresarial - grupo de trabajo",
                                        empresaMozaico, administrador);

                        // Reserva pendiente de confirmación
                        createReserva(clienteJorge, mesa9, LocalDateTime.now().plusDays(3).withHour(20).withMinute(0),
                                        4, EstadoReserva.PENDIENTE, "Cena romántica - esperando confirmación",
                                        empresaMozaico, administrador);

                        // Reserva cancelada
                        createReserva(clienteMarco, mesa10, LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                                        12, EstadoReserva.CANCELADA, "Cumpleaños familiar - cancelado por cliente",
                                        empresaMozaico, administrador);

                        // Reserva VIP para hoy en la noche
                        createReserva(clienteVIP, mesa10, LocalDateTime.now().withHour(21).withMinute(0),
                                        10, EstadoReserva.CONFIRMADA, "Evento corporativo VIP - cena de gala",
                                        empresaMozaico, administrador);

                        // PASO 12: PROCESAMIENTO DE PAGOS - Transacciones
                        System.out.println("12. Registrando pagos...");

                        // Pagos de pedidos completados
                        createPago(pedidoDelivery1, efectivo, pedidoDelivery1.getTotal(), EstadoPago.COMPLETADO,
                                        "Pago en efectivo al delivery - recibo #D001", empresaMozaico, administrador);
                        createPago(pedidoLlevar, tarjetaCredito, pedidoLlevar.getTotal(), EstadoPago.COMPLETADO,
                                        "Visa ****1234 - Terminal POS #002", empresaMozaico, administrador);

                        // Pago fallido del pedido cancelado
                        createPago(pedidoCancelado, yape, pedidoCancelado.getTotal(), EstadoPago.FALLIDO,
                                        "Transacción cancelada - reembolso automático", empresaMozaico, administrador);

                        // Pagos pendientes (para pedidos activos - normalmente se pagan al final)
                        // En un escenario real, estos pagos se crearían cuando el cliente paga

                        // PASO 13: GESTIÓN DE COMPRAS - Adquisiciones a proveedores
                        System.out.println("13. Registrando compras a proveedores...");

                        // Compra recibida la semana pasada - Bebidas
                        Compra compraBebidas = createCompraCompleta(proveedorBebidas, administrador,
                                        LocalDate.now().minusDays(5),
                                        EstadoCompra.RECIBIDA, "Compra semanal de bebidas - Factura #B-2024-001",
                                        empresaMozaico, administrador);
                        createDetalleCompra(compraBebidas, cocaCola, 100, new BigDecimal("0.80"));
                        createDetalleCompra(compraBebidas, sprite, 80, new BigDecimal("0.75"));
                        createDetalleCompra(compraBebidas, aguaMineral, 120, new BigDecimal("0.50"));
                        createDetalleCompra(compraBebidas, jugoNaranja, 60, new BigDecimal("1.20"));
                        recalcularTotalCompra(compraBebidas);

                        // Compra pendiente - Ingredientes frescos
                        Compra compraAlimentos = createCompraCompleta(proveedorAlimentos, administrador,
                                        LocalDate.now().minusDays(2),
                                        EstadoCompra.PENDIENTE, "Reposición ingredientes frescos - Esperando entrega",
                                        empresaMozaico, administrador);
                        createDetalleCompra(compraAlimentos, cevicheMixto, 25, new BigDecimal("12.00"));
                        createDetalleCompra(compraAlimentos, ensaladaCesar, 20, new BigDecimal("4.00"));
                        createDetalleCompra(compraAlimentos, ensaladaCaprese, 15, new BigDecimal("5.50"));
                        recalcularTotalCompra(compraAlimentos);

                        // Compra de productos de panadería - Recibida ayer
                        Compra compraPanaderia = createCompraCompleta(proveedorPanaderia, administrador,
                                        LocalDate.now().minusDays(1),
                                        EstadoCompra.RECIBIDA, "Productos de panadería frescos - Factura #P-2024-015",
                                        empresaMozaico, administrador);
                        createDetalleCompra(compraPanaderia, pastelChocolate, 30, new BigDecimal("2.00"));
                        createDetalleCompra(compraPanaderia, flan, 25, new BigDecimal("1.80"));
                        createDetalleCompra(compraPanaderia, tiramisuCasero, 20, new BigDecimal("3.50"));
                        createDetalleCompra(compraPanaderia, panAjo, 25, new BigDecimal("1.00"));
                        recalcularTotalCompra(compraPanaderia);

                        // Compra cancelada por problemas de calidad
                        Compra compraCancelada = createCompraCompleta(proveedorAlimentos, administrador,
                                        LocalDate.now().minusDays(3),
                                        EstadoCompra.CANCELADA,
                                        "Compra cancelada - productos no cumplían estándares de calidad",
                                        empresaMozaico, administrador);
                        createDetalleCompra(compraCancelada, hamburguesa, 50, new BigDecimal("5.50"));
                        createDetalleCompra(compraCancelada, lomoSaltado, 30, new BigDecimal("9.00"));
                        recalcularTotalCompra(compraCancelada);

                        System.out.println("✅ Carga de datos completada exitosamente!");
                        System.out.println("📊 Resumen de datos cargados:");
                        System.out.println("   - 1 Empresa configurada (INFORMAL - Sin RUC)");
                        System.out.println("   - 5 Categorías de productos");
                        System.out.println("   - 16 Productos en catálogo");
                        System.out.println("   - 10 Mesas distribuidas");
                        System.out.println("   - 8 Empleados registrados");
                        System.out.println("   - 3 Proveedores activos");
                        System.out.println("   - Inventario inicial configurado");
                        System.out.println("   - 4 Menús especiales");
                        System.out.println("   - 8 Clientes registrados");
                        System.out.println("   - 6 Métodos de pago");
                System.out.println("   - Simulación de operación diaria activa");
                System.out.println("   - Sistema de reservas configurado");
                System.out.println("   - Historial de compras a proveedores");
                System.out.println("🎟️  CONFIGURACIÓN: Negocio informal - Solo emite tickets sin IGV");
        }

        private Producto createProducto(String nombre, String descripcion, BigDecimal precio, Categoria categoria,
                        boolean requierePreparacion) {
                Empresa empresa = empresaRepository.findByActivaTrue()
                                .orElseThrow(() -> new RuntimeException("No hay empresa activa"));
                Usuario administrador = usuarioRepository.findByUsername("pedro.admin")
                                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

                Producto producto = new Producto();
                producto.setNombre(nombre);
                producto.setDescripcion(descripcion);
                producto.setPrecio(precio);
                producto.setCategoria(categoria);
                producto.setRequierePreparacion(requierePreparacion);
                producto.setDisponible(true); // Productos disponibles por defecto
                producto.setEstado(EstadoProducto.ACTIVO);
                producto.setEmpresa(empresa);
                producto.setUsuarioCreacion(administrador);
                return productoRepository.save(producto);
        }

        private Mesa createMesa(int numero, int capacidad, String ubicacion, EstadoMesa estado) {
                Empresa empresa = empresaRepository.findByActivaTrue()
                                .orElseThrow(() -> new RuntimeException("No hay empresa activa"));
                Usuario administrador = usuarioRepository.findByUsername("pedro.admin")
                                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

                Mesa mesa = new Mesa();
                mesa.setNumeroMesa(numero);
                mesa.setCapacidad(capacidad);
                mesa.setEstado(estado);
                mesa.setUbicacion(ubicacion);
                mesa.setEmpresa(empresa);
                mesa.setUsuarioCreacion(administrador);
                return mesaRepository.save(mesa);
        }

        private Usuario createUsuario(String nombre, String username, String email, TipoUsuario tipoUsuario,
                        String numeroDocumento, TipoDocumentoIdentidad tipoDocumento, EstadoUsuario estado,
                        Empresa empresa) {
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre);
                usuario.setUsername(username);
                usuario.setPasswordHash(passwordEncoder.encode("123456")); // Contraseña encriptada con BCrypt
                usuario.setEmail(email);
                usuario.setTipoUsuario(tipoUsuario);
                usuario.setNumeroDocumentoIdentidad(numeroDocumento);
                usuario.setTipoDocumentoIdentidad(tipoDocumento);
                usuario.setEstado(estado);
                usuario.setEmpresa(empresa);
                usuario.setTokenVersion(0L); // Inicializar para JWT
                usuario.setIntentosFallidos(0); // Inicializar contador de intentos fallidos
                return usuarioRepository.save(usuario);
        }

        private Cliente createCliente(String nombre, String apellido, String email, String telefono) {
                Empresa empresa = empresaRepository.findByActivaTrue()
                                .orElseThrow(() -> new RuntimeException("No hay empresa activa"));
                Usuario administrador = usuarioRepository.findByUsername("pedro.admin")
                                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

                Cliente cliente = Cliente.builder()
                                .nombre(nombre)
                                .apellido(apellido)
                                .email(email)
                                .telefono(telefono)
                                .tipoPersona(com.djasoft.mozaico.domain.enums.cliente.TipoPersona.NATURAL)
                                .empresa(empresa)
                                .usuarioCreacion(administrador)
                                .build();
                return clienteRepository.save(cliente);
        }

        private MetodoPago createMetodoPagoCompleto(String nombre, String descripcion) {
                Empresa empresa = empresaRepository.findByActivaTrue()
                                .orElseThrow(() -> new RuntimeException("No hay empresa activa"));
                Usuario administrador = usuarioRepository.findByUsername("pedro.admin")
                                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

                MetodoPago metodoPago = new MetodoPago();
                metodoPago.setNombre(nombre);
                metodoPago.setActivo(true);
                metodoPago.setEmpresa(empresa);
                metodoPago.setUsuarioCreacion(administrador);
                return metodoPagoRepository.save(metodoPago);
        }

        private void createInventario(Producto producto, int stock, int min, int max, BigDecimal costo) {
                Empresa empresa = empresaRepository.findByActivaTrue()
                                .orElseThrow(() -> new RuntimeException("No hay empresa activa"));
                Usuario administrador = usuarioRepository.findByUsername("pedro.admin")
                                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

                Inventario inventario = new Inventario();
                inventario.setProducto(producto);
                inventario.setStockActual(stock);
                inventario.setStockMinimo(min);
                inventario.setStockMaximo(max);
                inventario.setCostoUnitario(costo);
                inventario.setEmpresa(empresa);
                inventario.setUsuarioCreacion(administrador);
                inventarioRepository.save(inventario);
        }

        private Pedido createPedidoConEstado(Cliente cliente, Mesa mesa, Usuario empleado, TipoServicio tipoServicio,
                        String observaciones, EstadoPedido estado, Empresa empresa, Usuario usuarioCreacion) {
                Pedido pedido = new Pedido();
                pedido.setCliente(cliente);
                pedido.setMesa(mesa);
                pedido.setEmpleado(empleado);
                pedido.setTipoServicio(tipoServicio);
                pedido.setObservaciones(observaciones);
                pedido.setEstado(estado);
                pedido.setFechaPedido(LocalDateTime.now().minusHours((long) (Math.random() * 24)));
                pedido.setEmpresa(empresa);
                pedido.setUsuarioCreacion(usuarioCreacion);
                return pedidoRepository.save(pedido);
        }

        private Pedido createPedidoConFecha(Cliente cliente, Mesa mesa, Usuario empleado, TipoServicio tipoServicio,
                        String observaciones, EstadoPedido estado, LocalDateTime fecha, Empresa empresa,
                        Usuario usuarioCreacion) {
                Pedido pedido = new Pedido();
                pedido.setCliente(cliente);
                pedido.setMesa(mesa);
                pedido.setEmpleado(empleado);
                pedido.setTipoServicio(tipoServicio);
                pedido.setObservaciones(observaciones);
                pedido.setEstado(estado);
                pedido.setFechaPedido(fecha);
                pedido.setEmpresa(empresa);
                pedido.setUsuarioCreacion(usuarioCreacion);
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
                        EstadoReserva estado, String observaciones, Empresa empresa, Usuario usuarioCreacion) {
                Reserva reserva = new Reserva();
                reserva.setCliente(cliente);
                reserva.setMesa(mesa);
                reserva.setFechaHoraReserva(fechaHora);
                reserva.setNumeroPersonas(numeroPersonas);
                reserva.setEstado(estado);
                reserva.setObservaciones(observaciones);
                reserva.setEmpresa(empresa);
                reserva.setUsuarioCreacion(usuarioCreacion);
                return reservaRepository.save(reserva);
        }

        private Pago createPago(Pedido pedido, MetodoPago metodoPago, BigDecimal monto, EstadoPago estado,
                        String referencia, Empresa empresa, Usuario usuarioCreacion) {
                Pago pago = new Pago();
                pago.setPedido(pedido);
                pago.setMetodoPago(metodoPago);
                pago.setMonto(monto);
                pago.setEstado(estado);
                pago.setReferencia(referencia);
                pago.setEmpresa(empresa);
                pago.setUsuarioCreacion(usuarioCreacion);
                return pagoRepository.save(pago);
        }

        private Compra createCompraCompleta(Proveedor proveedor, Usuario usuario, LocalDate fechaCompra,
                        EstadoCompra estado,
                        String observaciones, Empresa empresa, Usuario usuarioCreacion) {
                Compra compra = new Compra();
                compra.setProveedor(proveedor);
                compra.setFechaCompra(fechaCompra);
                compra.setEstado(estado);
                compra.setObservaciones(observaciones);
                compra.setTotal(BigDecimal.ZERO);
                compra.setEmpresa(empresa);
                compra.setUsuarioCreacion(usuarioCreacion);
                return compraRepository.save(compra);
        }

        private DetalleCompra createDetalleCompra(Compra compra, Producto producto, int cantidad,
                        BigDecimal precioUnitario) {
                DetalleCompra detalleCompra = new DetalleCompra();
                detalleCompra.setCompra(compra);
                detalleCompra.setProducto(producto);
                detalleCompra.setCantidad(cantidad);
                detalleCompra.setPrecioUnitario(precioUnitario);
                detalleCompra.setSubtotal(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
                return detalleCompraRepository.save(detalleCompra);
        }

        private void recalcularTotalesPedido(Pedido pedido) {
                // Obtener todos los detalles del pedido
                List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);

                // Calcular subtotal sumando todos los subtotales de los detalles
                BigDecimal subtotal = detalles.stream()
                                .map(DetallePedido::getSubtotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Calcular impuestos - Solo si la empresa aplica IGV
                Empresa empresa = empresaRepository.findByActivaTrue().orElse(null);
                BigDecimal impuestos = BigDecimal.ZERO;
                if (empresa != null && empresa.getAplicaIgv()) {
                        impuestos = subtotal.multiply(empresa.getPorcentajeIgv().divide(new BigDecimal("100")));
                        System.out.println("💰 IGV aplicado: " + empresa.getPorcentajeIgv() + "% = S/ " + impuestos);
                } else {
                        System.out.println("🎟️ Empresa informal - Sin IGV aplicado");
                }

                // Descuento por ahora 0
                BigDecimal descuento = BigDecimal.ZERO;

                // Calcular total
                BigDecimal total = subtotal.add(impuestos).subtract(descuento);

                // Actualizar el pedido
                pedido.setSubtotal(subtotal);
                pedido.setImpuestos(impuestos);
                pedido.setDescuento(descuento);
                pedido.setTotal(total);

                pedidoRepository.save(pedido);
        }

        private void recalcularTotalCompra(Compra compra) {
                // Obtener todos los detalles de la compra
                List<DetalleCompra> detalles = detalleCompraRepository.findByCompra(compra);

                // Calcular total sumando todos los subtotales de los detalles
                BigDecimal total = detalles.stream()
                                .map(DetalleCompra::getSubtotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Actualizar el total de la compra
                compra.setTotal(total);
                compraRepository.save(compra);
        }

        private Empresa createEmpresaInformal() {
                Empresa empresa = Empresa.builder()
                                .nombre("Restaurante Mozaico")
                                .slug("restaurante-mozaico") // Slug único para URLs públicas
                                .descripcion("Restaurante familiar especializado en comida criolla y fusión, ubicado en el corazón de la ciudad. Ofrecemos un ambiente acogedor para disfrutar en familia.")
                                .direccion("Jr. Los Pinos 456, Distrito de San Miguel, Lima")
                                .telefono("01-234-5678")
                                .email("contacto@restaurantemozaico.com")
                                .paginaWeb("www.restaurantemozaico.com")
                                .logoUrl("/assets/images/logo-mozaico.png")
                                .activa(true)
                                .tipoOperacion(TipoOperacion.TICKET_SIMPLE) // Negocio informal
                                .aplicaIgv(false) // NO aplica IGV
                                .porcentajeIgv(new BigDecimal("18.00")) // Por si luego se formaliza
                                .moneda("PEN")
                                .prefijoTicket("MOZ")
                                .correlativoTicket(1L)
                                .datosFacturacion(null) // Sin datos de facturación
                                .build();

                return empresaRepository.save(empresa);
        }
}