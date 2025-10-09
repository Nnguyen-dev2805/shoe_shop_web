/**
 * Password Reset JavaScript Handler
 * Handles AJAX requests for password reset functionality
 */

$(document).ready(function() {
    // Initialize the password reset form
    initializePasswordReset();
});

function initializePasswordReset() {
    // Check session status on page load
    checkSessionStatus();
    
    // Bind form submit events
    bindFormEvents();
    
    // Initialize UI state
    resetFormState();
}

function checkSessionStatus() {
    $.ajax({
        url: '/api/password-reset/session-status',
        type: 'GET',
        success: function(response) {
            if (response.isCodeVerified) {
                showPasswordResetForm();
            } else if (response.isEmailSent) {
                showVerificationForm();
            } else {
                showEmailForm();
            }
        },
        error: function() {
            showEmailForm();
        }
    });
}

function bindFormEvents() {
    // Email form submission
    $('#emailForm').on('submit', function(e) {
        e.preventDefault();
        sendVerificationCode();
    });
    
    // Verification code form submission
    $('#verificationForm').on('submit', function(e) {
        e.preventDefault();
        verifyCode();
    });
    
    // Password reset form submission
    $('#passwordResetForm').on('submit', function(e) {
        e.preventDefault();
        resetPassword();
    });
    
    // Back to email form button
    $('#backToEmailBtn').on('click', function() {
        resetSession();
    });
    
    // Back to verification form button
    $('#backToVerificationBtn').on('click', function() {
        showVerificationForm();
    });
}

function sendVerificationCode() {
    const email = $('#email').val().trim();
    
    if (!email) {
        showMessage('Email không được để trống.', 'error');
        return;
    }
    
    if (!isValidEmail(email)) {
        showMessage('Vui lòng nhập email hợp lệ.', 'error');
        return;
    }
    
    // Show loading
    showLoading('#sendCodeBtn', 'Đang gửi...');
    
    $.ajax({
        url: '/api/password-reset/send-code',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ email: email }),
        success: function(response) {
            hideLoading('#sendCodeBtn', 'Gửi mã xác nhận');
            
            if (response.success) {
                showMessage(response.message, 'success');
                showVerificationForm();
            } else {
                showMessage(response.message, 'error');
            }
        },
        error: function(xhr) {
            hideLoading('#sendCodeBtn', 'Gửi mã xác nhận');
            
            let message = 'Có lỗi xảy ra. Vui lòng thử lại.';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                message = xhr.responseJSON.message;
            }
            showMessage(message, 'error');
        }
    });
}

function verifyCode() {
    const code = $('#verificationCode').val().trim();
    
    if (!code) {
        showMessage('Mã xác nhận không được để trống.', 'error');
        return;
    }
    
    if (!/^\d{6}$/.test(code)) {
        showMessage('Mã xác nhận phải là 6 chữ số.', 'error');
        return;
    }
    
    // Show loading
    showLoading('#verifyCodeBtn', 'Đang xác minh...');
    
    $.ajax({
        url: '/api/password-reset/verify-code',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ code: code }),
        success: function(response) {
            hideLoading('#verifyCodeBtn', 'Xác minh mã');
            
            if (response.success) {
                showMessage(response.message, 'success');
                showPasswordResetForm();
            } else {
                showMessage(response.message, 'error');
            }
        },
        error: function(xhr) {
            hideLoading('#verifyCodeBtn', 'Xác minh mã');
            
            let message = 'Có lỗi xảy ra. Vui lòng thử lại.';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                message = xhr.responseJSON.message;
            }
            showMessage(message, 'error');
        }
    });
}

function resetPassword() {
    const newPassword = $('#newPassword').val();
    const confirmPassword = $('#confirmPassword').val();
    
    if (!newPassword || !confirmPassword) {
        showMessage('Vui lòng nhập đầy đủ thông tin.', 'error');
        return;
    }
    
    if (newPassword.length < 6) {
        showMessage('Mật khẩu phải có ít nhất 6 ký tự.', 'error');
        return;
    }
    
    if (newPassword !== confirmPassword) {
        showMessage('Mật khẩu xác nhận không khớp.', 'error');
        return;
    }
    
    // Show loading
    showLoading('#resetPasswordBtn', 'Đang đặt lại...');
    
    $.ajax({
        url: '/api/password-reset/reset-password',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ 
            newPassword: newPassword,
            confirmPassword: confirmPassword
        }),
        success: function(response) {
            hideLoading('#resetPasswordBtn', 'Đặt lại mật khẩu');
            
            if (response.success) {
                showMessage(response.message, 'success');
                // Redirect to login page after 2 seconds
                setTimeout(function() {
                    window.location.href = '/login';
                }, 2000);
            } else {
                showMessage(response.message, 'error');
            }
        },
        error: function(xhr) {
            hideLoading('#resetPasswordBtn', 'Đặt lại mật khẩu');
            
            let message = 'Có lỗi xảy ra. Vui lòng thử lại.';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                message = xhr.responseJSON.message;
            }
            showMessage(message, 'error');
        }
    });
}

function resetSession() {
    // Clear session by making a request to get fresh session status
    $.ajax({
        url: '/api/password-reset/session-status',
        type: 'GET',
        success: function() {
            showEmailForm();
            clearForms();
        }
    });
}

function showEmailForm() {
    $('#emailFormContainer').show();
    $('#verificationFormContainer').hide();
    $('#passwordResetFormContainer').hide();
    updateStepIndicator(1);
    $('#email').focus();
}

function showVerificationForm() {
    $('#emailFormContainer').hide();
    $('#verificationFormContainer').show();
    $('#passwordResetFormContainer').hide();
    updateStepIndicator(2);
    $('#verificationCode').focus();
}

function showPasswordResetForm() {
    $('#emailFormContainer').hide();
    $('#verificationFormContainer').hide();
    $('#passwordResetFormContainer').show();
    updateStepIndicator(3);
    $('#newPassword').focus();
}

function updateStepIndicator(currentStep) {
    // Reset all steps
    $('.step').removeClass('active completed');
    $('.step-line').removeClass('completed');
    
    // Mark completed steps
    for (let i = 1; i < currentStep; i++) {
        $('#step' + i).addClass('completed');
        $('#line' + i).addClass('completed');
    }
    
    // Mark current step as active
    $('#step' + currentStep).addClass('active');
}

function resetFormState() {
    showEmailForm();
    clearForms();
    hideMessage();
}

function clearForms() {
    $('#email').val('');
    $('#verificationCode').val('');
    $('#newPassword').val('');
    $('#confirmPassword').val('');
}

function showMessage(message, type) {
    const messageDiv = $('#messageContainer');
    messageDiv.removeClass('alert-success alert-danger alert-info')
             .addClass('alert alert-' + (type === 'success' ? 'success' : 'danger'))
             .text(message)
             .show();
    
    // Auto hide success messages after 5 seconds
    if (type === 'success') {
        setTimeout(function() {
            hideMessage();
        }, 5000);
    }
}

function hideMessage() {
    $('#messageContainer').hide();
}

function showLoading(buttonSelector, loadingText) {
    const btn = $(buttonSelector);
    btn.prop('disabled', true)
       .data('original-text', btn.text())
       .html('<span class="loading-spinner"></span>' + loadingText);
}

function hideLoading(buttonSelector, originalText) {
    const btn = $(buttonSelector);
    btn.prop('disabled', false)
       .text(originalText || btn.data('original-text'));
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Utility function to handle enter key press
$(document).on('keypress', 'input', function(e) {
    if (e.which === 13) { // Enter key
        $(this).closest('form').submit();
    }
});

// Clear messages when user starts typing
$(document).on('input', 'input', function() {
    hideMessage();
});

// Password strength checker
$(document).on('input', '#newPassword', function() {
    const password = $(this).val();
    const strengthDiv = $('#passwordStrength');
    
    if (password.length === 0) {
        strengthDiv.text('').removeClass('weak medium strong');
        return;
    }
    
    const strength = checkPasswordStrength(password);
    strengthDiv.removeClass('weak medium strong').addClass(strength.class);
    strengthDiv.text(strength.text);
});

function checkPasswordStrength(password) {
    let score = 0;
    
    // Length check
    if (password.length >= 8) score += 1;
    if (password.length >= 12) score += 1;
    
    // Character variety checks
    if (/[a-z]/.test(password)) score += 1;
    if (/[A-Z]/.test(password)) score += 1;
    if (/[0-9]/.test(password)) score += 1;
    if (/[^a-zA-Z0-9]/.test(password)) score += 1;
    
    if (score < 3) {
        return { class: 'weak', text: 'Mật khẩu yếu - Cần ít nhất 8 ký tự, chữ hoa, chữ thường và số' };
    } else if (score < 5) {
        return { class: 'medium', text: 'Mật khẩu trung bình - Thêm ký tự đặc biệt để mạnh hơn' };
    } else {
        return { class: 'strong', text: 'Mật khẩu mạnh' };
    }
}

// Auto-format verification code input
$(document).on('input', '#verificationCode', function() {
    let value = $(this).val().replace(/\D/g, ''); // Remove non-digits
    if (value.length > 6) value = value.substring(0, 6);
    $(this).val(value);
});
