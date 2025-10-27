/**
 * Manager Chat Widget
 * Hiển thị danh sách conversations và chat với users
 */

const managerChat = {
    stompClient: null,
    isConnected: false,
    isOpen: false,
    conversations: [],
    selectedConversation: null,
    replyingTo: null,
    messagesCache: new Map(), // Cache messages by conversation ID
    loadingConversations: new Set(), // Track loading conversations
    unreadCount: 0
};

if (typeof jQuery !== 'undefined') {
    $(document).ready(function() {
        console.log('📱 Manager Chat Widget: Initializing...');
        
        if (typeof currentManagerId === 'undefined' || currentManagerId === 0) {
            console.log('⚠️ Manager not logged in');
            return;
        }
        
        console.log('🚀 Manager Chat: Init for manager:', currentManagerId);
        
        // Create widget HTML
        createManagerChatWidget();
        
        // Connect WebSocket
        connectWebSocket();
        
        // Setup event listeners
        setupEventListeners();
        
        console.log('✅ Manager Chat Widget ready');
    });
} else {
    console.error('❌ jQuery not found!');
}

// ============= CREATE WIDGET UI =============

function createManagerChatWidget() {
    const widgetHTML = `
        <div id="managerChatWidget" class="manager-chat-widget" style="display: none;">
            <!-- Header -->
            <div class="manager-chat-header">
                <div class="d-flex align-items-center">
                    <i class="fa fa-comments me-2"></i>
                    <div>
                        <strong>Tin nhắn khách hàng</strong>
                        <div class="small text-white-50" id="conversationCount">0 cuộc trò chuyện</div>
                    </div>
                </div>
                <button class="btn-close-chat" onclick="toggleManagerChat()">
                    <i class="fa fa-times"></i>
                </button>
            </div>
            
            <!-- Content Area -->
            <div class="manager-chat-content">
                <!-- Conversations List (default view) -->
                <div id="conversationsList" class="conversations-list">
                    <div class="search-box">
                        <input type="text" id="searchConversations" class="form-control" placeholder="🔍 Tìm kiếm...">
                    </div>
                    <div id="conversationsContainer" class="conversations-container">
                        <div class="text-center py-4 text-muted">
                            <i class="fa fa-spinner fa-spin fa-2x mb-2"></i>
                            <p>Đang tải...</p>
                        </div>
                    </div>
                </div>
                
                <!-- Chat View (when conversation selected) -->
                <div id="chatView" class="chat-view" style="display: none;">
                    <!-- Chat Header -->
                    <div class="chat-view-header">
                        <button class="btn-back" onclick="showConversationsList()">
                            <i class="fa fa-arrow-left"></i>
                        </button>
                        <div class="user-info">
                            <strong id="chatUserName">User</strong>
                            <div class="small text-muted" id="chatUserEmail">email@example.com</div>
                        </div>
                    </div>
                    
                    <!-- Messages -->
                    <div id="chatMessages" class="chat-messages">
                        <div class="text-center py-4 text-muted">
                            <i class="fa fa-comments fa-3x mb-2 opacity-25"></i>
                            <p>Chưa có tin nhắn</p>
                        </div>
                        <!-- Scroll to bottom button -->
                        <div class="scroll-to-bottom" id="scrollToBottomBtn" onclick="scrollToBottom()">
                            <i class="fa fa-chevron-down"></i>
                        </div>
                    </div>
                    
                    <!-- Input -->
                    <div class="chat-input">
                        <!-- Reply Preview -->
                        <div id="replyPreview" class="reply-preview" style="display: none;">
                            <div class="reply-content">
                                <i class="fa fa-reply me-2"></i>
                                <div class="flex-grow-1">
                                    <strong id="replyToName">User</strong>
                                    <p id="replyToContent" class="mb-0 text-muted small"></p>
                                </div>
                                <button type="button" class="btn-cancel-reply" onclick="cancelReply()">
                                    <i class="fa fa-times"></i>
                                </button>
                            </div>
                        </div>
                        <form id="managerChatForm" class="d-flex gap-2">
                            <input type="text" id="managerChatInput" class="form-control" placeholder="Nhập tin nhắn..." autocomplete="off">
                            <button type="submit" class="btn btn-primary">
                                <i class="fa fa-paper-plane"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        
        <style>
            .manager-chat-widget {
                position: absolute;
                top: calc(100% + 10px);
                right: 0;
                width: 480px;
                height: 650px;
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
            
            .manager-chat-header {
                background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
                color: white;
                padding: 18px 20px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            
            .manager-chat-header strong {
                font-size: 16px;
            }
            
            .btn-close-chat {
                background: transparent;
                border: none;
                color: white;
                cursor: pointer;
                padding: 4px 8px;
                border-radius: 4px;
                transition: background 0.2s;
            }
            
            .btn-close-chat:hover {
                background: rgba(255,255,255,0.2);
            }
            
            .manager-chat-content {
                flex: 1;
                overflow: hidden;
                display: flex;
                flex-direction: column;
                min-height: 0;
            }
            
            /* ===== CONVERSATIONS LIST ===== */
            .conversations-list {
                flex: 1;
                display: flex;
                flex-direction: column;
            }
            
            .search-box {
                padding: 15px;
                border-bottom: 1px solid #e5e7eb;
            }
            
            .search-box input {
                border-radius: 20px;
                border: 2px solid #e5e7eb;
                padding: 8px 16px;
                font-size: 14px;
            }
            
            .search-box input:focus {
                border-color: #3b82f6;
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
                outline: none;
            }
            
            .conversations-container {
                flex: 1;
                overflow-y: auto;
            }
            
            .conversation-item {
                padding: 16px;
                border-bottom: 1px solid #f3f4f6;
                cursor: pointer;
                transition: background 0.2s;
                display: flex;
                gap: 12px;
            }
            
            .conversation-item:hover {
                background: #f9fafb;
            }
            
            .conversation-item.active {
                background: #eff6ff;
                border-left: 4px solid #3b82f6;
            }
            
            .conversation-avatar {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-weight: 600;
                font-size: 18px;
                flex-shrink: 0;
            }
            
            .conversation-info {
                flex: 1;
                min-width: 0;
            }
            
            .conversation-name {
                font-weight: 600;
                font-size: 14px;
                color: #1f2937;
                margin-bottom: 4px;
            }
            
            .conversation-preview {
                font-size: 13px;
                color: #6b7280;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            
            .conversation-time {
                font-size: 11px;
                color: #9ca3af;
                white-space: nowrap;
            }
            
            .conversation-unread {
                background: #ef4444;
                color: white;
                border-radius: 50%;
                width: 20px;
                height: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 11px;
                font-weight: bold;
            }
            
            /* ===== CHAT VIEW ===== */
            .chat-view {
                flex: 1;
                display: flex;
                flex-direction: column;
                overflow: hidden;
                min-height: 0;
            }
            
            .chat-view-header {
                padding: 16px 20px;
                border-bottom: 1px solid #e5e7eb;
                display: flex;
                align-items: center;
                gap: 12px;
                background: #f9fafb;
                flex-shrink: 0;
            }
            
            .btn-back {
                background: transparent;
                border: none;
                color: #6b7280;
                cursor: pointer;
                padding: 8px 12px;
                border-radius: 8px;
                transition: all 0.2s;
            }
            
            .btn-back:hover {
                background: #e5e7eb;
                color: #1f2937;
            }
            
            .user-info strong {
                font-size: 15px;
                color: #1f2937;
            }
            
            .chat-messages {
                flex: 1;
                padding: 20px 30px 20px 20px;
                overflow-y: auto;
                overflow-x: visible;
                background: #f5f7fa;
                min-height: 0;
                scroll-behavior: smooth;
                position: relative;
                -ms-overflow-style: none;
                scrollbar-width: none;
            }
            
            .chat-messages::-webkit-scrollbar {
                display: none;
            }
            
            .chat-message {
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
                justify-content: flex-start;
            }
            
            .chat-message.manager {
                justify-content: flex-end;
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
            
            .chat-message.user .chat-message-content {
                background: white;
                color: #2d3748;
                border: 1px solid #e2e8f0;
                border-bottom-left-radius: 6px;
            }
            
            .chat-message.manager .chat-message-content {
                background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
                color: white;
                border-bottom-right-radius: 6px;
                box-shadow: 0 3px 10px rgba(59, 130, 246, 0.4);
            }
            
            .chat-message-time {
                font-size: 11px;
                opacity: 0.75;
                margin-top: 6px;
                font-weight: 500;
            }
            
            .chat-input {
                padding: 18px 20px;
                border-top: 2px solid #e5e7eb;
                background: white;
                box-shadow: 0 -3px 15px rgba(0,0,0,0.08);
                flex-shrink: 0;
            }
            
            .chat-input input {
                border-radius: 24px;
                border: 2px solid #e5e7eb;
                padding: 12px 20px;
                font-size: 15px;
                transition: all 0.2s;
                background: #f9fafb;
            }
            
            .chat-input input:focus {
                border-color: #3b82f6;
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
                outline: none;
                background: white;
            }
            
            .chat-input input::placeholder {
                color: #9ca3af;
                font-size: 14px;
            }
            
            .chat-input button {
                border-radius: 50%;
                width: 44px;
                height: 44px;
                display: flex;
                align-items: center;
                justify-content: center;
                background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
                border: none;
                box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
                transition: all 0.2s;
                cursor: pointer;
            }
            
            .chat-input button:hover {
                transform: scale(1.1);
                box-shadow: 0 6px 20px rgba(59, 130, 246, 0.6);
            }
            
            .chat-input button:active {
                transform: scale(0.95);
            }
            
            .chat-input button i {
                font-size: 16px;
                color: white;
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
            
            .chat-message.user .message-actions {
                right: -45px;
            }
            
            .chat-message.manager .message-actions {
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
                color: #3b82f6;
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
                border-left: 3px solid #3b82f6;
                border-radius: 6px;
            }
            
            .reply-content i.fa-reply {
                color: #3b82f6;
                margin-top: 2px;
            }
            
            .reply-content strong {
                font-size: 13px;
                color: #3b82f6;
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
                border-left: 3px solid #3b82f6;
                padding: 8px 12px;
                border-radius: 6px;
                margin-bottom: 8px;
                font-size: 13px;
            }
            
            .replied-message strong {
                color: #3b82f6;
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
            .scroll-to-bottom {
                position: absolute;
                bottom: 80px;
                right: 30px;
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
                border-radius: 50%;
                display: none;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
                z-index: 100;
                transition: all 0.3s;
                animation: bounceIn 0.5s;
            }
            
            .scroll-to-bottom:hover {
                transform: scale(1.1);
                box-shadow: 0 6px 20px rgba(59, 130, 246, 0.6);
            }
            
            .scroll-to-bottom i {
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
    
    const $container = $('#manager-chat-icon-container');
    if ($container.length > 0) {
        $container.css('position', 'relative');
        $container.append(widgetHTML);
        console.log('✅ Manager Chat Widget attached');
    }
}

// ============= TOGGLE WIDGET =============

function toggleManagerChat() {
    const $widget = $('#managerChatWidget');
    
    if (managerChat.isOpen) {
        $widget.fadeOut(200);
        managerChat.isOpen = false;
    } else {
        $widget.fadeIn(200);
        managerChat.isOpen = true;
        
        // Load conversations on first open
        if (!managerChat.selectedConversation) {
            loadConversations();
        }
    }
}

window.toggleManagerChat = toggleManagerChat;

// ============= EVENT LISTENERS =============

function setupEventListeners() {
    // Click icon to toggle
    $('#manager-chat-icon-link').on('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        toggleManagerChat();
    });
    
    // Send message form
    $('#managerChatForm').on('submit', function(e) {
        e.preventDefault();
        const content = $('#managerChatInput').val();
        if (content.trim()) {
            sendManagerMessage(content);
            $('#managerChatInput').val('');
        }
    });
    
    // Search conversations
    $('#searchConversations').on('input', function() {
        const query = $(this).val().toLowerCase();
        filterConversations(query);
    });
    
    // Close when clicking outside
    $(document).on('click', function(e) {
        const $widget = $('#managerChatWidget');
        const $icon = $('#manager-chat-icon-link');
        
        if (managerChat.isOpen && 
            !$widget.is(e.target) && 
            $widget.has(e.target).length === 0 &&
            !$icon.is(e.target) &&
            $icon.has(e.target).length === 0) {
            toggleManagerChat();
        }
    });
    
    $(document).on('click', '#managerChatWidget', function(e) {
        e.stopPropagation();
    });
}

// ============= API CALLS =============

function loadConversations() {
    console.log('📥 Loading conversations...');
    
    $.ajax({
        url: '/api/chat/manager/conversations',
        method: 'GET',
        success: function(response) {
            if (response.success) {
                managerChat.conversations = response.data;
                displayConversations(response.data);
                console.log('✅ Loaded', response.data.length, 'conversations');
            }
        },
        error: function(xhr) {
            console.error('❌ Error loading conversations:', xhr.responseText);
            $('#conversationsContainer').html(`
                <div class="text-center py-4 text-danger">
                    <i class="fa fa-exclamation-triangle fa-2x mb-2"></i>
                    <p>Không thể tải tin nhắn</p>
                </div>
            `);
        }
    });
}

function loadConversationMessages(conversationId) {
    console.log('📥 Loading messages for conversation:', conversationId);
    
    $.ajax({
        url: `/api/chat/messages/${conversationId}/all`,
        method: 'GET',
        success: function(response) {
            if (response.success) {
                displayMessages(response.data);
                console.log('✅ Loaded', response.data.length, 'messages');
            }
        },
        error: function(xhr) {
            console.error('❌ Error loading messages');
        }
    });
}

// ============= UI UPDATES =============

function displayConversations(conversations) {
    const $container = $('#conversationsContainer');
    $container.empty();
    
    if (!conversations || conversations.length === 0) {
        $container.html(`
            <div class="text-center py-5 text-muted">
                <i class="fa fa-inbox fa-3x mb-3 opacity-25"></i>
                <p>Chưa có cuộc trò chuyện nào</p>
            </div>
        `);
        $('#conversationCount').text('0 cuộc trò chuyện');
        return;
    }
    
    // Sort by last message time
    conversations.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
    
    conversations.forEach(conv => {
        const initials = getInitials(conv.userName);
        const time = formatTimeAgo(conv.updatedAt);
        const preview = conv.lastMessage || 'Chưa có tin nhắn';
        const unreadBadge = conv.unreadCount > 0 ? `<span class="conversation-unread">${conv.unreadCount}</span>` : '';
        
        const html = `
            <div class="conversation-item" data-conversation-id="${conv.id}" onclick="selectConversation(${conv.id})">
                <div class="conversation-avatar">${initials}</div>
                <div class="conversation-info">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <div class="conversation-name">${escapeHtml(conv.userName)}</div>
                        <div class="conversation-time">${time}</div>
                    </div>
                    <div class="conversation-preview">${escapeHtml(preview)}</div>
                </div>
                ${unreadBadge}
            </div>
        `;
        
        $container.append(html);
    });
    
    $('#conversationCount').text(`${conversations.length} cuộc trò chuyện`);
}

function selectConversation(conversationId) {
    console.log('👤 Selected conversation:', conversationId);
    console.log('📋 Available conversations:', managerChat.conversations);
    
    const conversation = managerChat.conversations.find(c => c.id === conversationId);
    if (!conversation) {
        console.error('❌ Conversation not found:', conversationId);
        return;
    }
    
    console.log('✅ Found conversation:', conversation);
    managerChat.selectedConversation = conversation;
    
    // Update UI
    $('.conversation-item').removeClass('active');
    $(`.conversation-item[data-conversation-id="${conversationId}"]`).addClass('active');
    console.log('🎨 Updated conversation item active state');
    
    // Show chat view
    console.log('📋 About to show chat view');
    showChatView();
    
    // Update header
    $('#chatUserName').text(conversation.userName || 'Unknown User');
    $('#chatUserEmail').text(conversation.userEmail || 'No email');
    console.log('📝 Updated chat header');
    
    // Load messages
    loadMessages(conversation.id);
    
    // Mark conversation as read
    markConversationAsRead(conversation.id);
}

window.selectConversation = selectConversation;

function showChatView() {
    console.log('📋 Showing chat view');
    
    const $conversationsList = $('#conversationsList');
    const $chatView = $('#chatView');
    
    console.log('📋 conversationsList element:', $conversationsList.length);
    console.log('📋 chatView element:', $chatView.length);
    
    $conversationsList.hide();
    $chatView.show();
    
    console.log('📋 conversationsList visible:', $conversationsList.is(':visible'));
    console.log('📋 chatView visible:', $chatView.is(':visible'));
}

function showConversationsList() {
    console.log('📋 Showing conversations list');
    $('#chatView').hide();
    $('#conversationsList').show();
    managerChat.selectedConversation = null;
    $('#chatMessages').empty();
}

window.showConversationsList = showConversationsList;

function loadMessages(conversationId, retryCount = 0, forceReload = false) {
    console.log('📨 Loading messages for conversation:', conversationId, 'retry:', retryCount, 'forceReload:', forceReload);
    
    const $container = $('#chatMessages');
    
    // Check if already loading
    if (managerChat.loadingConversations.has(conversationId)) {
        console.log('⏳ Already loading conversation:', conversationId);
        return;
    }
    
    // Check cache first (unless force reload)
    if (!forceReload && managerChat.messagesCache.has(conversationId)) {
        console.log('💾 Using cached messages for conversation:', conversationId);
        const cachedMessages = managerChat.messagesCache.get(conversationId);
        displayMessages(cachedMessages);
        return;
    }
    
    // Mark as loading
    managerChat.loadingConversations.add(conversationId);
    
    // Show loading immediately
    $container.html(`
        <div class="text-center py-4 text-muted">
            <i class="fa fa-spinner fa-spin fa-2x mb-2"></i>
            <p>Đang tải tin nhắn...</p>
        </div>
    `);
    
    // Call API to load messages with timeout
    $.ajax({
        url: `/api/chat/conversation/${conversationId}/messages`,
        method: 'GET',
        timeout: 8000, // 8 second timeout
        cache: false
    })
    .done(function(response) {
        console.log('📨 API Response:', response);
        
        // Remove from loading set
        managerChat.loadingConversations.delete(conversationId);
        
        if (response.success && response.data) {
            // Cache the messages
            managerChat.messagesCache.set(conversationId, response.data);
            
            displayMessages(response.data);
            console.log('✅ Messages loaded and cached:', response.data.length);
            
            // Scroll to bottom after loading
            setTimeout(scrollToBottom, 100);
        } else {
            console.warn('⚠️ No messages in response');
            // Cache empty array
            managerChat.messagesCache.set(conversationId, []);
            
            $container.html(`
                <div class="text-center py-4 text-muted">
                    <i class="fa fa-comments fa-3x mb-2 opacity-25"></i>
                    <p>Chưa có tin nhắn</p>
                    <small class="text-muted">Hãy bắt đầu cuộc trò chuyện!</small>
                </div>
            `);
        }
    })
    .fail(function(xhr, status, error) {
        console.error('❌ Failed to load messages:', {
            status: xhr.status,
            statusText: xhr.statusText,
            error: error,
            response: xhr.responseText
        });
        
        // Remove from loading set
        managerChat.loadingConversations.delete(conversationId);
        
        // Retry logic for network errors
        if (retryCount < 2 && (xhr.status === 0 || xhr.status >= 500)) {
            console.log('🔄 Retrying in 2 seconds...');
            setTimeout(() => {
                loadMessages(conversationId, retryCount + 1, forceReload);
            }, 2000);
            return;
        }
        
        // Show error with retry button
        $container.html(`
            <div class="text-center py-4 text-danger">
                <i class="fa fa-exclamation-triangle fa-2x mb-2"></i>
                <p>Lỗi tải tin nhắn</p>
                <small class="text-muted d-block mb-2">
                    ${xhr.status === 0 ? 'Không thể kết nối server' : 
                      xhr.status === 404 ? 'Cuộc trò chuyện không tồn tại' :
                      xhr.status >= 500 ? 'Lỗi server' : 'Lỗi không xác định'}
                </small>
                <button class="btn btn-sm btn-outline-primary" onclick="loadMessages(${conversationId}, 0, true)">
                    <i class="fa fa-refresh"></i> Thử lại
                </button>
            </div>
        `);
    });
}

function displayMessages(messages) {
    console.log('🎨 Displaying messages:', messages.length);
    const $container = $('#chatMessages');
    
    if (!messages || messages.length === 0) {
        $container.html(`
            <div class="text-center py-4 text-muted">
                <i class="fa fa-comments fa-3x mb-2 opacity-25"></i>
                <p>Chưa có tin nhắn</p>
                <small class="text-muted">Hãy bắt đầu cuộc trò chuyện!</small>
            </div>
        `);
        return;
    }
    
    // Clear container
    $container.empty();
    
    // Sort by time
    messages.sort((a, b) => new Date(a.sentAt) - new Date(b.sentAt));
    
    // Build HTML string for better performance
    let htmlContent = '';
    let lastDate = null;
    
    messages.forEach(msg => {
        const msgDate = new Date(msg.sentAt).toLocaleDateString('vi-VN');
        
        // Add date separator if needed
        if (msgDate !== lastDate) {
            htmlContent += `
                <div class="chat-date-separator">
                    <span>${msgDate}</span>
                </div>
            `;
            lastDate = msgDate;
        }
        
        // Build message HTML
        const isManager = msg.senderType === 'MANAGER';
        const messageClass = isManager ? 'manager' : 'user';
        const time = formatTime(msg.sentAt);
        const messageId = msg.id || Date.now();
        
        // Replied message indicator
        let repliedHtml = '';
        if (msg.replyTo) {
            const replyToName = msg.replyTo.senderType === 'USER' ? 
                managerChat.selectedConversation?.userName : 'Bạn';
            repliedHtml = `
                <div class="replied-message">
                    <strong>↩ ${replyToName}</strong>
                    <p>${escapeHtml(msg.replyTo.content)}</p>
                </div>
            `;
        }
        
        // Message actions (only for user messages)
        const actionsHtml = !isManager ? `
            <div class="message-actions">
                <button class="message-action-btn" onclick="replyToMessage(${messageId}, '${escapeHtml(msg.content).replace(/'/g, "\\'")}', '${msg.senderType}')" title="Trả lời">
                    <i class="fa fa-reply"></i>
                </button>
            </div>
        ` : '';
        
        htmlContent += `
            <div class="chat-message ${messageClass}" data-message-id="${messageId}">
                <div class="chat-message-content">
                    ${repliedHtml}
                    ${escapeHtml(msg.content)}
                    <div class="chat-message-time">${time}</div>
                </div>
                ${actionsHtml}
            </div>
        `;
    });
    
    // Set all HTML at once for better performance
    $container.html(htmlContent);
    
    console.log('✅ Messages displayed successfully');
    
    // Scroll to bottom
    setTimeout(scrollToBottom, 50);
}

function appendMessage(message, animate = true) {
    const $container = $('#chatMessages');
    $container.find('.text-center').remove();
    
    const isManager = message.senderType === 'MANAGER';
    const messageClass = isManager ? 'manager' : 'user';
    const time = formatTime(message.sentAt);
    const messageId = message.id || Date.now();
    
    // Replied message indicator
    let repliedHtml = '';
    if (message.replyTo) {
        const replyToName = message.replyTo.senderType === 'USER' ? 
            managerChat.selectedConversation?.userName : 'Bạn';
        repliedHtml = `
            <div class="replied-message">
                <strong>↩ ${replyToName}</strong>
                <p>${escapeHtml(message.replyTo.content)}</p>
            </div>
        `;
    }
    
    // Message actions (only for user messages)
    const actionsHtml = !isManager ? `
        <div class="message-actions">
            <button class="message-action-btn" onclick="replyToMessage(${messageId}, '${escapeHtml(message.content).replace(/'/g, "\\'")}', '${message.senderType}')" title="Trả lời">
                <i class="fa fa-reply"></i>
            </button>
        </div>
    ` : '';
    
    console.log('📝 Appending message:', {
        isManager: isManager,
        hasActions: actionsHtml.length > 0,
        messageId: messageId
    });
    
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

function filterConversations(query) {
    if (!query) {
        $('.conversation-item').show();
        return;
    }
    
    $('.conversation-item').each(function() {
        const name = $(this).find('.conversation-name').text().toLowerCase();
        const preview = $(this).find('.conversation-preview').text().toLowerCase();
        
        if (name.includes(query) || preview.includes(query)) {
            $(this).show();
        } else {
            $(this).hide();
        }
    });
}

// ============= MESSAGE SENDING =============

function sendManagerMessage(content) {
    if (!content.trim() || !managerChat.stompClient || !managerChat.isConnected) {
        console.warn('⚠️ Cannot send message');
        return;
    }
    
    if (!managerChat.selectedConversation) {
        console.error('❌ No conversation selected');
        return;
    }
    
    const message = {
        senderId: currentManagerId,  // ← THÊM senderId
        conversationId: managerChat.selectedConversation.id,
        content: content.trim(),
        senderType: 'MANAGER'
    };
    
    // Add reply info if replying
    if (managerChat.replyingTo) {
        message.replyToId = managerChat.replyingTo.id;
    }
    
    // Display immediately
    const uiMessage = {
        senderType: 'MANAGER',
        content: content.trim(),
        sentAt: new Date().toISOString(),
        replyTo: managerChat.replyingTo
    };
    
    appendMessage(uiMessage, true);
    
    // Send via WebSocket
    managerChat.stompClient.send('/app/chat.sendManager', {}, JSON.stringify(message));
    console.log('✅ Manager message sent');
    
    // Clear reply state
    if (managerChat.replyingTo) {
        cancelReply();
    }
}

// Reply to message
function replyToMessage(messageId, content, senderType) {
    const userName = managerChat.selectedConversation?.userName || 'User';
    const senderName = senderType === 'USER' ? userName : 'Bạn';
    
    managerChat.replyingTo = {
        id: messageId,
        content: content,
        senderType: senderType
    };
    
    // Show reply preview
    $('#replyToName').text(senderName);
    $('#replyToContent').text(content);
    $('#replyPreview').slideDown(200);
    
    // Focus input
    $('#managerChatInput').focus();
    
    console.log('📝 Replying to message:', messageId);
}

window.replyToMessage = replyToMessage;

// Cancel reply
function cancelReply() {
    managerChat.replyingTo = null;
    $('#replyPreview').slideUp(200);
}

window.cancelReply = cancelReply;

function markConversationAsRead(conversationId) {
    if (managerChat.stompClient && managerChat.isConnected) {
        managerChat.stompClient.send('/app/chat.markReadManager', {}, conversationId.toString());
    }
}

// ============= WEBSOCKET =============

function connectWebSocket() {
    const socket = new SockJS('/ws');
    managerChat.stompClient = Stomp.over(socket);
    
    managerChat.stompClient.connect({}, 
        function(frame) {
            console.log('✅ Manager WebSocket connected');
            managerChat.isConnected = true;
            
            // Subscribe to manager notifications
            managerChat.stompClient.subscribe('/topic/chat.managers', function(message) {
                const notification = JSON.parse(message.body);
                handleManagerNotification(notification);
            });
        },
        function(error) {
            console.error('❌ WebSocket error:', error);
            managerChat.isConnected = false;
        }
    );
}

function handleManagerNotification(notification) {
    console.log('📬 Manager notification received:', notification.type);
    
    if (notification.type === 'NEW_MESSAGE') {
        const message = notification.message;
        console.log('💬 New message in conversation:', message.conversationId);
        
        // Update conversations list
        loadConversations();
        
        // Update unread badge if widget is closed or different conversation
        if (!managerChat.isOpen || 
            !managerChat.selectedConversation || 
            managerChat.selectedConversation.id !== message.conversationId) {
            
            // Check unread count from server
            checkUnreadConversations();
        }
        
        // Update cache with new message
        if (managerChat.messagesCache.has(message.conversationId)) {
            const cachedMessages = managerChat.messagesCache.get(message.conversationId);
            cachedMessages.push(message);
            managerChat.messagesCache.set(message.conversationId, cachedMessages);
        }
        
        // If this conversation is currently open, append message
        if (managerChat.selectedConversation && 
            managerChat.selectedConversation.id === message.conversationId) {
            
            // Only append if message is from USER (manager messages already appended)
            if (message.senderType === 'USER') {
                appendMessage(message, true);
                
                // Mark as read if widget is open
                if (managerChat.isOpen) {
                    markConversationAsRead(message.conversationId);
                }
            }
        }
        
        // Update badge
        if (message.senderType === 'USER') {
            managerChat.unreadCount++;
            updateBadge();
        }
    }
}

function updateBadge() {
    const $badge = $('#manager-chat-unread-badge');
    if (managerChat.unreadCount > 0) {
        $badge.text(managerChat.unreadCount).show();
    } else {
        $badge.hide();
    }
}

// ============= UTILITY FUNCTIONS =============

function getInitials(name) {
    if (!name) return '?';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
        return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
}

function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

function formatTimeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);
    const diff = Math.floor((now - date) / 1000); // seconds
    
    if (diff < 60) return 'Vừa xong';
    if (diff < 3600) return Math.floor(diff / 60) + ' phút';
    if (diff < 86400) return Math.floor(diff / 3600) + ' giờ';
    if (diff < 604800) return Math.floor(diff / 86400) + ' ngày';
    
    return date.toLocaleDateString('vi-VN');
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function scrollToBottom() {
    const $container = $('#chatMessages');
    $container.animate({ 
        scrollTop: $container[0].scrollHeight 
    }, 400, 'swing'); // Smooth easing
    
    // Hide scroll button
    $('#scrollToBottomBtn').fadeOut(200);
}

// Check scroll position and show/hide button
function checkScrollPosition() {
    const $container = $('#chatMessages');
    if (!$container.length) return;
    
    const scrollTop = $container.scrollTop();
    const scrollHeight = $container[0].scrollHeight;
    const clientHeight = $container[0].clientHeight;
    
    // Show button if not at bottom
    if (scrollHeight - scrollTop - clientHeight > 100) {
        $('#scrollToBottomBtn').fadeIn(200);
    } else {
        $('#scrollToBottomBtn').fadeOut(200);
    }
}

// Attach scroll listener
$(document).on('scroll', '#chatMessages', checkScrollPosition);

// ============= UNREAD BADGE MANAGEMENT =============

// Update unread badge
function updateUnreadBadge(count) {
    const $badge = $('#manager-chat-unread-count');
    
    if (count > 0) {
        $badge.text(count).css('display', 'flex');
        console.log('📬 Manager unread badge shown:', count);
    } else {
        $badge.hide();
        console.log('✅ Manager unread badge hidden');
    }
}

// Check for unread conversations from server
function checkUnreadConversations() {
    $.get('/api/chat/manager/unread-count')
        .done(function(response) {
            if (response.success) {
                const count = response.count || 0;
                updateUnreadBadge(count);
            }
        })
        .fail(function() {
            console.warn('⚠️ Failed to check unread conversations');
        });
}

// Mark conversation as read
function markConversationAsRead(conversationId) {
    if (managerChat.stompClient && managerChat.isConnected) {
        managerChat.stompClient.send('/app/chat.markReadManager', {}, JSON.stringify(conversationId));
        console.log('✅ Marked conversation as read:', conversationId);
        
        // Update badge after marking as read
        setTimeout(checkUnreadConversations, 500);
    }
}

// Initialize unread check on page load
$(document).ready(function() {
    if (typeof currentManagerId !== 'undefined' && currentManagerId > 0) {
        // Check unread count after 1 second
        setTimeout(checkUnreadConversations, 1000);
        
        // Check periodically every 30 seconds
        setInterval(checkUnreadConversations, 30000);
    }
});

// Clear messages cache for a conversation
function clearMessagesCache(conversationId) {
    if (conversationId) {
        managerChat.messagesCache.delete(conversationId);
        console.log('🗑️ Cleared cache for conversation:', conversationId);
    } else {
        managerChat.messagesCache.clear();
        console.log('🗑️ Cleared all messages cache');
    }
}

// Debug function to test elements
window.debugManagerChat = function() {
    console.log('🔍 Manager Chat Debug:');
    console.log('- Widget:', $('#managerChatWidget').length);
    console.log('- Conversations List:', $('#conversationsList').length);
    console.log('- Chat View:', $('#chatView').length);
    console.log('- Chat Messages:', $('#chatMessages').length);
    console.log('- Chat User Name:', $('#chatUserName').length);
    console.log('- Chat User Email:', $('#chatUserEmail').length);
    console.log('- Manager Chat Object:', managerChat);
    console.log('- Current conversations:', managerChat.conversations);
    console.log('- Messages cache size:', managerChat.messagesCache.size);
    console.log('- Loading conversations:', managerChat.loadingConversations);
};

// Expose functions for debugging
window.clearMessagesCache = clearMessagesCache;
window.loadMessages = loadMessages;

console.log('✅ Manager Chat Widget loaded');
console.log('🔧 Run debugManagerChat() in console to check elements');

