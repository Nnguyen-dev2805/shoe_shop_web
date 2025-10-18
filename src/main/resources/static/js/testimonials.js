/**
 * CUSTOMER TESTIMONIALS - Simple Animation
 * Hiệu ứng fade-in khi scroll vào view
 */

$(document).ready(function() {
    
    // Fade-in animation khi scroll
    function animateTestimonials() {
        $('.testimonial-card').each(function(index) {
            const card = $(this);
            const cardTop = card.offset().top;
            const windowBottom = $(window).scrollTop() + $(window).height();
            
            // Nếu card trong viewport
            if (cardTop < windowBottom - 100) {
                setTimeout(function() {
                    card.css({
                        'opacity': '1',
                        'transform': 'translateY(0)'
                    });
                }, index * 100); // Delay mỗi card 100ms
            }
        });
    }
    
    // Set initial state
    $('.testimonial-card').css({
        'opacity': '0',
        'transform': 'translateY(20px)',
        'transition': 'all 0.5s ease'
    });
    
    // Run animation on scroll
    $(window).on('scroll', animateTestimonials);
    
    // Run animation on page load
    animateTestimonials();
});
