/**
 * FLASH SALE ADMIN - JavaScript
 * RESTful API + Ajax + jQuery
 * File riêng cho quản lý Flash Sale trong Admin Panel
 */

(function() {
    'use strict';

    // Namespace
    window.FlashSaleAdmin = {
        loadFlashSales: loadFlashSales,
        deleteFlashSale: deleteFlashSale,
        updateStatus: updateStatus
    };

    /**
     * Load Flash Sales từ API
     * @param {number} page - Page number (0-indexed)
     * @param {string} status - Filter by status (optional)
     */
    function loadFlashSales(page, status) {
        var url = '/admin/api/flash-sale?page=' + page + '&size=10';
        if (status) {
            url += '&status=' + status;
        }

        // Thêm loading effect
        $('#flashSaleTbody').addClass('loading');

        $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                $('#flashSaleTbody').removeClass('loading');
                console.log('✅ Load flash sales response:', response);
                
                renderFlashSales(response.content || response);
                
                // Update pagination if available
                if (response.totalPages) {
                    updatePagination(response.number, response.totalPages, status);
                }
            },
            error: function(xhr, status, error) {
                $('#flashSaleTbody').removeClass('loading');
                console.error('❌ Load flash sales error:', xhr, status, error);
                
                var tbody = $('#flashSaleTbody');
                var msg = getErrorMessage(xhr, error);
                tbody.html('<tr><td colspan="8" class="text-center text-danger py-4">' + msg + '</td></tr>');
            }
        });
    }

    /**
     * Render Flash Sales table rows
     * @param {Array} flashSales - Array of flash sale objects
     */
    function renderFlashSales(flashSales) {
        var tbody = $('#flashSaleTbody');
        tbody.empty();

        if (!flashSales || flashSales.length === 0) {
            tbody.append('<tr><td colspan="8" class="text-center text-muted py-4">Không có dữ liệu flash sale nào.</td></tr>');
            return;
        }

        flashSales.forEach(function(flashSale) {
            var statusBadge = getStatusBadge(flashSale.status);
            
            var row = `
                <tr>
                    <td>${flashSale.id || ''}</td>
                    <td>
                        <div class="d-flex align-items-center gap-2">
                            <span class="flash-sale-badge"><i class="bx bx-bolt"></i> FLASH SALE</span>
                            <p class="fs-15 mb-0 fw-semibold">${flashSale.name || ''}</p>
                        </div>
                        ${flashSale.description ? '<p class="text-muted small mb-0">' + flashSale.description + '</p>' : ''}
                    </td>
                    <td>
                        <span class="text-primary">${formatDateTime(flashSale.startTime)}</span>
                    </td>
                    <td>
                        <span class="text-danger">${formatDateTime(flashSale.endTime)}</span>
                    </td>
                    <td>${statusBadge}</td>
                    <td>
                        <span class="badge bg-info-subtle text-info" id="products-count-${flashSale.id}">
                            <i class="bx bx-loader bx-spin"></i> Đang tải...
                        </span>
                    </td>
                    <td>
                        <span class="badge bg-success-subtle text-success">${flashSale.totalSold || 0}</span>
                    </td>
                    <td>
                        <div class="d-flex gap-2">
                            <a href="/admin/flash-sale-edit/${flashSale.id}" class="btn btn-light btn-sm" title="Chỉnh sửa">
                                <i class="bx bx-edit fs-18"></i>
                            </a>
                            <button onclick="FlashSaleAdmin.deleteFlashSale(${flashSale.id})" class="btn btn-soft-danger btn-sm delete-btn" title="Xóa">
                                <i class="bx bx-trash fs-18"></i>
                            </button>
                            ${getActionButtons(flashSale)}
                        </div>
                    </td>
                </tr>
            `;
            tbody.append(row);
            
            // ✅ Fetch items và count unique products
            loadProductCount(flashSale.id);
        });
    }
    
    /**
     * Load và tính số products (unique) cho flash sale
     */
    function loadProductCount(flashSaleId) {
        $.ajax({
            url: '/admin/api/flash-sale/' + flashSaleId + '/items',
            type: 'GET',
            success: function(items) {
                // Count unique products
                var uniqueProducts = new Set();
                items.forEach(function(item) {
                    uniqueProducts.add(item.productName);
                });
                
                var productCount = uniqueProducts.size;
                var itemCount = items.length;
                
                // Update badge
                $('#products-count-' + flashSaleId).html(
                    productCount + ' sản phẩm <small>(' + itemCount + ' sizes)</small>'
                );
            },
            error: function() {
                $('#products-count-' + flashSaleId).html('0 sản phẩm');
            }
        });
    }

    /**
     * Get action buttons based on status
     */
    function getActionButtons(flashSale) {
        var buttons = '';
        
        if (flashSale.status === 'SCHEDULED') {
            buttons += `
                <button onclick="FlashSaleAdmin.updateStatus(${flashSale.id}, 'ACTIVE')" 
                        class="btn btn-success btn-sm" title="Kích hoạt">
                    <i class="bx bx-play"></i>
                </button>
            `;
        }
        
        if (flashSale.status === 'ACTIVE') {
            buttons += `
                <button onclick="FlashSaleAdmin.updateStatus(${flashSale.id}, 'ENDED')" 
                        class="btn btn-warning btn-sm" title="Kết thúc">
                    <i class="bx bx-stop"></i>
                </button>
            `;
        }
        
        return buttons;
    }

    /**
     * Delete Flash Sale
     * @param {number} id - Flash sale ID
     */
    function deleteFlashSale(id) {
        if (!confirm('Bạn có chắc muốn xóa Flash Sale ID ' + id + '?\nLưu ý: Tất cả sản phẩm trong flash sale này sẽ bị xóa!')) {
            return;
        }

        // Thêm loading effect
        var deleteBtn = event.target.closest('.delete-btn');
        if (deleteBtn) {
            deleteBtn.disabled = true;
            deleteBtn.innerHTML = '<i class="bx bx-loader bx-spin fs-18"></i>';
        }

        $.ajax({
            url: '/admin/api/flash-sale/' + id,
            type: 'DELETE',
            success: function(response) {
                console.log('✅ Delete success:', response);
                alert('Xóa Flash Sale thành công!');
                
                // Reload table
                var status = document.getElementById("statusSelect").value;
                loadFlashSales(0, status);
            },
            error: function(xhr, status, error) {
                // Khôi phục button
                if (deleteBtn) {
                    deleteBtn.disabled = false;
                    deleteBtn.innerHTML = '<i class="bx bx-trash fs-18"></i>';
                }
                
                console.error('❌ Delete error:', xhr, status, error);
                var msg = getErrorMessage(xhr, error);
                alert('Lỗi xóa Flash Sale: ' + msg);
            }
        });
    }

    /**
     * Update Flash Sale Status
     * @param {number} id - Flash sale ID
     * @param {string} newStatus - New status
     */
    function updateStatus(id, newStatus) {
        var confirmMsg = 'Bạn có chắc muốn ';
        if (newStatus === 'ACTIVE') confirmMsg += 'kích hoạt';
        else if (newStatus === 'ENDED') confirmMsg += 'kết thúc';
        else if (newStatus === 'CANCELLED') confirmMsg += 'hủy';
        confirmMsg += ' Flash Sale này?';

        if (!confirm(confirmMsg)) {
            return;
        }

        $.ajax({
            url: '/admin/api/flash-sale/' + id + '/status',
            type: 'PUT',
            data: { status: newStatus },
            success: function(response) {
                console.log('✅ Update status success:', response);
                alert('Cập nhật trạng thái thành công!');
                
                // Reload table
                var status = document.getElementById("statusSelect").value;
                loadFlashSales(0, status);
            },
            error: function(xhr, status, error) {
                console.error('❌ Update status error:', xhr, status, error);
                var msg = getErrorMessage(xhr, error);
                alert('Lỗi cập nhật trạng thái: ' + msg);
            }
        });
    }

    /**
     * Get status badge HTML
     */
    function getStatusBadge(status) {
        var badgeClass = '';
        var statusText = '';
        var icon = '';

        switch(status) {
            case 'SCHEDULED':
                badgeClass = 'bg-warning-subtle text-warning';
                statusText = 'Sắp diễn ra';
                icon = 'bx-time';
                break;
            case 'ACTIVE':
                badgeClass = 'bg-success-subtle text-success';
                statusText = 'Đang diễn ra';
                icon = 'bx-play-circle';
                break;
            case 'ENDED':
                badgeClass = 'bg-secondary-subtle text-secondary';
                statusText = 'Đã kết thúc';
                icon = 'bx-stop-circle';
                break;
            case 'CANCELLED':
                badgeClass = 'bg-danger-subtle text-danger';
                statusText = 'Đã hủy';
                icon = 'bx-x-circle';
                break;
            default:
                badgeClass = 'bg-secondary-subtle text-secondary';
                statusText = status;
                icon = 'bx-question-mark';
        }

        return `<span class="badge ${badgeClass}"><i class="bx ${icon}"></i> ${statusText}</span>`;
    }

    /**
     * Format DateTime
     */
    function formatDateTime(dateTimeString) {
        if (!dateTimeString) return '';
        
        var date = new Date(dateTimeString);
        var day = pad(date.getDate());
        var month = pad(date.getMonth() + 1);
        var year = date.getFullYear();
        var hours = pad(date.getHours());
        var minutes = pad(date.getMinutes());
        
        return `${day}/${month}/${year} ${hours}:${minutes}`;
    }

    function pad(num) {
        return num < 10 ? '0' + num : num;
    }

    /**
     * Update Pagination
     */
    function updatePagination(currentPage, totalPages, status) {
        var pagination = $('#pagination');
        pagination.empty();

        if (totalPages <= 1) return;

        // Previous button
        var prevClass = currentPage === 0 ? 'disabled' : '';
        pagination.append(`
            <li class="page-item ${prevClass}">
                <a class="page-link" href="javascript:void(0);" 
                   onclick="FlashSaleAdmin.loadFlashSales(${currentPage - 1}, '${status}')">
                    Trước
                </a>
            </li>
        `);

        // Page numbers
        for (var i = 0; i < totalPages; i++) {
            var activeClass = i === currentPage ? 'active' : '';
            pagination.append(`
                <li class="page-item ${activeClass}">
                    <a class="page-link" href="javascript:void(0);" 
                       onclick="FlashSaleAdmin.loadFlashSales(${i}, '${status}')">
                        ${i + 1}
                    </a>
                </li>
            `);
        }

        // Next button
        var nextClass = currentPage === totalPages - 1 ? 'disabled' : '';
        pagination.append(`
            <li class="page-item ${nextClass}">
                <a class="page-link" href="javascript:void(0);" 
                   onclick="FlashSaleAdmin.loadFlashSales(${currentPage + 1}, '${status}')">
                    Tiếp
                </a>
            </li>
        `);
    }

    /**
     * Get Error Message
     */
    function getErrorMessage(xhr, error) {
        var msg = '';
        
        if (xhr.status === 401) {
            msg = 'Chưa đăng nhập. Đang chuyển hướng...';
            setTimeout(function() { window.location.href = '/login'; }, 1000);
        } else if (xhr.status === 403) {
            msg = 'Không có quyền truy cập (Admin only).';
        } else if (xhr.status === 404) {
            msg = 'Không tìm thấy dữ liệu.';
        } else if (xhr.status === 500) {
            msg = 'Lỗi server: ' + (xhr.responseText || 'Vui lòng thử lại.');
        } else if (xhr.status === 0) {
            msg = 'Không thể kết nối đến server.';
        } else {
            msg = 'Status: ' + xhr.status + ' - ' + error;
        }
        
        return msg;
    }

    // ========================================
    // DOCUMENT READY
    // ========================================
    $(document).ready(function() {
        // Check if we're on the flash sale list page
        if ($('#flashSaleTable').length > 0) {
            console.log('🔥 Flash Sale Admin loaded');
            loadFlashSales(0, '');
        }
    });

})();
