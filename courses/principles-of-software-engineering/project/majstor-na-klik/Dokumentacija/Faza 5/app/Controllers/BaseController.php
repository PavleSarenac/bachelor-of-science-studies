<?php

/**
 * Autori:
 * Ljubica Majstorovic 2020/0253
 * Nikola Nikolic 2020/0357
 * Pavle Sarenac 2020/0359
 */

namespace App\Controllers;

use App\Models\RecenzijaModel;
use App\Models\SpecijalnostiModel;
use App\Models\RegistrovaniKorisnikModel;
use App\Models\SlikaModel;
use CodeIgniter\Controller;
use CodeIgniter\HTTP\CLIRequest;
use CodeIgniter\HTTP\IncomingRequest;
use CodeIgniter\HTTP\RequestInterface;
use CodeIgniter\HTTP\ResponseInterface;
use Psr\Log\LoggerInterface;

/**
 * Class BaseController
 *
 * BaseController provides a convenient place for loading components
 * and performing functions that are needed by all your controllers.
 * Extend this class in any new controllers:
 *     class Home extends BaseController
 *
 * For security be sure to declare any new methods as protected or private.
 */
abstract class BaseController extends Controller
{
    /**
     * Instance of the main Request object.
     *
     * @var CLIRequest|IncomingRequest
     */
    protected $request;

    /**
     * An array of helpers to be loaded automatically upon
     * class instantiation. These helpers will be available
     * to all other controllers that extend BaseController.
     *
     * @var array
     */
    protected $helpers = ['form', 'url'];

    /**
     * Be sure to declare properties for any property fetch you initialized.
     * The creation of dynamic property is deprecated in PHP 8.2.
     */
    protected $session;

    /**
     * Constructor.
     */
    public function initController(RequestInterface $request, ResponseInterface $response, LoggerInterface $logger)
    {
        // Do Not Edit This Line
        parent::initController($request, $response, $logger);

        // Preload any models, libraries, etc, here.
        $this->session = session();
    }

    /**
     * Prikaz pocetne stranice index
     * 
     * @return Response
     */
    public function index()
    {
        $this->show("index", []);
    }

    /**
     * Ova funkcija sluzi za prikaz profila majstora.
     * 
     * @param string $name Name
     * @param string $surname Surname
     * @param string $specialty Specialty
     * @param string $city City
     * @param string $phone Phone
     * @param string $email Email
     * @param int $id IdMajstora
     * 
     * @return void
     */
    public function prikazProfilaMajstora($name, $surname, $specialty, $city, $phone, $email, $id) {
        $regModel = new RegistrovaniKorisnikModel();
        $slikaModel = new SlikaModel();
        $idSlike = $regModel->where("IdKor", $id)->findAll();
        $idSlike = $idSlike[0]->IdSli;
        $path = $slikaModel->where("IdSli", $idSlike)->findAll();
        if($path != null){
            $path = $path[0]->Path;
        }
        $this->show("majstorInfo", [
            "name" => $name,
            "surname" => $surname,
            "specialty" => $specialty,
            "city" => $city,
            "phone" => $phone,
            "email" => $email,
            "path" => $path,
            "id" => $id
        ]);
    }


    /**
     * Ova funkcija sluzi za prikaz profila korisnika.
     * 
     * @param string $name Name
     * @param string $surname Surname
     * @param string $city City
     * @param string $phone Phone
     * @param string $email Email
     * @param int $id IdKorisnika
     * 
     * @return void
     */
    public function prikazProfilaKorisnika($name, $surname, $city, $phone, $email, $id) {
        $regModel = new RegistrovaniKorisnikModel();
        $slikaModel = new SlikaModel();
        $idSlike = $regModel->where("IdKor", $id)->findAll();
        $idSlike = $idSlike[0]->IdSli;
        $path = $slikaModel->where("IdSli", $idSlike)->findAll();
        if($path != null){
            $path = $path[0]->Path;
        }
        $this->show("korisnikInfo", [
            "name" => $name,
            "surname" => $surname,
            "city" => $city,
            "phone" => $phone,
            "email" => $email,
            "path" => $path,
            "id" => $id
        ]);
    }

   /**
     * Za prikaz zeljenih stranica iz foldera pages
     * 
     * @param String $page, @param Array $data
     * 
     * @return Response
     */
    protected function show($page, $data){
        throw \CodeIgniter\Exceptions\PageNotFoundException::forPageNotFound();
    }

    /**
     * Ova fukcija se izvrsava kada korisnik klikne na dugme pretrazi na pocetnoj stranici. Prikazuje se stranica sa izlistanim
     * majstorima koje je korisnik trazio.
     */
    public function search() {
        $_SESSION["func"] = "search";
        $handyman = $this->request->getVar("handyman");
        $city = $this->request->getVar("city");
        $priceSort = $this->request->getVar("priceRadio");
        $speedSort = $this->request->getVar("speedRadio");
        $qualitySort = $this->request->getVar("qualityRadio");

        $specijalnostiModel = new SpecijalnostiModel();
        $searchResult = $specijalnostiModel->search($handyman, $city, $priceSort, $speedSort, $qualitySort);
        $this->session->set("lastSearchResult", $searchResult);
        $this->session->set("searchRowNumber", 10);

        $this->show("pretraga_i_sortiranje", 
        [
            "searchHandyman" => $handyman, 
            "searchCity" => $city,
        ]);

    }

    /**
     * Ova funkcija je redefinisana u klasi Administrator, sluzi za ucitavanje novih prijava u browser
     */

     public function fetchNextResultsReports(){
        throw \CodeIgniter\Exceptions\PageNotFoundException::forPageNotFound();
    }

    /**
     * Ova funkcija je redefinisana u klasi Administrator, sluzi za ucitavanje novih prijava odredjenog majstora u browser
     */

    public function fetchNextResultsHandymanReports(){
        throw \CodeIgniter\Exceptions\PageNotFoundException::forPageNotFound();
    }

    /**
     * Ova funkcija sluzi za izlistavanje svih tekstualnih recenzija za majstora sa id-em $handymanId.
     * 
     * @param int $handymanId HandymanId
     * @param string $name Name
     * @param string $surname Surname
     * @param string $specialty Specialty
     * 
     * @return void
     */
    public function readHandymanReviews($handymanId, $name, $surname, $specialty) {
        $_SESSION["func"] = "reviews";
        $recenzijaModel = new RecenzijaModel();
        $reviews = $recenzijaModel->getReviews($handymanId);
        $this->session->set("reviewsResult", $reviews);
        $this->session->set("reviewsRowNumber", 10);

        $this->show("pregled_ocena", [
            "handymanName" => $name,
            "handymanSurname" => $surname,
            "handymanSpecialty" => $specialty
        ]);
    }

    /**
     * Ova funkcija se poziva svaki put kada majstor pri listanju tekstualnih recenzija nekog majstora dodje do dna stranice, a taj
     * majstor ima jos recenzija koje nisu prikazane bile u bazi - tada se te recenzije dinamicki ucitavaju. Ova funkcija se poziva
     * preko AJAX zahteva. Povratna vrednost funkcije je string koji je zapravo html kod u kom su te nove recenzije koje treba
     * prikazati.
     * 
     * @return string 
     */
    public function fetchNextResultsReviews() {
        $rowNumber = (int)$this->session->get("reviewsRowNumber");
        $remainingRows = count($this->session->get("reviewsResult")) - $rowNumber;
        $currentBlock = array_slice($this->session->get("reviewsResult"), $rowNumber, 
        $remainingRows >= 10 ? 10 : null);
        $newResultBlock = "";

        foreach ($currentBlock as $review) {
            if ($review->Tekst != "") {
                $encName = rawurlencode($review->Ime);
                $encSurname = rawurlencode($review->Prezime);
                $encCity = rawurlencode($review->Naziv);
                $encPhone = rawurlencode($review->Telefon);
                $encMail = rawurlencode($review->MejlAdresa);
                $encId = rawurlencode($review->IdKor);

                $newReview =
                "<div class='alert alert-light' role='alert'><div class='row'>" .
                "<div class='col-sm-3 text-left'>" . 
                anchor(
                    site_url($this->session->get("controller") . "/prikazProfilaKorisnika" .
                    "/$encName/$encSurname/$encCity/$encPhone/$encMail/$encId"), 
                    $review->Ime . " " . $review->Prezime,
                    array('class' => 'majstorLink')) .
                    "</div>" .
                    "<div class='col-sm-9 text-left'>" . $review->Tekst . "</div>" .
                    "</div>" . "</div>";
                $newResultBlock .= $newReview;
            }
        }
        $this->session->set("reviewsRowNumber", $rowNumber + 10);
        return $newResultBlock;
    }

    /**
     * Ova funkcija se izvrsava kao posledica AJAX zahteva kada korisnik scroll-uje kroz rezultate svoje pretrage i dodje do dna 
     * stranice - tada se dinamicki ucitava novi blok majstora u stranicu. Posto se na vise razlicitih stranica poziva ova funkcija,
     * na njenom pocetku radimo preusmeravanje na odgvoarajuce druge funkcije po potrebi (ako umesto rezultata pretrage zapravo
     * treba prikazati recenzije konkretnog majstora, sve prijave u bazi ili prijave konkretnog majstora).
     * 
     * @return string
     */
    public function fetchNextResults() {
        $controller = $this->session->get("controller");
        if (array_key_exists("func", $_SESSION)) {
            if ($_SESSION["func"] == "reviews") {
                return $this->fetchNextResultsReviews();
            }
            if ($_SESSION["func"] == "reports") {
                return $this->fetchNextResultsReports();
            }
            if ($_SESSION["func"] == "handymanReports") {
        
                return $this->fetchNextResultsHandymanReports();
            }
        }

        $rowNumber = (int)$this->session->get("searchRowNumber");
        $remainingRows = count($this->session->get("lastSearchResult")) - $rowNumber;

        $currentBlock = array_slice($this->session->get("lastSearchResult"), $rowNumber, 
        $remainingRows >= 10 ? 10 : null);
        $newResultBlock = "";
        foreach ($currentBlock as $result) {
            $newHandyman = "<div class='alert alert-light' role='alert'><div class='row'>" .
            "<div class='col text-center'>" . $result->Ime . " " . $result->Prezime . "</div>" .
            "<div class='col text-center'>" . ($result->BrojRecenzija ?? "Nije ocenjen.") . "</div>" .
            "<div class='col text-center'>" . ($result->ProsecnaCena ?? "Nije ocenjen.") . "</div>" . 
            "<div class='col text-center'>" . ($result->ProsecnaBrzina ?? "Nije ocenjen.") . "</div>" .
            "<div class='col text-center'>" . ($result->ProsecanKvalitet ?? "Nije ocenjen.") . "</div></div></div>";
            $encodedName = rawurlencode($result->Ime);
            $encodedSurname = rawurlencode($result->Prezime);
            $encodedSpecialty = rawurlencode($result->Opis);
            $encodedCity = rawurlencode($result->Naziv);
            $encodedPhone = rawurlencode($result->Telefon);
            $encodedMail = rawurlencode($result->MejlAdresa);
            $id = rawurlencode($result->IdKor);
            $newResultBlock .= anchor(
                "$controller/prikazProfilaMajstora/$encodedName/$encodedSurname/$encodedSpecialty/$encodedCity/$encodedPhone/$encodedMail/$id",
                $newHandyman,
                array('class' => 'majstorLink')
            );
        }
        $this->session->set("searchRowNumber", $rowNumber + 10);
        echo $newResultBlock;
    }
    
     /**
     * vraca vrednosti author iz sesije
     * 
     * @return JSON file
     */
    public function getAuthorSession(){
        $session = session();
        $author = "";
        if ($session->has("author")) {
            $author = $session->get("author");
        }
        else $author = "null";
        echo json_encode($author);
    }

    /**
     * nadji username od id iz POST niza
     * 
     * @return JSON file
     */
    public function getUsernameFromId(){
        $idReceiver = isset($_POST['idReceiver']) ? $_POST['idReceiver'] : null;
        $regKorModel = new RegistrovaniKorisnikModel();
        $korisnik = $regKorModel->getUsernameFromId($idReceiver);
        if($korisnik)$korisnik = $korisnik[0];
        echo json_encode($korisnik->KorisnickoIme);
    }

}
