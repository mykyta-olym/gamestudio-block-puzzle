document.addEventListener('DOMContentLoaded', function() {
    const introText = document.getElementById('welcomeText');
    const commentInput = document.getElementById('nome');
    const sendButton = document.getElementById('send');
    const form = document.querySelector('.form');
    const commentContainer = document.querySelector('.code-editor-container');
    const game = 'Endless'; // Assuming the game's name is statically assigned for now



    // Authentication check
    if (!sessionStorage.getItem('isLoggedIn')) {
        alert("You must be logged in to view this page.");
        window.location.href = '../Login/login.html'; // Redirect to login page
    }

    // Function to fetch and display comments after the form has been displayed
    function fetchAndDisplayComments() {
        fetch(`http://localhost:8080/comments/${game}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch comments');
                }
                return response.json();
            })
            .then(comments => {
                if (comments && comments.length > 0) {
                    displayComments(comments); // Display comments correctly sorted and limited
                } else {
                    console.log("No comments to display.");
                }
            })
            .catch(error => {
                console.error('Failed to fetch comments:', error);
            });
    }
    sendButton.addEventListener('click', function(event) {
        event.preventDefault();
        const game = 'Endless' // Make sure this function or variable correctly retrieves the game name
        const commentText = commentInput.value;
        const username = sessionStorage.getItem('username');


        if (!commentText.trim()) {
            alert('Please enter a comment before submitting!');
            return;
        }
        if (!username) {
            alert('No username found. Please log in before commenting.');
            return;
        }
        const newComment = {
            game: game,
            player: username,
            comment: commentText,
            commentedAt: new Date().toISOString() // It's a good practice to send dates in ISO format
        };

        addComment(newComment);
        commentInput.value = ''; // Clear input after sending
    });
    function addComment(comment) {
        fetch('http://localhost:8080/comments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(comment)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to submit comment');
                }
                return fetchAllComments(); // Refresh comments to include the new one
            })
            .then(comments => {
                fetchAndDisplayComments();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Failed to submit comment: ' + error.message);
            });
    }

    function fetchAllComments() {
        return fetch(`http://localhost:8080/comments/${game}`)
            .then(response => response.json());
    }

    function displayComments(comments) {
        // Sort comments by date descending to ensure the latest are first
        comments.sort((a, b) => new Date(b.commentedAt) - new Date(a.commentedAt));

        // Limit the number of comments to the last eight
        comments = comments.slice(0, 8);

        // Clear the current comments from the container
        commentContainer.innerHTML = '';

        // Iterate over the comments to display them, latest first
        comments.forEach((comment, index) => {
            setTimeout(() => {
                updateUIWithComment(comment, index + 1);
            }, 300 * index); // Delay each comment by 300ms times its index
        });
    }

// Function to convert backend date string to JavaScript compatible date string
    function parseDate(dateString) {
        if (!dateString) {
            console.log('Received invalid date:', dateString);
            return new Date(); // Default to current date if invalid
        }

        const adjustedDateStr = dateString.replace(' ', 'T') + 'Z'; // Assuming the date is in UTC
        return new Date(adjustedDateStr);
    }


    // Function to update UI with a new comment
    function updateUIWithComment(comment, index) {

        const parsedDate = parseDate(comment.commentedAt);

        const commentHTML = `
            <div class="code-editor" style="display: block; opacity: 1;">
                <div class="header">
                    <span class="title">Comment #${index}</span>
                </div>
                <div class="editor-content">
                    <code class="code">
                        <p><span class="color-0">.code-editor </span> {</p>
                        <p class="property">
                            <span class="color-2">Player</span><span>:</span>
                            <span class="color-1">${comment.player}</span>;
                        </p>
                        <p class="property">
                            <span class="color-2">Comment</span><span>:</span>
                            <span class="color-1">${comment.comment}</span>;
                        </p>
                        <p class="property">
                            <span class="color-2">Commented at</span><span>:</span>
                             <span class="color-1">${parsedDate.toLocaleString()}</span>;
                        </p>
                        <span>}</span>
                    </code>
                </div>
            </div>
        `;

        document.body.style.overflow = 'auto';

        commentContainer.insertAdjacentHTML('afterbegin', commentHTML);



    }

    // Fade-in effects for introductory text and form
    setTimeout(() => {
        introText.style.opacity = '0';
        setTimeout(() => {
            introText.style.display = 'none';
            form.style.display = 'block';
            setTimeout(() => {
                form.style.opacity = '1';
                fetchAndDisplayComments();
            }, 100);
        }, 2000);
    }, 2000);
});
