<?php 

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 * Pavle Sarenac 2020/0359
 */

namespace App\Models;

use CodeIgniter\Model;

/**
 * GradModel - klasa koja predstavlja model za tabelu Grad iz baze podataka
 * 
 * @version 1.0
 */

class GradModel extends Model
{
    /**
     * @var string $table Table
     */
    protected $table = 'grad';
    /**
     * @var string $primaryKey primaryKey
     */
    protected $primaryKey = 'IdGra';
    /**
     * @var string $returnType returnType
     */
    protected $returnType = 'object';
    /**
     * @var array $allowedFields allowedFields
     */
    protected $allowedFields = ['Naziv'];
    

    /**
     * searchId funckija koja pronalazi idGrada sa zadatim nazivom
     * 
     * @param string $tekst Tekst
     * 
     * @return int
     */
    public function searchId($tekst) {
        $grad = $this->where('Naziv', $tekst)->find(); 
        if($grad == null) return null;
        return $grad[0]->IdGra;  
    }

    /**
     * save_city funkcija radi insert novog reda u tabeli
     * 
     * @param array $data Data
     * 
     * @return void
     */
    public function save_city($data) {
        $this->insert($data, true);
    }

    /**
     * selectAllCities funkcija koja vraca sve gradove u tabeli sortirane abecedno rastuce
     * 
     * 
     * @return array
     * 
     */
    public function selectAllCities(){
        $db = \Config\Database::connect();
        $builder = $db->table("grad");
        return $builder->select("*")->orderBy("Naziv", "ASC")->get()->getResult();
    }

}