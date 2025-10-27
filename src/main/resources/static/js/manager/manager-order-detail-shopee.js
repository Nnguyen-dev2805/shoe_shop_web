// ========== SHOPEE-STYLE MANAGER ORDER DETAIL JAVASCRIPT ==========

// Toggle Payment Details
function togglePaymentDetails() {
    const details = document.getElementById('paymentDetails');
    const chevron = document.getElementById('paymentChevron');
    
    if (details.classList.contains('show')) {
        details.classList.remove('show');
        chevron.classList.remove('rotated');
    } else {
        details.classList.add('show');
        chevron.classList.add('rotated');
    }
}

// Function format giá VNĐ
function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Function chuyển đổi status sang tiếng Việt
function getStatusInVietnamese(status) {
    const statusMap = {
        'IN_STOCK': 'Đang chờ duyệt',
        'SHIPPED': 'Đang giao hàng',
        'DELIVERED': 'Đã giao hàng',
        'CANCEL': 'Đã hủy',
        'PENDING': 'Chờ xử lý',
        'PROCESSING': 'Đang xử lý',
        'READY_TO_SHIP': 'Sẵn sàng giao hàng'
    };
    return statusMap[status] || status;
}

// Toggle search box
function turnOnSearch() {
    const searchBox = document.getElementById('searchBox');
    searchBox.style.display = 'block';
    searchBox.scrollIntoView({ behavior: 'smooth' });
    loadAllShippers();
}

// Popup cancel order - scoped to avoid conflicts
(function() {
    window.managerSelectedOrderId = null;
    
    window.popUpClick = function(orderId) {
        let modal = new bootstrap.Modal(document.getElementById('confirmModal'));
        modal.show();
        window.managerSelectedOrderId = orderId;
        console.log("🚨 Order ID selected for cancellation:", window.managerSelectedOrderId);
    };
})();

// Load all shippers
function loadAllShippers() {
    var currentUrl = window.location.pathname;
    var orderid = currentUrl.split('/')[currentUrl.split('/').length - 1];

    $.ajax({
        url: "/user/shipper/all",
        type: "GET",
        success: function(response) {
            $("#resultSearch tbody").empty();
            let listShipper = response.listShipper;

            if (listShipper && listShipper.length > 0) {
                listShipper.forEach(function(user) {
                    $("#resultSearch tbody").append(
                        "<tr>" +
                        "<td>" + user.id + "</td>" +
                        "<td>" + user.fullname + "</td>" +
                        "<td style='text-align: center;'><a class='btn-shopee' style='padding: 8px 16px; font-size: 13px; text-decoration: none;' href='/manager/order/shipping?orderid=" + orderid + "&userid=" + user.id + "'>Giao hàng</a></td>" +
                        "</tr>"
                    );
                });
            } else {
                $("#resultSearch tbody").append("<tr><td colspan='3' style='text-align: center; color: #999;'>Không có shipper nào</td></tr>");
            }
        },
        error: function(error) {
            console.error("Lỗi khi gọi API", error);
        }
    });
}

// Main AJAX - Load order details
$(document).ready(function() {
    var currentUrl = window.location.pathname;
    var orderId = currentUrl.split('/')[currentUrl.split('/').length - 1];
    
    console.log("🔍 Current URL:", currentUrl);
    console.log("🔍 Order ID:", orderId);
    console.log("🔍 API URL:", "/manager/order/detail/" + orderId);
    
    // ========== READY TO SHIP BUTTON ==========
    $("#makeReadyShipBtn").off("click").on("click", function() {
        console.log("✅ Ready to Ship button clicked!");
        turnOnSearch();
    });
    
    // ========== CANCEL ORDER MODAL HANDLERS ==========
    $("#confirmCancelBtn").off("click").on("click", function() {
        if (window.managerSelectedOrderId) {
            console.log("⚠️ Cancelling order ID:", window.managerSelectedOrderId);
            
            $.ajax({
                url: "/manager/order/cancel",
                type: "POST",
                data: { orderId: window.managerSelectedOrderId },
                success: function(response) {
                    console.log("✅ Order cancelled successfully:", response);
                    alert("Đơn hàng #" + window.managerSelectedOrderId + " đã được hủy thành công!");
                    $("#confirmModal").modal("hide");
                    location.reload();
                },
                error: function(xhr) {
                    console.error("❌ Cancel order failed:", xhr);
                    let errMsg = "Có lỗi xảy ra khi hủy đơn hàng";
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errMsg = xhr.responseJSON.message;
                    }
                    alert(errMsg);
                }
            });
        }
    });
    
    // Close modal button
    $("#closeModalBtn").off("click").on("click", function() {
        $("#confirmModal").modal("hide");
        console.log("❌ Cancel order cancelled");
    });
    
    // Search shipper by name
    $("#searchButton").click(function() {
        var fullname = $("#searchInput").val();

        if (fullname.trim() !== "") {
            $.ajax({
                url: "/user/shipper/search",
                type: "GET",
                data: { name: fullname },
                success: function(response) {
                    $("#resultSearch tbody").empty();
                    let listShipper = response.listShipper;

                    if (listShipper && listShipper.length > 0) {
                        listShipper.forEach(function(user) {
                            $("#resultSearch tbody").append(
                                "<tr>" +
                                "<td>" + user.id + "</td>" +
                                "<td>" + user.fullname + "</td>" +
                                "<td style='text-align: center;'><a class='btn-shopee' style='padding: 8px 16px; font-size: 13px; text-decoration: none;' href='/manager/order/shipping?orderid=" + orderId + "&userid=" + user.id + "'>Giao hàng</a></td>" +
                                "</tr>"
                            );
                        });
                    } else {
                        $("#resultSearch tbody").append("<tr><td colspan='3' style='text-align: center; color: #999;'>Không tìm thấy shipper nào</td></tr>");
                    }
                },
                error: function(error) {
                    console.error("Lỗi khi gọi API", error);
                }
            });
        }
    });
    
    // Load order details
    console.log("📡 Starting AJAX call to load order details...");
    $.ajax({
        url: "/manager/order/detail/" + orderId,
        type: "GET",
        dataType: "json",
        success: function(response) {
            console.log("✅ === API Response SUCCESS ===", response);
            
            var order = response.order;
            var list = response.listOrderDetail;
            var payment = response.orderPayment;
            var shipment = response.shipment;
            
            // ========== 1. FILL ORDER INFO ==========
            console.log("📦 1. Loading Order Info...");
            if (order) {
                console.log("Order data:", order);
                
                // Order ID
                $("#orderId").text(order.id || "N/A");
                
                // Status
                var statusText = getStatusInVietnamese(order.status);
                $("#statusText").text(statusText || "Không xác định");
                console.log("✅ Status:", statusText);
                
                // Payment Method
                $("#orderPayOption").text(order.payOption || "Không xác định");
                
                // Created Date
                if (order.createdDate) {
                    var createdDate = new Date(order.createdDate).toLocaleString('vi-VN', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                    $("#orderCreatedDate").text(createdDate);
                    console.log("✅ Created Date:", createdDate);
                }
                
                // Set status banner color
                var statusBanner = document.getElementById('statusBanner');
                if (statusBanner) {
                    statusBanner.className = 'status-banner shopee-card ' + order.status;
                }
                
                // Show/hide buttons based on status
                if (order.status !== "IN_STOCK") {
                    $("#makeReadyShipBtn").prop("disabled", true).hide();
                    $("#cancelBtn").hide();
                } else {
                    $("#makeReadyShipBtn").prop("disabled", false).show();
                    $("#cancelBtn").show();
                }
                
                // Cancel button
                $("#cancelBtn").off("click").on("click", function() {
                    popUpClick(order.id);
                });
                
                console.log("✅ Order Info loaded successfully");
            } else {
                console.error("❌ No order data found");
            }
            
            // ========== 2. FILL CUSTOMER INFO ==========
            console.log("👤 2. Loading Customer Info...");
            if (order && order.user) {
                console.log("Customer data:", order.user);
                
                // Customer Name
                var customerName = order.user.fullname || order.user.username || "Không có tên";
                $("#customerName").text(customerName);
                console.log("✅ Name:", customerName);
                
                // Customer Phone
                var customerPhone = order.user.phone || "Chưa cập nhật";
                $("#customerPhone").text(customerPhone);
                console.log("✅ Phone:", customerPhone);
                
                // Customer Address
                var customerAddress = order.user.address || "Chưa cập nhật";
                $("#customerAddress").text(customerAddress);
                console.log("✅ Address:", customerAddress);
                
                // Customer Avatar
                if (order.user.avatar) {
                    $("#customerAvatar").attr("src", order.user.avatar);
                }
                
                console.log("✅ Customer Info loaded successfully");
            } else {
                console.error("❌ No customer data found");
            }
            
            // ========== 3. FILL PRODUCT LIST ==========
            console.log("🛍️ 3. Loading Product List...");
            if (list && list.length > 0) {
                console.log("Products data:", list);
                console.log("Total products:", list.length);
                
                // Update product count
                $("#productCount").text(list.length);
                
                // Clear existing products
                $("#productList").empty();
                
                // Add each product
                list.forEach(function(item, index) {
                    console.log(`Product ${index + 1}:`, {
                        name: item.product_name,
                        size: item.size,
                        quantity: item.quantity,
                        price: item.price
                    });
                    
                    var productHtml = 
                        '<div class="product-item-shopee">' +
                        '    <img src="' + (item.image || '/img/product/1.png') + '" ' +
                        '         alt="' + (item.product_name || 'Product') + '" ' +
                        '         class="product-image-shopee" ' +
                        '         onerror="this.src=\'/img/product/1.png\'">' +
                        '    <div class="product-info-shopee">' +
                        '        <p class="product-name-shopee">' + (item.product_name || 'Sản phẩm') + '</p>' +
                        '        <p class="product-variant-shopee">Size: ' + (item.size || 'N/A') + '</p>' +
                        '        <div class="product-bottom-shopee">' +
                        '            <span class="product-quantity-shopee">x' + (item.quantity || 0) + '</span>' +
                        '            <span class="product-price-shopee">' + formatVND(item.price || 0) + '</span>' +
                        '        </div>' +
                        '    </div>' +
                        '</div>';
                    
                    $("#productList").append(productHtml);
                });
                
                console.log("✅ Product List loaded successfully (" + list.length + " items)");
            } else {
                console.error("❌ No products found");
                $("#productCount").text(0);
                $("#productList").html('<p style="padding: 16px; text-align: center; color: #999;">Không có sản phẩm</p>');
            }
            
            // Fill payment summary from database
            if (payment) {
                console.log("Payment data:", payment);
                console.log("Order fields:", {
                    shippingFee: order.shippingFee,
                    shippingDiscountAmount: order.shippingDiscountAmount,
                    pointsEarned: order.pointsEarned
                });
                
                // Thành tiền (Total)
                $("#total").text(formatVND(payment.totalpay));
                
                // Tổng tiền hàng (Subtotal)
                $("#subtotal").text(formatVND(payment.subtotal));
                
                // Phí vận chuyển (Shipping Fee)
                var shippingFee = order.shippingFee || 0;
                $("#shippingFee").text(formatVND(shippingFee));
                
                // Ưu đãi phí vận chuyển (Shipping Discount)
                var shippingDiscount = order.shippingDiscountAmount || 0;
                if (shippingDiscount > 0) {
                    $("#shippingDiscount").text('-' + formatVND(shippingDiscount));
                    $("#shippingDiscount").closest('.payment-row').show();
                } else {
                    $("#shippingDiscount").closest('.payment-row').hide();
                }
                
                // Voucher giảm giá (Discount)
                var discount = payment.discount || 0;
                if (discount > 0) {
                    $("#voucherDiscount").text('-' + formatVND(discount));
                    $("#voucherDiscount").closest('.payment-row').show();
                } else {
                    $("#voucherDiscount").closest('.payment-row').hide();
                }
                
                // Điểm tích lũy (Points Earned)
                var pointsEarned = order.pointsEarned || 0;
                if (pointsEarned > 0) {
                    $("#pointsEarned").text('+' + pointsEarned + ' điểm');
                    $(".payment-points").show();
                } else {
                    $(".payment-points").hide();
                }
            } else {
                console.warn("No payment data available");
                // Set default values if no payment data
                $("#total").text("0đ");
                $("#subtotal").text("0đ");
                $("#shippingFee").text("0đ");
            }
            
            // Fill shipper info
            console.log("📦 Shipment data:", shipment);
            if (shipment && shipment.shipperId) {
                $("#shipperId").text(shipment.shipperId || "Không có");
                $("#shipperName").text(shipment.shipperName || "Không có tên");
                $("#shipperPhone").text(shipment.shipperPhone || "Chưa cập nhật");
                $("#shipperEmail").text(shipment.shipperEmail || "Chưa cập nhật");
                $("#shipperAddress").text(shipment.shipperAddress || "Chưa cập nhật");
                
                // Format ngày giao hàng
                if (shipment.shipmentDate || shipment.createdDate) {
                    var shipDate = new Date(shipment.shipmentDate || shipment.createdDate);
                    $("#shipmentDate").text(shipDate.toLocaleString('vi-VN'));
                } else {
                    $("#shipmentDate").text("Chưa cập nhật");
                }
                
                // Hiển thị ghi chú nếu có
                if (shipment.note && shipment.note.trim() !== "") {
                    $("#shipperNote").text(shipment.note);
                    $("#shipperNoteSection").show();
                } else {
                    $("#shipperNoteSection").hide();
                }
                
                // Hiển thị box shipper info, ẩn box need shipper
                $("#shipperInfoBox").show();
                $("#needShipperBox").hide();
            } else {
                // Chưa có shipper - hiển thị thông báo cần shipper
                $("#shipperInfoBox").hide();
                $("#needShipperBox").show();
            }
        },
        error: function(xhr, status, error) {
            console.error("❌ === API Response ERROR ===");
            console.error("Status:", status);
            console.error("Error:", error);
            console.error("XHR:", xhr);
            console.error("Response Text:", xhr.responseText);
            
            alert("Lỗi khi tải chi tiết đơn hàng!\nStatus: " + status + "\nError: " + error + "\nVui lòng kiểm tra console (F12) để xem chi tiết.");
        }
    });
});

