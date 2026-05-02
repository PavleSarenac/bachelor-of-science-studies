<!--
Autori: Ljubica Majstorovic 2020/0253
-->
<div class="row text-center">
            <h1><strong>Ažuriraj mejl:</strong></h1>
        </div>
        <div class="row">
            <form action="<?= site_url("$controller/submitMail") ?>" method="post">
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="text" name = "mail" class="form-control" placeholder="Unesite novi mejl..." value="<?= set_value("mail") ?>">
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col text-center">
                        <button type="submit" class="btn btn-dark btn-down text-yellow">Ažuriraj</button>
                    </div>
                    <div class="col errorMessages">
                    <?php if(!empty($errors['mail'])) 
                            echo $errors['mail'];
                        ?>
                    </div>
                </div>
            </form>
        </div>
    </div>
</body>

</html>