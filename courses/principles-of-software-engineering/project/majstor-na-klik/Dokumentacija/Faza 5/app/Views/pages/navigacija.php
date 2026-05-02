<!--
Autori: 
Ljubica Majstorovic 2020/0253
Nikola Nikolic 2020/0357
Pavle Sarenac 2020/0359
-->
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/x-icon" href="/images/favicon.ico">
    <!-- bootstrap and jquery -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js"></script>
    <script src="/js/jquery-3.7.0.min.js"></script>
    <!-- style -->
    <link rel="stylesheet" href="/css/index.css">
    <link rel="stylesheet" href="/css/ChatDesign.css">
    <link rel="stylesheet" href="/css/porukeDesign.css">
    <!-- script -->
    <script src="/js/script.js"></script>
    <script src="/js/cetovanje.js"></script>
    <script src="/js/scriptMajstorInfo.js"></script>
    <script src="/js/receiver.js"></script>
    <script src="/js/scriptPoruke.js"></script>
    <title>MajstorNaKlik</title>
</head>

<body>
    <div class="container-fluid">
        <div class="row">
            <nav class="navbar navbar-expand-lg navbar-dark bg-dark static-top text-center">
                <a class="navbar-brand" href="<?php echo site_url("/") ?>">
                    <img src="/images/logo.jpg" alt="logo" height="100">
                </a>
                <a class="heading-link" href="<?php echo site_url("/") ?>">
                    <h1>MajstorNaKlik</h1>
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                    aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link active" aria-current="page" href="<?php echo site_url("Chat/showMessages") ?>">
                                <?php 
                                    $session = session();
                                    if ($session->has("author")){
                                        echo "<span class='text-yellow'>Poruke</span>
                                        <span class='badgeMy'>0</span>";
                                    }
                                ?>
                                
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link active" aria-current="page" href="<?php echo site_url("/") ?>"><span
                                    class="text-yellow">Početna</span></a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                                data-bs-toggle="dropdown" aria-expanded="false">
                                <span class="text-yellow">Meni</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                            <?php
                                if ($session->has("author")) {
                                    $author = $session->get("author");
                                    $authorType = $author->TipKorisnika;
                                    $userType = "Gost";
                                    switch ($authorType) {
                                        // Administrator
                                        case "A":
                                            $userType = "Administrator";
                                            echo "<li>" . anchor("Administrator/showAllReports", "Vidi sve prijave", array('class' => 'dropdown-item')) . "</li>" .
                                            "<li>" . anchor("Administrator/logout", "Izloguj se", array('class' => 'dropdown-item')) . "</li>";
                                            break;
                                        // Majstor
                                        case "M":
                                            $userType = "Majstor";
                                            $name = rawurlencode($session->get("tekuciMajstorPodaci")[0]->Ime);
                                            $surname = rawurlencode($session->get("tekuciMajstorPodaci")[0]->Prezime);
                                            $specialty = rawurlencode($session->get("tekuciMajstorPodaci")[0]->Opis);
                                            $city = rawurlencode($session->get("tekuciMajstorPodaci")[0]->Naziv);
                                            $phone = rawurlencode($session->get("tekuciMajstorPodaci")[0]->Telefon);
                                            $email = rawurlencode($session->get("tekuciMajstorPodaci")[0]->MejlAdresa);
                                            $id = rawurlencode($session->get("tekuciMajstorPodaci")[0]->IdKor);
                                            echo "<li>" . anchor(
                                                "Majstor/prikazProfilaMajstora/$name/$surname/$specialty/$city/$phone/$email/$id", 
                                                "Moj profil", 
                                                array('class' => 'dropdown-item')) . "</li>" .
                                            "<li>" . anchor("Majstor/logout", "Izloguj se", array('class' => 'dropdown-item')) . "</li>";
                                            break;
                                        // Registrovani korisnik
                                        case "K":
                                            $userType = "Korisnik";
                                            $name = rawurlencode($session->get("tekuciKorisnikPodaci")[0]->Ime);
                                            $surname = rawurlencode($session->get("tekuciKorisnikPodaci")[0]->Prezime);
                                            $city = rawurlencode($session->get("tekuciKorisnikPodaci")[0]->Naziv);
                                            $phone = rawurlencode($session->get("tekuciKorisnikPodaci")[0]->Telefon);
                                            $email = rawurlencode($session->get("tekuciKorisnikPodaci")[0]->MejlAdresa);
                                            $id = rawurlencode($session->get("tekuciKorisnikPodaci")[0]->IdKor);
                                            echo "<li>" . anchor(
                                                "Korisnik/prikazProfilaKorisnika/$name/$surname/$city/$phone/$email/$id", 
                                                "Moj profil", 
                                                array('class' => 'dropdown-item')) . "</li>" .
                                            "<li>" . anchor("Korisnik/logout", "Izloguj se", array('class' => 'dropdown-item')) . "</li>";
                                            break;
                                        // in case of error return to start page
                                        default:
                                            $userType = "Gost";    
                                            break;
                                        }
                                    // Gost
                                    } else {
                                        echo "<li>" . anchor("Gost/register", "Registruj se", array('class' => 'dropdown-item')) . "</li>".
                                             "<li>". anchor("Gost/prikazLogovanja", "Uloguj se", array('class' => 'dropdown-item')) ."</li>";
                                    }
                                ?>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>
        </div>