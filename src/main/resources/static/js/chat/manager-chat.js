/**
 * Manager Chat WebSocket Client
 * Quản lý tất cả conversations từ users
 */

let stompClient = null;
let isConnected = false;
let currentConversationId = null;
let conversations = [];
let currentFilter = 'all';

// ============= INITIALIZATION =============

$(document).ready(function() {
    console.log('🚀 Initializing manager chat...');
    console.log('Manager ID:', currentManagerId);
    
    // Load conversations
    loadConversations();
    
    // Connect WebSocket
    connectWebSocket();
    
    // Setup event listeners
    setupEventListeners();
    
    // Auto-refresh conversations every 30 seconds
    setInterval(loadConversations, 30000);
});

// ============= WEBSOCKET CONNECTION =============

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, 
        function(frame) {
            console.log('✅ WebSocket connected:', frame);
            isConnected = true;
            updateConnectionStatus(true);
            
            // Subscribe to manager topic to receive all user messages
            stompClient.subscribe('/topic/chat.managers', function(message) {
                const notification = JSON.parse(message.body);
                handleNotification(notification);
            });
            
            console.log('📨 Subscribed to /topic/chat.managers');
        },
        function(error) {
            console.error('❌ WebSocket connection error:', error);
            isConnected = false;
            updateConnectionStatus(false);
            
            // Retry connection after 5 seconds
            setTimeout(connectWebSocket, 5000);
        }
    );
}

// ============= API CALLS =============

function loadConversations(showUnreadOnly = false) {
    const url = '/api/chat/manager/conversations?unreadOnly=' + (showUnreadOnly || currentFilter === 'unread');
    
    $.ajax({
        url: url,
        method: 'GET',
        success: function(response) {
            if (response.success) {
                conversations = response.data;
                displayConversations(conversations);
                updateUnreadCount(response.unreadCount);
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error loading conversations:', error);
        }
    });
}

function loadConversation(conversationId) {
    $.ajax({
        url: '/api/chat/manager/conversation/' + conversationId,
        method: 'GET',
        success: function(response) {
            if (response.success) {
                displayConversationDetails(response.data);
                currentConversationId = conversationId;
                
                // Mark messages as read
                markMessagesAsRead(conversationId);
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error loading conversation:', error);
        }
    });
}

function markMessagesAsRead(conversationId) {
    if (stompClient && isConnected) {
        stompClient.send('/app/chat.markReadManager', {}, conversationId);
    }
}

function closeConversation() {
    if (!currentConversationId) return;
    
    if (!confirm('Bạn có chắc muốn đóng cuộc trò chuyện này?')) {
        return;
    }
    
    $.ajax({
        url: '/api/chat/manager/conversation/' + currentConversationId + '/close',
        method: 'PUT',
        success: function(response) {
            if (response.success) {
                alert('Đã đóng cuộc trò chuyện');
                currentConversationId = null;
                $('#noChatSelected').show();
                $('#chatContent').hide();
                loadConversations();
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error closing conversation:', error);
            alert('Không thể đóng cuộc trò chuyện');
        }
    });
}

// ============= MESSAGE HANDLING =============

function sendMessage(content) {
    if (!content.trim() || !currentConversationId) {
        return;
    }
    
    if (!stompClient || !isConnected) {
        alert('Chưa kết nối WebSocket. Vui lòng đợi...');
        return;
    }
    
    const message = {
        senderId: currentManagerId,
        conversationId: currentConversationId,
        senderType: 'MANAGER',
        content: content.trim()
    };
    
    console.log('📤 Sending message:', message);
    
    stompClient.send('/app/chat.sendManager', {}, JSON.stringify(message));
}

function handleNotification(notification) {
    console.log('📬 Received notification:', notification);
    
    switch (notification.type) {
        case 'NEW_MESSAGE':
            const message = notification.message;
            
            // If message is in current conversation, append it
            if (message.conversationId === currentConversationId) {
                appendMessage(message);
                
                // Mark as read
                markMessagesAsRead(currentConversationId);
            }
            
            // Update conversation list
            loadConversations();
            
            // Show browser notification if not in focus
            if (!document.hasFocus()) {
                showBrowserNotification(message);
            }
            break;
            
        case 'MESSAGE_READ':
            // User đã đọc tin nhắn
            console.log('✅ User read messages in conversation', notification.conversationId);
            break;
    }
}

// ============= UI UPDATES =============

function displayConversations(conversations) {
    const $list = $('#conversationsList');
    $list.empty();
    
    if (conversations.length === 0) {
        $list.html(`
            <div class="empty-state">
                <i class="fas fa-inbox"></i>
                <p>Chưa có cuộc trò chuyện nào</p>
            </div>
        `);
        return;
    }
    
    conversations.forEach(conv => {
        const unreadClass = conv.unreadCount > 0 ? 'unread' : '';
        const activeClass = conv.id === currentConversationId ? 'active' : '';
        const unreadBadge = conv.unreadCount > 0 ? 
            `<span class="unread-badge">${conv.unreadCount}</span>` : '';
        
        const lastMessagePreview = conv.lastMessage ? 
            escapeHtml(conv.lastMessage.content) : 'Chưa có tin nhắn';
        
        const time = conv.lastMessageAt ? formatTime(conv.lastMessageAt) : '';
        
        const html = `
            <div class="conversation-item ${unreadClass} ${activeClass}" 
                 data-id="${conv.id}"
                 onclick="selectConversation(${conv.id})">
                <div class="conversation-info">
                    <div class="conversation-name">
                        ${escapeHtml(conv.userName)}
                        ${unreadBadge}
                    </div>
                    <div class="conversation-time">${time}</div>
                </div>
                <div class="conversation-preview">${lastMessagePreview}</div>
            </div>
        `;
        
        $list.append(html);
    });
}

function selectConversation(conversationId) {
    currentConversationId = conversationId;
    
    // Update active state
    $('.conversation-item').removeClass('active');
    $(`.conversation-item[data-id="${conversationId}"]`).addClass('active');
    
    // Load conversation details
    loadConversation(conversationId);
}

function displayConversationDetails(conversation) {
    // Show chat content
    $('#noChatSelected').hide();
    $('#chatContent').css('display', 'flex');
    
    // Update header
    $('#chatUserName').text(conversation.userName);
    $('#chatUserEmail').text(conversation.userEmail);
    
    // Display messages
    displayMessages(conversation.messages);
}

function displayMessages(messages) {
    const $chatMessages = $('#chatMessages');
    $chatMessages.empty();
    
    if (messages.length === 0) {
        $chatMessages.html(`
            <div class="empty-state">
                <i class="fas fa-comments"></i>
                <p>Chưa có tin nhắn nào</p>
            </div>
        `);
        return;
    }
    
    messages.forEach(message => {
        appendMessage(message, false);
    });
    
    scrollToBottom();
}

function appendMessage(message, animate = true) {
    const $chatMessages = $('#chatMessages');
    
    // Remove empty state if exists
    $chatMessages.find('.empty-state').remove();
    
    const isManager = message.senderType === 'MANAGER';
    const messageClass = isManager ? 'message-manager' : 'message-user';
    const time = formatTime(message.sentAt);
    
    const messageHtml = `
        <div class="message ${messageClass}" style="${animate ? 'animation: slideIn 0.3s ease;' : ''}">
            <div class="message-content">
                <div class="message-sender">${escapeHtml(message.senderName)}</div>
                <div>${escapeHtml(message.content)}</div>
                <div class="message-time">${time}</div>
            </div>
        </div>
    `;
    
    $chatMessages.append(messageHtml);
    
    if (animate) {
        scrollToBottom();
    }
}

function scrollToBottom() {
    const $chatMessages = $('#chatMessages');
    $chatMessages.animate({ 
        scrollTop: $chatMessages[0].scrollHeight 
    }, 300);
}

function updateUnreadCount(count) {
    $('#unreadCount').text(count);
}

function updateConnectionStatus(connected) {
    const $status = $('#connectionStatus');
    
    if (connected) {
        $status.addClass('connected');
        $status.html('<i class="fas fa-check-circle"></i> Đã kết nối');
        
        setTimeout(() => {
            $status.fadeOut();
        }, 2000);
    } else {
        $status.removeClass('connected');
        $status.html('<i class="fas fa-exclamation-triangle"></i> Mất kết nối. Đang thử kết nối lại...');
        $status.show();
    }
}

function showBrowserNotification(message) {
    if ('Notification' in window && Notification.permission === 'granted') {
        new Notification('Tin nhắn mới từ ' + message.senderName, {
            body: message.content,
            icon: '/img/logo.png'
        });
    }
}

// ============= EVENT LISTENERS =============

function setupEventListeners() {
    // Send message form
    $('#chatForm').on('submit', function(e) {
        e.preventDefault();
        
        const $input = $('#messageInput');
        const content = $input.val();
        
        sendMessage(content);
        
        // Clear input
        $input.val('');
    });
    
    // Filter buttons
    $('.filter-btn').on('click', function() {
        $('.filter-btn').removeClass('active');
        $(this).addClass('active');
        
        currentFilter = $(this).data('filter');
        loadConversations();
    });
    
    // Request notification permission
    if ('Notification' in window && Notification.permission === 'default') {
        Notification.requestPermission();
    }
}

// ============= UTILITY FUNCTIONS =============

function formatTime(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    
    const diffInMs = now - date;
    const diffInMinutes = Math.floor(diffInMs / 60000);
    
    if (diffInMinutes < 1) {
        return 'Vừa xong';
    } else if (diffInMinutes < 60) {
        return diffInMinutes + ' phút trước';
    } else if (diffInMinutes < 1440) {
        const hours = Math.floor(diffInMinutes / 60);
        return hours + ' giờ trước';
    } else {
        return date.toLocaleDateString('vi-VN') + ' ' + date.toLocaleTimeString('vi-VN', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    }
}

function escapeHtml(text) {
    if (!text) return '';
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
    if (stompClient) {
        stompClient.disconnect();
    }
});
