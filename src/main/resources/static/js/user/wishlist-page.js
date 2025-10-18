/**
 * Wishlist Page JavaScript
 * Quản lý giao diện trang wishlist với RESTful API + Ajax + jQuery
 * Location: /static/js/user/wishlist-page.js
 * 
 * Features:
 * - Load wishlist items via Ajax
 * - Display product cards
 * - Remove items
 * - Add to cart
 * - Empty state
 */

console.log('=== Wishlist Page JS Loaded ===');

// ==================== INITIALIZATION ====================
$(document).ready(function() {
    console.log('Wishlist Page: Document ready');
    
    // Load wishlist khi page load
    loadWishlistItems();
});

// ==================== MAIN FUNCTIONS ====================

/**
 * Load tất cả wishlist items
 */
function loadWishlistItems() {
    console.log('=== Loading Wishlist Items ===');
    
    // Show loading state
    showLoadingState();
    
    $.ajax({
        url: '/api/wishlist',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            console.log('Wishlist items response:', response);
            
            if (response.success) {
                if (response.wishlist && response.wishlist.length > 0) {
                    displayWishlistItems(response.wishlist);
                    updateWishlistCount(response.count);
                    console.log('Displayed', response.wishlist.length, 'wishlist items');
                } else {
                    showEmptyState();
                    console.log('Wishlist is empty');
                }
            } else {
                showErrorState(response.message || 'Không thể tải wishlist');
                console.error('Failed to load wishlist:', response.message);
            }
        },
        error: function(xhr, status, error) {
            console.error('Load wishlist error:', {
                status: xhr.status,
                error: error,
                responseText: xhr.responseText
            });
            
            if (xhr.status === 401) {
                showLoginRequired();
            } else {
                showErrorState('Có lỗi xảy ra khi tải wishlist');
            }
        }
    });
}

/**
 * Hiển thị danh sách wishlist items
 * @param {Array} items - Danh sách wishlist items
 */
function displayWishlistItems(items) {
    console.log('Displaying wishlist items:', items.length);
    
    const $container = $('#wishlist-items-container');
    $container.empty();
    
    let html = '<div class="wishlist-grid">';
    
    items.forEach(function(item, index) {
        console.log('Rendering item', index + 1, ':', item.title);
        html += createWishlistCard(item);
    });
    
    html += '</div>';
    
    $container.html(html);
    console.log('Wishlist items rendered successfully');
}

/**
 * Tạo HTML cho wishlist card - Shopee Style
 * @param {Object} item - Wishlist item data
 * @returns {string} HTML string
 */
function createWishlistCard(item) {
    const imageUrl = item.image || '/img/product/default.png';
    const title = item.title || 'Sản phẩm';
    const price = formatCurrency(item.price);
    const brandName = item.brandName || 'Chưa rõ';
    const categoryName = item.categoryName || 'Chưa rõ';
    const rating = item.averageRating || 0;
    const reviews = item.totalReviews || 0;
    const isDeleted = item.isDelete || false;
    
    // Rating stars HTML
    let starsHtml = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= Math.floor(rating)) {
            starsHtml += '<i class="fa fa-star"></i>';
        } else if (i === Math.ceil(rating) && rating % 1 !== 0) {
            starsHtml += '<i class="fa fa-star-half-o"></i>';
        } else {
            starsHtml += '<i class="fa fa-star-o"></i>';
        }
    }
    
    // Disabled state for deleted products
    const disabledClass = isDeleted ? 'disabled' : '';
    const disabledStyle = isDeleted ? 'opacity: 0.6; pointer-events: none;' : '';
    
    return `
        <div class="wishlist-product-card ${disabledClass}" style="${disabledStyle}">
            <!-- Product Image -->
            <div class="wishlist-product-image">
                <a href="/product/details/${item.productId}">
                    <img src="${imageUrl}" alt="${title}" loading="lazy">
                </a>
                
                <!-- Remove Button -->
                <button class="wishlist-remove-btn" 
                        onclick="removeFromWishlist(${item.productId}, this)"
                        title="Xóa khỏi wishlist">
                    <i class="fa fa-trash"></i>
                </button>
            </div>
            
            <!-- Product Info -->
            <div class="wishlist-product-info">
                <!-- Badges -->
                <div class="wishlist-product-badges">
                    <span class="wishlist-badge wishlist-badge-brand">${brandName}</span>
                    <span class="wishlist-badge wishlist-badge-category">${categoryName}</span>
                </div>
                
                <!-- Title -->
                <a href="/product/details/${item.productId}" 
                   class="wishlist-product-title" 
                   style="text-decoration: none;">
                    ${title}
                </a>
                
                <!-- Rating -->
                <div class="wishlist-product-rating">
                    <div class="wishlist-stars">${starsHtml}</div>
                    <span class="wishlist-rating-text">${reviews > 0 ? `(${reviews})` : 'Chưa có đánh giá'}</span>
                </div>
                
                <!-- Price -->
                <div class="wishlist-product-price">${price}</div>
                
                ${isDeleted ? `
                    <div style="color: #f44336; font-size: 13px; font-weight: 600; margin-bottom: 12px;">
                        <i class="fa fa-ban"></i> Sản phẩm không còn bán
                    </div>
                ` : ''}
                
                <!-- Action Buttons -->
                <div class="wishlist-product-actions">
                    <a href="/product/details/${item.productId}" 
                       class="wishlist-btn-view">
                        <i class="fa fa-eye"></i> Xem
                    </a>
                    ${!isDeleted ? `
                        <button onclick="addToCartFromWishlist(${item.productId})" 
                                class="wishlist-btn-cart">
                            <i class="fa fa-shopping-cart"></i> Giỏ Hàng
                        </button>
                    ` : `
                        <button class="wishlist-btn-cart" disabled style="opacity: 0.5; cursor: not-allowed;">
                            <i class="fa fa-ban"></i> Hết Hàng
                        </button>
                    `}
                </div>
            </div>
        </div>
    `;
}

/**
 * Xóa item khỏi wishlist
 * @param {number} productId - ID của product
 * @param {HTMLElement} buttonElement - Button element
 */
function removeFromWishlist(productId, buttonElement) {
    console.log('=== Remove from wishlist - productId:', productId, '===');
    
    // Disable button
    $(buttonElement).prop('disabled', true);
    $(buttonElement).html('<i class="fa fa-spinner fa-spin"></i>');
    
    $.ajax({
        url: '/api/wishlist/remove',
        type: 'DELETE',
        data: { productId: productId },
        dataType: 'json',
        success: function(response) {
            console.log('Remove response:', response);
            
            if (response.success) {
                // Show success toast
                showToast('Đã xóa sản phẩm khỏi wishlist', 'success');
                
                // Remove card with animation
                const $card = $(buttonElement).closest('.wishlist-product-card');
                $card.fadeOut(300, function() {
                    $card.remove();
                    
                    // Check if empty
                    if ($('.wishlist-product-card').length === 0) {
                        showEmptyState();
                    }
                });
                
                // Update count
                if (response.count !== undefined) {
                    updateWishlistCount(response.count);
                }
                
                console.log('Product removed successfully');
            } else {
                showToast(response.message || 'Có lỗi xảy ra', 'error');
                $(buttonElement).prop('disabled', false);
                $(buttonElement).html('<i class="fa fa-times"></i>');
            }
        },
        error: function(xhr, status, error) {
            console.error('Remove error:', error);
            showToast('Có lỗi xảy ra khi xóa sản phẩm', 'error');
            $(buttonElement).prop('disabled', false);
            $(buttonElement).html('<i class="fa fa-times"></i>');
        }
    });
}

/**
 * Thêm sản phẩm vào giỏ hàng
 * Redirect to product detail page để chọn size
 * @param {number} productId - ID của product
 */
function addToCartFromWishlist(productId) {
    console.log('🛒 Redirecting to product page - productId:', productId);
    
    // Show loading toast
    // showToast('⏳ Đang chuyển đến trang sản phẩm...', 'info');
    
    // Redirect to product detail page để user chọn size
    setTimeout(() => {
        window.location.href = '/product/details/' + productId;
    }, 300);
}

// ==================== UI STATE FUNCTIONS ====================

/**
 * Show loading state
 */
function showLoadingState() {
    console.log('Showing loading state');
    
    const html = `
        <div class="wishlist-loading">
            <i class="fa fa-spinner fa-spin"></i>
            <p class="wishlist-loading-text">
                Đang tải danh sách yêu thích...
            </p>
        </div>
    `;
    
    $('#wishlist-items-container').html(html);
}

/**
 * Show empty state
 */
function showEmptyState() {
    console.log('Showing empty state');
    
    const html = `
        <div class="wishlist-empty-state">
            <div class="wishlist-empty-icon">
                <i class="fa fa-heart-o"></i>
            </div>
            <h3 class="wishlist-empty-title">Chưa Có Sản Phẩm Yêu Thích</h3>
            <p class="wishlist-empty-text">
                Hãy thêm sản phẩm vào wishlist để dễ dàng theo dõi và mua sắm sau này
            </p>
            <a href="/user/shop" class="wishlist-empty-btn">
                <i class="fa fa-shopping-bag"></i> Khám Phá Ngay
            </a>
        </div>
    `;
    
    $('#wishlist-items-container').html(html);
    updateWishlistCount(0);
}

/**
 * Show error state
 * @param {string} message - Error message
 */
function showErrorState(message) {
    console.log('Showing error state:', message);
    
    const html = `
        <div class="wishlist-empty-state">
            <div class="wishlist-empty-icon" style="color: #f44336;">
                <i class="fa fa-exclamation-triangle"></i>
            </div>
            <h3 class="wishlist-empty-title">Có Lỗi Xảy Ra</h3>
            <p class="wishlist-empty-text">${message}</p>
            <button onclick="loadWishlistItems()" class="wishlist-empty-btn" style="border: none;">
                <i class="fa fa-refresh"></i> Thử Lại
            </button>
        </div>
    `;
    
    $('#wishlist-items-container').html(html);
}

/**
 * Show login required message
 */
function showLoginRequired() {
    console.log('Showing login required');
    
    const html = `
        <div class="wishlist-empty-state">
            <div class="wishlist-empty-icon" style="color: #ffc107;">
                <i class="fa fa-lock"></i>
            </div>
            <h3 class="wishlist-empty-title">Yêu Cầu Đăng Nhập</h3>
            <p class="wishlist-empty-text">
                Bạn cần đăng nhập để xem và quản lý danh sách yêu thích của mình
            </p>
            <a href="/login" class="wishlist-empty-btn">
                <i class="fa fa-sign-in"></i> Đăng Nhập Ngay
            </a>
        </div>
    `;
    
    $('#wishlist-items-container').html(html);
}

/**
 * Update wishlist count in header
 * @param {number} count - Number of items
 */
function updateWishlistCount(count) {
    console.log('Updating wishlist count:', count);
    
    // Update counter badge in breadcrumb
    $('#wishlist-header-count').text(`(${count})`);
    
    // Update total count in page header
    $('#wishlist-total-count').text(count);
    
    // Update global wishlist count
    $('.wishlist-count, .wishlist-counter, [data-wishlist-count]').text(count);
}

/**
 * Show toast notification - Modern Shopee Style
 * @param {string} message - Message
 * @param {string} type - Type: success, error, warning, info
 */
function showToast(message, type = 'success') {
    console.log('🔔 Toast:', type, '-', message);
    
    // Remove existing toasts
    $('.wishlist-toast').remove();
    
    let icon = '';
    
    switch(type) {
        case 'success':
            icon = '<i class="fa fa-check-circle"></i>';
            break;
        case 'error':
            icon = '<i class="fa fa-times-circle"></i>';
            break;
        case 'warning':
            icon = '<i class="fa fa-exclamation-circle"></i>';
            break;
        default:
            icon = '<i class="fa fa-info-circle"></i>';
    }
    
    const $toast = $(`
        <div class="wishlist-toast ${type}">
            ${icon}
            <span>${message}</span>
        </div>
    `);
    
    $('body').append($toast);
    
    // Trigger show animation
    setTimeout(function() {
        $toast.addClass('show');
    }, 100);
    
    // Auto hide after 3 seconds
    setTimeout(function() {
        $toast.removeClass('show');
        setTimeout(function() {
            $toast.remove();
        }, 400);
    }, 3000);
}

// ==================== HELPER FUNCTIONS ====================

/**
 * Format currency VND
 * @param {number} amount - Amount
 * @returns {string} Formatted currency
 */
function formatCurrency(amount) {
    if (!amount || isNaN(amount)) return '0 đ';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

console.log('=== Wishlist Page JS Fully Loaded - Shopee Style ===');
