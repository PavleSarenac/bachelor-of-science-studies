<!--
Autori:
Pavle Sarenac 2020/0359
-->

<div class="row text-center">

<h1><strong>Recenzije majstora:

    <?php

        echo $handymanName . " " . $handymanSurname . " (" . $handymanSpecialty . ")"

    ?>

</strong></h1>

</div>

<div id="searchResultsContainer">

<?php

    $session = session();

    $reviews = $session->get("reviewsResult");

    $finalString = "";

    if (!empty($reviews)) {

        $finalString .= "<div class='alert alert-light' role='alert'>

        <div class='row'>

            <div class='col-sm-3 text-left'>

                <h3>Korisnik</h3>

            </div>

            <div class='col-sm-9 text-left'>

                <h3>Tekst recenzije</h3>

            </div>

        </div>

        </div>";

    }

    $cnt = 0;

    $actuallyPrinted = 0;

    foreach ($reviews as $review) {

        if ($cnt == 10) {

            break;

        }

        $cnt++;

        if ($review->Tekst != "") {

            $actuallyPrinted++;

            $encName = rawurlencode($review->Ime);

            $encSurname = rawurlencode($review->Prezime);

            $encCity = rawurlencode($review->Naziv);

            $encPhone = rawurlencode($review->Telefon);

            $encMail = rawurlencode($review->MejlAdresa);

            $encId = rawurlencode($review->IdKor);

            $finalString .=

                "<div class='alert alert-light' role='alert'><div class='row'>" .

                "<div class='col-sm-3 text-left'>" .

                anchor(

                    site_url($session->get("controller") . "/prikazProfilaKorisnika" .

                    "/$encName/$encSurname/$encCity/$encPhone/$encMail/$encId"),

                    $review->Ime . " " . $review->Prezime,

                    array('class' => 'majstorLink')) .

                "</div>" .

                "<div class='col-sm-9 text-left'>" . $review->Tekst . "</div>" .

                "</div>" . "</div>";  

        }

    }

    if ($actuallyPrinted == 0) {

        echo "<div class='alert alert-danger text-center' role='alert'>

        <h3>Ovaj majstor nema nijednu tekstualnu recenziju.</h3>

        </div>";

    } else {

        echo $finalString;

    }

?>

</div>

</div>

</div>

</body>




</html>