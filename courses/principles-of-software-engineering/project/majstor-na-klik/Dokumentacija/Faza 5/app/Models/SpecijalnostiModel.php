<?php 

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 * Pavle Sarenac 2020/0359
 */

namespace App\Models;

use CodeIgniter\Model;

/**
 * Klasa SpecijalnostiModel se koristi za dohvatanje rezultata pretrage iz baze. Vazno je napomenuti da ovaj model koristi u
 * svojoj implementaciji, pored tabele specijalnosti i tabele majstor, registrovani_korisnik, telefon i grad.
 * 
 * @version 1.0
 */
class SpecijalnostiModel extends Model {
    /**
    * @var string $table Table
    */
    protected $table = 'specijalnosti';
    /**
    * @var string $primaryKey primaryKey
    */
    protected $primaryKey = 'IdSpec';
    /**
    * @var string $returnType returnType
    */
    protected $returnType = 'object';
    /**
    * @var array $allowedFields allowedFields
    */
    protected $allowedFields = ['opis'];

    /**
     * selectAllSpec funkcija dohvata sve specijalnosti u tabeli
     * 
     * @return array
     */
    public function selectAllSpec(){
        return $this->select()->orderBy("Opis", "ASC")->findAll();
    }

    /**
     * selectOpis funkcija dohvata id prve specijalnosti koja sadrzi zadati opis
     * 
     * @param string $opis Opis
     * 
     * @return array
     */

    public function selectId($opis){
        $spec = $this->where("Opis", $opis)->find();
        if($spec == null) return null;
        return $spec[0]->IdSpec;
    }

    /**
     * Ova funkcija vraca rezultate pretrage iz baze.
     * 
     * @param string $handyman;
     * @param string $city;
     * @param string $priceSort;
     * @param string $speedSort;
     * @param string $qualitySort;
     * 
     * @return array
     */
    public function search($handyman, $city, $priceSort, $speedSort, $qualitySort) {
        $db = \Config\Database::connect();
        $builder = $db->table("specijalnosti");
        $builder->select("*");
        if (!empty($handyman)) {
            $builder->like("Opis", $handyman);
        }
        $builder
        ->join("majstor", "specijalnosti.IdSpec = majstor.IdSpec")
        ->join("registrovani_korisnik", "majstor.IdMaj = registrovani_korisnik.IdKor")
        ->join("telefon", "registrovani_korisnik.IdKor = telefon.IdKor")
        ->join("grad", "registrovani_korisnik.IdGra = grad.IdGra");
        
        if (!empty($city)) {
            $builder->where("grad.Naziv", $city);
        }

        if ($priceSort == "priceAsc") {
            $builder->orderBy("majstor.ProsecnaCena", "ASC");
        } else if ($priceSort == "priceDesc") {
            $builder->orderBy("majstor.ProsecnaCena", "DESC");
        }

        if ($speedSort == "speedAsc") {
            $builder->orderBy("majstor.ProsecnaBrzina", "ASC");
        } else if ($speedSort == "speedDesc") {
            $builder->orderBy("majstor.ProsecnaBrzina", "DESC");
        }      

        if ($qualitySort == "qualityAsc") {
            $builder->orderBy("majstor.ProsecanKvalitet", "ASC");
        } else if ($qualitySort == "qualityDesc") {
            $builder->orderBy("majstor.ProsecanKvalitet", "DESC");
        }

        $builder->orderBy("majstor.BrojRecenzija", "DESC");
        
        return $builder->get()->getResult();
    }
}