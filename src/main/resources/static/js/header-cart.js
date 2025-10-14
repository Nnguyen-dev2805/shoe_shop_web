/**
 * Header Cart Management
 * Hiển thị số lượng items trong giỏ hàng realtime cho từng user
 * Sử dụng: RESTful API + Ajax + jQuery
 */

// Prevent multiple initializations
if (typeof window.cartBadgeInitialized === 'undefined') {
    window.cartBadgeInitialized = true;
    
    (function() {
        'use strict';
        
        // Wait for jQuery to be available
        function initCartBadge() {
            if (typeof jQuery === 'undefined') {
                console.warn('jQuery not loaded yet, retrying...');
                setTimeout(initCartBadge, 100);
                return;
            }
            
            // jQuery is available, initialize
            $(document).ready(function() {
                console.log('Cart badge initializing (once)...');
                
                // Load cart count khi trang load
                loadCartCount();
                
                // REMOVED: Auto-refresh interval to prevent infinite loop
                // Users can manually refresh by: window.refreshCartCount()
            });
        }
        
        // Start initialization
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', initCartBadge);
        } else {
            initCartBadge();
        }
    })();
}

/**
 * Load cart item count và details từ server
 */
function loadCartCount() {
    console.log('Loading cart data from API...');
    
    // Load full cart data (not just count)
    $.ajax({
        url: '/api/cart/current',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            console.log('API Response:', response);
            if (response.success && response.data) {
                const cart = response.data;
                const itemCount = cart.cartDetails ? cart.cartDetails.length : 0;
                
                // Update badge
                updateCartBadge(itemCount, true);
                
                // Render cart preview - DISABLED: User only wants to show item count
                // renderCartPreview(cart);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading cart:', error);
            console.error('Status:', status);
            
            // Nếu unauthorized, user chưa đăng nhập
            if (xhr.status === 401) {
                updateCartBadge(0, false);
                // renderCartPreview(null); // DISABLED
            } else {
                // Fallback to count API
                loadCartCountOnly();
            }
        }
    });
}

/**
 * Fallback: Load only cart count (if full cart API fails)
 */
function loadCartCountOnly() {
    $.ajax({
        url: '/api/cart/count',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                updateCartBadge(response.count, response.authenticated);
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading cart count:', error);
            updateCartBadge(0, false);
        }
    });
}

/**
 * Update cart badge hiển thị số lượng
 * @param {number} count - Số lượng items
 * @param {boolean} isAuthenticated - User đã đăng nhập chưa
 */
function updateCartBadge(count, isAuthenticated) {
    console.log('Updating badge with count:', count);
    
    // Tìm cart icon - support cả href="#" và href chứa "/cart"
    const cartIcon = $('.cart-menu a');
    console.log('Cart icon found:', cartIcon.length);
    
    // Xóa badge cũ nếu có
    cartIcon.find('.cart-badge').remove();
    $('.cart-badge').remove(); // Xóa tất cả badge cũ
    
    // Chỉ hiển thị badge nếu có items
    if (count > 0) {
        const badge = $('<span>', {
            class: 'cart-badge',
            text: count > 99 ? '99+' : count
        });
        
        console.log('Adding badge to cart icon');
        // Thêm badge vào cart icon
        cartIcon.append(badge);
        console.log('Badge added successfully');
    } else {
        console.log('No items in cart, badge not shown');
    }
}

/**
 * Render cart preview dropdown - DISABLED
 * User only wants to show item count badge, not hover preview
 * @param {Object} cart - Cart data from API
 */
/* COMMENTED OUT - Cart preview disabled
function renderCartPreview(cart) {
    console.log('Rendering cart preview...', cart);
    
    const cartMenu = $('.cart-menu li');
    
    // Xóa cart-info cũ
    cartMenu.find('.cart-info').remove();
    
    // Nếu không có cart hoặc user chưa đăng nhập
    if (!cart || !cart.cartDetails || cart.cartDetails.length === 0) {
        const emptyCartHtml = `
            <div class="cart-info">
                <div class="cart-empty">
                    <p>Giỏ hàng trống</p>
                </div>
            </div>
        `;
        cartMenu.append(emptyCartHtml);
        return;
    }
    
    // Build cart items HTML
    let itemsHtml = '';
    let totalPrice = 0;
    
    cart.cartDetails.forEach(function(item) {
        const itemTotal = item.price * item.quantity;
        totalPrice += itemTotal;
        
        // Access nested product data
        const product = item.product?.product;
        const productName = product?.title || 'Unknown Product';
        const productId = product?.id || '';
        // Use product.image directly (already has full path like web.js)
        const imageUrl = product?.image || 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2260%22 height=%2260%22%3E%3Crect width=%22100%25%22 height=%22100%25%22 fill=%22%23f5f5f5%22/%3E%3Ctext x=%2250%25%22 y=%2250%25%22 dominant-baseline=%22middle%22 text-anchor=%22middle%22 font-family=%22Arial%22 font-size=%2210%22 fill=%22%23999%22%3E📷%3C/text%3E%3C/svg%3E';
        const size = item.product?.size || '';
        
        console.log('Cart item image:', {
            productName: productName,
            imageUrl: imageUrl,
            fullProduct: product
        });
        
        itemsHtml += `
            <li>
                <div class="cart-img">
                    <img src="${imageUrl}" alt="${productName}" onerror="this.onerror=null; this.src='data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2260%22 height=%2260%22%3E%3Crect width=%22100%25%22 height=%22100%25%22 fill=%22%23f5f5f5%22/%3E%3Ctext x=%2250%25%22 y=%2250%25%22 dominant-baseline=%22middle%22 text-anchor=%22middle%22 font-family=%22Arial%22 font-size=%2210%22 fill=%22%23999%22%3E📷%3C/text%3E%3C/svg%3E'">
                </div>
                <div class="cart-details">
                    <a href="/product/${productId}" title="${productName}">${productName}</a>
                    <p>Size ${size} • SL: ${item.quantity}</p>
                    <div class="product-price">${formatPrice(itemTotal)}</div>
                </div>
                <div class="btn-remove" data-id="${item.id}" title="Xóa">
                </div>
            </li>
        `;
    });
    
    // Build full cart preview HTML
    const cartPreviewHtml = `
        <div class="cart-info">
            <ul>
                ${itemsHtml}
            </ul>
            <h3>Tổng: <span>${formatPrice(totalPrice)}</span></h3>
            <a href="/cart/view" class="checkout">Xem giỏ hàng</a>
        </div>
    `;
    
    cartMenu.append(cartPreviewHtml);
    
    // Bind remove button event
    $('.cart-info .btn-remove').on('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        handleQuickRemove($(this).data('id'));
    });
}
*/

/**
 * Handle quick remove from cart preview
 */
function handleQuickRemove(detailId) {
    if (!confirm('Xóa sản phẩm này khỏi giỏ hàng?')) {
        return;
    }
    
    $.ajax({
        url: `/api/cart/delete/${detailId}`,
        method: 'DELETE',
        success: function(response) {
            if (response.success) {
                // Reload cart data
                loadCartCount();
            } else {
                alert('Lỗi khi xóa sản phẩm: ' + (response.message || 'Unknown error'));
            }
        },
        error: function(xhr, status, error) {
            console.error('Error removing item:', error);
            alert('Không thể xóa sản phẩm. Vui lòng thử lại.');
        }
    });
}

/**
 * Format price to VND
 */
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

/**
 * Function để refresh cart count từ bên ngoài
 * Gọi sau khi add/remove item khỏi giỏ hàng
 */
window.refreshCartCount = function() {
    loadCartCount();
};
