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

// Voucher tracking
window.appliedShippingDiscount = 0;
window.appliedShippingVoucherId = null;
window.appliedOrderVoucherId = null;
window.orderVouchers = [];
window.shippingVouchers = [];

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
    loadVouchers();
    loadShippingCompanies();
    bindEventHandlers();
});

/**
 * Load data from sessionStorage
 * Supports both Buy Now and Cart checkout
 */
function loadDataFromSession() {
    // Try to get items from either cart or buy now source
    let itemsData = sessionStorage.getItem('selectedCartItems'); // New: from cart page
    let checkoutSource = sessionStorage.getItem('checkoutSource');
    
    // Fallback to old key for backward compatibility (Buy Now)
    if (!itemsData) {
        itemsData = sessionStorage.getItem('selectedItems');
        checkoutSource = 'buynow';
    }
    
    addressId = sessionStorage.getItem('selectedAddressId');
    cartId = sessionStorage.getItem('cartId');
    
    console.log('=== PAYMENT PAGE DEBUG ===');
    console.log('Checkout Source:', checkoutSource);
    console.log('Items data:', itemsData);
    console.log('Address ID:', addressId);
    console.log('Cart ID:', cartId);
    
    // Validate: Only check if items data exists
    // Address ID can be empty (user will select it in payment page)
    if (!itemsData) {
        console.warn('⚠️ No checkout data found in sessionStorage');
        alert('⚠️ Không có dữ liệu thanh toán. Vui lòng chọn sản phẩm trước.');
        window.location.href = '/';
        return;
    }
    
    // ✅ Validate: checkoutSource must be set
    if (!checkoutSource || (checkoutSource !== 'cart' && checkoutSource !== 'buynow')) {
        console.error('❌ Invalid checkoutSource:', checkoutSource);
        console.log('🧹 Clearing invalid sessionStorage data...');
        sessionStorage.clear();
        alert('⚠️ Dữ liệu thanh toán không hợp lệ. Vui lòng thử lại.');
        window.location.href = '/';
        return;
    }
    
    try {
        selectedItems = JSON.parse(itemsData);
        console.log('Parsed items:', selectedItems);
        
        // ✅ Validate: Check if cart items have valid IDs
        if (checkoutSource === 'cart' && selectedItems.length > 0) {
            const hasInvalidIds = selectedItems.some(item => !item.id || item.id <= 0);
            if (hasInvalidIds) {
                console.error('❌ Invalid cart item IDs detected! Clearing sessionStorage...');
                sessionStorage.clear();
                alert('⚠️ Dữ liệu giỏ hàng không hợp lệ. Vui lòng thêm sản phẩm vào giỏ lại.');
                window.location.href = '/cart/view';
                return;
            }
        }
    } catch (e) {
        console.error('Error parsing items:', e);
        alert('Lỗi dữ liệu. Vui lòng thử lại.');
        window.location.href = '/cart/view';
        return;
    }
    
    // Check flash sale for cart items (if from cart checkout)
    if (checkoutSource === 'cart') {
        checkFlashSaleForCartItems(function() {
            // Render after getting flash sale info
            renderSelectedProducts();
            loadAddressInfo();
            calculatePrices();
        });
    } else {
        // Buy Now flow - render directly (flash sale already in product data)
        renderSelectedProducts();
        loadAddressInfo();
        calculatePrices();
    }
}

/**
 * Check flash sale for cart items
 * Call API to get flash sale info and merge into selectedItems
 */
function checkFlashSaleForCartItems(callback) {
    console.log('=== Checking Flash Sale for Cart Items ===');
    
    // Extract product detail IDs
    const productDetailIds = selectedItems.map(item => item.productDetailId);
    console.log('Product Detail IDs:', productDetailIds);
    
    $.ajax({
        url: '/api/cart/check-flash-sale',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ productDetailIds: productDetailIds }),
        success: function(response) {
            if (response.success && response.data) {
                console.log('✅ Flash Sale info received:', response.data);
                
                // Merge flash sale info into selectedItems
                response.data.forEach(flashSaleInfo => {
                    const item = selectedItems.find(i => i.productDetailId === flashSaleInfo.productDetailId);
                    if (item && flashSaleInfo.hasFlashSale) {
                        // Add flash sale info to product
                        if (!item.product.product.flashSale) {
                            item.product.product.flashSale = {};
                        }
                        item.product.product.flashSale.active = true;
                        item.product.product.flashSale.flashSalePrice = flashSaleInfo.flashSalePrice;
                        item.product.product.flashSale.originalPrice = flashSaleInfo.originalPrice;
                        item.product.product.flashSale.discountPercent = flashSaleInfo.discountPercent;
                        
                        console.log('🔥 Applied flash sale to:', item.product.product.title, 
                                    'Price:', flashSaleInfo.flashSalePrice);
                    }
                });
                
                // Call callback to continue rendering
                if (callback) callback();
            } else {
                console.warn('No flash sale data returned');
                if (callback) callback();
            }
        },
        error: function(xhr, status, error) {
            console.error('Error checking flash sale:', error);
            // Continue rendering even if flash sale check fails
            if (callback) callback();
        }
    });
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
 * Load order vouchers và shipping vouchers
 */
function loadVouchers() {
    console.log('Loading vouchers...');
    
    // Load order vouchers
    $.ajax({
        url: '/api/vouchers/order',
        method: 'GET',
        success: function(response) {
            console.log('Order vouchers response:', response);
            if (response && response.success) {
                renderOrderVouchers(response.data);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading order vouchers:', error);
            $('#order-voucher-loading').html('<div style="color: #999;">Không thể tải voucher</div>');
        }
    });
    
    // Load shipping vouchers
    $.ajax({
        url: '/api/vouchers/shipping',
        method: 'GET',
        success: function(response) {
            console.log('Shipping vouchers response:', response);
            if (response && response.success) {
                renderShippingVouchers(response.data);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading shipping vouchers:', error);
            $('#shipping-voucher-loading').html('<div style="color: #999;">Không thể tải voucher ship</div>');
        }
    });
}

/**
 * Render order vouchers
 */
function renderOrderVouchers(vouchers) {
    console.log('🎫 Rendering order vouchers:', vouchers);
    window.orderVouchers = vouchers || [];
    
    // Hide loading
    $('#order-voucher-loading').hide();
    
    if (!vouchers || vouchers.length === 0) {
        $('#order-voucher-list').html('<div style="text-align: center; padding: 20px; color: #999;">Không có voucher đơn hàng</div>');
        $('#order-voucher-wrapper').show();
        return;
    }
    
    const container = $('#order-voucher-list');
    container.empty();
    
    vouchers.forEach((voucher, index) => {
        // Format date
        const endDate = voucher.endDate ? new Date(voucher.endDate).toLocaleDateString('vi-VN') : 'Không giới hạn';
        
        // ✅ Check discount type and format accordingly
        let badgeText, description, badgeClass;
        if (voucher.discountValueType === 'PERCENTAGE') {
            const discountPercent = Math.round(voucher.percent * 100); // Backend lưu 0-1, nhân 100 để ra %
            badgeText = `-${discountPercent}%`;
            description = `Giảm ${discountPercent}% tổng đơn hàng`;
            badgeClass = '';
        } else {
            // FIXED_AMOUNT
            badgeText = formatCurrency(voucher.percent);
            description = `Giảm ${formatCurrency(voucher.percent)} tổng đơn hàng`;
            badgeClass = 'fixed-amount';
        }
        
        // Check if best choice (first available voucher)
        const isBestChoice = index === 0;
        
        const voucherHtml = `
            <div class="shopee-voucher-card" data-voucher-id="${voucher.id}" data-type="order">
                <div class="voucher-left">
                    <div class="voucher-badge">
                        <div class="badge-percent ${badgeClass}">${badgeText}</div>
                        <div class="badge-label">GIẢM GIÁ</div>
                    </div>
                </div>
                <div class="voucher-middle">
                    <div class="voucher-title">
                        ${voucher.name}
                        ${isBestChoice ? '<span class="best-choice-badge">Lựa chọn tốt nhất</span>' : ''}
                    </div>
                    <div class="voucher-description">${description}</div>
                    <div class="voucher-expiry">
                        <i class="fa fa-clock-o"></i> HSD: ${endDate}
                    </div>
                </div>
                <div class="voucher-right">
                    <div class="voucher-min-order">Đơn tối thiểu ${formatCurrency(voucher.minOrderValue || 0)}</div>
                    <div class="voucher-checkbox">
                        <input type="radio" name="order-voucher" value="${voucher.id}" id="order-voucher-${voucher.id}">
                    </div>
                </div>
            </div>
        `;
        container.append(voucherHtml);
    });
    
    $('#order-voucher-count').text('0');
    $('#order-voucher-wrapper').show();
}

/**
 * Render shipping vouchers
 */
function renderShippingVouchers(vouchers) {
    console.log('🚚 Rendering shipping vouchers:', vouchers);
    window.shippingVouchers = vouchers || [];
    
    // Hide loading
    $('#shipping-voucher-loading').hide();
    
    if (!vouchers || vouchers.length === 0) {
        $('#shipping-voucher-list').html('<div style="text-align: center; padding: 20px; color: #999;">Không có voucher ship</div>');
        $('#shipping-voucher-wrapper').show();
        return;
    }
    
    const shippingContainer = $('#shipping-voucher-list');
    shippingContainer.empty();
    
    vouchers.forEach((voucher, index) => {
        // Format date
        const endDate = voucher.endDate ? new Date(voucher.endDate).toLocaleDateString('vi-VN') : 'Không giới hạn';
        
        // ✅ Calculate discount text
        let badgeText = '';
        let descriptionText = '';
        let badgeClass = '';
        
        if (voucher.discountValueType === 'PERCENTAGE') {
            const percent = Math.round(voucher.percent * 100); // Backend lưu 0-1, nhân 100 để ra %
            badgeText = `-${percent}%`;
            descriptionText = `Giảm ${percent}% phí ship`;
            if (voucher.maxDiscountAmount) {
                descriptionText += ` (Tối đa ${formatCurrency(voucher.maxDiscountAmount)})`;
            }
        } else {
            // FIXED_AMOUNT
            badgeText = formatCurrency(voucher.percent);
            descriptionText = `Giảm ${formatCurrency(voucher.percent)} phí ship`;
            badgeClass = 'fixed-amount';
        }
        
        const isBestChoice = index === 0;
        
        const voucherHtml = `
            <div class="shopee-voucher-card shipping-voucher-card" data-voucher-id="${voucher.id}" data-type="shipping">
                <div class="voucher-left shipping">
                    <div class="voucher-badge shipping">
                        <div class="badge-percent ${badgeClass}">${badgeText}</div>
                        <div class="badge-label">SHIP</div>
                    </div>
                </div>
                <div class="voucher-middle">
                    <div class="voucher-title">
                        ${voucher.name}
                        ${isBestChoice ? '<span class="best-choice-badge">Lựa chọn tốt nhất</span>' : ''}
                    </div>
                    <div class="voucher-description">${descriptionText}</div>
                    <div class="voucher-expiry">
                        <i class="fa fa-clock-o"></i> HSD: ${endDate}
                    </div>
                </div>
                <div class="voucher-right">
                    <div class="voucher-min-order">Đơn tối thiểu ${formatCurrency(voucher.minOrderValue || 0)}</div>
                    <div class="voucher-checkbox">
                        <input type="radio" name="shipping-voucher" value="${voucher.id}" id="shipping-voucher-${voucher.id}">
                    </div>
                </div>
            </div>
        `;
        shippingContainer.append(voucherHtml);
    });
    
    $('#shipping-voucher-count').text('0');
    $('#shipping-voucher-wrapper').show();
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
    
    // Calculate order discount (only if using percentage)
    if (discountPercent > 0) {
        discountAmount = subtotal * (discountPercent / 100);
    }
    // If discountAmount already set (FIXED_AMOUNT voucher), keep it
    
    // Get shipping discount (if applied)
    const shippingDiscount = window.appliedShippingDiscount || 0;
    const finalShippingFee = shippingFee - shippingDiscount;
    
    console.log('Shipping discount:', shippingDiscount);
    console.log('Final shipping fee:', finalShippingFee);
    
    // Calculate final total
    const finalTotal = subtotal + finalShippingFee - discountAmount;
    
    console.log('Final total:', finalTotal);
    
    // Update UI
    $('#subtotal-price').text(formatPrice(subtotal));
    updateShippingFeeDisplay();
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
 * Update shipping fee display with discount
 */
function updateShippingFeeDisplay() {
    const shippingDiscount = window.appliedShippingDiscount || 0;
    const finalShippingFee = shippingFee - shippingDiscount;
    
    if (shippingDiscount > 0) {
        // Show strikethrough original price and discounted price
        $('#shipping-fee').html(
            '<span style="text-decoration: line-through; color: #999; font-size: 13px;">' + 
            formatPrice(shippingFee) + 
            '</span> ' +
            '<span style="color: #ee4d2d; font-weight: 600;">' + 
            formatPrice(finalShippingFee) + 
            '</span>'
        );
    } else {
        // Show normal price
        $('#shipping-fee').text(formatPrice(shippingFee));
    }
}

/**
 * Bind event handlers
 */
function bindEventHandlers() {
    // Voucher card click - toggle checkbox
    $(document).on('click', '.shopee-voucher-card', function(e) {
        // Don't trigger if clicking directly on checkbox
        if ($(e.target).is('input[type="radio"]')) {
            return;
        }
        
        const radio = $(this).find('input[type="radio"]');
        radio.prop('checked', true);
        radio.trigger('change');
    });
    
    // Order voucher selection
    $(document).on('change', 'input[name="order-voucher"]', function() {
        const count = $('input[name="order-voucher"]:checked').length;
        $('#order-voucher-count').text(count);
    });
    
    // Shipping voucher selection
    $(document).on('change', 'input[name="shipping-voucher"]', function() {
        const count = $('input[name="shipping-voucher"]:checked').length;
        $('#shipping-voucher-count').text(count);
    });
    
    // Confirm order voucher button
    $('#btn-confirm-order-voucher').on('click', function() {
        const selectedId = $('input[name="order-voucher"]:checked').val();
        if (!selectedId) {
            alert('Vui lòng chọn voucher!');
            return;
        }
        
        // Find selected voucher from stored array
        const selectedVoucher = window.orderVouchers.find(v => v.id == selectedId);
        if (!selectedVoucher) {
            alert('Không tìm thấy voucher!');
            return;
        }
        
        console.log('✅ Applying order voucher:', selectedVoucher);
        
        // Check min order value
        if (selectedVoucher.minOrderValue && subtotal < selectedVoucher.minOrderValue) {
            alert('❌ Đơn hàng chưa đủ điều kiện áp dụng voucher này!\nYêu cầu tối thiểu: ' + formatCurrency(selectedVoucher.minOrderValue));
            return;
        }
        
        // ✅ Calculate order discount based on type
        if (selectedVoucher.discountValueType === 'PERCENTAGE') {
            // Percentage discount (backend stores as 0-1, multiply by 100 for display)
            const orderDiscountPercent = selectedVoucher.percent * 100;
            discountPercent = orderDiscountPercent;
            
            console.log('💰 Order discount:', orderDiscountPercent + '%');
            alert('✅ Đã áp dụng voucher đơn hàng: Giảm ' + orderDiscountPercent + '%');
        } else {
            // FIXED_AMOUNT discount
            discountPercent = 0; // Reset percentage
            discountAmount = selectedVoucher.percent; // Use fixed amount directly
            
            console.log('💰 Order discount:', formatCurrency(selectedVoucher.percent));
            alert('✅ Đã áp dụng voucher đơn hàng: Giảm ' + formatCurrency(selectedVoucher.percent));
        }
        
        window.appliedOrderVoucherId = selectedId;
        
        // Recalculate total
        calculatePrices();
    });
    
    // Confirm shipping voucher button
    $('#btn-confirm-shipping-voucher').on('click', function() {
        const selectedId = $('input[name="shipping-voucher"]:checked').val();
        if (!selectedId) {
            alert('Vui lòng chọn voucher ship!');
            return;
        }
        
        // Find selected voucher from stored array
        const selectedVoucher = window.shippingVouchers.find(v => v.id == selectedId);
        if (!selectedVoucher) {
            alert('Không tìm thấy voucher!');
            return;
        }
        
        console.log('✅ Applying shipping voucher:', selectedVoucher);
        
        // Calculate shipping discount
        let shippingDiscount = 0;
        
        if (selectedVoucher.discountValueType === 'PERCENTAGE') {
            // Percentage discount
            shippingDiscount = shippingFee * selectedVoucher.percent;
            
            // Apply max discount if exists
            if (selectedVoucher.maxDiscountAmount && shippingDiscount > selectedVoucher.maxDiscountAmount) {
                shippingDiscount = selectedVoucher.maxDiscountAmount;
            }
        } else {
            // FIXED_AMOUNT - voucher.percent contains the fixed amount
            shippingDiscount = Math.min(selectedVoucher.percent, shippingFee);
        }
        
        // Don't exceed shipping fee
        shippingDiscount = Math.min(shippingDiscount, shippingFee);
        
        console.log('💰 Shipping discount calculated:', shippingDiscount);
        console.log('   Original shipping fee:', shippingFee);
        console.log('   Discount amount:', shippingDiscount);
        console.log('   Final shipping fee:', shippingFee - shippingDiscount);
        
        // Store applied shipping discount
        window.appliedShippingDiscount = shippingDiscount;
        window.appliedShippingVoucherId = selectedId;
        
        // Update display
        updateShippingFeeDisplay();
        
        // Recalculate total
        calculatePrices();
        
        alert('✅ Đã áp dụng voucher ship: Giảm ' + formatCurrency(shippingDiscount));
    });
    
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
    console.log('========== HANDLE PAYMENT DEBUG ==========');
    
    const paymentMethod = $('input[name="paymentMethod"]:checked').val();
    const shippingCompanyId = $('#shippingCompanySelect').val();
    
    // Validation
    if (!addressId) {
        alert('Vui lòng chọn địa chỉ giao hàng!');
        return;
    }
    
    if (!shippingCompanyId) {
        alert('Vui lòng chọn đơn vị vận chuyển!');
        return;
    }
    
    if (!paymentMethod) {
        alert('Vui lòng chọn phương thức thanh toán!');
        return;
    }
    
    // Calculate final total correctly with shipping discount
    const shippingDiscount = window.appliedShippingDiscount || 0;
    const finalShippingFee = shippingFee - shippingDiscount;
    const finalTotal = subtotal + finalShippingFee - discountAmount;
    
    console.log('💰 Payment Calculation:');
    console.log('  Subtotal:', subtotal);
    console.log('  Original shipping fee:', shippingFee);
    console.log('  Shipping discount:', shippingDiscount);
    console.log('  Final shipping fee:', finalShippingFee);
    console.log('  Order discount amount:', discountAmount);
    console.log('  FINAL TOTAL:', finalTotal);
    
    // Collect selected item IDs and quantities
    const selectedItemIds = selectedItems.map(item => item.id);
    const selectedItemsData = selectedItems.map(item => ({
        id: item.id,
        quantity: item.quantity
    }));
    
    // Get applied voucher IDs
    const orderVoucherId = window.appliedOrderVoucherId || null;
    const shippingVoucherId = window.appliedShippingVoucherId || null;
    
    // 🔥 Get flash sale ID from sessionStorage (for Buy Now mode)
    const flashSaleId = sessionStorage.getItem('flashSaleId');
    
    console.log('🎫 Applied Vouchers:');
    console.log('  Order voucher ID:', orderVoucherId);
    console.log('  Shipping voucher ID:', shippingVoucherId);
    console.log('🔥 Flash Sale ID:', flashSaleId);
    
    const paymentData = {
        cartId: cartId ? parseInt(cartId) : null,
        addressId: parseInt(addressId),
        shippingCompanyId: parseInt(shippingCompanyId),
        orderDiscountId: orderVoucherId ? parseInt(orderVoucherId) : null,
        shippingDiscountId: shippingVoucherId ? parseInt(shippingVoucherId) : null,
        flashSaleId: flashSaleId ? parseInt(flashSaleId) : null, // 🔥 THÊM FLASH SALE ID
        payOption: paymentMethod,
        finalTotalPrice: finalTotal,
        subtotal: subtotal, // ✅ THÊM subtotal
        shippingFee: shippingFee, // ✅ THÊM shipping fee gốc
        orderDiscountAmount: discountAmount, // ✅ THÊM discount amount
        shippingDiscountAmount: shippingDiscount, // ✅ THÊM shipping discount
        selectedItemIds: selectedItemIds,
        selectedItemsData: selectedItemsData
    };
    
    console.log('📦 Payment Data:', paymentData);
    
    // Show loading
    $('#btn-payment').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Đang xử lý...');
    
    // Call API
    $.ajax({
        url: '/api/order/pay',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(paymentData),
        success: function(response) {
            console.log('✅ Payment API Response:', response);
            
            if (response.success) {
                console.log('🎉 Payment request successful!');
                console.log('🎉 Order placed successfully!');
                
                // ✅ Clear ALL session storage related to checkout
                sessionStorage.removeItem('selectedItems');        // Buy Now data
                sessionStorage.removeItem('selectedCartItems');    // Cart data
                sessionStorage.removeItem('selectedAddressId');
                sessionStorage.removeItem('cartId');
                sessionStorage.removeItem('checkoutSource');       // ✅ THÊM
                sessionStorage.removeItem('buyNowMode');           // ✅ THÊM
                sessionStorage.removeItem('flashSaleId');          // 🔥 Clear flash sale
                
                console.log('✅ SessionStorage cleared after successful payment');
                
                // Clear applied vouchers
                window.appliedShippingDiscount = 0;
                window.appliedShippingVoucherId = null;
                window.appliedOrderVoucherId = null;
                
                if (response.data && response.data.paymentUrl) {
                    // PayOS payment: Save data to sessionStorage before opening payment
                    console.log('💳 PayOS payment - saving data and opening payment window...');
                    
                    // Save payment data for order creation after PayOS return
                    sessionStorage.setItem('payosPaymentData', JSON.stringify(paymentData));
                    sessionStorage.setItem('payosPaymentPending', 'true');
                    sessionStorage.setItem('payosOrderCode', response.data.payosOrderCode); // Save orderCode for verification
                    
                    console.log('✅ Data saved to sessionStorage');
                    console.log('PayOS Order Code:', response.data.payosOrderCode);
                    console.log('Opening PayOS payment in new window...');
                    
                    // Open PayOS payment in new window/tab
                    const paymentWindow = window.open(response.data.paymentUrl, '_blank');
                    
                    if (paymentWindow) {
                        // Show message to user
                        alert('🔔 Vui lòng hoàn tất thanh toán trong cửa sổ mới.\n\nSau khi thanh toán xong, quay lại trang này và nhấn "Xác nhận đã thanh toán".');
                        
                        // Show confirmation button
                        showPaymentConfirmationButton();
                    } else {
                        // Popup blocked - fallback to redirect
                        alert('⚠️ Trình duyệt chặn popup. Đang chuyển hướng...');
                        window.location.href = response.data.paymentUrl;
                    }
                } else {
                    // COD payment: Order already created
                    console.log('📦 COD payment - order created successfully!');
                    
                    // Clear session storage
                    sessionStorage.removeItem('selectedItems');
                    sessionStorage.removeItem('selectedCartItems');
                    sessionStorage.removeItem('selectedAddressId');
                    sessionStorage.removeItem('cartId');
                    sessionStorage.removeItem('checkoutSource');
                    sessionStorage.removeItem('buyNowMode');
                    
                    // Clear applied vouchers
                    window.appliedShippingDiscount = 0;
                    window.appliedShippingVoucherId = null;
                    window.appliedOrderVoucherId = null;
                    
                    alert('✅ Đặt hàng thành công!');
                    window.location.href = '/user/order/view';
                }
            } else {
                console.error('❌ Payment failed:', response.message);
                alert('❌ ' + (response.message || 'Có lỗi xảy ra'));
                $('#btn-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Đặt hàng');
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Payment API Error:');
            console.error('  Status:', status);
            console.error('  Error:', error);
            console.error('  Response:', xhr.responseText);
            
            let errorMessage = 'Có lỗi xảy ra khi thanh toán. Vui lòng thử lại!';
            
            try {
                const errorResponse = JSON.parse(xhr.responseText);
                if (errorResponse.message) {
                    errorMessage = errorResponse.message;
                }
            } catch (e) {
                // Keep default error message
            }
            
            alert('❌ ' + errorMessage);
            $('#btn-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Đặt hàng');
        }
    });
}

/**
 * Show payment confirmation button after opening PayOS window
 */
function showPaymentConfirmationButton() {
    // Disable the original payment button
    $('#btn-payment').prop('disabled', true).html('<i class="fa fa-clock-o"></i> Đang chờ thanh toán...');
    
    // Create confirmation button
    const confirmButton = $('<button>')
        .attr('id', 'btn-confirm-payment')
        .addClass('btn btn-success btn-lg')
        .css({
            'margin-left': '10px',
            'animation': 'pulse 2s infinite'
        })
        .html('<i class="fa fa-check-circle"></i> Tôi đã thanh toán xong')
        .on('click', handlePaymentConfirmation);
    
    // Add button next to payment button
    $('#btn-payment').after(confirmButton);
    
    // Add CSS animation
    if (!$('#pulse-animation').length) {
        $('<style id="pulse-animation">')
            .text('@keyframes pulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.05); } }')
            .appendTo('head');
    }
}

/**
 * Handle payment confirmation after user completes PayOS payment
 */
function handlePaymentConfirmation() {
    console.log('=== User confirmed payment ===');
    
    // Check if payment data exists
    const paymentData = sessionStorage.getItem('payosPaymentData');
    const paymentPending = sessionStorage.getItem('payosPaymentPending');
    const payosOrderCode = sessionStorage.getItem('payosOrderCode');
    
    if (!paymentData || paymentPending !== 'true' || !payosOrderCode) {
        alert('❌ Không tìm thấy thông tin thanh toán. Vui lòng thử lại.');
        return;
    }
    
    console.log('Payment data found, verifying payment status...');
    console.log('PayOS Order Code:', payosOrderCode);
    
    // Show loading - verifying
    $('#btn-confirm-payment').prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Đang xác minh thanh toán...');
    
    // Step 1: Verify payment status with PayOS
    $.ajax({
        url: '/api/order/verify-payos-payment',
        method: 'GET',
        data: { orderCode: payosOrderCode },
        success: function(verifyResponse) {
            console.log('✅ Payment verification response:', verifyResponse);
            console.log('Response success:', verifyResponse.success);
            console.log('Response status:', verifyResponse.status);
            
            // Check if payment is PAID (case-insensitive)
            const isPaid = verifyResponse.status && verifyResponse.status.toUpperCase() === 'PAID';
            
            if (isPaid) {
                // Payment verified! Now create order
                console.log('✅ Payment verified as PAID, creating order...');
                
                // Update loading text
                $('#btn-confirm-payment').html('<i class="fa fa-spinner fa-spin"></i> Đang tạo đơn hàng...');
                
                // Parse payment data
                const data = JSON.parse(paymentData);
                
                // Add flag to tell backend this is payment confirmation
                data.isPaymentConfirmation = true;
                
                // Step 2: Create order
                $.ajax({
                    url: '/api/order/pay',
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(data),
                    success: function(response) {
                        console.log('✅ Order created:', response);
                        
                        if (response.success && response.data && response.data.orderId) {
                            // Clear payment data
                            sessionStorage.removeItem('payosPaymentData');
                            sessionStorage.removeItem('payosPaymentPending');
                            sessionStorage.removeItem('payosOrderCode');
                            
                            // Clear checkout data
                            sessionStorage.removeItem('selectedItems');
                            sessionStorage.removeItem('selectedCartItems');
                            sessionStorage.removeItem('selectedAddressId');
                            sessionStorage.removeItem('cartId');
                            sessionStorage.removeItem('checkoutSource');
                            sessionStorage.removeItem('buyNowMode');
                            
                            alert('✅ Thanh toán thành công! Đơn hàng #' + response.data.orderId + ' đã được tạo.');
                            window.location.href = '/user/order/view';
                        } else {
                            alert('❌ Có lỗi khi tạo đơn hàng. Vui lòng liên hệ hỗ trợ.');
                            $('#btn-confirm-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Tôi đã thanh toán xong');
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error('❌ Failed to create order:', error);
                        alert('❌ Có lỗi khi tạo đơn hàng: ' + error);
                        $('#btn-confirm-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Tôi đã thanh toán xong');
                    }
                });
                
            } else {
                // Payment not verified
                console.log('❌ Payment not verified:', verifyResponse.status);
                alert('❌ ' + verifyResponse.message);
                $('#btn-confirm-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Tôi đã thanh toán xong');
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Failed to verify payment:', error);
            alert('❌ Không thể xác minh trạng thái thanh toán. Vui lòng thử lại sau.');
            $('#btn-confirm-payment').prop('disabled', false).html('<i class="fa fa-check-circle"></i> Tôi đã thanh toán xong');
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
