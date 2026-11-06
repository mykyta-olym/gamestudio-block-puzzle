document.querySelectorAll('.stars').forEach(starContainer => {
    for (let i = 1; i <= 5; i++) {
        const star = document.createElement('div');
        star.className = 'star';
        star.dataset.value = i;
        starContainer.appendChild(star);
    }
});

function updateStars(container, rating) {
    const stars = container.querySelectorAll('.star');
    stars.forEach((star, index) => {
        star.classList.remove('filled');
        if (index < rating) {
            star.classList.add('filled');
        }
    });
    container.style.display = 'flex'; // Ensure container is visible before updating stars
    container.style.opacity = '1'; // Ensure container is fully opaque when stars are updated
}
document.addEventListener('DOMContentLoaded', function() {
    const welcomeText = document.getElementById('welcomeText');
    if (welcomeText) {
        welcomeText.textContent = welcomeText.textContent.replace(/\|/g, '♥');
    }
});

document.addEventListener('DOMContentLoaded', function() {
    if (!sessionStorage.getItem('isLoggedIn')) {
        alert("You must be logged in to view this page.");
        window.location.href = '../Login/login.html'; // Redirect to the login page
    }
});


// Function to fetch and update ratings
function fetchRatingsAndUpdateUI() {
    const username = sessionStorage.getItem('username');
    const game = 'Endless';

    fetch(`http://localhost:8080/rating/average/${game}`)
        .then(response => response.json())
        .then(averageRating => {
            const averageRatingDiv = document.getElementById('averageRating');
            updateStars(averageRatingDiv.querySelector('.stars'), averageRating);
        })
        .catch(error => console.error('Error loading average ratings:', error));

    fetch(`http://localhost:8080/rating/${game}/${username}`)
        .then(response => response.json())
        .then(userRating => {
            const userRatingDiv = document.getElementById('userRating');
            updateStars(userRatingDiv.querySelector('.stars'), userRating);
        })
        .catch(error => console.error('Error loading user rating:', error));
}

// Handle welcome text fade and form appearance
document.addEventListener('DOMContentLoaded', function() {
    const introText = document.getElementById('welcomeText');
    const form = document.querySelector('.form');
    const ratings = document.querySelectorAll('.rating'); // Collect all rating divs

    // Fade out the intro text first
    setTimeout(() => {
        introText.style.opacity = '0';

        // After the text has faded out, hide it and start fading in the form and ratings
        setTimeout(() => {
            introText.style.display = 'none';

            // Fade in the form first
            form.style.display = 'block';
            form.style.opacity = '0';
            setTimeout(() => {
                form.style.opacity = '1';

                // Then sequentially fade in each rating category
                ratings.forEach((rating, index) => {
                    setTimeout(() => {
                        rating.style.display = 'flex';
                        rating.style.opacity = '0';
                        setTimeout(() => {
                            rating.style.opacity = '1';
                            // Fetch and update ratings after last rating div is visible
                            if (index === ratings.length - 1) {
                                fetchRatingsAndUpdateUI();
                            }
                        }, 100); // This delay starts the opacity transition
                    }, 200 + index * 200); // Staggered delay for each rating
                });
            }, 1000); // Delay for form to fade in fully
        }, 2000); // This timeout matches the duration of the text fade-out
    }, 2000); // Initial delay before starting to fade out the text
});


// Function to handle rating submission
document.addEventListener('DOMContentLoaded', function() {
    const sendButton = document.getElementById('send');
    const nomeInput = document.getElementById('nome');
    const username = sessionStorage.getItem('username');
    const game = 'Endless';

    sendButton.addEventListener('click', function(event) {
        event.preventDefault();
        const rating = parseInt(nomeInput.value, 10);

        // Post the rating to the server
        fetch('http://localhost:8080/rating', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ game, username, rating })
        })
            .then(response => {
                if (!response.ok) throw new Error('Failed to submit rating');
                return response.text();
            })
            .then(() => {
                alert('Rating submitted successfully');
                nomeInput.value = ''; // Clear the input after successful submission
                fetchRatingsAndUpdateUI(); // Refresh ratings automatically
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Failed to submit rating: ' + error.message);
            });
    });
});

// Initialize ratings on page load
document.addEventListener('DOMContentLoaded', function() {
    fetchRatingsAndUpdateUI();
});

// Ensure user is logged in before accessing ratings page
document.addEventListener('DOMContentLoaded', function() {
    if (!sessionStorage.getItem('isLoggedIn')) {
        alert("You must be logged in to view this page.");
        window.location.href = '../Login/login.html'; // Redirect to the login page
    }
});
