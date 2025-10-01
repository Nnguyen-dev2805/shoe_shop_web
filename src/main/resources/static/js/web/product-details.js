$(document).ready(function() {
    // Lấy product ID từ URL
    const productId = getProductIdFromUrl();
    
    if (productId) {
        loadProductDetails(productId);
    } else {
        console.error('Không tìm thấy product ID trong URL');
        window.location.href = '/';
    }
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

    // Hàm format theo VND
    function formatVND(price) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    }

// Render giá
    const formattedPrice = formatVND(product.price);
    $('#product-price').text(formattedPrice);
    $('#final-price').text(formattedPrice);
    $('#final-price').attr('data-base-price', product.price);
    
    // Render mô tả
    $('#product-description').text(product.description || 'Chưa có mô tả');
    
    // Render category và brand (nếu cần hiển thị)
    $('#product-category').text(product.categoryName || 'N/A');
    $('#product-brand').text(product.brandName || 'N/A');
    
    // Render size options
    renderSizeOptions(product.sizeOptions);
    
    // Set product ID cho các elements khác
    $('.add-to-wishlist').attr('data-product-id', product.id);
    $('#viewAllReview').attr('data-product-id', product.id);
    $('input[name="productId"]').val(product.id);
    $('#product-review-title').text(`You are reviewing: ${product.title}`);
    
    // Setup event handler cho size selection
    setupSizeChangeHandler(product.price);
}

/**
 * Render danh sách size options
 */
function renderSizeOptions(sizeOptions) {
    const $select = $('#size-select');
    $select.empty();
    $select.append('<option value="" disabled selected>-- Vui lòng chọn size --</option>');
    
    if (sizeOptions && sizeOptions.length > 0) {
        sizeOptions.forEach(function(size) {
            const priceAdd = size.priceAdd ? size.priceAdd.toFixed(0) : '0';
            $select.append(
                `<option value="${size.id}" data-priceadd="${size.priceAdd}">
                    Size: ${size.size} + $${priceAdd}
                </option>`
            );
        });
    } else {
        $select.append('<option value="" disabled>Không có size nào</option>');
    }
}

/**
 * Setup event handler khi thay đổi size
 */
function setupSizeChangeHandler(basePrice) {
    $('#size-select').off('change').on('change', function() {
        const selectedOption = $(this).find('option:selected');
        const priceAdd = parseFloat(selectedOption.attr('data-priceadd')) || 0;
        const productDetailId = selectedOption.val();
        
        // Cập nhật giá cuối cùng
        const totalPrice = basePrice + priceAdd;
        $('#final-price').text(formatPrice(totalPrice));
        
        // Lưu productDetailId vào hidden input
        $('#productDetailId').val(productDetailId);
    });
}

/**
 * Format giá tiền
 */
function formatPrice(price) {
    return '$' + parseFloat(price).toFixed(2);
}
