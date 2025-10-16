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
    $.ajax({
        url: `/api/product/${productId}`,
        type: 'GET',
        dataType: 'json',
        success: function(product) {
            console.log('✅ Dữ liệu sản phẩm:', product);
            renderProductDetails(product);
        },
        error: function(xhr, status, error) {
            console.error('❌ Lỗi khi load product:', error);
            alert('Không thể tải thông tin sản phẩm. Vui lòng thử lại sau.');
            window.location.href = '/';
        }
    });
}

/**
 * Render thông tin sản phẩm vào HTML
 */
function renderProductDetails(product) {
    // Store product data globally for Buy Now feature
    window.currentProduct = product;
    
    // Render ảnh sản phẩm
    $('#product-image').attr('src', product.image);
    $('#product-image').attr('data-zoom-image', product.image);
    
    // Render tên sản phẩm
    $('#product-title').text(product.title);
    
    // Render rating
    const avgRating = product.avgRating ? product.avgRating.toFixed(1) : '0.0';
    $('#product-rating').text(`Điểm đánh giá: ${avgRating}`);
    if (product.totalReviews && product.totalReviews > 0) {
        $('#product-reviews').text(`${product.totalReviews} đánh giá`);
    } else {
        $('#product-reviews').text("Chưa có đánh giá");
    }

    // Render giá
    const formattedPrice = formatVND(product.price);
    $('#product-price').text(formattedPrice);
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
            const sizeLabel = `Size ${size.size}`;
            
            // Add badge for out of stock
            const stockBadge = stock === 0 ? ' <span class="badge bg-danger">Hết hàng</span>' : '';
            
            const button = $(`
                <button type="button" class="size-btn ${stock === 0 ? 'disabled' : ''}" 
                        data-size-id="${size.id}" 
                        data-size="${size.size}"
                        data-price-add="${priceAdd}"
                        data-stock="${stock}"
                        ${stock === 0 ? 'disabled' : ''}>
                    ${sizeLabel}${stockBadge}
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
    $('.size-btn').removeClass('active');
    
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
    
    // Show stock info
    $('#stock-info').show();
    $('#stock-quantity').text(maxStock);
    
    // Update stock info class based on quantity
    const $stockInfo = $('#stock-info');
    $stockInfo.removeClass('low-stock out-of-stock');
    if (maxStock === 0) {
        $stockInfo.addClass('out-of-stock');
        $stockInfo.find('i').removeClass('fa-check-circle').addClass('fa-times-circle');
        $('#stock-quantity').parent().html('<strong>Hết hàng</strong>');
    } else if (maxStock <= 3) {
        $stockInfo.addClass('low-stock');
        $stockInfo.find('i').removeClass('fa-times-circle').addClass('fa-exclamation-triangle');
    } else {
        $stockInfo.find('i').removeClass('fa-times-circle fa-exclamation-triangle').addClass('fa-check-circle');
    }
    
    // Show quantity selector
    $('#quantity-selector').show();
    
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
        $addButton.html('<i class="fa fa-shopping-cart"></i> Thêm Vào Giỏ');
        
        $buyNowButton.prop('disabled', false);
        $buyNowButton.html('<i class="fa fa-bolt"></i> Mua Ngay');
    } else {
        $addButton.prop('disabled', true);
        $addButton.html('<i class="fa fa-ban"></i> Hết hàng');
        
        $buyNowButton.prop('disabled', true);
        $buyNowButton.html('<i class="fa fa-ban"></i> Hết hàng');
    }
}

/**
 * Cập nhật hiển thị giá
 */
function updatePriceDisplay(basePrice, sizeFee) {
    const pricePerUnit = basePrice + sizeFee; // Giá 1 đôi giày
    const totalPrice = pricePerUnit * currentQuantity; // Tổng tiền = giá 1 đôi * số lượng
    
    // Update main product price (tổng tiền)
    $('#product-price').text(formatVND(totalPrice));
    
    // Update price breakdown
    $('#base-price-display').text(formatVND(basePrice)); // Giá cơ bản cho 1 sản phẩm (không đổi)
    $('#size-fee-display').text(formatVND(sizeFee)); // Phụ phí size cho 1 sản phẩm (không đổi)
    $('#final-price').text(formatVND(totalPrice)); // Tổng cộng tăng theo số lượng
    
    // Show price breakdown
    $('#price-breakdown').show();
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
                    $btn.html('<i class="fa fa-shopping-cart"></i> Thêm vào giỏ hàng');
                    
                    // Optional: Redirect to cart page
                    // window.location.href = '/user/cart';
                } else {
                    alert(' ' + response.message);
                    $btn.prop('disabled', false);
                    $btn.html('<i class="fa fa-shopping-cart"></i> Thêm vào giỏ hàng');
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
                $btn.html('<i class="fa fa-shopping-cart"></i> Thêm vào giỏ hàng');
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
