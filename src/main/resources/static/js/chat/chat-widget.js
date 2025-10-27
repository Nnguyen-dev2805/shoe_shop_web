/**
 * Chat Widget - Popup chat cho user
 * Hiển thị dưới dạng floating widget
 */

let chatWidget = {
    stompClient: null,
    isConnected: false,
    isOpen: false,
    unreadCount: 0,
    currentUserId: null,
    currentUserName: null,
    replyingTo: null,  // Message being replied to
    messagesCache: null, // Cache messages for current conversation
    isLoadingMessages: false // Prevent duplicate loading
};

// ============= INITIALIZATION =============

// Initialize chat widget when DOM is ready
$(document).ready(function() {
    console.log('💬 Chat Widget: DOM ready, initializing...');
    
    if (typeof currentUserId !== 'undefined' && currentUserId > 0) {
        chatWidget.currentUserId = currentUserId;
        console.log('✅ Chat Widget: User ID set to', currentUserId);
        
        initializeChatWidget();
        
        // Check for unread messages on page load (multiple attempts)
        setTimeout(checkUnreadMessages, 1000);
        setTimeout(checkUnreadMessages, 3000);
        setTimeout(checkUnreadMessages, 5000);
        
        // Check periodically every 30 seconds
        setInterval(checkUnreadMessages, 30000);
    } else {
        console.warn('⚠️ Chat Widget: No valid user ID found');
    }
});

// Initialize chat widget
function initializeChatWidget() {
    console.log('✅ Chat Widget: Initializing for user:', chatWidget.currentUserId);
    
    // Create widget HTML
    createChatWidget();
    
    // Setup event listeners
    setupEventListeners();
    
    console.log('✅ Chat Widget: Initialization complete (waiting for user to open)');
}

// ============= CREATE WIDGET UI =============

function createChatWidget() {
    const widgetHTML = `
        <!-- Chat Widget Container -->
        <div id="chatWidget" class="chat-widget" style="display: none;">
            <div class="chat-widget-header">
                <div class="d-flex align-items-center">
                    <i class="fa fa-comments-o me-2"></i>
                    <div>
                        <strong>Hỗ trợ khách hàng</strong>
                        <div class="chat-status">
                            <span class="status-indicator"></span>
                            <small id="chatStatus">Đang kết nối...</small>
                        </div>
                    </div>
                </div>
                <button class="btn-close-widget" onclick="toggleChatWidget()">
                    <i class="fa fa-times"></i>
                </button>
            </div>
            
            <div class="chat-widget-messages" id="chatWidgetMessages">
                <div class="text-center py-4 text-muted">
                    <i class="fa fa-spinner fa-spin fa-2x mb-2"></i>
                    <p>Đang tải tin nhắn...</p>
                </div>
                <!-- Scroll to bottom button -->
                <div class="scroll-to-bottom-user" id="scrollToBottomUserBtn" onclick="scrollToBottom()">
                    <i class="fa fa-chevron-down"></i>
                </div>
            </div>
            
            <div class="chat-widget-input">
                <!-- Reply Preview -->
                <div id="userReplyPreview" class="reply-preview" style="display: none;">
                    <div class="reply-content">
                        <i class="fa fa-reply me-2"></i>
                        <div class="flex-grow-1">
                            <strong id="userReplyToName">Shop</strong>
                            <p id="userReplyToContent" class="mb-0 text-muted small"></p>
                        </div>
                        <button type="button" class="btn-cancel-reply" onclick="cancelUserReply()">
                            <i class="fa fa-times"></i>
                        </button>
                    </div>
                </div>
                <form id="chatWidgetForm" class="d-flex gap-2">
                    <input type="text" 
                           id="chatWidgetInput" 
                           class="form-control" 
                           placeholder="Nhập tin nhắn..."
                           autocomplete="off">
                    <button type="submit" class="btn btn-primary">
                        <i class="fa fa-paper-plane"></i>
                    </button>
                </form>
            </div>
        </div>
        
        <style>
            .chat-widget {
                position: absolute;
                top: 100%;
                right: 0;
                margin-top: 12px;
                width: 420px;
                max-width: 95vw;
                height: 680px;
                max-height: 85vh;
                background: white;
                border-radius: 24px;
                box-shadow: 0 20px 60px rgba(0,0,0,0.15), 0 8px 25px rgba(0,0,0,0.1);
                display: flex;
                flex-direction: column;
                z-index: 9999;
                animation: slideDown 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
                overflow: hidden;
                border: 1px solid rgba(0,0,0,0.06);
            }
            
            @keyframes slideDown {
                from {
                    opacity: 0;
                    transform: translateY(-30px) scale(0.9);
                }
                to {
                    opacity: 1;
                    transform: translateY(0) scale(1);
                }
            }
            
            .chat-widget-header {
                background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
                color: white;
                padding: 20px 24px;
                border-radius: 24px 24px 0 0;
                display: flex;
                justify-content: space-between;
                align-items: center;
                box-shadow: 0 4px 12px rgba(30, 58, 138, 0.25);
                position: relative;
                z-index: 10;
            }
            
            .chat-widget-header::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
                height: 1px;
                background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2) 50%, transparent);
            }
            
            .chat-widget-header strong {
                font-size: 17px;
                font-weight: 700;
                letter-spacing: -0.2px;
            }
            
            .chat-widget-header i.fa-comments-o {
                font-size: 20px;
                opacity: 0.95;
            }
            
            .chat-widget-header .chat-status small {
                font-size: 12px;
                opacity: 0.9;
                font-weight: 500;
            }
            
            .chat-status {
                font-size: 12px;
                display: flex;
                align-items: center;
                gap: 5px;
            }
            
            .status-indicator {
                width: 9px;
                height: 9px;
                border-radius: 50%;
                background: #fbbf24;
                display: inline-block;
                box-shadow: 0 0 0 2px rgba(251, 191, 36, 0.3);
            }
            
            .status-indicator.connected {
                background: #10b981;
                box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.3);
                animation: pulse 2.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
            }
            
            @keyframes pulse {
                0%, 100% { 
                    opacity: 1; 
                    transform: scale(1);
                }
                50% { 
                    opacity: 0.7; 
                    transform: scale(1.1);
                }
            }
            
            .btn-close-widget {
                background: rgba(255,255,255,0.1);
                border: none;
                color: white;
                font-size: 20px;
                cursor: pointer;
                padding: 0;
                width: 36px;
                height: 36px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
                transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                backdrop-filter: blur(10px);
            }
            
            .btn-close-widget:hover {
                background: rgba(255,255,255,0.25);
                transform: rotate(90deg);
            }
            
            .btn-close-widget:active {
                transform: rotate(90deg) scale(0.9);
            }
            
            .chat-widget-messages {
                flex: 1;
                padding: 18px 24px 18px 18px;
                overflow-y: auto;
                overflow-x: visible;
                background: #f8f9fa;
                background-image: linear-gradient(to bottom, #fafbfc 0%, #f5f7fa 100%);
                min-height: 0;
                scroll-behavior: smooth;
                position: relative;
            }
            
            /* Hide scrollbar */
            .chat-widget-messages::-webkit-scrollbar {
                display: none;
            }
            
            .chat-widget-messages {
                -ms-overflow-style: none;
                scrollbar-width: none;
            }
            
            .chat-message {
                margin-bottom: 14px;
                display: flex;
                animation: messageSlide 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                position: relative;
            }
            
            .chat-message:hover .message-actions {
                opacity: 1;
                visibility: visible;
            }
            
            @keyframes messageSlide {
                from {
                    opacity: 0;
                }
                to {
                    opacity: 1;
                }
            }
            
            .chat-message.user {
                justify-content: flex-end;
            }
            
            .chat-message.user .chat-message-content {
                background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
                color: white;
                border-bottom-right-radius: 6px;
                box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
                font-weight: 500;
            }
            
            .chat-message.shop .chat-message-content {
                background: white;
                color: #1f2937;
                border: 1px solid #e5e7eb;
                border-bottom-left-radius: 6px;
                box-shadow: 0 3px 10px rgba(0,0,0,0.08);
                font-weight: 500;
            }
            
            .chat-message-content {
                max-width: 72%;
                padding: 12px 16px;
                border-radius: 16px;
                font-size: 14px;
                line-height: 1.6;
                word-wrap: break-word;
                box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                position: relative;
            }
            
            .chat-message-content::before {
                content: '';
                position: absolute;
                width: 8px;
                height: 8px;
                border-radius: 2px;
            }
            
            .chat-message.user .chat-message-content::before {
                right: -4px;
                bottom: 0;
                background: inherit;
                transform: rotate(45deg) skewX(20deg);
            }
            
            .chat-message.shop .chat-message-content::before {
                left: -4px;
                bottom: 0;
                background: white;
                border-left: 1px solid #e5e7eb;
                border-bottom: 1px solid #e5e7eb;
                transform: rotate(45deg) skewX(-20deg);
            }
            
            .chat-message-time {
                font-size: 11px;
                opacity: 0.75;
                margin-top: 6px;
                font-weight: 500;
            }
            
            .chat-widget-input {
                padding: 18px 20px;
                border-top: 2px solid #e5e7eb;
                background: white;
                border-radius: 0 0 24px 24px;
                box-shadow: 0 -4px 16px rgba(0,0,0,0.08);
                flex-shrink: 0;
                position: relative;
            }
            
            .chat-widget-input::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 1px;
                background: linear-gradient(90deg, transparent, rgba(0,0,0,0.05) 50%, transparent);
            }
            
            .chat-widget-input input {
                border-radius: 12px;
                border: 2px solid #e5e7eb;
                padding: 11px 16px;
                font-size: 14px;
                transition: all 0.2s;
                background: #fafbfc;
            }
            
            .chat-widget-input input:focus {
                border-color: #3b82f6;
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12);
                outline: none;
                background: white;
            }
            
            .chat-widget-input input::placeholder {
                color: #9ca3af;
                font-size: 14px;
            }
            
            .chat-widget-input button {
                border-radius: 50%;
                width: 44px;
                height: 44px;
                display: flex;
                align-items: center;
                justify-content: center;
                background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
                border: none;
                box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
                transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                cursor: pointer;
                position: relative;
                overflow: hidden;
                flex-shrink: 0;
            }
            
            .chat-widget-input button::before {
                content: '';
                position: absolute;
                top: 50%;
                left: 50%;
                width: 0;
                height: 0;
                border-radius: 50%;
                background: rgba(255,255,255,0.3);
                transition: width 0.6s, height 0.6s, top 0.6s, left 0.6s;
            }
            
            .chat-widget-input button:hover::before {
                width: 100px;
                height: 100px;
                top: -50px;
                left: -50px;
            }
            
            .chat-widget-input button:hover {
                transform: scale(1.08);
                box-shadow: 0 6px 20px rgba(59, 130, 246, 0.5);
            }
            
            .chat-widget-input button:active {
                transform: scale(0.92);
            }
            
            .chat-widget-input button i {
                font-size: 16px;
                color: white;
                position: relative;
                z-index: 1;
            }
            
            @media (max-width: 768px) {
                .chat-widget {
                    width: 90vw;
                    right: 5vw;
                    left: auto;
                    height: 70vh;
                }
            }
            
            /* Arrow pointing to chat icon */
            .chat-widget::before {
                content: '';
                position: absolute;
                top: -8px;
                right: 15px;
                width: 0;
                height: 0;
                border-left: 8px solid transparent;
                border-right: 8px solid transparent;
                border-bottom: 8px solid white;
                filter: drop-shadow(0 -2px 3px rgba(0,0,0,0.1));
            }
            
            /* Date separator */
            .chat-date-separator {
                text-align: center;
                margin: 20px 0;
                position: relative;
            }
            
            .chat-date-separator::before {
                content: '';
                position: absolute;
                top: 50%;
                left: 0;
                right: 0;
                height: 1px;
                background: linear-gradient(to right, transparent, #e5e7eb 20%, #e5e7eb 80%, transparent);
            }
            
            .chat-date-separator span {
                background: white;
                padding: 6px 16px;
                border-radius: 16px;
                font-size: 12px;
                color: #6b7280;
                font-weight: 600;
                position: relative;
                z-index: 1;
                display: inline-block;
                border: 1px solid #e5e7eb;
                box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            }
            
            /* Message Actions */
            .message-actions {
                position: absolute;
                top: 50%;
                transform: translateY(-50%);
                background: white;
                border-radius: 20px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                display: flex;
                gap: 4px;
                padding: 4px;
                opacity: 0;
                visibility: hidden;
                transition: all 0.2s;
                z-index: 10;
            }
            
            .chat-message.shop .message-actions {
                right: -45px;
            }
            
            .chat-message.user .message-actions {
                left: -45px;
            }
            
            .message-action-btn {
                background: transparent;
                border: none;
                padding: 8px;
                border-radius: 50%;
                cursor: pointer;
                color: #6b7280;
                transition: all 0.2s;
                width: 32px;
                height: 32px;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .message-action-btn:hover {
                background: #f3f4f6;
                color: #667eea;
                transform: scale(1.1);
            }
            
            .message-action-btn i {
                font-size: 14px;
            }
            
            /* Reply Preview */
            .reply-preview {
                padding: 12px 16px;
                border-bottom: 1px solid #e5e7eb;
                background: #f9fafb;
            }
            
            .reply-content {
                display: flex;
                align-items: flex-start;
                gap: 8px;
                padding: 8px 12px;
                background: white;
                border-left: 3px solid #667eea;
                border-radius: 6px;
            }
            
            .reply-content i.fa-reply {
                color: #667eea;
                margin-top: 2px;
            }
            
            .reply-content strong {
                font-size: 13px;
                color: #667eea;
            }
            
            .reply-content p {
                font-size: 12px;
                max-height: 40px;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
            }
            
            .btn-cancel-reply {
                background: transparent;
                border: none;
                color: #6b7280;
                cursor: pointer;
                padding: 4px 8px;
                border-radius: 4px;
                transition: all 0.2s;
            }
            
            .btn-cancel-reply:hover {
                background: #e5e7eb;
                color: #1f2937;
            }
            
            /* Replied Message Indicator */
            .replied-message {
                background: #f3f4f6;
                border-left: 3px solid #667eea;
                padding: 8px 12px;
                border-radius: 6px;
                margin-bottom: 8px;
                font-size: 13px;
            }
            
            .replied-message strong {
                color: #667eea;
                font-size: 12px;
            }
            
            .replied-message p {
                margin: 0;
                color: #6b7280;
                font-size: 12px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            
            /* Scroll to bottom button */
            .scroll-to-bottom-user {
                position: absolute;
                bottom: 80px;
                right: 30px;
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border-radius: 50%;
                display: none;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                z-index: 100;
                transition: all 0.3s;
                animation: bounceIn 0.5s;
            }
            
            .scroll-to-bottom-user:hover {
                transform: scale(1.1);
                box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
            }
            
            .scroll-to-bottom-user i {
                color: white;
                font-size: 18px;
            }
            
            @keyframes bounceIn {
                0% {
                    opacity: 0;
                }
                100% {
                    opacity: 1;
                }
            }
        </style>
    `;
    
    // Find chat icon parent container and append widget there
    const $chatIconContainer = $('#chat-icon-container');
    if ($chatIconContainer.length > 0) {
        $chatIconContainer.css('position', 'relative');
        $chatIconContainer.append(widgetHTML);
        console.log('✅ Chat Widget: Attached to chat icon container');
    } else {
        // Fallback: try old selector
        const $chatIconParent = $('#chat-unread-count').closest('li');
        if ($chatIconParent.length > 0) {
            $chatIconParent.css('position', 'relative');
            $chatIconParent.append(widgetHTML);
            console.log('✅ Chat Widget: Attached to chat icon (fallback)');
        } else {
            // Last resort: attach to body
            $('body').append(widgetHTML);
            console.log('⚠️ Chat Widget: Attached to body (last resort)');
        }
    }
}

// ============= WIDGET TOGGLE =============

function toggleChatWidget() {
    console.log('💬 Chat Widget: Toggle called, isOpen:', chatWidget.isOpen);
    
    // Check unread when user interacts with chat
    if (!chatWidget.isOpen) {
        console.log('🔍 Checking unread before opening widget...');
        checkUnreadMessages();
    }
    
    const $widget = $('#chatWidget');
    
    if ($widget.length === 0) {
        console.error('❌ Chat Widget: Widget element not found!');
        return;
    }
    
    if ($widget.is(':visible')) {
        $widget.fadeOut(200);
        chatWidget.isOpen = false;
        console.log('💬 Chat widget closed');
        
        // Check unread count when closing widget
        setTimeout(checkUnreadMessages, 500);
    } else {
        $widget.fadeIn(200);
        chatWidget.isOpen = true;
        console.log('💬 Chat widget opened');
        
        // Mark messages as read when opening
        markMessagesAsRead();
        
        // Ensure conversation exists before loading messages
        if (!chatWidget.conversationId) {
            console.log('🔄 No conversation ID, creating conversation first...');
            loadConversation();
        } else if (!chatWidget.messagesLoaded) {
            loadMessages();
        }
        
        // Connect WebSocket if not connected
        if (!chatWidget.isConnected) {
            connectWebSocket();
        }
        
        // Scroll to bottom
        setTimeout(scrollToBottom, 100);
    }
}

// Make toggleChatWidget available globally IMMEDIATELY
window.toggleChatWidget = toggleChatWidget;
// Debug function for user widget
window.debugChatWidget = function() {
    console.log('🔍 User Chat Widget Debug:');
    console.log('- Widget:', $('#chatWidget').length);
    console.log('- Messages container:', $('#chatWidgetMessages').length);
    console.log('- Input:', $('#chatWidgetInput').length);
    console.log('- Chat object:', chatWidget);
    console.log('- Conversation ID:', chatWidget.conversationId);
    console.log('- Messages loaded:', chatWidget.messagesLoaded);
    console.log('- Messages cache:', chatWidget.messagesCache ? chatWidget.messagesCache.length : 'null');
    console.log('- Is loading:', chatWidget.isLoadingMessages);
};

// Expose functions for debugging
window.loadMessages = loadMessages;
window.loadConversation = loadConversation;
window.checkUnreadMessages = checkUnreadMessages;
window.forceCheckUnread = function() {
    console.log('🔄 Force checking unread messages...');
    checkUnreadMessages();
};

window.testBadge = function(count = 5) {
    console.log('🧪 Testing badge with count:', count);
    chatWidget.unreadCount = count;
    updateUnreadBadge();
    
    // Check if badge element exists
    const $badge = $('#chat-unread-count');
    console.log('Badge element:', {
        exists: $badge.length > 0,
        visible: $badge.is(':visible'),
        text: $badge.text(),
        css: {
            display: $badge.css('display'),
            position: $badge.css('position'),
            zIndex: $badge.css('z-index')
        }
    });
};

console.log('✅ Chat Widget loaded');
console.log('🔧 Run debugChatWidget() in console to check elements');

// ============= WEBSOCKET CONNECTION =============

function connectWebSocket() {
    const socket = new SockJS('/ws');
    chatWidget.stompClient = Stomp.over(socket);
    
    chatWidget.stompClient.connect({}, 
        function(frame) {
            console.log('✅ WebSocket connected');
            chatWidget.isConnected = true;
            updateConnectionStatus(true);
            
            // Subscribe to personal queue
            chatWidget.stompClient.subscribe('/queue/chat.user.' + chatWidget.currentUserId, function(message) {
                const notification = JSON.parse(message.body);
                handleNotification(notification);
            });
            
            console.log('📨 Subscribed to chat updates');
        },
        function(error) {
            console.error('❌ WebSocket error:', error);
            chatWidget.isConnected = false;
            updateConnectionStatus(false);
            
            // Retry after 5s
            setTimeout(connectWebSocket, 5000);
        }
    );
}

function updateConnectionStatus(connected) {
    const $indicator = $('.status-indicator');
    const $status = $('#chatStatus');
    
    if (connected) {
        $indicator.addClass('connected');
        $status.text('Trực tuyến');
    } else {
        $indicator.removeClass('connected');
        $status.text('Đang kết nối...');
    }
}

// ============= API CALLS =============

function loadConversation(retryCount = 0) {
    console.log('🔄 Chat Widget: Loading conversation... retry:', retryCount);
    
    // Show loading state
    const $container = $('#chatWidgetMessages');
    $container.html(`
        <div class="text-center py-4 text-muted">
            <i class="fa fa-spinner fa-spin fa-2x mb-2"></i>
            <p>Đang tạo cuộc trò chuyện...</p>
        </div>
    `);
    
    $.ajax({
        url: '/api/chat/conversation',
        method: 'GET',
        dataType: 'json',
        timeout: 10000, // 10 second timeout
        cache: false
    })
    .done(function(response) {
        console.log('📥 Conversation response:', response);
        
        if (response.success && response.data) {
            chatWidget.conversationId = response.data.id;
            console.log('✅ Conversation loaded:', chatWidget.conversationId);
            
            // Now load messages
            loadMessages();
        } else {
            console.error('❌ Invalid conversation response:', response);
            showConversationError('Không thể tạo cuộc trò chuyện', 'Dữ liệu không hợp lệ');
        }
    })
    .fail(function(xhr, status, error) {
        console.error('❌ Error loading conversation:', {
            status: xhr.status,
            statusText: xhr.statusText,
            response: xhr.responseText,
            error: error,
            ajaxStatus: status
        });
        
        // Retry logic for network/server errors
        if (retryCount < 2 && (xhr.status === 0 || xhr.status >= 500 || status === 'timeout')) {
            console.log('🔄 Retrying conversation load in 2 seconds...');
            setTimeout(() => {
                loadConversation(retryCount + 1);
            }, 2000);
            return;
        }
        
        let errorMsg = 'Không thể kết nối';
        if (status === 'timeout') {
            errorMsg = 'Quá thời gian chờ - Server phản hồi chậm';
        } else if (xhr.status === 401) {
            errorMsg = 'Vui lòng đăng nhập lại';
        } else if (xhr.status === 403) {
            errorMsg = 'Không có quyền truy cập';
        } else if (xhr.status >= 500) {
            errorMsg = 'Lỗi server - Vui lòng thử lại sau';
        }
        
        showConversationError('Lỗi tải cuộc trò chuyện', errorMsg);
    });
}

// Show conversation-specific error with retry
function showConversationError(title, message) {
    const $container = $('#chatWidgetMessages');
    $container.html(`
        <div class="text-center py-4 text-danger">
            <i class="fa fa-exclamation-triangle fa-2x mb-2"></i>
            <p>${title}</p>
            <small class="text-muted d-block mb-2">${message}</small>
            <button class="btn btn-sm btn-outline-primary" onclick="loadConversation(0)">
                <i class="fa fa-refresh"></i> Thử lại
            </button>
        </div>
    `);
}

function loadMessages(retryCount = 0, forceReload = false) {
    if (!chatWidget.conversationId) {
        console.log('⚠️ Chat Widget: No conversation ID, skipping message load');
        showError('Lỗi', 'Không có ID cuộc trò chuyện');
        return;
    }
    
    // Check if already loading
    if (chatWidget.isLoadingMessages) {
        console.log('⏳ Already loading messages');
        return;
    }
    
    // Check cache first (unless force reload)
    if (!forceReload && chatWidget.messagesCache) {
        console.log('💾 Using cached messages');
        displayMessages(chatWidget.messagesCache);
        return;
    }
    
    console.log('📥 Chat Widget: Loading messages for conversation:', chatWidget.conversationId, 'retry:', retryCount);
    
    // Mark as loading
    chatWidget.isLoadingMessages = true;
    
    // Show loading state
    const $container = $('#chatWidgetMessages');
    $container.html(`
        <div class="text-center py-4 text-muted">
            <i class="fa fa-spinner fa-spin fa-2x mb-2"></i>
            <p>Đang tải tin nhắn...</p>
        </div>
    `);
    
    $.ajax({
        url: '/api/chat/messages/' + chatWidget.conversationId + '/all',
        method: 'GET',
        dataType: 'json',
        timeout: 8000, // 8 second timeout
        cache: false
    })
    .done(function(response) {
        console.log('✅ Message response:', response);
        
        // Clear loading state
        chatWidget.isLoadingMessages = false;
        
        if (response.success) {
            const messages = response.data || [];
            console.log('📝 Chat Widget: Messages loaded and cached:', messages.length);
            
            // Cache the messages
            chatWidget.messagesCache = messages;
            
            displayMessages(messages);
            chatWidget.messagesLoaded = true;
        } else {
            console.error('❌ Failed response:', response);
            showError('Không thể tải tin nhắn', response.message || 'Lỗi không xác định');
        }
    })
    .fail(function(xhr, status, error) {
        console.error('❌ Error loading messages:', {
            status: xhr.status,
            statusText: xhr.statusText,
            response: xhr.responseText,
            error: error,
            ajaxStatus: status
        });
        
        // Clear loading state
        chatWidget.isLoadingMessages = false;
        
        // Retry logic for network errors
        if (retryCount < 2 && (xhr.status === 0 || xhr.status >= 500 || status === 'timeout')) {
            console.log('🔄 Retrying in 2 seconds...');
            setTimeout(() => {
                loadMessages(retryCount + 1, forceReload);
            }, 2000);
            return;
        }
        
        let errorMsg = 'Vui lòng thử lại';
        if (status === 'timeout') {
            errorMsg = 'Quá thời gian chờ - Server phản hồi chậm';
        } else if (xhr.status === 401) {
            errorMsg = 'Vui lòng đăng nhập lại';
        } else if (xhr.status === 403) {
            errorMsg = 'Không có quyền truy cập';
        } else if (xhr.status === 404) {
            errorMsg = 'Không tìm thấy cuộc trò chuyện';
        } else if (xhr.status >= 500) {
            errorMsg = 'Lỗi server - Vui lòng thử lại sau';
        }
        
        // Show error with retry button
        const $container = $('#chatWidgetMessages');
        $container.html(`
            <div class="text-center py-4 text-danger">
                <i class="fa fa-exclamation-triangle fa-2x mb-2"></i>
                <p>Lỗi tải tin nhắn</p>
                <small class="text-muted d-block mb-2">${errorMsg}</small>
                <button class="btn btn-sm btn-outline-primary" onclick="loadMessages(0, true)">
                    <i class="fa fa-refresh"></i> Thử lại
                </button>
            </div>
        `);
    });
}

// Helper function to show errors
function showError(title, message) {
    $('#chatWidgetMessages').html(`
        <div class="text-center py-4 text-muted">
            <i class="fa fa-exclamation-triangle fa-2x mb-3 text-warning"></i>
            <h6 class="mb-2">${title}</h6>
            <p class="small">${message}</p>
            <button class="btn btn-sm btn-primary mt-2" onclick="window.chatWidgetRetry()">
                <i class="fa fa-refresh"></i> Thử lại
            </button>
        </div>
    `);
}

// Retry function - global
window.chatWidgetRetry = function() {
    console.log('🔄 Retrying conversation load...');
    $('#chatWidgetMessages').html(`
        <div class="text-center py-4 text-muted">
            <i class="fa fa-spinner fa-spin fa-2x mb-2"></i>
            <p>Đang tải tin nhắn...</p>
        </div>
    `);
    loadConversation();
};

function markMessagesAsRead() {
    if (chatWidget.stompClient && chatWidget.isConnected && chatWidget.conversationId) {
        chatWidget.stompClient.send('/app/chat.markReadUser', {}, chatWidget.conversationId);
    }
}

// ============= MESSAGE HANDLING =============

function sendMessage(content) {
    if (!content.trim()) {
        console.warn('⚠️ Empty message, ignoring');
        return;
    }
    
    if (!chatWidget.stompClient || !chatWidget.isConnected) {
        console.error('❌ WebSocket not connected');
        alert('Chưa kết nối. Vui lòng thử lại!');
        return;
    }
    
    const messageContent = content.trim();
    console.log('📤 Sending message:', messageContent);
    
    // Create message object for sending
    const message = {
        senderId: chatWidget.currentUserId,
        senderType: 'USER',
        content: messageContent
    };
    
    // Add reply info if replying
    if (chatWidget.replyingTo) {
        message.replyToId = chatWidget.replyingTo.id;
    }
    
    // Create message object for UI (with current timestamp)
    const uiMessage = {
        ...message,
        sentAt: new Date().toISOString(),
        id: Date.now(), // Temporary ID
        replyTo: chatWidget.replyingTo
    };
    
    // Append to UI immediately
    appendMessage(uiMessage, true);
    
    // Send via WebSocket
    chatWidget.stompClient.send('/app/chat.sendUser', {}, JSON.stringify(message));
    console.log('✅ Message sent and displayed');
    
    // Clear reply state
    if (chatWidget.replyingTo) {
        cancelUserReply();
    }
}

// Reply to message (User widget)
function replyToUserMessage(messageId, content, senderType) {
    const senderName = senderType === 'MANAGER' ? 'Shop' : 'Bạn';
    
    chatWidget.replyingTo = {
        id: messageId,
        content: content,
        senderType: senderType
    };
    
    // Show reply preview
    $('#userReplyToName').text(senderName);
    $('#userReplyToContent').text(content);
    $('#userReplyPreview').slideDown(200);
    
    // Focus input
    $('#chatWidgetInput').focus();
    
    console.log('📝 User replying to message:', messageId);
}

window.replyToUserMessage = replyToUserMessage;

// Cancel reply (User widget)
function cancelUserReply() {
    chatWidget.replyingTo = null;
    $('#userReplyPreview').slideUp(200);
}

window.cancelUserReply = cancelUserReply;

function handleNotification(notification) {
    console.log('📬 Notification received:', notification.type);
    
    if (notification.type === 'NEW_MESSAGE') {
        const message = notification.message;
        console.log('💬 New message from:', message.senderType);
        
        // Only append MANAGER messages (user messages already appended)
        if (message.senderType === 'MANAGER') {
            // Update cache with new message
            if (chatWidget.messagesCache) {
                chatWidget.messagesCache.push(message);
            }
            
            appendMessage(message, true);
            
            // Update unread count based on widget state
            if (!chatWidget.isOpen) {
                // Widget closed → Increment unread count
                chatWidget.unreadCount++;
                updateUnreadBadge();
                console.log('📬 New message while widget closed, unread count:', chatWidget.unreadCount);
            } else {
                // Widget open → Mark as read immediately
                console.log('📖 New message while widget open, marking as read');
                markMessagesAsRead();
            }
            
            scrollToBottom();
        }
    }
}

// Mark messages as read when widget is opened
function markMessagesAsRead() {
    console.log('📖 Marking messages as read, current unread:', chatWidget.unreadCount);
    
    if (chatWidget.unreadCount > 0 && chatWidget.conversationId) {
        // Send mark as read via WebSocket
        if (chatWidget.stompClient && chatWidget.isConnected) {
            chatWidget.stompClient.send('/app/chat.markReadUser', {}, JSON.stringify(chatWidget.conversationId));
            console.log('📤 Sent mark as read via WebSocket for conversation:', chatWidget.conversationId);
        }
        
        // Reset local count
        chatWidget.unreadCount = 0;
        updateUnreadBadge();
        
        // Also check server state after marking as read
        setTimeout(checkUnreadMessages, 1000);
    } else {
        console.log('📖 No unread messages to mark or no conversation ID');
    }
}

// ============= UI UPDATES =============

function displayMessages(messages) {
    const $container = $('#chatWidgetMessages');
    $container.empty();
    
    if (!messages || messages.length === 0) {
        $container.html(`
            <div class="text-center py-4 text-muted">
                <i class="fa fa-comments fa-3x mb-2 opacity-25"></i>
                <p>Chưa có tin nhắn<br>Hãy bắt đầu cuộc trò chuyện!</p>
            </div>
        `);
        return;
    }
    
    console.log('📝 Chat Widget: Displaying', messages.length, 'messages');
    
    // Sort messages by sentAt (oldest first)
    messages.sort((a, b) => new Date(a.sentAt) - new Date(b.sentAt));
    
    // Display all messages with date separators
    let lastDate = null;
    messages.forEach(msg => {
        const msgDate = new Date(msg.sentAt).toLocaleDateString('vi-VN');
        
        // Add date separator if date changed
        if (msgDate !== lastDate) {
            $container.append(`
                <div class="chat-date-separator">
                    <span>${msgDate}</span>
                </div>
            `);
            lastDate = msgDate;
        }
        
        appendMessage(msg, false);
    });
    
    // Scroll to bottom after short delay to ensure render
    setTimeout(scrollToBottom, 100);
}

function appendMessage(message, animate = true) {
    const $container = $('#chatWidgetMessages');
    $container.find('.text-center').remove();
    
    const isUser = message.senderType === 'USER';
    const messageClass = isUser ? 'user' : 'shop';
    const time = formatTime(message.sentAt);
    const messageId = message.id || Date.now();
    
    // Replied message indicator
    let repliedHtml = '';
    if (message.replyTo) {
        const replyToName = message.replyTo.senderType === 'MANAGER' ? 'Shop' : 'Bạn';
        repliedHtml = `
            <div class="replied-message">
                <strong>↩ ${replyToName}</strong>
                <p>${escapeHtml(message.replyTo.content)}</p>
            </div>
        `;
    }
    
    // Message actions (only for shop messages)
    const actionsHtml = !isUser ? `
        <div class="message-actions">
            <button class="message-action-btn" onclick="replyToUserMessage(${messageId}, '${escapeHtml(message.content).replace(/'/g, "\\'")}', '${message.senderType}')" title="Trả lời">
                <i class="fa fa-reply"></i>
            </button>
        </div>
    ` : '';
    
    const html = `
        <div class="chat-message ${messageClass}" data-message-id="${messageId}">
            <div class="chat-message-content">
                ${repliedHtml}
                ${escapeHtml(message.content)}
                <div class="chat-message-time">${time}</div>
            </div>
            ${actionsHtml}
        </div>
    `;
    
    $container.append(html);
    
    if (animate) {
        scrollToBottom();
    }
}

function scrollToBottom() {
    const $container = $('#chatWidgetMessages');
    $container.animate({ 
        scrollTop: $container[0].scrollHeight 
    }, 400, 'swing'); // Smooth easing
    
    // Hide scroll button
    $('#scrollToBottomUserBtn').fadeOut(200);
}

// Check scroll position for user widget
function checkUserScrollPosition() {
    const $container = $('#chatWidgetMessages');
    if (!$container.length) return;
    
    const scrollTop = $container.scrollTop();
    const scrollHeight = $container[0].scrollHeight;
    const clientHeight = $container[0].clientHeight;
    
    // Show button if not at bottom
    if (scrollHeight - scrollTop - clientHeight > 100) {
        $('#scrollToBottomUserBtn').fadeIn(200);
    } else {
        $('#scrollToBottomUserBtn').fadeOut(200);
    }
}

// Attach scroll listener for user widget
$(document).on('scroll', '#chatWidgetMessages', checkUserScrollPosition);

function updateUnreadBadge() {
    const $badge = $('#chat-unread-count');
    
    if (chatWidget.unreadCount > 0) {
        $badge.text(chatWidget.unreadCount).css('display', 'flex');
        console.log('📬 User unread badge shown:', chatWidget.unreadCount);
    } else {
        $badge.hide();
        console.log('✅ User unread badge hidden');
    }
}

// Check for unread messages from server
function checkUnreadMessages() {
    if (!chatWidget.currentUserId) {
        console.warn('⚠️ Cannot check unread messages: No user ID');
        return;
    }
    
    console.log('🔍 Checking unread messages for user:', chatWidget.currentUserId);
    
    // Call API to get user's unread count
    $.ajax({
        url: '/api/chat/user/unread-count',
        method: 'GET',
        timeout: 10000,
        cache: false
    })
    .done(function(response) {
        console.log('📊 Unread count response:', response);
        
        if (response.success) {
            const newUnreadCount = response.count || 0;
            console.log('📬 Server unread count:', newUnreadCount, 'Local count:', chatWidget.unreadCount);
            
            chatWidget.unreadCount = newUnreadCount;
            updateUnreadBadge();
        } else {
            console.error('❌ Invalid unread count response:', response);
        }
    })
    .fail(function(xhr, status, error) {
        console.error('❌ Failed to check unread messages:', {
            status: xhr.status,
            statusText: xhr.statusText,
            error: error,
            response: xhr.responseText
        });
        
        // If 401, user might need to login again
        if (xhr.status === 401) {
            console.warn('🔑 User not authenticated, badge may not work');
        }
    });
}

function showBrowserNotification(message) {
    if ('Notification' in window && Notification.permission === 'granted') {
        new Notification('Tin nhắn mới từ Shop', {
            body: message.content,
            icon: '/img/logo.png',
            badge: '/img/logo.png'
        });
    }
}

// ============= EVENT LISTENERS =============

function setupEventListeners() {
    // Check if chat icon exists
    const $chatIcon = $('#chat-icon-link');
    const $chatMenu = $('#chat-menu-link');
    
    console.log('🔍 Checking elements:', {
        chatIcon: $chatIcon.length,
        chatMenu: $chatMenu.length,
        chatWidget: $('#chatWidget').length
    });
    
    // Attach click handler to chat icon in header
    if ($chatIcon.length > 0) {
        $chatIcon.on('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log('🖱️ Chat icon clicked');
            toggleChatWidget();
        });
        console.log('✅ Chat icon handler attached');
    } else {
        console.warn('⚠️ Chat icon not found in DOM');
    }
    
    // Attach click handler to chat menu item
    if ($chatMenu.length > 0) {
        $chatMenu.on('click', function(e) {
            e.preventDefault();
            console.log('🖱️ Chat menu item clicked');
            toggleChatWidget();
        });
        console.log('✅ Chat menu handler attached');
    } else {
        console.warn('⚠️ Chat menu item not found in DOM');
    }
    
    // Send message form
    $('#chatWidgetForm').on('submit', function(e) {
        e.preventDefault();
        
        const $input = $('#chatWidgetInput');
        const content = $input.val();
        
        sendMessage(content);
        $input.val('');
    });
    
    // Close widget when clicking outside
    $(document).on('click', function(e) {
        const $widget = $('#chatWidget');
        const $chatIcon = $('#chat-icon-link');
        
        // If widget is open and click is outside widget and chat icon
        if (chatWidget.isOpen && 
            !$widget.is(e.target) && 
            $widget.has(e.target).length === 0 &&
            !$chatIcon.is(e.target) &&
            $chatIcon.has(e.target).length === 0) {
            toggleChatWidget();
        }
    });
    
    // Prevent widget clicks from bubbling
    $(document).on('click', '#chatWidget', function(e) {
        e.stopPropagation();
    });
    
    // Request notification permission
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission();
    }
    
    console.log('✅ Event listeners attached');
}

// ============= UTILITY FUNCTIONS =============

function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// ============= CLEANUP =============

window.addEventListener('beforeunload', function() {
    if (chatWidget.stompClient) {
        chatWidget.stompClient.disconnect();
    }
});
