/*
Autori:
Pavle Sarenac 2020/0359
*/
$(document).ready(function () {

    function setPriceRadio() {
        if ($("#criteriaPrice").prop("checked") == true) {
            $("#inlineRadio1").prop("disabled", false);
            $("#inlineRadio2").prop("disabled", false);

            $("#inlineRadio2").prop("checked", true);
        } else {
            $("#inlineRadio1").prop("disabled", true);
            $("#inlineRadio2").prop("disabled", true);

            $("#inlineRadio1").prop("checked", false);
            $("#inlineRadio2").prop("checked", false);
        }
    }

    function setSpeedRadio() {
        if ($("#criteriaSpeed").prop("checked") == true) {
            $("#inlineRadio3").prop("disabled", false);
            $("#inlineRadio4").prop("disabled", false);

            $("#inlineRadio4").prop("checked", true);
        } else {
            $("#inlineRadio3").prop("disabled", true);
            $("#inlineRadio4").prop("disabled", true);

            $("#inlineRadio3").prop("checked", false);
            $("#inlineRadio4").prop("checked", false);
        }
    }

    function setQualityRadio() {
        if ($("#criteriaQuality").prop("checked") == true) {
            $("#inlineRadio5").prop("disabled", false);
            $("#inlineRadio6").prop("disabled", false);

            $("#inlineRadio6").prop("checked", true);
        } else {
            $("#inlineRadio5").prop("disabled", true);
            $("#inlineRadio6").prop("disabled", true);

            $("#inlineRadio5").prop("checked", false);
            $("#inlineRadio6").prop("checked", false);
        }
    }

    $("#criteriaPrice").on("click", setPriceRadio);

    $("#criteriaSpeed").on("click", setSpeedRadio);

    $("#criteriaQuality").on("click", setQualityRadio);

    $("#submit-btn").on("click", function () {
        $("#criteriaPrice").prop("checked", false);
        $("#criteriaSpeed").prop("checked", false);
        $("#criteriaQuality").prop("checked", false);
    });

    function loadNextBatchOfResults() {
        let urlParts = window.location.href.split("/");
        let newUrl = "http://localhost:8080/" + urlParts[3] + "/fetchNextResults";
        $.ajax({
            url: newUrl,
            success: function (result) {
                $("#searchResultsContainer").append(result);
            }
        });
    }

    $(window).scroll(function () {
        if ($(document).height() - $(this).height() <= $(this).scrollTop() + 100) {
            loadNextBatchOfResults();
        };
    });

    // Price review
    $("#price-star-1").on("click", function () {
        for (let i = 1; i <= 1; i++) {
            $("#price-star-" + i).prop("checked", true);
        }
        for (let i = 2; i <= 5; i++) {
            $("#price-star-" + i).prop("checked", false);
        }
    });

    $("#price-star-2").on("click", function () {
        for (let i = 1; i <= 2; i++) {
            $("#price-star-" + i).prop("checked", true);
        }
        for (let i = 3; i <= 5; i++) {
            $("#price-star-" + i).prop("checked", false);
        }
    });

    $("#price-star-3").on("click", function () {
        for (let i = 1; i <= 3; i++) {
            $("#price-star-" + i).prop("checked", true);
        }
        for (let i = 4; i <= 5; i++) {
            $("#price-star-" + i).prop("checked", false);
        }
    });

    $("#price-star-4").on("click", function () {
        for (let i = 1; i <= 4; i++) {
            $("#price-star-" + i).prop("checked", true);
        }
        for (let i = 5; i <= 5; i++) {
            $("#price-star-" + i).prop("checked", false);
        }
    });

    $("#price-star-5").on("click", function () {
        for (let i = 1; i <= 5; i++) {
            $("#price-star-" + i).prop("checked", true);
        }
    });

    // Speed review
    $("#speed-star-1").on("click", function () {
        for (let i = 1; i <= 1; i++) {
            $("#speed-star-" + i).prop("checked", true);
        }
        for (let i = 2; i <= 5; i++) {
            $("#speed-star-" + i).prop("checked", false);
        }
    });

    $("#speed-star-2").on("click", function () {
        for (let i = 1; i <= 2; i++) {
            $("#speed-star-" + i).prop("checked", true);
        }
        for (let i = 3; i <= 5; i++) {
            $("#speed-star-" + i).prop("checked", false);
        }
    });

    $("#speed-star-3").on("click", function () {
        for (let i = 1; i <= 3; i++) {
            $("#speed-star-" + i).prop("checked", true);
        }
        for (let i = 4; i <= 5; i++) {
            $("#speed-star-" + i).prop("checked", false);
        }
    });

    $("#speed-star-4").on("click", function () {
        for (let i = 1; i <= 4; i++) {
            $("#speed-star-" + i).prop("checked", true);
        }
        for (let i = 5; i <= 5; i++) {
            $("#speed-star-" + i).prop("checked", false);
        }
    });

    $("#speed-star-5").on("click", function () {
        for (let i = 1; i <= 5; i++) {
            $("#speed-star-" + i).prop("checked", true);
        }
    });

    // Quality review
    $("#quality-star-1").on("click", function () {
        for (let i = 1; i <= 1; i++) {
            $("#quality-star-" + i).prop("checked", true);
        }
        for (let i = 2; i <= 5; i++) {
            $("#quality-star-" + i).prop("checked", false);
        }
    });

    $("#quality-star-2").on("click", function () {
        for (let i = 1; i <= 2; i++) {
            $("#quality-star-" + i).prop("checked", true);
        }
        for (let i = 3; i <= 5; i++) {
            $("#quality-star-" + i).prop("checked", false);
        }
    });

    $("#quality-star-3").on("click", function () {
        for (let i = 1; i <= 3; i++) {
            $("#quality-star-" + i).prop("checked", true);
        }
        for (let i = 4; i <= 5; i++) {
            $("#quality-star-" + i).prop("checked", false);
        }
    });

    $("#quality-star-4").on("click", function () {
        for (let i = 1; i <= 4; i++) {
            $("#quality-star-" + i).prop("checked", true);
        }
        for (let i = 5; i <= 5; i++) {
            $("#quality-star-" + i).prop("checked", false);
        }
    });

    $("#quality-star-5").on("click", function () {
        for (let i = 1; i <= 5; i++) {
            $("#quality-star-" + i).prop("checked", true);
        }
    });

    $("#reviewForm").submit(function (event) {
        let priceRated = false;
        let speedRated = false;
        let qualityRated = false;
        // Check price rating
        for (let i = 1; i <= 5; i++) {
            if ($("#price-star-" + i).prop("checked") == true) {
                priceRated = true;
            }
        }
        // Check speed rating
        for (let i = 1; i <= 5; i++) {
            if ($("#speed-star-" + i).prop("checked") == true) {
                speedRated = true;
            }
        }
        // Check quality rating
        for (let i = 1; i <= 5; i++) {
            if ($("#quality-star-" + i).prop("checked") == true) {
                qualityRated = true;
            }
        }
        if (!priceRated || !speedRated || !qualityRated) {
            alert("Niste ocenili majstora po svim parametrima!");
            event.preventDefault();
        }
    });
});