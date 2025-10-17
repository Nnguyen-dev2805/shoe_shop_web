/**
 * payment.js - Trang thanh toán
 */

let selectedItems = [];
let addressId = null;
let cartId = null;
let subtotal = 0;
let shippingFee = 5000; // Default fee, will be updated by API
let discountPercent = 0;
let discountAmount = 0;
let shippingInfo = null; // Store shipping calculation result

/**
 * Format currency to Vietnamese format
 */
function formatCurrency(amount) {
    if (!amount && amount !== 0) return '0đ';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

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
    
    // Validate: Only check if items data exists
    // Address ID can be empty (user will select it in payment page)
    if (!itemsData) {
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
        const product = item.product.product;
        
        // ✅ Check if flash sale is active
        let basePrice = product.price;
        let isFlashSale = false;
        
        if (product.flashSale && product.flashSale.active) {
            basePrice = product.flashSale.flashSalePrice;
            isFlashSale = true;
            console.log('🔥 Flash Sale Active:', product.title, 'Price:', basePrice);
        }
        
        // Calculate unit price (base price + size add-on)
        const unitPrice = basePrice + item.product.priceadd;
        const totalPrice = unitPrice * item.quantity;
        
        // Price display with flash sale badge
        const priceDisplay = isFlashSale 
            ? `<span style="color: #ee4d2d; font-weight: 600;">${formatPrice(unitPrice)}</span>
               <span style="text-decoration: line-through; color: #999; font-size: 12px; margin-left: 5px;">${formatPrice(product.price + item.product.priceadd)}</span>`
            : formatPrice(unitPrice);
        
        const productHtml = `
            <div class="product-table-row">
                <div class="product-info">
                    <img src="${product.image}" 
                         alt="${product.title}" 
                         class="product-image">
                    <div class="product-details">
                        <div class="product-name">
                            ${product.title}
                            ${isFlashSale ? '<span style="background: #ee4d2d; color: white; padding: 2px 6px; font-size: 11px; border-radius: 3px; margin-left: 5px;">FLASH SALE</span>' : ''}
                        </div>
                        <div class="product-variant">Phân loại: ${item.product.size}</div>
                    </div>
                </div>
                <div class="product-price">${priceDisplay}</div>
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
                
                let address = null;
                
                // If addressId is empty or not found, use default or first address
                if (!addressId || addressId === '') {
                    // Try to find default address
                    address = response.addresses.find(a => a.isDefault === true);
                    
                    // If no default, use first address
                    if (!address && response.addresses.length > 0) {
                        address = response.addresses[0];
                    }
                    
                    // Update addressId
                    if (address) {
                        addressId = address.id;
                        sessionStorage.setItem('selectedAddressId', addressId);
                        console.log('✅ Auto-selected address ID:', addressId);
                    }
                } else {
                    // Find address by ID
                    address = response.addresses.find(a => a.id == addressId);
                }
                
                if (address) {
                    displayAddress(address);
                    
                    // ✅ Populate address modal
                    populateAddressModal(response.addresses);
                } else {
                    // ⚠️ No address available - Show Add Address Modal
                    console.warn('⚠️ No address available - Opening Add Address Modal');
                    $('#delivery-address').html('<div class="loading-text">Đang tải địa chỉ...</div>');
                    
                    // Show modal to add address
                    showAddAddressModal();
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
    
    // ✅ Load shipping fee when address is displayed
    loadShippingFee(address.id);
}

/**
 * Load shipping fee from API based on address
 */
function loadShippingFee(addrId) {
    if (!addrId) {
        console.warn('⚠️ No address ID provided, using default shipping fee');
        return;
    }
    
    console.log('📦 Loading shipping fee for address ID:', addrId);
    
    // Show loading state
    $('#shipping-loading').show();
    $('#shipping-detail').hide();
    
    $.ajax({
        url: '/api/shipping/calculate-fee',
        method: 'GET',
        data: { addressId: addrId },
        success: function(response) {
            console.log('✅ Shipping fee response:', response);
            
            if (response.success) {
                // Update shipping fee
                shippingFee = response.fee;
                shippingInfo = response;
                
                // Update UI
                $('#shipping-fee').text(formatPrice(shippingFee));
                $('#shipping-distance').text(response.formattedDistance);
                $('#shipping-duration').text(response.formattedDuration);
                $('#shipping-detail').show();
                
                console.log('💰 Updated shipping fee:', shippingFee);
                console.log('📍 Distance:', response.formattedDistance);
                console.log('⏱️ Duration:', response.formattedDuration);
                console.log('🏭 Warehouse:', response.warehouseName);
                
                // Recalculate prices
                calculatePrices();
            } else {
                console.error('❌ Shipping fee calculation failed:', response.message);
                // Keep default fee
                calculatePrices();
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error loading shipping fee:', error);
            console.error('Response:', xhr.responseJSON);
            
            // Keep default fee and show error message
            $('#shipping-fee').text(formatPrice(shippingFee) + ' (Tạm tính)');
            
            // Recalculate prices with default fee
            calculatePrices();
        },
        complete: function() {
            // Hide loading state
            $('#shipping-loading').hide();
        }
    });
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
 * Render discounts - Direct Display (No Modal)
 */
function renderDiscounts(discounts) {
    console.log('🎫 Rendering discounts:', discounts);
    console.log('💰 Current subtotal for validation:', subtotal);
    window.allDiscounts = discounts || [];
    
    // Hide loading
    $('#voucher-loading').hide();
    
    if (!discounts || discounts.length === 0) {
        $('#voucher-cards-list').html('<div style="text-align: center; padding: 20px; color: #999;">Không có voucher khả dụng</div>');
        $('#voucher-cards-wrapper').show();
        return;
    }
    
    // Separate available and unavailable vouchers
    const available = [];
    const unavailable = [];
    
    discounts.forEach(discount => {
        // Check status
        const isActive = discount.status === 'ACTIVE';
        
        // Check date range (startDate <= now <= endDate)
        const now = new Date();
        const startDate = discount.startDate ? new Date(discount.startDate) : null;
        const endDate = discount.endDate ? new Date(discount.endDate) : null;
        
        const isWithinDateRange = (!startDate || now >= startDate) && (!endDate || now <= endDate);
        
        // Check minOrderValue (only check subtotal, not including shipping)
        const minOrder = discount.minOrderValue || 0;
        const meetsMinOrder = subtotal >= minOrder;
        
        // Debug log for each voucher
        console.log(`📋 Voucher "${discount.name}":`, {
            status: discount.status,
            isActive,
            startDate: startDate?.toLocaleDateString('vi-VN'),
            endDate: endDate?.toLocaleDateString('vi-VN'),
            now: now.toLocaleDateString('vi-VN'),
            isWithinDateRange,
            minOrderValue: minOrder,
            currentSubtotal: subtotal,
            meetsMinOrder,
            finalAvailable: isActive && isWithinDateRange && meetsMinOrder
        });
        
        if (isActive && isWithinDateRange && meetsMinOrder) {
            available.push(discount);
        } else {
            unavailable.push(discount);
        }
    });
    
    const container = $('#voucher-cards-list');
    container.empty();
    
    console.log(`✅ Available vouchers: ${available.length}`, available.map(d => d.name));
    console.log(`❌ Unavailable vouchers: ${unavailable.length}`, unavailable.map(d => d.name));
    
    // Render Available Vouchers (no section title)
    if (available.length > 0) {
        available.forEach((discount, index) => {
            const card = createVoucherCard(discount, index === 0, false);
            container.append(card);
        });
    }
    
    // Render Unavailable Vouchers
    if (unavailable.length > 0) {
        container.append('<div class="voucher-section-title" style="margin-top: 20px;">Voucher không khả dụng</div>');
        unavailable.forEach(discount => {
            const card = createVoucherCard(discount, false, true);
            container.append(card);
        });
    }
    
    // Show wrapper
    $('#voucher-cards-wrapper').show();
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
    // ✅ Calculate subtotal with flash sale price
    subtotal = selectedItems.reduce((sum, item) => {
        const product = item.product.product;
        let basePrice = product.price;
        
        // Check if flash sale is active
        if (product.flashSale && product.flashSale.active) {
            basePrice = product.flashSale.flashSalePrice;
        }
        
        // Unit price = base price + size add-on
        const unitPrice = basePrice + item.product.priceadd;
        const itemTotal = unitPrice * item.quantity;
        
        return sum + itemTotal;
    }, 0);
    
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

/**
 * Show Add Address Modal (when user has no address)
 */
function showAddAddressModal() {
    console.log('📍 Opening Add Address Modal...');
    
    // Store product page URL for cancel redirect
    const referrer = document.referrer;
    if (referrer && referrer.includes('/product/details/')) {
        sessionStorage.setItem('returnToProduct', referrer);
    }
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('paymentAddAddressModal'));
    modal.show();
    
    // Setup cancel button handlers
    setupCancelAddressHandlers();
    
    // Setup form submit handler
    setupAddAddressFormHandler();
    
    // Note: Goong Map is initialized automatically when modal opens (see payment.html script)
    console.log('✅ Goong Map will be initialized when modal is shown');
}

/**
 * Setup Cancel button handlers - Return to product page
 */
function setupCancelAddressHandlers() {
    const handleCancel = function() {
        console.log('❌ User cancelled adding address');
        
        // Get return URL
        const returnUrl = sessionStorage.getItem('returnToProduct') || document.referrer;
        
        if (returnUrl && returnUrl.includes('/product/details/')) {
            // Redirect back to product page
            window.location.href = returnUrl;
        } else {
            // Fallback: Go to home
            window.location.href = '/';
        }
    };
    
    // Bind to both cancel buttons
    $('#cancelAddressBtn').off('click').on('click', handleCancel);
    $('#cancelAddressFooterBtn').off('click').on('click', handleCancel);
}

/**
 * Setup Add Address Form Submit Handler
 */
function setupAddAddressFormHandler() {
    $('#paymentAddAddressForm').off('submit').on('submit', function(e) {
        e.preventDefault();
        console.log('📝 Submitting add address form...');
        
        // Get form data - MATCH Backend DTO fields exactly!
        const selectedAddr = $('#paymentSelectedAddress').val() || '';
        const street = $('#paymentStreet').val() || '';
        const city = $('#paymentCity').val() || '';
        const country = $('#paymentCountry').val() || 'Việt Nam';
        
        const formData = {
            recipientName: $('#paymentRecipientName').val(),
            recipientPhone: $('#paymentRecipientPhone').val(),
            selectedAddress: selectedAddr,  // ✅ Backend expects 'selectedAddress'
            street: street,                 // ✅ Backend expects 'street'
            city: city,                     // ✅ Backend expects 'city'
            country: country,
            addressType: $('#paymentAddressType').val(),
            isDefault: $('#paymentIsDefault').is(':checked'),
            latitude: parseFloat($('#paymentLatitude').val()) || null,
            longitude: parseFloat($('#paymentLongitude').val()) || null
        };
        
        console.log('📋 Form data to submit:', formData);
        console.log('  - recipientName:', formData.recipientName);
        console.log('  - recipientPhone:', formData.recipientPhone);
        console.log('  - selectedAddress:', formData.selectedAddress);
        console.log('  - street:', formData.street);
        console.log('  - city:', formData.city);
        console.log('  - addressType:', formData.addressType);
        console.log('  - isDefault:', formData.isDefault);
        console.log('  - latitude:', formData.latitude);
        console.log('  - longitude:', formData.longitude);
        
        // Validate - Check required fields
        if (!formData.recipientName || !formData.recipientPhone || !formData.selectedAddress || !formData.street || !formData.city) {
            alert('Vui lòng điền đầy đủ thông tin bắt buộc (Tên, SĐT, Địa chỉ, Số nhà, Tỉnh/TP)!');
            return;
        }
        
        // Submit via AJAX
        $.ajax({
            url: '/api/user/addresses',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                console.log('✅ Address added successfully:', response);
                
                if (response.success) {
                    alert('✅ Thêm địa chỉ thành công!');
                    
                    // Close modal
                    bootstrap.Modal.getInstance(document.getElementById('paymentAddAddressModal')).hide();
                    
                    // Update addressId and reload address info
                    if (response.address && response.address.id) {
                        addressId = response.address.id;
                        sessionStorage.setItem('selectedAddressId', addressId);
                    }
                    
                    // Reload payment page to show new address
                    window.location.reload();
                } else {
                    alert('❌ Lỗi: ' + (response.message || 'Không thể thêm địa chỉ'));
                }
            },
            error: function(xhr, status, error) {
                console.error('❌ Error adding address:', error);
                console.error('Status:', xhr.status);
                console.error('Response:', xhr.responseText);
                console.error('Full XHR:', xhr);
                
                // Try to parse error message
                let errorMessage = 'Có lỗi xảy ra khi thêm địa chỉ. Vui lòng thử lại!';
                try {
                    const errorData = JSON.parse(xhr.responseText);
                    if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    // Use default message
                }
                
                alert('❌ ' + errorMessage);
            }
        });
    });
}

/**
 * ========== INLINE VOUCHER FUNCTIONS ==========
 */

// Global variables for voucher selection
window.allDiscounts = [];
window.selectedVoucherId = null;

/**
 * Create voucher card HTML
 */
function createVoucherCard(discount, isBest, isDisabled) {
    const discountPercent = (discount.percent * 100).toFixed(0);
    const minOrderValue = discount.minOrderValue || 0;
    const endDate = discount.endDate ? new Date(discount.endDate).toLocaleDateString('vi-VN') : '';
    
    const cardClass = `voucher-card ${isDisabled ? 'disabled' : ''}`;
    const checkboxDisabled = isDisabled ? 'disabled' : '';
    
    return `
        <div class="${cardClass}" data-id="${discount.id}" data-percent="${discountPercent}">
            <!-- Voucher Icon -->
            <div class="voucher-card-icon type-discount">
                <div class="voucher-icon-text">-${discountPercent}%</div>
                <div class="voucher-icon-subtext">GIẢM GIÁ</div>
            </div>
            
            <!-- Voucher Content -->
            <div class="voucher-card-content">
                <div class="voucher-card-top">
                    <div class="voucher-card-title">${discount.name}</div>
                    ${isBest ? '<span class="voucher-best-badge">Lựa chọn tốt nhất</span>' : ''}
                </div>
                
                <div class="voucher-card-subtitle">
                    Giảm ${discountPercent}% tổng đơn hàng
                </div>
                
                ${discount.description ? `<div class="voucher-card-special-badge">${discount.description}</div>` : ''}
                
                <div class="voucher-card-footer">
                    <div class="voucher-expiry">
                        <i class="fa fa-clock-o"></i> HSD: ${endDate}
                    </div>
                    <div class="voucher-min-order">
                        Đơn tối thiểu ${formatCurrency(minOrderValue)}
                    </div>
                </div>
            </div>
            
            <!-- Checkbox -->
            <input type="checkbox" 
                   class="voucher-card-checkbox" 
                   ${checkboxDisabled}
                   ${window.selectedVoucherId == discount.id ? 'checked' : ''}>
        </div>
    `;
}

/**
 * Handle voucher card click (Inline)
 */
$(document).on('click', '.voucher-card:not(.disabled)', function() {
    const voucherId = $(this).data('id');
    
    // Remove previous selection
    $('.voucher-card').removeClass('selected');
    $('.voucher-card-checkbox').prop('checked', false);
    
    // Select this one
    $(this).addClass('selected');
    $(this).find('.voucher-card-checkbox').prop('checked', true);
    window.selectedVoucherId = voucherId;
    
    // Update footer count (inline)
    $('#voucher-selected-count-inline').text(voucherId ? '1' : '0');
});

/**
 * Confirm voucher selection (Inline - No Modal)
 */
function confirmVoucherSelection() {
    const voucherId = window.selectedVoucherId;
    
    if (!voucherId) {
        alert('Vui lòng chọn voucher');
        return;
    }
    
    // Find selected voucher
    const voucher = window.allDiscounts.find(d => d.id == voucherId);
    if (!voucher) return;
    
    // Update hidden input
    $('#selected-discount-id').val(voucherId);
    
    // Update discount calculation
    discountPercent = voucher.percent * 100;
    calculatePrices();
    
    // Success message
    alert(`✅ Đã áp dụng voucher: ${voucher.name} (-${(voucher.percent * 100).toFixed(0)}%)`);
    
    console.log('Voucher applied:', voucher);
}

/**
 * Event Listeners (Inline Voucher)
 */
$(document).ready(function() {
    // Confirm selection (inline button)
    $(document).on('click', '#btn-confirm-voucher-inline', function() {
        confirmVoucherSelection();
    });
});
