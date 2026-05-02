<?php

/**
 * Autori:
 * Nikola Nikolic 2020/0357
 */

namespace App\Filters;

use CodeIgniter\Filters\FilterInterface;
use CodeIgniter\HTTP\RequestInterface;
use CodeIgniter\HTTP\ResponseInterface;

/**
 * GostFilter - klasa za kontrolu pristupa stranici gost
 * 
 * @version 1.0
 */
class GostFilter implements FilterInterface
{
    /**
     * Kontrola pre pristupa stranici
     * 
     * @param RequestInterface $request, @param Array $arguments
     * 
     * @return Redirect
     */
    public function before(RequestInterface $request, $arguments = null)
    {
        $session = session();
        $author = null;
        if($session->has("author")) {
            $author = $session->get("author");
            // var_dump($author);
            $authorType = $author->TipKorisnika;
            $userType = "";
            switch ($authorType) {
                // administrator
                case "A":
                    $userType = "Administrator";
                    break;
                // majstor
                case "M":
                    $userType = "Majstor";
                    break;
                // registrovani korisnik
                case "K":
                    $userType = "Korisnik";
                    break;
                // in case of error return to start page
                default:
                    $userType = "Gost";    
                    break;
                }
            return redirect()->to(site_url($userType."/index"));
        }
    }

    /**
     * Kontrola nakon pristupa stranici, ne koristi se pa nije potrebno implementirati
     * 
     * @param RequestInterface $request, @param Array $arguments
     * 
     * @return Redirect
     */
    public function after(RequestInterface $request, ResponseInterface $response, $arguments = null)
    {
        // Do something here
    }
}

?>