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
 * Render selected products (Shopee table style)
 */
function renderSelectedProducts() {
    console.log('Rendering selected products...');
    console.log('Selected items count:', selectedItems.length);
    
    const container = $('#selected-products-list');
    container.empty();
    
    if (selectedItems.length === 0) {
        container.html('<div class="loading-text">Không có sản phẩm</div>');
        return;
    }
    
    // Add table header
    const headerHtml = `
        <div class="product-table-header">
            <div>Sản phẩm</div>
            <div>Đơn giá</div>
            <div>Số lượng</div>
            <div>Thành tiền</div>
        </div>
    `;
    container.append(headerHtml);
    
    // Add product rows
    selectedItems.forEach(function(item) {
        const unitPrice = item.product.product.price + item.product.priceadd;
        const totalPrice = item.price * item.quantity;
        
        const productHtml = `
            <div class="product-table-row">
                <div class="product-info">
                    <img src="${item.product.product.image}" 
                         alt="${item.product.product.title}" 
                         class="product-image">
                    <div class="product-details">
                        <div class="product-name">${item.product.product.title}</div>
                        <div class="product-variant">Phân loại: ${item.product.size}</div>
                    </div>
                </div>
                <div class="product-price">${formatPrice(unitPrice)}</div>
                <div class="product-qty">${item.quantity}</div>
                <div class="product-total">${formatPrice(totalPrice)}</div>
            </div>
        `;
        container.append(productHtml);
    });
}

let allAddresses = [];  // ✅ Store all addresses

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
                allAddresses = response.addresses;  // ✅ Store addresses
                
                const address = response.addresses.find(a => a.id == addressId);
                if (address) {
                    displayAddress(address);
                    
                    // ✅ Populate address modal
                    populateAddressModal(response.addresses);
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
 * Display selected address
 */
function displayAddress(address) {
    const defaultBadge = address.isDefault ? '<span class="address-default-badge" style="margin-left: 10px;">Mặc định</span>' : '';
    
    $('#delivery-address').html(`
        <div class="address-name">
            <i class="fa fa-user"></i> ${address.recipientName || 'Người nhận'}
            <span style="margin-left: 10px; color: #888;">|</span>
            <span style="margin-left: 10px; color: #666;">${address.recipientPhone || ''}</span>
            ${defaultBadge}
        </div>
        <div class="address-detail">
            ${address.address}
        </div>
    `);
}

/**
 * Populate address modal with radio buttons
 */
function populateAddressModal(addresses) {
    const container = $('#address-modal-list');
    container.empty();
    
    if (!addresses || addresses.length === 0) {
        container.html('<div class="loading-text">Không có địa chỉ</div>');
        return;
    }
    
    addresses.forEach(function(addr) {
        const isSelected = addr.id == addressId;
        const defaultBadge = addr.isDefault ? '<span class="address-default-badge">Mặc định</span>' : '';
        
        const html = `
            <div class="address-radio-item ${isSelected ? 'selected' : ''}" data-address-id="${addr.id}">
                <input type="radio" name="address-radio" value="${addr.id}" ${isSelected ? 'checked' : ''}>
                <div class="address-radio-content">
                    <div class="address-radio-header">
                        <span class="address-radio-name">${addr.recipientName || 'Người nhận'}</span>
                        <span class="address-radio-phone">| ${addr.recipientPhone || ''}</span>
                        ${defaultBadge}
                    </div>
                    <div class="address-radio-detail">
                        ${addr.address}
                    </div>
                </div>
                <button class="address-update-btn" data-address-id="${addr.id}">
                    Cập nhật
                </button>
            </div>
        `;
        container.append(html);
    });
    
    // Add "Thêm Địa Chỉ Mới" button
    container.append(`
        <div class="address-add-new" id="btn-add-address">
            <i class="fa fa-plus"></i>
            <span>Thêm Địa Chỉ Mới</span>
        </div>
    `);
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
        container.html('<div class="loading-text">Không có mã giảm giá</div>');
        return;
    }
    
    // Add "No discount" option
    container.append(`
        <div class="voucher-row" data-id="" data-percent="0">
            <div class="voucher-info">
                <div class="voucher-name">Không sử dụng mã giảm giá</div>
            </div>
        </div>
    `);
    
    discounts.forEach(function(discount) {
        if (discount.status === 'EXPIRED') return;
        
        const item = `
            <div class="voucher-row" data-id="${discount.id}" data-percent="${discount.percent * 100}">
                <div class="voucher-info">
                    <div class="voucher-name">${discount.name}</div>
                    <div class="voucher-desc">${discount.description || 'Giảm ' + (discount.percent * 100).toFixed(0) + '%'}</div>
                </div>
                <div class="voucher-badge">-${(discount.percent * 100).toFixed(0)}%</div>
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
    $(document).on('click', '.voucher-row', handleDiscountSelection);
    
    // Payment button
    $('#btn-payment').on('click', handlePayment);
    
    // ✅ Address modal handlers
    $('#btn-change-address').on('click', handleOpenAddressModal);
    $('#btn-modal-confirm').on('click', handleModalConfirm);
    $('#btn-modal-cancel').on('click', handleModalCancel);
    
    // Close modal when clicking outside
    $(document).on('click', '#address-modal', function(e) {
        if (e.target.id === 'address-modal') {
            handleModalCancel();
        }
    });
    
    // Handle radio item selection
    $(document).on('click', '.address-radio-item', handleRadioItemClick);
    
    // Handle add new address
    $(document).on('click', '#btn-add-address', handleAddNewAddress);
    
    // Handle update address
    $(document).on('click', '.address-update-btn', handleUpdateAddress);
}

/**
 * Handle open address modal
 */
function handleOpenAddressModal() {
    $('#address-modal').fadeIn(200);
    populateAddressModal(allAddresses);
}

/**
 * Handle modal confirm
 */
function handleModalConfirm() {
    const selectedRadio = $('input[name="address-radio"]:checked');
    const newAddressId = selectedRadio.val();
    
    if (!newAddressId) {
        alert('Vui lòng chọn địa chỉ!');
        return;
    }
    
    // Update addressId
    addressId = newAddressId;
    sessionStorage.setItem('selectedAddressId', newAddressId);
    
    // Find and display new address
    const newAddress = allAddresses.find(a => a.id == newAddressId);
    if (newAddress) {
        displayAddress(newAddress);
    }
    
    // Close modal
    $('#address-modal').fadeOut(200);
}

/**
 * Handle modal cancel
 */
function handleModalCancel() {
    $('#address-modal').fadeOut(200);
}

/**
 * Handle radio item click
 */
function handleRadioItemClick(e) {
    // Don't trigger if clicking update button
    if ($(e.target).hasClass('address-update-btn')) {
        return;
    }
    
    // Remove selected class from all items
    $('.address-radio-item').removeClass('selected');
    
    // Add selected class to clicked item
    $(this).addClass('selected');
    
    // Check the radio button
    $(this).find('input[type="radio"]').prop('checked', true);
}

/**
 * Handle add new address
 */
function handleAddNewAddress(e) {
    e.stopPropagation();
    // TODO: Implement add address functionality
    alert('Chức năng thêm địa chỉ mới đang phát triển');
}

/**
 * Handle update address
 */
function handleUpdateAddress(e) {
    e.stopPropagation();
    const addressId = $(this).data('address-id');
    // TODO: Implement update address functionality
    alert('Chức năng cập nhật địa chỉ đang phát triển');
}

/**
 * Handle discount selection
 */
function handleDiscountSelection() {
    // Remove previous selection
    $('.voucher-row').removeClass('selected');
    
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
                $('#btn-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Đặt hàng');
            }
        },
        error: function(xhr, status, error) {
            console.error('Payment error:', error);
            alert('❌ Có lỗi xảy ra khi thanh toán. Vui lòng thử lại!');
            $('#btn-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Đặt hàng');
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
