let currentPage = 1;
const itemsPerPage = 7;
let totalPages = 0;
let searchTerm = '';

$(document).ready(function() {
    loadProducts(currentPage, searchTerm);

    // debounce search
    $('#searchInput').on('keyup', function () {
        clearTimeout(window.searchTimeout);
        const value = $(this).val();
        window.searchTimeout = setTimeout(() => {
            searchTerm = value;
            loadProducts(1, searchTerm);
        }, 500);
    });
});

function loadProducts(page, search) {
    const tbody = $('#productBody');

    $.ajax({
        url: `/api/product`,
        type: 'GET',
        data: {
            page: page - 1,  // backend 0-based
            size: itemsPerPage,
            search: search || ''
        },
        dataType: 'json',
        success: function (response) {
            if (!response || !response.content) {
                tbody.html('<tr><td colspan="6" class="text-center text-danger">Lỗi response từ server.</td></tr>');
                return;
            }

            currentPage = response.currentPage;
            totalPages = response.totalPages;
            searchTerm = search || '';

            renderProducts(response.content);
            renderPagination();
        },
        error: function (xhr, status, error) {
            let msg = 'Lỗi load dữ liệu: ';
            if (xhr.status === 401) {
                msg += 'Chưa đăng nhập. Đang chuyển hướng...';
                setTimeout(() => window.location.href = '/login', 1000);
            } else if (xhr.status === 403) {
                msg += 'Không có quyền truy cập.';
            } else if (xhr.status === 500) {
                msg += 'Lỗi server. Vui lòng thử lại.';
            } else {
                msg += xhr.status + ' - ' + error;
            }
            tbody.html(`<tr><td colspan="6" class="text-center text-danger">${msg}</td></tr>`);
        }
    });
}

function renderProducts(products) {
    const tbody = $('#productBody');
    if (products.length === 0) {
        tbody.html('<tr><td colspan="6" class="text-center">Không có dữ liệu</td></tr>');
        return;
    }

    let rows = '';
    products.forEach(product => {
        rows += `
            <tr>
                <td>${product.id}</td>
                <td>
                    <div class="d-flex align-items-center gap-2">
                        <div class="product-avatar">
                            <img src="${product.image}" 
                                 alt="${product.title}" 
                                 class="product-img"
                                 onerror="this.src='/assets/images/users/avatar-1.jpg';">
                         </div>
                        <div>
                            <a href="/admin/product/detail/${product.id}" 
                               class="text-dark fw-medium fs-15">${product.title}</a>
                        </div>
                    </div>
                </td>
                <td>${product.price ? product.price.toLocaleString('vi-VN') + ' VNĐ' : 'N/A'}</td>
                <td>${product.categoryName || 'N/A'}</td>
                <td>${product.brandName || 'N/A'}</td>
                <td>
                    <div class="d-flex gap-2">
                        <a href="/admin/product/updateProduct/${product.id}" 
                           class="btn btn-soft-primary btn-sm">
                           <iconify-icon icon="solar:pen-2-broken" class="align-middle fs-18"></iconify-icon>
                        </a>
                        <button onclick="deleteProduct(${product.id})" 
                                class="btn btn-soft-danger btn-sm">
                            <iconify-icon icon="solar:trash-bin-minimalistic-2-broken" 
                                          class="align-middle fs-18"></iconify-icon>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
    tbody.html(rows);
}

function renderPagination() {
    const pagination = $('#pagination');

    pagination.empty();

    if (totalPages <= 1) {
        return;  // Không hiển thị pagination nếu chỉ 1 trang
    }

    // Previous button
    let prevClass = currentPage === 1 ? 'disabled' : '';
    let prevOnclick = currentPage > 1 ? 'changePage(' + (currentPage - 1) + ');' : '';
    let prevLi = `<li class="page-item ${prevClass}"><a class="page-link" href="javascript:void(0);" onclick="${prevOnclick}">Trang trước</a></li>`;
    pagination.append(prevLi);

    // Các nút số trang (hiển thị tối đa 5 nút, tập trung vào currentPage)
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, currentPage + 2);
    if (endPage - startPage < 4) {
        if (startPage === 1) {
            endPage = Math.min(5, totalPages);
        } else if (endPage === totalPages) {
            startPage = Math.max(1, totalPages - 4);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        const activeClass = (i === currentPage) ? 'active' : '';
        let pageLi = `
                <li class="page-item ${activeClass}">
                    <a class="page-link" href="javascript:void(0);" onclick="changePage(${i})">${i}</a>
                </li>
            `;
        pagination.append(pageLi);
    }

    // Next button
    let nextClass = currentPage === totalPages ? 'disabled' : '';
    let nextOnclick = currentPage < totalPages ? 'changePage(' + (currentPage + 1) + ');' : '';
    let nextLi = `<li class="page-item ${nextClass}"><a class="page-link" href="javascript:void(0);" onclick="${nextOnclick}">Trang sau</a></li>`;
    pagination.append(nextLi);
    // pagination.empty();
    //
    // const prevDisabled = currentPage === 1 ? 'disabled' : '';
    // pagination.append(`
    //     <li class="page-item ${prevDisabled}">
    //         <a class="page-link" href="javascript:void(0);"
    //            onclick="${prevDisabled ? '' : `loadProducts(${currentPage - 1}, '${searchTerm}')`}">
    //            Previous
    //         </a>
    //     </li>
    // `);
    //
    // for (let i = 1; i <= totalPages; i++) {
    //     const active = i === currentPage ? 'active' : '';
    //     pagination.append(`
    //         <li class="page-item ${active}">
    //             <a class="page-link" href="javascript:void(0);"
    //                onclick="loadProducts(${i}, '${searchTerm}')">${i}</a>
    //         </li>
    //     `);
    // }
    //
    // const nextDisabled = currentPage === totalPages ? 'disabled' : '';
    // pagination.append(`
    //     <li class="page-item ${nextDisabled}">
    //         <a class="page-link" href="javascript:void(0);"
    //            onclick="${nextDisabled ? '' : `loadProducts(${currentPage + 1}, '${searchTerm}')`}">
    //            Next
    //         </a>
    //     </li>
    // `);
}

// Thay đổi trang (gọi loadCategories với page mới, giữ search nếu có)
window.changePage = function(page) {
    if (page < 1 || page > totalPages) return;

    loadProducts(page, searchTerm);  // Giữ searchTerm nếu đang search
};

// Search function (gọi API với param search, reset về trang 1)
function searchProduct () {
    const input = document.getElementById("searchInput");
    const filter = input.value.trim();

    // Debounce: Chỉ gọi API nếu filter thay đổi sau 300ms (tối ưu, tránh gọi liên tục)
    clearTimeout(window.searchTimeout);
    window.searchTimeout = setTimeout(function() {
        searchTerm = filter;  // Cập nhật global searchTerm
        loadProducts(1, searchTerm);  // Load trang 1 với search
    }, 300);
}

function deleteProduct(id) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
        $.ajax({
            url: `/api/product/${id}`,
            type: 'DELETE',
            dataType: 'json',
            success: function () {
                alert('Xóa thành công!');
                loadProducts(currentPage, searchTerm);
            },
            error: function (xhr, status, error) {
                let msg = 'Lỗi xóa sản phẩm: ';
                if (xhr.status === 403) msg += 'Không có quyền.';
                else if (xhr.status === 404) msg += 'Sản phẩm không tồn tại.';
                else msg += xhr.responseJSON ? xhr.responseJSON.message : error;
                alert(msg);
            }
        });
    }
}
