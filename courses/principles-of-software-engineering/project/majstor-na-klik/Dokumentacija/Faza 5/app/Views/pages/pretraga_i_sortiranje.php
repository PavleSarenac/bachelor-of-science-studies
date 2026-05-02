<!--
Autori:
Pavle Sarenac 2020/0359
-->
        <div class="row text-center">
            <h1><strong>
                <?php 
                if (!empty($searchHandyman) || !empty($searchCity)) {
                    echo "Rezultat pretrage za: " . "" .
                    ($searchHandyman ?? "") . ($searchHandyman ? " (vrsta majstora) " : " ") .
                    ($searchCity ?? "") . ($searchCity ? " (grad)" : "");
                } else {
                    echo "Svi majstori:";
                }
                ?>
            </strong></h1>
        </div>
        <div id="searchResultsContainer">
            <?php
                $session = session();
                $searchResult = $session->get("lastSearchResult");
                if (!empty($searchResult)) {
                    echo "<div class='alert alert-light' role='alert'>
                    <div class='row'>
                        <div class='col text-center'>
                            <h3>Majstor</h3>
                        </div>
                        <div class='col text-center'>
                            <h3>Broj recenzija</h3>
                        </div>
                        <div class='col text-center'>
                            <h3>Prosečna cena</h3>
                        </div>
                        <div class='col text-center'>
                            <h3>Prosečna brzina</h3>
                        </div>
                        <div class='col text-center'>
                            <h3>Prosečan kvalitet</h3>
                        </div>
                    </div>
                    </div>";
                }
                $cnt = 0;
                foreach ($searchResult as $result) {
                    if ($cnt == 10) {
                        break;
                    }
                    $cnt++;
                    $newHandyman = "<div class='alert alert-light' role='alert'><div class='row'>" .
                    "<div class='col text-center'>" . $result->Ime . " " . $result->Prezime . "</div>" .
                    "<div class='col text-center'>" . ($result->BrojRecenzija ?? "Nije ocenjen.") . "</div>" .
                    "<div class='col text-center'>" . ($result->ProsecnaCena ?? "Nije ocenjen.") . "</div>" . 
                    "<div class='col text-center'>" . ($result->ProsecnaBrzina ?? "Nije ocenjen.") . "</div>" .
                    "<div class='col text-center'>" . ($result->ProsecanKvalitet ?? "Nije ocenjen.") . "</div></div></div>";
                    $encodedName = rawurlencode($result->Ime);
                    $encodedSurname = rawurlencode($result->Prezime);
                    $encodedSpecialty = rawurlencode($result->Opis);
                    $encodedCity = rawurlencode($result->Naziv);
                    $encodedPhone = urlencode($result->Telefon);
                    $encodedMail = rawurlencode($result->MejlAdresa);
                    $id = rawurlencode($result->IdKor);
                    echo anchor(
                        "$controller/prikazProfilaMajstora/$encodedName/$encodedSurname/$encodedSpecialty/$encodedCity/$encodedPhone/$encodedMail/$id",
                        $newHandyman,
                        array('class' => 'majstorLink')
                    );
                }
                if (empty($searchResult)) {
                    echo "<div class='alert alert-danger text-center' role='alert'>
                    <h3>Nema majstora za unete parametre pretrage!</h3>
                    </div>";
                }
            ?>
        </div>
    </div>
</body>

</html>