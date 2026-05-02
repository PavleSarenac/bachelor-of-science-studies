<?php 

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 */

namespace App\Models;

use CodeIgniter\Model;

/**
 * Ova klasa sluzi za rad sa tabelom telefon iz baze.
 * 
 * @version 1.0
 */
class TelefonModel extends Model
{
        /**
        * @var string $table Table
        */
        protected $table      = 'telefon';
        /**
        * @var string $primaryKey primaryKey
        */
        protected $primaryKey = ['IdKor', 'Telefon'];
        /**
        * @var string $returnType returnType
        */
        protected $returnType = 'object';
        /**
        * @var array $allowedFields allowedFields
        */
        protected $allowedFields = ['IdKor', 'Telefon'];

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
}