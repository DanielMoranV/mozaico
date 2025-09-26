package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.reports.LowStockItemResponseDTO;
import com.djasoft.mozaico.web.dtos.reports.ProductSalesResponseDTO;
import com.djasoft.mozaico.web.dtos.reports.SalesSummaryResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    SalesSummaryResponseDTO getSalesSummaryReport(LocalDate startDate, LocalDate endDate);
    List<ProductSalesResponseDTO> getProductSalesReports(LocalDate startDate, LocalDate endDate);
    List<LowStockItemResponseDTO> getLowStockInventoryReport();
}
