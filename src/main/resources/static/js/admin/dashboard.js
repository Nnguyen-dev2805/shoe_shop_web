/**
 * Admin Dashboard JavaScript
 * Handles API calls and chart rendering for dashboard statistics
 */

// Global variables for charts (to destroy before re-rendering)
let ordersByStatusChart = null;
let ordersTimeSeriesChart = null;
let revenueTimeSeriesChart = null;

$(document).ready(function() {
    // Load data on page load
    loadDashboardData();
    
    // Filter button click handler
    $('#filterBtn').on('click', function() {
        loadDashboardData();
    });
    
    // Reset button click handler
    $('#resetBtn').on('click', function() {
        $('#startDate').val('');
        $('#endDate').val('');
        loadDashboardData();
    });
    
    // Enter key handler for date inputs
    $('#startDate, #endDate').on('keypress', function(e) {
        if (e.which === 13) {
            loadDashboardData();
        }
    });
    
    // Stat card click handlers
    $('.stat-card.clickable').on('click', function() {
        const modalId = $(this).data('modal');
        if (modalId) {
            const modal = new bootstrap.Modal(document.getElementById(modalId));
            modal.show();
            
            // Update date range display
            updateModalDateRanges();
            
            // Load data based on modal type
            if (modalId === 'revenueModal') {
                loadProductsByRevenue();
            } else if (modalId === 'productsSoldModal') {
                loadProductsByQuantity();
            } else if (modalId === 'customersModal') {
                loadTopCustomers();
            }
        }
    });
    
    // Export Excel button handlers
    $('#exportMainExcel').on('click', function() {
        exportDashboardExcel('all');
    });
    
    $('#exportRevenueExcel').on('click', function() {
        exportDashboardExcel('revenue');
    });
    
    $('#exportProductsSoldExcel').on('click', function() {
        exportDashboardExcel('products-sold');
    });
    
    $('#exportCustomersExcel').on('click', function() {
        exportDashboardExcel('customers');
    });
});

/**
 * Load all dashboard data from API with optional date range
 */
function loadDashboardData() {
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    
    // Build query parameters
    let params = {};
    if (startDate) {
        params.startDate = startDate;
    }
    if (endDate) {
        params.endDate = endDate;
    }
    
    $.ajax({
        url: '/api/admin/dashboard/stats',
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function(data) {
            console.log('Dashboard data loaded:', data);
            
            // Update stat cards
            updateStatCards(data);
            
            // Render charts (destroy old ones first)
            renderOrdersByStatusChart(data.ordersByStatus);
            renderOrdersTimeSeriesChart(data.orderTimeSeries);
            renderRevenueTimeSeriesChart(data.revenueTimeSeries);
            
            // Update top products table
            updateTopProductsTable(data.topProducts);
            
            // Update date range display in modals
            updateModalDateRanges();
        },
        error: function(xhr, status, error) {
            console.error('Error loading dashboard data:', error);
            showErrorMessage();
        }
    });
}

/**
 * Update stat cards with data
 */
function updateStatCards(data) {
    // Total Orders
    $('#totalOrders').html(formatNumber(data.totalOrders || 0));
    
    // Total Revenue
    $('#totalRevenue').html(formatCurrency(data.totalRevenue || 0));
    
    // Total Products Sold
    $('#totalProductsSold').html(formatNumber(data.totalProductsSold || 0));
    
    // Total Customers
    $('#totalCustomers').html(formatNumber(data.totalCustomers || 0));
    
    // ✅ NEW: Inventory & Profit Stats
    // Total Inventory Value
    $('#totalInventoryValue').html(formatCurrency(data.totalInventoryValue || 0));
    
    // Total Profit
    $('#totalProfit').html(formatCurrency(data.totalProfit || 0));
    
    // Profit Margin
    const profitMargin = data.profitMargin || 0;
    $('#profitMargin').html(profitMargin.toFixed(1) + '%');
    
    // Total COGS
    $('#totalCOGS').html(formatCurrency(data.totalCOGS || 0));
    
    // Average ROI
    const avgROI = data.avgROI || 0;
    $('#avgROI').html(avgROI.toFixed(1) + '%');
}

/**
 * Render Orders by Status Pie Chart
 */
function renderOrdersByStatusChart(ordersByStatus) {
    if (!ordersByStatus) return;
    
    // Destroy old chart if exists
    if (ordersByStatusChart) {
        ordersByStatusChart.destroy();
    }
    
    const statusLabels = {
        'IN_STOCK': 'Chờ Xác Nhận',
        'SHIPPED': 'Đang Vận Chuyển',
        'DELIVERED': 'Đã Giao',
        'CANCEL': 'Đã Hủy',
        'RETURN': 'Đã Trả Hàng'
    };
    
    const statusColors = {
        'IN_STOCK': '#FFA500',
        'SHIPPED': '#3B82F6',
        'DELIVERED': '#10B981',
        'CANCEL': '#EF4444',
        'RETURN': '#6B7280'
    };
    
    const labels = [];
    const series = [];
    const colors = [];
    
    for (const [status, count] of Object.entries(ordersByStatus)) {
        if (count > 0) {
            labels.push(statusLabels[status] || status);
            series.push(count);
            colors.push(statusColors[status] || '#6B7280');
        }
    }
    
    const options = {
        series: series,
        chart: {
            type: 'donut',
            height: 300
        },
        labels: labels,
        colors: colors,
        legend: {
            show: false
        },
        dataLabels: {
            enabled: true,
            formatter: function(val, opts) {
                return opts.w.config.series[opts.seriesIndex];
            }
        },
        plotOptions: {
            pie: {
                donut: {
                    size: '65%',
                    labels: {
                        show: true,
                        total: {
                            show: true,
                            label: 'Tổng',
                            fontSize: '16px',
                            fontWeight: 600,
                            formatter: function (w) {
                                return w.globals.seriesTotals.reduce((a, b) => a + b, 0);
                            }
                        }
                    }
                }
            }
        },
        responsive: [{
            breakpoint: 480,
            options: {
                chart: {
                    height: 250
                }
            }
        }]
    };
    
    ordersByStatusChart = new ApexCharts(document.querySelector("#ordersByStatusChart"), options);
    ordersByStatusChart.render();
    
    // Create legend
    let legendHtml = '<div class="row">';
    for (let i = 0; i < labels.length; i++) {
        legendHtml += `
            <div class="col-6 mb-2">
                <div class="d-flex align-items-center">
                    <div class="badge" style="background-color: ${colors[i]}; width: 12px; height: 12px; border-radius: 2px;"></div>
                    <span class="ms-2 fs-13">${labels[i]}: <strong>${series[i]}</strong></span>
                </div>
            </div>
        `;
    }
    legendHtml += '</div>';
    $('#statusLegend').html(legendHtml);
}

/**
 * Render Orders Time Series Chart
 */
function renderOrdersTimeSeriesChart(timeSeries) {
    // Destroy old chart if exists
    if (ordersTimeSeriesChart) {
        ordersTimeSeriesChart.destroy();
    }
    
    if (!timeSeries || timeSeries.length === 0) {
        $('#ordersTimeSeriesChart').html('<p class="text-center text-muted">Chưa có dữ liệu</p>');
        return;
    }
    
    // Reverse to show oldest to newest
    const sortedData = [...timeSeries].reverse();
    
    const categories = sortedData.map(item => item.date);
    const data = sortedData.map(item => item.orderCount);
    
    const options = {
        series: [{
            name: 'Số đơn hàng',
            data: data
        }],
        chart: {
            type: 'area',
            height: 350,
            toolbar: {
                show: true
            }
        },
        dataLabels: {
            enabled: false
        },
        stroke: {
            curve: 'smooth',
            width: 2
        },
        xaxis: {
            categories: categories,
            labels: {
                rotate: -45,
                rotateAlways: false
            }
        },
        yaxis: {
            title: {
                text: 'Số đơn hàng'
            }
        },
        colors: ['#3B82F6'],
        fill: {
            type: 'gradient',
            gradient: {
                shadeIntensity: 1,
                opacityFrom: 0.7,
                opacityTo: 0.3,
                stops: [0, 90, 100]
            }
        },
        tooltip: {
            x: {
                format: 'dd/MM/yyyy'
            }
        }
    };
    
    ordersTimeSeriesChart = new ApexCharts(document.querySelector("#ordersTimeSeriesChart"), options);
    ordersTimeSeriesChart.render();
}

/**
 * Render Revenue Time Series Chart
 */
function renderRevenueTimeSeriesChart(timeSeries) {
    // Destroy old chart if exists
    if (revenueTimeSeriesChart) {
        revenueTimeSeriesChart.destroy();
    }
    
    if (!timeSeries || timeSeries.length === 0) {
        $('#revenueTimeSeriesChart').html('<p class="text-center text-muted">Chưa có dữ liệu</p>');
        return;
    }
    
    // Reverse to show oldest to newest
    const sortedData = [...timeSeries].reverse();
    
    const categories = sortedData.map(item => item.date);
    const data = sortedData.map(item => item.revenue);
    
    const options = {
        series: [{
            name: 'Doanh thu',
            data: data
        }],
        chart: {
            type: 'bar',
            height: 350,
            toolbar: {
                show: true
            }
        },
        plotOptions: {
            bar: {
                borderRadius: 4,
                dataLabels: {
                    position: 'top'
                }
            }
        },
        dataLabels: {
            enabled: true,
            formatter: function (val) {
                return formatCurrencyShort(val);
            },
            offsetY: -20,
            style: {
                fontSize: '10px',
                colors: ["#304758"]
            }
        },
        xaxis: {
            categories: categories,
            labels: {
                rotate: -45,
                rotateAlways: false
            }
        },
        yaxis: {
            title: {
                text: 'Doanh thu (VNĐ)'
            },
            labels: {
                formatter: function (val) {
                    return formatCurrencyShort(val);
                }
            }
        },
        colors: ['#10B981'],
        tooltip: {
            y: {
                formatter: function (val) {
                    return formatCurrency(val);
                }
            }
        }
    };
    
    revenueTimeSeriesChart = new ApexCharts(document.querySelector("#revenueTimeSeriesChart"), options);
    revenueTimeSeriesChart.render();
}

/**
 * Update date range display in modals
 */
function updateModalDateRanges() {
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    
    let dateRangeText = 'Tất cả';
    
    if (startDate && endDate) {
        dateRangeText = `Từ ${formatDate(startDate)} đến ${formatDate(endDate)}`;
    } else if (startDate) {
        dateRangeText = `Từ ${formatDate(startDate)}`;
    } else if (endDate) {
        dateRangeText = `Đến ${formatDate(endDate)}`;
    }
    
    // Update all modal date range displays
    $('#revenueModalDateRange').text(dateRangeText);
    $('#productsSoldModalDateRange').text(dateRangeText);
    $('#customersModalDateRange').text(dateRangeText);
}

/**
 * Format date to Vietnamese format
 */
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

/**
 * Update top products table (Hiển thị trên dashboard, KHÔNG có trong Excel tổng quát)
 */
function updateTopProductsTable(products) {
    const tbody = $('#topProductsTableBody');
    
    if (!products || products.length === 0) {
        tbody.html('<tr><td colspan="5" class="text-center text-muted">Chưa có dữ liệu</td></tr>');
        return;
    }
    
    let html = '';
    products.forEach((product, index) => {
        html += `
            <tr>
                <td>${index + 1}</td>
                <td>
                    <div class="d-flex align-items-center">
                        <div class="flex-grow-1">
                            <h6 class="mb-0">${escapeHtml(product.productName)}</h6>
                            <small class="text-muted">ID: ${product.productId}</small>
                        </div>
                    </div>
                </td>
                <td>
                    ${product.productImage 
                        ? `<img src="${escapeHtml(product.productImage)}" alt="${escapeHtml(product.productName)}" 
                               class="rounded" style="width: 50px; height: 50px; object-fit: cover;">` 
                        : '<div class="bg-light rounded d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;"><i class="bx bx-image"></i></div>'}
                </td>
                <td class="text-end">
                    <span class="badge bg-primary-subtle text-primary px-2 py-1">
                        ${formatNumber(product.quantitySold)} sản phẩm
                    </span>
                </td>
                <td class="text-end">
                    <strong class="text-success">${formatCurrency(product.totalRevenue)}</strong>
                </td>
            </tr>
        `;
    });
    
    tbody.html(html);
}

/**
 * Format number with thousand separator
 */
function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(num);
}

/**
 * Format currency in VND
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Format currency in short form (K, M)
 */
function formatCurrencyShort(amount) {
    if (amount >= 1000000) {
        return (amount / 1000000).toFixed(1) + 'M';
    } else if (amount >= 1000) {
        return (amount / 1000).toFixed(1) + 'K';
    }
    return amount.toFixed(0);
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

/**
 * Show error message
 */
function showErrorMessage() {
    const errorHtml = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lỗi!</strong> Không thể tải dữ liệu thống kê. Vui lòng thử lại sau.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    $('.container-fluid').prepend(errorHtml);
}

/**
 * Load products by revenue for modal
 */
function loadProductsByRevenue() {
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    
    let params = { limit: 20 };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    
    $.ajax({
        url: '/api/admin/dashboard/products-by-revenue',
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function(products) {
            populateProductsTable(products, '#revenueTableBody');
        },
        error: function(xhr, status, error) {
            console.error('Error loading products by revenue:', error);
            $('#revenueTableBody').html('<tr><td colspan="5" class="text-center text-danger">Không thể tải dữ liệu</td></tr>');
        }
    });
}

/**
 * Load products by quantity for modal
 */
function loadProductsByQuantity() {
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    
    let params = { limit: 20 };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    
    $.ajax({
        url: '/api/admin/dashboard/products-by-quantity',
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function(products) {
            populateProductsTable(products, '#productsSoldTableBody');
        },
        error: function(xhr, status, error) {
            console.error('Error loading products by quantity:', error);
            $('#productsSoldTableBody').html('<tr><td colspan="5" class="text-center text-danger">Không thể tải dữ liệu</td></tr>');
        }
    });
}

/**
 * Load top customers for modal
 */
function loadTopCustomers() {
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    
    let params = { limit: 20 };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    
    $.ajax({
        url: '/api/admin/dashboard/top-customers',
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function(customers) {
            populateCustomersTable(customers);
        },
        error: function(xhr, status, error) {
            console.error('Error loading top customers:', error);
            $('#customersTableBody').html('<tr><td colspan="7" class="text-center text-danger">Không thể tải dữ liệu</td></tr>');
        }
    });
}

/**
 * Populate products table in modal
 */
function populateProductsTable(products, tableBodySelector) {
    const tbody = $(tableBodySelector);
    
    if (!products || products.length === 0) {
        tbody.html('<tr><td colspan="5" class="text-center text-muted">Chưa có dữ liệu</td></tr>');
        return;
    }
    
    let html = '';
    products.forEach((product, index) => {
        html += `
            <tr>
                <td><strong>${index + 1}</strong></td>
                <td>
                    ${product.productImage 
                        ? `<img src="${escapeHtml(product.productImage)}" alt="${escapeHtml(product.productName)}" 
                               class="rounded" style="width: 50px; height: 50px; object-fit: cover;">` 
                        : '<div class="bg-light rounded d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;"><i class="bx bx-image"></i></div>'}
                </td>
                <td>
                    <div class="fw-semibold">${escapeHtml(product.productName)}</div>
                    <small class="text-muted">ID: ${product.productId}</small>
                </td>
                <td class="text-end">
                    <span class="badge bg-primary-subtle text-primary">${formatNumber(product.quantitySold)}</span>
                </td>
                <td class="text-end">
                    <strong class="text-success">${formatCurrency(product.totalRevenue)}</strong>
                </td>
            </tr>
        `;
    });
    
    tbody.html(html);
}

/**
 * Populate customers table in modal
 */
function populateCustomersTable(customers) {
    const tbody = $('#customersTableBody');
    
    if (!customers || customers.length === 0) {
        tbody.html('<tr><td colspan="6" class="text-center text-muted">Chưa có dữ liệu</td></tr>');
        return;
    }
    
    let html = '';
    customers.forEach((customer, index) => {
        html += `
            <tr>
                <td><strong>${index + 1}</strong></td>
                <td>
                    <div class="d-flex align-items-center">
                        <div class="avatar-sm bg-primary bg-opacity-10 rounded-circle d-flex align-items-center justify-content-center me-2">
                            <i class="bx bx-user text-primary"></i>
                        </div>
                        <div>
                            <div class="fw-semibold">${escapeHtml(customer.customerName)}</div>
                            <small class="text-muted">ID: ${customer.customerId}</small>
                        </div>
                    </div>
                </td>
                <td>${escapeHtml(customer.customerEmail)}</td>
                <td>${escapeHtml(customer.customerPhone || 'N/A')}</td>
                <td class="text-end">
                    <span class="badge bg-info-subtle text-info">${formatNumber(customer.totalOrders)} đơn</span>
                </td>
                <td class="text-end">
                    <strong class="text-success">${formatCurrency(customer.totalSpent)}</strong>
                </td>
            </tr>
        `;
    });
    
    tbody.html(html);
}

/**
 * Export dashboard data to Excel
 * @param {string} type - Type of export: 'all', 'revenue', 'products-sold', 'customers'
 */
function exportDashboardExcel(type) {
    const startDate = $('#startDate').val();
    const endDate = $('#endDate').val();
    
    // Build query parameters
    let params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    params.append('type', type);
    
    // Construct download URL
    const url = `/api/admin/dashboard/export-excel?${params.toString()}`;
    
    // Create temporary link and trigger download
    const link = document.createElement('a');
    link.href = url;
    link.download = `dashboard_report_${type}_${new Date().getTime()}.xlsx`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    // Show success notification
    showNotification('Đang tải xuống file Excel...', 'info');
}

/**
 * Show notification message
 */
function showNotification(message, type = 'success') {
    const alertClass = type === 'success' ? 'alert-success' : type === 'info' ? 'alert-info' : 'alert-danger';
    const html = `
        <div class="alert ${alertClass} alert-dismissible fade show position-fixed top-0 end-0 m-3" role="alert" style="z-index: 9999;">
            <strong>${message}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    $('body').append(html);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        $('.alert').fadeOut('slow', function() {
            $(this).remove();
        });
    }, 3000);
}
