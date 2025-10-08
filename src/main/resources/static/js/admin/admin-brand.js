/**
 * Admin Brand Management JavaScript
 * Handles CRUD operations for brand management
 */

// Global variables
let currentPage = 0;
let totalPages = 1;
let searchTerm = '';

/**
 * Initialize brand management
 */
$(document).ready(function() {
    // Show initial pagination
    showSimplePagination();
    
    loadBrands();
    
    // Search functionality
    $('#searchInput').on('input', function() {
        searchTerm = $(this).val();
        currentPage = 0; // Reset to first page when searching
        loadBrands();
    });
    
    // Page size change handler
    $('#pageSizeSelect').on('change', function() {
        currentPage = 0; // Reset to first page when changing page size
        loadBrands();
    });
});

/**
 * Load brands with pagination and search
 */
function loadBrands() {
    $.ajax({
        url: '/api/brand',
        type: 'GET',
        data: {
            page: currentPage,
            size: $('#pageSizeSelect').val() || 10,
            search: searchTerm
        },
        dataType: 'json',
        success: function(response) {
            const tbody = $('#brandTbody');
            tbody.empty();
            
            if (!response || !response.content || response.content.length === 0) {
                tbody.append('<tr><td colspan="3" class="text-center text-muted">Không có dữ liệu brand nào.</td></tr>');
                // Show simple pagination even when no data
                showSimplePagination();
                updatePaginationInfo(response);
                return;
            }
            
            response.content.forEach(function(brand) {
                const row = `
                <tr data-brand-name="${brand.name.toLowerCase()}">
                    <td>${brand.id || ''}</td>
                    <td>${brand.name || ''}</td>
                    <td>
                        <div class="d-flex gap-2">
                            <a href="/admin/brand/edit/${brand.id}" class="btn btn-light btn-sm">
                                <iconify-icon icon="solar:pen-bold" class="align-middle fs-18"></iconify-icon> Sửa
                            </a>
                            <button onclick="deleteBrand(${brand.id})" class="btn btn-soft-danger btn-sm delete-btn">
                                <iconify-icon icon="solar:trash-broken" class="align-middle fs-18"></iconify-icon> Xoá
                            </button>
                        </div>
                    </td>
                </tr>
            `;
                tbody.append(row);
            });
            
            updatePagination(response);
            updatePaginationInfo(response);
        },
        error: function(xhr, status, error) {
            const tbody = $('#brandTbody');
            let msg = 'Lỗi load dữ liệu: ';
            if (xhr.status === 401) {
                msg += 'Chưa đăng nhập. Đang chuyển hướng...';
                setTimeout(function() { window.location.href = '/login'; }, 1000);
            } else if (xhr.status === 403) {
                msg += 'Không có quyền truy cập.';
            } else if (xhr.status === 500) {
                msg += 'Lỗi server. Vui lòng thử lại.';
            } else {
                msg += xhr.status + ' - ' + error;
            }
            tbody.html('<tr><td colspan="3" class="text-center text-danger">' + msg + '</td></tr>');
        }
    });
}

/**
 * Update pagination controls
 */
function updatePagination(response) {
    if (response) {
        currentPage = response.currentPage - 1; // Convert to 0-based
        totalPages = response.totalPages;
        
        const pagination = $('#pagination');
        pagination.empty();
        
        // Always show pagination if we have data
        if (totalPages <= 0) {
            // Show at least page 1 if we have some data
            if (response.totalElements > 0) {
                totalPages = 1;
                currentPage = 0;
            } else {
                return;
            }
        }
        
        // Previous button
        const prevDisabled = currentPage === 0 ? 'disabled' : '';
        pagination.append(`
            <li class="page-item ${prevDisabled}">
                <a class="page-link" href="javascript:void(0);" onclick="changePage(${currentPage - 1})">Previous</a>
            </li>
        `);
        
        // Page numbers - show max 5 pages around current page
        let startPage = Math.max(0, currentPage - 2);
        let endPage = Math.min(totalPages - 1, currentPage + 2);
        
        // Adjust if we're near the beginning or end
        if (endPage - startPage < 4) {
            if (startPage === 0) {
                endPage = Math.min(totalPages - 1, startPage + 4);
            } else {
                startPage = Math.max(0, endPage - 4);
            }
        }
        
        // First page and ellipsis if needed
        if (startPage > 0) {
            pagination.append(`
                <li class="page-item">
                    <a class="page-link" href="javascript:void(0);" onclick="changePage(0)">1</a>
                </li>
            `);
            if (startPage > 1) {
                pagination.append(`
                    <li class="page-item disabled">
                        <span class="page-link">...</span>
                    </li>
                `);
            }
        }
        
        // Page numbers
        for (let i = startPage; i <= endPage; i++) {
            const active = i === currentPage ? 'active' : '';
            pagination.append(`
                <li class="page-item ${active}">
                    <a class="page-link" href="javascript:void(0);" onclick="changePage(${i})">${i + 1}</a>
                </li>
            `);
        }
        
        // Last page and ellipsis if needed
        if (endPage < totalPages - 1) {
            if (endPage < totalPages - 2) {
                pagination.append(`
                    <li class="page-item disabled">
                        <span class="page-link">...</span>
                    </li>
                `);
            }
            pagination.append(`
                <li class="page-item">
                    <a class="page-link" href="javascript:void(0);" onclick="changePage(${totalPages - 1})">${totalPages}</a>
                </li>
            `);
        }
        
        // Next button
        const nextDisabled = currentPage >= totalPages - 1 ? 'disabled' : '';
        pagination.append(`
            <li class="page-item ${nextDisabled}">
                <a class="page-link" href="javascript:void(0);" onclick="changePage(${currentPage + 1})">Next</a>
            </li>
        `);
        
    }
}

/**
 * Change page
 */
function changePage(page) {
    if (page >= 0 && page < totalPages) {
        currentPage = page;
        loadBrands();
    }
}

/**
 * Show simple pagination when no data
 */
function showSimplePagination() {
    const pagination = $('#pagination');
    pagination.empty();
    pagination.append(`
        <li class="page-item disabled">
            <a class="page-link" href="javascript:void(0);">Previous</a>
        </li>
        <li class="page-item active">
            <a class="page-link" href="javascript:void(0);">1</a>
        </li>
        <li class="page-item disabled">
            <a class="page-link" href="javascript:void(0);">Next</a>
        </li>
    `);
}

/**
 * Update pagination info (showing X of Y results)
 */
function updatePaginationInfo(response) {
    if (response) {
        const startItem = (response.currentPage - 1) * response.size + 1;
        const endItem = Math.min(response.currentPage * response.size, response.totalElements);
        const totalItems = response.totalElements;
        
        let infoText = '';
        if (totalItems === 0) {
            infoText = 'Không có dữ liệu';
        } else {
            infoText = `Hiển thị ${startItem}-${endItem} của ${totalItems} kết quả`;
        }
        
        // Update pagination info
        const cardFooter = $('.card-footer');
        if (cardFooter.length > 0) {
            // Remove existing pagination info
            $('#paginationInfo').remove();
            
            // Add new pagination info
            cardFooter.prepend(`<div class="text-muted small mb-2" id="paginationInfo">${infoText}</div>`);
        }
    }
}

/**
 * Delete brand
 */
window.deleteBrand = function(id) {
    if (!confirm('Bạn có chắc muốn xóa brand ID ' + id + '? Dữ liệu sẽ không thể khôi phục!')) {
        return;
    }
    
    const deleteBtn = event.target.closest('.delete-btn');
    const originalText = deleteBtn.innerHTML;
    deleteBtn.innerHTML = '<iconify-icon icon="solar:loader-circle-bold" class="align-middle fs-18"></iconify-icon> Đang xóa...';
    deleteBtn.disabled = true;
    
    $.ajax({
        url: '/api/brand/' + id,
        type: 'DELETE',
        success: function(response) {
            alert('Xóa thành công!');
            loadBrands();
        },
        error: function(xhr) {
            let msg = 'Lỗi xóa: ';
            if (xhr.status === 404) {
                msg += 'Brand không tồn tại.';
            } else if (xhr.status === 500) {
                msg += xhr.responseText || 'Lỗi server.';
            } else {
                msg += xhr.status;
            }
            alert(msg);
            deleteBtn.innerHTML = originalText;
            deleteBtn.disabled = false;
        }
    });
};

// Brand form handlers (for add/edit pages)
$(document).ready(function() {
    // Create brand form handler
    $('#categoryForm').on('submit', function(e) {
        e.preventDefault();
        const requestData = {
            name: $('#name').val().trim()
        };
        
        if (!requestData.name) {
            $('#nameError').text('Tên không được để trống').show();
            $('#name').focus();
            return false;
        }
        
        $('#nameError').hide();
        $('#message').empty();

        $.ajax({
            url: '/api/brand',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            dataType: 'json',
            success: function(response) {
                $('#message').html(
                    '<div class="alert alert-success alert-dismissible fade show" role="alert">' +
                    'Thêm thành công! Tên: <strong>' + response.name + '</strong> (ID: ' + response.id + ')' +
                    '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
                    '</div>'
                );
                $('#categoryForm')[0].reset();
                setTimeout(function() {
                    window.location.href = '/admin/brand'
                }, 2000);
            },
            error: function(xhr, status, error) {
                let errorMsg = 'Lỗi: ';
                if (xhr.status === 400) {
                    errorMsg += xhr.responseText || 'Dữ liệu không hợp lệ';
                } else if (xhr.status === 401) {
                    errorMsg += 'Chưa đăng nhập. Vui lòng login lại.';
                    window.location.href = '/login';
                } else if (xhr.status === 403) {
                    errorMsg += 'Không có quyền truy cập.';
                } else if (xhr.status === 500) {
                    errorMsg += 'Lỗi server. Vui lòng thử lại.' + (xhr.responseText ? ' Chi tiết: ' + xhr.responseText : '');
                } else {
                    errorMsg += xhr.status + ' - ' + error;
                }
                $('#message').html(
                    '<div class="alert alert-danger alert-dismissible fade show" role="alert">' +
                    errorMsg +
                    '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
                    '</div>'
                );
            }
        });
    });
    
    // Edit brand functionality
    const pathParts = window.location.pathname.split('/');
    const brandId = pathParts[pathParts.length - 1];
    
    // Load brand data for editing
    if (window.location.pathname.includes('/edit/')) {
        $.ajax({
            url: '/api/brand/' + brandId,
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                $('#name').val(response.name);
            },
            error: function(xhr, status, error) {
                let msg = 'Lỗi load dữ liệu: ';
                if (xhr.status === 404) {
                    msg += 'Brand không tồn tại.';
                } else if (xhr.status === 401) {
                    msg += 'Chưa đăng nhập. Vui lòng login lại.';
                    window.location.href = '/login';
                } else if (xhr.status === 403) {
                    msg += 'Không có quyền truy cập.';
                } else if (xhr.status === 500) {
                    msg += 'Lỗi server. Vui lòng thử lại.';
                } else {
                    msg += xhr.status + ' - ' + error;
                }
                $('#message').html(
                    '<div class="alert alert-danger alert-dismissible fade show" role="alert">' +
                    msg +
                    '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
                    '</div>'
                );
            }
        });
        
        $('#brandForm').on('submit', function(e) {
            e.preventDefault();
            const requestData = {
                name: $('#name').val().trim()
            };
            
            if (!requestData.name) {
                $('#nameError').text('Tên không được để trống').show();
                $('#name').focus();
                return false;
            }
            
            $('#nameError').hide();
            $('#message').empty();

            $.ajax({
                url: '/api/brand/' + brandId,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(requestData),
                dataType: 'json',
                success: function(response) {
                    $('#message').html(
                        '<div class="alert alert-success alert-dismissible fade show" role="alert">' +
                        'Cập nhật thành công! Tên: <strong>' + response.name + '</strong> (ID: ' + response.id + ')' +
                        '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
                        '</div>'
                    );
                    setTimeout(function() {
                        window.location.href = '/admin/brand'
                    }, 2000);
                },
                error: function(xhr, status, error) {
                    let errorMsg = 'Lỗi: ';
                    if (xhr.status === 400) {
                        errorMsg += xhr.responseText || 'Dữ liệu không hợp lệ';
                    } else if (xhr.status === 401) {
                        errorMsg += 'Chưa đăng nhập. Vui lòng login lại.';
                        window.location.href = '/login';
                    } else if (xhr.status === 403) {
                        errorMsg += 'Không có quyền truy cập.';
                    } else if (xhr.status === 404) {
                        errorMsg += 'Brand không tồn tại.';
                    } else if (xhr.status === 500) {
                        errorMsg += 'Lỗi server. Vui lòng thử lại.' + (xhr.responseText ? ' Chi tiết: ' + xhr.responseText : '');
                    } else {
                        errorMsg += xhr.status + ' - ' + error;
                    }
                    $('#message').html(
                        '<div class="alert alert-danger alert-dismissible fade show" role="alert">' +
                        errorMsg +
                        '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
                        '</div>'
                    );
                }
            });
        });
    }
});
