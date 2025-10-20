/**
 * Manager Orders JavaScript
 * Deeg Manager V6.0 - Vietnamese Locale
 */

let currentPage = 0;
let currentStatus = '';

$(document).ready(function() {
    loadOrderStatistics();
    loadOrders();
    
    // Status filter change event
    $('#status-filter').on('change', function() {
        currentStatus = $(this).val();
        currentPage = 0;
        loadOrders();
    });
});

/**
 * Load order statistics
 */
function loadOrderStatistics() {
    $.ajax({
        url: '/api/manager/orders/statistics',
        method: 'GET',
        success: function(data) {
            updateStatistics(data);
        },
        error: function(xhr, status, error) {
            console.error('Lỗi tải thống kê đơn hàng:', error);
            showAlert('danger', 'Không thể tải thống kê đơn hàng');
        }
    });
}

/**
 * Update statistics with animation
 */
function updateStatistics(stats) {
    animateValue('cancel-orders', 0, stats.cancel, 1000);
    animateValue('shipping-orders', 0, stats.shipping, 1200);
    animateValue('instock-orders', 0, stats.inStock, 1400);
    animateValue('delivered-orders', 0, stats.delivered, 1600);
    animateValue('return-orders', 0, stats.preturn, 1800);
}

/**
 * Animate number counting
 */
function animateValue(elementId, start, end, duration) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const range = end - start;
    const increment = range / (duration / 16); // 60fps
    let current = start;
    
    const timer = setInterval(function() {
        current += increment;
        if (current >= end) {
            current = end;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current);
    }, 16);
}

/**
 * Load orders with pagination
 */
function loadOrders(page = 0) {
    currentPage = page;
    
    const params = {
        page: page,
        size: 10
    };
    
    if (currentStatus) {
        params.status = currentStatus;
    }
    
    $.ajax({
        url: '/api/manager/orders',
        method: 'GET',
        data: params,
        success: function(data) {
            displayOrders(data.content);
            displayPagination(data);
        },
        error: function(xhr, status, error) {
            console.error('Lỗi tải đơn hàng:', error);
            displayOrdersError();
        }
    });
}

/**
 * Display orders in table
 */
function displayOrders(orders) {
    const tbody = $('#orders-table-body');
    
    if (!orders || orders.length === 0) {
        tbody.html(`
            <tr>
                <td colspan="7" class="text-center py-4">
                    <iconify-icon icon="solar:box-bold-duotone" class="fs-48 text-muted mb-3"></iconify-icon>
                    <h5 class="text-muted">Không có đơn hàng nào</h5>
                    <p class="text-muted">Chưa có đơn hàng phù hợp với bộ lọc</p>
                </td>
            </tr>
        `);
        return;
    }
    
    let html = '';
    orders.forEach(order => {
        const statusBadge = getStatusBadge(order.status);
        const paymentBadge = getPaymentBadge(order.payOption);
        
        html += `
            <tr class="fade-in">
                <td>
                    <strong>#${order.id}</strong>
                </td>
                <td>${formatDate(order.createdDate)}</td>
                <td>
                    <div>
                        <strong>${order.customerName}</strong>
                        ${order.customerEmail ? `<br><small class="text-muted">${order.customerEmail}</small>` : ''}
                    </div>
                </td>
                <td>
                    <strong class="text-success">${formatCurrency(order.totalPrice)}</strong>
                </td>
                <td>${paymentBadge}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="d-flex gap-2 justify-content-center">
                        <button class="btn btn-light btn-sm" onclick="viewOrderDetail(${order.id})" title="Xem chi tiết">
                            <iconify-icon icon="solar:eye-bold-duotone" class="fs-16"></iconify-icon>
                        </button>
                        ${order.status === 'IN_STOCK' ? `
                            <button class="btn btn-danger btn-sm" onclick="cancelOrder(${order.id})" title="Hủy đơn hàng">
                                <iconify-icon icon="solar:trash-bin-minimalistic-bold-duotone" class="fs-16"></iconify-icon>
                            </button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `;
    });
    
    tbody.html(html);
}

/**
 * Display orders error
 */
function displayOrdersError() {
    const tbody = $('#orders-table-body');
    tbody.html(`
        <tr>
            <td colspan="7" class="text-center py-4">
                <iconify-icon icon="solar:danger-circle-bold-duotone" class="fs-48 text-danger mb-3"></iconify-icon>
                <h5 class="text-danger">Không thể tải đơn hàng</h5>
                <p class="text-muted">Vui lòng thử lại sau</p>
                <button class="btn btn-primary" onclick="loadOrders()">
                    <iconify-icon icon="solar:refresh-bold-duotone" class="me-1"></iconify-icon>
                    Thử lại
                </button>
            </td>
        </tr>
    `);
}

/**
 * Display pagination
 */
function displayPagination(pageData) {
    const container = $('#pagination-container');
    
    if (pageData.totalPages <= 1) {
        container.empty();
        return;
    }
    
    let html = '';
    
    // Previous button
    if (pageData.number > 0) {
        html += `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="loadOrders(${pageData.number - 1})">
                    <iconify-icon icon="solar:arrow-left-bold-duotone" class="me-1"></iconify-icon>
                    Trước
                </a>
            </li>
        `;
    }
    
    // Page numbers
    const startPage = Math.max(0, pageData.number - 2);
    const endPage = Math.min(pageData.totalPages - 1, pageData.number + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        html += `
            <li class="page-item ${i === pageData.number ? 'active' : ''}">
                <a class="page-link" href="javascript:void(0)" onclick="loadOrders(${i})">${i + 1}</a>
            </li>
        `;
    }
    
    // Next button
    if (pageData.number < pageData.totalPages - 1) {
        html += `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="loadOrders(${pageData.number + 1})">
                    Sau
                    <iconify-icon icon="solar:arrow-right-bold-duotone" class="ms-1"></iconify-icon>
                </a>
            </li>
        `;
    }
    
    container.html(html);
}

/**
 * Get status badge HTML
 */
function getStatusBadge(status) {
    const statusConfig = {
        'IN_STOCK': { class: 'badge badge-status-in-stock', text: 'Trong Kho' },
        'SHIPPED': { class: 'badge badge-status-shipped', text: 'Đang Giao' },
        'DELIVERED': { class: 'badge badge-status-delivered', text: 'Đã Giao' },
        'CANCEL': { class: 'badge badge-status-cancel', text: 'Đã Hủy' },
        'RETURN': { class: 'badge badge-status-return', text: 'Hoàn Trả' }
    };
    
    const config = statusConfig[status] || { class: 'badge bg-secondary', text: status };
    
    return `<span class="${config.class} px-2 py-1 fs-13">${config.text}</span>`;
}

/**
 * Get payment badge HTML
 */
function getPaymentBadge(payOption) {
    const paymentConfig = {
        'COD': { class: 'badge badge-payment-cod', text: 'Tiền mặt' },
        'BANKING': { class: 'badge badge-payment-banking', text: 'Banking' },
        'VNPAY': { class: 'badge badge-payment-vnpay', text: 'VNPay' }
    };
    
    const config = paymentConfig[payOption] || { class: 'badge bg-secondary', text: payOption };
    
    return `<span class="${config.class} px-2 py-1 fs-13">${config.text}</span>`;
}

/**
 * Format currency to VND
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Format date to Vietnamese format
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * View order detail
 */
function viewOrderDetail(orderId) {
    window.location.href = `/manager/orders/${orderId}`;
}

/**
 * Cancel order
 */
function cancelOrder(orderId) {
    if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')) {
        return;
    }
    
    $.ajax({
        url: `/api/manager/orders/${orderId}/cancel`,
        method: 'POST',
        success: function(response) {
            showAlert('success', 'Hủy đơn hàng thành công');
            loadOrders(currentPage);
            loadOrderStatistics();
        },
        error: function(xhr, status, error) {
            const message = xhr.responseText || 'Không thể hủy đơn hàng';
            showAlert('danger', message);
        }
    });
}

/**
 * Refresh orders
 */
function refreshOrders() {
    currentPage = 0;
    loadOrderStatistics();
    loadOrders();
    showAlert('info', 'Đã làm mới danh sách đơn hàng');
}

/**
 * Show alert message
 */
function showAlert(type, message) {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <iconify-icon icon="solar:info-circle-bold-duotone" class="me-2"></iconify-icon>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    // Insert alert at the top of container
    $('.container-xxl').prepend(alertHtml);
    
    // Auto dismiss after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut();
    }, 5000);
}
