# 🔧 Admin Chat - Debug Guide

## ❌ Vấn đề: Admin không thấy conversations

**Screenshot shows:** Widget mở nhưng không có conversations

---

## 🔍 Debug Steps

### Bước 1: Check Console Logs

**Login Admin → Dashboard → Open Console (F12)**

**Expected logs (cần thấy ALL):**
```javascript
✅ "🔑 Admin Auth: ID = [số], Name = Admin"
✅ "📱 Manager Chat Widget: Initializing..."
✅ "🚀 Manager Chat: Init for manager: [số]"
✅ "✅ Manager Chat Widget ready"
```

**Nếu thấy:**
```
❌ "🔑 Admin Auth: ID = 0, Name = Admin"
→ session.admin KHÔNG được set!
```

**Fix:** Restart server (AdminHomeController.java đã set session.admin)

---

### Bước 2: Click Chat Icon

**Click icon → Check logs:**
```javascript
✅ "💬 Chat widget opened"
✅ "📥 Loading conversations..."
✅ "✅ Loaded X conversations"
```

**Nếu thấy:**
```
❌ "❌ Error loading conversations: ..."
→ API call failed!
```

---

### Bước 3: Manual API Test

**Run trong Console:**
```javascript
// Test API directly
$.get('/api/chat/manager/conversations', function(response) {
    console.log('✅ API Response:', response);
    console.log('✅ Conversations:', response.data);
}).fail(function(xhr) {
    console.log('❌ API Error:', xhr.status, xhr.responseText);
});
```

**Expected response:**
```json
{
    "success": true,
    "data": [
        {
            "id": 1,
            "userId": 2,
            "userName": "User",
            "userEmail": "user@example.com",
            "lastMessage": "Hello",
            "updatedAt": "2025-10-27T..."
        }
    ],
    "totalElements": 2,
    "unreadCount": 1
}
```

---

### Bước 4: Check Database

**Run SQL query:**
```sql
SELECT * FROM chat_conversations WHERE status = 'ACTIVE';
SELECT * FROM chat_messages ORDER BY sent_at DESC LIMIT 10;
```

**Should see:** Conversations with messages

---

## 🐛 Common Issues & Fixes

### Issue 1: currentManagerId = 0

**Symptoms:**
```
🔑 Admin Auth: ID = 0
```

**Cause:** session.admin not set

**Fix:**
1. Check `AdminHomeController.java`:
```java
session.setAttribute("admin", u);
```

2. **RESTART SERVER!** ⚠️

3. Login again

---

### Issue 2: jQuery Not Loaded

**Symptoms:**
```
❌ $ is not defined
❌ jQuery is not defined
```

**Cause:** Scripts load in wrong order

**Fix:** Check `dashboard.html` - jQuery MUST load before manager-chat-widget.js

**Correct order:**
```html
<head>
    <!-- jQuery first -->
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    
    <!-- Then WebSocket -->
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
    
    <!-- Then Manager Chat Widget -->
    <script src="/js/chat/manager-chat-widget.js"></script>
</head>
```

---

### Issue 3: Widget Not Created

**Symptoms:**
- Icon click → Nothing happens
- Console: "Manager Chat Widget: Initializing..." NOT shown

**Debug:**
```javascript
// Run in Console
$('#managerChatWidget').length
// Expected: 1 (widget exists)
// If 0: Widget not created!
```

**Fix:**
Check if manager-chat-widget.js is loaded:
```javascript
console.log('Manager chat object:', typeof managerChat);
// Expected: "object"
// If "undefined": Script not loaded!
```

---

### Issue 4: API Returns Empty

**Symptoms:**
```
✅ "✅ Loaded 0 conversations"
```

**Cause:** Database has no conversations

**Fix:** Send messages as User first!

**Test sequence:**
1. Login as User
2. Open user chat widget
3. Send message to shop
4. Login as Admin
5. Should see conversation!

---

## 🧪 Complete Test Flow

### 1. Prepare Data
```sql
-- Check conversations exist
SELECT * FROM chat_conversations;
-- Should see at least 1 row
```

### 2. Login Admin
- Go to `/admin`
- Should redirect to `/admin/dashboard`

### 3. Check Console
```
✅ Admin Auth: ID = [number > 0]
✅ Manager Chat Widget: Initializing...
✅ Manager Chat Widget ready
```

### 4. Click Chat Icon
- Icon in topbar (💬)
- Widget opens bottom-right
- Should show "Đang tải..." then conversations

### 5. Verify Conversations
```
✅ See user names
✅ See last messages
✅ See timestamps
✅ Unread badges if any
```

---

## 🔧 Manual Fixes

### Force Reload Conversations

**Run in Console:**
```javascript
// Force load
loadConversations();
```

### Check Widget State

**Run in Console:**
```javascript
debugManagerChat();
// Shows:
// - Widget element
// - Conversations array
// - Messages cache
// - Loading state
```

### Reset Everything

**Run in Console:**
```javascript
// Clear cache
managerChat.conversations = [];
managerChat.messagesCache.clear();

// Reload
loadConversations();
```

---

## 📊 Expected vs Actual

### Expected (Manager - Working):
```
1. Click icon
2. Widget opens
3. Shows conversations
4. Can click to chat
5. Messages load
```

### Actual (Admin - Not Working):
```
1. Click icon ✅
2. Widget opens ✅
3. Shows empty ❌ ← PROBLEM HERE
4. No conversations
5. Nothing to click
```

---

## 🎯 Root Cause Checklist

- [ ] Server restarted after code changes?
- [ ] Admin logged in (not just refreshed old session)?
- [ ] Console shows `currentManagerId > 0`?
- [ ] No JavaScript errors in Console?
- [ ] API `/api/chat/manager/conversations` returns data?
- [ ] Database has conversations?
- [ ] jQuery loaded before manager-chat-widget.js?
- [ ] Widget element `#managerChatWidget` exists?
- [ ] Event listener attached to `#manager-chat-icon-link`?

**If ALL checked ✅ → Should work!**

---

## 🚀 Quick Fix Attempt

**Run these in order:**

```javascript
// 1. Check manager ID
console.log('Manager ID:', currentManagerId);
// Must be > 0

// 2. Check widget
console.log('Widget:', $('#managerChatWidget').length);
// Must be 1

// 3. Check API
$.get('/api/chat/manager/conversations').done(r => console.log('API:', r));
// Must return { success: true, data: [...] }

// 4. Force load
if (typeof loadConversations === 'function') {
    loadConversations();
} else {
    console.error('loadConversations not defined!');
}
```

---

**Chạy debug steps trên và report kết quả!** 🔍
