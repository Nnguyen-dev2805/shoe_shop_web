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
                }
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
                    productName: item.productName,
                    productImage: item.productImage,
                    flashSalePrice: item.flashSalePrice,
                    originalPrice: item.originalPrice,
                    discountPercent: item.discountPercent,
                    sizes: [],
                    remaining: 0,
                    sold: 0,
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
                <div class="flash-product-card" data-item-id="${product.firstItemId}" onclick="openFlashSaleModal(${product.firstItemId})">
                    <div class="flash-badge-container">
                        <div class="voucher-badge">VOUCHER XTRA</div>
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
                        
                        <div class="stock-progress">
                            <div class="stock-progress-bar">
                                <div class="stock-progress-fill" style="width: ${soldPercentage}%">
                                    <span class="stock-progress-text">Đã bán ${product.sold || 0}</span>
                                </div>
                            </div>
                            <div class="stock-remaining">Còn lại: <strong>${product.remaining || 0}</strong> sản phẩm</div>
                        </div>
                        
                        <button class="flash-buy-button ${product.remaining === 0 ? 'sold-out' : ''}" 
                                onclick="event.stopPropagation(); openFlashSaleModal(${product.firstItemId})">
                            ${product.remaining === 0 ? 'HẾT HÀNG' : 'MUA NGAY'}
                        </button>
                        
                        <div class="sold-count">Đã bán ${formatSoldCount(product.sold || 0)}</div>
                    </div>
                </div>
            `;
        }).join('');
    }
    
    function displayUpcomingFlashSale(flashSale) {
        const html = `
            <div class="upcoming-flash-sale" id="upcomingFlashSale">
                <h3><i class="fa fa-clock-o"></i> Flash Sale sắp bắt đầu lúc ${formatDateTime(flashSale.startTime)}</h3>
                <div class="flash-sale-countdown">
                    <span class="countdown-label">BẮT ĐẦU SAU</span>
                    <div class="countdown-timer" id="upcomingCountdownTimer">
                        <span class="countdown-time" id="upcomingHours">00</span>
                        <span class="countdown-separator">:</span>
                        <span class="countdown-time" id="upcomingMinutes">00</span>
                        <span class="countdown-separator">:</span>
                        <span class="countdown-time" id="upcomingSeconds">00</span>
                    </div>
                </div>
            </div>
        `;
        
        $('.products-area').before(html);
        startUpcomingCountdown(flashSale.startTime);
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
            
            $('#upcomingHours').text(pad(hours));
            $('#upcomingMinutes').text(pad(minutes));
            $('#upcomingSeconds').text(pad(seconds));
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
                
                // Update progress bar
                card.find('.stock-progress-fill').css('width', (data.soldPercentage || 0) + '%');
                card.find('.stock-progress-text').text('Đã bán ' + (data.sold || 0));
                
                // Update stock remaining
                card.find('.stock-remaining').html('Còn lại: <strong>' + data.remaining + '</strong> sản phẩm');
                
                // Update sold count
                card.find('.sold-count').text('Đã bán ' + formatSoldCount(data.sold || 0));
                
                // Update button
                const button = card.find('.flash-buy-button');
                if (data.remaining === 0) {
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

// Modal mua hàng (sẽ implement sau)
function openFlashSaleModal(itemId) {
    alert('Flash Sale Modal - Item ID: ' + itemId + '\n\nTính năng đang phát triển!');
    // TODO: Implement modal mua hàng
}
