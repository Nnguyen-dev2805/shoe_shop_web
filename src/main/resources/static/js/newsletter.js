/**
 * NEWSLETTER SIGNUP - JavaScript Handler
 */

function subscribeNewsletter(event) {
    event.preventDefault();
    
    const emailInput = document.getElementById('newsletterEmail');
    const message = document.getElementById('newsletterMessage');
    const email = emailInput.value.trim();
    
    // Validate email
    if (!email || !isValidEmail(email)) {
        showMessage('Vui lòng nhập email hợp lệ!', 'error');
        return false;
    }
    
    // Show loading state
    const button = event.target.querySelector('.newsletter-button');
    const originalText = button.innerHTML;
    button.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang xử lý...';
    button.disabled = true;
    
    // Simulate API call (replace with actual endpoint)
    setTimeout(() => {
        // Success
        showMessage('Đăng ký thành công! Cảm ơn bạn đã đăng ký nhận bản tin.', 'success');
        emailInput.value = '';
        
        // Reset button
        button.innerHTML = originalText;
        button.disabled = false;
        
        // Hide message after 5 seconds
        setTimeout(() => {
            message.style.display = 'none';
        }, 5000);
        
        // Optional: Send to backend
        // $.ajax({
        //     url: '/api/newsletter/subscribe',
        //     method: 'POST',
        //     data: { email: email },
        //     success: function(response) {
        //         showMessage('Đăng ký thành công!', 'success');
        //     },
        //     error: function() {
        //         showMessage('Có lỗi xảy ra. Vui lòng thử lại!', 'error');
        //     }
        // });
    }, 1000);
    
    return false;
}

function showMessage(text, type) {
    const message = document.getElementById('newsletterMessage');
    message.textContent = text;
    message.className = `newsletter-message ${type}`;
    message.style.display = 'block';
}

function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Animation on scroll - DISABLED FOR DEBUG
$(document).ready(function() {
    // Show immediately without animation for debugging
    $('.newsletter-wrapper').css({
        'opacity': '1',
        'transform': 'translateY(0)',
        'visibility': 'visible',
        'display': 'flex'
    });
    
    // Fade-in animation when section comes into view - COMMENTED OUT
    // function animateNewsletter() {
    //     const section = $('.newsletter-section');
    //     if (section.length) {
    //         const sectionTop = section.offset().top;
    //         const windowBottom = $(window).scrollTop() + $(window).height();
    //         
    //         if (sectionTop < windowBottom - 100) {
    //             section.addClass('animated');
    //         }
    //     }
    // }
    
    // $(window).on('scroll', animateNewsletter);
    // animateNewsletter(); // Check on load
});
