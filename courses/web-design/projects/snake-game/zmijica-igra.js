$(document).ready(function () {
    // First element of array snakeCells is snake's tail, and last element is snake's head. 
    let snakeCells = [];
    let tableDimension = getTableDimension();
    let snakeSpeed = getSnakeSpeed();
    let bestScores = [];
    initializeScores();
    createGameTable();
    setRegularFood(Math.floor(tableDimension / 2), tableDimension - 5);
    generateNewSpecialFood();

    function initializeScores() {
        $("#current-score").text("0");
        let data = localStorage.getItem("bestScores");
        if (data) {
            bestScores = JSON.parse(data);
            bestScores.sort((a, b) => (parseInt(a.score) < parseInt(b.score)));
            $("#best-score").text(bestScores[0].score);
        } else {
            $("#best-score").text("0");
        }
    }

    function createGameTable() {
        for (let i = 0; i < tableDimension; i++) {
            let row = $("<tr></tr>");
            for (let j = 0; j < tableDimension; j++) {
                let cell = $("<td></td>");
                setCellSize(cell);
                setCellColor(cell, i, j);
                row.append(cell);
            }
            $("#game-table").append(row);
        }
    }

    function setRegularFood(i, j) {
        let foodCell = getTableCell(i, j);
        let foodImg = $("<img class='img-fluid' id='regular-food' src='zmijica-dodatno/red_circle.png'>");
        foodCell.append(foodImg);
    }

    function setSpecialFood(i, j) {
        let foodCell = getTableCell(i, j);
        let foodImg = $("<img class='img-fluid' id='special-food' src='zmijica-dodatno/golden_circle.png'>");
        foodCell.append(foodImg);
    }

    async function generateNewSpecialFood() {
        while (true) {
            await sleep(10000);

            let i = Math.floor(Math.random() * tableDimension);
            let j = Math.floor(Math.random() * tableDimension);
            let tableCell = getTableCell(i, j);

            while (tableCell.css("background-color") == "rgb(5, 241, 249)" ||
                tableCell.find("img").length > 0) {
                i = Math.floor(Math.random() * tableDimension);
                j = Math.floor(Math.random() * tableDimension);
                tableCell = getTableCell(i, j);
            }

            setSpecialFood(i, j);
            await sleep(3000);
            tableCell.empty();
        }
    }

    function getTableDimension() {
        let tableSize = parseInt(localStorage.getItem("tableSize"));
        let dimension;
        switch (tableSize) {
            case 1:
                dimension = 15;
                break;
            case 2:
                dimension = 20;
                break;
            case 3:
                dimension = 25;
                break;
        }
        return dimension;
    }

    function getSnakeSpeed() {
        let speed = parseInt(localStorage.getItem("snakeSpeed"));
        let snakeDelay;
        switch (speed) {
            case 1:
                snakeDelay = 100;
                break;
            case 2:
                snakeDelay = 80;
                break;
            case 3:
                snakeDelay = 50;
                break;
        }
        return snakeDelay;
    }

    function setCellSize(cell) {
        cell.css({
            "min-width": "20px",
            "min-height": "20px"
        });
        switch (tableDimension) {
            case 15:
                cell.css({
                    "width": "40px",
                    "height": "40px"
                });
                break;
            case 20:
                cell.css({
                    "width": "30px",
                    "height": "30px"
                });
            case 25:
                cell.css({
                    "width": "25px",
                    "height": "25px"
                });
        }
    }

    function setCellColor(cell, i, j) {
        if ((i == Math.floor(tableDimension / 2)) && (j >= 1 && j <= 3)) {
            setSnakeCell(cell, i, j);
        } else {
            setTableCell(cell, i, j);
        }
    }

    function setTableCell(cell, i, j) {
        if ((i + j) % 2 == 0) {
            cell.css({
                "background-color": "black"
            });
        } else {
            cell.css({
                "background-color": "#202020"
            });
        }
    }

    function setSnakeCellExtend(cell, i, j) {
        snakeCells.unshift([i, j]);
        cell.css({
            "background-color": "#05f1f9"
        });
    }

    function setSnakeCell(cell, i, j) {
        snakeCells.push([i, j]);
        cell.css({
            "background-color": "#05f1f9"
        });
    }

    function updateScore(headCell) {
        if (headCell.find("img").length > 0) {
            let currentScore = parseInt($("#current-score").text());
            if (headCell.find("#regular-food").length > 0) {
                currentScore++;
                generateNewRegularFood();
            } else if (headCell.find("#special-food").length > 0) {
                currentScore += 10;
            }
            $("#current-score").text(currentScore);
            headCell.empty();
            extendSnake();
        }
    }

    function freeSlot(i, j) {
        if (getTableCell(i, j).css("background-color") != "rgb(5, 241, 249)" &&
            getTableCell(i, j).find("img").length == 0) {
            return true;
        } else {
            return false;
        }
    }

    function extendSnake() {
        let snakeTail = snakeCells[0];
        let tailX = snakeTail[0], tailY = snakeTail[1];

        if (tailY - 1 >= 0 && freeSlot(tailX, tailY - 1)) {

            setSnakeCellExtend(getTableCell(tailX, tailY - 1), tailX, tailY - 1);

        } else if (tailX + 1 < tableDimension && freeSlot(tailX + 1, tailY)) {

            setSnakeCellExtend(getTableCell(tailX + 1, tailY), tailX + 1, tailY);

        } else if (tailY + 1 < tableDimension && freeSlot(tailX, tailY + 1)) {

            setSnakeCellExtend(getTableCell(tailX, tailY + 1), tailX, tailY + 1);

        } else if (tailX - 1 >= 0 && freeSlot(tailX - 1, tailY)) {

            setSnakeCellExtend(getTableCell(tailX - 1, tailY), tailX - 1, tailY);

        }
    }

    function generateNewRegularFood() {
        let i = Math.floor(Math.random() * tableDimension);
        let j = Math.floor(Math.random() * tableDimension);
        let tableCell = getTableCell(i, j);
        while (tableCell.css("background-color") == "rgb(5, 241, 249)" ||
            tableCell.find("img").length > 0) {
            i = Math.floor(Math.random() * tableDimension);
            j = Math.floor(Math.random() * tableDimension);
            tableCell = getTableCell(i, j);
        }
        setRegularFood(i, j);
    }

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    let snakeDirection = null;

    $(this).keydown(function (event) {
        controlSnakeMovement(event);
    });

    function controlSnakeMovement(event) {
        let previousDirection = snakeDirection;
        switch (event.which) {
            case 37:
                if (previousDirection && previousDirection != "right" && previousDirection != "left") {
                    snakeDirection = "left";
                    moveSnakeLeft();
                }
                break;
            case 38:
                if (previousDirection != "down" && previousDirection != "up") {
                    snakeDirection = "up";
                    moveSnakeUp();
                }
                break;
            case 39:
                if (previousDirection != "left" && previousDirection != "right") {
                    snakeDirection = "right";
                    moveSnakeRight();
                }
                break;
            case 40:
                if (previousDirection != "up" && previousDirection != "down") {
                    snakeDirection = "down";
                    moveSnakeDown();
                }
                break;
        }
    }

    async function moveSnakeLeft() {
        let snakeHead = snakeCells[snakeCells.length - 1];
        let headX = snakeHead[0], headY = snakeHead[1];
        if (headY - 1 < 0 || getTableCell(headX, headY - 1).css("background-color") == "rgb(5, 241, 249)") {
            gameOver();
            return;
        }
        let snakeTail = snakeCells.shift();
        let tailX = snakeTail[0], tailY = snakeTail[1];

        let tailCell = getTableCell(tailX, tailY);
        let headCell = getTableCell(headX, headY - 1);

        setTableCell(tailCell, tailX, tailY);
        setSnakeCell(headCell, headX, headY - 1);

        updateScore(headCell);

        await sleep(snakeSpeed);
        if (snakeDirection == "left") {
            moveSnakeLeft();
        }
    }

    async function moveSnakeUp() {
        let snakeHead = snakeCells[snakeCells.length - 1];
        let headX = snakeHead[0], headY = snakeHead[1];
        if (headX - 1 < 0 || getTableCell(headX - 1, headY).css("background-color") == "rgb(5, 241, 249)") {
            gameOver();
            return;
        }
        let snakeTail = snakeCells.shift();
        let tailX = snakeTail[0], tailY = snakeTail[1];

        let tailCell = getTableCell(tailX, tailY);
        let headCell = getTableCell(headX - 1, headY);

        setTableCell(tailCell, tailX, tailY);
        setSnakeCell(headCell, headX - 1, headY);

        updateScore(headCell);

        await sleep(snakeSpeed);
        if (snakeDirection == "up") {
            moveSnakeUp();
        }
    }

    async function moveSnakeRight() {
        let snakeHead = snakeCells[snakeCells.length - 1];
        let headX = snakeHead[0], headY = snakeHead[1];
        if (headY + 1 >= tableDimension || getTableCell(headX, headY + 1).css("background-color") == "rgb(5, 241, 249)") {
            gameOver();
            return;
        }
        let snakeTail = snakeCells.shift();
        let tailX = snakeTail[0], tailY = snakeTail[1];

        let tailCell = getTableCell(tailX, tailY);
        let headCell = getTableCell(headX, headY + 1);

        setTableCell(tailCell, tailX, tailY);
        setSnakeCell(headCell, headX, headY + 1);

        updateScore(headCell);

        await sleep(snakeSpeed);
        if (snakeDirection == "right") {
            moveSnakeRight();
        }
    }

    async function moveSnakeDown() {
        let snakeHead = snakeCells[snakeCells.length - 1];
        let headX = snakeHead[0], headY = snakeHead[1];
        if (headX + 1 >= tableDimension || getTableCell(headX + 1, headY).css("background-color") == "rgb(5, 241, 249)") {
            gameOver();
            return;
        }
        let snakeTail = snakeCells.shift();
        let tailX = snakeTail[0], tailY = snakeTail[1];

        let tailCell = getTableCell(tailX, tailY);
        let headCell = getTableCell(headX + 1, headY);

        setTableCell(tailCell, tailX, tailY);
        setSnakeCell(headCell, headX + 1, headY);

        updateScore(headCell);

        await sleep(snakeSpeed);
        if (snakeDirection == "down") {
            moveSnakeDown();
        }
    }

    let isGameOver = false;
    function gameOver() {
        if (!isGameOver) {
            let username = prompt("Game over! Please enter your name:");
            if (!username) {
                username = "Private Player";
            }

            localStorage.setItem("recentUsername", username);
            localStorage.setItem("recentScore", $("#current-score").text());
            localStorage.setItem("gamePlayed", "1");

            isGameOver = true;
            window.location.replace("zmijica-rezultati.html");
        }
    }

    function getTableCell(i, j) {
        return $("#game-table tr:eq(" + i + ") td:eq(" + j + ")");
    }

});
