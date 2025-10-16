/**
 * Manager Products JavaScript
 * Deeg Manager V6.0 - Vietnamese Locale
 */

let currentPage = 0;
let currentSearch = '';
let currentCategoryId = '';
let currentStatus = '';

$(document).ready(function() {
    loadCategories();
    loadProducts();
    
    // Filter change events
    $('#category-filter').on('change', function() {
        currentCategoryId = $(this).val();
        currentPage = 0;
        loadProducts();
    });
    
    $('#status-filter').on('change', function() {
        currentStatus = $(this).val();
        currentPage = 0;
        loadProducts();
    });
    
    // Search on Enter key
    $('#search-input').on('keypress', function(e) {
        if (e.which === 13) {
            searchProducts();
        }
    });
});

/**
 * Load categories for filter
 */
function loadCategories() {
    $.ajax({
        url: '/api/manager/categories',
        method: 'GET',
        success: function(data) {
            const select = $('#category-filter');
            data.forEach(category => {
                select.append(`<option value="${category.id}">${category.name}</option>`);
            });
        },
        error: function(xhr, status, error) {
            console.error('Lỗi tải danh mục:', error);
        }
    });
}

/**
 * Load products with pagination
 */
function loadProducts(page = 0) {
    currentPage = page;
    
    const params = {
        page: page,
        size: 12
    };
    
    if (currentSearch) {
        params.search = currentSearch;
    }
    
    if (currentCategoryId) {
        params.categoryId = currentCategoryId;
    }
    
    $.ajax({
        url: '/api/manager/products',
        method: 'GET',
        data: params,
        success: function(data) {
            displayProducts(data.content);
            displayPagination(data);
        },
        error: function(xhr, status, error) {
            console.error('Lỗi tải sản phẩm:', error);
            displayProductsError();
        }
    });
}

/**
 * Display products grid
 */
function displayProducts(products) {
    const container = $('#products-container');
    
    if (!products || products.length === 0) {
        container.html(`
            <div class="col-12 text-center py-5">
                <iconify-icon icon="solar:box-bold-duotone" class="fs-48 text-muted mb-3"></iconify-icon>
                <h5 class="text-muted">Không có sản phẩm nào</h5>
                <p class="text-muted">Không tìm thấy sản phẩm phù hợp với bộ lọc</p>
            </div>
        `);
        return;
    }
    
    let html = '';
    products.forEach(product => {
        const statusBadge = product.isDelete ? 
            '<span class="badge bg-danger">Đã ẩn</span>' : 
            '<span class="badge bg-success">Hiển thị</span>';
        
        const stockStatus = product.totalQuantity > 0 ? 
            `<span class="text-success"><iconify-icon icon="solar:check-circle-bold-duotone" class="me-1"></iconify-icon>Còn hàng (${product.totalQuantity})</span>` :
            '<span class="text-danger"><iconify-icon icon="solar:close-circle-bold-duotone" class="me-1"></iconify-icon>Hết hàng</span>';
        
        html += `
            <div class="col-md-6 col-xl-3 mb-4">
                <div class="card product-card h-100">
                    <div class="position-relative">
                        <img src="${product.image || '/assets/images/product/default.jpg'}" 
                             class="card-img-top product-image" 
                             alt="${product.title}"
                             onerror="this.src='/assets/images/product/default.jpg'">
                        <div class="position-absolute top-0 end-0 m-2">
                            ${statusBadge}
                        </div>
                    </div>
                    <div class="card-body d-flex flex-column">
                        <h6 class="card-title fw-bold mb-2">${product.title}</h6>
                        <p class="text-muted small mb-2">${product.categoryName} - ${product.brandName}</p>
                        <p class="card-text text-muted small flex-grow-1">${product.description || 'Không có mô tả'}</p>
                        
                        <div class="mb-2">
                            <div class="product-price mb-1">${formatCurrency(product.price)}</div>
                            <div class="small">${stockStatus}</div>
                        </div>
                        
                        <div class="d-flex align-items-center justify-content-between mb-2">
                            <div class="product-rating">
                                ${generateStarRating(product.averageRating)}
                                <small class="text-muted">(${product.totalReviews})</small>
                            </div>
                        </div>
                        
                        <div class="d-flex gap-2 mt-auto">
                            <button class="btn btn-primary btn-sm flex-fill" onclick="viewProductDetail(${product.id})">
                                <iconify-icon icon="solar:eye-bold-duotone" class="me-1"></iconify-icon>
                                Chi tiết
                            </button>
                            <button class="btn ${product.isDelete ? 'btn-success' : 'btn-warning'} btn-sm" 
                                    onclick="toggleProductStatus(${product.id}, ${product.isDelete})"
                                    title="${product.isDelete ? 'Hiển thị sản phẩm' : 'Ẩn sản phẩm'}">
                                <iconify-icon icon="${product.isDelete ? 'solar:eye-bold-duotone' : 'solar:eye-closed-bold-duotone'}"></iconify-icon>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    });
    
    container.html(html);
}

/**
 * Display products error
 */
function displayProductsError() {
    const container = $('#products-container');
    container.html(`
        <div class="col-12 text-center py-5">
            <iconify-icon icon="solar:danger-circle-bold-duotone" class="fs-48 text-danger mb-3"></iconify-icon>
            <h5 class="text-danger">Không thể tải sản phẩm</h5>
            <p class="text-muted">Vui lòng thử lại sau</p>
            <button class="btn btn-primary" onclick="loadProducts()">
                <iconify-icon icon="solar:refresh-bold-duotone" class="me-1"></iconify-icon>
                Thử lại
            </button>
        </div>
    `);
}

/**
 * Display pagination
 */
function displayPagination(pageData) {
    const container = $('#pagination-container');
    
    if (pageData.totalPages <= 1) {
        container.empty();
        return;
    }
    
    let html = '';
    
    // Previous button
    if (pageData.number > 0) {
        html += `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="loadProducts(${pageData.number - 1})">
                    <iconify-icon icon="solar:arrow-left-bold-duotone" class="me-1"></iconify-icon>
                    Trước
                </a>
            </li>
        `;
    }
    
    // Page numbers
    const startPage = Math.max(0, pageData.number - 2);
    const endPage = Math.min(pageData.totalPages - 1, pageData.number + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        html += `
            <li class="page-item ${i === pageData.number ? 'active' : ''}">
                <a class="page-link" href="javascript:void(0)" onclick="loadProducts(${i})">${i + 1}</a>
            </li>
        `;
    }
    
    // Next button
    if (pageData.number < pageData.totalPages - 1) {
        html += `
            <li class="page-item">
                <a class="page-link" href="javascript:void(0)" onclick="loadProducts(${pageData.number + 1})">
                    Sau
                    <iconify-icon icon="solar:arrow-right-bold-duotone" class="ms-1"></iconify-icon>
                </a>
            </li>
        `;
    }
    
    container.html(html);
}

/**
 * Generate star rating HTML
 */
function generateStarRating(rating) {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    let html = '';
    
    for (let i = 0; i < 5; i++) {
        if (i < fullStars) {
            html += '<iconify-icon icon="solar:star-bold" class="text-warning"></iconify-icon>';
        } else if (i === fullStars && hasHalfStar) {
            html += '<iconify-icon icon="solar:star-half-bold" class="text-warning"></iconify-icon>';
        } else {
            html += '<iconify-icon icon="solar:star-outline" class="text-muted"></iconify-icon>';
        }
    }
    
    return html;
}

/**
 * Search products
 */
function searchProducts() {
    currentSearch = $('#search-input').val().trim();
    currentPage = 0;
    loadProducts();
}

/**
 * Clear all filters
 */
function clearFilters() {
    $('#search-input').val('');
    $('#category-filter').val('');
    $('#status-filter').val('');
    
    currentSearch = '';
    currentCategoryId = '';
    currentStatus = '';
    currentPage = 0;
    
    loadProducts();
    showAlert('info', 'Đã xóa tất cả bộ lọc');
}

/**
 * View product detail
 */
function viewProductDetail(productId) {
    window.location.href = `/manager/products/${productId}`;
}

/**
 * Toggle product status (show/hide)
 */
function toggleProductStatus(productId, isCurrentlyDeleted) {
    const action = isCurrentlyDeleted ? 'hiển thị' : 'ẩn';
    
    if (!confirm(`Bạn có chắc chắn muốn ${action} sản phẩm này?`)) {
        return;
    }
    
    $.ajax({
        url: `/api/manager/products/${productId}/toggle-status`,
        method: 'PUT',
        success: function(response) {
            showAlert('success', response);
            loadProducts(currentPage);
        },
        error: function(xhr, status, error) {
            const message = xhr.responseText || 'Không thể cập nhật trạng thái sản phẩm';
            showAlert('danger', message);
        }
    });
}

/**
 * Format currency to VND
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Show alert message
 */
function showAlert(type, message) {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <iconify-icon icon="solar:info-circle-bold-duotone" class="me-2"></iconify-icon>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    // Insert alert at the top of container
    $('.container-xxl').prepend(alertHtml);
    
    // Auto dismiss after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut();
    }, 5000);
}
