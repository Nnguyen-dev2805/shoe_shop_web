/**
 * Cart Management JavaScript
 * Handles cart operations using RESTful API + AJAX + jQuery
 */

$(document).ready(function() {
    // Initialize cart when page loads
    initializeCart();
    // Bind event handlers
    bindEventHandlers();
});

/**
 * Initialize cart page
 */
function initializeCart() {
    loadCartData();
    loadUserAddresses();
}

/**
 * Load cart data from API
{{ ... }}
 */
function loadCartData() {
    $.ajax({
        url: '/api/cart/current',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                renderCartItems(response.data);
                updateCartTotals(response.data);
            } else {
                showToast('⚠️ ' + (response.message || 'Không thể tải giỏ hàng'), 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading cart:', error);
            showToast('❌ Lỗi khi tải giỏ hàng. Vui lòng thử lại!', 'error');
        }
    });
}

/**
 * Load available discounts
 */
function loadDiscounts() {
    $.ajax({
        url: '/api/discounts/available',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                renderDiscounts(response.data);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading discounts:', error);
        }
    });
}


/**
 * Load user addresses
 */
function loadUserAddresses() {
    $.ajax({
        url: '/api/user/addresses',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            console.log('Address API response:', response);  // Debug log
            if (response.success && response.addresses) {  // ✅ Sửa: response.data → response.addresses
                renderUserAddresses(response.addresses);
            } else {
                console.warn('No addresses found or API error');
                renderUserAddresses([]);  // Render empty state
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading addresses:', error);
            console.error('Response:', xhr.responseJSON);
        }
    });
}

/**
 * Render cart items in the table
 */
function renderCartItems(cart) {
    const tbody = $('table tbody');
    tbody.empty();
    
    if (!cart.cartDetails || cart.cartDetails.length === 0) {
        tbody.append(`
            <tr>
                <td colspan="9" class="text-center">Giỏ hàng của bạn chưa có sản phẩm nào!</td>
            </tr>
        `);
        return;
    }
    
    cart.cartDetails.forEach(function(detail) {
        const row = `
            <tr data-detail-id="${detail.id}">
                <td class="text-center">
                    <input type="checkbox" class="item-checkbox" 
                           data-id="${detail.id}" 
                           data-price="${detail.price}" 
                           data-quantity="${detail.quantity}"
                           checked>
                </td>
                <td class="cart-item-img">
                    <a href="/product/details/${detail.product.product.id}">
                        <img src="${detail.product.product.image}" alt="image" 
                             style="max-width: 100px; max-height: 100px; object-fit: cover;">
                    </a>
                </td>
                <td class="cart-product-name">
                    <a href="/product/details/${detail.product.product.id}">${detail.product.product.title}</a>
                </td>
                <td class="unit-price">
                    <span>${detail.product.size}</span>
                </td>
                <td class="unit-price">
                    <span class="formatted-price">${formatPrice(detail.product.product.price + detail.product.priceadd)}</span>
                </td>
                <td class="quantity">
                    <div class="quantity-controller" style="margin: 0 auto;">
                        <button type="button" class="quantity-btn decrease" data-id="${detail.id}">-</button>
                        <input type="number" class="quantity-input" value="${detail.quantity}" data-id="${detail.id}" readonly>
                        <button type="button" class="quantity-btn increase" data-id="${detail.id}">+</button>
                    </div>
                </td>
                <td class="subtotal">
                    <span class="formatted-price">${formatPrice(detail.price * detail.quantity)}</span>
                </td>
                <td class="remove-icon">
                    <button type="button" class="remove-btn" data-id="${detail.id}" style="border: none; background: none; padding: 0; margin: 0;">
                        <img src="/img/cart/btn_remove.png" alt="Remove" style="display: block;">
                    </button>
                </td>
            </tr>
        `;
        tbody.append(row);
        
        // Store detail object using jQuery data() - this avoids JSON escaping issues
        $(`tr[data-detail-id="${detail.id}"] .item-checkbox`).data('product-detail', detail);
    });
    
    // Store cart data globally for calculations
    window.currentCart = cart;
}

/**
 * Render discounts in modal
 */
function renderDiscounts(discounts) {
    const discountList = $('#discountList');
    discountList.empty();
    
    if (!discounts || discounts.length === 0) {
        discountList.append('<p class="text-center">No discount available</p>');
        return;
    }
    
    discounts.forEach(function(discount) {
        if (discount.status === 'EXPIRED') return;
        
        const listItem = `
            <li class="list-group-item d-flex justify-content-between align-items-center"
                data-id="${discount.id}"
                data-discount-code="${discount.name}"
                data-percent="${discount.percent * 100}"
                data-start-date="${discount.startDate}"
                data-end-date="${discount.endDate}">
                <div>
                    <strong>${discount.name}</strong> - ${discount.percent * 100}% OFF
                    <br>
                    <small>
                        Start: ${discount.startDate} - End: ${discount.endDate}
                    </small>
                </div>
                <div>
                    ${renderDiscountButton(discount)}
                </div>
            </li>
        `;
        discountList.append(listItem);
    });
}

/**
 * Render discount button based on status
 */
function renderDiscountButton(discount) {
    switch(discount.status) {
        case 'ACTIVE':
            return `<button type="button" class="btn btn-primary btn-sm select-discount-btn">Select</button>`;
        case 'COMING':
            return `<span class="badge bg-warning">Coming Soon</span>`;
        case 'INACTIVE':
            return `<span class="badge bg-secondary">Not Condition</span>`;
        default:
            return '';
    }
}


/**
 * Render user addresses
 */
function renderUserAddresses(addresses) {
    const select = $('select[name="addressId"]');
    select.empty();
    
    // Handle empty addresses
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
 * Calculate total price based on selected items only
 */
function calculateSelectedTotal() {
    let total = 0;
    
    $('.item-checkbox:checked').each(function() {
        const price = parseFloat($(this).data('price'));
        const quantity = parseInt($(this).data('quantity'));
        total += price * quantity;
    });
    
    return total;
}

/**
 * Update cart totals - only count selected items
 */
function updateCartTotals(cart) {
    // Calculate total based on selected items only
    const selectedTotal = calculateSelectedTotal();
    
    $('#totalPrice').text(formatPrice(selectedTotal));
    
    // Update created date
    const createdDate = new Date(cart.createdDate);
    const formattedDate = createdDate.toLocaleString('vi-VN');
    $('#createdDate').text(formattedDate);
    
    // Update cart ID
    $('#cartIdInput').val(cart.id);
    
    // Store for later use
    window.originalTotalPrice = selectedTotal;
}

/**
 * Bind event handlers
 */
function bindEventHandlers() {
    // Checkbox handlers
    $(document).on('change', '#select-all-items', handleSelectAll);
    $(document).on('change', '.item-checkbox', handleItemCheckboxChange);
    
    // Quantity change handlers
    $(document).on('click', '.quantity-btn.decrease', handleQuantityDecrease);
    $(document).on('click', '.quantity-btn.increase', handleQuantityIncrease);
    
    // Remove handler
    $(document).on('click', '.remove-btn', handleRemoveItem);
    
    // Continue to payment button
    $('#btn-continue-payment').on('click', handleContinueToPayment);
}

/**
 * Handle "Select All" checkbox
 */
function handleSelectAll() {
    const isChecked = $(this).prop('checked');
    $('.item-checkbox').prop('checked', isChecked);
    updateCartTotals(window.currentCart);
}

/**
 * Handle individual item checkbox change
 */
function handleItemCheckboxChange() {
    // Update "Select All" checkbox status
    const totalCheckboxes = $('.item-checkbox').length;
    const checkedCheckboxes = $('.item-checkbox:checked').length;
    $('#select-all-items').prop('checked', totalCheckboxes === checkedCheckboxes);
    
    // Update cart totals
    updateCartTotals(window.currentCart);
}

/**
 * Handle quantity decrease
 */
function handleQuantityDecrease() {
    const detailId = $(this).data('id');
    const quantityInput = $(this).siblings('.quantity-input');
    const currentQuantity = parseInt(quantityInput.val());
    
    if (currentQuantity > 1) {
        updateQuantity(detailId, currentQuantity - 1);
    }
}

/**
 * Handle quantity increase
 */
function handleQuantityIncrease() {
    const detailId = $(this).data('id');
    const quantityInput = $(this).siblings('.quantity-input');
    const currentQuantity = parseInt(quantityInput.val());
    
    updateQuantity(detailId, currentQuantity + 1);
}

/**
 * Update item quantity via API
 */
function updateQuantity(detailId, newQuantity) {
    $.ajax({
        url: `/api/cart/update-quantity/${detailId}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({ quantity: newQuantity }),
        success: function(response) {
            if (response.success) {
                // Update the input value
                $(`.quantity-input[data-id="${detailId}"]`).val(newQuantity);
                // Reload cart to update totals
                loadCartData();
            } else {
                showToast('⚠️ ' + (response.message || 'Không thể cập nhật số lượng'), 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error updating quantity:', error);
            showToast('❌ Lỗi khi cập nhật số lượng. Vui lòng thử lại!', 'error');
        }
    });
}

/**
 * Handle edit item
 * COMMENTED OUT - Edit column removed from cart UI
 */
// function handleEditItem() {
//     const detailId = $(this).data('id');
//     const quantity = $(this).closest('tr').find('.quantity-input').val();
//     
//     $.ajax({
//         url: `/api/cart/edit/${detailId}`,
//         method: 'PUT',
//         contentType: 'application/json',
//         data: JSON.stringify({ quantity: parseInt(quantity) }),
//         success: function(response) {
//             if (response.success) {
//                 showAlert('Item updated successfully', 'success');
//                 loadCartData();
//             } else {
//                 showAlert(response.message || 'Error updating item', 'error');
//             }
//         },
//         error: function(xhr, status, error) {
//             console.error('Error editing item:', error);
//             showAlert('Failed to update item', 'error');
//         }
//     });
// }

/**
 * Handle remove item
 */
function handleRemoveItem() {
    const detailId = $(this).data('id');
    
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng không?')) {
        $.ajax({
            url: `/api/cart/delete/${detailId}`,
            method: 'DELETE',
            success: function(response) {
                if (response.success) {
                    // Show beautiful toast instead of ugly Bootstrap alert
                    showToast('✓ Đã xóa sản phẩm khỏi giỏ hàng!', 'success');
                    
                    // Reload cart data
                    loadCartData();
                    
                    // Refresh cart count in header
                    if (typeof window.refreshCartCount === 'function') {
                        window.refreshCartCount();
                    }
                } else {
                    showToast('⚠️ ' + (response.message || 'Không thể xóa sản phẩm'), 'error');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error removing item:', error);
                showToast('❌ Lỗi khi xóa sản phẩm. Vui lòng thử lại!', 'error');
            }
        });
    }
}

/**
 * Handle continue to payment button
 * Similar to Buy Now - no address selection required at cart page
 */
function handleContinueToPayment() {
    console.log('🛒 Checkout button clicked!');
    
    // Validate: At least one item must be selected
    const selectedItems = $('.item-checkbox:checked');
    console.log('📦 Selected items:', selectedItems.length);
    
    if (selectedItems.length === 0) {
        showToast('⚠️ Vui lòng chọn ít nhất một sản phẩm để thanh toán!', 'error');
        return;
    }
    
    // Collect selected items data from data attribute
    const selectedData = [];
    selectedItems.each(function() {
        const checkbox = $(this);
        const detailData = checkbox.data('product-detail');
        
        if (detailData) {
            selectedData.push({
                id: detailData.id,
                productDetailId: detailData.product?.id || detailData.productDetailId,
                quantity: detailData.quantity,
                price: detailData.price,
                product: detailData.product
            });
        }
    });
    
    console.log('✅ Selected data prepared:', selectedData);
    
    // Store in sessionStorage for payment page
    sessionStorage.setItem('selectedCartItems', JSON.stringify(selectedData));
    sessionStorage.setItem('checkoutSource', 'cart'); // Indicate checkout from cart
    sessionStorage.setItem('cartId', $('#cartIdInput').val());
    
    console.log('🚀 Redirecting to payment page...');
    
    // Redirect to payment page (address will be selected there)
    window.location.href = '/user/payment';
}

/**
 * Format price with Vietnamese locale
 */
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', { 
        style: 'decimal', 
        minimumFractionDigits: 0 
    }).format(price) + ' đ';
}

/**
 * Show toast notification (Shopee style)
 */
function showToast(message, type = 'success') {
    // Remove existing toasts
    $('.cart-success-toast, .cart-error-toast').remove();
    
    const toastClass = type === 'error' ? 'cart-error-toast' : 'cart-success-toast';
    const icon = type === 'error' ? 'fa-times-circle' : 'fa-check-circle';
    
    const $toast = $(`
        <div class="${toastClass}">
            <i class="fa ${icon}"></i>
            <span>${message}</span>
        </div>
    `).appendTo('body');
    
    // Show with animation
    setTimeout(() => {
        $toast.addClass('show');
    }, 100);
    
    // Hide and remove after 3 seconds
    setTimeout(() => {
        $toast.removeClass('show');
        setTimeout(() => {
            $toast.remove();
        }, 400);
    }, 3000);
}

/**
 * Show alert message (Legacy - kept for compatibility)
 * Now redirects to showToast
 */
function showAlert(message, type = 'info') {
    showToast(message, type);
}

/**
 * Initialize price formatting for existing elements
 */
function initializePriceFormatting() {
    $('.formatted-price').each(function() {
        const price = parseFloat($(this).text());
        if (!isNaN(price)) {
            $(this).text(formatPrice(price));
        }
    });
}

// Initialize price formatting when DOM is ready
$(document).ready(function() {
    initializePriceFormatting();
});
