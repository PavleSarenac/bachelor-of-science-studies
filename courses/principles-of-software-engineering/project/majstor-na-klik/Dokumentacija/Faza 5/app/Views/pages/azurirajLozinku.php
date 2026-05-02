<!--
Autori: Ljubica Majstorovic 2020/0253
-->
<div class="row text-center">
            <h1><strong>Ažuriraj lozinku:</strong></h1>
        </div>
        <div class="row">
            <form action="<?= site_url("$controller/submitPass") ?>" method="post">
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="password" class="form-control" name = "pass" placeholder="Unesite staru lozinku..." value="<?= set_value("pass") ?>">
                    </div>
                    <div class="col errorMessages">
                    <?php if(!empty($poruka)) 
                            echo $poruka;
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col ">
                        <input type="password" class="form-control" name = "pass2" placeholder="Unesite novu lozinku...">
                    </div>
                    <div class="col errorMessages">
                    <?php if(!empty($errors['pass2'])) 
                            echo $errors['pass2'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="password" class="form-control" name = "pass3" placeholder="Unesite novu lozinku...">
                    </div>
                    <div class="col errorMessages">
                    <?php if(!empty($errors['pass3'])) 
                            echo $errors['pass3'];
                        ?>
                    </div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <button type="submit" class="btn btn-down btn-dark text-yellow">Ažuriraj</button>
                    </div>
                    <div class="col"></div>
                </div>
            </form>
        </div>
    </div>
</body>

</html>