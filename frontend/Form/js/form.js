document.addEventListener('DOMContentLoaded', function() {
    const introText = document.getElementById('welcomeText');
    const buttons = document.querySelectorAll('.btn2');
    const socialMediaIcons = document.querySelectorAll('.main');

    setTimeout(() => {
        introText.style.opacity = '0';  // Start fading out the intro text

        // Wait for the text fade-out to finish
        setTimeout(() => {
            introText.style.display = 'none';  // Completely hide the text

            // Fade in the buttons and social media icons
            Array.from(buttons).forEach(button => {
                button.style.display = 'block';
                button.style.opacity = '0';
                setTimeout(() => {
                    button.style.opacity = '1';
                }, 100);  // Short delay to start the opacity transition
            });

            Array.from(socialMediaIcons).forEach(icon => {
                icon.style.display = 'flex';  // Assuming flex display for icons
                icon.style.opacity = '0';
                setTimeout(() => {
                    icon.style.opacity = '1';
                }, 100);  // Short delay to start the opacity transition
            });
        }, 2000);  // Matches the CSS transition duration for the fade-out
    }, 2000);  // Delay before starting to fade out the text
});
