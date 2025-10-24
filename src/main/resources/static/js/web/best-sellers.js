/**
 * Best Sellers Products - Load via AJAX
 */

$(document).ready(function() {
    loadBestSellers();
});

/**
 * Load top 10 best-selling products
 */
function loadBestSellers() {
    $.ajax({
        url: '/api/shop/best-sellers',
        type: 'GET',
        data: {
            limit: 10
        },
        beforeSend: function() {
            $('#bestSellersLoading').show();
            $('#bestSellersEmpty').hide();
            $('#bestSellersContainer').hide();
        },
        success: function(data) {
            $('#bestSellersLoading').hide();
            
            if (data && data.length > 0) {
                renderBestSellers(data);
                $('#bestSellersContainer').show();
            } else {
                $('#bestSellersEmpty').show();
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading best sellers:', error);
            $('#bestSellersLoading').hide();
            $('#bestSellersEmpty').show();
        }
    });
}

/**
 * Render best sellers products
 */
function renderBestSellers(products) {
    const container = $('#bestSellersList');
    container.empty();
    
    products.forEach(function(product, index) {
        const rank = index + 1;
        const rankClass = getRankClass(rank);
        const card = createBestSellerCard(product, rank, rankClass);
        container.append(card);
    });
    
    // Initialize Owl Carousel
    initBestSellersCarousel();
}

/**
 * Get rank class for badge styling
 */
function getRankClass(rank) {
    if (rank === 1) return 'top-1';
    if (rank === 2) return 'top-2';
    if (rank === 3) return 'top-3';
    return '';
}

/**
 * Create best seller product card
 */
function createBestSellerCard(product, rank, rankClass) {
    const productUrl = '/product/details/' + product.productId;
    const formattedPrice = formatCurrency(product.totalRevenue / product.quantitySold);
    
    return `
        <div class="item">
            <div class="best-seller-card">
                <!-- Rank Badge -->
                <div class="rank-badge ${rankClass}">
                    #${rank}
                </div>
                
                <!-- Product Image -->
                <div class="best-seller-image">
                    <a href="${productUrl}">
                        <img src="${product.productImage || '/img/product/default.png'}" 
                             alt="${escapeHtml(product.productName)}"
                             onerror="this.src='/img/product/default.png'">
                    </a>
                    
                    <!-- Sold Badge -->
                    <div class="sold-badge">
                        <i class="fa fa-fire"></i> Đã bán ${product.quantitySold}
                    </div>
                </div>
                
                <!-- Product Info -->
                <div class="best-seller-info">
                    <h3 class="best-seller-name">
                        <a href="${productUrl}" title="${escapeHtml(product.productName)}">
                            ${escapeHtml(product.productName)}
                        </a>
                    </h3>
                    
                    <!-- Rating -->
                    <div class="best-seller-rating">
                        ${renderStars(product.averageRating || 0)}
                        <span class="rating-number" style="margin-left: 5px; color: #FFB400; font-weight: 500;">
                            ${product.averageRating ? product.averageRating.toFixed(1) : '0.0'}
                        </span>
                        <span class="sold-count">(${product.quantitySold} đã bán)</span>
                    </div>
                    
                    <!-- Price -->
                    <div class="price-stats">
                        <div class="best-seller-price">${formattedPrice}</div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Initialize Owl Carousel for best sellers
 */
function initBestSellersCarousel() {
    const carousel = $('.best-sellers-slider');
    
    // Destroy existing carousel if any
    if (carousel.hasClass('owl-loaded')) {
        carousel.trigger('destroy.owl.carousel');
        carousel.removeClass('owl-loaded');
        carousel.find('.owl-stage-outer').children().unwrap();
    }
    
    // Initialize new carousel
    carousel.owlCarousel({
        loop: true,
        margin: 20,
        nav: true,
        dots: true,
        autoplay: true,
        autoplayTimeout: 5000,
        autoplayHoverPause: true,
        navText: [
            '<i class="fa fa-angle-left"></i>',
            '<i class="fa fa-angle-right"></i>'
        ],
        responsive: {
            0: {
                items: 1,
                margin: 10
            },
            480: {
                items: 2,
                margin: 15
            },
            768: {
                items: 3,
                margin: 15
            },
            992: {
                items: 4,
                margin: 20
            },
            1200: {
                items: 5,
                margin: 20
            }
        }
    });
}

/**
 * Format currency (VND)
 */
function formatCurrency(amount) {
    if (!amount) return '0 ₫';
    
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    }).format(amount);
}

/**
 * Render star icons based on rating
 * @param {number} rating - Rating value (0-5)
 * @returns {string} HTML string of star icons
 */
function renderStars(rating) {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
    
    let stars = '';
    
    // Full stars
    for (let i = 0; i < fullStars; i++) {
        stars += '<i class="fa fa-star"></i>';
    }
    
    // Half star
    if (hasHalfStar) {
        stars += '<i class="fa fa-star-half-o"></i>';
    }
    
    // Empty stars
    for (let i = 0; i < emptyStars; i++) {
        stars += '<i class="fa fa-star-o"></i>';
    }
    
    return stars;
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}
