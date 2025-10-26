/**
 * Chat Badge Updater
 * Cập nhật số lượng tin nhắn chưa đọc trong header
 * Dùng cho cả User và Manager
 */

// Check if user is logged in and is Manager
function isManager() {
    // Check if on manager pages
    return window.location.pathname.startsWith('/manager');
}

// Update chat badge for Manager
function updateManagerChatBadge() {
    $.ajax({
        url: '/api/chat/manager/unread-count',
        method: 'GET',
        success: function(response) {
            if (response.success && response.count > 0) {
                $('#manager-chat-unread-badge').text(response.count).show();
            } else {
                $('#manager-chat-unread-badge').hide();
            }
        },
        error: function() {
            // Silently fail - user might not be authenticated
            $('#manager-chat-unread-badge').hide();
        }
    });
}

// Initialize badge updater
$(document).ready(function() {
    // Only run on manager pages
    if (isManager()) {
        // Initial load
        updateManagerChatBadge();
        
        // Update every 30 seconds
        setInterval(updateManagerChatBadge, 30000);
    }
});
