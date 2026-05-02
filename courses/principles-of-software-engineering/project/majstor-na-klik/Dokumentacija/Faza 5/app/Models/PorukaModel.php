<?php

/**
 * Autori:
 * Nikola Nikolic 2020/0357
 */

namespace App\Models;

use CodeIgniter\Model;
use CodeIgniter\I18n\Time;

/**
 * PorukaModel - klasa za implementaciju funkcionalnosti za koriscenje tabele iz baze podataka
 * 
 * @version 1.0
 */
class PorukaModel extends Model
{
    /**
     * var String $table
     */
    protected $table      = "poruka";
    /**
     * var String $primaryKey
     */
    protected $primaryKey = "IdP";
    /**
     * var String $returnType
     */
    protected $returnType     = "object";
    /**
     * var Array $allowedFields
     */
    protected $allowedFields = ["Tekst", "DatumVreme", "IdPri", "IdPos", "Status"];

    /**
     * ubacivanje u bazu podataka u tabelu poruka
     * 
     * @param integer $IdFrom, @param integer $IdTo, @param String $message
     * 
     * @return integer
     */
    public function insertData($IdFrom, $IdTo, $message)
    {
        $data = array(
            'Tekst' => $message,
            'DatumVreme' => Time::now("Europe/Belgrade", "en_US"),
            'IdPos' => $IdFrom,
            'IdPri' => $IdTo,
            'Status'=> 0,
        );
        $this->insert($data, true); // true for auto validation
        return $this->insertID();
    }

    /**
     * vraca DatumVreme ukoliko postoji prosledjeni id, u suprotnom null
     * 
     * @param integer $IdP
     * 
     * @return String
     */
    public function getTimestampFromId($IdP)
    {
        $poruka = $this->find($IdP);
        if($poruka)
            return $poruka->DatumVreme;
        else 
            return null;
    }

    /**
     * vraca sve poruke korisnika koje su u statusu pos(0), grupisane po IdPos
     * 
     * @param integer $IdTo
     * 
     * @return Array $poruke
     */
    public function getAllReceivedMessagesGroupBy($IdTo){
        $poruke = $this->select('IdPos, COUNT(*) as BrojPoruka')
                    ->where("IdPri", $IdTo)
                    ->where("Status", 0)
                    ->groupBy('IdPos')
                    ->orderBy("DatumVreme", "ASC")
                    ->findAll();

        if($poruke == null){
            return null;
        } 

        return $poruke;
    }

    /**
     * vraca sve poruke korisnika koje su u statusu pos(0)
     * 
     * @param integer $IdTo
     * 
     * @return Array $poruke
     */
    public function getAllReceivedMessages($IdTo){
        $poruke = $this->select('IdPos')
                    ->where("IdPri", $IdTo)
                    ->where("Status", 0)
                    ->orderBy("DatumVreme", "ASC")
                    ->findAll();

        if($poruke == null){
            return null;
        } 

        return $poruke;
    }

    /**
     * vraca celu istoriju catovanja 
     * 
     * @param integer $id - autor sesije
     * 
     * @param integer $idTo - receiver sesije
     * 
     * @return Array $poruke
     */
    public function getAllMessages($id, $idTo){
        $poruke = $this->select()
        ->where("(IdPri = $id AND IdPos = $idTo) OR (IdPri = $idTo AND IdPos = $id)")
        ->orderBy('DatumVreme', 'ASC')
        ->findAll();

        if($poruke == null){
            return null;
        } 

        return $poruke;
    }

    /**
     * azurira status poruka, gde je autor(idFrom) primalac, na procitan
     * 
     * @param integer $idFrom
     * 
     * @param integer $idTo
     */
    public function setMessagesSeen($idFrom, $idTo){
        $this->set(['Status' => 2])
        ->where('IdPri', $idFrom)
        ->where('IdPos', $idTo)
        ->update();
    }
    
    /**
     * azurira status poruka, gde je autor(idFrom) primalac, na primljen
     * 
     * @param integer $idFrom
     * 
     * @param integer $idTo
     */
    public function setMessagesReceived($idFrom){
        $this->set(['Status' => 1])
        ->where('IdPri', $idFrom)
        ->where('Status !=', 2)
        ->update();
    }

    /**
     * prima obavestenje o primljenim porukama ali ne od osobe sa kojom se trenutno cetuje
     * 
     * @param integer $authorid
     * 
     * @param integer $IdTo
     */
    public function getAllReceivedMessagesWhileInChat($authorId, $idTo){
        $poruke = $this->select('IdPos')
                    ->where("IdPri", $authorId)
                    ->where("IdPos !=", $idTo)
                    ->where("Status", 0)
                    ->orderBy("DatumVreme", "ASC")
                    ->findAll();

        if($poruke == null){
            return null;
        } 

        return $poruke;
    }

    /**
     * prima obavestenje o primljenim porukama ali ne od osobe sa kojom se trenutno cetuje
     * grupisane po IdPos
     * 
     * @param integer $authorid
     * 
     * @param integer $IdTo
     */
    public function getAllReceivedMessagesWhileInChatGroupBy($authorId, $idTo){
        $poruke = $this->select('IdPos, COUNT(*) as BrojPoruka')
                    ->where("IdPri", $authorId)
                    ->where("IdPos !=", $idTo)
                    ->where("Status", 0)
                    ->groupBy('IdPos')
                    ->orderBy("DatumVreme", "ASC")
                    ->findAll();

        if($poruke == null){
            return null;
        } 

        return $poruke;
    }

    /**
     * trazi celu istoriju caskanja za autora sesije
     * 
     * @param integer $authorId - autor sesije
     * 
     * @return Array [{integer MergedId,integer StatusNew,integer SumStatus,String DatumVreme,String Ime,String Prezime,integer IdPos,integer IdPri}]
     */
    public function findAllMessageHistory($authorId){
        $db = \Config\Database::connect();
        $query = $db->query("
            SELECT 
                CASE
                    WHEN p.IdPri = $authorId THEN p.IdPos
                    WHEN p.IdPos = $authorId THEN p.IdPri
                END AS MergedId,
                MIN(p.Status) AS StatusNew,
                SUM(CASE WHEN p.Status IN (0, 1) THEN 1 ELSE 0 END) AS SumStatus,
                MAX(p.DatumVreme) AS DatumVremeNew,
                r.Ime,
                r.Prezime,
                (
                    SELECT p1.IdPos
                    FROM poruka p1
                    WHERE (p1.IdPos = p.IdPos AND p1.IdPri = p.IdPri)
                    OR
                    (p1.IdPri = p.IdPos AND p1.IdPos = p.IdPri)
                    ORDER BY p1.DatumVreme DESC
                    LIMIT 1
                ) AS IdPos,
                (
                    SELECT p2.IdPri
                    FROM poruka p2
                    WHERE (p2.IdPos = p.IdPos AND p2.IdPri = p.IdPri)
                    OR
                    (p2.IdPri = p.IdPos AND p2.IdPos = p.IdPri)
                    ORDER BY p2.DatumVreme DESC
                    LIMIT 1
                ) AS IdPri
            FROM poruka p
            JOIN registrovani_korisnik r ON r.IdKor = 
                CASE
                    WHEN p.IdPri = $authorId THEN p.IdPos
                    WHEN p.IdPos = $authorId THEN p.IdPri
                END
            WHERE (p.IdPri = $authorId OR p.IdPos = $authorId)
            GROUP BY MergedId, r.Ime, r.Prezime
            ORDER BY StatusNew ASC, DatumVremeNew DESC, MergedId ASC
        ");
        
        $result = $query->getResult();
        return $result;
    }

    /**
     * proverava da li autor sesije ima pristiglih poruka od osobe sa idjem IdFrom
     * 
     * @param integer $author - autor sesije
     * 
     * @param integer $IdFrom - korisnik od koga se trazi prijem poruka
     * 
     * @return Array [poruka] $poruke
     */
    public function getNewMessages($author, $IdFrom){
        $poruke = $this->where("IdPri", $author)
            ->where("IdPos", $IdFrom)
            ->where("Status", 0)
            ->orderBy("DatumVreme", "ASC")
            ->findAll();
        
        if($poruke) return $poruke;
        else return null;
    }

    
}



?>