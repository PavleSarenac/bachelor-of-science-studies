<!--
Autori: 
Nikola Nikolic 2020/0357
-->
        <div class="row text-center">
            <h1><strong>Prijava:</strong></h1>
        </div>
        <div class="row">
            <form name="loginForm" action="<?php echo site_url("Gost/loginSubmit")?>" method="post">
                <div class="row">
                    <div class="col"></div>
                    <div class="col error-text">
                    <?php 
                        if(!empty($errors["usernameInput"])) {
                            $errorMessage = str_replace("usernameInput", "username", $errors["usernameInput"]);
                            echo $errorMessage;
                        }
                        if(!empty($message) && $message == "Korisničko ime ne postoji.")
                            echo $message;
                    ?>
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name="usernameInput" class="form-control" value="<?= set_value('usernameInput') ?>" placeholder="Unesite korisničko ime...">
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col error-text">
                        <?php 
                            if(!empty($errors["passwordInput"])) {
                                $errorMessage = str_replace("passwordInput", "password", $errors["passwordInput"]);
                                echo $errorMessage;
                            }
                            if(!empty($message) && $message == "Uneta lozinka nije ispravna.")
                                echo $message;
                        ?>
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="password" name="passwordInput" class="form-control" placeholder="Unesite lozinku...">
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <button type="submit" class="btn btn-down btn-dark text-yellow">Prijavi se</button>
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <hr>
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        Nemate nalog? Registrujte se <?php echo anchor("Gost/register", "ovde.") ?>
                    </div>
                    <div class="col"></div>
                </div>
            </form>
        </div>
    </div>
</body>

</html>