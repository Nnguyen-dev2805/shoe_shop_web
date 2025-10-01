$(document).ready(function (){
    $.ajax({
        url: "/api/product/list",
        type: "GET",
        success: function (data) {
            let $productSelect = $("#productId");
            $productSelect.empty();
            $productSelect.append('<option value="">Chọn sản phẩm</option>');

            $.each(data, function (index, product) {
                $productSelect.append(
                    `<option value="${product.id}">${product.title}</option>`
                );
            });
        },
        error: function (xhr, status, error) {
            console.error("Lỗi khi load products:", error);
        }
    });

    $("#saveInventory").click(function (e) {
        e.preventDefault();

        const productId = $("#productId").val();
        if (!productId) {
            alert("⚠️ Vui lòng chọn sản phẩm trước khi lưu tồn kho!");
            return;
        }

        let sizes = {};
        $("input[name^='sizes']").each(function () {
            const size = $(this).attr("name").match(/\d+/)[0];
            let quantity = $(this).val().trim();

            if (quantity === "") return;

            quantity = Number(quantity);

            if (!Number.isInteger(quantity) || quantity < 0) {
                alert(`⚠️ Size ${size} chỉ được nhập số nguyên dương!`);
                $(this).val("");
                return;
            }

            if (quantity > 0) {
                sizes[size] = quantity;
            }
        });

        if (Object.keys(sizes).length === 0) {
            alert("⚠️ Bạn chưa nhập số lượng nào lớn hơn 0!");
            return;
        }

        const confirmSend = confirm("📦 Bạn có chắc chắn muốn lưu tồn kho này không?");
        if (!confirmSend) return;

        const requestData = {
            productId: productId,
            sizes: sizes,
            createdAt: new Date().toISOString()
        };

        console.log("Dữ liệu gửi lên backend:", JSON.stringify(requestData));

        $.ajax({
            url: "/api/inventory",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestData),
            success: function (response) {
                alert("✅ Lưu tồn kho thành công!");
                $("#inventoryForm")[0].reset();
            },
            error: function (xhr, status, error) {
                console.error("❌ Lỗi khi lưu tồn kho:", error);
                alert("❌ Có lỗi xảy ra khi lưu tồn kho. Vui lòng thử lại!");
            }
        });
    });

});