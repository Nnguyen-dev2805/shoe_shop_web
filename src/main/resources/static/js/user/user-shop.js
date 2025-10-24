// Global variables
let currentPage = 0;
let currentCategory = null;
let searchKeyword = '';

$(document).ready(function () {
    // Load categories on page load
    loadCategories();
    
    // Load products on page load
    loadProducts(currentPage);
    
    // === EVENT HANDLERS ===

    // Event: Click category to filter products
    $(document).on('click', '.category-link', function (e) {
        e.preventDefault();

        // Remove active class from all categories
        $('.category-link').parent().removeClass('active');
        $(this).parent().addClass('active');

        // Get category ID
        currentCategory = $(this).data('category-id');
        $('#selectedCategory').val(currentCategory);

        // Reset to first page and load products
        currentPage = 0;
        loadProducts(currentPage);
    });
    
    // Event: Click "Tất cả sản phẩm" button
    $(document).on('click', '#allProductsBtn', function (e) {
        e.preventDefault();
        
        // Remove active class from all categories
        $('.category-link').parent().removeClass('active');
        $(this).parent().addClass('active');
        
        // Reset category filter
        currentCategory = null;
        $('#selectedCategory').val('');
        
        // Reset to first page and load all products
        currentPage = 0;
        loadProducts(currentPage);
    });

    // Search button click handler
    $('#searchBtn').click(function (e) {
        e.preventDefault();
        searchKeyword = $('#searchInput').val();
        currentPage = 0;
        loadProducts(currentPage);
    });

    // Search on Enter key
    $('#searchInput').keypress(function (e) {
        if (e.which === 13) {
            e.preventDefault();
            searchKeyword = $(this).val();
            currentPage = 0;
            loadProducts(currentPage);
        }
    });
    
    // Event: Click pagination
    $(document).on('click', '.page-link', function (e) {
        e.preventDefault();
        
        if (!$(this).parent().hasClass('disabled')) {
            currentPage = $(this).data('page');
            loadProducts(currentPage);
            
            // Scroll to top of product list
            $('html, body').animate({
                scrollTop: $('#productListContainer').offset().top - 100
            }, 500);
        }
    });

    // Bắt sự kiện khi nhấn vào liên kết "add-to-wishlist"
    $(document).on('click', '.add-to-wishlist', function (e) {
        e.preventDefault();

        var productIdStr = $(this).data('product-id');
        var productId = parseInt(productIdStr, 10);
        if (isNaN(productId) || productId <= 0) {
            alert('ID sản phẩm không hợp lệ');
            return;
        }

        var heartIcon = $(this).find('i');

        $.ajax({
            url: '/wishlist/toggle',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({productId: productId}),
            success: function (response) {
                if (response === 'ADDED') {
                    heartIcon.removeClass('fa-heart-o').addClass('fa-heart');
                    heartIcon.css('color', 'red');
                } else if (response === 'REMOVED') {
                    heartIcon.removeClass('fa-heart').addClass('fa-heart-o');
                    heartIcon.css('color', '');
                } else if (response === 'LOGIN_REQUIRED') {
                    alert('Vui lòng đăng nhập để thêm sản phẩm vào wishlist');
                } else if (response === 'ALREADY_EXISTS') {
                    alert('Sản phẩm đã có trong wishlist');
                }
            },
            error: function () {
                alert('Đã có lỗi xảy ra, vui lòng thử lại sau');
            }
        });
    });
});

/**
 * Load categories from API and render
 */
function loadCategories() {
    $.ajax({
        url: "/api/category/list",
        type: "GET",
        success: function (categories) {
            let $list = $("#categoryList");
            $list.empty();

            // Thêm button "Tất cả sản phẩm" đầu tiên
            $list.append(`
                <li role="presentation" class="active" style="list-style: none;">
                    <a href="#" 
                       id="allProductsBtn"
                       class="category-link"
                       style="display: flex; justify-content: space-between; align-items: center; padding: 10px 15px;">
                       <span style="flex: 1; line-height: 24px;">
                           <i class="fa fa-th-large" style="margin-right: 8px;"></i>
                           Tất cả sản phẩm
                       </span>
                       <i class="fa fa-arrow-right" style="font-size: 14px;"></i>
                    </a>
                </li>
            `);

            if (!categories || categories.length === 0) {
                $list.append('<li>Chưa có danh mục</li>');
                return;
            }

            // Render các categories
            $.each(categories, function (index, category) {
                const productCount = category.productCount || 0;
                
                $list.append(`
                    <li role="presentation" style="list-style: none;">
                        <a href="#" 
                           class="category-link"
                           data-category-id="${category.id}"
                           style="display: flex; justify-content: space-between; align-items: center; padding: 10px 15px;">
                           <span style="flex: 1; line-height: 24px;">${category.name}</span>
                           <span style="display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; background: #e0e0e0; color: #555; border-radius: 50%; font-size: 11px; font-weight: 600; line-height: 1; text-align: center;">${productCount}</span>
                        </a>
                    </li>
                `);
            });
        },
        error: function (xhr, status, error) {
            console.error("❌ Lỗi khi load categories:", error);
            $("#categoryList").append('<li>Lỗi tải danh mục</li>');
        }
    });
}

/**
 * Load products with pagination, category filter, and search
 * @param {number} page - Page number (0-based)
 */
function loadProducts(page) {
    $('#loadingSpinner').show();
    
    let params = {
        page: page,
        size: 6
    };

    if (searchKeyword && searchKeyword.trim() !== '') {
        params.search = searchKeyword.trim();
    }
    
    // Add category filter if exists
    if (currentCategory) {
        params.categoryId = currentCategory;
    }
    
    $.ajax({
        url: '/api/product',
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function (response) {
            $('#loadingSpinner').hide();
            console.log("✅ JSON Backend trả về:", response);
            if (response && response.content) {
                renderProducts(response.content);
                renderPagination(response);
            } else {
                $('#productList').html('<div class="col-12 text-center" style="margin-top: 50px;"><h4>Không tìm thấy sản phẩm</h4></div>');
            }
        },
        error: function (xhr, status, error) {
            $('#loadingSpinner').hide();
            console.error('❌ Lỗi khi load products:', error);
            $('#productList').html('<div class="col-12 text-center" style="margin-top: 50px;"><p>Lỗi tải sản phẩm. Vui lòng thử lại sau.</p></div>');
        }
    });
}

/**
 * Render products to the DOM with Flash Sale support
 * @param {Array} products - Array of product objects
 */
function renderProducts(products) {
    let productHtml = '';
    
    // 🔍 DEBUG: Log toàn bộ products array
    console.log('=== 🔍 DEBUG renderProducts() [user-shop.js] ===');
    console.log('📦 Total products:', products.length);
    console.log('📄 Full products array:', products);
    
    if (products && products.length > 0) {
        products.forEach(function (product, index) {
            // 🔍 DEBUG: Log từng product
            console.log(`\n--- Product #${index + 1}: ${product.title} ---`);
            console.log('  💰 Price:', product.price);
            console.log('  🔥 Flash Sale Object:', product.flashSale);
            
            // Check if product has flash sale
            const hasFlashSale = product.flashSale && product.flashSale.active;
            console.log('  ✅ Has Flash Sale?', hasFlashSale);
            const flashSalePrice = hasFlashSale ? product.flashSale.flashSalePrice : null;
            const originalPrice = product.price;
            const discountPercent = hasFlashSale ? product.flashSale.discountPercent : 0;
            
            let formattedPrice = new Intl.NumberFormat('vi-VN').format(hasFlashSale ? flashSalePrice : originalPrice);
            let formattedOriginalPrice = new Intl.NumberFormat('vi-VN').format(originalPrice);
            
            productHtml += `
                <div class="col-lg-4 col-md-6">
                    <div class="single-product ${hasFlashSale ? 'flash-sale-product' : ''}">
                        ${hasFlashSale ? `
                            <!-- Flash Sale Badges -->
                            <div class="flash-sale-badges">
                                <div class="voucher-badge">FLASH SALE</div>
                                <div class="discount-badge">-${Math.round(discountPercent)}%</div>
                            </div>
                        ` : `
<!--                            <div class="level-pro-new">-->
<!--                                <span>Mới</span>-->
<!--                            </div>-->
                        `}
                        
                        <div class="product-img">
                            <a href="/product/details/${product.id}">
                                <img src="${product.image}" alt="${product.title}" class="primary-img">
                                <img src="/img/logo-1.png" alt="Image2" class="secondary-img">
                            </a>
                        </div>
                        
                        <div class="product-name" style="padding: 0 12px;">
                            <a href="/product/details/${product.id}" title="${product.title}">${product.title}</a>
                        </div>
                        
                        <div class="price-rating" style="padding: 0 12px;">
                            ${hasFlashSale ? `
                                <!-- Flash Sale Price -->
                                <div style="margin-bottom: 6px;">
                                    <div style="display: flex; align-items: baseline; gap: 8px; flex-wrap: wrap;">
                                        <span style="color: #ff4b2b; font-size: 20px; font-weight: 700; line-height: 1.2;">${formattedPrice} đ</span>
                                        <span style="color: #999; font-size: 14px; text-decoration: line-through;">${formattedOriginalPrice} đ</span>
                                    </div>
                                </div>
                            ` : `
                                <div style="margin-bottom: 6px;">
                                    <span style="color: #333; font-size: 16px; font-weight: 600;">${formattedPrice} đ</span>
                                </div>
                            `}
                            
                            <!-- Rating và Đã bán (theo style Shopee) -->
                            <div style="display: flex; align-items: center; gap: 10px; font-size: 12px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;">
                                <span style="color: #FFB400; font-weight: 400; display: flex; align-items: center; gap: 2px;">
                                    <i class="fa fa-star" style="font-size: 11px;"></i> ${product.averageRating ? product.averageRating.toFixed(1) : '0.0'}
                                </span>
                                <!-- ✅ Luôn dùng Product.soldQuantity -->
                                <span style="color: #757575; font-weight: 400;">Đã bán ${product.soldQuantity || 0}</span>
                            </div>
                        </div>
                        
                        ${hasFlashSale && product.flashSale.stock ? `
                            <!-- ✅ Comment progress bar, chỉ hiển thị tổng stock -->
                            <div class="flash-sale-stock">
                                <div class="stock-remaining" style="margin: 8px 0; color: #666; font-size: 13px;">
                                    Còn lại: <strong>${product.flashSale.totalStock || product.flashSale.remaining || 0}</strong> sản phẩm
                                </div>
                            </div>
                        ` : ''}
                    </div>
                </div>
            `;
        });
    } else {
        productHtml = '<div class="col-12 text-center" style="margin-top: 50px;"><h4>Không có sản phẩm nào</h4></div>';
    }
    
    $('#productList').html(productHtml);
}

/**
 * Render pagination controls
 * @param {Object} data - Pagination data from API response
 */
function renderPagination(data) {
    let paginationHtml = '';

    if (data.totalPages > 1) {
        let currentPageNum = data.currentPage - 1;

        paginationHtml += `
            <div class="col-12">
                <ul class="pagination justify-content-center" style="margin-top: 30px">
        `;

        // Nút "Trang trước"
        paginationHtml += `
            <li class="page-item ${currentPageNum === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPageNum - 1}">Trang trước</a>
            </li>
        `;

        // Các số trang
        for (let i = 0; i < data.totalPages; i++) {
            paginationHtml += `
                <li class="page-item ${i === currentPageNum ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `;
        }

        // Nút "Trang sau"
        paginationHtml += `
            <li class="page-item ${currentPageNum === data.totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPageNum + 1}">Trang sau</a>
            </li>
        `;

        paginationHtml += `
                </ul>
            </div>
        `;
    }

    // Xóa pagination cũ rồi thêm mới
    $('#productList').next('.col-12').remove();
    $('#productList').after(paginationHtml);
}
