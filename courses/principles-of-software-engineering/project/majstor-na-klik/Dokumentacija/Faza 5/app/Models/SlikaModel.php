<?php namespace App\Models;

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 */

use CodeIgniter\Model;

/**
 * Ova klasa sluzi za rad sa tabelom slika iz baze.
 * 
 * @version 1.0
 */
class SlikaModel extends Model {
    /**
    * @var string $table Table
    */
    protected $table      = 'slika';
    /**
    * @var string $primaryKey primaryKey
    */
    protected $primaryKey = 'IdSli';
    /**
    * @var string $returnType returnType
    */
    protected $returnType = 'object';
    /**
    * @var array $allowedFields allowedFields
    */
    protected $allowedFields = ['IdSli', 'Path'];
}