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
        ordersHTML += createOrderCard(order, currentStatus);
    });
    
    ordersList.html(ordersHTML);
    ordersList.show();
    ordersEmpty.hide();
    
    // Bind cancel order events after rendering
    bindCancelOrderEvents();
    
    // Add return buttons for DELIVERED orders (if within 7 days)
    if (typeof window.addReturnButtonsToOrders === 'function') {
        addReturnButtonsToDeliveredOrders(orders);
    }
}

// Create order card HTML (Shopee Style)
function createOrderCard(order, currentStatus) {
    const statusClass = getStatusClass(order.status);
    const statusText = getStatusText(order.status);
    const borderColor = getStatusBorderColor(order.status); // ✅ Get border color based on status
    const formattedDate = formatDate(order.createdDate);
    const formattedPrice = formatPrice(order.totalPrice);
    
    // Build product items HTML
    let productsHTML = '';
    let totalQuantity = 0; // ✅ Tính tổng số lượng sản phẩm
    if (order.orderDetails && order.orderDetails.length > 0) {
        order.orderDetails.forEach(function(item) {
            productsHTML += createProductItem(item);
            totalQuantity += item.quantity || 0; // ✅ Cộng dồn quantity
        });
    }
    
    // Action buttons based on status
    let actionButtons = '';
    if (currentStatus === 'IN_STOCK' && order.status === 'IN_STOCK') {
        actionButtons = `
            <button class="btn-cancel-order" data-order-id="${order.id}" 
                    style="background: #ee4d2d; color: white; padding: 10px 24px; border: 1px solid #ee4d2d; border-radius: 4px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 4px rgba(238,77,45,0.2);">
                <i class="fa fa-times-circle"></i> Hủy đơn
            </button>
        `;
    } else if (order.status === 'DELIVERED') {
        // Check if can return (within 7 days)
        const canReturn = checkCanReturn(order);
        const daysLeft = canReturn ? getDaysLeftToReturn(order) : 0;
        
        actionButtons = `
            ${canReturn ? `
                <button class="btn-return-request" 
                        onclick="openReturnRequestModal(${order.id})" 
                        style="background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%); 
                               color: white; 
                               border: none; 
                               padding: 10px 20px; 
                               border-radius: 4px; 
                               font-size: 14px; 
                               font-weight: 600; 
                               cursor: pointer; 
                               transition: all 0.3s; 
                               box-shadow: 0 2px 8px rgba(243, 156, 18, 0.3); 
                               margin-right: 8px;"
                        onmouseover="this.style.transform='translateY(-2px)'; this.style.boxShadow='0 4px 12px rgba(243, 156, 18, 0.4)'" 
                        onmouseout="this.style.transform='translateY(0)'; this.style.boxShadow='0 2px 8px rgba(243, 156, 18, 0.3)'">
                    <i class="fa fa-undo"></i> Yêu Cầu Trả Hàng
                </button>
                <div style="display: inline-block; font-size: 12px; color: #f39c12; margin-right: 12px;">
                    <i class="fa fa-clock-o"></i> Còn ${daysLeft} ngày
                </div>
            ` : ''}
            <button class="btn-reorder" data-order-id="${order.id}" 
                    style="background: white; color: #ee4d2d; padding: 10px 24px; border: 1px solid #ee4d2d; border-radius: 4px; font-size: 14px; font-weight: 500; cursor: pointer; transition: all 0.3s ease;">
                <i class="fa fa-refresh"></i> Mua lại
            </button>
        `;
    }
    
    return `
        <div class="order-card-shopee" data-order-id="${order.id}" data-status="${order.status}" style="border-left-color: ${borderColor};">
            <!-- Order Header -->
            <div class="order-header-shopee">
                <div style="display: flex; align-items: center; justify-content: space-between;">
                    <div style="display: flex; align-items: center; gap: 12px;">
                        <i class="fa fa-shopping-bag" style="color: #ee4d2d; font-size: 18px;"></i>
                        <span style="font-size: 14px; font-weight: 600; color: #333;">DeeG Shop</span>
                        <span style="font-size: 12px; color: #999;">|</span>
                        <span style="font-size: 13px; color: #666;">${formattedDate}</span>
                    </div>
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <span class="status-badge-shopee ${statusClass}">${statusText}</span>
                    </div>
                </div>
            </div>
            
            <!-- Order Products -->
            <div class="order-products-shopee">
                ${productsHTML}
            </div>
            
            <!-- Order Footer -->
            <div class="order-footer-shopee">
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #f5f5f5;">
                    <div style="color: #666; font-size: 14px;">
                        <i class="fa fa-credit-card" style="margin-right: 6px;"></i>
                        Thanh toán: <span style="font-weight: 500; color: #333;">${getPayOptionText(order.payOption)}</span>
                    </div>
                    <div style="display: flex; align-items: center; gap: 8px;">
                        <span style="color: #666; font-size: 14px;">Tổng số tiền (${totalQuantity} sản phẩm):</span>
                        <span style="color: #ee4d2d; font-size: 20px; font-weight: 600;">${formattedPrice}</span>
                    </div>
                </div>
                <div style="display: flex; justify-content: flex-end; align-items: center; gap: 12px; padding: 16px 20px;">
                    <a href="/user/order-detail/${order.id}" 
                       style="background: white; color: #555; padding: 10px 24px; border: 1px solid #ddd; border-radius: 4px; text-decoration: none; font-size: 14px; font-weight: 500; transition: all 0.3s ease; display: inline-block;">
                        Xem chi tiết
                    </a>
                    ${actionButtons}
                </div>
            </div>
        </div>
    `;
}

// Create product item HTML (Shopee Style)
function createProductItem(item) {
    // Support both flat structure and nested structure
    const productImage = item.image || 
                        (item.productDetail && item.productDetail.product && item.productDetail.product.image) || 
                        '/img/product-default.jpg';
    const productName = item.product_name || 
                       (item.productDetail && item.productDetail.product && item.productDetail.product.title) || 
                       'Sản phẩm';
    const size = item.size || 
                (item.productDetail && item.productDetail.size) || 
                'N/A';
    const quantity = item.quantity || 1;
    const price = item.price || 0; // ✅ Giá đã mua (có flash sale nếu có)
    const originalPrice = item.originalPrice || 0; // ✅ Giá gốc từ OrderDetail
    const total = price * quantity;
    
    return `
        <div class="product-item-shopee">
            <div style="display: flex; gap: 16px; padding: 16px 20px;">
                <!-- Product Image -->
                <div style="flex-shrink: 0;">
                    <img src="${productImage}" 
                         alt="${productName}"
                         style="width: 80px; height: 80px; object-fit: cover; border-radius: 4px; border: 1px solid #f0f0f0;">
                </div>
                
                <!-- Product Info -->
                <div style="flex: 1; display: flex; flex-direction: column; justify-content: space-between;">
                    <div>
                        <h4 style="margin: 0 0 8px 0; font-size: 15px; font-weight: 500; color: #333; line-height: 1.4;">
                            ${productName}
                        </h4>
                        <p style="margin: 0; font-size: 13px; color: #888;">
                            Size: ${size}
                        </p>
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="color: #888; font-size: 14px;">x${quantity}</span>
                    </div>
                </div>
                
                <!-- Product Price -->
                <div style="flex-shrink: 0; text-align: right; display: flex; flex-direction: column; justify-content: space-between; align-items: flex-end;">
                    <div>
                        ${originalPrice > price ? `
                            <span style="color: #888; font-size: 13px; text-decoration: line-through; display: block; margin-bottom: 4px;">
                                ${formatPrice(originalPrice)}
                            </span>
                        ` : ''}
                        <span style="color: #ee4d2d; font-size: 16px; font-weight: 600;">
                            ${formatPrice(price)}
                        </span>
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

function getStatusBorderColor(status) {
    switch(status) {
        case 'DELIVERED': return '#27ae60'; // Green
        case 'SHIPPED': return '#3498db'; // Blue
        case 'IN_STOCK': return '#ee4d2d'; // Orange/Red
        case 'CANCEL': return '#e74c3c'; // Red
        case 'RETURN': return '#95a5a6'; // Gray
        default: return '#ee4d2d';
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

// Bind cancel order events
function bindCancelOrderEvents() {
    $('.btn-cancel-order').off('click').on('click', function() {
        const orderId = $(this).data('order-id');
        handleCancelOrder(orderId);
    });
}

// Handle cancel order
function handleCancelOrder(orderId) {
    // Xác nhận trước khi hủy
    if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng #' + orderId + '?')) {
        return;
    }
    
    // Disable button và hiển thị loading
    const button = $(`.btn-cancel-order[data-order-id="${orderId}"]`);
    const originalText = button.html();
    button.prop('disabled', true);
    button.html('<i class="fa fa-spinner fa-spin"></i> Đang xử lý...');
    
    // Gọi API hủy đơn hàng
    $.ajax({
        url: `/user/api/orders/${orderId}/cancel`,
        method: 'POST',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                // Hiển thị thông báo thành công
                alert('Hủy đơn hàng thành công!');
                
                // Reload orders để cập nhật UI
                const currentStatus = $('.tab-link.active').data('status');
                loadOrdersByStatus(currentStatus);
                loadOrderCounts();
            } else {
                alert('Lỗi: ' + (response.message || 'Không thể hủy đơn hàng'));
                button.prop('disabled', false);
                button.html(originalText);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error canceling order:', error);
            let errorMessage = 'Có lỗi xảy ra khi hủy đơn hàng. Vui lòng thử lại sau.';
            
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            }
            
            alert('Lỗi: ' + errorMessage);
            button.prop('disabled', false);
            button.html(originalText);
        }
    });
}

// ========== CHECK IF ORDER CAN BE RETURNED ==========
function checkCanReturn(order) {
    if (order.status !== 'DELIVERED') {
        return false;
    }
    
    // DEMO: Giả sử delivered date = created date + 3 days
    // Trong thực tế, sẽ lấy từ order.deliveredDate
    const deliveredDate = order.deliveredDate ? new Date(order.deliveredDate) : new Date(order.createdDate);
    deliveredDate.setDate(deliveredDate.getDate() + 3); // Giả lập delivered sau 3 ngày đặt
    
    const now = new Date();
    const diffTime = now - deliveredDate;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    // Chỉ được trả trong vòng 7 ngày
    return diffDays <= 7;
}

function getDaysLeftToReturn(order) {
    const deliveredDate = order.deliveredDate ? new Date(order.deliveredDate) : new Date(order.createdDate);
    deliveredDate.setDate(deliveredDate.getDate() + 3);
    
    const now = new Date();
    const diffTime = now - deliveredDate;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return Math.max(0, 7 - diffDays);
}

// Helper function to add return buttons (compatibility with order-view.html)
function addReturnButtonsToDeliveredOrders(orders) {
    // This function is called after orders are rendered
    // Return buttons are already added in createOrderCard function
    console.log('✅ Return buttons added for eligible DELIVERED orders');
}
