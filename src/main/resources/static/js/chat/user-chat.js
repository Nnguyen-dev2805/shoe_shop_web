/**
 * User Chat WebSocket Client
 * Quản lý kết nối WebSocket và gửi/nhận tin nhắn
 */

let stompClient = null;
let conversationId = null;
let isConnected = false;

// ============= INITIALIZATION =============

$(document).ready(function() {
    console.log('🚀 Initializing user chat...');
    console.log('Current User ID:', currentUserId);
    
    // Load conversation and messages
    loadConversation();
    
    // Connect WebSocket
    connectWebSocket();
    
    // Setup event listeners
    setupEventListeners();
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
            
            // Subscribe to personal queue for receiving messages from manager
            stompClient.subscribe('/queue/chat.user.' + currentUserId, function(message) {
                const notification = JSON.parse(message.body);
                handleNotification(notification);
            });
            
            console.log('📨 Subscribed to /queue/chat.user.' + currentUserId);
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

function loadConversation() {
    $.ajax({
        url: '/api/chat/conversation',
        method: 'GET',
        success: function(response) {
            if (response.success) {
                conversationId = response.data.id;
                console.log('✅ Conversation loaded:', conversationId);
                
                // Load messages
                loadMessages();
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error loading conversation:', error);
            showError('Không thể tải cuộc trò chuyện');
        }
    });
}

function loadMessages() {
    if (!conversationId) {
        console.warn('⚠️ No conversation ID');
        return;
    }
    
    $.ajax({
        url: '/api/chat/messages/' + conversationId + '/all',
        method: 'GET',
        success: function(response) {
            if (response.success) {
                displayMessages(response.data);
                
                // Mark manager messages as read
                markMessagesAsRead();
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ Error loading messages:', error);
        }
    });
}

function markMessagesAsRead() {
    if (stompClient && isConnected && conversationId) {
        stompClient.send('/app/chat.markReadUser', {}, conversationId);
    }
}

// ============= MESSAGE HANDLING =============

function sendMessage(content) {
    if (!content.trim()) {
        return;
    }
    
    if (!stompClient || !isConnected) {
        showError('Chưa kết nối WebSocket. Vui lòng đợi...');
        return;
    }
    
    const message = {
        senderId: currentUserId,
        senderType: 'USER',
        content: content.trim()
    };
    
    console.log('📤 Sending message:', message);
    
    stompClient.send('/app/chat.sendUser', {}, JSON.stringify(message));
}

function handleNotification(notification) {
    console.log('📬 Received notification:', notification);
    
    switch (notification.type) {
        case 'NEW_MESSAGE':
            const message = notification.message;
            if (message.senderType === 'MANAGER') {
                appendMessage(message);
                
                // Mark as read
                markMessagesAsRead();
            }
            break;
            
        case 'MESSAGE_READ':
            // Manager đã đọc tin nhắn
            console.log('✅ Messages marked as read');
            break;
            
        case 'USER_TYPING':
            // Show typing indicator (if from manager)
            break;
    }
}

// ============= UI UPDATES =============

function displayMessages(messages) {
    const $chatMessages = $('#chatMessages');
    $chatMessages.empty();
    
    if (messages.length === 0) {
        $chatMessages.html(`
            <div class="empty-chat">
                <i class="fas fa-comments"></i>
                <p>Chưa có tin nhắn nào. Hãy bắt đầu cuộc trò chuyện!</p>
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
    $chatMessages.find('.empty-chat').remove();
    
    const isUser = message.senderType === 'USER';
    const messageClass = isUser ? 'message-user' : 'message-shop';
    const time = formatTime(message.sentAt);
    
    const messageHtml = `
        <div class="message ${messageClass}" style="${animate ? 'animation: slideIn 0.3s ease;' : ''}">
            <div class="message-content">
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

function showError(message) {
    // You can use Bootstrap toast or alert
    alert(message);
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
    
    // Auto-scroll when new message arrives
    $('#chatMessages').on('DOMNodeInserted', function() {
        const $this = $(this);
        const scrollHeight = $this[0].scrollHeight;
        const scrollTop = $this.scrollTop();
        const clientHeight = $this[0].clientHeight;
        
        // Auto-scroll if user is near bottom
        if (scrollHeight - scrollTop - clientHeight < 100) {
            scrollToBottom();
        }
    });
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
