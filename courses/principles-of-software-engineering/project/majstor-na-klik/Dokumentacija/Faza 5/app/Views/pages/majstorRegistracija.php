<!--
Autori: 
Ljubica Majstorovic 2020/0253
-->
        <div class="row text-center">
            <h1><strong>Odaberi svoje specijalnosti:</strong></h1>
        </div>
        <div class="row error text-center">
            <h3><?php if(isset($poruka)) echo "<span class='errorMessages'>$poruka</span><br>"; ?></h3>
        </div>
        <div class="row">
            <form name="spec" method="post" action="<?= site_url("Gost/majstorRegistrationSubmit") ?>">
                <div class="row mx-auto">
                    <div class="col"></div>
                    <div class="col">
                        <select class="form-select form-select-lg" name="specVal">
                            <option>Izaberite specijalnost:</option>
                            <?php foreach ($specijalnosti as $specijalnost) {
                                $selected = ($specijalnost->Opis === set_value("specVal")) ? "selected" : "";
                                echo "<option $selected>$specijalnost->Opis</option>";
                            }
                            ?>
                        </select>
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row">
                    <div class="col"></div>
                    <div class="col">
                        <?php echo "<input type='hidden' name='Ime' value={$Ime}>";
                        echo "<input type='hidden' name='Prezime' value={$Prezime}>";
                        echo "<input type='hidden' name='KorisnickoIme' value={$KorisnickoIme}>";
                        echo "<input type='hidden' name='Lozinka' value={$Lozinka}>";
                        echo "<input type='hidden' name='MejlAdresa' value={$MejlAdresa}>";
                        echo "<input type='hidden' name='IdGra' value={$IdGra}>";
                        echo "<input type='hidden' name='Telefon' value={$Telefon}>";
                        ?>
                    </div>
                    <div class="col"></div>
                </div>
                <div class="row mx-auto">
                    <div class="col"></div>
                    <div class="col mx-auto">
                        <button type="submit" class="btn btn-down btn-dark text-yellow">Potvrdi</button>
                    </div>
                    <div class="col"></div>
                </div>
            </form>  
        </div>  
</body>

</html>