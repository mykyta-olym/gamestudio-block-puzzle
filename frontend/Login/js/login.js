document.addEventListener('DOMContentLoaded', function() {
    const welcomeText = document.getElementById('welcomeText');
    const loginForm = document.getElementById('loginForm');

    setTimeout(() => {
        welcomeText.style.opacity = '0';

        // Wait for the text fade-out to finish before fading in the form
        setTimeout(() => {
            welcomeText.style.display = 'none'; // Completely hide the text

            // Initialize the form's visibility and start the fade-in
            loginForm.style.display = 'block';
            loginForm.style.opacity = '0'; // Ensure it starts from invisible

            // Smooth fade-in for the form
            setTimeout(() => {
                loginForm.style.opacity = '1';
            }, 100); // Short delay to ensure the style applies
        }, 2000); // Matches the CSS transition duration
    }, 2000); // Delay before fading out the text
});

document.addEventListener('DOMContentLoaded', function() {
    const submitButton = document.getElementById('submitLogin');
    const usernameInput = document.querySelector('.user-box input[type="text"]');
    const passwordInput = document.querySelector('.user-box input[type="password"]');

    submitButton.addEventListener('click', function(event) {
        event.preventDefault(); // Prevent the default anchor action

        const userDetails = {
            username: usernameInput.value,
            password: passwordInput.value
        };

        fetch('http://localhost:8080/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userDetails)
        })
            .then(response => {
                if (!response.ok) {
                    // Handle HTTP errors
                    return response.text().then(text => {
                        throw new Error("Authentication failed: " + text);
                    });
                }
                return response.json(); // We need to parse JSON here
            })
            .then(data => {
                sessionStorage.setItem('isLoggedIn', 'true'); // Save login state
                sessionStorage.setItem('username', data.username); // Store the username
                alert("You successfully logged in as " + data.username);  // Greet the user by username
                window.location.href = '../Game/game.html';  // Redirect to the game page
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Login Failed: ' + error.message);
            });
    });
});
