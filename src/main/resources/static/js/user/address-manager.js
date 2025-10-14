/**
 * Address Management with RESTful API + Ajax + jQuery
 * File: /js/user/address-manager.js
 * DeeG Shoe Shop
 */

$(document).ready(function() {
    
    // Load addresses when page loads
    loadAddresses();
    
    /**
     * Load all addresses from API
     */
    function loadAddresses() {
        $.ajax({
            url: '/api/user/addresses',
            type: 'GET',
            success: function(response) {
                if (response.success) {
                    displayAddresses(response.addresses);
                } else {
                    showMessage(response.message, 'error');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error loading addresses:', error);
                if (xhr.status === 401) {
                    showMessage('Vui lòng đăng nhập để xem địa chỉ.', 'error');
                } else {
                    showMessage('Không thể tải danh sách địa chỉ.', 'error');
                }
            }
        });
    }
    
    /**
     * Display addresses in the list - Shopee Style
     */
    function displayAddresses(addresses) {
        const $addressList = $('#address-list');
        $addressList.empty();
        
        if (!addresses || addresses.length === 0) {
            $addressList.html(`
                <div style="text-align: center; padding: 40px 20px; color: #999;">
                    <i class="fa fa-map-marker" style="font-size: 48px; color: #ddd; margin-bottom: 15px;"></i>
                    <p style="font-size: 14px;">Bạn chưa có địa chỉ nào</p>
                    <p style="font-size: 13px; color: #bbb;">Hãy thêm địa chỉ để nhận hàng thuận tiện hơn</p>
                </div>
            `);
            return;
        }
        
        addresses.forEach(function(address, index) {
            const isDefault = address.isDefault;
            const defaultBadge = isDefault 
                ? '<span style="display: inline-block; border: 1px solid #ee4d2d; color: #ee4d2d; padding: 2px 6px; border-radius: 2px; font-size: 11px; margin-left: 12px; font-weight: 400;">Mặc định</span>' 
                : '';
            
            const addressHtml = `
                <div class="address-item" style="padding: 20px 0; ${index < addresses.length - 1 ? 'border-bottom: 1px solid #efefef;' : ''}">
                    <!-- Tên và SĐT -->
                    <div style="margin-bottom: 12px;">
                        <span style="font-size: 16px; font-weight: 600; color: #333;">${address.recipientName || 'Người nhận'}</span>
                        <span style="color: #ddd; margin: 0 8px;">|</span>
                        <span style="font-size: 14px; color: #999;">${address.recipientPhone || ''}</span>
                        ${defaultBadge}
                    </div>
                    
                    <!-- Địa chỉ chi tiết -->
                    <div style="margin-bottom: 12px;">
                        <div style="font-size: 14px; color: #666; line-height: 1.6;">
                            ${address.street || ''}
                        </div>
                        <div style="font-size: 14px; color: #999; margin-top: 4px;">
                            ${address.city || ''}, ${address.country || 'Việt Nam'}
                        </div>
                    </div>
                    
                    <!-- Action buttons -->
                    <div style="display: flex; gap: 12px;">
                        ${!isDefault ? `
                            <button type="button" class="btn-set-default" data-id="${address.id}" 
                                    style="background: white; color: #ee4d2d; border: 1px solid #ee4d2d; padding: 6px 16px; 
                                           border-radius: 4px; cursor: pointer; font-size: 13px; font-weight: 500; transition: all 0.3s;"
                                    onmouseover="this.style.background='#fff5f3'" onmouseout="this.style.background='white'">
                                Đặt làm mặc định
                            </button>
                        ` : ''}
                        <button type="button" class="btn-delete-address" data-id="${address.id}"
                                style="background: white; color: #555; border: 1px solid #ddd; padding: 6px 16px; 
                                       border-radius: 4px; cursor: pointer; font-size: 13px; transition: all 0.3s;"
                                onmouseover="this.style.background='#f5f5f5'; this.style.color='#ee4d2d'; this.style.borderColor='#ee4d2d'" 
                                onmouseout="this.style.background='white'; this.style.color='#555'; this.style.borderColor='#ddd'">
                            Xóa
                        </button>
                    </div>
                </div>
            `;
            
            $addressList.append(addressHtml);
        });
        
        // Attach event handlers
        attachAddressEventHandlers();
    }
    
    /**
     * Attach event handlers to address buttons
     */
    function attachAddressEventHandlers() {
        // Set default button
        $('.btn-set-default').off('click').on('click', function() {
            const addressId = $(this).data('id');
            setDefaultAddress(addressId);
        });
        
        // Delete button
        $('.btn-delete-address').off('click').on('click', function() {
            const addressId = $(this).data('id');
            if (confirm('Bạn có chắc chắn muốn xóa địa chỉ này?')) {
                deleteAddress(addressId);
            }
        });
    }
    
    /**
     * Handle Add Address Form Submit
     */
    $('#addAddressForm').on('submit', function(e) {
        e.preventDefault();
        
        // ⭐ Get form data - PHIÊN BẢN ĐƠN GIẢN
        const recipientName = $('#recipientName').val().trim();
        const recipientPhone = $('#recipientPhone').val().trim();
        const selectedAddress = $('#selectedAddress').val().trim();
        const latitude = $('#latitude').val();
        const longitude = $('#longitude').val();
        const street = $('#street').val().trim();
        const city = $('#city').val().trim();
        const country = $('#country').val(); // Hidden field = "Việt Nam"
        const addressType = $('#addressType').val() || 'HOME';
        const isDefault = $('#isDefault').is(':checked');
        
        // ⭐ Validate - Kiểm tra các field bắt buộc
        if (!recipientName) {
            showMessage('Vui lòng nhập tên người nhận!', 'error');
            return;
        }
        
        if (!recipientPhone) {
            showMessage('Vui lòng nhập số điện thoại người nhận!', 'error');
            return;
        }
        
        // Validate phone format
        const phoneRegex = /^0\d{9}$/;
        if (!phoneRegex.test(recipientPhone)) {
            showMessage('Số điện thoại không hợp lệ! Phải là 10 số và bắt đầu bằng 0.', 'error');
            return;
        }
        
        if (!selectedAddress) {
            showMessage('Vui lòng chọn địa chỉ trên bản đồ!', 'error');
            return;
        }
        
        if (!street) {
            showMessage('Vui lòng nhập số nhà, tên đường!', 'error');
            return;
        }
        
        if (!city) {
            showMessage('Vui lòng chọn địa chỉ trên bản đồ để tự động điền tỉnh/thành phố!', 'error');
            return;
        }
        
        // ⭐ Prepare data - CHỈ GỬI NHỮNG GÌ CẦN THIẾT
        const addressData = {
            // Thông tin người nhận
            recipientName: recipientName,
            recipientPhone: recipientPhone,
            
            // Địa chỉ từ Goong Maps (bao gồm GPS)
            selectedAddress: selectedAddress,
            latitude: parseFloat(latitude) || null,   // GPS cho shipper & tính phí
            longitude: parseFloat(longitude) || null,
            
            // Chi tiết địa chỉ
            street: street,          // Bao gồm: số nhà, đường, phường, quận
            city: city,              // Tỉnh/Thành phố
            country: country,        // Mặc định "Việt Nam"
            postalCode: null,
            
            // Metadata
            addressType: addressType,
            isDefault: isDefault
        };
        
        // Disable submit button
        const $submitBtn = $(this).find('button[type="submit"]');
        const originalText = $submitBtn.text();
        $submitBtn.prop('disabled', true).text('Đang lưu...');
        
        // Submit via AJAX
        $.ajax({
            url: '/api/user/addresses',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(addressData),
            success: function(response) {
                if (response.success) {
                    showMessage(response.message, 'success');
                    
                    // Reload addresses
                    loadAddresses();
                    
                    // Close modal
                    $('#addAddressModal').modal('hide');
                    
                    // Reset form
                    resetAddAddressForm();
                } else {
                    showMessage(response.message, 'error');
                }
                
                $submitBtn.prop('disabled', false).text(originalText);
            },
            error: function(xhr, status, error) {
                console.error('Error adding address:', error);
                let errorMessage = 'Có lỗi xảy ra khi thêm địa chỉ.';
                
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                
                showMessage(errorMessage, 'error');
                $submitBtn.prop('disabled', false).text(originalText);
            }
        });
    });
    
    /**
     * Set default address
     */
    function setDefaultAddress(addressId) {
        $.ajax({
            url: `/api/user/addresses/${addressId}/set-default`,
            type: 'PUT',
            success: function(response) {
                if (response.success) {
                    showMessage(response.message, 'success');
                    loadAddresses();
                } else {
                    showMessage(response.message, 'error');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error setting default address:', error);
                showMessage('Có lỗi xảy ra khi đặt địa chỉ mặc định.', 'error');
            }
        });
    }
    
    /**
     * Delete address
     */
    function deleteAddress(addressId) {
        $.ajax({
            url: `/api/user/addresses/${addressId}`,
            type: 'DELETE',
            success: function(response) {
                if (response.success) {
                    showMessage(response.message, 'success');
                    loadAddresses();
                } else {
                    showMessage(response.message, 'error');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error deleting address:', error);
                showMessage('Có lỗi xảy ra khi xóa địa chỉ.', 'error');
            }
        });
    }
    
    /**
     * Show message
     */
    function showMessage(message, type) {
        const $errorDiv = $('#error-message');
        const $errorText = $('#error-text');
        
        $errorText.text(message);
        
        if (type === 'success') {
            $errorDiv.removeClass('alert-danger').addClass('alert-success');
        } else {
            $errorDiv.removeClass('alert-success').addClass('alert-danger');
        }
        
        $errorDiv.fadeIn();
        
        // Auto hide after 5 seconds
        setTimeout(function() {
            $errorDiv.fadeOut();
        }, 5000);
        
        // Scroll to message
        $('html, body').animate({
            scrollTop: $errorDiv.offset().top - 100
        }, 500);
    }
    
    /**
     * Reset Add Address Form - Reset TẤT CẢ fields
     */
    function resetAddAddressForm() {
        $('#addAddressForm')[0].reset();
        
        // Reset text inputs
        $('#recipientName').val('');
        $('#recipientPhone').val('');
        $('#selectedAddress').val('');
        $('#latitude').val('');
        $('#longitude').val('');
        $('#street').val('');
        $('#city').val('');
        $('#mapSearchInput').val('');
        
        // Reset select and checkbox
        $('#addressType').val('HOME');
        $('#isDefault').prop('checked', false);
    }
    
    /**
     * Reset form when modal closes
     */
    $('#addAddressModal').on('hidden.bs.modal', function() {
        resetAddAddressForm();
    });
});
