/*
Autori:
Nikola Nikolic 2020/0357
*/
$(document).ready(function () {

    /**
     * promenljiva u kojoj cuvamo "pokazivac" na kontejner u kojem ispisujemo poruke
     */
    let chatsBox = $(".container-messages");
    /**
     * promenljiva u kojoj cuvamo autora sesije kada ga pronadjemo
     */
    let authorFound;

    /**
     * proveravamo da li smo na ispravnoj stranici i pozivamo funkciju za inicijalizaciju
     */
    if (window.location.pathname == "/Chat/showMessages") {
        initFunc();
    }

    /**
     * wrapper funkcija za poziv odgovarajucih procedura (za prijem istorije caskanja i pokretanje osluskivaca novih poruka u okviru ceta)
     */
    function ajaxAllMessagesAndAjaxStatus() {
        makeAjaxRequestForAllMessages();
        makeAjaxRequestForChangeStatusReceived();
    }

    /**
     * funkcija za inicijalizaciju konteksta stranice
     */
    function initFunc() {
        makeAjaxRequestForAuthor()
            .then(ajaxAllMessagesAndAjaxStatus)
            .catch(function (error) {
                console.error(error);
            });
    }

    /**
     * salje zahtev serveru da upise Status poruka na primljen (1)
     * 
     * @returns Response
     */
    function makeAjaxRequestForChangeStatusReceived() {
        return new Promise(function (resolve, reject) {
            let newUrl = clearUrl("changeStatusReceived");
            $.ajax({
                url: newUrl,
                method: "POST",
                data: {
                    idAuthor: authorFound.IdKor,
                },
                success: function (response) {
                    let jsonResponse = JSON.parse(response);
                    // alert(response);
                },
                error: function (error) {
                    reject(error);
                }
            })
        });
    }

    /**
    * proverava da li sesija ima autora
    * 
    * @return Array(autora sesije)
    */
    function makeAjaxRequestForAllMessages() {
        return new Promise(function (resolve, reject) {
            let newUrl = clearUrl("getMessageHistory");
            $.ajax({
                url: newUrl,
                method: 'POST',
                data: {
                    idAuthor: authorFound.IdKor,
                },
                success: function (response) {
                    let jsonResponse1 = JSON.parse(response);
                    let messages = jsonResponse1;
                    // dodaj poruke u odgovarajuci kontejner
                    // alert(response);
                    addChats(messages);
                },
                error: function (error) {
                    reject(error);
                }
            });
        });
    }

    /**
    * prima niz cetova, raspakuje ga i poziva odgovarajucu proceduru za ispis
    * 
    * @param {Array} poruke 
    */
    function addChats(chats) {
        for (let i = 0; i < chats.length; i++) {
            let chat = chats[i];
            appendChat(chat.MergedId, chat.Ime, chat.Prezime, chat.SumStatus, chat.StatusNew, chat.IdPos, chat.IdPri);
        }
    }

    /**
   * funkcija koja kreira i dodaje novi element klase "person"
   * @param {integer} Id 
   * @param {String} Name
   * @param {integer} Surname
   * @param {integer} SumStatus - br neprocitanih poruka
   * @param {integer} StatusNew - najnizi rang statusa
   * @param {integer} IdPos
   * @param {integer} IdPri
   */
    function appendChat(Id, Name, Surname, SumStatus, StatusNew, IdPos, IdPri) {
        let chatDiv = $("<div></div>");
        chatDiv.addClass("row");
        chatDiv.addClass("person");
        let set = 0
        if (StatusNew == 0 && IdPos != authorFound.IdKor) {
            chatDiv.addClass("status-0");
            set = 1;
        }
        if (StatusNew == 1 && IdPos != authorFound.IdKor) {
            chatDiv.addClass("status-1");
            set = 1;
        }
        if (StatusNew == 2) {
            chatDiv.addClass("status-2");
            set = 1;
        }
        if (set == 0) {
            chatDiv.addClass("status-2");
            set = 1;
        }
        chatDiv.attr("id", Id);
        let h2Tag = $("<h2></h2>");
        h2Tag.text(Name + " " + Surname);
        let spanText = $("<span></span>");
        spanText.text("Broj nepročitanih poruka: ");
        let spanBrojNeprocitanih = $("<span></span>");
        spanBrojNeprocitanih.text(SumStatus);
        if (StatusNew != 2 && IdPos == authorFound.IdKor) {
            spanBrojNeprocitanih.text(0);
        }

        chatDiv.append(h2Tag, spanText, spanBrojNeprocitanih);
        chatsBox.append(chatDiv);
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
     * 
     * prima string koji ce da nalepi iza kontrolera, pritom cisteci sve nepotrebne parametre iz urla
     * 
     * @param {String} string 
     * @returns String 
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
     * event listener za klik na odredjeni div kojim se dalje poziva trazena funkcija
     */
    $(document).on("click", "div.person", function () {
        let IdFrom = authorFound.IdKor;
        let IdTo = $(this).attr("id");
        let newUrl = clearUrl("showChatting?IdFrom=" + IdFrom + "&IdTo=" + IdTo);
        window.location.href = newUrl;
    });
});