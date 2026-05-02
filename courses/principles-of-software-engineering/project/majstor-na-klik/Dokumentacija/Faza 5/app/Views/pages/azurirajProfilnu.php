<!--
Autori: Ljubica Majstorovic 2020/0253
-->
<div class="row text-center">
            <h1><strong>Ažuriraj profilnu sliku:</strong></h1>
        </div>
        <div class="row">
            <form method="post" action="<?= site_url("$controller/submitPhoto") ?>" enctype="multipart/form-data">
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <input type="file" name = "photo" class="form-control">
                    </div>
                    <div class="col errorMessages">
                    <?php if(!empty($errors['photo'])) 
                            echo $errors['photo'];
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