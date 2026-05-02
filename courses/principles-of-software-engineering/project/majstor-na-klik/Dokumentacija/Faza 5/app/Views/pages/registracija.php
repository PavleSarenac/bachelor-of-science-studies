<!--
Autori: 
Ljubica Majstorovic 2020/0253
-->
        <div class="row text-center">
            <h1><strong>Registracija:</strong></h1>
        </div>
        <div class="row">
            <form action="<?= site_url("Gost/submitRegistration") ?>" method="post">
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name="ime" class="form-control" placeholder="Unesite Vaše ime..."
                        value="<?= set_value("ime") ?>">
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($errors['ime'])) 
                            echo $errors['ime'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name="prezime" class="form-control" placeholder="Unesite Vaše prezime..." value="<?= set_value("prezime") ?>">
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($errors['prezime'])) 
                            echo $errors['prezime'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name="username" class="form-control" placeholder="Unesite korisničko ime..." 
                        value="<?= set_value("username") ?>">
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($errors['username'])) 
                            echo $errors['username'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name="mail" class="form-control" placeholder="Unesite mejl adresu..." 
                        value="<?= set_value("mail") ?>">
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($errors['mail'])) 
                            echo $errors['mail'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name="phone" class="form-control" placeholder="Unesite broj telefona u formatu +381-6x-xxx-xxx(x)..." value="<?= set_value("phone") ?>">
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($errors['phone'])) 
                            echo $errors['phone'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <select class="form-select form-select-lg" name="cities">
                            <option>Izaberite grad:</option>
                            <?php foreach ($gradovi as $grad) {
                                $selected = ($grad->Naziv === set_value("cities")) ? "selected" : "";
                                echo "<option $selected>$grad->Naziv</option>";
                            }
                            ?>
                        </select>
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($cityError)) 
                            echo $cityError;
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="password" name="pass" class="form-control" placeholder="Unesite lozinku..." value="<?= set_value("pass") ?>">
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($errors['pass'])) 
                            echo $errors['pass'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="password" name="password" class="form-control" placeholder="Potvrdite lozinku..." value="<?= set_value("password") ?>">
                    </div>
                    <div class="col errorMessages">
                        <?php if(!empty($errors['password'])) 
                            echo $errors['password'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col text-center">
                        <div class="form-check form-check-inline">
                            Registruj se kao:
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" name="userType" type="radio" id="user-type-client" value="korisnik">
                            <label class="form-check-label" for="user-type-client">Korisnik</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input checked class="form-check-input" name="userType" type="radio" id="user-type-master" value="majstor">
                            <label class="form-check-label" for="user-type-master">Majstor</label>
                        </div>
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <button type="submit" class="btn btn-down btn-dark text-yellow">Registruj se</button>
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
                        Imate nalog? Prijavite se <?php echo anchor("Gost/prikazLogovanja", "ovde.") ?>
                    </div>
                    <div class="col"></div>
                </div>
            </form>
        </div>
    </div>
</body>

</html>