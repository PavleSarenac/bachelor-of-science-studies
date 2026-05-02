<?php

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 */

namespace App\Controllers;

use App\Models\MajstorModel;
use App\Models\PrijavaModel;
use App\Models\RegistrovaniKorisnikModel;
use App\Models\TelefonModel;
use App\Models\ZabranjeniMejloviModel;

/**
 * Ova klasa predstavlja administratore.
 * 
 * @version 1.0
 */
class Administrator extends RegistrovaniKorisnik {

    /**
     * Ova funkcija sluzi za prikaz prosledjene stranice pri cemu joj se prosledjuju i odgovarajuci parametri.
     * 
     * @param string $page Page
     * @param array $data Data
     * 
     * @return void
     */
    protected function show($page, $data){
        $data["author"] = $this->session->get("author");
        $data['controller'] = 'Administrator';
        $this->session->set("controller", "Administrator");
        echo view("pages/navigacija", $data);
        echo view("pages/$page", $data);
    }

    /**
     * Ova funkcija sluzi za ucitavenje stranice za prikazivanje svih pristiglih prijava.
     * 
     * @return void
     */

    public function showAllReports() {
        $_SESSION["func"] = "reports";

        $prijavaModel = new PrijavaModel();
        $searchResult = $prijavaModel->search();
        $this->session->set("allReportsResult", $searchResult);
        $this->session->set("allReportsRowNum", 10);

        return $this->show("izlistaj_prijave", []);
    }

    /**
     * Ova funkcija sluzi sa prikazivanje svih prijava za jednog odredjenog majstora.
     * 
     * @return void
     */

    public function majstorovePrijave($id){
        $_SESSION["func"] = "handymanReports";

        $prijavaModel = new PrijavaModel();
        $searchResult = $prijavaModel->searchHandyman($id);
        $this->session->set("handymanReportsResult", $searchResult);
        $this->session->set("handymanReportsRowNum", 10);

        return $this->show("izlistaj_prijave_jednog_majstora", []);
    }

    /**
     * Ova funkcija se poziva uz pomoc ajax tehnologije i koristi se kada admin 
     * skroluje stranicu sa prijavama i izlista sve ucitane prijave da ucita novi set 
     * prijava u browser ukoliko ih naravno ima
     * 
     * @return void
     */

    public function fetchNextResultsReports(){
        $controller = $this->session->get("controller");
        $rowNumber = (int) $this->session->get("allReportsRowNum");
        $remainingRows = count($this->session->get("allReportsResult")) - $rowNumber;

        $currentBlock = array_slice($this->session->get("allReportsResult"), $rowNumber, 
        $remainingRows >= 10 ? 10 : null);
        $newResultBlock = "";
        foreach ($currentBlock as $result) {
            $prijavaModel= new PrijavaModel();
            $resultKlijent = $prijavaModel->searchReporter($result->IdKli);
            $resultKlijent = $resultKlijent[0];

            $encodedKName = rawurlencode($resultKlijent->Ime);
            $encodedKSurname = rawurlencode($resultKlijent->Prezime);
            $encodedKCity = rawurlencode($resultKlijent->Naziv);
            $encodedKPhone = urlencode($resultKlijent->Telefon);
            $encodedKMail = rawurlencode($resultKlijent->MejlAdresa);
            $idK = rawurlencode($resultKlijent->IdKor);
            
            $encodedName = rawurlencode($result->Ime);
            $encodedSurname = rawurlencode($result->Prezime);
            $encodedSpecialty = rawurlencode($result->Opis);
            $encodedCity = rawurlencode($result->Naziv);
            $encodedPhone = urlencode($result->Telefon);
            $encodedMail = rawurlencode($result->MejlAdresa);
            $id = rawurlencode($result->IdKor);
            
            echo "<div class='alert alert-light' role='alert'><div class='row' >" .
            "<div class='row'><div class='col text-center'>" . anchor(
                "$controller/prikazProfilaMajstora/$encodedName/$encodedSurname/$encodedSpecialty/$encodedCity/$encodedPhone/$encodedMail/$id",
                $result->Ime. " " . $result->Prezime,
                array('class' => 'majstorLink')
            ) . "<br>" . $result->DatumVreme ."</div><div class='col text-center'>". anchor(
                "$controller/prikazProfilaKorisnika/$encodedKName/$encodedKSurname/$encodedKCity/$encodedKPhone/$encodedKMail/$idK",
                $resultKlijent->Ime. " " . $resultKlijent->Prezime,
                array('class' => 'majstorLink')
            ) . "</div><div class = 'col text-center text-break'>".$result->Tekst."</div></div>" .
            "</div></div>";
        }
        $this->session->set("allReportsRowNum", $rowNumber + 10);
        echo $newResultBlock;
    }

   /**
     * Ova funkcija se poziva uz pomoc ajax tehnologije i koristi se kada admin 
     * skroluje stranicu sa prijavama jednog majstora i izlista sve ucitane prijave da ucita novi set 
     * prijava u browser ukoliko ih naravno ima
     * 
     * @return void
     */

    public function fetchNextResultsHandymanReports(){
        $controller = $this->session->get("controller");
        $rowNumber = (int)$this->session->get("handymanReportsRowNum");
        $remainingRows = count($this->session->get("handymanReportsResult")) - $rowNumber;

        $currentBlock = array_slice($this->session->get("handymanReportsResult"), $rowNumber, 
        $remainingRows >= 10 ? 10 : null);
        $newResultBlock = "";
        foreach ($currentBlock as $result) {
            $prijavaModel= new PrijavaModel();
            $resultKlijent = $prijavaModel->searchReporter($result->IdKli);
            $resultKlijent = $resultKlijent[0];

            $encodedKName = rawurlencode($resultKlijent->Ime);
            $encodedKSurname = rawurlencode($resultKlijent->Prezime);
            $encodedKCity = rawurlencode($resultKlijent->Naziv);
            $encodedKPhone = urlencode($resultKlijent->Telefon);
            $encodedKMail = rawurlencode($resultKlijent->MejlAdresa);
            $idK = rawurlencode($resultKlijent->IdKor);
            
            
            echo "<div class='alert alert-light' role='alert'><div class='row' >" .
                    "<div class='row'><div class='col text-center'>" . $result->DatumVreme ."</div><div class='col text-center'>". anchor(
                        "$controller/prikazProfilaKorisnika/$encodedKName/$encodedKSurname/$encodedKCity/$encodedKPhone/$encodedKMail/$idK",
                        $resultKlijent->Ime. " " . $resultKlijent->Prezime,
                        array('class' => 'majstorLink')
                    ) . "</div><div class = 'col text-center text-break'>".$result->Tekst."</div></div>" .
                    "</div></div>"; 

        }
        $this->session->set("handymanReportsRowNum", $rowNumber + 10);
        echo $newResultBlock;
    }

    /**
     * Ova funkcija sluzi za brisanje majstora iz baze sa prosledjenim id-ijem
     * 
     *@param integer $id Id
     * 
     * 
     * @return void
     */

    public function izbaciMajstora($id){
        $majstorModel = new MajstorModel();
        $regModel = new RegistrovaniKorisnikModel();
        $telefonModel = new TelefonModel();
        $zabranjeniMejloviModel = new ZabranjeniMejloviModel();

        $user = $regModel->where("IdKor", $id)->findAll();
        $userMail = $user[0]->MejlAdresa;
        $session = session();
        $author = $session->get("author");
        $idAdm = $author->IdKor;
        $zabranjeniMejloviModel->insert([
            "MejlAdresa" => $userMail,
            "IdAdm" => $idAdm
        ], true);

        $telefonModel->where("IdKor", $id)
        ->delete();
        $majstorModel->where("IdMaj", $id)
        ->delete();
        $regModel->where("IdKor", $id)
        ->delete();

        return $this->index();

    }

    

}

?>