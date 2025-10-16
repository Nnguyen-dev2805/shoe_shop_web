/**
 * User Change Password - RESTful API + Ajax + jQuery
 * File: user-change-password.js
 */

$(document).ready(function() {
    console.log('User Change Password script loaded');

    // Handle form submit
    $('#changePasswordForm').on('submit', function(e) {
        e.preventDefault();
        
        // Clear previous messages
        hideMessage();
        
        // Get form data
        const currentPassword = $('input[name="currentPassword"]').val().trim();
        const newPassword = $('input[name="newPassword"]').val().trim();
        const confirmPassword = $('input[name="confirmPassword"]').val().trim();
        
        // Validate input
        if (!currentPassword) {
            showError('Vui lòng nhập mật khẩu hiện tại.');
            return;
        }
        
        if (!newPassword) {
            showError('Vui lòng nhập mật khẩu mới.');
            return;
        }
        
        if (!confirmPassword) {
            showError('Vui lòng xác nhận mật khẩu mới.');
            return;
        }
        
        if (newPassword !== confirmPassword) {
            showError('Mật khẩu xác nhận không khớp.');
            return;
        }
        
        if (newPassword.length < 6) {
            showError('Mật khẩu mới phải có ít nhất 6 ký tự.');
            return;
        }
        
        // Prepare data
        const data = {
            currentPassword: currentPassword,
            newPassword: newPassword,
            confirmPassword: confirmPassword
        };
        
        // Show loading
        const $submitBtn = $(this).find('button[type="submit"]');
        const originalText = $submitBtn.text();
        $submitBtn.prop('disabled', true).text('Đang xử lý...');
        
        // Send Ajax request
        $.ajax({
            url: '/api/user/password/change',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                if (response.success) {
                    showSuccess(response.message);
                    // Clear form
                    $('#changePasswordForm')[0].reset();
                    
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
                    showError('Bạn cần đăng nhập để thay đổi mật khẩu.');
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
    
    // Show error message
    function showError(message) {
        const $errorDiv = $('#password-error');
        const $errorText = $('#password-error-text');
        
        $errorText.html('<i class="fa fa-exclamation-circle"></i> ' + message);
        $errorDiv.fadeIn(300);
        
        // Hide success if showing
        $('#password-success').hide();
        
        // Scroll to message
        $('html, body').animate({
            scrollTop: $errorDiv.offset().top - 100
        }, 500);
    }
    
    // Show success message
    function showSuccess(message) {
        const $successDiv = $('#password-success');
        const $successText = $('#password-success-text');
        
        $successText.html('<i class="fa fa-check-circle"></i> ' + message);
        $successDiv.fadeIn(300);
        
        // Hide error if showing
        $('#password-error').hide();
        
        // Scroll to message
        $('html, body').animate({
            scrollTop: $successDiv.offset().top - 100
        }, 500);
    }
    
    // Hide messages
    function hideMessage() {
        $('#password-error').fadeOut(300);
        $('#password-success').fadeOut(300);
    }
    
    // Real-time password match validation
    $('input[name="confirmPassword"]').on('keyup', function() {
        const newPassword = $('input[name="newPassword"]').val();
        const confirmPassword = $(this).val();
        
        if (confirmPassword && newPassword !== confirmPassword) {
            $(this).css('border-color', '#dc3545');
        } else {
            $(this).css('border-color', '');
        }
    });
    
    // Password strength indicator (optional)
    $('input[name="newPassword"]').on('keyup', function() {
        const password = $(this).val();
        let strength = 0;
        
        if (password.length >= 6) strength++;
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[^a-zA-Z0-9]/.test(password)) strength++;
        
        // You can add visual strength indicator here
        // For now, just add border color
        if (strength >= 3) {
            $(this).css('border-color', '#28a745');
        } else if (strength >= 2) {
            $(this).css('border-color', '#ffc107');
        } else {
            $(this).css('border-color', '');
        }
    });
});
