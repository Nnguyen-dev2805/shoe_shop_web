$(document).ready(function () {
    $.ajax({
        url: "/api/category/list",
        type: "GET",
        success: function (data) {
            let $categorySelect = $("#categoryId");
            $categorySelect.empty(); // xóa option cũ
            $categorySelect.append('<option value="">Chọn loại sản phẩm</option>');

            $.each(data, function (index, category) {
                $categorySelect.append(
                    `<option value="${category.id}">${category.name}</option>`
                );
            });
        },
        error: function (xhr, status, error) {
            console.error("Lỗi khi load categories:", error);
        }
    });

    $.ajax({
        url: "/api/brand/list",
        type: "GET",
        success: function (data) {
            let $brandSelect = $("#brandId");
            $brandSelect.empty(); // xóa option cũ
            $brandSelect.append('<option value="">Chọn thương hiệu</option>');

            $.each(data, function (index, brand) {
                $brandSelect.append(
                    `<option value="${brand.id}">${brand.name}</option>`
                );
            });
        },
        error: function (xhr, status, error) {
            console.error("Lỗi khi load brands:", error);
        }
    });

    // Flag để prevent double submission
    let isSubmitting = false;

    $('#productForm').on('submit', function(e) {
        e.preventDefault();
        
        // ✅ Prevent multiple submissions
        if (isSubmitting) {
            console.log('⚠️ Form is already submitting, please wait...');
            return false;
        }
        
        // Set flag và disable button
        isSubmitting = true;
        const $submitBtn = $(this).find('button[type="submit"]');
        const originalText = $submitBtn.html();
        $submitBtn.prop('disabled', true)
                  .html('<span class="spinner-border spinner-border-sm me-1"></span> Đang lưu...');
        
        const productData = {
            title: $('#title').val(),
            categoryId: parseInt($('#categoryId').val()),
            brandId: parseInt($('#brandId').val()),
            description: $('#description').val(),
            voucher: $('#voucher').val() ? parseInt($('#voucher').val()) : null,
            price: $('#price').val() ? parseFloat($('#price').val()) : null,
            productDetails: []
        };
        // // Thu thập giá thêm từng size
        // $('input[name^="productDetails"]').each(function() {
        //     const name = $(this).attr('name'); // ví dụ "productDetails[38]"
        //     const size = name.match(/\[(\d+)\]/)[1]; // lấy số size
        //     const value = $(this).val();
        //     if (value !== '' && !isNaN(value)) {
        //         productData.productDetails[size] = parseFloat(value);
        //     }
        // });

        // Thu thập size
        $('input[name^="productDetails"]').each(function() {
            const name = $(this).attr('name'); // ví dụ "productDetails[38]"
            const size = name.match(/\[(\d+)\]/)[1];
            const value = $(this).val();
            if (value !== '' && !isNaN(value)) {
                productData.productDetails.push({
                    size: parseInt(size),
                    priceAdd: parseFloat(value)
                });
            }
        });

        console.log("JSON gửi lên:", JSON.stringify(productData, null, 2));

        // Tạo FormData để chứa cả JSON và file
        let formData = new FormData();
        if ($('#image')[0].files.length > 0) {
            formData.append("image", $('#image')[0].files[0]);
        }
        // formData.append("image", $('#image')[0].files[0]); // file
        formData.append("request", new Blob([JSON.stringify(productData)], { type: "application/json" })); // JSON

        $.ajax({
            url: '/api/product',
            type: 'POST',
            data: formData,
            processData: false, // bắt buộc với FormData
            contentType: false, // bắt buộc với FormData
            success: function(response) {
                alert("Thêm sản phẩm thành công!");
                // Redirect to product list - no need to reset button
                window.location.href = '/admin/product';
            },
            error: function(xhr) {
                alert("Lỗi khi thêm sản phẩm: " + xhr.responseText);
                
                // ✅ Reset button và flag khi có lỗi
                isSubmitting = false;
                $submitBtn.prop('disabled', false).html(originalText);
            }
        });

        // // Gửi AJAX POST JSON
        // $.ajax({
        //     url: '/api/product',
        //     type: 'POST',
        //     contentType: 'application/json',
        //     data: JSON.stringify(productData),
        //     success: function(response) {
        //         alert('Thêm sản phẩm thành công!');
        //         window.location.href = '/admin/product'; // chuyển về danh sách
        //     },
        //     error: function(xhr) {
        //         let msg = 'Lỗi khi thêm sản phẩm: ';
        //         if (xhr.responseText) {
        //             msg += xhr.responseText;
        //         } else {
        //             msg += xhr.statusText;
        //         }
        //         alert(msg);
        //     }
        // });
    });
});