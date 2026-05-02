<!--
Autori: Ljubica Majstorovic 2020/0253
-->
        <div class="row text-center">
            <h1><strong>Ažuriraj svoj profil:</strong></h1>
        </div>
        <div class="row">
            <div class="row">
                <div class="col"></div>
                <div class="col text-center">
                    <?php
                    echo anchor(
                        "$controller/azurirajLozinku",
                        '<button class= "btn btn-side btn-dark text-yellow btn-style">Azuriraj lozinku</button>'
                    );
                    ?>
                </div>
                <div class="col"></div>
            </div>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center">
                <?php
                        echo anchor(
                            "$controller/azurirajTelefon",
                            '<button class= "btn btn-side btn-dark text-yellow btn-style">Azuriraj telefon</button>'
                        );
                        ?>
                </div>
                <div class="col"></div>
            </div>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center">
                    <?php
                        echo  anchor(
                            "$controller/azurirajMejl",
                            '<button class= "btn btn-side btn-dark text-yellow btn-style">Azuriraj mejl</button>'
                        );
                        ?>  
                    </div>
                <div class="col"></div>
            </div>
            <div class="row">
                    <div class="col"></div>
                    <div class="col text-center">
                    <?php
                        echo anchor(
                            "$controller/azurirajLokaciju",
                            '<button class= "btn btn-side btn-dark text-yellow btn-style">Azuriraj lokaciju</button>'
                        );
                        ?>
                    </div>
                    <div class="col"></div>
                </div>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center">
                    <?php
                        echo  anchor(
                            "$controller/azurirajSliku",
                            '<button class= "btn btn-side btn-dark text-yellow btn-style">Azuriraj sliku</button>'
                        );
                        ?>  
                    </div>
                <div class="col"></div>
            </div>
        </div>
    </div>
</body>

</html>