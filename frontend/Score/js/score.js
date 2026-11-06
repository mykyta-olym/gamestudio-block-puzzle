document.addEventListener('DOMContentLoaded', function() {
    const introText = document.getElementById('welcomeText');
    const tableContainer = document.querySelector('.container');

    // Start by fading out the welcome text
    setTimeout(() => {
        introText.style.opacity = '0';

        // Once the text has faded out, begin fading in the table container
        setTimeout(() => {
            introText.style.display = 'none'; // Hide the text after fade out

            // Fade in the table container
            tableContainer.style.display = 'table';
            tableContainer.style.opacity = '0';
            setTimeout(() => {
                tableContainer.style.opacity = '1';
            }, 100); // Start opacity transition for the table
        }, 2000); // Matches the CSS transition duration for text fade-out
    }, 2000); // Initial delay before starting to fade out the text
});
document.addEventListener('DOMContentLoaded', function() {
    fetchAndDisplayScores('gameCompleted');
    fetchAndDisplayScores('experience');
    fetchAndDisplayScores('playTime');
});

function fetchAndDisplayScores(columnName) {
    console.log('Fetching scores for:', columnName);
    fetch(`http://localhost:8080/scores/${columnName}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch scores');
            }
            return response.json();
        })
        .then(scores => {
            // Assuming the column names in the database and those in the HTML are the same
            const tbody = document.querySelector('tbody');  // Directly select tbody where scores will be displayed
            tbody.innerHTML = '';  // Clear existing content each time

            scores.forEach((score, index) => {
                const row = tbody.insertRow(-1);  // Insert a new row in the table at the last position
                const cell1 = row.insertCell(0);
                const cell2 = row.insertCell(1);
                const cell3 = row.insertCell(2);
                const cell4 = row.insertCell(3);

                cell1.textContent = score.username;
                cell2.textContent = score.gameCompleted;
                cell3.textContent = score.experience;
                cell4.textContent = score.playTime;
            });
        })
        .catch(error => {
            console.error('Error fetching scores:', error);
            alert('Failed to fetch scores: ' + error.message);
        });
}

