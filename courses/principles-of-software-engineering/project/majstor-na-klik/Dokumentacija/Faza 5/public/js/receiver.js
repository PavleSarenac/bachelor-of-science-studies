/*
Autori:
Nikola Nikolic 2020/0357
*/
$(document).ready(function () {
    /**
     * @var Interval
     */
    let interval;
    /**
     * @var Object
     */
    let authorFound = "null";

    // poziva se pri ucitavanju svake stranice
    initFunc();

    /**
     * cisti url od svih suvisnih '/' znakova i na njega dodaje prosledjenu metodu kontrolera
     * 
     * @param String 
     * @return String
     */
    function clearUrl(string) {
        let urlParts = window.location.href.split("/");
        if (urlParts.length <= 4) {
            urlParts.push("Gost");
        }
        else {
            while (urlParts.length >= 5) {
                urlParts.pop();
            }
        }
        urlParts.push(string);
        return urlParts.join("/");
    }

    /**
     * proverava da li sesija ima autora
     * 
     * @return Array(autora sesije)
     */
    function makeAjaxRequestForAuthor() {
        return new Promise(function (resolve, reject) {
            let newUrl = clearUrl("getAuthorSession");
            $.ajax({
                url: newUrl,
                method: 'POST',
                success: function (response) {
                    let jsonResponse = JSON.parse(response);
                    let author = jsonResponse; // author koristimo u check author funkciji
                    authorFound = author;
                    resolve(author);
                },
                error: function (error) {
                    reject(error);
                }
            });
        });
    }

    /**
     * salje upit ka serveru da li ima pristiglih poruka za autora sesije
     * 
     * @return Array(pristigle poruke)
     */
    function makeAjaxRequestForMessages() {
        let newUrl = clearUrl("checkMessagesForAuthor");
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: newUrl,
                method: 'POST',
                data: {
                    authorMessages: authorFound.IdKor
                },
                success: function (response) {
                    let jsonResponse = JSON.parse(response);
                    // alert("response: " + response);
                    let poruke = jsonResponse;
                    // alert(poruke);
                    let len = 0;
                    if (poruke) len = poruke.length;
                    $(".badgeMy").text(len);
                    resolve(poruke);
                },
                error: function (error) {
                    reject(error);
                }
            });
        });
    }

    /**
     * salje upit ka serveru da li ima pristiglih poruka za autora sesije ako smo u stranici za cet
     * 
     * @return Array(pristigle poruke)
     */
    function makeAjaxRequestForMessagesChat() {
        let newUrl = clearUrl("checkMessagesForAuthor");
        let url = new URL(window.location.href);
        let params = new URLSearchParams(url.search);
        IdFrom = params.get("IdFrom");
        IdTo = params.get("IdTo");
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: newUrl,
                method: 'POST',
                data: {
                    authorMessages: IdFrom,
                    idTo: IdTo,
                },
                success: function (response) {
                    let jsonResponse = JSON.parse(response);
                    // alert("response: " + response);
                    let poruke = jsonResponse;
                    // alert(poruke);
                    let len = 0;
                    if (poruke) len = poruke.length;
                    $(".badgeMy").text(len);
                    resolve(poruke);
                },
                error: function (error) {
                    reject(error);
                }
            });
        });
    }

    /**
     * periodicno proverava koliko poruka je pristiglo za autora ssesije
     * 
     * @return Response
     */
    function checkMessages() {
        if (authorFound === "null") {
            clearInterval(interval);
            return;
        }
        interval = setInterval(makeAjaxRequestForMessages, 5000);
    }

    /**
     * prva provera da li postoje poruke pa onda pokretanje intervala koji ce proveravati na svakih 5 sekundi
     * 
     * @return Response
     */
    function firstCallForMessages() {
        makeAjaxRequestForMessages()
            .then(checkMessages)
            .catch(function (error) {
                console.error(error);
            });
    }

    /**
     * funkcija koja trazi da li ucitana stranica ima autora i zatim poziva funkciju koja periodicno proverava da li ima poruka za autora
     * 
     * @return Response
     */
    function initFunc() {
        if (window.location.pathname === "/Chat/showChatting") {
            makeAjaxRequestForMessagesChat();
        }
        else {
            makeAjaxRequestForAuthor()
                .then(firstCallForMessages)
                .catch(function (error) {
                    console.error(error);
                });
        }

    }
});
