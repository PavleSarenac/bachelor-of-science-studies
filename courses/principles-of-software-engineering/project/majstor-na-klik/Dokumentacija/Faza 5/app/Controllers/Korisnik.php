<?php

/**
 * Autori:
 * Pavle Sarenac 2020/0359
 */

namespace App\Controllers;

use App\Models\MajstorModel;
use App\Models\RecenzijaModel;
use CodeIgniter\I18n\Time;

/**
 * Ova klasa predstavlja korisnike.
 * 
 * @version 1.0
 */
class Korisnik extends RegistrovaniKorisnik {

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
        $data['controller'] = 'Korisnik';
        $this->session->set("controller", "Korisnik");
        echo view("pages/navigacija", $data);
        echo view("pages/$page", $data);
    }

    public function showPageForRatingHandyman($id, $name, $surname, $specialty, $error = null) {
        return $this->show("oceniMajstora", [
            "handymanId" => $id,
            "name" => $name,
            "surname" => $surname,
            "specialty" => $specialty,
            "alreadyReviewed" => $error
        ]);

    }

    public function saveReviewToDatabase($handymanId) {
        $recenzijaModel = new RecenzijaModel();
        $majstorModel = new MajstorModel();

        $priceRating = $this->request->getVar("priceRating");
        $speedRating = $this->request->getVar("speedRating");
        $qualityRating = $this->request->getVar("qualityRating");
        $textRating = $this->request->getVar("tekstRecenzije");

        $name = rawurlencode($majstorModel->getName($handymanId)[0]->Ime);
        $surname = rawurlencode($majstorModel->getSurname($handymanId)[0]->Prezime);
        $specialty = rawurlencode($majstorModel->getSpecialty($handymanId)[0]->Opis);

        $userId = $this->session->get("author")->IdKor;

        if ($recenzijaModel->isAlreadyReviewed($userId, $handymanId) == true) {
            return redirect()->to(site_url("Korisnik/showPageForRatingHandyman/$handymanId/$name/$surname/$specialty/1"));
        }

        $recenzijaData = [
            "IdKli" => $userId,
            "IdMaj" => $handymanId,
            "Tekst" => $textRating,
            "DatumVreme" => Time::now("Europe/Belgrade", "en_US")
        ];
        $recenzijaModel->saveReview($recenzijaData);

        $majstorModel->updateRatings($handymanId, $priceRating, $speedRating, $qualityRating);

        return redirect()->to(site_url());
    } 

}

?>