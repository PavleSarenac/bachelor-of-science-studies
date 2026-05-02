<?php 

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 */

namespace App\Models;

use CodeIgniter\Model;


/**
 * PrijavaModel - klasa koja predstavlja model za tabelu Prijava iz baze podataka
 * 
 * @version 1.0
 */
class PrijavaModel extends Model
{
        /**
        * @var string $table Table
        */
        protected $table = 'prijava';
        /**
        *@var string $primaryKey primaryKey
        */
        protected $primaryKey = 'IdPri';
        /**
        * @var string $returnType returnType
        */        
        protected $returnType = 'object';
        /**
        * @var array $allowedFields AllowedFields
        */
        protected $allowedFields = ['IdPri','IdKli', 'IdPrijavljenog', 'Tekst', 'DatumVreme'];
        
        /**
        * save_city funkcija radi insert novog reda u tabeli
        * @param array $data Data
        * 
        * @return void
        */
        public function save_report($data) {
            $this->insert($data, true);
             // Return the inserted ID
        }

        /**
         * Ova funkcija vraca spojenu tabelu prijava sa tabelom registrovani_korisnik, majstor, telefon
         * grad i specijalnosti, kako bi dobili sve potrebne podatke o prijavljenom majstoru i izlistali
         * sve prijave
         * 
         * @return array
         */

        public function search() {
                $db = \Config\Database::connect();
                $builder = $db->table("prijava");
                $builder->select("*");
                $builder->join("registrovani_korisnik", "prijava.IdPrijavljenog = registrovani_korisnik.IdKor")
                ->join("majstor", "registrovani_korisnik.IdKor = majstor.IdMaj")
                ->join("telefon", "registrovani_korisnik.IdKor = telefon.IdKor")
                ->join("grad", "registrovani_korisnik.IdGra = grad.IdGra")
                ->join("specijalnosti", "specijalnosti.IdSpec = majstor.IdSpec");

                $builder->orderBy("DatumVreme", "DESC");
        
              
               
                
                return $builder->get()->getResult();
        }

        /**
         * Ova funkcija vraca spojenu tabelu prijava sa tabelom registrovani_korisnik, majstor, telefon
         * i grad, kako bi dobili sve potrebne podatke o klijentu koji je podneo prijavu
         * 
         * @return array
         */

        public function searchReporter($id){
            $db = \Config\Database::connect();
            $builder = $db->table("registrovani_korisnik");
            $builder->select("*");
             $builder->join("telefon", "registrovani_korisnik.IdKor = telefon.IdKor")
                ->join("grad", "registrovani_korisnik.IdGra = grad.IdGra");
            $builder->where("registrovani_korisnik.IdKor", $id);

            return $builder->get()->getResult();
                
        }


        /**
         * Ova funkcija vraca spojenu tabelu prijava sa tabelom registrovani_korisnik, majstor, telefon
         * grad i specijalnosti i to samo onde gde sr IdKor poklapa sa id-ijem trazenog majstora, kako bi dobili sve
         * podatke o prijavljenom majstoru i izlistali sve njegove prijave.
         * 
         * 
         * @param integer $id Id
         * 
         * @return array
         */


        public function searchHandyman($id){
            $db = \Config\Database::connect();
            $builder = $db->table("prijava");
            $builder->select("*");
            $builder->join("registrovani_korisnik", "prijava.IdPrijavljenog = registrovani_korisnik.IdKor")
            ->join("majstor", "registrovani_korisnik.IdKor = majstor.IdMaj")
            ->join("telefon", "registrovani_korisnik.IdKor = telefon.IdKor")
            ->join("grad", "registrovani_korisnik.IdGra = grad.IdGra")
            ->join("specijalnosti", "specijalnosti.IdSpec = majstor.IdSpec");
            $builder->where("registrovani_korisnik.IdKor", $id);

            $builder->orderBy("DatumVreme", "DESC");
    
          
           
            
            return $builder->get()->getResult();
        }

    
       
}