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
 * Initialize cart data loading
 */
function initializeCart() {
    loadCartData();
    loadDiscounts();
    loadUserAddresses();
}

/**
 * Load cart data from API
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
                showAlert(response.message || 'Error loading cart data', 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading cart:', error);
            showAlert('Failed to load cart data', 'error');
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
            if (response.success) {
                renderUserAddresses(response.data);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading addresses:', error);
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
                <td colspan="9" class="text-center">Your cart is empty</td>
            </tr>
        `);
        return;
    }
    
    cart.cartDetails.forEach(function(detail) {
        const row = `
            <tr data-detail-id="${detail.id}">
                <td class="text-center">
                    <input type="checkbox" class="item-checkbox" data-id="${detail.id}" 
                           data-price="${detail.price}" data-quantity="${detail.quantity}" checked>
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
                    <div class="quantity-controller" style="margin-top: 30px; margin-left: 30px; margin-right: -30px">
                        <button type="button" class="quantity-btn decrease" data-id="${detail.id}">-</button>
                        <input type="number" class="quantity-input" value="${detail.quantity}" data-id="${detail.id}" readonly>
                        <button type="button" class="quantity-btn increase" data-id="${detail.id}">+</button>
                    </div>
                </td>
                <td class="subtotal">
                    <span class="formatted-price">${formatPrice(detail.price * detail.quantity)}</span>
                </td>
                <td class="remove-icon">
                    <button type="button" class="edit-btn" data-id="${detail.id}" style="margin-top: 30px; border: none; background: none;">
                        <img src="/img/cart/btn_edit.gif" alt="Edit">
                    </button>
                </td>
                <td class="remove-icon">
                    <button type="button" class="remove-btn" data-id="${detail.id}" style="border: none; background: none;">
                        <img src="/img/cart/btn_remove.png" alt="Remove">
                    </button>
                </td>
            </tr>
        `;
        tbody.append(row);
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
    const totalPrice = selectedTotal > 0 ? selectedTotal + 5 : selectedTotal;
    
    $('#totalPrice').text(formatPrice(totalPrice));
    
    // Update created date
    const createdDate = new Date(cart.createdDate);
    const formattedDate = createdDate.toLocaleString('vi-VN');
    $('.totals p').first().html(`Created Date: <span>${formattedDate}</span>`);
    
    // Update cart ID in form
    $('input[name="cartId"]').val(cart.id);
    
    // Store original total price for discount calculations
    window.originalTotalPrice = totalPrice;
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
    
    // Edit and remove handlers
    $(document).on('click', '.edit-btn', handleEditItem);
    $(document).on('click', '.remove-btn', handleRemoveItem);
    
    // Discount selection
    $(document).on('click', '.select-discount-btn', handleDiscountSelection);
    
    // Checkout form submission
    $('#form2').on('submit', handleCheckoutSubmission);
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
                showAlert(response.message || 'Error updating quantity', 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error updating quantity:', error);
            showAlert('Failed to update quantity', 'error');
        }
    });
}

/**
 * Handle edit item
 */
function handleEditItem() {
    const detailId = $(this).data('id');
    const quantity = $(this).closest('tr').find('.quantity-input').val();
    
    $.ajax({
        url: `/api/cart/edit/${detailId}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({ quantity: parseInt(quantity) }),
        success: function(response) {
            if (response.success) {
                showAlert('Item updated successfully', 'success');
                loadCartData();
            } else {
                showAlert(response.message || 'Error updating item', 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error editing item:', error);
            showAlert('Failed to update item', 'error');
        }
    });
}

/**
 * Handle remove item
 */
function handleRemoveItem() {
    const detailId = $(this).data('id');
    
    if (confirm('Are you sure you want to remove this item from cart?')) {
        $.ajax({
            url: `/api/cart/delete/${detailId}`,
            method: 'DELETE',
            success: function(response) {
                if (response.success) {
                    showAlert('Item removed successfully', 'success');
                    loadCartData();
                } else {
                    showAlert(response.message || 'Error removing item', 'error');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error removing item:', error);
                showAlert('Failed to remove item', 'error');
            }
        });
    }
}

/**
 * Handle discount selection
 */
function handleDiscountSelection() {
    const listItem = $(this).closest('li');
    const discountId = listItem.data('id');
    const discountName = listItem.data('discount-code');
    const discountPercent = parseFloat(listItem.data('percent'));
    
    // Update discount inputs
    $('#discountIdInput').val(discountId);
    $('#couponCodeInput').val(`${discountName} - ${discountPercent}% OFF`);
    
    // Calculate new total price
    const newTotalPrice = window.originalTotalPrice * (1 - (discountPercent / 100));
    
    // Update display
    $('#totalPrice').text(formatPrice(newTotalPrice));
    $('#finalTotalPrice').val(newTotalPrice.toFixed(2));
    
    // Close modal
    $('#discountModal').modal('hide');
}

/**
 * Handle checkout form submission
 */
function handleCheckoutSubmission(e) {
    e.preventDefault();
    
    // Validate: At least one item must be selected
    const selectedItems = $('.item-checkbox:checked');
    if (selectedItems.length === 0) {
        showAlert('Vui lòng chọn ít nhất một sản phẩm để thanh toán!', 'error');
        return;
    }
    
    // Get list of selected item IDs
    const selectedItemIds = [];
    selectedItems.each(function() {
        selectedItemIds.push($(this).data('id'));
    });
    
    const formData = {
        cartId: $('input[name="cartId"]').val(),
        finalTotalPrice: $('#finalTotalPrice').val() || window.originalTotalPrice,
        payOption: $('#payOption').val(),
        addressId: $('select[name="addressId"]').val(),
        discountId: $('#discountIdInput').val(),
        selectedItemIds: selectedItemIds // Send selected items to backend
    };
    
    $.ajax({
        url: '/api/order/pay',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(response) {
            if (response.success) {
                if (response.redirectUrl) {
                    window.location.href = response.redirectUrl;
                } else {
                    showAlert('Order placed successfully!', 'success');
                    setTimeout(() => {
                        window.location.href = '/user/orders';
                    }, 2000);
                }
            } else {
                showAlert(response.message || 'Error processing order', 'error');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error processing checkout:', error);
            showAlert('Failed to process order', 'error');
        }
    });
}

/**
 * Format price with Vietnamese locale
 */
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', { 
        style: 'decimal', 
        minimumFractionDigits: 0 
    }).format(price);
}

/**
 * Show alert message
 */
function showAlert(message, type = 'info') {
    // Remove existing alerts
    $('.alert').remove();
    
    const alertClass = type === 'error' ? 'alert-danger' : 
                      type === 'success' ? 'alert-success' : 'alert-info';
    
    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    
    $('.container').prepend(alertHtml);
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        $('.alert').fadeOut();
    }, 5000);
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
