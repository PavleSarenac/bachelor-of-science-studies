<?php 

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 * Pavle Sarenac 2020/0359
 */

namespace App\Models;

use CodeIgniter\Model;


/**
 * MajstorModel - klasa koja predstavlja model za tabelu Majstor iz baze podataka
 * 
 * @version 1.0
 */
class MajstorModel extends Model
{
        /**
        * @var string $table Table
        */
        protected $table      = 'majstor';
        /**
        *@var string $primaryKey primaryKey
        */
        protected $primaryKey = 'IdMaj';
        /**
        * @var string $returnType returnType
        */        
        protected $returnType = 'object';
        /**
        * @var array $allowedFields AllowedFields
        */
        protected $allowedFields = ['IdMaj', 'BrojRecenzija', 'ProsecnaCena', 'ProsecnaBrzina', 'ProsecanKvalitet', 'IdSpec'];
        
        /**
        * save_city funkcija radi insert novog reda u tabeli
        * @param array $data Data
        * 
        * @return void
        */
        public function save_user($data) {
            $this->insert($data, true);
             // Return the inserted ID
        }

        /**
         * Ova funkcija vraca red tabele u kom je jedna od kolona specijalnost majstora sa $IdMaj.
         * 
         * @param int $IdMaj IdMaj
         * 
         * @return array
         */
        public function getSpecialty($IdMaj) {
                $db = \Config\Database::connect();
                $builder = $db->table("majstor");
                $builder->select("specijalnosti.Opis");
                $builder->where("majstor.IdMaj", $IdMaj);
                $builder->join("specijalnosti", "majstor.IdSpec = specijalnosti.IdSpec");
                return $builder->get()->getResult();
        }
       
        /**
         * Ova funkcija vraca red tabele u kom je jedna od kolona ime majstora sa $IdMaj.
         * 
         * @param int $IdMaj IdMaj
         * 
         * @return array
         */
        public function getName($IdMaj) {
                $db = \Config\Database::connect();
                $builder = $db->table("majstor");
                $builder->select("registrovani_korisnik.Ime");
                $builder->where("majstor.IdMaj", $IdMaj);
                $builder->join("registrovani_korisnik", "majstor.IdMaj = registrovani_korisnik.IdKor");
                return $builder->get()->getResult();
        }

        /**
         * Ova funkcija vraca red tabele u kom je jedna od kolona prezime majstora sa $IdMaj.
         * 
         * @param int $IdMaj IdMaj
         * 
         * @return array
         */
        public function getSurname($IdMaj) {
                $db = \Config\Database::connect();
                $builder = $db->table("majstor");
                $builder->select("registrovani_korisnik.Prezime");
                $builder->where("majstor.IdMaj", $IdMaj);
                $builder->join("registrovani_korisnik", "majstor.IdMaj = registrovani_korisnik.IdKor");
                return $builder->get()->getResult();
        }

        /**
         * Ova funkcija treba da azurira prosecne ocena majstora nakon sto korisnik oceni majstora.
         * 
         * @param int $handymanId HandymanId
         * @param int $priceRating PriceRating
         * @param int $speedRating SpeedRating
         * @param int $qualityRating QualityRating
         * 
         * @return void
         */
        public function updateRatings($handymanId, $priceRating, $speedRating, $qualityRating) {
                $db = \Config\Database::connect();
                $builder = $db->table("majstor");
                $handymanRow = $builder->select("*")->where("majstor.IdMaj", $handymanId)->get()->getResult()[0];
                
                $oldNumOfReviews = $handymanRow->BrojRecenzija == null ? 0 : intval($handymanRow->BrojRecenzija);
                $newNumOfReviews = $oldNumOfReviews + 1;
                $newPriceAverage = null;
                $newSpeedAverage = null;
                $newQualityAverage = null;

                // New price average
                if ($handymanRow->ProsecnaCena == null) {
                        $newPriceAverage = $priceRating;
                } else {
                        $newPriceAverage = 
                        ((floatval($handymanRow->ProsecnaCena) * $oldNumOfReviews) + $priceRating) / $newNumOfReviews;
                }

                // New speed average
                if ($handymanRow->ProsecnaBrzina == null) {
                        $newSpeedAverage = $speedRating;
                } else {
                        $newSpeedAverage = 
                        ((floatval($handymanRow->ProsecnaBrzina) * $oldNumOfReviews) + $speedRating) / $newNumOfReviews;
                }

                // New quality average
                if ($handymanRow->ProsecanKvalitet == null) {
                        $newQualityAverage = $qualityRating;
                } else {
                        $newQualityAverage = 
                        ((floatval($handymanRow->ProsecanKvalitet) * $oldNumOfReviews) + $qualityRating) / $newNumOfReviews;
                }

                $builder->set([
                        "BrojRecenzija" => $newNumOfReviews,
                        "ProsecnaCena" => $newPriceAverage,
                        "ProsecnaBrzina" => $newSpeedAverage,
                        "ProsecanKvalitet" => $newQualityAverage,
                ]);
                $builder->where("IdMaj", $handymanId);
                $builder->update();

        }
}