# 💬 Admin Chat Integration - Manager Chat Widget for Admin

## ✨ Đã thêm chức năng chat cho Admin giống Manager

Admin giờ có thể chat với users giống hệt như Manager, sử dụng cùng Manager Chat Widget.

---

## 📁 Files Created/Modified

### 1. **Created: `fragments/admin/topbar.html`**

Fragment mới cho admin topbar với:
- ✅ Chat icon với unread badge
- ✅ Manager chat widget integration
- ✅ Reusable topbar component

**Fragments:**
- `topbar(pageTitle)` - Topbar với page title dynamic
- `chat-scripts` - Chat widget scripts và styles

### 2. **Modified: `admin/dashboard.html`**

**Before:**
```html
<!-- Inline topbar HTML -->
<header class="topbar">
    <!-- ... lots of HTML ... -->
</header>
```

**After:**
```html
<!-- Use fragment -->
<div th:replace="~{fragments/admin/topbar :: topbar('Thống Kê')}"></div>

<!-- At end of body -->
<div th:replace="~{fragments/admin/topbar :: chat-scripts}"></div>
```

---

## 🎯 How It Works

### Architecture:
```
Admin Page
├── topbar fragment (with chat icon)
├── panel_menu fragment (sidebar)
├── page content
└── chat-scripts fragment (manager widget)
    ├── currentManagerId = admin.id
    ├── manager-chat-widget.js
    └── custom styles for admin
```

### Key Features:
1. **Same widget as Manager:** Admin sử dụng `manager-chat-widget.js`
2. **Admin ID as Manager ID:** `currentManagerId = session.admin.id`
3. **Fixed position:** Widget ở bottom-right corner
4. **Unread badge:** Hiển thị số tin nhắn chưa đọc

---

## 🔧 Apply to Other Admin Pages

### Bước 1: Replace Topbar

**Find this in your admin page:**
```html
<header class="topbar">
    <div class="container-fluid">
        <!-- ... topbar content ... -->
    </div>
</header>
```

**Replace with:**
```html
<div th:replace="~{fragments/admin/topbar :: topbar('Your Page Title')}"></div>
```

### Bước 2: Add Chat Scripts

**At the end of `<body>` (before `</body>`):**
```html
<!-- Your existing scripts -->
<script th:src="@{/js/your-page.js}"></script>

<!-- Admin Chat Integration -->
<div th:replace="~{fragments/admin/topbar :: chat-scripts}"></div>

</body>
```

### Example for `product-list.html`:

**Before:**
```html
<header class="topbar">
    <!-- ... -->
    <h4 class="fw-bold">Danh Sách Sản Phẩm</h4>
    <!-- ... -->
</header>

<!-- ... page content ... -->

<script th:src="@{/js/admin/product.js}"></script>
</body>
```

**After:**
```html
<!-- Replace topbar -->
<div th:replace="~{fragments/admin/topbar :: topbar('Danh Sách Sản Phẩm')}"></div>

<!-- ... page content ... -->

<script th:src="@{/js/admin/product.js}"></script>

<!-- Add chat -->
<div th:replace="~{fragments/admin/topbar :: chat-scripts}"></div>
</body>
```

---

## 📋 Complete Integration Checklist

Apply to all admin pages:

### Core Pages:
- [x] ✅ `admin/dashboard.html` - **Done**
- [ ] `admin/index.html`
- [ ] `admin/settings.html`

### Product Pages:
- [ ] `admin/products/product-list.html`
- [ ] `admin/products/product-add.html`
- [ ] `admin/products/product-edit.html`
- [ ] `admin/products/product-details.html`

### Category Pages:
- [ ] `admin/categories/category-list.html`
- [ ] `admin/categories/category-add.html`
- [ ] `admin/categories/category-edit.html`

### Brand Pages:
- [ ] `admin/brand/brand-list.html`
- [ ] `admin/brand/brand-add.html`
- [ ] `admin/brand/brand-edit.html`

### Order Pages:
- [ ] `admin/order/orders-list.html`
- [ ] `admin/order/order-detail.html`

### Inventory Pages:
- [ ] `admin/inventory/inventory-list.html`
- [ ] `admin/inventory/inventory-add.html`
- [ ] `admin/inventory/inventory-edit.html`
- [ ] `admin/inventory/inventory-warehouse.html`
- [ ] `admin/inventory/inventory-received-orders.html`

### Discount Pages:
- [ ] `admin/discount/discount_list.html`
- [ ] `admin/discount/discount_add.html`
- [ ] `admin/discount/discount_edit.html`

### Flash Sale Pages:
- [ ] `admin/flashsale/flash_sale_list.html`
- [ ] `admin/flashsale/flash_sale_add.html`
- [ ] `admin/flashsale/flash_sale_edit.html`

### Shipping Pages:
- [ ] `admin/shippingCompany/shippingCompany-list.html`
- [ ] `admin/shippingCompany/shippingCompany-add.html`
- [ ] `admin/shippingCompany/shippingCompany-edit.html`

### Other Pages:
- [ ] `admin/permission/pages-permissions.html`
- [ ] `admin/shipping-rates.html`

---

## 🎨 Topbar Fragment Details

### Fragment: `topbar(pageTitle)`

**Parameters:**
- `pageTitle` - String - Page title hiển thị trong topbar

**Components:**
1. **Menu Toggle** - Hamburger menu button
2. **Page Title** - Dynamic title from parameter
3. **Light/Dark Mode** - Theme toggle
4. **Chat Icon** - With unread badge
5. **User Dropdown** - Profile, settings, logout

**Usage:**
```html
<!-- Static title -->
<div th:replace="~{fragments/admin/topbar :: topbar('Thống Kê')}"></div>

<!-- Dynamic title from model -->
<div th:replace="~{fragments/admin/topbar :: topbar(${pageTitle})}"></div>
```

### Fragment: `chat-scripts`

**Includes:**
1. **Manager ID Setup**
   ```javascript
   var currentManagerId = session.admin.id;
   ```

2. **Manager Chat Widget**
   ```html
   <script src="/js/chat/manager-chat-widget.js"></script>
   ```

3. **Custom Styles**
   ```css
   .manager-chat-widget {
       position: fixed !important;
       right: 20px !important;
       bottom: 20px !important;
   }
   ```

---

## 🔐 Backend Requirements

### Admin Role Check:

Admin cần có quyền access chat API endpoints giống Manager:

**ChatApiController.java:**
```java
@GetMapping("/manager/conversations")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public ResponseEntity<?> getAllConversations(...) {
    // Both manager and admin can access
}
```

### Session Admin Check:

**In Controller:**
```java
@GetMapping("/admin/dashboard")
public String dashboard(Model model, HttpSession session) {
    Users admin = (Users) session.getAttribute("admin");
    model.addAttribute("admin", admin);
    return "admin/dashboard";
}
```

---

## 🎯 Features

### For Admin:
✅ **Same chat widget** as Manager
✅ **View all conversations** with users
✅ **Reply to messages** in real-time
✅ **Unread badge** in topbar
✅ **Fixed position** bottom-right
✅ **Responsive** on mobile

### Differences from Manager:
- ❌ No difference in functionality
- ✅ Widget position: **Fixed** (admin) vs **Absolute** (manager)
- ✅ Icon location: **Topbar** (admin) vs **Sidebar** (manager)

---

## 📱 UI/UX

### Chat Icon (Topbar):
```html
<button onclick="toggleManagerChat()">
    <iconify-icon icon="solar:chat-dots-bold-duotone"></iconify-icon>
    <span id="admin-chat-unread-count" class="badge bg-danger">3</span>
</button>
```

### Widget Position:
```css
.manager-chat-widget {
    position: fixed !important;  /* Always visible */
    right: 20px !important;      /* From right edge */
    bottom: 20px !important;     /* From bottom edge */
}
```

### Responsive:
```css
@media (max-width: 768px) {
    .manager-chat-widget {
        width: 90vw !important;
        max-width: 420px !important;
    }
}
```

---

## 🧪 Testing

### Test Admin Chat:

1. **Login as Admin**
2. **Go to Dashboard** (or any admin page)
3. **Check topbar** → Should see chat icon
4. **Click chat icon** → Widget opens bottom-right
5. **View conversations** → Should see all user conversations
6. **Send message** → User receives message
7. **Check unread badge** → Updates real-time

### Expected Behavior:
- ✅ Chat icon visible in topbar
- ✅ Widget opens/closes on click
- ✅ Shows all user conversations
- ✅ Can send/receive messages
- ✅ Unread badge updates
- ✅ Widget persists across pages
- ✅ Mobile responsive

---

## 🔧 Quick Apply Script

**For bulk update, use Find & Replace:**

### Step 1: Find Topbar
```regex
<header class="topbar">[\s\S]*?</header>
```

### Step 2: Replace With
```html
<div th:replace="~{fragments/admin/topbar :: topbar('CHANGE_PAGE_TITLE')}"></div>
```

### Step 3: Add Chat Scripts Before `</body>`
```html
<!-- Admin Chat Integration -->
<div th:replace="~{fragments/admin/topbar :: chat-scripts}"></div>
```

---

## 📊 Comparison

| Feature | Manager | Admin |
|---------|---------|-------|
| **Chat Widget** | ✅ Same | ✅ Same |
| **Icon Location** | Sidebar | **Topbar** |
| **Widget Position** | Absolute (sidebar) | **Fixed (bottom-right)** |
| **Conversations** | ✅ All users | ✅ All users |
| **Send Messages** | ✅ Yes | ✅ Yes |
| **Unread Badge** | ✅ Yes | ✅ Yes |
| **Real-time** | ✅ WebSocket | ✅ WebSocket |
| **Mobile Support** | ✅ Yes | ✅ Yes |

---

## 🎉 Results

### Before:
- ❌ Admin không có chức năng chat
- ❌ Phải switch sang manager account để chat
- ❌ Không thấy unread messages

### After:
- ✅ **Admin có full chat features** giống Manager
- ✅ **Chat icon trong topbar** (easy access)
- ✅ **Unread badge** hiển thị real-time
- ✅ **Fixed widget** luôn accessible
- ✅ **Consistent UX** across admin pages
- ✅ **Reusable fragments** dễ maintain

---

**Admin giờ có thể chat với users y hệt Manager! 💬✨**

### Next Steps:
1. Apply topbar fragment to all admin pages
2. Test chat functionality
3. Verify unread badge updates
4. Check mobile responsiveness
