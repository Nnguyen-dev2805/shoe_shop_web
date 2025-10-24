// Global variables
let currentQuantity = 1;
let maxStock = 0; // Will be set from API
let selectedSizeData = null;
let baseProductPrice = 0;

$(document).ready(function() {
    // Lấy product ID từ URL
    const productId = getProductIdFromUrl();
    
    if (productId) {
        loadProductDetails(productId);
    } else {
        console.error('Không tìm thấy product ID trong URL');
        window.location.href = '/';
    }
    
    // Setup quantity controls
    setupQuantityControls();
    
    // Setup add to cart button
    setupAddToCartButton();
});

/**
 * Lấy Product ID từ URL
 * URL format: /product/details/{id}
 */
function getProductIdFromUrl() {
    const path = window.location.pathname;
    const parts = path.split('/');
    return parts[parts.length - 1];
}

/**
 * Gọi API để load thông tin chi tiết sản phẩm
 */
function loadProductDetails(productId) {
    console.log('📡 Loading product details for ID:', productId);
    $.ajax({
        url: `/api/product/${productId}`,
        type: 'GET',
        dataType: 'json',
        success: function(product) {
            console.log('✅ API Response - Full Product Data:', product);
            console.log('🔥 API Response - FlashSale Field:', product.flashSale);
            
            if (product.flashSale) {
                console.log('  flashSale.active:', product.flashSale.active);
                console.log('  flashSale.flashSalePrice:', product.flashSale.flashSalePrice);
                console.log('  flashSale.discountPercent:', product.flashSale.discountPercent);
                console.log('  flashSale.endTime:', product.flashSale.endTime);
                console.log('  flashSale.stock:', product.flashSale.stock);
                console.log('  flashSale.sold:', product.flashSale.sold);
            } else {
                console.log('⚠️ No flashSale field in API response');
            }
            
            renderProductDetails(product);
        },
        error: function(xhr, status, error) {
            console.error('❌ Lỗi khi load product:', error);
            console.error('XHR Response:', xhr.responseText);
            alert('Không thể tải thông tin sản phẩm. Vui lòng thử lại sau.');
            window.location.href = '/';
        }
    });
}

/**
 * Render thông tin sản phẩm vào HTML
 */
function renderProductDetails(product) {
    console.log('=== 🔍 DEBUG renderProductDetails() ===');
    console.log('📦 Full Product Data:', product);
    console.log('🔥 FlashSale Object:', product.flashSale);
    
    // Store product data globally for Buy Now feature
    window.currentProduct = product;
    
    // Render ảnh sản phẩm
    $('#product-image').attr('src', product.image);
    $('#product-image').attr('data-zoom-image', product.image);
    
    // Render tên sản phẩm
    $('#product-title').text(product.title);
    
    // Render rating (Shopee style)
    const avgRating = product.avgRating ? product.avgRating.toFixed(1) : '0.0';
    $('#product-rating-number').text(avgRating);
    if (product.totalReviews && product.totalReviews > 0) {
        $('#product-reviews').text(`${product.totalReviews} Đánh Giá`);
    } else {
        $('#product-reviews').text("0 Đánh Giá");
    }
    
    // Render sold quantity
    const soldQty = product.soldQuantity || 0;
    $('#product-sold-count').text(formatSoldQuantity(soldQty));
    console.log('💰 Sold Quantity:', soldQty);

    // Check Flash Sale
    console.log('🔍 Checking Flash Sale...');
    console.log('  product.flashSale exists?', !!product.flashSale);
    if (product.flashSale) {
        console.log('  product.flashSale.active?', product.flashSale.active);
    }
    
    const hasFlashSale = product.flashSale && product.flashSale.active;
    console.log('✅ Has Flash Sale?', hasFlashSale);
    
    if (hasFlashSale) {
        console.log('🔥 Flash Sale DETECTED! Rendering...');
        // Store flash sale data
        window.currentFlashSale = product.flashSale;
        
        // Show flash sale UI
        renderFlashSale(product.flashSale, product.price);
    } else {
        console.log('❌ No Flash Sale - Normal price display');
        // Clear flash sale data
        window.currentFlashSale = null;
        
        // Normal price display
        const formattedPrice = formatVND(product.price);
        $('#product-price').text(formattedPrice);
    }
    
    baseProductPrice = product.price; // Store base price
    
    // Render mô tả
    $('#product-description').text(product.description || 'Chưa có mô tả');
    
    // Render category và brand (nếu cần hiển thị)
    $('#product-category').text(product.categoryName || 'N/A');
    $('#product-brand').text(product.brandName || 'N/A');
    
    // Render size buttons
    renderSizeButtons(product.sizeOptions, product.price);
    
    // Set product ID cho các elements khác
    $('.add-to-wishlist').attr('data-product-id', product.id);
    $('#viewAllReview').attr('data-product-id', product.id);
    $('input[name="productId"]').val(product.id);
    $('#product-review-title').text(`You are reviewing: ${product.title}`);
    
    // ✅ Trigger wishlist check after setting product ID
    if (typeof checkProductInWishlist === 'function') {
        checkProductInWishlist(product.id);
        console.log('🔄 Triggered wishlist check for product:', product.id);
    }
}

/**
 * Render size buttons (thay vì dropdown)
 */
function renderSizeButtons(sizeOptions, basePrice) {
    const $container = $('#size-buttons');
    $container.empty();
    
    console.log('🔧 renderSizeButtons called with:', sizeOptions);
    console.log('📊 Total size options:', sizeOptions ? sizeOptions.length : 0);
    
    if (sizeOptions && sizeOptions.length > 0) {
        sizeOptions.forEach(function(size, index) {
            console.log(`  [${index}] Size:`, size.size, 'ID:', size.id, 'Stock:', size.stock);
            const priceAdd = size.priceAdd || 0;
            const stock = size.stock || 0; // Get stock from API
            const sizeLabel = size.size; // Just show size number
            
            const button = $(`
                <button type="button" class="shopee-size-btn ${stock === 0 ? 'out-of-stock' : ''}" 
                        data-size-id="${size.id}" 
                        data-size="${size.size}"
                        data-price-add="${priceAdd}"
                        data-stock="${stock}">
                    ${sizeLabel}${stock === 0 ? '<span style="display:block;font-size:9px;color:#999;margin-top:2px;">Hết hàng</span>' : ''}
                </button>
            `);
            
            // Click handler cho size button (always attach, handle out-of-stock in selectSize)
            button.on('click', function() {
                selectSize($(this), basePrice);
            });
            
            $container.append(button);
        });
    } else {
        $container.html('<p>Không có size nào</p>');
    }
}

/**
 * Xử lý khi chọn size
 */
function selectSize($button, basePrice) {
    // Get data from button
    const sizeId = $button.data('size-id');
    const sizeName = $button.data('size');
    const priceAdd = $button.data('price-add');
    const stock = $button.data('stock');
    
    // Remove active class from all buttons
    $('.shopee-size-btn').removeClass('active');
    
    // Add active class to selected button
    $button.addClass('active');
    
    // Store selected size data
    selectedSizeData = {
        sizeId: sizeId,
        sizeName: sizeName,
        priceAdd: priceAdd,
        stock: stock,
        basePrice: basePrice
    };
    
    // Store product detail globally for Buy Now feature
    window.currentProductDetail = {
        id: sizeId,
        size: sizeName,
        priceadd: priceAdd,
        quantity: stock
    };
    
    // ✅ Set current product detail for WebSocket realtime updates
    if (window.productWS) {
        window.productWS.setCurrentProductDetail(sizeId);
        console.log('✅ WebSocket tracking product detail:', sizeId);
    }
    
    maxStock = stock;
    
    // Update hidden inputs
    $('#productDetailId').val(sizeId);
    $('#selectedSize').val(sizeName);
    
    // Show quantity row (Shopee style)
    $('#quantity-row').show();
    $('#stock-quantity').text(stock);
    
    // Show price breakdown
    $('#price-breakdown').show()
    
    // Reset quantity to 1
    currentQuantity = 1;
    $('#qty-input').val(currentQuantity);
    
    // Update price display
    updatePriceDisplay(basePrice, priceAdd);
    
    // Enable/disable buttons based on stock
    const $addButton = $('#add-to-cart-btn');
    const $buyNowButton = $('#buy-now-btn');
    const $qtyMinus = $('#qty-minus');
    const $qtyPlus = $('#qty-plus');
    const $qtyInput = $('#qty-input');
    
    if (maxStock > 0) {
        // Enable all controls
        $addButton.prop('disabled', false);
        $addButton.html('<i class="fa fa-shopping-cart"></i><span>Thêm Vào Giỏ Hàng</span>');
        
        $buyNowButton.prop('disabled', false);
        $buyNowButton.html('<span>Mua Ngay</span>');
        
        $qtyMinus.prop('disabled', false);
        $qtyPlus.prop('disabled', false);
        $qtyInput.prop('disabled', false);
    } else {
        // Disable all controls for out-of-stock
        $addButton.prop('disabled', true);
        $addButton.html('<i class="fa fa-ban"></i><span>Hết Hàng</span>');
        $addButton.css('background', '#ccc');
        
        $buyNowButton.prop('disabled', true);
        $buyNowButton.html('<span>Hết Hàng</span>');
        $buyNowButton.css('background', '#ccc');
        
        $qtyMinus.prop('disabled', true);
        $qtyPlus.prop('disabled', true);
        $qtyInput.prop('disabled', true);
    }
}

/**
 * Cập nhật hiển thị giá
 */
function updatePriceDisplay(basePrice, sizeFee) {
    // Check if there's an active flash sale
    let effectiveBasePrice = basePrice;
    
    if (window.currentFlashSale && window.currentFlashSale.active) {
        // Use flash sale price instead of base price
        effectiveBasePrice = window.currentFlashSale.flashSalePrice;
        console.log('🔥 Using Flash Sale Price:', effectiveBasePrice);
    }
    
    const pricePerUnit = effectiveBasePrice + sizeFee; // Giá 1 đôi giày (flash/base + size fee)
    const totalPrice = pricePerUnit * currentQuantity; // Tổng tiền = giá 1 đôi * số lượng
    
    console.log('💰 Price Calculation:', {
        basePrice,
        effectiveBasePrice,
        sizeFee,
        pricePerUnit,
        quantity: currentQuantity,
        totalPrice
    });
    
    // Main product price KHÔNG ĐỔI (giữ giá gốc)
    // $('#product-price').text(formatVND(totalPrice)); // REMOVED
    
    // Update price breakdown section
    $('#base-price-display').text(formatVND(effectiveBasePrice)); // Giá cơ bản (flash hoặc gốc)
    $('#size-fee-display').text(formatVND(sizeFee)); // Phụ phí size
    $('#final-price-display').text(formatVND(totalPrice)); // Tổng cộng (hiển thị)
    $('#final-price').val(totalPrice); // Hidden input (for form submission)
}

/**
 * Setup quantity controls (+/- buttons)
 */
function setupQuantityControls() {
    // Minus button
    $('#qty-minus').on('click', function() {
        if (currentQuantity > 1) {
            currentQuantity--;
            $('#qty-input').val(currentQuantity);
            // Update price when quantity changes
            if (selectedSizeData) {
                updatePriceDisplay(baseProductPrice, selectedSizeData.priceAdd);
            }
        }
    });
    
    // Plus button
    $('#qty-plus').on('click', function() {
        if (currentQuantity < maxStock) {
            currentQuantity++;
            $('#qty-input').val(currentQuantity);
            // Update price when quantity changes
            if (selectedSizeData) {
                updatePriceDisplay(baseProductPrice, selectedSizeData.priceAdd);
            }
        } else {
            alert(`Chỉ còn ${maxStock} sản phẩm trong kho!`);
        }
    });
}

/**
 * Setup add to cart button handler
 */
function setupAddToCartButton() {
    $('#add-to-cart-btn').on('click', function() {
        if (!selectedSizeData) {
            alert('Vui lòng chọn size trước khi thêm vào giỏ hàng!');
            return;
        }
        
        if (currentQuantity <= 0) {
            alert('Số lượng phải lớn hơn 0!');
            return;
        }
        
        // Calculate price per unit (base price + size fee)
        const pricePerUnit = baseProductPrice + selectedSizeData.priceAdd;
        
        // Prepare data
        const cartData = {
            productDetailId: selectedSizeData.sizeId,
            quantity: currentQuantity,
            pricePerUnit: pricePerUnit
        };
        
        console.log('Adding to cart:', cartData);
        
        // Disable button and show loading
        const $btn = $(this);
        $btn.prop('disabled', true);
        $btn.html('<i class="fa fa-spinner fa-spin"></i> Đang thêm...');
        
        // Call API
        $.ajax({
            url: '/api/cart/add',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(cartData),
            success: function(response) {
                console.log('Add to cart success:', response);
                if (response.success) {
                    // Trigger flying cart animation
                    flyToCart();
                    
                    // Show success toast
                    showSuccessToast(response.message || 'Đã thêm vào giỏ hàng!');
                    
                    // Reset button
                    $btn.prop('disabled', false);
                    $btn.html('<i class="fa fa-shopping-cart"></i><span>Thêm Vào Giỏ Hàng</span>');
                    
                    // Update cart badge count
                    if (typeof window.refreshCartCount === 'function') {
                        console.log('🔄 Refreshing cart badge...');
                        window.refreshCartCount();
                    } else {
                        console.warn('⚠️ window.refreshCartCount not found');
                    }
                } else {
                    alert('⚠️ ' + response.message);
                    $btn.prop('disabled', false);
                    $btn.html('<i class="fa fa-shopping-cart"></i><span>Thêm vào giỏ hàng</span>');
                }
            },
            error: function(xhr, status, error) {
                console.error('Add to cart error:', error);
                console.error('XHR:', xhr);
                
                if (xhr.status === 401) {
                    // alert('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!');
                    window.location.href = '/login';
                } else {
                    alert('Có lỗi xảy ra khi thêm vào giỏ hàng. Vui lòng thử lại!');
                }
                
                $btn.prop('disabled', false);
                $btn.html('<i class="fa fa-shopping-cart"></i><span>Thêm Vào Giỏ Hàng</span>');
            }
        });
    });
}

/**
 * Format giá theo VND
 */
function formatVND(price) {
    return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', 
        currency: 'VND' 
    }).format(price);
}

/**
 * Format sold quantity (Shopee style)
 * Examples: 12 -> 12, 1234 -> 1.2k, 45678 -> 45.6k, 1500000 -> 1.5tr
 */
function formatSoldQuantity(quantity) {
    if (quantity < 1000) {
        return quantity.toString();
    } else if (quantity < 1000000) {
        // Format as "k" (thousands)
        const k = (quantity / 1000).toFixed(1);
        return k.endsWith('.0') ? Math.floor(quantity / 1000) + 'k' : k + 'k';
    } else {
        // Format as "tr" (triệu - millions)
        const m = (quantity / 1000000).toFixed(1);
        return m.endsWith('.0') ? Math.floor(quantity / 1000000) + 'tr' : m + 'tr';
    }
}

/**
 * Render Flash Sale UI
 */
function renderFlashSale(flashSale, originalPrice) {
    console.log('=== 🔥 renderFlashSale() CALLED ===');
    console.log('  flashSale data:', flashSale);
    console.log('  originalPrice:', originalPrice);
    
    // Show flash sale banner
    console.log('  ✅ Showing flash sale banner...');
    $('#flash-sale-banner').show();
    startCountdown(flashSale.endTime);
    
    // Update price section
    const flashPrice = flashSale.flashSalePrice;
    const discountPercent = flashSale.discountPercent;
    
    console.log('  ✅ Updating prices...');
    console.log('    Original:', originalPrice, '→', formatVND(originalPrice));
    console.log('    Flash:', flashPrice, '→', formatVND(flashPrice));
    console.log('    Discount:', discountPercent + '%');
    
    $('#original-price').text(formatVND(originalPrice)).show();
    $('#product-price').text(formatVND(flashPrice));
    $('#discount-badge').text(`-${Math.round(discountPercent)}% GIẢM`).show();
    
    // Add flash style to price section
    console.log('  ✅ Adding flash style to price section');
    $('#price-section').addClass('shopee-price-flash');
    
    // Note: Stock progress bar removed for cleaner Shopee-style UI
    console.log('  ℹ️ Stock progress bar hidden (Shopee style)');
    
    console.log('=== 🔥 renderFlashSale() COMPLETE ===');
}

/**
 * Update stock progress bar
 */
function updateStockProgress(flashSale) {
    const sold = flashSale.sold || 0;
    const remaining = flashSale.remaining || flashSale.stock;
    const total = sold + remaining;
    const soldPercentage = total > 0 ? Math.round((sold / total) * 100) : 0;
    
    $('#stock-progress-fill').css('width', soldPercentage + '%');
    $('#sold-count').text(sold);
    $('#remaining-count').text(remaining);
}

/**
 * Countdown timer for flash sale
 */
function startCountdown(endTime) {
    console.log('⏰ Starting countdown with endTime:', endTime);
    
    function updateCountdown() {
        const now = new Date().getTime();
        const end = new Date(endTime).getTime();
        const distance = end - now;
        
        console.log('  Countdown update - Distance:', distance, 'ms');
        
        if (distance < 0) {
            console.log('  ❌ Flash Sale ended');
            $('.countdown-digit').eq(0).text('00');
            $('.countdown-digit').eq(1).text('00');
            $('.countdown-digit').eq(2).text('00');
            $('#flash-sale-banner').css('opacity', '0.7');
            return;
        }
        
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        
        // Update individual digit boxes (Shopee style)
        $('.countdown-digit').eq(0).text(String(hours).padStart(2, '0'));
        $('.countdown-digit').eq(1).text(String(minutes).padStart(2, '0'));
        $('.countdown-digit').eq(2).text(String(seconds).padStart(2, '0'));
        
        console.log('  ✅ Countdown:', `${hours}:${minutes}:${seconds}`);
    }
    
    updateCountdown();
    const interval = setInterval(updateCountdown, 1000);
    console.log('  ✅ Countdown interval started');
}

/**
 * Flying cart animation - Product image flies to cart icon
 */
function flyToCart() {
    // Get product image
    const $productImg = $('#product-image');
    if (!$productImg.length) return;
    
    // Get cart icon in header - target the specific cart-menu icon
    const $cartIcon = $('.cart-menu img[src*="icon-cart"]');
    if (!$cartIcon.length) {
        console.warn('Cart icon not found in header');
        return;
    }
    
    console.log('🎯 Cart icon found:', $cartIcon);
    
    // Get positions
    const imgOffset = $productImg.offset();
    const cartOffset = $cartIcon.offset();
    
    // Create flying image clone
    const $flyingImg = $productImg.clone()
        .addClass('flying-image')
        .css({
            position: 'fixed',
            top: imgOffset.top,
            left: imgOffset.left,
            width: $productImg.width(),
            height: $productImg.height(),
            opacity: 1,
            zIndex: 9999
        })
        .appendTo('body');
    
    // Animate to cart
    setTimeout(() => {
        $flyingImg.css({
            top: cartOffset.top,
            left: cartOffset.left,
            width: 40,
            height: 40,
            opacity: 0
        });
    }, 50);
    
    // Create particles when image reaches cart
    setTimeout(() => {
        createParticles(cartOffset.left, cartOffset.top);
    }, 700);
    
    // Create ripple effect at cart
    setTimeout(() => {
        createRipple($cartIcon);
    }, 750);
    
    // Shake cart icon and its container
    const $cartContainer = $('.cart-menu');
    $cartIcon.addClass('cart-shake');
    $cartContainer.addClass('cart-shake');
    setTimeout(() => {
        $cartIcon.removeClass('cart-shake');
        $cartContainer.removeClass('cart-shake');
    }, 500);
    
    // Remove flying image after animation
    setTimeout(() => {
        $flyingImg.remove();
    }, 1000);
}

/**
 * Create particle explosion effect
 */
function createParticles(x, y) {
    const colors = ['#ee4d2d', '#ff6b45', '#ff8c69', '#10b981', '#fbbf24'];
    const particleCount = 12;
    
    for (let i = 0; i < particleCount; i++) {
        const angle = (Math.PI * 2 * i) / particleCount;
        const velocity = 50 + Math.random() * 50;
        const tx = Math.cos(angle) * velocity;
        const ty = Math.sin(angle) * velocity;
        
        const $particle = $('<div class="particle"></div>')
            .css({
                left: x,
                top: y,
                background: colors[Math.floor(Math.random() * colors.length)],
                '--tx': tx + 'px',
                '--ty': ty + 'px'
            })
            .appendTo('body');
        
        setTimeout(() => {
            $particle.remove();
        }, 800);
    }
}

/**
 * Create ripple effect at target
 */
function createRipple($target) {
    const offset = $target.offset();
    const $ripple = $('<div class="cart-ripple"></div>')
        .css({
            left: offset.left - 20,
            top: offset.top - 20
        })
        .appendTo('body');
    
    setTimeout(() => {
        $ripple.remove();
    }, 600);
}

/**
 * Show success toast notification
 */
function showSuccessToast(message) {
    // Remove existing toast if any
    $('.cart-success-toast').remove();
    
    // Create enhanced toast with quantity badge
    const $toast = $(`
        <div class="cart-success-toast">
            <i class="fa fa-check-circle"></i>
            <div style="flex: 1;">
                <div style="font-size: 15px; font-weight: 600;">${message}</div>
                <div style="font-size: 12px; opacity: 0.9; margin-top: 2px;">
                    Số lượng: <strong>${currentQuantity}</strong>
                </div>
            </div>
            <div style="background: rgba(255,255,255,0.2); padding: 6px 10px; border-radius: 6px; font-size: 13px; font-weight: 700;">
                +${currentQuantity}
            </div>
        </div>
    `).appendTo('body');
    
    // Show with animation
    setTimeout(() => {
        $toast.addClass('show');
    }, 100);
    
    // Add progress bar
    const $progress = $('<div style="position: absolute; bottom: 0; left: 0; height: 3px; background: rgba(255,255,255,0.5); width: 100%; transform-origin: left; animation: progressBar 3s linear;"></div>')
        .appendTo($toast);
    
    // Hide and remove after 3 seconds
    setTimeout(() => {
        $toast.removeClass('show');
        setTimeout(() => {
            $toast.remove();
        }, 400);
    }, 3000);
}

// CSS for progress bar animation (add to inline style)
if (!$('#toast-progress-style').length) {
    $('<style id="toast-progress-style">@keyframes progressBar { from { transform: scaleX(1); } to { transform: scaleX(0); } }</style>')
        .appendTo('head');
}
