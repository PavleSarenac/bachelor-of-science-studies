<!--
Autori:
Ljubica Majstorovic 2020/0253
-->
<div class="row text-center">
    <div class="row">
        <div class="col">

        </div>
        <div class="col">
        <h1><strong>Opisite razlog vase prijave: </strong></h1>
        </div>
        <div class="col">

        </div>
    </div>
    <form action="<?= site_url("$controller/prijaviSubmit/$id") ?>" method="post">
        <div class = "row text-center">
            <div class="col"></div>
            <div class="col text-center">
                <textarea class="form-control" name="prijava" rows="4" maxlength="100"></textarea>   
            </div>
            <div class="col"></div>
        </div>        
        <div class="row">
            <div class="col"></div>
            <div class="col text-center">
                <button type="submit" class="btn btn-dark btn-down text-yellow">Posalji prijavu nasem administratoru</button>
            </div>
            <div class="col"></div>
        </div>
        <div class="row">
            <div class="col"></div>
            <div class="col">
                <div class="errorMessages"><?php if(!empty($errors)) echo $errors;?>
            </div>
            <div class="col"></div>
        </div>
        <?php echo "<input type='hidden' name='id' value={$id}>";?>
    </form>
</body>

</html>