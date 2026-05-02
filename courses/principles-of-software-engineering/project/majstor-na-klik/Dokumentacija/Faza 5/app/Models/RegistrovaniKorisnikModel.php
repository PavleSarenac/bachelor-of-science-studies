<?php 

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 * Nikola Nikolic 2020/0357
 * Pavle Sarenac 2020/0359
 */

namespace App\Models;

use CodeIgniter\Model;

/**
 * Klasa za implementaciju funkcionalnosti za koriscenje tabele registrovani_korisnik iz baze podataka.
 * 
 * @version 1.0
 */
class RegistrovaniKorisnikModel extends Model {  
    /**
    * @var string $table Table
    */
    protected $table      = 'registrovani_korisnik';
    /**
    * @var string $primaryKey primaryKey
    */
    protected $primaryKey = 'IdKor';
    /**
    * @var string $returnType returnType
    */
    protected $returnType = 'object';
    /**
    * @var array $allowedFields allowedFields
    */
    protected $allowedFields = ['TipKorisnika', 'Ime', 'Prezime', 'MejlAdresa', 'Lozinka', 'KorisnickoIme', 'IdSli', 'IdGra'];


    /**
    * save_user funkcija radi insert novog reda u tabeli
    * @param array $data Data
    * 
    * @return void
    */
    public function save_user($data) {
        $this->insert($data, true);
            // Return the inserted ID
    }

    /**
     * Vraca niz objekata korisnika sa prosledjenim korisnickim imenom
     * 
     * @param String $username
     * 
     * @return Array
     */
    public function getUser($username){
        return $this->where("KorisnickoIme", $username)->findAll();
    }

    /**
     * Ova funkcija vraca red koji se dobija spajanjem vise tabela u kom imamo sve potrebne podatke za majstora (grad, specijalnosti,
     * telefon).
     * 
     * @param int $IdKor IdKor
     * 
     * @return array
     */
    public function getAllForHandyman($IdKor) {
        $db = \Config\Database::connect();
        $builder = $db->table("registrovani_korisnik");
        $builder->select("*");
        return $builder->join("grad", "registrovani_korisnik.IdGra = grad.IdGra")
                ->join("majstor", "majstor.IdMaj = registrovani_korisnik.IdKor")
                ->join("specijalnosti", "majstor.IdSpec = specijalnosti.IdSpec")
                ->join("telefon", "registrovani_korisnik.IdKor = telefon.IdKor")
                ->where("registrovani_korisnik.IdKor", $IdKor)->get()->getResult();
    }

    /**
     * Ova funkcija vraca red koji se dobija spajanjem vise tabela u kom imamo sve potrebne podatke za korisnika (grad, telefon).
     * 
     * @param int $IdKor IdKor
     * 
     * @return array
     */
    public function getAllForRegUser($IdKor) {
        $db = \Config\Database::connect();
        $builder = $db->table("registrovani_korisnik");
        $builder->select("*");
        return $builder->join("grad", "registrovani_korisnik.IdGra = grad.IdGra")
        ->join("telefon", "registrovani_korisnik.IdKor = telefon.IdKor")
        ->where("registrovani_korisnik.IdKor", $IdKor)->get()->getResult();
    }

    /**
     * Vraca red za korisnika sa prosledjenom mejl adresom.
     * 
     * @param string $mail Mail
     * 
     * @return array
     */
    public function getUserWithCertainMailAddress($mail){
        return $this->where("MejlAdresa", $mail)->findAll();
    }

    /**
     * Vraca korisnika sa prosledjenim id-em.
     * 
     * @param int $id Id
     * 
     * @return array
     */
    public function getUsernameFromId($id){
        return $this->where("IdKor", $id)->findAll();
    }


    /**
     * Upisuje novu sifru za zadatog korisnika u bazu
     * 
     * @param integer $IdKor IdKor
     * 
     * @param integer $pass Pass
     * 
     * @return void
     * 
     * 
     */

     public function updatePassword($IdKor, $pass){
        $this->update($IdKor, [
            "Lozinka" => $pass
        ]);
    }

    /**
     * Upisuje putanju do nove slike za zadatog korisnika u bazu
     * 
     * @param integer $IdKor IdKor
     * 
     * @param string $IdPho IdPho
     * 
     * @return void
     * 
     * 
     */

    public function updatePhoto($IdKor, $IdPho){

        
        
        $this->update($IdKor, [
            "IdSli" => $IdPho
        ]);
    }


    /**
     * Upisuje novi grad za zadatog korisnika u bazu
     * 
     * @param integer $IdKor IdKor
     * 
     * @param string $city City
     * 
     * @return void
     * 
     * 
     */
    
    public function updateCity($IdKor, $city){
        $gradModel = new GradModel();
        $idG = $gradModel->searchId($city);
        $this->update($IdKor, [
            "IdGra" => $idG
        ]);

    }


    /**
     * Upisuje novi mejl za zadatog korisnika u bazu
     * 
     * @param integer $IdKor IdKor
     * 
     * @param string $mail Mail
     * 
     * @return void
     * 
     * 
     */

    public function updateMail($IdKor, $mail){
        $this->update($IdKor, [
            "MejlAdresa" => $mail
        ]);

    }



    /**
     * Pronalazi id slike zadatog korisnika
     * 
     * @param integer $IdKor IdKor
     * 
     * @return integer
     * 
     *
     */


    public function getIdPic($IdKor){
        $result = $this->where("IdKor", $IdKor)->find();
        if($result == null) return null;
        return $result[0]->IdSli;
    }
}