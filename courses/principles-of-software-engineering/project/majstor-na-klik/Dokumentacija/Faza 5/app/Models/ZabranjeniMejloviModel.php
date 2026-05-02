<?php 

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 */

namespace App\Models;

use CodeIgniter\Model;

/**
 * Ova klasa sluzi za rad sa tabelom zabranjeni_mejlovi iz baze.
 * 
 * @version 1.0
 */
class ZabranjeniMejloviModel extends Model
{
        /**
        * @var string $table Table
        */
        protected $table      = 'zabranjeni_mejlovi';
        /**
        * @var string $primaryKey primaryKey
        */
        protected $primaryKey = 'MejlAdresa';
        /**
        * @var string $returnType returnType
        */
        protected $returnType = 'object';
        /**
        * @var array $allowedFields allowedFields
        */
        protected $allowedFields = ['MejlAdresa', 'IdAdm'];

        
}