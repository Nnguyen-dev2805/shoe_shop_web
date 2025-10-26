/**
 * Chat Widget - Popup chat cho user
 * Hiển thị dưới dạng floating widget
 */

let chatWidget = {
    stompClient: null,
    conversationId: null,
    isConnected: false,
    isOpen: false,
    unreadCount: 0,
    currentUserId: null,
    currentUserName: null,
    replyingTo: null  // Message being replied to
};

// ============= INITIALIZATION =============

// Initialize when jQuery is ready
if (typeof jQuery !== 'undefined') {
    $(document).ready(function() {
        console.log('📱 Chat Widget: jQuery loaded, initializing...');
        console.log('🔍 Current user info:', {
            userId: typeof currentUserId !== 'undefined' ? currentUserId : 'undefined',
            userName: typeof currentUserName !== 'undefined' ? currentUserName : 'undefined'
        });
        
        // Check if user is logged in
        if (typeof currentUserId === 'undefined' || currentUserId === 0) {
            console.log('⚠️ Chat Widget: User not logged in - disabled');
            return;
        }
        
        chatWidget.currentUserId = currentUserId;
        chatWidget.currentUserName = currentUserName || 'User';
        
        console.log('🚀 Chat Widget: Initializing for user:', {
            id: chatWidget.currentUserId,
            name: chatWidget.currentUserName
        });
        
        // Create widget HTML
        createChatWidget();
        
        // Connect WebSocket
        connectWebSocket();
        
        // Setup event listeners
        setupEventListeners();
        
        // Load conversation (will be called when widget is opened)
        // loadConversation(); // Don't load immediately
        
        console.log('✅ Chat Widget: Initialization complete (waiting for user to open)');
    });
} else {
    console.error('❌ Chat Widget: jQuery not found!');
}

// ============= CREATE WIDGET UI =============

function createChatWidget() {
    const widgetHTML = `
        <!-- Chat Widget Container -->
        <div id="chatWidget" class="chat-widget" style="display: none;">
            <div class="chat-widget-header">
                <div class="d-flex align-items-center">
                    <i class="fa fa-store me-2"></i>
                    <div>
                        <strong>Shop Support</strong>
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
                margin-top: 10px;
                width: 450px;
                height: 620px;
                background: white;
                border-radius: 16px;
                box-shadow: 0 10px 50px rgba(0,0,0,0.15), 0 4px 20px rgba(0,0,0,0.1);
                display: flex;
                flex-direction: column;
                z-index: 9999;
                animation: slideDown 0.3s ease;
                overflow: hidden;
            }
            
            @keyframes slideDown {
                from {
                    opacity: 0;
                }
                to {
                    opacity: 1;
                }
            }
            
            .chat-widget-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 18px 20px;
                border-radius: 16px 16px 0 0;
                display: flex;
                justify-content: space-between;
                align-items: center;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            
            .chat-widget-header strong {
                font-size: 16px;
                font-weight: 600;
            }
            
            .chat-status {
                font-size: 12px;
                display: flex;
                align-items: center;
                gap: 5px;
            }
            
            .status-indicator {
                width: 8px;
                height: 8px;
                border-radius: 50%;
                background: #fbbf24;
                display: inline-block;
            }
            
            .status-indicator.connected {
                background: #4ade80;
                animation: pulse 2s infinite;
            }
            
            @keyframes pulse {
                0%, 100% { opacity: 1; }
                50% { opacity: 0.5; }
            }
            
            .btn-close-widget {
                background: none;
                border: none;
                color: white;
                font-size: 20px;
                cursor: pointer;
                padding: 0;
                width: 30px;
                height: 30px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
                transition: background 0.2s;
            }
            
            .btn-close-widget:hover {
                background: rgba(255,255,255,0.2);
            }
            
            .chat-widget-messages {
                flex: 1;
                padding: 20px 30px 20px 20px;
                overflow-y: auto;
                overflow-x: visible;
                background: #f5f7fa;
                background-image: linear-gradient(to bottom, #f8f9fa 0%, #f5f7fa 100%);
                min-height: 0;
                scroll-behavior: smooth;
                position: relative;
            }
            
            /* Custom scrollbar */
            .chat-widget-messages::-webkit-scrollbar {
                width: 8px;
            }
            
            .chat-widget-messages::-webkit-scrollbar-track {
                background: #f1f1f1;
                border-radius: 10px;
            }
            
            .chat-widget-messages::-webkit-scrollbar-thumb {
                background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
                border-radius: 10px;
            }
            
            .chat-widget-messages::-webkit-scrollbar-thumb:hover {
                background: linear-gradient(180deg, #764ba2 0%, #667eea 100%);
            }
            
            .chat-message {
                margin-bottom: 16px;
                display: flex;
                animation: messageSlide 0.3s ease;
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
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border-bottom-right-radius: 6px;
                box-shadow: 0 3px 10px rgba(102, 126, 234, 0.4);
            }
            
            .chat-message.shop .chat-message-content {
                background: white;
                color: #2d3748;
                border: 1px solid #e2e8f0;
                border-bottom-left-radius: 6px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.06);
            }
            
            .chat-message-content {
                max-width: 75%;
                padding: 12px 16px;
                border-radius: 18px;
                font-size: 14.5px;
                line-height: 1.5;
                word-wrap: break-word;
                box-shadow: 0 2px 6px rgba(0,0,0,0.08);
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
                border-radius: 0 0 16px 16px;
                box-shadow: 0 -3px 15px rgba(0,0,0,0.08);
                flex-shrink: 0;
            }
            
            .chat-widget-input input {
                border-radius: 24px;
                border: 2px solid #e5e7eb;
                padding: 12px 20px;
                font-size: 15px;
                transition: all 0.2s;
                background: #f9fafb;
            }
            
            .chat-widget-input input:focus {
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
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
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border: none;
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                transition: all 0.2s;
                cursor: pointer;
            }
            
            .chat-widget-input button:hover {
                transform: scale(1.1);
                box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
            }
            
            .chat-widget-input button:active {
                transform: scale(0.95);
            }
            
            .chat-widget-input button i {
                font-size: 16px;
                color: white;
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
    
    const $widget = $('#chatWidget');
    
    if ($widget.length === 0) {
        console.error('❌ Chat Widget: Widget element not found!');
        return;
    }
    
    if (chatWidget.isOpen) {
        console.log('🔽 Chat Widget: Closing...');
        $widget.fadeOut(200);
        chatWidget.isOpen = false;
    } else {
        console.log('🔼 Chat Widget: Opening...');
        $widget.fadeIn(200);
        chatWidget.isOpen = true;
        
        // Load conversation if not loaded yet
        if (!chatWidget.conversationId) {
            console.log('📞 Chat Widget: First time opening, loading conversation...');
            loadConversation();
        } else {
            console.log('📞 Chat Widget: Conversation already loaded:', chatWidget.conversationId);
        }
        
        // Mark messages as read
        markMessagesAsRead();
        
        // Reset unread count
        chatWidget.unreadCount = 0;
        updateUnreadBadge();
        
        // Scroll to bottom
        setTimeout(scrollToBottom, 100);
    }
}

// Make toggleChatWidget available globally IMMEDIATELY
window.toggleChatWidget = toggleChatWidget;
console.log('✅ Chat Widget: toggleChatWidget function registered globally');

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

function loadConversation() {
    console.log('🔄 Chat Widget: Loading conversation...');
    
    $.ajax({
        url: '/api/chat/conversation',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            console.log('📥 Conversation response:', response);
            
            if (response.success && response.data) {
                chatWidget.conversationId = response.data.id;
                console.log('✅ Conversation loaded:', chatWidget.conversationId);
                loadMessages();
            } else {
                console.error('❌ Invalid conversation response:', response);
                showError('Không thể tạo cuộc trò chuyện', 'Vui lòng thử lại sau');
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error loading conversation:', {
                status: xhr.status,
                statusText: xhr.statusText,
                response: xhr.responseText,
                error: error
            });
            
            let errorMsg = 'Không thể kết nối';
            if (xhr.status === 401) {
                errorMsg = 'Vui lòng đăng nhập lại';
            } else if (xhr.status === 500) {
                errorMsg = 'Lỗi server';
            }
            
            showError('Lỗi tải cuộc trò chuyện', errorMsg);
        }
    });
}

function loadMessages() {
    if (!chatWidget.conversationId) {
        console.log('⚠️ Chat Widget: No conversation ID, skipping message load');
        showError('Lỗi', 'Không có ID cuộc trò chuyện');
        return;
    }
    
    console.log('📥 Chat Widget: Loading messages for conversation:', chatWidget.conversationId);
    
    $.ajax({
        url: '/api/chat/messages/' + chatWidget.conversationId + '/all',
        method: 'GET',
        dataType: 'json',
        timeout: 10000, // 10 second timeout
        success: function(response) {
            console.log('✅ Message response:', response);
            
            if (response.success) {
                const messages = response.data || [];
                console.log('📝 Chat Widget: Messages loaded:', messages.length);
                displayMessages(messages);
            } else {
                console.error('❌ Failed response:', response);
                showError('Không thể tải tin nhắn', response.message || 'Lỗi không xác định');
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error loading messages:', {
                status: xhr.status,
                statusText: xhr.statusText,
                response: xhr.responseText,
                error: error,
                ajaxStatus: status
            });
            
            let errorMsg = 'Vui lòng thử lại';
            if (status === 'timeout') {
                errorMsg = 'Quá thời gian chờ';
            } else if (xhr.status === 401) {
                errorMsg = 'Vui lòng đăng nhập lại';
            } else if (xhr.status === 403) {
                errorMsg = 'Không có quyền truy cập';
            } else if (xhr.status === 404) {
                errorMsg = 'Không tìm thấy cuộc trò chuyện';
            }
            
            showError('Lỗi tải tin nhắn', errorMsg);
        }
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
            appendMessage(message, true);
            
            // If widget is closed, increase unread count
            if (!chatWidget.isOpen) {
                chatWidget.unreadCount++;
                updateUnreadBadge();
                
                // Show browser notification
                showBrowserNotification(message);
            } else {
                // Mark as read if widget is open
                markMessagesAsRead();
            }
            
            scrollToBottom();
        }
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
    } else {
        $badge.hide();
    }
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
