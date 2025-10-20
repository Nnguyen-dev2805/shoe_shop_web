/**
 * PayOS Order Confirmation Handler
 * Checks if user returned from PayOS payment and creates order
 */

$(document).ready(function() {
    console.log('=== PayOS Order Confirm Script Loaded ===');
    console.log('Current URL:', window.location.href);
    console.log('Current Time:', new Date().toISOString());
    
    // Check if user just returned from PayOS payment
    const payosPaymentPending = sessionStorage.getItem('payosPaymentPending');
    const payosPaymentData = sessionStorage.getItem('payosPaymentData');
    
    console.log('PayOS Payment Pending:', payosPaymentPending);
    console.log('PayOS Payment Data exists:', payosPaymentData ? 'YES' : 'NO');
    
    // DEBUG: Show all sessionStorage keys
    console.log('All SessionStorage keys:');
    for (let i = 0; i < sessionStorage.length; i++) {
        const key = sessionStorage.key(i);
        console.log('  -', key, ':', sessionStorage.getItem(key));
    }
    
    if (payosPaymentPending === 'true' && payosPaymentData) {
        console.log('=== PayOS Payment Return Detected ===');
        console.log('Creating order after PayOS payment confirmation...');
        console.log('Payment Data:', payosPaymentData);
        
        // Show alert to confirm script is running
        alert('🔍 DEBUG: PayOS payment detected! Creating order...');
        
        try {
            const paymentData = JSON.parse(payosPaymentData);
            
            // Show loading overlay
            showLoadingOverlay('Đang tạo đơn hàng sau khi thanh toán...');
            
            // Create order now
            $.ajax({
                url: '/api/order/pay',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(paymentData),
                success: function(response) {
                    console.log('✅ Order created after PayOS payment:', response);
                    
                    // Clear PayOS session data
                    sessionStorage.removeItem('payosPaymentPending');
                    sessionStorage.removeItem('payosPaymentData');
                    
                    // Clear checkout session data
                    sessionStorage.removeItem('selectedItems');
                    sessionStorage.removeItem('selectedCartItems');
                    sessionStorage.removeItem('selectedAddressId');
                    sessionStorage.removeItem('cartId');
                    sessionStorage.removeItem('checkoutSource');
                    sessionStorage.removeItem('buyNowMode');
                    
                    hideLoadingOverlay();
                    
                    if (response.success && response.data && response.data.orderId) {
                        // Show success message
                        showSuccessMessage('Thanh toán thành công! Đơn hàng #' + response.data.orderId + ' đã được tạo.');
                        
                        // Reload page to show new order
                        setTimeout(function() {
                            window.location.reload();
                        }, 1500);
                    } else {
                        showErrorMessage('Có lỗi khi tạo đơn hàng. Vui lòng liên hệ hỗ trợ.');
                    }
                },
                error: function(xhr, status, error) {
                    console.error('❌ Failed to create order after PayOS payment:', error);
                    console.error('Response:', xhr.responseText);
                    
                    // Don't clear session data on error - allow retry
                    hideLoadingOverlay();
                    
                    let errorMessage = 'Có lỗi khi tạo đơn hàng sau thanh toán.';
                    try {
                        const errorResponse = JSON.parse(xhr.responseText);
                        if (errorResponse.message) {
                            errorMessage = errorResponse.message;
                        }
                    } catch (e) {
                        // Keep default message
                    }
                    
                    showErrorMessage(errorMessage + ' Vui lòng liên hệ hỗ trợ.');
                }
            });
            
        } catch (e) {
            console.error('❌ Error parsing PayOS payment data:', e);
            // Clear invalid data
            sessionStorage.removeItem('payosPaymentPending');
            sessionStorage.removeItem('payosPaymentData');
        }
    }
});

// Helper functions
function showLoadingOverlay(message) {
    const overlay = $('<div>')
        .attr('id', 'payos-loading-overlay')
        .css({
            'position': 'fixed',
            'top': 0,
            'left': 0,
            'width': '100%',
            'height': '100%',
            'background': 'rgba(0, 0, 0, 0.7)',
            'z-index': 9999,
            'display': 'flex',
            'align-items': 'center',
            'justify-content': 'center',
            'flex-direction': 'column'
        });
    
    const spinner = $('<div>')
        .html('<i class="fa fa-spinner fa-spin" style="font-size: 48px; color: white;"></i>')
        .css('margin-bottom', '20px');
    
    const text = $('<div>')
        .text(message)
        .css({
            'color': 'white',
            'font-size': '18px',
            'text-align': 'center'
        });
    
    overlay.append(spinner).append(text);
    $('body').append(overlay);
}

function hideLoadingOverlay() {
    $('#payos-loading-overlay').remove();
}

function showSuccessMessage(message) {
    alert('✅ ' + message);
}

function showErrorMessage(message) {
    alert('❌ ' + message);
}
