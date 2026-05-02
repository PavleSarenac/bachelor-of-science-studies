<?php

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 * Nikola Nikolic 2020/0357
 */

namespace App\Controllers;

use App\Models\PorukaModel;
use App\Models\PrijavaModel;
use App\Models\RegistrovaniKorisnikModel;
use App\Models\GradModel;
use App\Models\SlikaModel;
use CodeIgniter\I18n\Time;

/**
 * Ova klasa sluzi kao osnovna za sve registrovane korisnike.
 * 
 * @version 1.0
 */
abstract class RegistrovaniKorisnik extends BaseController {

    /**
     * Odjavljivanje ulogovane uloge
     * 
     * @return Response
     */
    public function logout(){
        $this->session->destroy();
        return redirect()->to(site_url("/")); // idi na podrazumevanu pocetnu stranu
    }

    /**
     * vraca niz poruka koje su namenjene za autora ciji se id cita iz niza POST
     * 
     * @return JSON file
     */
    public function checkMessagesForAuthor(){
        $authorId = isset($_POST['authorMessages']) ? $_POST['authorMessages'] : null;
        $porukaModel = new PorukaModel();
        $poruke = $porukaModel->getAllReceivedMessages($authorId);
        echo json_encode($poruke);
    }

    /**
     * Funkcija koja sluzi za ucitavanje stranice na kojoj korisnik moze poaslati svoju prijavu za majstora
     * 
     * @return void
     */

     public function prijavi($id, $errors=null){

        $this->show("prijavi", ["id" => $id, "errors"=>$errors]);
    }

    /**
     * U ovoj funkciji se prijava kupi sa stranice i cuva u bazi podataka
     * 
     * @return void
     */

     public function prijaviSubmit(){
        if($this->request->getVar("prijava") == ""){
            return $this->prijavi($this->request->getVar("id"), "Morate napisati razlog prijave pre nego sto je pošaljete");
        }

        $session = session();

        $klijent = $session->get("author");
        $klijentId = $klijent->IdKor;
        $prijavljeni = $this->request->getVar("id");
        $tekst = $this->request->getVar("prijava");
        $current_time = Time::now('Europe/Belgrade', 'en_US');
        $prijavaModel = new PrijavaModel();

        $prijavaModel->save_report([
            "IdKli"=>$klijentId,
            "IdPrijavljenog"=>$prijavljeni,
            "Tekst"=>$tekst,
            "DatumVreme"=>$current_time
        ]);
        
        $this->show("index", []);
    }

    /**
     * Ova funkcija sluzi za ucitavanje starnice za azuriranje profila ulogovanog korisnika
     * 
     * 
     * @return void
     */

     public function azurirajProfil(){
        $author = $this->session->get("author");
        $this->show("azurirajProfil", ["id" => $author->IdKor]);
    }

    /**
     * Ova funkcija sluzi za ucitavanje stranice za azuriranje lozinke ulogovanog korisnika
     * 
     * @return void
     */

    public function azurirajLozinku($errors = null, $poruka = null){
        $data["errors"] = $errors;
        $data["poruka"] = $poruka;
        $this->show("azurirajLozinku", $data);
    }

    /**
     * Ova funkcija sluzi za ucitavanje stranice za azuriranje telefona ulogovanog korisnika
     * 
     * @return void
     */

    public function azurirajTelefon($errors = null){
        $this->show("azurirajTelefon", ["errors" => $errors]);
    }

    /**
     * Ova funkcija sluzi za ucitavanje stranice za azuriranje mejla ulogovanog korisnika
     * 
     * @return void
     */


    public function azurirajMejl($errors = null){
        $author = $this->session->get("author");
        $this->show("azurirajMejl", ["errors" => $errors]);
    }


    /**
     * Ova funkcija sluzi za ucitavanje stranice za azuriranje grada ulogovanog korisnika
     * 
     * @return void
     */


    public function azurirajLokaciju(){
        $gradModel = new GradModel();
        $data["gradovi"] = $gradModel->selectAllCities();
        $this->show("azurirajLokaciju", $data);
    }

    /**
     * Ova funkcija sluzi za ucitavanje stranice za azuriranje slike ulogovanog korisnika
     * 
     * @return void
     */


    public function azurirajSliku($data = null) {
        $this->show("azurirajProfilnu", $data);
    }

    /**
     * 
     * Ova funkcija sluzi za prikupljanje podataka potrebnih za cuvanje nove lozinke i njihove provere,
     * ukoliko sve provere prodju, nova lozinka se pamti u bazi podataka
     * 
     * @return void
     */

    public function submitPass(){
        $author = $_SESSION["author"];
        $id = $author->IdKor;
        
        $regModel = new RegistrovaniKorisnikModel();
        $truePass = $regModel->where("IdKor", $id)->findAll();
        $truePass = $truePass[0]->Lozinka;
        
        
        
        if (!$this->validate (
            [
                
                "pass2" => [
                    "rules" => "required|min_length[7]",
                    "errors" => [
                        "required" => "Morate da uneste novu lozinku",
                        "min_length" => "Šifra mora biti dugačka bar 7 karaktera."
                    ]
                ],
                "pass3" => [
                    "rules" => "required|matches[pass2]",
                    "errors" => [
                        "required" => "Morate da potvrdite lozinku",
                        "matches" => "Unete šifre se ne podudaraju."
                    ]
                ]
            ]
        ) || ($truePass != $this->request->getVar("pass"))) {
            $poruka = null;
            if($truePass != $this->request->getVar("pass")) {
                $poruka = "Stara lozinka nije ispravna";
            }
            return $this->azurirajLozinku($this->validator->getErrors(), $poruka);
        }

        $regModel->updatePassword($id,$this->request->getVar("pass2"));
        return $this->azurirajProfil();
    }

    /**
     * 
     * Ova funkcija sluzi za prikupljanje podatka o novom broju telefona,
     * ukoliko provera prodje, novi telefon se pamti u bazi podataka
     * 
     * @return void
     */

    public function submitPhone(){
        $author = $this->session->get("author");
        $id = $author->IdKor;
        if (!$this->validate (
            [
                "phone" => [
                    "rules" => "required|regex_match[/^\+381-6\d-\d{3}-\d{3,4}$/]",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vaš broj telefona.",
                        "regex_match" => "Telefon mora biti u formatu +381-6x-xxx-xxx(x)."
                    ]
                ]
            ]
        ) ) {
            return $this->azurirajTelefon($this->validator->getErrors());
        }

        $db = \Config\Database::connect();
        $builder = $db->table("Telefon");
        $builder->set(["Telefon" => $this->request->getVar("phone")]);
        $builder->where("IdKor", $id);
        $builder->update();
        return $this->azurirajProfil();
    }

    
    /**
     * 
     * Ova funkcija sluzi za prikupljanje podatka o novom mejlu,
     * ukoliko provera prodje, novi telefon se pamti u bazi podataka
     * 
     * @return void
     * 
     */

    public function submitMail(){
        $author = $this->session->get("author");
        $id = $author->IdKor;
        $regModel = new RegistrovaniKorisnikModel();
        if (!$this->validate (
            [   "mail" => [
                    "rules" => "required|valid_email|is_unique[registrovani_korisnik.MejlAdresa]",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vaš broj telefona.",
                        "valid_email" => "Uneta mejl adresa nije u dobrom formatu.",
                        "is_unique" => "Već postoji nalog sa unetom mejl adresom."
                    ]
                ]
            ]
        )) {
            
            return $this->azurirajMejl($this->validator->getErrors());
        }
        $regModel->updateMail($id, $this->request->getVar("mail"));
        return $this->azurirajProfil();
    }


    
    /**
     * 
     * Ova funkcija sluzi za prikupljanje podatka o novom gradu,
     * ukoliko provera prodje, novi grad se pamti u bazi podataka
     * 
     * @return void
     * 
     */


    public function submitCity(){
        $author = $this->session->get("author");
        $id = $author->IdKor;
        $regModel = new RegistrovaniKorisnikModel();
        $regModel->updateCity($id, $this->request->getVar("cities"));
        return $this->azurirajProfil();
    }


    
    /**
     * 
     * Ova funkcija sluzi za prikupljanje podatka o novoj slici,
     * ukoliko provera prodje, nova slika se pamti u bazi podataka
     * 
     * @return void
     * 
     */


    public function submitPhoto(){
        $author = $this->session->get("author");
        $id = $author->IdKor;

        $file = $this->request->getFile('photo');

        if ($file->isValid() && $file->getClientMimeType() == 'image/jpeg') {
            $newName = $file->getRandomName();
            try {
                $file->move(FCPATH . '/uploads', $newName);
            } catch (\Exception $e) {
                echo 'Error: ' . $e->getMessage();
            }
            $model = new SlikaModel();
            $model->insert(['Path' => 'uploads/' . $newName], true);

            // Redirect or display a success message
        } else {
            $errors["photo"] = "Slika mora biti u jpeg formatu";
            $data["errors"] = $errors;
            return $this->azurirajSliku($data);
        }
        $regModel = new RegistrovaniKorisnikModel();
        $regModel->updatePhoto($id, $model->getInsertID());
        return $this->azurirajProfil();
    }

}

?>