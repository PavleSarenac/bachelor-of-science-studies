$(document).ready(function () {
    defaultSettings();

    function defaultSettings() {
        localStorage.setItem("tableSize", "2");
        localStorage.setItem("snakeSpeed", "2");
    }

    // How to play
    $("#how-to-btn").click(function () {
        $("#main").empty();
        showInstructions();
    });

    function showInstructions() {
        let instructionsDiv = $("<div class='row' id='main-how-to-play'></div>");
        let instructions = $("<div class='text-main'></div>");
        let heading = $("<h1 class='text-main'></h1>");

        heading.html("How to play<br><br>").css({
            "font-weight": "bolder"
        });
        instructions.html(
            "The direction in which snake moves is controlled by keyboard arrows. When the snake touches the food, she eats it and grows by one table field. The game is over when the snake touches either a table border or itself. The objective of the player is to make the snake as big as possible before the game is over. <br><br> There is regular food, which appears on the table always as soon as the old regular food has been eaten, but there is also special food, which appears on the table every ten seconds, and it disappears within few seconds. For every eaten piece of regular food - the player gets a point, and for every eaten piece of special food - the player gets ten points."
        );

        instructionsDiv.append(heading).append(instructions);
        $("#main").append(instructionsDiv);
    }

    // Settings
    $("#settings-btn").click(function () {
        $("#main").empty();
        showSettings();
    });

    function showSettings() {
        let settingsDiv = $("<div class='row' id='main-settings'></div>");
        let heading = $("<h1 class='text-main'></h1>");

        let tableSizeLabel = $("<label for='tableSize' class='form-label'><span class='text-main'>Table size</span></label>");
        let tableSizeRange = $("<input type='range' class='form-range' min='1' max='3' id='tableSize'></input>");

        let snakeSpeedLabel = $("<label for='snakeSpeed' class='form-label'><span class='text-main'>Snake speed</span></label>");
        let snakeSpeedRange = $("<input type='range' class='form-range' min='1' max='3' id='snakeSpeed'></input>");

        heading.html("Settings<br><br>").css({
            "font-weight": "bolder"
        });
        snakeSpeedLabel.css({
            "font-style": "italic",
            "font-weight": "bold"
        });
        tableSizeLabel.css({
            "font-style": "italic",
            "font-weight": "bold"
        });

        settingsDiv.
            append(heading).
            append(tableSizeLabel).
            append(tableSizeRange);

        settingsDiv.append($("<span><br><br></span>"));

        settingsDiv.
            append(snakeSpeedLabel).
            append(snakeSpeedRange);

        $("#main").append(settingsDiv);
    }

    // Adding a listener for the range input for table size - since it is dynamically loaded, we have to attach the listener to the parent div (which is statically loaded) inside of which it will be loaded.
    $("#main").on("change", "#tableSize", function () {
        localStorage.setItem("tableSize", $("#tableSize").val());
    });

    // Adding a listener for the range input for snake speed - since it is dynamically loaded, we have to attach the listener to the parent div (which is statically loaded) inside of which it will be loaded.
    $("#main").on("change", "#snakeSpeed", function () {
        localStorage.setItem("snakeSpeed", $("#snakeSpeed").val());
    });

    // Play
    $("#play-btn").click(function () {
        window.location.replace("zmijica-igra.html");
    });

    // Scoreboard
    $("#score-btn").click(function () {
        window.location.replace("zmijica-rezultati.html");
    });

});
