/**
 * Wishlist Handler JavaScript
 * Quản lý tất cả chức năng wishlist với RESTful API + Ajax + jQuery
 * Location: /static/js/user/wishlist-handler.js
 * 
 * Features:
 * - Toggle wishlist từ single product page
 * - Update icon state (outline/filled)
 * - Show toast notifications
 * - Update wishlist counter
 */

console.log('=== Wishlist Handler JS Loaded ===');

// ==================== GLOBAL VARIABLES ====================
let wishlistCount = 0;

// ==================== INITIALIZATION ====================
$(document).ready(function() {
    console.log('Wishlist Handler: Document ready');
    
    // Load wishlist count khi page load
    loadWishlistCount();
    
    // ℹ️ NOTE: checkProductInWishlist() will be called from product-details.js
    // after product data is loaded and data-product-id is set
});

// ==================== MAIN FUNCTIONS ====================

/**
 * Toggle wishlist - Thêm hoặc xóa product
 * @param {number} productId - ID của product
 * @param {HTMLElement} buttonElement - Button element được click
 */
function toggleWishlist(productId, buttonElement) {
    console.log('=== Toggle Wishlist ===');
    console.log('Product ID:', productId);
    console.log('Button Element:', buttonElement);
    
    // Validate input
    if (!productId || isNaN(productId)) {
        console.error('Invalid product ID:', productId);
        showToast('Lỗi: ID sản phẩm không hợp lệ', 'error');
        return;
    }
    
    // Disable button để tránh double click
    $(buttonElement).prop('disabled', true);
    
    // Show loading state
    const $icon = $(buttonElement).find('i');
    const $shopeeIcon = $('#wishlist-icon');
    const originalClass = $icon.attr('class');
    const originalShopeeClass = $shopeeIcon.attr('class');
    
    $icon.attr('class', 'fa fa-spinner fa-spin');
    $shopeeIcon.attr('class', 'fa fa-spinner fa-spin');
    
    console.log('Sending AJAX request to toggle wishlist...');
    
    // Gọi API toggle
    $.ajax({
        url: '/api/wishlist/toggle',
        type: 'POST',
        data: { productId: productId },
        dataType: 'json',
        success: function(response) {
            console.log('Toggle wishlist response:', response);
            
            if (response.success) {
                // Update icon state
                updateWishlistIcon(buttonElement, response.isInWishlist);
                
                // Update product likes counter
                if (response.totalLikes !== undefined) {
                    updateProductLikesCounter(response.totalLikes);
                }
                
                // ❌ REMOVED: Toast notification
                // const message = response.isInWishlist 
                //     ? '❤️ Đã thêm vào danh sách yêu thích!' 
                //     : '💔 Đã xóa khỏi danh sách yêu thích';
                // showToast(message, 'success');
                
                console.log('Wishlist toggled successfully. Is in wishlist:', response.isInWishlist);
                console.log('Total product likes:', response.totalLikes);
            } else {
                // Show error message
                showToast(response.message || 'Có lỗi xảy ra', 'error');
                console.error('Toggle wishlist failed:', response.message);
                
                // Restore original icons
                $icon.attr('class', originalClass);
                $shopeeIcon.attr('class', originalShopeeClass);
            }
        },
        error: function(xhr, status, error) {
            console.error('Toggle wishlist AJAX error:', {
                status: xhr.status,
                statusText: xhr.statusText,
                error: error,
                responseText: xhr.responseText
            });
            
            // Handle specific error cases
            if (xhr.status === 401) {
                showToast('Bạn cần đăng nhập để sử dụng chức năng này', 'warning');
                
                // Optional: Redirect to login after delay
                setTimeout(function() {
                    window.location.href = '/login';
                }, 2000);
            } else {
                showToast('Có lỗi xảy ra khi cập nhật wishlist', 'error');
            }
            
            // Restore original icons
            $icon.attr('class', originalClass);
            $shopeeIcon.attr('class', originalShopeeClass);
        },
        complete: function() {
            // Re-enable button
            $(buttonElement).prop('disabled', false);
            console.log('Toggle wishlist request completed');
        }
    });
}

/**
 * Kiểm tra product có trong wishlist không
 * @param {number} productId - ID của product
 */
function checkProductInWishlist(productId) {
    console.log('Checking if product', productId, 'is in wishlist...');
    
    $.ajax({
        url: '/api/wishlist/check/' + productId,
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            console.log('Check wishlist response:', response);
            
            if (response.success) {
                // Update total likes counter first (always show)
                if (response.totalLikes !== undefined) {
                    updateProductLikesCounter(response.totalLikes);
                }
                
                // Update icon based on wishlist status
                const $shopeeIcon = $('#wishlist-icon');
                if (response.isInWishlist) {
                    // User đã thích -> fill icon
                    $shopeeIcon.removeClass('fa-heart-o').addClass('fa-heart');
                    console.log('Product is in wishlist - icon updated to filled');
                } else {
                    // User chưa thích -> outline icon
                    $shopeeIcon.removeClass('fa-heart').addClass('fa-heart-o');
                    console.log('Product not in wishlist - icon updated to outline');
                }
            }
        },
        error: function(xhr, status, error) {
            console.error('Check wishlist error:', error);
        }
    });
}

/**
 * Load wishlist count
 */
function loadWishlistCount() {
    console.log('Loading wishlist count...');
    
    $.ajax({
        url: '/api/wishlist/count',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            console.log('Wishlist count response:', response);
            
            if (response.success) {
                updateWishlistCounter(response.count);
            }
        },
        error: function(xhr, status, error) {
            console.error('Load wishlist count error:', error);
        }
    });
}

// ==================== UI UPDATE FUNCTIONS ====================

/**
 * Update wishlist icon state
 * @param {HTMLElement} buttonElement - Button element
 * @param {boolean} isInWishlist - Có trong wishlist không
 */
function updateWishlistIcon(buttonElement, isInWishlist) {
    console.log('Updating wishlist icon. Is in wishlist:', isInWishlist);
    
    const $icon = $(buttonElement).find('i');
    
    // Update Shopee-style wishlist section icon (if exists)
    const $shopeeIcon = $('#wishlist-icon');
    
    // ✅ Remove spinner classes first
    $icon.removeClass('fa-spinner fa-spin');
    $shopeeIcon.removeClass('fa-spinner fa-spin');
    
    if (isInWishlist) {
        // Filled heart (trong wishlist)
        $icon.removeClass('fa-heart-o').addClass('fa-heart');
        $(buttonElement).attr('title', 'Xóa khỏi yêu thích');
        
        // Update Shopee icon
        $shopeeIcon.removeClass('fa-heart-o').addClass('fa-heart');
        
        console.log('Icon updated to filled heart');
    } else {
        // Outline heart (không trong wishlist)
        $icon.removeClass('fa-heart').addClass('fa-heart-o');
        $(buttonElement).attr('title', 'Thêm vào yêu thích');
        
        // Update Shopee icon
        $shopeeIcon.removeClass('fa-heart').addClass('fa-heart-o');
        
        console.log('Icon updated to outline heart');
    }
}

/**
 * Update wishlist counter trong header
 * @param {number} count - Số lượng items
 */
function updateWishlistCounter(count) {
    console.log('Updating wishlist counter:', count);
    
    wishlistCount = count || 0;
    
    // Update counter badge (nếu có trong header)
    const $counterBadge = $('.wishlist-counter, .wishlist-count, [data-wishlist-count]');
    if ($counterBadge.length > 0) {
        $counterBadge.text(wishlistCount);
        
        if (wishlistCount > 0) {
            $counterBadge.show();
        } else {
            $counterBadge.hide();
        }
        
        console.log('Counter badge updated');
    }
}

/**
 * Update product total likes counter (số người thích sản phẩm)
 * @param {number} totalLikes - Tổng số người thích
 */
function updateProductLikesCounter(totalLikes) {
    console.log('Updating product likes counter:', totalLikes);
    
    const $shopeeCount = $('#wishlist-count');
    if ($shopeeCount.length > 0) {
        $shopeeCount.text(`(${totalLikes || 0})`);
        console.log('Product likes count updated:', totalLikes);
    }
}

/**
 * Show toast notification
 * @param {string} message - Thông báo
 * @param {string} type - Loại: success, error, warning, info
 */
function showToast(message, type = 'info') {
    console.log('Showing toast:', type, '-', message);
    
    // Remove existing toasts
    $('.wishlist-toast').remove();
    
    // Icon theo type
    let icon = '';
    let bgColor = '';
    
    switch(type) {
        case 'success':
            icon = '<i class="fa fa-check-circle"></i>';
            bgColor = '#28a745';
            break;
        case 'error':
            icon = '<i class="fa fa-times-circle"></i>';
            bgColor = '#dc3545';
            break;
        case 'warning':
            icon = '<i class="fa fa-exclamation-triangle"></i>';
            bgColor = '#ffc107';
            break;
        default:
            icon = '<i class="fa fa-info-circle"></i>';
            bgColor = '#17a2b8';
    }
    
    // Create toast HTML
    const toastHtml = `
        <div class="wishlist-toast" style="
            position: fixed;
            top: 80px;
            right: 20px;
            background: ${bgColor};
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            z-index: 9999;
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 15px;
            font-weight: 500;
            animation: slideInRight 0.3s ease-out;
        ">
            ${icon}
            <span>${message}</span>
        </div>
    `;
    
    // Add CSS animation if not exists
    if ($('#wishlist-toast-style').length === 0) {
        $('head').append(`
            <style id="wishlist-toast-style">
                @keyframes slideInRight {
                    from {
                        transform: translateX(400px);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
                @keyframes slideOutRight {
                    from {
                        transform: translateX(0);
                        opacity: 1;
                    }
                    to {
                        transform: translateX(400px);
                        opacity: 0;
                    }
                }
            </style>
        `);
    }
    
    // Append toast
    $('body').append(toastHtml);
    
    // Auto hide after 3 seconds
    setTimeout(function() {
        $('.wishlist-toast').css('animation', 'slideOutRight 0.3s ease-out');
        setTimeout(function() {
            $('.wishlist-toast').remove();
        }, 300);
    }, 3000);
}

// ==================== HELPER FUNCTIONS ====================

/**
 * Lấy product ID từ page hiện tại
 * @returns {number|null} Product ID hoặc null
 */
function getProductIdFromPage() {
    // Try to get from data attribute
    const $wishlistButton = $('.add-to-wishlist');
    if ($wishlistButton.length > 0) {
        const productId = $wishlistButton.attr('data-product-id');
        if (productId && !isNaN(productId)) {
            return parseInt(productId);
        }
    }
    
    // Try to get from URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    if (productId && !isNaN(productId)) {
        return parseInt(productId);
    }
    
    console.log('No product ID found on page');
    return null;
}

/**
 * Format currency VND
 * @param {number} amount - Số tiền
 * @returns {string} Formatted currency
 */
function formatCurrency(amount) {
    if (!amount || isNaN(amount)) return '0 đ';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

console.log('=== Wishlist Handler JS Fully Loaded ===');
