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
 * KorisnikFilter - klasa za kontrolu pristupa stranici korisnik
 * 
 * @version 1.0
 */
class KorisnikFilter implements FilterInterface
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
        if(!$session->has("author"))
            return redirect()->to(site_url("Gost/index"));
        $author = $session->get("author");  
        if($author && $author->TipKorisnika != "K"){
            $userType = "";
            switch($author->TipKorisnika){
                case "M":
                    $userType = "Majstor";
                    break;
                case "A":
                    $userType = "Administrator";
                    break;
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