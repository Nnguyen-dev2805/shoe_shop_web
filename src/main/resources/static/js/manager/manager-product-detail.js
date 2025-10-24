/**
 * Manager Product Detail JavaScript
 * Deeg Manager V6.0 - Vietnamese Locale
 */

$(document).ready(function() {
    loadProductDetail();
});

/**
 * Extract product ID from URL
 */
function getProductIdFromUrl() {
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1];
}

/**
 * Load product detail from API
 */
function loadProductDetail() {
    const productId = getProductIdFromUrl();
    
    if (!productId || isNaN(productId)) {
        showError('ID sản phẩm không hợp lệ');
        return;
    }
    
    // Show loading state
    $('#loading-container').show();
    $('#product-detail-container').hide();
    $('#error-container').hide();
    
    $.ajax({
        url: `/api/manager/products/${productId}`,
        method: 'GET',
        success: function(data) {
            displayProductDetail(data);
            $('#loading-container').hide();
            $('#product-detail-container').fadeIn();
        },
        error: function(xhr, status, error) {
            console.error('Lỗi tải sản phẩm:', error);
            $('#loading-container').hide();
            $('#error-container').fadeIn();
        }
    });
}

/**
 * Display product detail information
 */
function displayProductDetail(product) {
    // Basic Information
    $('#product-title').text(product.title || 'N/A');
    $('#product-category').text(product.categoryName || 'N/A');
    $('#product-brand').text(product.brandName || 'N/A');
    $('#product-price').text(formatCurrency(product.price || 0));
    $('#product-description').text(product.description || 'Không có mô tả');
    
    // Product Image
    const imageSrc = product.image || '/assets/images/product/default.jpg';
    $('#product-image').attr('src', imageSrc)
        .attr('alt', product.title)
        .on('error', function() {
            $(this).attr('src', '/assets/images/product/default.jpg');
        });
    
    // Rating
    const avgRating = product.avgRating || 0;
    const totalReviews = product.totalReviews || 0;
    $('#product-rating').html(generateStarRating(avgRating));
    $('#total-reviews').text(totalReviews);
    
    // Calculate total stock
    let totalStock = 0;
    if (product.sizeOptions && product.sizeOptions.length > 0) {
        totalStock = product.sizeOptions.reduce((sum, size) => sum + (size.stock || 0), 0);
    }
    
    // Stock Status
    displayStockStatus(totalStock);
    
    // Size Table
    displaySizeTable(product.sizeOptions || []);
    
    // Statistics
    $('#stat-total-stock').text(totalStock);
    $('#stat-sold-quantity').text(product.soldQuantity || 0);
    $('#stat-avg-rating').text((avgRating || 0).toFixed(1));
    
    // Flash Sale Information (if available)
    if (product.flashSale && product.flashSale.active) {
        displayFlashSale(product.flashSale);
    } else {
        $('#flash-sale-card').hide();
    }
}

/**
 * Display stock status badge
 */
function displayStockStatus(totalStock) {
    const statusElement = $('#stock-status');
    
    if (totalStock > 50) {
        statusElement
            .removeClass('alert-warning alert-danger')
            .addClass('alert-success')
            .html(`
                <iconify-icon icon="solar:check-circle-bold-duotone" class="me-2"></iconify-icon>
                <strong>Còn hàng</strong> - ${totalStock} sản phẩm có sẵn
            `);
    } else if (totalStock > 0) {
        statusElement
            .removeClass('alert-success alert-danger')
            .addClass('alert-warning')
            .html(`
                <iconify-icon icon="solar:danger-triangle-bold-duotone" class="me-2"></iconify-icon>
                <strong>Sắp hết hàng</strong> - Chỉ còn ${totalStock} sản phẩm
            `);
    } else {
        statusElement
            .removeClass('alert-success alert-warning')
            .addClass('alert-danger')
            .html(`
                <iconify-icon icon="solar:close-circle-bold-duotone" class="me-2"></iconify-icon>
                <strong>Hết hàng</strong> - Cần nhập thêm
            `);
    }
}

/**
 * Display size table
 */
function displaySizeTable(sizeOptions) {
    const tbody = $('#size-table-body');
    tbody.empty();
    
    if (!sizeOptions || sizeOptions.length === 0) {
        tbody.html(`
            <tr>
                <td colspan="5" class="text-center text-muted py-4">
                    <iconify-icon icon="solar:inbox-out-bold-duotone" class="fs-2 mb-2"></iconify-icon>
                    <div>Chưa có thông tin kích cỡ</div>
                </td>
            </tr>
        `);
        $('#total-stock').text('0');
        return;
    }
    
    // Sort by size
    sizeOptions.sort((a, b) => a.size - b.size);
    
    let totalStock = 0;
    sizeOptions.forEach((size, index) => {
        const stock = size.stock || 0;
        totalStock += stock;
        
        const statusBadge = getStockStatusBadge(stock);
        const priceAdd = size.priceAdd ? formatCurrency(size.priceAdd) : '-';
        
        const row = `
            <tr>
                <td>${index + 1}</td>
                <td>
                    <strong>Size ${size.size}</strong>
                </td>
                <td>${priceAdd}</td>
                <td>
                    <span class="badge ${stock > 20 ? 'bg-success' : (stock > 0 ? 'bg-warning' : 'bg-danger')} fs-6">
                        ${stock}
                    </span>
                </td>
                <td>${statusBadge}</td>
            </tr>
        `;
        
        tbody.append(row);
    });
    
    $('#total-stock').text(totalStock);
}

/**
 * Get stock status badge
 */
function getStockStatusBadge(stock) {
    if (stock > 20) {
        return '<span class="badge bg-success-subtle text-success">Đầy đủ</span>';
    } else if (stock > 10) {
        return '<span class="badge bg-warning-subtle text-warning">Trung bình</span>';
    } else if (stock > 0) {
        return '<span class="badge bg-danger-subtle text-danger">Sắp hết</span>';
    } else {
        return '<span class="badge bg-dark-subtle text-dark">Hết hàng</span>';
    }
}

/**
 * Display flash sale information
 */
function displayFlashSale(flashSale) {
    $('#flash-sale-card').show();
    
    $('#flash-sale-price').text(formatCurrency(flashSale.flashSalePrice || 0));
    $('#discount-percent').text((flashSale.discountPercent || 0) + '%');
    $('#flash-sale-remaining').text(flashSale.remaining || 0);
    $('#flash-sale-sold').text(flashSale.sold || 0);
    
    const soldPercentage = flashSale.soldPercentage || 0;
    $('#flash-sale-progress')
        .css('width', soldPercentage + '%')
        .attr('aria-valuenow', soldPercentage);
    $('#flash-sale-progress-text').text(soldPercentage.toFixed(1) + '% đã bán');
    
    // Change progress bar color based on percentage
    const progressBar = $('#flash-sale-progress');
    progressBar.removeClass('bg-danger bg-warning bg-success');
    
    if (soldPercentage >= 75) {
        progressBar.addClass('bg-danger');
    } else if (soldPercentage >= 50) {
        progressBar.addClass('bg-warning');
    } else {
        progressBar.addClass('bg-success');
    }
}

/**
 * Generate star rating HTML
 */
function generateStarRating(rating) {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    let html = '';
    
    for (let i = 0; i < 5; i++) {
        if (i < fullStars) {
            html += '<iconify-icon icon="solar:star-bold" class="text-warning fs-5"></iconify-icon>';
        } else if (i === fullStars && hasHalfStar) {
            html += '<iconify-icon icon="solar:star-half-bold" class="text-warning fs-5"></iconify-icon>';
        } else {
            html += '<iconify-icon icon="solar:star-outline" class="text-muted fs-5"></iconify-icon>';
        }
    }
    
    html += ` <span class="ms-1 fw-bold">${rating.toFixed(1)}</span>`;
    
    return html;
}

/**
 * Format currency to VND
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Show error message
 */
function showError(message) {
    $('#loading-container').hide();
    $('#product-detail-container').hide();
    $('#error-container').fadeIn();
    console.error(message);
}
