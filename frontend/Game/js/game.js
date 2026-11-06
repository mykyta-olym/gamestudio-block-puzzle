document.addEventListener('DOMContentLoaded', function() {
    if (!sessionStorage.getItem('isLoggedIn')) {
        alert("You must be logged in to view this page.");
        window.location.href = '../Login/login.html'; // Redirect to the login page
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const introText = document.getElementById('welcomeText');
    const gameButtons = document.querySelectorAll('.button-container span'); // Assuming the buttons are spans in .button-container
    const mainGameContainers = document.querySelectorAll('.main-container'); // Targeting the main game container

    setTimeout(() => {
        introText.style.opacity = '0';  // Start fading out the intro text

        // Wait for the text fade-out to finish
        setTimeout(() => {
            introText.style.display = 'none';  // Completely hide the text

            // Fade in the game buttons
            gameButtons.forEach(button => {
                button.style.display = 'inline-block'; // Make sure buttons are visible and styled correctly
                button.style.opacity = '0';
                setTimeout(() => {
                    button.style.opacity = '1';  // Fade in each button
                }, 100);  // Short delay to start the opacity transition
            });

            // Assume mainGameContainers should be set to 'flex' to properly display
            mainGameContainers.forEach(container => {
                container.style.display = 'flex';  // Assuming flex display for the main game container
                container.style.opacity = '0';
                setTimeout(() => {
                    container.style.opacity = '1';  // Fade in the container
                }, 100);  // Short delay to start the opacity transition
            });
        }, 2000);  // Matches the CSS transition duration for the fade-out
    }, 2000);  // Delay before starting to fade out the text
});


document.addEventListener('DOMContentLoaded', function () {
    const container = document.querySelector('.container');
    const nextContainer = document.querySelector('.next-container');
    const gameOverMessage = document.createElement('div');
    gameOverMessage.className = 'game-over';
    gameOverMessage.textContent = 'Game Over!';
    gameOverMessage.style.display = 'none';
    document.body.appendChild(gameOverMessage);

    let score = 0;
    let gamesPlayed = 0;
    let finishedGames = 0;
    let gameStarted = false;

    let clearedRowsCount = 0;
    let clearedColumnsCount = 0;
    const POINTS_PER_ROW = 100;
    const POINTS_PER_COLUMN = 150;

    const rows = 18; // Number of rows in the grid
    const cols = 10; // Number of columns in the grid
    const blockSize = parseInt(
        getComputedStyle(document.documentElement).getPropertyValue('--block-size'),
        10
    );

    let grid = Array.from({ length: rows }, () => Array(cols).fill(false));

    function createGrid() {
        container.style.gridTemplateColumns = `repeat(${cols}, ${blockSize}px)`;
        container.innerHTML = ''; // Clear previous blocks
        grid = Array.from({ length: rows }, () => Array(cols).fill(false));
        for (let i = 0; i < rows * cols; i++) {
            let cell = document.createElement('div');
            cell.className = 'block';
            cell.style.width = `${blockSize}px`;
            cell.style.height = `${blockSize}px`;
            container.appendChild(cell);
        }
    }

    function generateNextBlock() {
        nextContainer.innerHTML = ''; // Clear previous shapes

        const shapes = {
            I: [[1], [1], [1], [1]],
            O: [
                [1, 1],
                [1, 1],
            ],
            T: [
                [0, 1, 0],
                [1, 1, 1],
            ],
            S: [
                [0, 1, 1],
                [1, 1, 0],
            ],
            L: [
                [1, 0],
                [1, 0],
                [1, 1],
            ],
            J: [
                [0, 1],
                [0, 1],
                [1, 1],
            ],
            Z: [
                [1, 1, 0],
                [0, 1, 1],
            ],
            U: [
                [1, 0, 1],
                [1, 1, 1],
            ],
            Plus: [
                [0, 1, 0],
                [1, 1, 1],
                [0, 1, 0]
            ],
            Single: [
                [1]
            ],
            Corner: [
                [1, 0],
                [1, 1]
            ]
        };

        const colors = ['red', 'blue', 'green', 'yellow', 'purple'];
        const shapeKeys = Object.keys(shapes);
        const shapeKey = shapeKeys[Math.floor(Math.random() * shapeKeys.length)];
        const shape = shapes[shapeKey];
        const color = colors[Math.floor(Math.random() * colors.length)];

        const shapeContainer = document.createElement('div');
        shapeContainer.className = 'shape-container draggable';
        shapeContainer.style.position = 'absolute';
        shapeContainer.style.width = `${shape[0].length * blockSize}px`;
        shapeContainer.style.height = `${shape.length * blockSize}px`;
        const ghostContainer = shapeContainer.cloneNode(true);
        ghostContainer.className = 'ghost-container';
        ghostContainer.style.opacity = '0.5';
        ghostContainer.style.visibility = 'hidden';

        shape.forEach((row, rowIndex) => {
            row.forEach((cell, cellIndex) => {
                if (cell === 1) {
                    const block = document.createElement('div');
                    block.className = `game-block ${color}`;
                    block.style.width = `${blockSize}px`;
                    block.style.height = `${blockSize}px`;
                    block.style.position = 'absolute';
                    block.style.left = `${cellIndex * blockSize}px`;
                    block.style.top = `${rowIndex * blockSize}px`;
                    shapeContainer.appendChild(block);
                    ghostContainer.appendChild(block.cloneNode(true));
                }
            });
        });
        shapeContainer.addEventListener('click', function () {
            // Calculate new orientation of the shape
            const newShape = [];
            for (let i = 0; i < shape[0].length; i++) {
                newShape.push([]);
                for (let j = shape.length - 1; j >= 0; j--) {
                    newShape[i].push(shape[j][i]);
                }
            }

            // Update the display for both the actual and ghost shape
            shapeContainer.innerHTML = '';
            ghostContainer.innerHTML = '';
            newShape.forEach((row, rowIndex) => {
                row.forEach((cell, cellIndex) => {
                    if (cell === 1) {
                        const block = document.createElement('div');
                        block.className = `game-block ${color}`;
                        block.style.width = `${blockSize}px`;
                        block.style.height = `${blockSize}px`;
                        block.style.position = 'absolute';
                        block.style.left = `${cellIndex * blockSize}px`;
                        block.style.top = `${rowIndex * blockSize}px`;
                        shapeContainer.appendChild(block);
                        ghostContainer.appendChild(block.cloneNode(true));
                    }
                });
            });

            shapeContainer.style.width = `${newShape[0].length * blockSize}px`;
            shapeContainer.style.height = `${newShape.length * blockSize}px`;
            ghostContainer.style.width = shapeContainer.style.width;
            ghostContainer.style.height = shapeContainer.style.height;

            // Update the shape array in-place
            shape.length = 0;
            newShape.forEach(row => shape.push(row));
        });
        if (!canPlaceAny(shape)) {
            gameOverMessage.style.display = 'flex';
            gamesPlayed++;
            finishedGames++;
            sendFinishedToServer(finishedGames);
        }

        container.appendChild(ghostContainer);
        nextContainer.appendChild(shapeContainer);

        // Enable drag and drop functionality
        shapeContainer.draggable = true;
        shapeContainer.addEventListener('dragstart', (event) => {
            event.dataTransfer.setData('text/plain', ''); // Fix for Firefox
            event.dataTransfer.setData('application/json', JSON.stringify({ shape, color }));
            event.dataTransfer.effectAllowed = 'move';

            // Adjust the position of the shape relative to the cursor
            const rect = shapeContainer.getBoundingClientRect();
            event.dataTransfer.setDragImage(shapeContainer, event.clientX - rect.left, event.clientY - rect.top);
        });


        container.addEventListener('dragover', (event) => {
            event.preventDefault();
            const offsetX = event.clientX - container.getBoundingClientRect().left;
            const offsetY = event.clientY - container.getBoundingClientRect().top;
            const colPosition = Math.floor(offsetX / blockSize);
            const rowPosition = Math.floor(offsetY / blockSize);

            const ghost = container.querySelector('.ghost-container');
            ghost.style.left = `${colPosition * blockSize}px`;
            ghost.style.top = `${rowPosition * blockSize}px`;

            // Check placement validity
            if (canPlaceShapeAtPosition(shape, rowPosition, colPosition)) {
                ghost.style.visibility = 'visible';
            } else {
                ghost.style.visibility = 'hidden';
            }
        });

        container.addEventListener('dragstart', (event) => {
            // When dragging starts, show the ghost at its starting position
            const ghost = container.querySelector('.ghost-container');
            if (ghost) {
                const offsetX = event.clientX - container.getBoundingClientRect().left;
                const offsetY = event.clientY - container.getBoundingClientRect().top;
                const colPosition = Math.floor(offsetX / blockSize);
                const rowPosition = Math.floor(offsetY / blockSize);
                ghost.style.left = `${colPosition * blockSize}px`;
                ghost.style.top = `${rowPosition * blockSize}px`;
                if (canPlaceShapeAtPosition(shape, rowPosition, colPosition)) {
                    ghost.style.visibility = 'visible';
                } else {
                    ghost.style.visibility = 'hidden';
                }
            }
        });

        function canPlaceShapeAtPosition(shape, row, col) {
            // Check if the placement starts or ends outside the grid's vertical bounds
            if (row < 0 || row + shape.length > rows) {
                return false; // Placement extends above or below the grid
            }

            // Iterate through each block in the shape
            for (let rowIndex = 0; rowIndex < shape.length; rowIndex++) {
                for (let colIndex = 0; colIndex < shape[0].length; colIndex++) {
                    if (shape[rowIndex][colIndex] === 1) { // If there's a block in the shape at this position
                        const targetRow = row + rowIndex; // Calculate the absolute row on the grid
                        const targetCol = col + colIndex; // Calculate the absolute column on the grid

                        // Check if the block extends outside the horizontal bounds of the grid
                        if (targetCol < 0 || targetCol >= cols) {
                            return false; // Placement extends beyond the left or right grid boundary
                        }

                        // Check if the position is already occupied
                        if (grid[targetRow][targetCol]) {
                            return false; // Placement overlaps with an already occupied cell
                        }
                    }
                }
            }
            return true; // Return true if all blocks in the shape can be placed
        }

    }

    function startGame() {
        if (!gameStarted) {
            gamesPlayed++;
            sendGamesToServer(gamesPlayed);
            gameStarted = true;
        }
        createGrid();
        generateNextBlock();
    }

    container.addEventListener('drop', (event) => {
        event.preventDefault();
        const ghost = container.querySelector('.ghost-container');
        if (ghost) {
            ghost.remove();
        }
        const data = JSON.parse(event.dataTransfer.getData('application/json'));
        const offsetX = event.clientX - container.getBoundingClientRect().left;
        const offsetY = event.clientY - container.getBoundingClientRect().top;
        const colPosition = Math.floor(offsetX / blockSize);
        const rowPosition = Math.floor(offsetY / blockSize);

        // Attempt to place the shape at the calculated position
        if (placeShape(data.shape, data.color, rowPosition, colPosition)) {
            // If placement is successful, generate the next block
            generateNextBlock();
            ghost.style.visibility = 'visible';
        } else {
            if (ghost) {
                ghost.style.visibility = 'visible';
            }
            generateNextBlock();
        }


    });

    function canPlaceAny(shape) {
        for (let row = 0; row < rows; row++) {
            for (let col = 0; col < cols; col++) {
                if (canPlaceShapeAtPosition(shape, row, col)) return true;
            }
        }
        return false;
    }

    function canPlaceShapeAtPosition(shape, startRow, startCol) {
        const rotations = [shape]; // Initialize with the current shape
        let currentRotation = shape;

        // Generate all rotations for the shape
        for (let r = 0; r < 3; r++) {
            currentRotation = rotateShape(currentRotation); // Rotate shape
            rotations.push(currentRotation); // Add new rotation
        }

        // Check all rotations if they can be placed
        return rotations.some(rotation => {
            for (let i = 0; i < rotation.length; i++) {
                for (let j = 0; j < rotation[i].length; j++) {
                    if (rotation[i][j] === 1) {
                        let x = startCol + j;
                        let y = startRow + i;
                        if (x < 0 || x >= cols || y < 0 || y >= rows || grid[y][x]) {
                            return false;
                        }
                    }
                }
            }
            return true;
        });
    }

    function rotateShape(shape) {
        const newShape = [];
        for (let i = 0; i < shape[0].length; i++) {
            newShape.push([]);
            for (let j = shape.length - 1; j >= 0; j--) {
                newShape[i].push(shape[j][i]);
            }
        }
        return newShape;
    }


    function placeShape(shape, color, row, col) {
        let placedBlocks = [];
        // Simulate placement to predict future state issues
        function simulatePlacement(testShape, testRow, testCol) {
            for (let rowIndex = 0; rowIndex < testShape.length; rowIndex++) {
                for (let colIndex = 0; colIndex < testShape[0].length; colIndex++) {
                    if (testShape[rowIndex][colIndex] === 1) {
                        const targetRow = testRow + rowIndex;
                        const targetCol = testCol + colIndex;
                        // Check if the cell is out of horizontal bounds or already occupied
                        if (targetCol >= cols || targetCol < 0 || grid[targetRow] && grid[targetRow][targetCol]) {
                            return false;
                        }
                        // Check if the cell is out of vertical bounds (bottom)
                        if (targetRow >= rows) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        // Check if the rotation of the block is valid at the initial place
        if (!simulatePlacement(shape, row, col)) {
            return false;
        }

        // Check boundaries and if the cell is already occupied
        for (let rowIndex = 0; rowIndex < shape.length; rowIndex++) {
            for (let colIndex = 0; colIndex < shape[0].length; colIndex++) {
                if (shape[rowIndex][colIndex] === 1) {
                    const targetRow = row + rowIndex;
                    const targetCol = col + colIndex;
                    if (targetRow >= rows || targetCol >= cols || targetCol < 0 || grid[targetRow][targetCol]) {
                        return false; // Block cannot be placed
                    }
                }
            }
        }

        // Place the block if all checks are passed
        for (let rowIndex = 0; rowIndex < shape.length; rowIndex++) {
            for (let colIndex = 0; colIndex < shape[0].length; colIndex++) {
                if (shape[rowIndex][colIndex] === 1) {
                    const targetRow = row + rowIndex;
                    const targetCol = col + colIndex;

                    // Additional check to ensure that the placement is within the bounds and the cell is not already occupied
                    if (targetRow < 0 || targetRow >= rows || targetCol < 0 || targetCol >= cols || grid[targetRow][targetCol]) {
                        continue;
                    }

                    grid[targetRow][targetCol] = true; // Mark the grid cell as occupied
                    const block = document.createElement('div');
                    block.className = `game-block ${color}`;
                    block.style.position = 'absolute';
                    block.style.left = `${targetCol * blockSize}px`;
                    block.style.top = `${targetRow * blockSize}px`;
                    block.style.width = `${blockSize}px`;
                    block.style.height = `${blockSize}px`;
                    placedBlocks.push({ element: block, row: targetRow, col: targetCol });
                    container.appendChild(block);
                }
            }
        }

        checkAndClearFullRowsAndColumns();
        updateScore(40);
        sendScoreToServer(40);
        return true;
    }

    function checkAndClearFullRowsAndColumns() {
        let filledRows = [];
        let filledColumns = Array(cols).fill(true);

        // Check for filled rows and columns
        for (let row = 0; row < rows; row++) {
            let isRowFilled = true;
            for (let col = 0; col < cols; col++) {
                if (!grid[row][col]) {
                    isRowFilled = false;
                    filledColumns[col] = false;
                }
            }
            if (isRowFilled) {
                filledRows.push(row);
            }
        }

        // Clear filled rows
        filledRows.forEach(row => {
            for (let col = 0; col < cols; col++) {
                grid[row][col] = false;
                removeBlockFromDOM(row, col);
            }
            updateScore(POINTS_PER_ROW);
            sendScoreToServer(POINTS_PER_ROW + 40);
            clearedRowsCount += 1;
        });

        document.getElementById('rowsDisplay').textContent = clearedRowsCount;

        // Clear filled columns
        filledColumns.forEach((isColFilled, col) => {
            if (isColFilled) {
                for (let row = 0; row < rows; row++) {
                    grid[row][col] = false;
                    removeBlockFromDOM(row, col);
                }
                clearedColumnsCount += 1;
                updateScore(POINTS_PER_COLUMN);
                sendScoreToServer(POINTS_PER_COLUMN + 40);
            }
        });
        document.getElementById('columnsDisplay').textContent = clearedColumnsCount;
        // Handle block dropping animation and grid update if needed
        dropBlocks();
    }

    function removeBlockFromDOM(row, col) {
        const blocks = container.querySelectorAll('.game-block');
        blocks.forEach(block => {
            const blockRow = parseInt(block.style.top) / blockSize;
            const blockCol = parseInt(block.style.left) / blockSize;
            if (blockRow === row && blockCol === col) {
                container.removeChild(block);
            }
        });
    }

    function dropBlocks() {
        // Logic to drop blocks down to fill empty spaces after row/column clearance
        for (let col = 0; col < cols; col++) {
            let emptyIndex = rows - 1;
            for (let row = rows - 1; row >= 0; row--) {
                if (!grid[row][col] && grid[emptyIndex][col]) {
                    emptyIndex = row; // Find the first empty cell from the bottom
                } else if (grid[row][col] && emptyIndex < row) {
                    // Move the block in the grid
                    grid[emptyIndex][col] = true;
                    grid[row][col] = false;

                    // Move the block visually
                    const blocks = container.querySelectorAll('.game-block');
                    blocks.forEach(block => {
                        const blockRow = parseInt(block.style.top) / blockSize;
                        const blockCol = parseInt(block.style.left) / blockSize;
                        if (blockRow === row && blockCol === col) {
                            block.style.top = `${emptyIndex * blockSize}px`;
                        }
                    });

                    emptyIndex--;
                }
            }
        }
    }


    function updateScore(points) {
        score += points;
        document.getElementById('scoreDisplay').textContent = score.toString();
    }

    function sendScoreToServer(score) {
        fetch('http://localhost:8080/scores/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: sessionStorage.getItem('username'),
                experience: score
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                // Check for no content explicitly
                if (response.status === 204 || response.headers.get("content-length") === "0") {
                    console.log('Score updated successfully');
                    return null; // No content to process
                } else {
                    return response.json(); // Process JSON response
                }
            })
            .then(data => {
                if (data) { // Check if there's any data to log
                    console.log('Score updated successfully', data);
                }
            })
            .catch(error => console.error('Error updating score', error));
    }

    function sendGamesToServer(gamesPlayed) {
        fetch('http://localhost:8080/scores/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: sessionStorage.getItem('username'),
                playTime: gamesPlayed
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                // Check for no content explicitly
                if (response.status === 204 || response.headers.get("content-length") === "0") {
                    console.log('Score updated successfully');
                    return null; // No content to process
                } else {
                    return response.json(); // Process JSON response
                }
            })
            .then(data => {
                if (data) { // Check if there's any data to log
                    console.log('Score updated successfully', data);
                }
            })
            .catch(error => console.error('Error updating score', error));
    }

    function sendFinishedToServer(finishedGames) {
        fetch('http://localhost:8080/scores/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: sessionStorage.getItem('username'),
                gameCompleted: finishedGames
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                // Check for no content explicitly
                if (response.status === 204 || response.headers.get("content-length") === "0") {
                    console.log('Score updated successfully');
                    return null; // No content to process
                } else {
                    return response.json(); // Process JSON response
                }
            })
            .then(data => {
                if (data) { // Check if there's any data to log
                    console.log('Score updated successfully', data);
                }
            })
            .catch(error => console.error('Error updating score', error));
    }

    const refreshButton = document.querySelector('.again');
    if (refreshButton) {
        refreshButton.addEventListener('click', function () {
            score = 0;
            clearedRowsCount = 0;
            clearedColumnsCount = 0;
            document.getElementById('scoreDisplay').textContent = '0';
            gamesPlayed++;
            gameOverMessage.style.display = 'none';
            gameStarted = false;
            startGame();
        });
    } else {
        console.error("Refresh button not found!");
    }


    startGame();
});
