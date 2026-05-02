<!--
Autori: 
Ljubica Majstorovic 2020/0253
-->
<div class="row text-center">
            <h1><strong>Sve prijave:</strong></h1>
        </div>
        <div id="searchResultsContainer">
            <?php
            use App\Models\PrijavaModel;
                $session = session();
                $searchResult = $session->get("allReportsResult");
                
                echo "<div class='alert alert-light' role='alert'>
                    <div class='row'>
                        <div class='col text-center'>
                            <h3>Prijavljeni majstor</h3>
                        </div>
                        <div class='col text-center'>
                            <h3>Prijavu podneo</h3>
                        </div>
                        <div class='col text-center'>
                            <h3>Opis prijave</h3>
                        </div>
                        
                    </div>
                    </div>";
                $cnt = 0;
                foreach ($searchResult as $result) {
                    if ($cnt == 10) {
                        break;
                    }

                    $cnt++;

                    $prijavaModel= new PrijavaModel();
                    $resultKlijent = $prijavaModel->searchReporter($result->IdKli);
                    $resultKlijent = $resultKlijent[0];

                    $encodedKName = rawurlencode($resultKlijent->Ime);
                    $encodedKSurname = rawurlencode($resultKlijent->Prezime);
                    $encodedKCity = rawurlencode($resultKlijent->Naziv);
                    $encodedKPhone = urlencode($resultKlijent->Telefon);
                    $encodedKMail = rawurlencode($resultKlijent->MejlAdresa);
                    $idK = rawurlencode($resultKlijent->IdKor);
                    
                    $encodedName = rawurlencode($result->Ime);
                    $encodedSurname = rawurlencode($result->Prezime);
                    $encodedSpecialty = rawurlencode($result->Opis);
                    $encodedCity = rawurlencode($result->Naziv);
                    $encodedPhone = urlencode($result->Telefon);
                    $encodedMail = rawurlencode($result->MejlAdresa);
                    $id = rawurlencode($result->IdKor);
                    
                    echo "<div class='alert alert-light' role='alert'><div class='row' >" .
                    "<div class='row'><div class='col text-center'>" . anchor(
                        "$controller/prikazProfilaMajstora/$encodedName/$encodedSurname/$encodedSpecialty/$encodedCity/$encodedPhone/$encodedMail/$id",
                        $result->Ime. " " . $result->Prezime,
                        array('class' => 'majstorLink')
                    ) . "<br>" . $result->DatumVreme ."</div><div class='col text-center'>". anchor(
                        "$controller/prikazProfilaKorisnika/$encodedKName/$encodedKSurname/$encodedKCity/$encodedKPhone/$encodedKMail/$idK",
                        $resultKlijent->Ime. " " . $resultKlijent->Prezime,
                        array('class' => 'majstorLink')
                    ) . "</div><div class = 'col text-center text-break'>".$result->Tekst."</div></div>" .
                    "</div></div>"; 

                    
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