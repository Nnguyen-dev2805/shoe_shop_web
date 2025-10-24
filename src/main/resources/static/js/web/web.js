let currentPage = 0;
let currentCategory = null;
let searchKeyword = '';

$(document).ready(function (){
    // Load categories
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
    
    // Event: Search form submit
    $('form[action*="search"]').on('submit', function (e) {
        e.preventDefault();
        searchKeyword = $(this).find('input[name="key"]').val();
        currentPage = 0;
        loadProducts(currentPage);
    });
});

// function loadCategories() {
//     $.ajax({
//         url: "/api/category/list",
//         type: "GET",
//         success: function (categories) {
//             let $list = $("#categoryList");
//             $list.empty();
//
//             // Thêm button "Tất cả sản phẩm" đầu tiên
//             $list.append(`
//                 <li role="presentation" class="active">
//                     <a href="#tab1"
//                        id="allProductsBtn"
//                        class="category-link"
//                        aria-controls="tab1"
//                        role="tab"
//                        data-bs-toggle="tab"
//                        style="display: flex; justify-content: flex-start; align-items: center; padding: 10px 15px;"
//                        <span style="flex: 1; line-height: 24px;">
//                            <i class="fa fa-th-large" style="margin-right: 8px;"></i>
//                            Tất cả sản phẩm
//                        </span>
//                        <i class="fa fa-arrow-right" style="font-size: 14px;"></i>
//                     </a>
//                 </li>
//             `);
//
//             if (!categories || categories.length === 0) {
//                 $list.append('<li>Chưa có danh mục</li>');
//                 return;
//             }
//
//             // Render các categories
//             $.each(categories, function (index, category) {
//                 const productCount = category.productCount || 0;
//
//                 $list.append(`
//                     <li role="presentation">
//                         <a href="#tab1" aria-controls="tab1" role="tab" data-bs-toggle="tab"
//                            class="category-link"
//                            data-category-id="${category.id}"
//                            style="display: flex; justify-content: flex-start; align-items: center; padding: 10px 15px;"
//                            <span style="flex: 1; line-height: 24px;">${category.name}</span>
//                            <span style="display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; background: #e0e0e0; color: #555; border-radius: 50%; font-size: 11px; font-weight: 600; line-height: 1; text-align: center;">${productCount}</span>
//                         </a>
//                     </li>
//                 `);
//             });
//         },
//         error: function (xhr, status, error) {
//             console.error("❌ Lỗi khi load categories:", error);
//             $("#categoryList").append('<li>Lỗi tải danh mục</li>');
//         }
//     });
// }
function loadCategories() {
    $.ajax({
        url: "/api/category/list",
        type: "GET",
        success: function (categories) {
            let $list = $("#categoryList");
            $list.empty();

            // Thêm button "Tất cả sản phẩm" đầu tiên
            $list.append(`
                <li role="presentation" class="active">
                    <a href="#tab1" 
                       id="allProductsBtn"
                       class="category-link"
                       aria-controls="tab1" 
                       role="tab" 
                       data-bs-toggle="tab"
                       style="display: flex; justify-content: space-between; align-items: center; padding: 10px 15px;">
                       
                       <span style="line-height: 24px; display: flex; align-items: center;">
                           <i class="fa fa-th-large" style="margin-right: 8px;"></i>
                           Tất cả sản phẩm
                       </span>
                       <i class="fa fa-arrow-right" style="font-size: 14px; color: #777;"></i>
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
                    <li role="presentation">
                        <a href="#tab1" aria-controls="tab1" role="tab" data-bs-toggle="tab"
                           class="category-link"
                           data-category-id="${category.id}"
                           style="display: flex; justify-content: space-between; align-items: center; padding: 10px 15px;">
                           
                           <span style="line-height: 24px;">${category.name}</span>
                           <span style="display: inline-flex; align-items: center; justify-content: center; min-width: 24px; height: 24px; background: #e0e0e0; color: #555; border-radius: 50%; font-size: 11px; font-weight: 600; line-height: 1; text-align: center; padding: 0 6px;">
                               ${productCount}
                           </span>
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


function loadProducts(page) {

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
        url: '/api/shop/products',  // ✅ FIX: Endpoint đúng
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function (response) {
            console.log("✅ JSON Backend trả về:", response);
            if (response && response.content) {
                renderProducts(response.content);
                renderPagination(response);
            } else {
                $('#productList').html('<div class="col-12 text-center" style="margin-top: 50px;"><h4>Không tìm thấy sản phẩm</h4></div>');
            }
        },
        error: function (xhr, status, error) {
            console.error('❌ Lỗi khi load products:', error);
            $('#productList').html('<div class="col-12 text-center" style="margin-top: 50px;"><p>Lỗi tải sản phẩm. Vui lòng thử lại sau.</p></div>');
        }
    });
}

function renderProducts(products) {
    let productHtml = '';
    
    // 🔍 DEBUG: Log toàn bộ products array
    console.log('=== 🔍 DEBUG renderProducts() ===');
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
                                <span style="color: #757575; font-weight: 400;">Đã bán ${formatSoldQuantity(product.soldQuantity || 0)}</span>
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

function renderPagination(data) {
    let paginationHtml = '';

    if (data.totalPages > 1) {
        const currentPageNum = data.currentPage; // Backend already returns 0-based page number
        const totalPages = data.totalPages;
        
        // Style cho pagination container
        const containerStyle = `
            margin-top: 40px; 
            display: flex; 
            justify-content: center; 
            align-items: center;
            gap: 8px;
        `;
        
        // Base style cho tất cả các nút
        const baseButtonStyle = `
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 36px;
            height: 36px;
            padding: 0 12px;
            background: #fff;
            border: 1px solid #e8e8e8;
            border-radius: 4px;
            color: #333;
            font-size: 14px;
            text-decoration: none;
            transition: all 0.2s ease;
            cursor: pointer;
            user-select: none;
        `;
        
        // Style cho nút active
        const activeButtonStyle = `
            background: #ee4d2d;
            border-color: #ee4d2d;
            color: #fff;
            font-weight: 600;
        `;
        
        // Style cho nút disabled
        const disabledButtonStyle = `
            cursor: not-allowed;
            opacity: 0.5;
            pointer-events: none;
        `;
        
        // Style cho hover
        const hoverStyle = `
            onmouseover="if(!this.classList.contains('active') && !this.classList.contains('disabled')) { this.style.borderColor='#ee4d2d'; this.style.color='#ee4d2d'; }"
            onmouseout="if(!this.classList.contains('active') && !this.classList.contains('disabled')) { this.style.borderColor='#e8e8e8'; this.style.color='#333'; }"
        `;

        paginationHtml += `<div class="col-12"><div style="${containerStyle}">`;

        // Nút "Previous" (Trang trước)
        const isPrevDisabled = currentPageNum === 0;
        paginationHtml += `
            <a class="page-link ${isPrevDisabled ? 'disabled' : ''}" 
               href="#" 
               data-page="${currentPageNum - 1}"
               style="${baseButtonStyle} ${isPrevDisabled ? disabledButtonStyle : ''}"
               ${!isPrevDisabled ? hoverStyle : ''}>
                <i class="fa fa-chevron-left" style="font-size: 12px;"></i>
            </a>
        `;

        // Logic hiển thị số trang (Shopee style: max 5 pages visible)
        let pages = [];
        
        if (totalPages <= 7) {
            // Nếu <= 7 trang, hiển thị tất cả
            for (let i = 0; i < totalPages; i++) {
                pages.push(i);
            }
        } else {
            // Logic phức tạp hơn khi có nhiều trang
            if (currentPageNum <= 3) {
                // Đầu: 1 2 3 4 5 ... 10
                pages = [0, 1, 2, 3, 4, -1, totalPages - 1];
            } else if (currentPageNum >= totalPages - 4) {
                // Cuối: 1 ... 6 7 8 9 10
                pages = [0, -1, totalPages - 5, totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1];
            } else {
                // Giữa: 1 ... 4 5 6 ... 10
                pages = [0, -1, currentPageNum - 1, currentPageNum, currentPageNum + 1, -1, totalPages - 1];
            }
        }

        // Render các số trang
        pages.forEach((pageNum, index) => {
            if (pageNum === -1) {
                // Dấu "..."
                paginationHtml += `
                    <span style="${baseButtonStyle} cursor: default; pointer-events: none;">...</span>
                `;
            } else {
                const isActive = pageNum === currentPageNum;
                paginationHtml += `
                    <a class="page-link ${isActive ? 'active' : ''}" 
                       href="#" 
                       data-page="${pageNum}"
                       style="${baseButtonStyle} ${isActive ? activeButtonStyle : ''}"
                       ${!isActive ? hoverStyle : ''}>
                        ${pageNum + 1}
                    </a>
                `;
            }
        });

        // Nút "Next" (Trang sau)
        const isNextDisabled = currentPageNum === totalPages - 1;
        paginationHtml += `
            <a class="page-link ${isNextDisabled ? 'disabled' : ''}" 
               href="#" 
               data-page="${currentPageNum + 1}"
               style="${baseButtonStyle} ${isNextDisabled ? disabledButtonStyle : ''}"
               ${!isNextDisabled ? hoverStyle : ''}>
                <i class="fa fa-chevron-right" style="font-size: 12px;"></i>
            </a>
        `;

        paginationHtml += `</div></div>`;
    }

    // Xóa pagination cũ rồi thêm mới
    $('#productList').next('.col-12').remove();
    $('#productList').after(paginationHtml);
}

/**
 * Format sold quantity (Shopee style)
 * Examples: 12 -> 12, 1234 -> 1.2k, 45678 -> 45.6k, 1500000 -> 1.5tr
 */
function formatSoldQuantity(quantity) {
    if (!quantity) return '0';
    
    if (quantity < 1000) {
        return quantity.toString();
    } else if (quantity < 1000000) {
        // Format as "k" (thousands)
        const k = (quantity / 1000).toFixed(1);
        return k.endsWith('.0') ? Math.floor(quantity / 1000) + 'k' : k + 'k';
    } else {
        // Format as "tr" (triệu - millions)
        const m = (quantity / 1000000).toFixed(1);
        return m.endsWith('.0') ? Math.floor(quantity / 1000000) + 'tr' : m + 'tr';
    }
}
