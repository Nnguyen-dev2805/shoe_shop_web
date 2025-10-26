/**
 * Chat Widget Debug Script
 * Thêm vào page để debug chat icon issues
 */

console.log('🔧 Chat Debug Script loaded');

// Test function để gọi từ console
window.testChatIcon = function() {
    console.log('=== CHAT ICON DEBUG ===');
    
    // Check elements
    const $icon = $('#chat-icon-link');
    const $container = $('#chat-icon-container');
    const $widget = $('#chatWidget');
    
    console.log('Elements found:', {
        icon: $icon.length,
        container: $container.length,
        widget: $widget.length
    });
    
    if ($icon.length > 0) {
        const icon = $icon[0];
        console.log('Icon element:', icon);
        console.log('Icon computed style:', {
            display: window.getComputedStyle(icon).display,
            pointerEvents: window.getComputedStyle(icon).pointerEvents,
            cursor: window.getComputedStyle(icon).cursor,
            zIndex: window.getComputedStyle(icon).zIndex,
            visibility: window.getComputedStyle(icon).visibility,
            opacity: window.getComputedStyle(icon).opacity
        });
        console.log('Icon position:', icon.getBoundingClientRect());
        
        // Check event listeners
        const events = $._data(icon, 'events');
        console.log('Event listeners:', events);
        
        // Try to click
        console.log('Attempting manual click...');
        $icon.trigger('click');
    } else {
        console.error('❌ Chat icon not found!');
        console.log('All elements with chat-related IDs:', {
            'chat-icon-link': $('#chat-icon-link').length,
            'chat-icon-container': $('#chat-icon-container').length,
            'chat-unread-count': $('#chat-unread-count').length,
            'chat-menu-link': $('#chat-menu-link').length
        });
    }
    
    // Check authentication
    console.log('Authentication elements:', {
        'sec:authorize elements': $('[sec\\:authorize]').length,
        'user in session': typeof currentUserId !== 'undefined' ? currentUserId : 'undefined'
    });
    
    // Check if toggleChatWidget is defined
    console.log('toggleChatWidget function:', typeof window.toggleChatWidget);
    
    console.log('=== END DEBUG ===');
};

// Auto-run on page load
$(document).ready(function() {
    console.log('📊 Chat Debug: Page ready');
    
    // Wait a bit for everything to load
    setTimeout(function() {
        console.log('📊 Running auto-debug...');
        window.testChatIcon();
        
        // Try to manually attach handler as backup
        $('#chat-icon-link, #chat-menu-link').off('click').on('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log('🖱️ BACKUP HANDLER: Chat clicked!');
            
            if (typeof window.toggleChatWidget === 'function') {
                window.toggleChatWidget();
            } else {
                console.error('❌ toggleChatWidget not found!');
            }
        });
        
        console.log('✅ Backup click handlers attached');
    }, 2000);
});
