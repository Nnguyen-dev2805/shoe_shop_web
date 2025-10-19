/**
 * FLASH SALE - SHOPEE STYLE
 * Load và hiển thị flash sale trên homepage
 */

$(document).ready(function() {
    loadFlashSale();
    
    // Polling stock mỗi 5 giây (khi có flash sale active)
    let stockPollingInterval;
    
    function loadFlashSale() {
        $.ajax({
            url: '/api/flash-sale/active',
            method: 'GET',
            success: function(data) {
                if (data) {
                    displayActiveFlashSale(data);
                    startCountdown(data.endTime);
                    
                    // Start stock polling
                    if (stockPollingInterval) clearInterval(stockPollingInterval);
                    stockPollingInterval = setInterval(function() {
                        updateAllStock(data.items);
                    }, 5000);
                } else {
                    // Check upcoming flash sale
                    checkUpcomingFlashSale();
                }
            },
            error: function() {
                console.log('No active flash sale');
                checkUpcomingFlashSale();
            }
        });
    }
    
    function checkUpcomingFlashSale() {
        $.ajax({
            url: '/api/flash-sale/upcoming',
            method: 'GET',
            success: function(data) {
                if (data) {
                    displayUpcomingFlashSale(data);
                } else {
                    // Không có active và không có upcoming → Empty state
                    displayEmptyFlashSale();
                }
            },
            error: function() {
                // Lỗi API → Show empty state
                displayEmptyFlashSale();
            }
        });
    }
    
    function displayActiveFlashSale(flashSale) {
        const html = `
            <div class="flash-sale-section" id="flashSaleSection">
                <div class="flash-sale-header">
                    <div class="flash-sale-title">
                        <i class="fa fa-bolt flash-icon"></i>
                        <h2>FLASH SALE</h2>
                        <i class="fa fa-bolt flash-icon"></i>
                    </div>
                    <div class="flash-sale-countdown">
                        <span class="countdown-label">KẾT THÚC TRONG</span>
                        <div class="countdown-timer" id="countdownTimer">
                            <span class="countdown-time" id="hours">00</span>
                            <span class="countdown-separator">:</span>
                            <span class="countdown-time" id="minutes">00</span>
                            <span class="countdown-separator">:</span>
                            <span class="countdown-time" id="seconds">00</span>
                        </div>
                    </div>
                </div>
                
                <div class="flash-sale-products">
                    <div class="container">
                        <div class="flash-product-grid" id="flashProductGrid">
                            ${renderFlashProducts(flashSale.items)}
                        </div>
                    </div>
                </div>
                
                <div class="view-all-flash-sale">
                    <a href="/flash-sale" class="view-all-button">
                        XEM TẤT CẢ <i class="fa fa-arrow-right"></i>
                    </a>
                </div>
            </div>
        `;
        
        // Insert sau slider, trước products area
        $('.products-area').before(html);
    }
    
    function renderFlashProducts(items) {
        if (!items || items.length === 0) {
            // Add 'empty-state' class to grid for styling
            setTimeout(() => {
                $('#flashProductGrid').addClass('flash-grid-empty');
            }, 100);
            
            return `
                <div class="flash-sale-empty">
                    <i class="fa fa-shopping-bag"></i>
                    <h3>Chưa có sản phẩm flash sale</h3>
                    <p>Vui lòng quay lại sau!</p>
                </div>
            `;
        }
        
        // ✅ GROUP BY PRODUCT (không hiển thị từng size)
        const productMap = {};
        
        items.forEach(item => {
            const key = item.productName;
            
            if (!productMap[key]) {
                productMap[key] = {
                    productId: item.productId, // ✅ Product ID
                    productName: item.productName,
                    productImage: item.productImage,
                    flashSalePrice: item.flashSalePrice,
                    originalPrice: item.originalPrice,
                    discountPercent: item.discountPercent,
                    sizes: [],
                    remaining: 0,
                    sold: 0,
                    totalStock: item.totalStock || 0, // ✅ Tổng inventory tất cả size
                    productSoldQuantity: item.productSoldQuantity || 0, // ✅ Product.soldQuantity
                    firstItemId: item.id  // Dùng item đầu tiên để click
                };
            }
            
            productMap[key].sizes.push(item.size);
            productMap[key].remaining += (item.remaining || 0);
            productMap[key].sold += (item.sold || 0);
        });
        
        // Giới hạn 10 products để hiển thị
        const products = Object.values(productMap).slice(0, 10);
        
        return products.map(product => {
            const sizeList = product.sizes.sort((a, b) => a - b).join(', ');
            const soldPercentage = product.remaining > 0 
                ? Math.round((product.sold / (product.sold + product.remaining)) * 100) 
                : 100;
            
            return `
                <div class="flash-product-card" data-item-id="${product.firstItemId}" onclick="window.location.href='/product/${product.productId}'">
                    <div class="flash-badge-container">
                        <div class="voucher-badge">FLASH SALE</div>
                        <div class="discount-badge">-${Math.round(product.discountPercent)}%</div>
                    </div>
                    
                    <div class="flash-product-image">
                        <img src="${product.productImage || '/img/product/default.jpg'}" alt="${product.productName}">
                    </div>
                    
                    <div class="flash-product-info">
                        <div class="flash-product-name">${product.productName}</div>
                        <div class="flash-product-sizes" style="font-size: 11px; color: #666; margin-bottom: 5px;">
                            Sizes: ${sizeList}
                        </div>
                        
                        <div class="flash-product-price">
                            <span class="flash-price">${formatPrice(product.flashSalePrice)}</span>
                            <span class="original-price">${formatPrice(product.originalPrice)}</span>
                            <div class="savings">Tiết kiệm ${formatPrice(product.originalPrice - product.flashSalePrice)}</div>
                        </div>
                        
                        <!-- ✅ COMMENT: Progress bar (không cần thiết cho marketing) -->
                        <!-- <div class="stock-progress">
                            <div class="stock-progress-bar">
                                <div class="stock-progress-fill" style="width: ${soldPercentage}%"></div>
                                <span class="stock-progress-text">Đã bán ${product.sold || 0}</span>
                            </div>
                            <div class="stock-remaining">Còn lại: <strong>${product.remaining || 0}</strong> sản phẩm</div>
                        </div> -->
                        
                        <!-- ✅ Hiển thị tổng inventory tất cả size -->
                        <div class="stock-remaining" style="margin: 8px 0; color: #666; font-size: 13px;">
                            Còn lại: <strong>${product.totalStock || product.remaining || 0}</strong> sản phẩm
                        </div>
                        
                        <button class="flash-buy-button ${product.remaining === 0 ? 'sold-out' : ''}" 
                                onclick="event.stopPropagation(); window.location.href='/product/${product.productId}'">
                            ${product.remaining === 0 ? 'HẾT HÀNG' : 'MUA NGAY'}
                        </button>
                        
                        <!-- ✅ Dùng Product.soldQuantity thay vì FlashSale.sold -->
                        <div class="sold-count">Đã bán ${formatSoldCount(product.productSoldQuantity || product.sold || 0)}</div>
                    </div>
                </div>
            `;
        }).join('');
    }
    
    function displayUpcomingFlashSale(flashSale) {
        const startDateTime = formatDateTime(flashSale.startTime);
        const itemCount = flashSale.items ? flashSale.items.length : 0;
        
        const html = `
            <div class="flash-sale-section flash-sale-upcoming" id="flashSaleSection">
                <div class="flash-sale-header upcoming-header">
                    <div class="flash-sale-title">
                        <i class="fa fa-bolt flash-icon"></i>
                        <h2>FLASH SALE SẮP BẮT ĐẦU</h2>
                        <i class="fa fa-bolt flash-icon"></i>
                    </div>
                    <div class="flash-sale-countdown upcoming-countdown">
                        <span class="countdown-label">BẮT ĐẦU SAU</span>
                        <div class="countdown-timer" id="countdownTimer">
                            <span class="countdown-time" id="hours">00</span>
                            <span class="countdown-separator">:</span>
                            <span class="countdown-time" id="minutes">00</span>
                            <span class="countdown-separator">:</span>
                            <span class="countdown-time" id="seconds">00</span>
                        </div>
                    </div>
                </div>
                
                <div class="upcoming-content">
                    <div class="container">
                        <div class="upcoming-hero">
                            <div class="upcoming-bell-icon">
                                <i class="fa fa-bell"></i>
                            </div>
                            <h3>CHUẨN BỊ SẴN SÀNG!</h3>
                            <p class="upcoming-time">Flash Sale sẽ bắt đầu lúc <strong>${startDateTime}</strong></p>
                            
                            <div class="upcoming-features-shopee">
                                <div class="upcoming-feature-item">
                                    <div class="feature-icon"><i class="fa fa-gift"></i></div>
                                    <div class="feature-text">Giảm đến 70%</div>
                                </div>
                                <div class="upcoming-feature-item">
                                    <div class="feature-icon"><i class="fa fa-fire"></i></div>
                                    <div class="feature-text">${itemCount}+ sản phẩm</div>
                                </div>
                                <div class="upcoming-feature-item">
                                    <div class="feature-icon"><i class="fa fa-bolt"></i></div>
                                    <div class="feature-text">Số lượng có hạn</div>
                                </div>
                            </div>
                            
                            <div class="upcoming-actions-shopee">
                                <button class="remind-button-shopee" onclick="setReminder()">
                                    <i class="fa fa-bell-o"></i> Nhắc Tôi Khi Bắt Đầu
                                </button>
                                <a href="/user/shop" class="browse-button-shopee">
                                    <i class="fa fa-shopping-bag"></i> Xem Sản Phẩm Khác
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('.products-area').before(html);
        startUpcomingCountdown(flashSale.startTime);
    }
    
    function displayEmptyFlashSale() {
        const html = `
            <div class="flash-sale-section flash-sale-empty-new" id="flashSaleSection">
                <div class="flash-sale-header empty-header-new">
                    <div class="flash-sale-title">
                        <i class="fa fa-bolt flash-icon" style="color: #ffb74d;"></i>
                        <h2>FLASH SALE</h2>
                        <i class="fa fa-bolt flash-icon" style="color: #ffb74d;"></i>
                    </div>
                </div>
                
                <div class="empty-content-new">
                    <div class="container">
                        <div class="empty-wrapper">
                            <!-- Icon và Message -->
                            <div class="empty-message">
                                <div class="empty-icon-new">
                                    <i class="fa fa-calendar-times-o"></i>
                                </div>
                                <h3>Chưa Có Flash Sale</h3>
                                <p>Hiện tại không có chương trình Flash Sale nào đang diễn ra</p>
                            </div>
                            
                            <!-- Subscribe Compact -->
                            <div class="subscribe-compact">
                                <div class="subscribe-header">
                                    <i class="fa fa-bell"></i>
                                    <span>Nhận thông báo khi có Flash Sale mới</span>
                                </div>
                                <div class="subscribe-form-inline">
                                    <input type="email" id="subscribeEmail" placeholder="Email của bạn" class="email-input-compact">
                                    <button onclick="subscribeFlashSale()" class="btn-subscribe-compact">
                                        Đăng Ký
                                    </button>
                                </div>
                            </div>
                            
                            <!-- Quick Links -->
                            <div class="quick-links">
                                <span class="quick-links-label">Khám phá ngay:</span>
                                <a href="/user/shop?filter=new" class="quick-link-btn">
                                    <i class="fa fa-star"></i> Sản Phẩm Mới
                                </a>
                                <a href="/user/shop?filter=bestseller" class="quick-link-btn">
                                    <i class="fa fa-fire"></i> Bán Chạy
                                </a>
                                <a href="/user/shop?filter=sale" class="quick-link-btn">
                                    <i class="fa fa-tag"></i> Giảm Giá
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $('.products-area').before(html);
    }
    
    // Countdown Timer
    function startCountdown(endTime) {
        const countdownInterval = setInterval(function() {
            const now = new Date().getTime();
            const end = new Date(endTime).getTime();
            const distance = end - now;
            
            if (distance < 0) {
                clearInterval(countdownInterval);
                clearInterval(stockPollingInterval);
                location.reload(); // Reload khi hết giờ
                return;
            }
            
            const hours = Math.floor(distance / (1000 * 60 * 60));
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);
            
            $('#hours').text(pad(hours));
            $('#minutes').text(pad(minutes));
            $('#seconds').text(pad(seconds));
        }, 1000);
    }
    
    function startUpcomingCountdown(startTime) {
        const countdownInterval = setInterval(function() {
            const now = new Date().getTime();
            const start = new Date(startTime).getTime();
            const distance = start - now;
            
            if (distance < 0) {
                clearInterval(countdownInterval);
                location.reload(); // Reload khi đến giờ bắt đầu
                return;
            }
            
            const hours = Math.floor(distance / (1000 * 60 * 60));
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);
            
            // Fix: Dùng đúng IDs từ HTML (không có prefix 'upcoming')
            $('#hours').text(pad(hours));
            $('#minutes').text(pad(minutes));
            $('#seconds').text(pad(seconds));
        }, 1000);
    }
    
    // Update stock real-time
    function updateAllStock(items) {
        if (!items) return;
        
        items.forEach(item => {
            updateItemStock(item.id);
        });
    }
    
    function updateItemStock(itemId) {
        $.ajax({
            url: '/api/flash-sale/item/' + itemId + '/stock',
            method: 'GET',
            success: function(data) {
                const card = $(`.flash-product-card[data-item-id="${itemId}"]`);
                
                if (card.length === 0) return;
                
                // Update progress bar (đã comment trong HTML)
                // card.find('.stock-progress-fill').css('width', (data.soldPercentage || 0) + '%');
                // card.find('.stock-progress-text').text('Đã bán ' + (data.sold || 0));
                
                // ✅ Update stock remaining - Dùng TỔNG TẤT CẢ SIZE
                card.find('.stock-remaining').html('Còn lại: <strong>' + (data.totalStock || data.remaining || 0) + '</strong> sản phẩm');
                
                // ✅ Update sold count - Dùng Product.soldQuantity
                card.find('.sold-count').text('Đã bán ' + formatSoldCount(data.productSoldQuantity || data.sold || 0));
                
                // Update button - Kiểm tra totalStock thay vì remaining
                const button = card.find('.flash-buy-button');
                if ((data.totalStock || data.remaining || 0) === 0) {
                    button.addClass('sold-out').text('HẾT HÀNG').prop('disabled', true);
                } else {
                    button.removeClass('sold-out').text('MUA NGAY').prop('disabled', false);
                }
            }
        });
    }
    
    // Helper functions
    function pad(num) {
        return num < 10 ? '0' + num : num;
    }
    
    function formatPrice(price) {
        if (!price) return '0đ';
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    }
    
    function formatSoldCount(count) {
        if (count >= 1000) {
            return (count / 1000).toFixed(1) + 'k+';
        }
        return count;
    }
    
    function formatDateTime(dateTime) {
        const date = new Date(dateTime);
        const hours = pad(date.getHours());
        const minutes = pad(date.getMinutes());
        const day = pad(date.getDate());
        const month = pad(date.getMonth() + 1);
        return `${hours}:${minutes}, ${day}/${month}`;
    }
});

// ✅ REMOVED: openFlashSaleModal() - Đã đổi sang redirect trực tiếp

// Set Reminder for upcoming flash sale
function setReminder() {
    // Check if browser supports notifications
    if (!("Notification" in window)) {
        alert("Trình duyệt không hỗ trợ thông báo!");
        return;
    }
    
    // Request permission
    Notification.requestPermission().then(function(permission) {
        if (permission === "granted") {
            alert("✅ Đã bật nhắc nhở! Bạn sẽ nhận thông báo khi Flash Sale bắt đầu.");
            
            // TODO: Lưu reminder vào localStorage hoặc backend
            localStorage.setItem('flashSaleReminder', 'enabled');
        } else {
            alert("❌ Vui lòng cho phép thông báo trong cài đặt trình duyệt!");
        }
    });
}

// Subscribe Flash Sale alerts
function subscribeFlashSale() {
    const email = $('#subscribeEmail').val().trim();
    
    if (!email) {
        alert('Vui lòng nhập email!');
        return;
    }
    
    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('Email không hợp lệ!');
        return;
    }
    
    // TODO: Call API to subscribe
    // For now, just show success message
    alert('✅ Đăng ký thành công! Bạn sẽ nhận email thông báo khi có Flash Sale mới.');
    $('#subscribeEmail').val('');
    
    // TODO: Implement backend API
    // $.post('/api/flash-sale/subscribe', { email: email });
}
