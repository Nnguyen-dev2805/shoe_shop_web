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
        url: '/api/product',
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
    
    if (products && products.length > 0) {
        products.forEach(function (product) {
            let formattedPrice = new Intl.NumberFormat('vi-VN').format(product.price);
            
            productHtml += `
                <div class="col-lg-4 col-md-6">
                    <div class="single-product">
                        <div class="level-pro-new">
                            <span>Mới</span>
                        </div>
                        <div class="product-img">
                            <a href="/product/details/${product.id}">
                                <img src="${product.image}" alt="${product.title}" class="primary-img">
                                <img src="/img/logo-1.png" alt="Image2" class="secondary-img">
                            </a>
                        </div>
                        <div class="product-name">
                            <a href="/product/details/${product.id}" title="${product.title}">${product.title}</a>
                        </div>
                        <div class="price-rating">
                            <span class="formatted-price">${formattedPrice} đ</span>
                            <div class="ratings">
                                <i class="fa fa-star"></i>
                                <i class="fa fa-star"></i>
                                <i class="fa fa-star"></i>
                                <i class="fa fa-star"></i>
                                <i class="fa fa-star"></i>
                            </div>
                        </div>
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
