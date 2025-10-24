$(document).ready(function (){
    let productList = []; // Store product data for later use
    
    // Load products
    $.ajax({
        url: "/api/product/list",
        type: "GET",
        success: function (data) {
            productList = data; // Cache product data
            let $productSelect = $("#productId");
            $productSelect.empty();
            $productSelect.append('<option value="">Chọn sản phẩm</option>');

            $.each(data, function (index, product) {
                $productSelect.append(
                    `<option value="${product.id}" data-price="${product.price}">${product.title}</option>`
                );
            });
        },
        error: function (xhr, status, error) {
            console.error("Lỗi khi load products:", error);
        }
    });
    
    // Auto-fill cost price when product is selected
    $("#productId").on("change", function() {
        const productId = $(this).val();
        
        if (!productId) {
            $("#priceInfoBox").hide();
            $("#costPrice").val("");
            return;
        }
        
        // Find selected product from cached data
        const selectedProduct = productList.find(p => p.id == productId);
        
        if (selectedProduct && selectedProduct.price) {
            const sellingPrice = selectedProduct.price;
            const suggestedCost = Math.round(sellingPrice * 0.8); // 80% of selling price
            
            // Display price info
            $("#productSellingPrice").text(sellingPrice.toLocaleString('vi-VN') + 'đ');
            $("#suggestedCostPrice").text(suggestedCost.toLocaleString('vi-VN') + 'đ');
            $("#priceInfoBox").show();
            
            // Auto-fill cost price (user can edit)
            $("#costPrice").val(suggestedCost);
            
            console.log(`Product: ${selectedProduct.title} | Selling: ${sellingPrice}đ | Suggested Cost: ${suggestedCost}đ`);
        } else {
            $("#priceInfoBox").hide();
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

        // ✅ Get cost price and other fields
        const costPrice = parseFloat($("#costPrice").val());
        const importDate = $("#importDate").val();
        const note = $("#note").val();
        
        // Validate cost price
        if (!costPrice || costPrice <= 0) {
            alert("⚠️ Vui lòng nhập giá nhập hợp lệ!");
            return;
        }
        
        // ✅ Check if cost price exceeds 80% of selling price (optional warning)
        const selectedProduct = productList.find(p => p.id == productId);
        if (selectedProduct && selectedProduct.price) {
            const maxCost = selectedProduct.price * 0.8;
            if (costPrice > maxCost * 1.1) { // Allow 10% margin
                const confirmHighCost = confirm(
                    `⚠️ Cảnh báo: Giá nhập (${costPrice.toLocaleString('vi-VN')}đ) cao hơn đề xuất (${maxCost.toLocaleString('vi-VN')}đ).\n\n` +
                    `Điều này có thể làm giảm lợi nhuận. Bạn có chắc muốn tiếp tục?`
                );
                if (!confirmHighCost) return;
            }
        }
        
        const confirmSend = confirm("📦 Bạn có chắc chắn muốn lưu tồn kho này không?");
        if (!confirmSend) return;

        const requestData = {
            productId: productId,
            sizes: sizes,
            costPrice: costPrice,  // ✅ NEW
            importDate: importDate || new Date().toISOString(),  // ✅ NEW
            note: note || "",  // ✅ NEW
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