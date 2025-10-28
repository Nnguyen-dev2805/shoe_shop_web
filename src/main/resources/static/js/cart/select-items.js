/**
 * select-items.js - Trang chọn sản phẩm thanh toán
 */

$(document).ready(function() {
    loadCartData();
    loadAddresses();
    bindEventHandlers();
    
    // Check if this is a reorder with auto-submit
    checkAutoSubmit();
});

/**
 * Check URL parameters for auto-submit after reorder
 */
function checkAutoSubmit() {
    const urlParams = new URLSearchParams(window.location.search);
    const reorder = urlParams.get('reorder');
    const autoSubmit = urlParams.get('autoSubmit');
    
    if (reorder === 'true' && autoSubmit === 'true') {
        console.log('🔄 Reorder detected - Auto-submitting after data loads...');
        
        // Wait for cart data to load, then auto-submit
        setTimeout(function() {
            // Check all items are loaded
            const checkboxes = $('.item-checkbox');
            if (checkboxes.length > 0) {
                console.log('✅ Cart loaded with', checkboxes.length, 'items - Auto-proceeding to payment');
                
                // Make sure all items are checked
                checkboxes.prop('checked', true);
                
                // Update total
                updateTotalPrice();
                
                // Auto-click continue button after short delay
                setTimeout(function() {
                    $('#btn-continue-checkout').click();
                }, 500);
            } else {
                console.warn('⚠️ No items in cart for reorder');
            }
        }, 1500); // Wait 1.5s for data to load
    }
}

/**
 * Load cart data from API
 */
function loadCartData() {
    $.ajax({
        url: '/api/cart/current',
        method: 'GET',
        success: function(response) {
            if (response.success && response.data) {
                renderCartItems(response.data);
                updateTotalPrice();
            } else {
                showAlert('Không thể tải giỏ hàng', 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading cart:', error);
            showAlert('Lỗi khi tải giỏ hàng', 'error');
        }
    });
}

/**
 * Render cart items with checkboxes
 */
function renderCartItems(cart) {
    const tbody = $('table tbody');
    tbody.empty();
    
    if (!cart.cartDetails || cart.cartDetails.length === 0) {
        tbody.append(`
            <tr>
                <td colspan="8" class="text-center">Giỏ hàng trống</td>
            </tr>
        `);
        return;
    }
    
    cart.cartDetails.forEach(function(detail) {
        const unitPrice = detail.product.product.price + detail.product.priceadd;
        const row = `
            <tr data-detail-id="${detail.id}">
                <td class="text-center">
                    <input type="checkbox" class="item-checkbox" 
                           data-id="${detail.id}" 
                           data-price="${detail.price}" 
                           data-quantity="${detail.quantity}"
                           data-product-detail='${JSON.stringify(detail)}'
                           checked>
                </td>
                <td class="cart-item-img">
                    <img src="${detail.product.product.image}" alt="image" 
                         style="max-width: 80px; max-height: 80px; object-fit: cover;">
                </td>
                <td>${detail.product.product.title}</td>
                <td>${detail.product.size}</td>
                <td class="formatted-price">${formatPrice(unitPrice)}</td>
                <td>
                    <div class="quantity-controller">
                        <button type="button" class="quantity-btn decrease" data-id="${detail.id}">-</button>
                        <input type="number" class="quantity-input" value="${detail.quantity}" data-id="${detail.id}" readonly>
                        <button type="button" class="quantity-btn increase" data-id="${detail.id}">+</button>
                    </div>
                </td>
                <td class="formatted-price">${formatPrice(detail.price * detail.quantity)}</td>
                <td>
                    <button type="button" class="btn-delete-item" data-id="${detail.id}">
                        <i class="fa fa-trash"></i> Xóa
                    </button>
                </td>
            </tr>
        `;
        tbody.append(row);
    });
    
    // Store cart globally
    window.currentCart = cart;
}

/**
 * Load user addresses
 */
function loadAddresses() {
    $.ajax({
        url: '/api/user/addresses',
        method: 'GET',
        success: function(response) {
            console.log('Address API response:', response);  // Debug log
            if (response.success && response.addresses) {  // ✅ Sửa: response.data → response.addresses
                renderAddresses(response.addresses);
            } else {
                console.warn('No addresses found or API error');
                renderAddresses([]);  // Render empty state
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading addresses:', error);
            console.error('Response:', xhr.responseJSON);
        }
    });
}

/**
 * Render addresses in select
 */
function renderAddresses(addresses) {
    const select = $('#addressSelect');
    select.empty();
    
    if (!addresses || addresses.length === 0) {
        select.append('<option value="">Chưa có địa chỉ</option>');
        return;
    }
    
    addresses.forEach(function(address) {
        const selected = address.isDefault ? 'selected' : '';
        select.append(`<option value="${address.id}" ${selected}>${address.address}</option>`);
    });
}

/**
 * Calculate total price of selected items
 */
function updateTotalPrice() {
    let total = 0;
    
    $('.item-checkbox:checked').each(function() {
        const price = parseFloat($(this).data('price'));
        const quantity = parseInt($(this).data('quantity'));
        total += price * quantity;
    });
    
    $('#totalPrice').text(formatPrice(total));
}

/**
 * Bind event handlers
 */
function bindEventHandlers() {
    // Select all checkbox
    $(document).on('change', '#select-all-items', handleSelectAll);
    
    // Individual checkbox
    $(document).on('change', '.item-checkbox', handleCheckboxChange);
    
    // Quantity buttons
    $(document).on('click', '.quantity-btn.decrease', handleQuantityDecrease);
    $(document).on('click', '.quantity-btn.increase', handleQuantityIncrease);
    
    // Quantity input direct change
    $(document).on('change', '.quantity-input', handleQuantityInputChange);
    $(document).on('blur', '.quantity-input', handleQuantityInputChange);
    
    // Delete button
    $(document).on('click', '.btn-delete-item', handleDeleteItem);
    
    // Continue to payment button
    $('#btn-continue-checkout').on('click', handleContinueCheckout);
}

/**
 * Handle select all checkbox
 */
function handleSelectAll() {
    const isChecked = $(this).prop('checked');
    $('.item-checkbox').prop('checked', isChecked);
    updateTotalPrice();
}

/**
 * Handle individual checkbox change
 */
function handleCheckboxChange() {
    updateTotalPrice();
    
    // Update select-all checkbox
    const totalCheckboxes = $('.item-checkbox').length;
    const checkedCheckboxes = $('.item-checkbox:checked').length;
    $('#select-all-items').prop('checked', totalCheckboxes === checkedCheckboxes);
}

/**
 * Handle quantity decrease
 */
function handleQuantityDecrease() {
    const detailId = $(this).data('id');
    const input = $(this).siblings('.quantity-input');
    const currentQuantity = parseInt(input.val());
    
    if (currentQuantity > 1) {
        updateQuantity(detailId, currentQuantity - 1);
    }
}

/**
 * Handle quantity increase
 */
function handleQuantityIncrease() {
    const detailId = $(this).data('id');
    const input = $(this).siblings('.quantity-input');
    const currentQuantity = parseInt(input.val());
    
    updateQuantity(detailId, currentQuantity + 1);
}

/**
 * Handle quantity input direct change
 */
function handleQuantityInputChange() {
    const input = $(this);
    const detailId = input.data('id');
    const newQuantity = parseInt(input.val()) || 1;
    
    // Validate and update
    if (newQuantity !== parseInt(input.attr('data-original-value') || input.val())) {
        updateQuantity(detailId, newQuantity);
    }
}

/**
 * Update quantity via API
 */
function updateQuantity(detailId, newQuantity) {
    // Frontend validation
    if (newQuantity < 1) {
        showAlert('Số lượng phải lớn hơn 0', 'error');
        return;
    }
    
    if (newQuantity > 999) {
        showAlert('Số lượng không được vượt quá 999', 'error');
        return;
    }
    
    // Show loading state
    const input = $(`.quantity-input[data-id="${detailId}"]`);
    const originalValue = input.val();
    input.prop('disabled', true);
    
    $.ajax({
        url: `/api/cart/update-quantity/${detailId}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({ quantity: newQuantity }),
        success: function(response) {
            if (response.success) {
                // Update UI
                input.val(newQuantity);
                
                // Update checkbox data
                const checkbox = $(`.item-checkbox[data-id="${detailId}"]`);
                checkbox.data('quantity', newQuantity);
                
                // Reload cart
                loadCartData();
                showAlert('✅ Cập nhật số lượng thành công!', 'success');
            } else {
                // Revert to original value on error
                input.val(originalValue);
                showAlert('❌ ' + (response.message || 'Lỗi cập nhật số lượng'), 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error updating quantity:', error);
            
            // Revert to original value on error
            input.val(originalValue);
            
            // Parse error message from server response
            let errorMessage = 'Lỗi khi cập nhật số lượng';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            } else if (xhr.responseText) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.message) {
                        errorMessage = response.message;
                    }
                } catch (e) {
                    // Keep default message if parsing fails
                }
            }
            
            showAlert('❌ ' + errorMessage, 'error');
        },
        complete: function() {
            // Re-enable input
            input.prop('disabled', false);
        }
    });
}

/**
 * Handle delete item
 */
function handleDeleteItem() {
    const detailId = $(this).data('id');
    
    if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }
    
    deleteCartItem(detailId);
}

/**
 * Delete cart item via API
 */
function deleteCartItem(detailId) {
    $.ajax({
        url: `/api/cart/delete/${detailId}`,
        method: 'DELETE',
        success: function(response) {
            if (response.success) {
                showAlert('Xóa sản phẩm thành công!', 'success');
                // Reload cart data
                loadCartData();
            } else {
                showAlert(response.message || 'Lỗi khi xóa sản phẩm', 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error deleting item:', error);
            
            // Parse error message from server response
            let errorMessage = 'Lỗi khi xóa sản phẩm';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            } else if (xhr.responseText) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.message) {
                        errorMessage = response.message;
                    }
                } catch (e) {
                    // Keep default message if parsing fails
                }
            }
            
            showAlert(errorMessage, 'error');
        }
    });
}

/**
 * Handle continue to checkout
 */
function handleContinueCheckout() {
    const selectedItems = $('.item-checkbox:checked');
    
    if (selectedItems.length === 0) {
        showAlert('Vui lòng chọn ít nhất một sản phẩm!', 'error');
        return;
    }
    
    const addressId = $('#addressSelect').val();
    if (!addressId) {
        showAlert('Vui lòng chọn địa chỉ giao hàng!', 'error');
        return;
    }
    
    // Collect selected items data
    const selectedData = [];
    selectedItems.each(function() {
        const detailData = $(this).data('product-detail');
        selectedData.push({
            id: detailData.id,
            product: detailData.product,
            quantity: parseInt($(this).data('quantity')),
            price: parseFloat($(this).data('price'))
        });
    });
    
    // Store in sessionStorage to pass to payment page
    sessionStorage.setItem('selectedItems', JSON.stringify(selectedData));
    sessionStorage.setItem('selectedAddressId', addressId);
    sessionStorage.setItem('cartId', window.currentCart.id);
    
    // Redirect to payment page
    window.location.href = '/user/payment';
}

/**
 * Show alert message
 */
function showAlert(message, type) {
    const alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="close" data-dismiss="alert">
                <span>&times;</span>
            </button>
        </div>
    `;
    $('#alert-container').html(alertHtml);
    
    // Auto hide after 3 seconds
    setTimeout(function() {
        $('.alert').fadeOut();
    }, 3000);
}

/**
 * Format price
 */
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', { 
        style: 'decimal', 
        minimumFractionDigits: 0 
    }).format(price) + ' đ';
}
