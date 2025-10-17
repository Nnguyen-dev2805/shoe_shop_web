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
}

/**
 * Render size buttons (thay vì dropdown)
 */
function renderSizeButtons(sizeOptions, basePrice) {
    const $container = $('#size-buttons');
    $container.empty();
    
    if (sizeOptions && sizeOptions.length > 0) {
        sizeOptions.forEach(function(size) {
            const priceAdd = size.priceAdd || 0;
            const stock = size.stock || 0; // Get stock from API
            const sizeLabel = size.size; // Just show size number
            
            const button = $(`
                <button type="button" class="shopee-size-btn ${stock === 0 ? 'disabled' : ''}" 
                        data-size-id="${size.id}" 
                        data-size="${size.size}"
                        data-price-add="${priceAdd}"
                        data-stock="${stock}"
                        ${stock === 0 ? 'disabled' : ''}>
                    ${sizeLabel}
                </button>
            `);
            
            // Click handler cho size button (only if in stock)
            if (stock > 0) {
                button.on('click', function() {
                    selectSize($(this), basePrice);
                });
            }
            
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
    
    // Enable add to cart button
    const $addButton = $('#add-to-cart-btn');
    const $buyNowButton = $('#buy-now-btn');
    
    if (maxStock > 0) {
        $addButton.prop('disabled', false);
        $addButton.html('<i class="fa fa-shopping-cart"></i><span>Thêm Vào Giỏ Hàng</span>');
        
        $buyNowButton.prop('disabled', false);
        $buyNowButton.html('<span>Mua Ngay</span>');
    } else {
        $addButton.prop('disabled', true);
        $addButton.html('<i class="fa fa-ban"></i><span>Hết Hàng</span>');
        
        $buyNowButton.prop('disabled', true);
        $buyNowButton.html('<span>Hết Hàng</span>');
    }
}

/**
 * Cập nhật hiển thị giá
 */
function updatePriceDisplay(basePrice, sizeFee) {
    const pricePerUnit = basePrice + sizeFee; // Giá 1 đôi giày (base + size fee)
    const totalPrice = pricePerUnit * currentQuantity; // Tổng tiền = giá 1 đôi * số lượng
    
    // Main product price KHÔNG ĐỔI (giữ giá gốc)
    // $('#product-price').text(formatVND(totalPrice)); // REMOVED
    
    // Update price breakdown section
    $('#base-price-display').text(formatVND(basePrice)); // Giá cơ bản
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
                    // Show success message
                    alert(' ' + response.message);
                    
                    // Reset button
                    $btn.prop('disabled', false);
                    $btn.html('<i class="fa fa-shopping-cart"></i><span>Thêm Vào Giỏ Hàng</span>');
                    
                    // Optional: Redirect to cart page
                    // window.location.href = '/user/cart';
                } else {
                    alert(' ' + response.message);
                    $btn.prop('disabled', false);
                    $btn.html('<i class="fa fa-shopping-cart"></i><span>Thêm vào giỏ hàng</span>');
                }
            },
            error: function(xhr, status, error) {
                console.error('Add to cart error:', error);
                console.error('XHR:', xhr);
                
                if (xhr.status === 401) {
                    alert('Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!');
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
 * Render Flash Sale UI
 */
function renderFlashSale(flashSale, originalPrice) {
    console.log('=== 🔥 renderFlashSale() CALLED ===');
    console.log('  flashSale data:', flashSale);
    console.log('  originalPrice:', originalPrice);
    
    // Show flash sale timer
    console.log('  ✅ Showing timer...');
    $('#flash-sale-timer').show();
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
    
    // Show stock progress
    if (flashSale.stock) {
        console.log('  ✅ Showing stock progress...');
        $('#flash-sale-stock').show();
        updateStockProgress(flashSale);
    } else {
        console.log('  ⚠️ No stock data - hiding progress bar');
    }
    
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
    const countdownElement = $('#flash-sale-countdown');
    
    function updateCountdown() {
        const now = new Date().getTime();
        const end = new Date(endTime).getTime();
        const distance = end - now;
        
        console.log('  Countdown update - Distance:', distance, 'ms');
        
        if (distance < 0) {
            console.log('  ❌ Flash Sale ended');
            countdownElement.text('ĐÃ KẾT THÚC');
            $('#flash-sale-timer').css('background', '#666');
            return;
        }
        
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        
        const timeText = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
        console.log('  ✅ Countdown:', timeText);
        countdownElement.text(timeText);
    }
    
    updateCountdown();
    const interval = setInterval(updateCountdown, 1000);
    console.log('  ✅ Countdown interval started');
}
