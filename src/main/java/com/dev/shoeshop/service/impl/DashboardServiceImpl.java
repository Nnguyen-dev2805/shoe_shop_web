package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.dto.dashboard.*;
import com.dev.shoeshop.enums.ShipmentStatus;
import com.dev.shoeshop.repository.OrderDetailRepository;
import com.dev.shoeshop.repository.OrderRepository;
import com.dev.shoeshop.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    
    @Override
    public DashboardStatsDTO getDashboardStats(Date startDate, Date endDate) {
        // Lấy các thống kê tổng quan với date range
        Long totalOrders = orderRepository.countTotalOrders(startDate, endDate);
        Double totalRevenue = orderRepository.calculateTotalRevenue(startDate, endDate);
        Long totalProductsSold = orderRepository.countTotalProductsSold(startDate, endDate);
        Long totalCustomers = orderRepository.countTotalCustomers(startDate, endDate);
        
        // Lấy số lượng đơn hàng theo trạng thái
        Map<String, Long> ordersByStatus = getOrdersByStatus(startDate, endDate);
        
        // Lấy thống kê theo thời gian
        List<OrderTimeSeriesDTO> orderTimeSeries = getOrderTimeSeries(startDate, endDate);
        List<RevenueTimeSeriesDTO> revenueTimeSeries = getRevenueTimeSeries(startDate, endDate);
        
        // Lấy top 10 sản phẩm bán chạy (hiển thị trên dashboard, KHÔNG xuất trong Excel tổng quát)
        List<TopProductDTO> topProducts = getTopProducts(10, startDate, endDate);
        
        return DashboardStatsDTO.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalProductsSold(totalProductsSold)
                .totalCustomers(totalCustomers)
                .ordersByStatus(ordersByStatus)
                .orderTimeSeries(orderTimeSeries)
                .revenueTimeSeries(revenueTimeSeries)
                .topProducts(topProducts) // Hiển thị trên giao diện, nhưng không có trong Excel "all"
                .build();
    }
    
    /**
     * Lấy số lượng đơn hàng theo trạng thái
     */
    private Map<String, Long> getOrdersByStatus(Date startDate, Date endDate) {
        List<Object[]> results = orderRepository.countOrdersByStatus(startDate, endDate);
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        
        // Khởi tạo tất cả các status với giá trị 0
        for (ShipmentStatus status : ShipmentStatus.values()) {
            ordersByStatus.put(status.name(), 0L);
        }
        
        // Cập nhật giá trị thực tế
        for (Object[] result : results) {
            ShipmentStatus status = (ShipmentStatus) result[0];
            Long count = (Long) result[1];
            ordersByStatus.put(status.name(), count);
        }
        
        return ordersByStatus;
    }
    
    /**
     * Lấy thống kê đơn hàng theo ngày
     */
    private List<OrderTimeSeriesDTO> getOrderTimeSeries(Date startDate, Date endDate) {
        List<Object[]> results = orderRepository.getOrderTimeSeries(startDate, endDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        return results.stream()
                .map(result -> OrderTimeSeriesDTO.builder()
                        .date(dateFormat.format((Date) result[0]))
                        .orderCount((Long) result[1])
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy thống kê doanh thu theo ngày
     */
    private List<RevenueTimeSeriesDTO> getRevenueTimeSeries(Date startDate, Date endDate) {
        List<Object[]> results = orderRepository.getRevenueTimeSeries(startDate, endDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        return results.stream()
                .map(result -> RevenueTimeSeriesDTO.builder()
                        .date(dateFormat.format((Date) result[0]))
                        .revenue((Double) result[1])
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy top N sản phẩm bán chạy nhất
     */
    private List<TopProductDTO> getTopProducts(int limit, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = orderDetailRepository.findTopSellingProducts(startDate, endDate, pageable);
        
        return results.stream()
                .map(result -> TopProductDTO.builder()
                        .productId(((Number) result[0]).longValue())
                        .productName((String) result[1])
                        .productImage((String) result[2])
                        .quantitySold(((Number) result[3]).longValue())
                        .totalRevenue((Double) result[4])
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TopProductDTO> getProductsByRevenue(Date startDate, Date endDate, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = orderDetailRepository.findProductsByRevenue(startDate, endDate, pageable);
        
        return results.stream()
                .map(result -> TopProductDTO.builder()
                        .productId(((Number) result[0]).longValue())
                        .productName((String) result[1])
                        .productImage((String) result[2])
                        .quantitySold(((Number) result[3]).longValue())
                        .totalRevenue((Double) result[4])
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TopProductDTO> getProductsByQuantity(Date startDate, Date endDate, int limit) {
        // Sử dụng lại query findTopSellingProducts vì đã sắp xếp theo quantity
        return getTopProducts(limit, startDate, endDate);
    }
    
    @Override
    public List<TopCustomerDTO> getTopCustomers(Date startDate, Date endDate, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = orderRepository.findTopCustomers(startDate, endDate, pageable);
        
        return results.stream()
                .map(result -> TopCustomerDTO.builder()
                        .customerId(((Number) result[0]).longValue())
                        .customerName((String) result[1])
                        .customerEmail((String) result[2])
                        .customerPhone((String) result[3])
                        .totalOrders(((Number) result[4]).longValue())
                        .totalProducts(0L) // Set to 0 for now, can be calculated separately if needed
                        .totalSpent((Double) result[5])
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public byte[] exportToExcel(String type, Date startDate, Date endDate, String username) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            
            switch (type.toLowerCase()) {
                case "all":
                    createAllReportSheet(workbook, startDate, endDate, username, headerStyle, currencyStyle);
                    break;
                case "revenue":
                    createProductsByRevenueSheet(workbook, startDate, endDate, username, headerStyle, currencyStyle);
                    break;
                case "products-sold":
                    createProductsByQuantitySheet(workbook, startDate, endDate, username, headerStyle, currencyStyle);
                    break;
                case "customers":
                    createTopCustomersSheet(workbook, startDate, endDate, username, headerStyle, currencyStyle);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid export type: " + type);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat((short) 8); // Currency format
        return style;
    }
    
    private void createAllReportSheet(Workbook workbook, Date startDate, Date endDate, String username, 
                                     CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Báo Cáo Tổng Hợp");
        
        int rowNum = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        // Metadata
        Row metaRow1 = sheet.createRow(rowNum++);
        metaRow1.createCell(0).setCellValue("BÁOCÁO THỐNG KÊ DASHBOARD");
        
        Row metaRow2 = sheet.createRow(rowNum++);
        metaRow2.createCell(0).setCellValue("Ngày tạo: " + sdf.format(new Date()));
        
        Row metaRow3 = sheet.createRow(rowNum++);
        metaRow3.createCell(0).setCellValue("Người tạo: " + username);
        
        if (startDate != null || endDate != null) {
            Row metaRow4 = sheet.createRow(rowNum++);
            String dateRange = "Từ ngày: " + (startDate != null ? dateFormat.format(startDate) : "Tất cả") + 
                               " - Đến ngày: " + (endDate != null ? dateFormat.format(endDate) : "Tất cả");
            metaRow4.createCell(0).setCellValue(dateRange);
        }
        
        rowNum++; // Empty row
        
        // Get stats
        DashboardStatsDTO stats = getDashboardStats(startDate, endDate);
        
        // Statistics section
        Row statsHeaderRow = sheet.createRow(rowNum++);
        statsHeaderRow.createCell(0).setCellValue("Thống Kê Tổng Quan");
        
        Row statsRow1 = sheet.createRow(rowNum++);
        statsRow1.createCell(0).setCellValue("Tổng Đơn Hàng:");
        statsRow1.createCell(1).setCellValue(stats.getTotalOrders());
        
        Row statsRow2 = sheet.createRow(rowNum++);
        statsRow2.createCell(0).setCellValue("Tổng Doanh Thu:");
        Cell revenueCell = statsRow2.createCell(1);
        revenueCell.setCellValue(stats.getTotalRevenue());
        revenueCell.setCellStyle(currencyStyle);
        
        Row statsRow3 = sheet.createRow(rowNum++);
        statsRow3.createCell(0).setCellValue("Sản Phẩm Đã Bán:");
        statsRow3.createCell(1).setCellValue(stats.getTotalProductsSold());
        
        Row statsRow4 = sheet.createRow(rowNum++);
        statsRow4.createCell(0).setCellValue("Tổng Khách Hàng:");
        statsRow4.createCell(1).setCellValue(stats.getTotalCustomers());
        
        rowNum++; // Empty row
        
        // KHÔNG XUẤT Top Products trong báo cáo tổng quát
        // Top products chỉ hiển thị trên giao diện dashboard
        // Nếu muốn xuất top products, sử dụng báo cáo riêng:
        // - "Sản Phẩm Theo Doanh Thu" (type=revenue)
        // - "Sản Phẩm Theo Số Lượng" (type=products-sold)
        
        // Daily Statistics section
        Row dailyHeaderRow = sheet.createRow(rowNum++);
        dailyHeaderRow.createCell(0).setCellValue("THỐNG KÊ THEO NGÀY");
        
        Row dailyTitleRow = sheet.createRow(rowNum++);
        dailyTitleRow.createCell(0).setCellValue("Ngày");
        dailyTitleRow.createCell(1).setCellValue("Số Đơn Hàng");
        dailyTitleRow.createCell(2).setCellValue("Doanh Thu");
        for (int i = 0; i < 3; i++) {
            dailyTitleRow.getCell(i).setCellStyle(headerStyle);
        }
        
        // Merge order and revenue time series by date
        Map<String, Long> ordersByDate = new LinkedHashMap<>();
        for (OrderTimeSeriesDTO ots : stats.getOrderTimeSeries()) {
            ordersByDate.put(ots.getDate(), ots.getOrderCount());
        }
        
        Map<String, Double> revenueByDate = new LinkedHashMap<>();
        for (RevenueTimeSeriesDTO rts : stats.getRevenueTimeSeries()) {
            revenueByDate.put(rts.getDate(), rts.getRevenue());
        }
        
        // Get all unique dates and sort
        Set<String> allDates = new TreeSet<>();
        allDates.addAll(ordersByDate.keySet());
        allDates.addAll(revenueByDate.keySet());
        
        for (String date : allDates) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(date);
            row.createCell(1).setCellValue(ordersByDate.getOrDefault(date, 0L));
            Cell revCell = row.createCell(2);
            revCell.setCellValue(revenueByDate.getOrDefault(date, 0.0));
            revCell.setCellStyle(currencyStyle);
        }
        
        // Set column widths (in units of 1/256th of a character width)
        sheet.setColumnWidth(0, 25 * 256); // ~25 characters
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 20 * 256);
    }
    
    private void createProductsByRevenueSheet(Workbook workbook, Date startDate, Date endDate, String username,
                                             CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Sản Phẩm Theo Doanh Thu");
        addMetadata(sheet, "SẢN PHẨM THEO DOANH THU", startDate, endDate, username);
        
        int rowNum = 5;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("#");
        headerRow.createCell(1).setCellValue("Tên Sản Phẩm");
        headerRow.createCell(2).setCellValue("Số Lượng Bán");
        headerRow.createCell(3).setCellValue("Doanh Thu");
        for (int i = 0; i < 4; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<TopProductDTO> products = getProductsByRevenue(startDate, endDate, 100);
        for (int i = 0; i < products.size(); i++) {
            TopProductDTO product = products.get(i);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(product.getProductName());
            row.createCell(2).setCellValue(product.getQuantitySold());
            Cell cell = row.createCell(3);
            cell.setCellValue(product.getTotalRevenue());
            cell.setCellStyle(currencyStyle);
        }
        
        // Set column widths
        sheet.setColumnWidth(0, 8 * 256);   // # column
        sheet.setColumnWidth(1, 40 * 256);  // Product name
        sheet.setColumnWidth(2, 15 * 256);  // Quantity
        sheet.setColumnWidth(3, 20 * 256);  // Revenue
    }
    
    private void createProductsByQuantitySheet(Workbook workbook, Date startDate, Date endDate, String username,
                                              CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Sản Phẩm Theo Số Lượng");
        addMetadata(sheet, "SẢN PHẨM THEO SỐ LƯỢNG BÁN", startDate, endDate, username);
        
        int rowNum = 5;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("#");
        headerRow.createCell(1).setCellValue("Tên Sản Phẩm");
        headerRow.createCell(2).setCellValue("Số Lượng Bán");
        headerRow.createCell(3).setCellValue("Doanh Thu");
        for (int i = 0; i < 4; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<TopProductDTO> products = getProductsByQuantity(startDate, endDate, 100);
        for (int i = 0; i < products.size(); i++) {
            TopProductDTO product = products.get(i);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(product.getProductName());
            row.createCell(2).setCellValue(product.getQuantitySold());
            Cell cell = row.createCell(3);
            cell.setCellValue(product.getTotalRevenue());
            cell.setCellStyle(currencyStyle);
        }
        
        // Set column widths
        sheet.setColumnWidth(0, 8 * 256);   // # column
        sheet.setColumnWidth(1, 40 * 256);  // Product name
        sheet.setColumnWidth(2, 15 * 256);  // Quantity
        sheet.setColumnWidth(3, 20 * 256);  // Revenue
    }
    
    private void createTopCustomersSheet(Workbook workbook, Date startDate, Date endDate, String username,
                                        CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Top Khách Hàng");
        addMetadata(sheet, "TOP KHÁCH HÀNG MUA NHIỀU NHẤT", startDate, endDate, username);
        
        int rowNum = 5;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("#");
        headerRow.createCell(1).setCellValue("Tên Khách Hàng");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Số Điện Thoại");
        headerRow.createCell(4).setCellValue("Tổng Đơn");
        headerRow.createCell(5).setCellValue("Tổng Chi Tiêu");
        for (int i = 0; i < 6; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<TopCustomerDTO> customers = getTopCustomers(startDate, endDate, 100);
        for (int i = 0; i < customers.size(); i++) {
            TopCustomerDTO customer = customers.get(i);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(customer.getCustomerName());
            row.createCell(2).setCellValue(customer.getCustomerEmail());
            row.createCell(3).setCellValue(customer.getCustomerPhone() != null ? customer.getCustomerPhone() : "N/A");
            row.createCell(4).setCellValue(customer.getTotalOrders());
            Cell cell = row.createCell(5);
            cell.setCellValue(customer.getTotalSpent());
            cell.setCellStyle(currencyStyle);
        }
        
        // Set column widths
        sheet.setColumnWidth(0, 8 * 256);   // # column
        sheet.setColumnWidth(1, 30 * 256);  // Customer name
        sheet.setColumnWidth(2, 30 * 256);  // Email
        sheet.setColumnWidth(3, 15 * 256);  // Phone
        sheet.setColumnWidth(4, 12 * 256);  // Total orders
        sheet.setColumnWidth(5, 20 * 256);  // Total spent
    }
    
    private void addMetadata(Sheet sheet, String title, Date startDate, Date endDate, String username) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(title);
        
        Row dateCreatedRow = sheet.createRow(1);
        dateCreatedRow.createCell(0).setCellValue("Ngày tạo: " + sdf.format(new Date()));
        
        Row creatorRow = sheet.createRow(2);
        creatorRow.createCell(0).setCellValue("Người tạo: " + username);
        
        if (startDate != null || endDate != null) {
            Row dateRangeRow = sheet.createRow(3);
            String dateRange = "Từ ngày: " + (startDate != null ? dateFormat.format(startDate) : "Tất cả") + 
                               " - Đến ngày: " + (endDate != null ? dateFormat.format(endDate) : "Tất cả");
            dateRangeRow.createCell(0).setCellValue(dateRange);
        }
    }
}
