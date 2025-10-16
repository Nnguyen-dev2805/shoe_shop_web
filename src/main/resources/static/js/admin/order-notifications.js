/**
 * WebSocket Order Notifications for Admin
 * Real-time notifications khi có đơn hàng mới từ user
 */

// Immediate log to verify script loads
console.log('%c============================================', 'color: green; font-weight: bold; font-size: 14px');
console.log('%c📦 ORDER-NOTIFICATIONS.JS LOADED!', 'color: green; font-weight: bold; font-size: 14px');
console.log('%c============================================', 'color: green; font-weight: bold; font-size: 14px');

// Wrap everything in IIFE to avoid conflicts
(function() {
    'use strict';
    
    console.log('📦 Starting WebSocket initialization...');

let stompClient = null;
let notificationCount = 0;
let isConnected = false;

// Check if dependencies are loaded
console.log('🔍 Order-notifications.js loaded');
console.log('jQuery available:', typeof jQuery !== 'undefined');
console.log('SockJS available:', typeof SockJS !== 'undefined');
console.log('Stomp available:', typeof Stomp !== 'undefined');

// Init function
function initWebSocketNotifications() {
    try {
        console.log('🚀 Initializing WebSocket Order Notifications...');
        
        // Check dependencies
        if (typeof SockJS === 'undefined') {
            console.error('❌ SockJS not loaded!');
            console.error('Check CDN: https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js');
            return;
        }
        if (typeof Stomp === 'undefined') {
            console.error('❌ Stomp not loaded!');
            console.error('Check CDN: https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js');
            return;
        }
        
        console.log('✅ All dependencies loaded!');
        
        // Connect to WebSocket server
        connectWebSocket();
        
        // Setup click handler for notification badge
        setupNotificationHandlers();
        
    } catch (error) {
        console.error('❌ Error in initWebSocketNotifications:', error);
        console.error('Stack trace:', error.stack);
    }
}

// Wait for jQuery or use vanilla JS
if (typeof jQuery !== 'undefined') {
    $(document).ready(initWebSocketNotifications);
} else if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initWebSocketNotifications);
} else {
    initWebSocketNotifications();
}

/**
 * Connect to WebSocket server using SockJS and STOMP
 */
function connectWebSocket() {
    console.log('🔌 Connecting to WebSocket server...');
    
    // Create SockJS connection
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    // Disable debug logging (optional)
    stompClient.debug = null;
    
    // Connect to server
    stompClient.connect({}, function(frame) {
        console.log('✅ WebSocket Connected:', frame);
        isConnected = true;
        
        // Subscribe to admin orders topic
        stompClient.subscribe('/topic/admin/orders', function(message) {
            const notification = JSON.parse(message.body);
            console.log('📦 Received order notification:', notification);
            
            // Handle the notification
            handleOrderNotification(notification);
        });
        
        console.log('✅ Subscribed to /topic/admin/orders');
        
    }, function(error) {
        console.error('❌ WebSocket connection error:', error);
        isConnected = false;
        
        // Retry connection after 5 seconds
        console.log('🔄 Retrying connection in 5 seconds...');
        setTimeout(connectWebSocket, 5000);
    });
}

/**
 * Handle incoming order notification
 */
function handleOrderNotification(notification) {
    console.log('📬 Processing notification:', notification);
    
    // Increment counter
    notificationCount++;
    
    // Update badge
    updateNotificationBadge(notificationCount);
    
    // Show toast notification
    showToastNotification(notification);
    
    // Play notification sound
    playNotificationSound();
    
    // Add animation effect to menu item
    animateOrderMenuItem();
    
    // If on orders page, refresh the table
    if (window.location.pathname.includes('/admin/order')) {
        console.log('📋 On orders page - refreshing table...');
        setTimeout(function() {
            location.reload(); // Simple reload, hoặc dùng AJAX refresh
        }, 2000);
    }
}

/**
 * Update notification badge on "Đơn Hàng" menu
 */
function updateNotificationBadge(count) {
    const badgeEl = document.getElementById('order-notification-badge');
    
    if (!badgeEl) {
        console.warn('⚠️ Badge element not found');
        return;
    }
    
    if (count > 0) {
        badgeEl.textContent = count > 99 ? '99+' : count;
        badgeEl.style.display = 'inline-block';
    } else {
        badgeEl.style.display = 'none';
    }
    
    console.log('🔔 Badge updated:', count);
}

/**
 * Show toast notification using Bootstrap Toast
 */
function showToastNotification(notification) {
    const notificationType = notification.notificationType || 'NEW_ORDER';
    let icon = '🛍️';
    let title = 'Đơn Hàng Mới!';
    let bgClass = 'bg-success';
    
    if (notificationType === 'ORDER_CANCELLED') {
        icon = '❌';
        title = 'Đơn Hàng Bị Hủy';
        bgClass = 'bg-danger';
    } else if (notificationType === 'ORDER_UPDATED') {
        icon = '🔄';
        title = 'Đơn Hàng Cập Nhật';
        bgClass = 'bg-info';
    }
    
    // Create toast HTML
    const toastHtml = `
        <div class="toast align-items-center text-white ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true" data-bs-delay="5000">
            <div class="d-flex">
                <div class="toast-body">
                    <h6 class="mb-1">${icon} ${title}</h6>
                    <p class="mb-1"><strong>${notification.customerName}</strong></p>
                    <p class="mb-1">Đơn hàng #${notification.orderId} - ${formatPrice(notification.totalPrice)}</p>
                    <small class="text-white-50">${notification.itemCount} sản phẩm</small>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    
    // Append to toast container (create if not exists)
    let toastContainer = $('#toast-container');
    if (toastContainer.length === 0) {
        $('body').append('<div id="toast-container" class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;"></div>');
        toastContainer = $('#toast-container');
    }
    
    // Add toast
    const $toast = $(toastHtml);
    toastContainer.append($toast);
    
    // Show toast using Bootstrap 5
    const toast = new bootstrap.Toast($toast[0]);
    toast.show();
    
    // Remove toast element after it's hidden
    $toast.on('hidden.bs.toast', function() {
        $(this).remove();
    });
    
    console.log('🍞 Toast notification shown');
}

/**
 * Play notification sound
 */
function playNotificationSound() {
    try {
        // Create audio element
        const audio = new Audio('/sounds/notification.mp3');
        audio.volume = 0.5;
        audio.play().catch(e => {
            console.log('Could not play notification sound:', e.message);
        });
    } catch (e) {
        console.log('Notification sound not available');
    }
}

/**
 * Animate order menu item to draw attention
 */
function animateOrderMenuItem() {
    const sidebarOrders = document.getElementById('sidebarOrders');
    if (!sidebarOrders) return;
    
    const orderMenuItem = sidebarOrders.closest('.nav-item');
    if (!orderMenuItem) return;
    
    // Add pulse animation
    orderMenuItem.classList.add('animate-pulse');
    
    // Remove animation after 2 seconds
    setTimeout(function() {
        orderMenuItem.classList.remove('animate-pulse');
    }, 2000);
}

/**
 * Setup notification handlers
 */
function setupNotificationHandlers() {
    // Click on badge to clear notifications
    const badge = document.getElementById('order-notification-badge');
    if (badge) {
        badge.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            // Reset counter
            notificationCount = 0;
            updateNotificationBadge(0);
            
            console.log('🔕 Notifications cleared');
        });
    }
}

/**
 * Format price to VND currency
 */
function formatPrice(price) {
    if (!price) return '0 ₫';
    
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

/**
 * Disconnect WebSocket when page unloads
 */
window.addEventListener('beforeunload', function() {
    if (stompClient !== null && isConnected) {
        console.log('👋 Disconnecting WebSocket...');
        stompClient.disconnect();
    }
});

// Add CSS animation for pulse effect
const style = document.createElement('style');
style.innerHTML = `
    @keyframes pulse {
        0% {
            transform: scale(1);
            opacity: 1;
        }
        50% {
            transform: scale(1.05);
            opacity: 0.8;
        }
        100% {
            transform: scale(1);
            opacity: 1;
        }
    }
    
    .animate-pulse {
        animation: pulse 0.5s ease-in-out 3;
    }
    
    #order-notification-badge {
        animation: pulse 1s ease-in-out infinite;
    }
`;
document.head.appendChild(style);

console.log('✅ Order notification system initialized');
console.log('===========================================');

})(); // End of IIFE

console.log('%c============================================', 'color: blue; font-weight: bold');
console.log('%c✅ ORDER-NOTIFICATIONS.JS FULLY EXECUTED', 'color: blue; font-weight: bold');
console.log('%c============================================', 'color: blue; font-weight: bold');
