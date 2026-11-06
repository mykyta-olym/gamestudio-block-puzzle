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
    const submitButton = document.getElementById('submitRegister');
    const usernameInput = document.querySelector('.user-box input[type="text"]');
    const passwordInput = document.querySelector('.user-box input[type="password"]');

    // Set name attributes dynamically to avoid automatic detection
    usernameInput.setAttribute('name', 'reg-username-' + Math.random());
    passwordInput.setAttribute('name', 'reg-password-' + Math.random());

    submitButton.addEventListener('click', function(event) {
        event.preventDefault(); // Prevent the default anchor action

        const userDetails = {
            username: usernameInput.value,
            password: passwordInput.value
        };

        fetch('http://localhost:8080/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userDetails)
        })
            .then(response => {
                const contentType = response.headers.get("content-type");
                if (!response.ok) {
                    if (contentType && contentType.includes("text")) {
                        return response.text().then(text => {
                            throw new Error("Error: " + text);
                        });
                    } else {
                        throw new Error('Server responded with a status: ' + response.status);
                    }
                }

                return response.text();
            })
            .then(text => {
                alert(text);  // Displaying text message received from the server
                window.location.href = '../Login/login.html';  // Redirect to the login page after successful registration
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Registration Failed: ' + error.message);
            });
    });
});
