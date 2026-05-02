<!--
Autori:
Pavle Sarenac 2020/0359
-->       
        <div class="row text-center">
            <div class="col">
                <h1><strong>Ocenite majstora: </strong><?php echo $name . " " . $surname . " (" . $specialty . ")"?></h1>
            </div>
        </div>
        <hr>
        <form id="reviewForm" action="<?php echo site_url("Korisnik/saveReviewToDatabase/$handymanId") ?>" method="POST">
            <?php
                if (isset($alreadyReviewed)) {
                    echo "
                        <div class='row'>
                            <div class='col-sm-1'></div>
                            <div class='col-sm-10'>
                                <div class='alert alert-danger text-center' role='alert'>
                                    <h3>Već ste ocenili ovog majstora!</h3>
                                </div>
                            </div>
                            <div class='col-sm-1'></div>
                        </div>";
                } 
            ?>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center">
                    <h3>Cena:</h3>
                </div>
                <div class="col"></div>
            </div>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center"> 
                    <input name="priceRating" value="1" class="star" id="price-star-1" type="checkbox"/>
                    <label id="aaa" class="star" for="price-star-1"></label>
                    <input name="priceRating" value="2"  class="star" id="price-star-2" type="checkbox"/>
                    <label class="star" id="price-star-2" for="price-star-2"></label>
                    <input name="priceRating" value="3"  class="star" id="price-star-3" type="checkbox"/>
                    <label class="star" for="price-star-3"></label>
                    <input name="priceRating" value="4"  class="star" id="price-star-4" type="checkbox"/>
                    <label class="star" for="price-star-4"></label>
                    <input name="priceRating" value="5"  class="star" id="price-star-5" type="checkbox"/>
                    <label class="star" for="price-star-5"></label>
                </div>
                <div class="col"></div>
            </div>
            <hr>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center">
                    <h3>Brzina:</h3>
                </div>
                <div class="col"></div>
            </div>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center"> 
                    <input name="speedRating" value="1" class="star" id="speed-star-1" type="checkbox"/>
                    <label class="star" for="speed-star-1"></label>
                    <input name="speedRating" value="2" class="star" id="speed-star-2" type="checkbox"/>
                    <label class="star" id="speed-star-2" for="speed-star-2"></label>
                    <input name="speedRating" value="3" class="star" id="speed-star-3" type="checkbox"/>
                    <label class="star" for="speed-star-3"></label>
                    <input name="speedRating" value="4" class="star" id="speed-star-4" type="checkbox"/>
                    <label class="star" for="speed-star-4"></label>
                    <input name="speedRating" value="5" class="star" id="speed-star-5" type="checkbox"/>
                    <label class="star" for="speed-star-5"></label>
                </div>
                <div class="col"></div>
            </div>
            <hr>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center">
                    <h3>Kvalitet:</h3>
                </div>
                <div class="col"></div>
            </div>
            <div class="row">
                <div class="col"></div>
                <div class="col text-center"> 
                    <input name="qualityRating" value="1" class="star" id="quality-star-1" type="checkbox"/>
                    <label class="star" for="quality-star-1"></label>
                    <input name="qualityRating" value="2" class="star" id="quality-star-2" type="checkbox"/>
                    <label class="star" id="quality-star-2" for="quality-star-2"></label>
                    <input name="qualityRating" value="3" class="star" id="quality-star-3" type="checkbox"/>
                    <label class="star" for="quality-star-3"></label>
                    <input name="qualityRating" value="4" class="star" id="quality-star-4" type="checkbox"/>
                    <label class="star" for="quality-star-4"></label>
                    <input name="qualityRating" value="5" class="star" id="quality-star-5" type="checkbox"/>
                    <label class="star" for="quality-star-5"></label>
                </div>
                <div class="col"></div>
            </div>
            <hr>
            <div class="row text-center">
                <h3>Opišite ukratko Vaše iskustvo sa ovim majstorom:</h3>
            </div>
            <div class="row">
                <div class="col-sm-2"></div>
                <div class="col-sm-8">
                    <div class="form-group">
                        <textarea name="tekstRecenzije" class="form-control rounded-0" rows="6" maxlength="1000"></textarea>
                    </div>
                </div>
                <div class="col-sm-2"></div>
            </div>
            <div class="row">
                <div class="col"></div>
                <div class="col">
                    <button id="submit-review-btn" type="submit" class="btn btn-down btn-dark text-yellow">Ostavi svoj utisak</button>
                </div>
                <div class="col"></div>
            </div>
        </form>
    </div>
</body>