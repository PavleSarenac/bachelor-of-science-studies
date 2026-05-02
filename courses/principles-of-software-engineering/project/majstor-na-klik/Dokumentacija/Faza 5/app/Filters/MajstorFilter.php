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
 * MajstorFilter - klasa za kontrolu pristupa stranici majstor
 * 
 * @version 1.0
 */
class MajstorFilter implements FilterInterface
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
        if($author && $author->TipKorisnika != "M"){
            $userType = "";
            switch($author->TipKorisnika){
                case "A":
                    $userType = "Administrator";
                    break;
                case "K":
                    $userType = "Korisnik";
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