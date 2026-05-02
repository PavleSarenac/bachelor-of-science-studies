<?php

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 * Nikola Nikolic 2020/0357
 * Pavle Sarenac 2020/0359
 */

namespace App\Controllers;

use App\Models\RegistrovaniKorisnikModel;
use App\Models\MajstorModel;
use App\Models\TelefonModel;
use App\Models\GradModel;
use App\Models\SpecijalnostiModel;

/**
 * GostController - klasa za implementaciju funkcionalnosti gosta
 * 
 * @version 1.0
 */
class Gost extends BaseController
{
    /**
     * 
     * Show funkcija sluzi kao sablon za prikazivanje stranice, override-uje metodu iz BaseController-a
     * 
     * @param Request string $page Page, array $data Data
     * 
     * @return void
     */
    protected function show($page, $data){
        $data["author"] = $this->session->get("author");
        $data['controller'] = 'Gost';
        $this->session->set("controller", "Gost");
        echo view("pages/navigacija", $data);
        echo view("pages/$page", $data);
    }

    /**
     * Ova funkcija sluzi za prikazivanje stranice na kojoj je moguce logovanje.
     * 
     * @return void
     */
    public function prikazLogovanja() {
        $this->show("logovanje", []);
    }

    /**
     * Za logovanje gosta na sajt
     * 
     * @return Response
     */
    public function loginSubmit(){
        if(!$this->validate([
            "usernameInput" => [
                "rules" => "required",
                "errors" => [
                    "required" => "Unesite korisničko ime."
                ]
            ],
            "passwordInput" => [
                "rules" => "required",
                "errors" => [
                    "required" => "Unesite lozinku."
                ]
            ]
            ])){
            $data["errors"] = $this->validator->getErrors();
            return $this->show("logovanje", $data);
        }
        $registrovaniKorisnikModel = new RegistrovaniKorisnikModel();
        // [0] stoji jer fja vraca niz
        $regKorisnikLista = $registrovaniKorisnikModel->getUser($this->request->getVar("usernameInput"));
        $regKorisnik = null;
        if($regKorisnikLista != [])
            $regKorisnik = $regKorisnikLista[0];
        // var_dump($regKorisnik);
        if($regKorisnik == null)
            return $this->show("logovanje", ["message" => "Korisničko ime ne postoji."]);
        if($regKorisnik->Lozinka != $this->request->getVar("passwordInput"))
            return $this->show("logovanje", ["message" => "Uneta lozinka nije ispravna."]);
        $this->session->set("author", $regKorisnik);

        $userType = "Gost";
        switch ($regKorisnik->TipKorisnika) {
            // administrator
            case "A":
                $userType = "Administrator";
                break;
            // majstor
            case "M":
                $userType = "Majstor";
                $this->session->set("tekuciMajstorPodaci", $registrovaniKorisnikModel->getAllForHandyman($regKorisnik->IdKor));
                break;
            // registrovani korisnik
            case "K":
                $userType = "Korisnik";
                $this->session->set("tekuciKorisnikPodaci", $registrovaniKorisnikModel->getAllForRegUser($regKorisnik->IdKor));
                break;
            // in case of error return to start page
            default:
                $userType = "Gost";    
                break;
            }
            return redirect()->to(site_url($userType."/index")); 
    }
    
    /** 
     * 
     * register funckija sluzi za prikazivanje stranice za registraciju, parametri sluze kako bi se na stranici ispisale greske * * * ukoliko bi lose uneli potrebne podatke
     * 
     * @param string $errors Errors, string $cityError cityError
     * 
     *  @return void
     * 
     * 
    */
    public function register($errors = null, $cityError = null)
    {
        $gradModel = new GradModel();
        $data["gradovi"] = $gradModel->selectAllCities();
        $data["errors"] = $errors;
        $data["cityError"] = $cityError;
        $this->show('registracija', $data);
    }
    
    /**
     * majstorRegistration funkcija sluzi za prikazivanje stranice za odabir specijalnosti pri registraciji majstora, parametar $data
     * predstavlja niz u kojem se pakuju informacije o majstoru koji zeli da se registruje, kako bi se kasnije sacuvao u bazu
     * 
     * @param array $data Data
     * 
     *  @return void
     */
    public function majstorRegistration($data){
        $specModel = new SpecijalnostiModel();
        $data["specijalnosti"] =  $specModel->selectAllSpec();
        $this->show('majstorRegistracija', $data);
    }
    /**
     * majstorRegistrationSubmit funkcija koja sakuplja odabranu specijalnost sa stranice i upisuje sve podatke za majstora u *  * * * bazupodataka
     * 
     *  @return void
     */
    public function majstorRegistrationSubmit(){
        if (!$this->request->isAJAX() && empty($this->request->getServer('HTTP_REFERER'))) {
            return redirect()->to(site_url('Gost/register'));
        }

        $majModel = new MajstorModel();
        $regModel = new RegistrovaniKorisnikModel();
        $telModel = new TelefonModel();
        $specModel = new SpecijalnostiModel();
        $specijalnost = $this->request->getVar("specVal");

        if($specijalnost == "Izaberite specijalnost:"){
            $data["Ime"] = $this->request->getVar("Ime");
            $data["Prezime"] = $this->request->getVar("Prezime");
            $data["KorisnickoIme"] = $this->request->getVar("KorisnickoIme");
            $data["Lozinka"] = $this->request->getVar("Lozinka");
            $data["MejlAdresa"] = $this->request->getVar("MejlAdresa");
            $data["Telefon"] = $this->request->getVar("Telefon");
            $data["IdGra"] = $this->request->getVar("IdGra");
            $data['poruka'] = "Izaberite svoju specijalnost!";
            return $this->majstorRegistration($data);
        }
        $regModel->save_user([
            "Ime"=>$this->request->getVar("Ime"),
            "Prezime"=>$this->request->getVar("Prezime"),
            "KorisnickoIme"=>$this->request->getVar("KorisnickoIme"),
            "Lozinka"=>$this->request->getVar("Lozinka"),
            "TipKorisnika"=>"M",
            "MejlAdresa"=>$this->request->getVar("MejlAdresa"),
            "IdGra"=>$this->request->getVar("IdGra")
        ]);
        $telModel->save_user(
            [
                "IdKor"=>$regModel->getInsertID(),
                "Telefon"=>$this->request->getVar("Telefon")
            ]
        );
        $majModel->save_user([
            "IdMaj"=>$regModel->getInsertID(),
            "IdSpec" => $specModel->selectId($specijalnost)
        ]);
        
        return redirect()->to(site_url("Gost/prikazLogovanja"));
    }

    /**
     * submitRegistration funkcija je funkcija koja se kupi podatke korisnika i u koliko je se registuje klijent automatski ih * *  * upisuje u bazu, a u koliko je majstor podatke prosledjuje funkciji majstorRegistration($data)
     * 
     *  @return void
     */

    public function submitRegistration() 
    {
        $regModel = new RegistrovaniKorisnikModel();
        $telModel = new TelefonModel();
        $gradModel = new GradModel();

        if (!$this->validate (
            [
                "ime" => [
                    "rules" => "required",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vaše ime."
                    ]
                ],
                "prezime" => [
                    "rules" => "required",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vaše prezime."
                    ]
                ],
                "username" => [
                    "rules" => "required|max_length[20]|is_unique[registrovani_korisnik.KorisnickoIme]|regex_match[/^[a-zA-Z0-9._]+$/]",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vaše korisničko ime.",
                        "max_length" => "Korisničko ime ne sme biti duže od 20 karaktera.",
                        "is_unique" => "Korisničko ime je već zauzeto.",
                        "regex_match" => "Korisničko ime sme da sadrži samo slova, brojeve, '.' i '_'."
                    ]
                ],
                "pass" => [
                    "rules" => "required|min_length[7]",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vašu lozinku.",
                        "min_length" => "Šifra mora biti dugačka bar 7 karaktera."
                    ]
                ],
                "password" => [
                    "rules" => "required|min_length[7]|matches[pass]",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vašu lozinku.",
                        "min_length" => "Šifra mora biti dugačka bar 7 karaktera.",
                        "matches" => "Unete šifre se ne podudaraju."
                    ]
                ],
                "phone" => [
                    "rules" => "required|regex_match[/^\+381-6\d-\d{3}-\d{3,4}$/]|is_unique[telefon.Telefon]",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vaš broj telefona.",
                        "regex_match" => "Telefon mora biti u formatu +381-6x-xxx-xxx(x).",
                        "is_unique" => "Uneti telefon već postoji u bazi."
                    ]
                ],
                "mail" => [
                    "rules" => "required|valid_email|is_unique[registrovani_korisnik.MejlAdresa]|is_unique[zabranjeni_mejlovi.MejlAdresa]",
                    "errors" => [
                        "required" => "Obavezno je da unesete Vaš broj telefona.",
                        "valid_email" => "Uneta mejl adresa nije u dobrom formatu.",
                        "is_unique" => "Uneti mejl je već u bazi."
                    ]
                ]
            ]
        ) || $this->request->getVar("cities")=="Izaberite grad:") {
            $cityError = null;
            if($this->request->getVar("cities")=="Izaberite grad:") {
                $cityError = "Niste izabrali grad.";
            }
            return $this->register($this->validator->getErrors(), $cityError);
        }

        $gradId = $gradModel->searchId($this->request->getVar("cities"));
        if($this->request->getVar("userType")=="majstor"){
            $send["Ime"] = $this->request->getVar("ime");
            $send["Prezime"] = $this->request->getVar("prezime");
            $send["KorisnickoIme"] = $this->request->getVar("username");
            $send["Lozinka"] = $this->request->getVar("pass");
            $send["MejlAdresa"] = $this->request->getVar("mail");
            $send["IdGra"] = $gradId;
            $send["Telefon"] = $this->request->getVar("phone");
            return $this->majstorRegistration($send);
        }
        else{
            
            $regModel->save_user( [
                "Ime"=>$this->request->getVar("ime"),
                "Prezime"=>$this->request->getVar("prezime"),
                "KorisnickoIme"=>$this->request->getVar("username"),
                "Lozinka"=>$this->request->getVar("pass"),
                "TipKorisnika"=>"K",
                "MejlAdresa"=>$this->request->getVar("mail"),
                "IdGra"=>$gradId
            

            ]);
            $telModel->save_user(
                [
                    "IdKor"=>$regModel->getInsertID(),
                    "Telefon"=>$this->request->getVar("phone")
                ]
            );
            return redirect()->to(site_url("Gost/prikazLogovanja"));
        }
    }

}
