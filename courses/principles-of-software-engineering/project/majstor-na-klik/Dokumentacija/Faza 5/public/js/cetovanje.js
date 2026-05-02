/*
Autori:
Nikola Nikolic 2020/0357
*/
$(document).ready(function () {
  /**
   * promeljiva u kojoj se cuva chat box
   */
  const chatBox = $(".chat-box");
  /**
   * promenljiva u kojoj se cuva input za poruke
   */
  const messageInput = $("#message-input-chat");
  /**
   * promenljiva u kojoj se cuva dugme za slanje
   */
  const sendButton = $("#send-button-chat");

  /**
   * integer - id korisnika sa kojim se dopisujemo
   */
  let IdTo;

  /**
   * integer - id autora sesije (nas id)
   */
  let IdFrom;

  /**
   * autor sesije i ssvi njegovi podaci
   */
  let authorFound;

  /**
   * String - korisnicko ime autora sesije
   */
  let authorUsername;

  /**
   * String - korisnicko ime korisnika sa kojim se dopisujemo
   */
  let receiverUsername;

  /**
   * interval periodicne provere o pristiglim porukama
   */
  let interval;

  /**
   * pomera skrol bar na dno stranice
   */
  function scrollToBottom() {
    chatBox.scrollTop(chatBox[0].scrollHeight);
  }

  /**
   * ovime obezbedjujemo da se initFunc() iz ovog js fajla poziva samo pri ucitavanju prikaza ceta
   */
  if(window.location.pathname == "/Chat/showChatting"){
    initFunc();
    scrollToBottom();
  }

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
   * proverava da li postoji istorija caskanja izmedju ova dva korisnika i ucitava je
   * 
   * @return Response - ucitava u stranicu sve potrebne elemente
   */
  function makeAjaxRequestForAllMessages() {
    let url = new URL(window.location.href);
    let params = new URLSearchParams(url.search);
    IdFrom = params.get("IdFrom");
    IdTo = params.get("IdTo");
    return new Promise(function (resolve, reject) {
      let newUrl = clearUrl("getAllMessages");
      $.ajax({
        url: newUrl,
        method: 'POST',
        data:{
          idFrom: IdFrom, 
          idTo: IdTo,
        },
        success: function (response) {
          let jsonResponse = JSON.parse(response);
          let poruke = jsonResponse; // author koristimo u check author funkciji
          // dodati poruke u tag, gde je pos stavi klasu owner, gde je pri other 
          if(poruke)dodajPoruke(poruke);
          // postaviti poruke gde je autor.IdKor = IdPri i gde je  na procitane
          newUrl = clearUrl("setReadMessages");
          $.ajax({
            url: newUrl,
            method: "POST",
            data:{
              idFrom: IdFrom,
              idTo: IdTo,
            },
            success: function (response) {
            },
            error: function (error){
              reject(error)
            }
          });
        },
        error: function (error) {
          reject(error);
        }
      });
    });
  }

  /**
   * prima niz poruka, raspakuje ga i poziva odgovarajucu proceduru za ispis
   * 
   * @param {Array} poruke 
   */
  function dodajPoruke(poruke){
    for(let i = 0; i < poruke.length; i++){
      let poruka = poruke[i];
      appendMessage(poruka.Tekst, poruka.IdPos, poruka.DatumVreme, "");
    }
    scrollToBottom();
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
   * wrapper funkcija koja trazi primaoca poruka od servera, a potom postavlja njegovo korisnicko ime
   * i poziva zahtev ka serveru koji trazi sve istoriju caskanja
   * 
   * @return Response
   */
  function callGetReceiver(){
    makeAjaxRequestToGetReceiver()
    .then((receiver) =>{
        authorUsername = authorFound.KorisnickoIme;
        receiverUsername = receiver;
        makeAjaxRequestForAllMessages();
        interval = setInterval(makeAjaxRequestForNewMessages, 1000);
      })
      .catch((error) =>{
        console.error(error);
      });
  }

  /**
   * proverava da li tekuci autor sesije ima pristiglih poruka dok se nalazi u cetu sa specificnom osobom
   * 
   * @returns Response
   */
  function makeAjaxRequestForNewMessages(){
    let newUrl = clearUrl("getNewMessages");
    let url = new URL(window.location.href);
    let params = new URLSearchParams(url.search);
    IdFrom = params.get("IdFrom");
    IdTo = params.get("IdTo");
    return new Promise(function (resolve, reject) {
      $.ajax({
        url: newUrl,
        method: "POST",
        data:{
          author: IdFrom,
          idfrom: IdTo,
        },
        success: function (response) {
          let jsonResponse = JSON.parse(response);
          let messages = jsonResponse;
          if(messages){
            dodajPoruke(messages);
            scrollToBottom();
            newUrl = clearUrl("setReadMessages");
            alert(newUrl);
            $.ajax({
              url: newUrl,
              method: "POST",
              data:{
                idFrom: IdFrom,
                idTo: IdTo,
              },
              success: function (response) {
              },
              error: function (error){
                reject(error)
              }
            });
          }
        },
        error: function (error) {
          console.log("Error:", error);
          reject(error);
        }
      });
    });
  }
  
  /**
   * salje zahtev serveru za proveru da li vec postoje poruke u ovom cetu, ako postoje ispisuje ih 
   * i tamo gde je on primalac postavlja status na procitano
   * 
   * @return Response
   */
  function initFunc() {
    // nadji autora sesije
    makeAjaxRequestForAuthor()
    .then(callGetReceiver)
    .catch(function (error) {
        console.error(error);
    });
    
  }

  /**
   * obradjuje url nalazi primaoca i od servera trazi njegovo korisnicko ime koje ce se koristiti prilikom ispisa poruka
   * 
   * @returns String(Response)
   */
  function makeAjaxRequestToGetReceiver() {
    let url = new URL(window.location.href);
    let params = new URLSearchParams(url.search);
    let IdTo = params.get("IdTo");
    let newUrl = clearUrl("getUsernameFromId");
    return new Promise((resolve, reject) => {
      $.ajax({
        url: newUrl,
        method: "POST",
        data: {
          idReceiver: IdTo,
        },
        success: function (response) {
          let receiver = JSON.parse(response);
          resolve(receiver);
        },
        error: function (error) {
          console.log("Error:", error);
          reject(error);
        },
      });
    });
  }

  /**
   * sluzi za obradu formata vremena, koristi se prilikom upisa
   * 
   * @param {String} timestamp 
   * @returns String
   */
  function formatTimestamp(timestamp) {
    let dateTime = timestamp.split(" ");
    let date = dateTime[0];
    let time = dateTime[1];
    let Ymd = date.split("-");
    Ymd = Ymd.reverse();
    return Ymd.join("-") + " " + time;
  }

  /**
   * funkcija koja kreira i dodaje novi element klase "message" u chat box
   * @param {String} username 
   * @param {String} content 
   * @param {integer} IdFrom 
   * @param {String} timestamp 
   */
  function appendMessage(content, IdFrom, timestamp, username) {
    let dt = formatTimestamp(timestamp);
    let messageDiv = $("<div>", { class: "message" });
    // deciding is receiver or sender (for view), null if-branch added while in developing phase
    if (authorFound == null) {
      if(username == "")username = authorUsername;
      messageDiv.addClass("your-message");
    }
    else {
      if (authorFound.IdKor == IdFrom) {
        if(username == "")username = authorUsername;
        messageDiv.addClass("your-message");
      }
      else {
        if(username == "")username = receiverUsername;
        messageDiv.addClass("other-message");
      }
    }
    const usernameSpan = $("<span>", { class: "usernameChat" }).text(username + ":");
    const brTag = $("<br>");
    const contentSpan = $("<span>", { class: "contentChat" }).text(content);
    const timestampSpan = $("<span>", { class: "timestampChat" }).text(dt);
    messageDiv.append(usernameSpan, brTag, contentSpan, timestampSpan);
    chatBox.append(messageDiv);
  }

  let isSending = false;

  /**
   * funkcija za slanje poruka
   * @returns Response
   */
  function sendMessage() {
    if (isSending || messageInput.val() === "") return;
    isSending = true;
    const message = messageInput.val();
    let url = new URL(window.location.href);
    let params = new URLSearchParams(url.search);
    IdFrom = params.get("IdFrom");
    IdTo = params.get("IdTo");

    $.ajax({
      url: "acceptMessage",
      method: "POST",
      data: {
        message: message,
        from: IdFrom,
        to: IdTo,
      },
      success: function (response) {
        let jsonResponse = JSON.parse(response);
        let user = jsonResponse[0];
        let messageAccept = jsonResponse[1];
        let timestamp = jsonResponse[2];
        appendMessage(messageAccept, IdFrom, timestamp, user.KorisnickoIme);
        messageInput.val(""); // Clear the input field
        messageInput.focus(); // Set focus back to the input field
        scrollToBottom();
        isSending = false;
      },
      error: function (error) {
        // Handle the error, if any
        isSending = false;
        console.log("Error:", error);
      },
    });
    return false;
  }


  /**
   * Osluskivac pritiska na dugme posalji
   */
  sendButton.on("click", function () {
    sendMessage();
  });

  /**
   * osluskivac za pritisak tastera 'Enter'
   */
  messageInput.on("keyup", function (event) {
    if (event.keyCode === 13) { // 13 is the key code for Enter
      sendMessage();
    }
  });

  /**
   * osluskivac za pritisak tastera 'esc' 
  */
  $("body").on("keyup", function (event) {
    if (event.keyCode == 27) {
      if (messageInput.is(':focus')) {
        messageInput.blur();
      }
    }
  })

  /**
   * osluskivac za pritisak tastora '/'
   */
  $("body").on("keyup", function (event) {
    if (event.keyCode == 191) {
      messageInput.focus();
    }
  })

});