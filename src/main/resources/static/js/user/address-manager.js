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
     * Display addresses in the list
     */
    function displayAddresses(addresses) {
        const $addressList = $('#address-list');
        $addressList.empty();
        
        if (!addresses || addresses.length === 0) {
            $addressList.html('<p style="color: #666; font-style: italic;">Bạn chưa có địa chỉ nào. Hãy thêm địa chỉ mới!</p>');
            return;
        }
        
        addresses.forEach(function(address) {
            const isDefault = address.isDefault;
            const defaultBadge = isDefault 
                ? '<span style="background: #4CAF50; color: white; padding: 2px 8px; border-radius: 3px; font-size: 12px; margin-left: 10px;">Mặc định</span>' 
                : '';
            
            const addressHtml = `
                <div class="address-item" style="border: 1px solid ${isDefault ? '#4CAF50' : '#ddd'}; 
                                                  padding: 15px; margin-bottom: 10px; border-radius: 5px; 
                                                  background: ${isDefault ? '#f0f9f0' : 'white'};">
                    <div style="display: flex; align-items: center; margin-bottom: 8px;">
                        <input type="radio" name="selectedAddress" value="${address.id}" 
                               ${isDefault ? 'checked' : ''} 
                               style="margin-right: 10px; cursor: pointer;">
                        <strong style="font-size: 15px;">${address.address}</strong>
                        ${defaultBadge}
                    </div>
                    <div style="padding-left: 25px; color: #666; font-size: 13px;">
                        <p style="margin: 3px 0;">
                            <i class="fa fa-map-marker"></i> 
                            ${address.addressLine || ''}
                            ${address.district ? ', ' + address.district : ''}
                        </p>
                        <p style="margin: 3px 0;">
                            <i class="fa fa-building"></i> ${address.city || ''}, ${address.country || ''}
                        </p>
                    </div>
                    <div style="padding-left: 25px; margin-top: 10px;">
                        <button type="button" class="btn-set-default" data-id="${address.id}" 
                                style="background: #2196F3; color: white; border: none; padding: 5px 12px; 
                                       border-radius: 3px; cursor: pointer; font-size: 13px; margin-right: 5px;"
                                ${isDefault ? 'disabled' : ''}>
                            <i class="fa fa-check"></i> Đặt làm mặc định
                        </button>
                        <button type="button" class="btn-delete-address" data-id="${address.id}"
                                style="background: #f44336; color: white; border: none; padding: 5px 12px; 
                                       border-radius: 3px; cursor: pointer; font-size: 13px;">
                            <i class="fa fa-trash"></i> Xóa
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
