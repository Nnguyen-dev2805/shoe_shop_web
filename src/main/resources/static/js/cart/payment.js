/**
 * payment.js - Trang thanh toán
 */

let selectedItems = [];
let addressId = null;
let cartId = null;
let subtotal = 0;
let shippingFee = 5000;
let discountPercent = 0;
let discountAmount = 0;

$(document).ready(function() {
    loadDataFromSession();
    loadDiscounts();
    loadShippingCompanies();
    bindEventHandlers();
});

/**
 * Load data from sessionStorage
 */
function loadDataFromSession() {
    const itemsData = sessionStorage.getItem('selectedItems');
    addressId = sessionStorage.getItem('selectedAddressId');
    cartId = sessionStorage.getItem('cartId');
    
    console.log('=== PAYMENT PAGE DEBUG ===');
    console.log('Items data:', itemsData);
    console.log('Address ID:', addressId);
    console.log('Cart ID:', cartId);
    
    if (!itemsData || !addressId) {
        alert('Dữ liệu không hợp lệ. Vui lòng quay lại trang chọn sản phẩm.');
        window.location.href = '/cart/view';
        return;
    }
    
    try {
        selectedItems = JSON.parse(itemsData);
        console.log('Parsed items:', selectedItems);
    } catch (e) {
        console.error('Error parsing items:', e);
        alert('Lỗi dữ liệu. Vui lòng thử lại.');
        window.location.href = '/cart/view';
        return;
    }
    
    // Render selected products
    renderSelectedProducts();
    
    // Load and display address
    loadAddressInfo();
    
    // Calculate prices
    calculatePrices();
}

/**
 * Render selected products (read-only)
 */
function renderSelectedProducts() {
    console.log('Rendering selected products...');
    console.log('Selected items count:', selectedItems.length);
    
    const tbody = $('#selected-products-tbody');
    tbody.empty();
    
    if (selectedItems.length === 0) {
        tbody.append(`
            <tr>
                <td colspan="6" class="text-center">Không có sản phẩm</td>
            </tr>
        `);
        return;
    }
    
    selectedItems.forEach(function(item) {
        const unitPrice = item.product.product.price + item.product.priceadd;
        const row = `
            <tr style="background-color: #fafafa;">
                <td>
                    <img src="${item.product.product.image}" alt="image" 
                         style="max-width: 60px; max-height: 60px; object-fit: cover;">
                </td>
                <td>${item.product.product.title}</td>
                <td>${item.product.size}</td>
                <td>${formatPrice(unitPrice)}</td>
                <td><strong>${item.quantity}</strong></td>
                <td><strong>${formatPrice(item.price * item.quantity)}</strong></td>
            </tr>
        `;
        tbody.append(row);
    });
}

/**
 * Load address info from API
 */
function loadAddressInfo() {
    console.log('Loading address info for ID:', addressId);
    $.ajax({
        url: '/api/user/addresses',
        method: 'GET',
        success: function(response) {
            console.log('Address API response:', response);
            if (response.success && response.addresses) {
                const address = response.addresses.find(a => a.id == addressId);
                if (address) {
                    $('#delivery-address').html(`
                        <i class="fa fa-check-circle text-success"></i> 
                        <strong>${address.address}</strong>
                    `);
                } else {
                    console.warn('Address not found with ID:', addressId);
                }
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading address:', error);
            console.error('Response:', xhr.responseJSON);
        }
    });
}

/**
 * Load available discounts
 */
function loadDiscounts() {
    console.log('Loading discounts...');
    $.ajax({
        url: '/api/discounts/available',
        method: 'GET',
        success: function(response) {
            if (response.success && response.data) {
                renderDiscounts(response.data);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading discounts:', error);
        }
    });
}

/**
 * Render discounts
 */
function renderDiscounts(discounts) {
    console.log('Rendering discounts:', discounts);
    const container = $('#discount-list');
    container.empty();
    
    if (!discounts || discounts.length === 0) {
        container.append('<p class="text-center">Không có mã giảm giá</p>');
        return;
    }
    
    // Add "No discount" option
    container.append(`
        <div class="discount-item" data-id="" data-percent="0">
            <strong>Không sử dụng mã giảm giá</strong>
        </div>
    `);
    
    discounts.forEach(function(discount) {
        if (discount.status === 'EXPIRED') return;
        
        const item = `
            <div class="discount-item" data-id="${discount.id}" data-percent="${discount.percent * 100}">
                <strong>${discount.name}</strong><br>
                <small>Giảm ${(discount.percent * 100).toFixed(0)}% - 
                ${discount.description || ''}</small>
            </div>
        `;
        container.append(item);
    });
}

/**
 * Load shipping companies
 */
function loadShippingCompanies() {
    console.log('Loading shipping companies...');
    $.ajax({
        url: '/api/shipping/companies',
        method: 'GET',
        success: function(response) {
            if (response.success && response.data) {
                renderShippingCompanies(response.data);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading shipping companies:', error);
        }
    });
}

/**
 * Render shipping companies
 */
function renderShippingCompanies(companies) {
    console.log('Rendering shipping companies:', companies);
    const select = $('#shippingCompanySelect');
    select.empty();
    
    if (!companies || companies.length === 0) {
        select.append('<option value="">Không có đơn vị vận chuyển</option>');
        return;
    }
    
    companies.forEach(function(company) {
        select.append(`<option value="${company.id}">${company.name}</option>`);
    });
}

/**
 * Calculate all prices
 */
function calculatePrices() {
    // Calculate subtotal
    subtotal = selectedItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    
    console.log('Calculating prices...');
    console.log('Subtotal:', subtotal);
    console.log('Shipping fee:', shippingFee);
    console.log('Discount percent:', discountPercent);
    
    // Calculate discount
    discountAmount = subtotal * (discountPercent / 100);
    
    // Calculate final total
    const finalTotal = subtotal + shippingFee - discountAmount;
    
    console.log('Final total:', finalTotal);
    
    // Update UI
    $('#subtotal-price').text(formatPrice(subtotal));
    $('#shipping-fee').text(formatPrice(shippingFee));
    $('#discount-percent').text(discountPercent.toFixed(0));
    $('#discount-amount').text(formatPrice(discountAmount));
    $('#final-total-price').text(formatPrice(finalTotal));
    
    // Show/hide discount row
    if (discountPercent > 0) {
        $('#discount-row').show();
    } else {
        $('#discount-row').hide();
    }
}

/**
 * Bind event handlers
 */
function bindEventHandlers() {
    // Discount selection
    $(document).on('click', '.discount-item', handleDiscountSelection);
    
    // Payment button
    $('#btn-payment').on('click', handlePayment);
}

/**
 * Handle discount selection
 */
function handleDiscountSelection() {
    // Remove previous selection
    $('.discount-item').removeClass('selected');
    
    // Add selected class
    $(this).addClass('selected');
    
    // Get discount data
    const discountId = $(this).data('id');
    const percent = parseFloat($(this).data('percent'));
    
    // Update discount
    $('#selected-discount-id').val(discountId || '');
    discountPercent = percent;
    
    // Recalculate prices
    calculatePrices();
}

/**
 * Handle payment
 */
function handlePayment() {
    const paymentMethod = $('input[name="paymentMethod"]:checked').val();
    const shippingCompanyId = $('#shippingCompanySelect').val();
    const discountId = $('#selected-discount-id').val();
    
    if (!shippingCompanyId) {
        alert('Vui lòng chọn đơn vị vận chuyển!');
        return;
    }
    
    // Get final total
    const finalTotal = subtotal + shippingFee - discountAmount;
    
    // Collect selected item IDs and quantities
    const selectedItemIds = selectedItems.map(item => item.id);
    const selectedItemsData = selectedItems.map(item => ({
        id: item.id,
        quantity: item.quantity
    }));
    
    const paymentData = {
        cartId: cartId ? parseInt(cartId) : null,
        addressId: parseInt(addressId),
        shippingCompanyId: parseInt(shippingCompanyId),
        discountId: discountId ? parseInt(discountId) : null,
        payOption: paymentMethod,
        finalTotalPrice: finalTotal,
        selectedItemIds: selectedItemIds,
        selectedItemsData: selectedItemsData  // Include quantity info
    };
    
    console.log('Payment data:', paymentData);
    
    // Show loading
    $('#btn-payment').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Đang xử lý...');
    
    // Call API
    $.ajax({
        url: '/api/order/pay',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(paymentData),
        success: function(response) {
            if (response.success) {
                // Clear session storage
                sessionStorage.removeItem('selectedItems');
                sessionStorage.removeItem('selectedAddressId');
                sessionStorage.removeItem('cartId');
                
                if (response.data && response.data.paymentUrl) {
                    // Redirect to payment gateway
                    window.location.href = response.data.paymentUrl;
                } else {
                    // Success - redirect to order list
                    alert('✅ Đặt hàng thành công!');
                    window.location.href = '/user/orders';
                }
            } else {
                alert('❌ ' + (response.message || 'Có lỗi xảy ra'));
                $('#btn-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Xác nhận thanh toán');
            }
        },
        error: function(xhr, status, error) {
            console.error('Payment error:', error);
            alert('❌ Có lỗi xảy ra khi thanh toán. Vui lòng thử lại!');
            $('#btn-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Xác nhận thanh toán');
        }
    });
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
