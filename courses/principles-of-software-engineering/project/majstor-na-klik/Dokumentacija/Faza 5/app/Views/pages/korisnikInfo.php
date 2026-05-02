<!--
Autori: 
Ljubica Majstorovic 2020/0253
Nikola Nikolic 2020/0357
Pavle Sarenac 2020/0359
-->
        <div class="row text-center">
            <div class="col">
                <h1><strong>Pregled profila korisnika:</strong></h1>
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col flex-center">
                <?php
                    $session = session();
                    switch ($controller) {
                        case "Gost":
                            break;
                        case "Korisnik":
                            if ($email != $session->get("tekuciKorisnikPodaci")[0]->MejlAdresa) {
                                echo "<button id='porukaMajstor' class='btn btn-side btn-dark text-yellow btn-style'>
                                Ostavi poruku korisniku
                                </button>";
                            } else {
                                echo anchor("Korisnik/azurirajProfil", "<button class='btn btn-side btn-dark text-yellow btn-style'>
                                Ažuriraj svoj profil
                                </button>");
                            }
                            break;
                        default:
                            echo "<button id='porukaMajstor' class='btn btn-side btn-dark text-yellow btn-style'>
                            Ostavi poruku korisniku
                            </button>";
                            break;
                    }
                ?>
            </div>
            <div class="col">
                <div class="card mx-auto">
                    <div class="aspect-ratio">
                        <div class="card-img-top-wrapper">
                            <img class="card-img-top img-fluid" src = "<?php
                            if($path != null){
                                echo base_url('./' . $path);
                            } else {
                                echo "/images/defaultProfilePicture.png";
                            } ?>" alt="Card image cap">
                        </div>
                    </div>
                    <div class="card-body">
                        <p class="card-text">
                            <?php
                                echo $name . " " . $surname . ", " . $city .
                                    "<br>Telefon: ". urlencode($phone) . "<br>" . 
                                    "Mejl: " . $email;
                            ?>
                        </p>
                    </div>
                </div>
            </div>
            <div class="col"></div>
        </div>
        <div class="row" id="emptyUserFooter">

        </div>
    </div>
</body>

</html>