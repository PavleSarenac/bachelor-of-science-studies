$(document).ready(function () {
    let bestScores = [];
    let bestScoresData = localStorage.getItem("bestScores");
    let mostRecentUsername = localStorage.getItem("recentUsername");
    let mostRecentScore = parseInt(localStorage.getItem("recentScore"));
    let gamePlayed = localStorage.getItem("gamePlayed");

    if (gamePlayed == "1") {
        localStorage.setItem("gamePlayed", "0");

        if (bestScoresData) {
            bestScores = JSON.parse(bestScoresData);
            bestScores.push({
                username: mostRecentUsername,
                score: mostRecentScore
            });
            bestScores.sort((a, b) => (parseInt(a.score) < parseInt(b.score) ? 1 : -1));
            if (bestScores.length >= 6) {
                bestScores.pop();
            }
            localStorage.setItem("bestScores", JSON.stringify(bestScores));
        } else if (mostRecentUsername) {
            bestScores.push({
                username: mostRecentUsername,
                score: mostRecentScore
            });
            localStorage.setItem("bestScores", JSON.stringify(bestScores));
        }
        printBestScores();
        $("#homeButton").append("<button class='btn btn-dark btn-lg' id='playAgain'>Play again</button>");
    } else {
        if (bestScoresData) {
            bestScores = JSON.parse(bestScoresData);
            printBestScores();
        }
    }

    if (mostRecentUsername) {
        printLastScore();
    }

    function getTableCellScoreboard(i, j) {
        return $("#scoreboard tr:eq(" + i + ") td:eq(" + j + ")");
    }

    function getTableCellLastgame(i, j) {
        return $("#lastgame tr:eq(" + i + ") td:eq(" + j + ")");
    }

    function printBestScores() {
        for (let i = 0; i < bestScores.length; i++) {
            let userCell = getTableCellScoreboard(i + 1, 0);
            let scoreCell = getTableCellScoreboard(i + 1, 1);

            userCell.empty();
            userCell.text(bestScores[i].username);

            scoreCell.empty();
            scoreCell.text(bestScores[i].score);
        }
    }

    function printLastScore() {
        let userCell = getTableCellLastgame(1, 0);
        let scoreCell = getTableCellLastgame(1, 1);

        userCell.empty();
        userCell.text(mostRecentUsername);

        scoreCell.empty();
        scoreCell.text(mostRecentScore);
    }

    $("#home").click(function () {
        window.location.replace("zmijica-uputstvo.html");
    })

    $("#homeButton").on("click", "#playAgain", function () {
        window.location.replace("zmijica-igra.html");
    });

});