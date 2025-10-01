// Global variables
let currentQuantity = 1;
let maxStock = 10; // Hardcode stock quantity
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
            const sizeLabel = `Size ${size.size}`;
            
            const button = $(`
                <button type="button" class="size-btn" 
                        data-size-id="${size.id}" 
                        data-size="${size.size}"
                        data-price-add="${priceAdd}">
                    ${sizeLabel}
                </button>
            `);
            
            // Click handler cho size button
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
    // Remove active class from all buttons
    $('.size-btn').removeClass('active');
    
    // Add active class to selected button
    $button.addClass('active');
    
    // Get size data
    const sizeId = $button.data('size-id');
    const sizeName = $button.data('size');
    const priceAdd = parseFloat($button.data('price-add')) || 0;
    
    // Store selected size data
    selectedSizeData = {
        id: sizeId,
        name: sizeName,
        priceAdd: priceAdd
    };
    
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
    if (maxStock > 0) {
        $addButton.prop('disabled', false);
        $addButton.html('<i class="fa fa-shopping-cart"></i> Thêm vào giỏ hàng');
    } else {
        $addButton.prop('disabled', true);
        $addButton.html('<i class="fa fa-ban"></i> Hết hàng');
    }
}

/**
 * Cập nhật hiển thị giá
 */
function updatePriceDisplay(basePrice, sizeFee) {
    const totalPrice = basePrice + sizeFee;
    
    $('#base-price-display').text(formatVND(basePrice));
    $('#size-fee-display').text(formatVND(sizeFee));
    $('#final-price').text(formatVND(totalPrice));
    
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
        }
    });
    
    // Plus button
    $('#qty-plus').on('click', function() {
        if (currentQuantity < maxStock) {
            currentQuantity++;
            $('#qty-input').val(currentQuantity);
        } else {
            alert(`Chỉ còn ${maxStock} sản phẩm trong kho!`);
        }
    });
}

/**
 * Format giá theo VND
 */
function formatVND(price) {
    return new Intl.NumberFormat('vi-VN').format(price) + ' đ';
}
