package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.ReportService;
import com.djasoft.mozaico.web.dtos.reports.LowStockItemResponseDTO;
import com.djasoft.mozaico.web.dtos.reports.ProductSalesResponseDTO;
import com.djasoft.mozaico.web.dtos.reports.SalesSummaryResponseDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales-summary")
    public ResponseEntity<ApiResponse<SalesSummaryResponseDTO>> getSalesSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SalesSummaryResponseDTO report = reportService.getSalesSummaryReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Reporte de resumen de ventas generado exitosamente"));
    }

    @GetMapping("/product-sales")
    public ResponseEntity<ApiResponse<List<ProductSalesResponseDTO>>> getProductSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ProductSalesResponseDTO> reports = reportService.getProductSalesReports(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(reports, "Reporte de ventas por producto generado exitosamente"));
    }

    @GetMapping("/low-stock-inventory")
    public ResponseEntity<ApiResponse<List<LowStockItemResponseDTO>>> getLowStockInventory() {
        List<LowStockItemResponseDTO> reports = reportService.getLowStockInventoryReport();
        return ResponseEntity.ok(ApiResponse.success(reports, "Reporte de inventario bajo stock generado exitosamente"));
    }
}
