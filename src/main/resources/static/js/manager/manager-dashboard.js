/**
 * Manager Dashboard JavaScript
 * Deeg Manager V6.0 - Vietnamese Locale
 */

$(document).ready(function() {
    loadCategoryStatistics();
    loadCategories();
});

// Category icons (8 icons rotating)
const categoryIcons = [
    'solar:sneakers-bold-duotone',      // giày
    'solar:bag-4-bold-duotone',         // túi
    'solar:box-bold-duotone',           // hộp
    'solar:star-bold-duotone',          // ngôi sao
    'solar:shopping-cart-bold-duotone', // giỏ hàng
    'solar:tag-price-bold-duotone',     // thẻ giá
    'solar:gift-bold-duotone',          // quà tặng
    'solar:heart-bold-duotone'          // trái tim
];

// Category colors (4 colors rotating)
const categoryColors = [
    'bg-primary-subtle',    // xanh dương nhạt
    'bg-success-subtle',    // xanh lá nhạt
    'bg-warning-subtle',    // vàng nhạt
    'bg-info-subtle'        // xanh cyan nhạt
];

/**
 * Load category statistics
 */
function loadCategoryStatistics() {
    $.ajax({
        url: '/api/manager/categories/statistics',
        method: 'GET',
        success: function(data) {
            updateStatistics(data);
        },
        error: function(xhr, status, error) {
            console.error('Lỗi tải thống kê:', error);
            showAlert('danger', 'Không thể tải thống kê danh mục');
        }
    });
}

/**
 * Update statistics with animation
 */
function updateStatistics(stats) {
    animateValue('total-categories', 0, stats.totalCategories, 1000);
    animateValue('total-products', 0, stats.totalProducts, 1200);
    animateValue('active-categories', 0, stats.categoriesWithProducts, 1400);
    animateValue('empty-categories', 0, stats.emptyCategories, 1600);
}

/**
 * Animate number counting
 */
function animateValue(elementId, start, end, duration) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const range = end - start;
    const increment = range / (duration / 16); // 60fps
    let current = start;
    
    const timer = setInterval(function() {
        current += increment;
        if (current >= end) {
            current = end;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current);
    }, 16);
}

/**
 * Load categories
 */
function loadCategories() {
    $.ajax({
        url: '/api/manager/categories',
        method: 'GET',
        success: function(data) {
            displayCategories(data);
        },
        error: function(xhr, status, error) {
            console.error('Lỗi tải danh mục:', error);
            displayError();
        }
    });
}

/**
 * Display categories grid
 */
function displayCategories(categories) {
    const container = $('#categories-container');
    
    if (!categories || categories.length === 0) {
        container.html(`
            <div class="col-12 text-center py-5">
                <iconify-icon icon="solar:box-bold-duotone" class="fs-48 text-muted mb-3"></iconify-icon>
                <h5 class="text-muted">Chưa có danh mục nào</h5>
                <p class="text-muted">Hãy thêm danh mục đầu tiên để bắt đầu</p>
            </div>
        `);
        return;
    }
    
    let html = '';
    categories.forEach((category, index) => {
        const colorClass = categoryColors[index % categoryColors.length];
        const iconName = categoryIcons[index % categoryIcons.length];
        
        html += `
            <div class="col-md-6 col-xl-3 mb-3">
                <div class="card category-card h-100" onclick="viewCategory(${category.id})" style="cursor: pointer;">
                    <div class="card-body text-center">
                        <div class="category-icon-box ${colorClass} d-flex align-items-center justify-content-center mx-auto mb-3">
                            <iconify-icon icon="${iconName}" class="fs-48 text-primary"></iconify-icon>
                        </div>
                        <h5 class="fw-bold mt-2 mb-1">${category.name}</h5>
                        <div class="badge bg-primary-subtle text-primary">
                            <iconify-icon icon="solar:box-bold-duotone" class="me-1"></iconify-icon>
                            ${category.productCount} sản phẩm
                        </div>
                        ${category.description ? `<p class="text-muted mt-2 mb-0 small">${category.description}</p>` : ''}
                    </div>
                </div>
            </div>
        `;
    });
    
    container.html(html);
}

/**
 * Display error message
 */
function displayError() {
    const container = $('#categories-container');
    container.html(`
        <div class="col-12 text-center py-5">
            <iconify-icon icon="solar:danger-circle-bold-duotone" class="fs-48 text-danger mb-3"></iconify-icon>
            <h5 class="text-danger">Không thể tải danh mục</h5>
            <p class="text-muted">Vui lòng thử lại sau</p>
            <button class="btn btn-primary" onclick="loadCategories()">
                <iconify-icon icon="solar:refresh-bold-duotone" class="me-1"></iconify-icon>
                Thử lại
            </button>
        </div>
    `);
}

/**
 * View category products
 */
function viewCategory(categoryId) {
    window.location.href = `/manager/products?categoryId=${categoryId}`;
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
