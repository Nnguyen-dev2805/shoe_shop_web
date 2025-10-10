/**
 * Registration Page - RESTful API with Ajax + jQuery
 * DeeG Shoe Shop
 */

$(document).ready(function() {
    let isCodeSent = false;
    let codeSentEmail = '';

    // Send Verification Code Button Handler
    $('#sendCodeBtn').on('click', function() {
        const email = $('#email').val().trim();
        
        // Validate email
        if (!email) {
            showAlert('Vui lòng nhập email.', 'error');
            return;
        }

        if (!isValidEmail(email)) {
            showAlert('Email không hợp lệ.', 'error');
            return;
        }

        // Disable button and show loading state
        const $btn = $(this);
        const originalText = $btn.text();
        $btn.prop('disabled', true).text('Đang gửi...');

        // Send verification code via AJAX
        $.ajax({
            url: '/api/register/send-verification-code',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: email }),
            success: function(response) {
                if (response.success) {
                    showAlert(response.message, 'success');
                    isCodeSent = true;
                    codeSentEmail = email;
                    
                    // Change button text to resend
                    $btn.text('Gửi lại mã');
                    
                    // Enable verification code input
                    $('#verificationCode').prop('disabled', false).focus();
                    
                    // Start countdown timer (optional)
                    startCountdown($btn, 60);
                } else {
                    showAlert(response.message, 'error');
                    $btn.prop('disabled', false).text(originalText);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error sending verification code:', error);
                let errorMessage = 'Có lỗi xảy ra khi gửi mã xác nhận. Vui lòng thử lại.';
                
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                
                showAlert(errorMessage, 'error');
                $btn.prop('disabled', false).text(originalText);
            }
        });
    });

    // Register Form Submit Handler
    $('#registerForm').on('submit', function(e) {
        e.preventDefault();

        // Get form data
        const email = $('#email').val().trim();
        const fullname = $('#fullname').val().trim();
        const password = $('#password').val();
        const confirmPassword = $('#confirmPassword').val();
        const verificationCode = $('#verificationCode').val().trim();

        // Validate form
        if (!validateForm(email, fullname, password, confirmPassword, verificationCode)) {
            return;
        }

        // Check if code was sent
        if (!isCodeSent) {
            showAlert('Vui lòng gửi mã xác nhận trước khi đăng ký.', 'error');
            return;
        }

        // Check if email matches
        if (email !== codeSentEmail) {
            showAlert('Email không khớp với email đã gửi mã xác nhận. Vui lòng gửi lại mã.', 'error');
            return;
        }

        // Disable submit button
        const $submitBtn = $(this).find('button[type="submit"]');
        const originalText = $submitBtn.text();
        $submitBtn.prop('disabled', true).text('Đang xử lý...');

        // Prepare data
        const registerData = {
            email: email,
            fullname: fullname,
            password: password,
            confirmPassword: confirmPassword,
            verificationCode: verificationCode
        };

        // Submit registration via AJAX
        $.ajax({
            url: '/api/register/submit',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(registerData),
            success: function(response) {
                if (response.success) {
                    showAlert(response.message, 'success');
                    
                    // Clear form
                    $('#registerForm')[0].reset();
                    isCodeSent = false;
                    codeSentEmail = '';
                    
                    // Redirect to login page after 2 seconds
                    setTimeout(function() {
                        window.location.href = response.redirectUrl || '/login';
                    }, 2000);
                } else {
                    showAlert(response.message, 'error');
                    $submitBtn.prop('disabled', false).text(originalText);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error registering user:', error);
                let errorMessage = 'Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.';
                
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                
                showAlert(errorMessage, 'error');
                $submitBtn.prop('disabled', false).text(originalText);
            }
        });
    });

    // Real-time password confirmation validation
    $('#confirmPassword').on('input', function() {
        const password = $('#password').val();
        const confirmPassword = $(this).val();
        
        if (confirmPassword && password !== confirmPassword) {
            $(this).css('border-color', '#d6334d');
        } else {
            $(this).css('border-color', '#ccc');
        }
    });

    /**
     * Validate email format
     */
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * Validate registration form
     */
    function validateForm(email, fullname, password, confirmPassword, verificationCode) {
        // Email validation
        if (!email) {
            showAlert('Vui lòng nhập email.', 'error');
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert('Email không hợp lệ.', 'error');
            return false;
        }

        // Fullname validation
        if (!fullname) {
            showAlert('Vui lòng nhập tên đầy đủ.', 'error');
            return false;
        }

        if (fullname.length < 2) {
            showAlert('Tên đầy đủ phải có ít nhất 2 ký tự.', 'error');
            return false;
        }

        // Password validation
        if (!password) {
            showAlert('Vui lòng nhập mật khẩu.', 'error');
            return false;
        }

        if (password.length < 6) {
            showAlert('Mật khẩu phải có ít nhất 6 ký tự.', 'error');
            return false;
        }

        // Confirm password validation
        if (!confirmPassword) {
            showAlert('Vui lòng xác nhận mật khẩu.', 'error');
            return false;
        }

        if (password !== confirmPassword) {
            showAlert('Mật khẩu xác nhận không khớp.', 'error');
            return false;
        }

        // Verification code validation
        if (!verificationCode) {
            showAlert('Vui lòng nhập mã xác nhận.', 'error');
            return false;
        }

        if (!/^\d{6}$/.test(verificationCode)) {
            showAlert('Mã xác nhận phải là 6 chữ số.', 'error');
            return false;
        }

        return true;
    }

    /**
     * Show alert message
     */
    function showAlert(message, type) {
        const $alertBox = $('#alertMessage');
        const $alertText = $('#alertText');
        
        $alertText.text(message);
        
        // Set color based on type
        if (type === 'success') {
            $alertBox.css({
                'background-color': '#d4edda',
                'color': '#155724',
                'border': '1px solid #c3e6cb'
            });
        } else if (type === 'error') {
            $alertBox.css({
                'background-color': '#f8d7da',
                'color': '#721c24',
                'border': '1px solid #f5c6cb'
            });
        }
        
        // Show alert
        $alertBox.fadeIn();
        
        // Auto hide after 5 seconds
        setTimeout(function() {
            $alertBox.fadeOut();
        }, 5000);
        
        // Scroll to alert
        $('html, body').animate({
            scrollTop: $alertBox.offset().top - 100
        }, 500);
    }

    /**
     * Countdown timer for resend button
     */
    function startCountdown($btn, seconds) {
        let remaining = seconds;
        
        const interval = setInterval(function() {
            remaining--;
            
            if (remaining > 0) {
                $btn.text(`Gửi lại (${remaining}s)`);
            } else {
                clearInterval(interval);
                $btn.prop('disabled', false).text('Gửi lại mã');
            }
        }, 1000);
    }
});
