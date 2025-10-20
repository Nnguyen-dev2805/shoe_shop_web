// Order View Page JavaScript
// File này sẽ xử lý logic cho trang order-view.html

$(document).ready(function() {
    console.log('Order View page loaded');
    
    // Initialize page
    initializeOrderView();
    
    // Bind tab click events
    bindTabEvents();
});

// Initialize order view page
function initializeOrderView() {
    // Load orders for the first tab (IN_STOCK)
    loadOrdersByStatus('IN_STOCK');
    
    // Load order counts for all tabs
    loadOrderCounts();
}

// Bind tab click events
function bindTabEvents() {
    $('.tab-link').on('click', function(e) {
        e.preventDefault();
        
        const status = $(this).data('status');
        
        // Update active tab
        $('.tab-link').removeClass('active');
        $(this).addClass('active');
        
        // Load orders for selected status
        loadOrdersByStatus(status);
    });
}

// Load orders by status
function loadOrdersByStatus(status) {
    showLoading();
    
    $.ajax({
        url: `/user/api/orders/status/${status}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                displayOrders(response.orders, status);
            } else {
                showError('Không thể tải danh sách đơn hàng: ' + response.error);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading orders:', error);
            showError('Có lỗi xảy ra khi tải danh sách đơn hàng. Vui lòng thử lại sau.');
        }
    });
}

// Load order counts for all tabs
function loadOrderCounts() {
    const statuses = ['IN_STOCK', 'SHIPPED', 'DELIVERED', 'CANCEL', 'RETURN'];
    
    statuses.forEach(function(status) {
        $.ajax({
            url: `/user/api/orders/status/${status}`,
            method: 'GET',
            dataType: 'json',
            success: function(response) {
                if (response.success) {
                    const count = response.orders ? response.orders.length : 0;
                    $(`#count-${status}`).text(count);
                }
            },
            error: function(xhr, status, error) {
                console.error(`Error loading count for ${status}:`, error);
                $(`#count-${status}`).text('0');
            }
        });
    });
}

// Display orders
function displayOrders(orders, currentStatus) {
    const ordersList = $('#orders-list');
    const ordersLoading = $('#orders-loading');
    const ordersEmpty = $('#orders-empty');
    
    // Hide loading
    ordersLoading.hide();
    
    if (!orders || orders.length === 0) {
        // Show empty state
        ordersEmpty.show();
        ordersList.hide();
        return;
    }
    
    // Build orders HTML
    let ordersHTML = '';
    orders.forEach(function(order) {
        ordersHTML += createOrderCard(order);
    });
    
    ordersList.html(ordersHTML);
    ordersList.show();
    ordersEmpty.hide();
}

// Create order card HTML
function createOrderCard(order) {
    const statusClass = getStatusClass(order.status);
    const statusText = getStatusText(order.status);
    const payOptionText = getPayOptionText(order.payOption);
    const formattedDate = formatDate(order.createdDate);
    const formattedPrice = formatPrice(order.totalPrice);
    
    return `
        <div class="order-card">
            <div class="order-header">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <h5 style="margin: 0; font-size: 16px; font-weight: 600; color: #333;">
                            Đơn hàng #${order.id}
                        </h5>
                        <p style="margin: 5px 0 0 0; color: #666; font-size: 13px;">
                            <i class="fa fa-calendar"></i> ${formattedDate}
                        </p>
                    </div>
                    <div style="text-align: right;">
                        <span class="status-badge ${statusClass}" style="padding: 6px 12px; border-radius: 20px; font-size: 12px; font-weight: 500; text-transform: uppercase; display: inline-block; margin-bottom: 8px;">
                            ${statusText}
                        </span>
                        <p style="margin: 0; font-size: 16px; font-weight: 600; color: #ee4d2d;">
                            ${formattedPrice}
                        </p>
                    </div>
                </div>
            </div>
            <div class="order-body">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            <i class="fa fa-credit-card"></i> Thanh toán: ${payOptionText}
                        </p>
                    </div>
                    <div>
                        <a href="/user/order-detail/${order.id}" 
                           style="background: #ee4d2d; color: white; padding: 8px 16px; border-radius: 4px; text-decoration: none; font-size: 13px; font-weight: 500; display: inline-block; transition: all 0.3s ease;">
                            <i class="fa fa-eye"></i> Xem chi tiết
                        </a>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Helper functions
function getStatusClass(status) {
    switch(status) {
        case 'DELIVERED': return 'status-delivered';
        case 'SHIPPED': return 'status-shipped';
        case 'IN_STOCK': return 'status-in_stock';
        case 'CANCEL': return 'status-cancel';
        case 'RETURN': return 'status-return';
        default: return 'status-default';
    }
}

function getStatusText(status) {
    switch(status) {
        case 'DELIVERED': return 'Đã giao';
        case 'SHIPPED': return 'Đang giao';
        case 'IN_STOCK': return 'Chờ xử lý';
        case 'CANCEL': return 'Đã hủy';
        case 'RETURN': return 'Trả hàng';
        default: return 'Không xác định';
    }
}

function getPayOptionText(payOption) {
    switch(payOption) {
        case 'COD': return 'Thanh toán khi nhận hàng';
        case 'PAYOS': return 'Thanh toán PayOS';
        default: return 'Không xác định';
    }
}

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

function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

// Show loading state
function showLoading() {
    $('#orders-loading').show();
    $('#orders-list').hide();
    $('#orders-empty').hide();
}

// Show error message
function showError(message) {
    const ordersLoading = $('#orders-loading');
    const ordersList = $('#orders-list');
    const ordersEmpty = $('#orders-empty');
    
    ordersLoading.hide();
    ordersList.hide();
    ordersEmpty.hide();
    
    // Show error message
    const errorHTML = `
        <div class="empty-state">
            <i class="fa fa-exclamation-triangle" style="color: #e74c3c;"></i>
            <h3>Lỗi tải dữ liệu</h3>
            <p>${message}</p>
            <a href="#" onclick="location.reload()">
                <i class="fa fa-refresh"></i> Thử lại
            </a>
        </div>
    `;
    
    $('.orders-content').append(errorHTML);
}
