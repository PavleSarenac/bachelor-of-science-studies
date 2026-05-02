<!--
Autori: Ljubica Majstorovic 2020/0253
-->
<div class="row text-center">
            <h1><strong>Ažuriraj lokaciju:</strong></h1>
        </div>
        <div class="row">
            <form action="<?= site_url("$controller/submitCity") ?>" method="post">
                <div class="row">
                <div class="col"></div>
                    <div class="col">
                        <select class="form-select form-select-lg" name="cities">
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
                        <button type="submit" class="btn btn-down btn-dark text-yellow">Ažuriraj</button>
                    </div>
                    <div class="col"></div>
                </div>
            </form>
        </div>
    </div>
</body>

</html>