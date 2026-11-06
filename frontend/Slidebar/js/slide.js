document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.querySelector('.btn');
    const navContainer = document.querySelector('.nav-container');

    // Listener to toggle the sidebar
    toggleBtn.addEventListener('click', function() {
        navContainer.classList.toggle('open');
    });

    // Listener to close the sidebar if clicking outside of it
    document.addEventListener('click', function(event) {
        // Check if the click was outside the navContainer
        if (!navContainer.contains(event.target) && !toggleBtn.contains(event.target)) {
            // If navContainer is open and the click is outside, close it
            if (navContainer.classList.contains('open')) {
                navContainer.classList.remove('open');
            }
        }
    });
});
