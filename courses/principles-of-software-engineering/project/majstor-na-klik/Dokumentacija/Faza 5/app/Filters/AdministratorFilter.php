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
 * AdministratorFilter - klasa za kontrolu pristupa stranici administrator
 * 
 * @version 1.0
 */
class AdministratorFilter implements FilterInterface
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
        if($author && $author->TipKorisnika != "A"){
            $userType = "";
            switch($author->TipKorisnika){
                case "M":
                    $userType = "Majstor";
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