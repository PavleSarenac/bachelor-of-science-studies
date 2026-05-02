<!--
Autori: Ljubica Majstorovic 2020/0253
-->
<div class="row text-center">
            <h1><strong>Ažuriraj telefon:</strong></h1>
        </div>
        <div class="row">
            <form action="<?= site_url("$controller/submitPhone") ?>" method="post">
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name = "phone" class="form-control" placeholder="Unesite nov broj telefona..."  value ="<?= set_value("phone") ?>">
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col text-center">
                        <button type="submit" class="btn btn-dark btn-down text-yellow">Ažuriraj</button>
                    </div>
                    <div class="col errorMessages">
                    <?php if(!empty($errors['phone'])) 
                            echo $errors['phone'];
                        ?>
                    </div>
                </div>
            </form>
        </div>
    </div>
</body>

</html>