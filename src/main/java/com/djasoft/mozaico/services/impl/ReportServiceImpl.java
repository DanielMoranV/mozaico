package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.services.ReportService;
import com.djasoft.mozaico.web.dtos.reports.LowStockItemResponseDTO;
import com.djasoft.mozaico.web.dtos.reports.ProductSalesResponseDTO;
import com.djasoft.mozaico.web.dtos.reports.SalesSummaryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

        // Repositories would be injected here, e.g.:
        // private final PedidoRepository pedidoRepository;
        // private final InventarioRepository inventarioRepository;
        // private final ProductoRepository productoRepository;

        @Override
        public SalesSummaryResponseDTO getSalesSummaryReport(LocalDate startDate, LocalDate endDate) {

                return SalesSummaryResponseDTO.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .totalRevenue(new BigDecimal("15000.75"))
                                .totalOrders(250L)
                                .averageOrderValue(new BigDecimal("60.00"))
                                .build();
        }

        @Override
        public List<ProductSalesResponseDTO> getProductSalesReports(LocalDate startDate, LocalDate endDate) {

                return Arrays.asList(
                                ProductSalesResponseDTO.builder()
                                                .idProducto(1L)
                                                .nombreProducto("Pizza Pepperoni")
                                                .nombreCategoria("Pizzas")
                                                .cantidadVendida(120L)
                                                .totalVentas(new BigDecimal("2400.00"))
                                                .build(),
                                ProductSalesResponseDTO.builder()
                                                .idProducto(2L)
                                                .nombreProducto("Hamburguesa Clásica")
                                                .nombreCategoria("Hamburguesas")
                                                .cantidadVendida(90L)
                                                .totalVentas(new BigDecimal("1350.00"))
                                                .build());
        }

        @Override
        public List<LowStockItemResponseDTO> getLowStockInventoryReport() {

                return Arrays.asList(
                                LowStockItemResponseDTO.builder()
                                                .idProducto(101L)
                                                .nombreProducto("Tomates")
                                                .stockActual(5)
                                                .stockMinimo(10)
                                                .build(),
                                LowStockItemResponseDTO.builder()
                                                .idProducto(102L)
                                                .nombreProducto("Harina")
                                                .stockActual(8)
                                                .stockMinimo(15)
                                                .build());
        }
}
