/**
 * User Profile Management - RESTful API + Ajax + jQuery
 * File: user-profile.js
 */

$(document).ready(function() {
    console.log('User Profile script loaded');

    // Load user profile on page load
    loadUserProfile();

    // Handle form submit
    $('#updateProfileForm').on('submit', function(e) {
        e.preventDefault();
        
        // Clear previous messages
        hideMessage();
        
        // Get form data
        const fullname = $('input[name="fullname"]').val().trim();
        const phone = $('input[name="phone"]').val().trim();
        
        // Validate input
        if (!fullname) {
            showError('Vui lòng nhập họ tên.');
            return;
        }
        
        if (!phone) {
            showError('Vui lòng nhập số điện thoại.');
            return;
        }
        
        // Validate phone format
        if (!/^0\d{9}$/.test(phone)) {
            showError('Số điện thoại phải có 10 số và bắt đầu bằng 0.');
            return;
        }
        
        // Prepare data
        const data = {
            fullname: fullname,
            phone: phone
        };
        
        // Show loading
        const $submitBtn = $(this).find('button[type="submit"]');
        const originalText = $submitBtn.text();
        $submitBtn.prop('disabled', true).text('Đang lưu...');
        
        // Send Ajax request
        $.ajax({
            url: '/api/user/profile/update',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                if (response.success) {
                    showSuccess(response.message);
                    
                    // Auto hide message after 3 seconds
                    setTimeout(function() {
                        hideMessage();
                    }, 3000);
                } else {
                    showError(response.message);
                }
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    showError('Bạn cần đăng nhập để cập nhật thông tin.');
                } else if (xhr.responseJSON && xhr.responseJSON.message) {
                    showError(xhr.responseJSON.message);
                } else {
                    showError('Có lỗi xảy ra. Vui lòng thử lại.');
                }
            },
            complete: function() {
                // Reset button
                $submitBtn.prop('disabled', false).text(originalText);
            }
        });
    });
    
    // Load user profile information
    function loadUserProfile() {
        $.ajax({
            url: '/api/user/profile',
            type: 'GET',
            success: function(response) {
                if (response.success && response.user) {
                    const user = response.user;
                    $('#user-email').val(user.email);
                    $('#user-fullname').val(user.fullname);
                    $('#user-phone').val(user.phone);
                }
            },
            error: function(xhr) {
                console.error('Error loading profile:', xhr);
            }
        });
    }
    
    // Show error message
    function showError(message) {
        const $errorDiv = $('#profile-error');
        const $errorText = $('#profile-error-text');
        
        $errorText.html('<i class="fa fa-exclamation-circle"></i> ' + message);
        $errorDiv.fadeIn(300);
        
        // Hide success if showing
        $('#profile-success').hide();
        
        // Scroll to message
        $('html, body').animate({
            scrollTop: $errorDiv.offset().top - 100
        }, 500);
    }
    
    // Show success message
    function showSuccess(message) {
        const $successDiv = $('#profile-success');
        const $successText = $('#profile-success-text');
        
        $successText.html('<i class="fa fa-check-circle"></i> ' + message);
        $successDiv.fadeIn(300);
        
        // Hide error if showing
        $('#profile-error').hide();
        
        // Scroll to message
        $('html, body').animate({
            scrollTop: $successDiv.offset().top - 100
        }, 500);
    }
    
    // Hide messages
    function hideMessage() {
        $('#profile-error').fadeOut(300);
        $('#profile-success').fadeOut(300);
    }
    
    // Real-time phone validation
    $('input[name="phone"]').on('keyup', function() {
        const phone = $(this).val();
        
        if (phone && !/^0\d{9}$/.test(phone)) {
            $(this).css('border-color', '#dc3545');
        } else {
            $(this).css('border-color', '');
        }
    });
    
    // Format phone on input
    $('input[name="phone"]').on('input', function() {
        // Remove non-digit characters
        let value = $(this).val().replace(/\D/g, '');
        
        // Limit to 10 digits
        if (value.length > 10) {
            value = value.substring(0, 10);
        }
        
        $(this).val(value);
    });
});
