/**
* Template Name: Yummy
* Updated: May 30 2023 with Bootstrap v5.3.0
* Template URL: https://bootstrapmade.com/yummy-bootstrap-restaurant-website-template/
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/

document.addEventListener('DOMContentLoaded', () => {
  "use strict";

  /**
   * Preloader
   */
  const preloader = document.querySelector('#preloader');
  if (preloader) {
    window.addEventListener('load', () => {
      preloader.remove();
    });
  }

  /**
   * Sticky header on scroll
   */
  const selectHeader = document.querySelector('#header');
  if (selectHeader) {
    document.addEventListener('scroll', () => {
      window.scrollY > 100 ? selectHeader.classList.add('sticked') : selectHeader.classList.remove('sticked');
    });
  }

  /**
   * Navbar links active state on scroll
   */
  let navbarlinks = document.querySelectorAll('#navbar a');

  function navbarlinksActive() {
    navbarlinks.forEach(navbarlink => {

      if (!navbarlink.hash) return;

      let section = document.querySelector(navbarlink.hash);
      if (!section) return;

      let position = window.scrollY + 200;

      if (position >= section.offsetTop && position <= (section.offsetTop + section.offsetHeight)) {
        navbarlink.classList.add('active');
      } else {
        navbarlink.classList.remove('active');
      }
    })
  }
  window.addEventListener('load', navbarlinksActive);
  document.addEventListener('scroll', navbarlinksActive);

  /**
   * Mobile nav toggle
   */
  const mobileNavShow = document.querySelector('.mobile-nav-show');
  const mobileNavHide = document.querySelector('.mobile-nav-hide');

  document.querySelectorAll('.mobile-nav-toggle').forEach(el => {
    el.addEventListener('click', function (event) {
      event.preventDefault();
      mobileNavToogle();
    })
  });

  function mobileNavToogle() {
    document.querySelector('body').classList.toggle('mobile-nav-active');
    mobileNavShow.classList.toggle('d-none');
    mobileNavHide.classList.toggle('d-none');
  }

  /**
   * Hide mobile nav on same-page/hash links
   */
  document.querySelectorAll('#navbar a').forEach(navbarlink => {

    if (!navbarlink.hash) return;

    let section = document.querySelector(navbarlink.hash);
    if (!section) return;

    navbarlink.addEventListener('click', () => {
      if (document.querySelector('.mobile-nav-active')) {
        mobileNavToogle();
      }
    });

  });

  /**
   * Toggle mobile nav dropdowns
   */
  const navDropdowns = document.querySelectorAll('.navbar .dropdown > a');

  navDropdowns.forEach(el => {
    el.addEventListener('click', function (event) {
      if (document.querySelector('.mobile-nav-active')) {
        event.preventDefault();
        this.classList.toggle('active');
        this.nextElementSibling.classList.toggle('dropdown-active');

        let dropDownIndicator = this.querySelector('.dropdown-indicator');
        dropDownIndicator.classList.toggle('bi-chevron-up');
        dropDownIndicator.classList.toggle('bi-chevron-down');
      }
    })
  });

  /**
   * Scroll top button
   */
  const scrollTop = document.querySelector('.scroll-top');
  if (scrollTop) {
    const togglescrollTop = function () {
      window.scrollY > 100 ? scrollTop.classList.add('active') : scrollTop.classList.remove('active');
    }
    window.addEventListener('load', togglescrollTop);
    document.addEventListener('scroll', togglescrollTop);
    scrollTop.addEventListener('click', window.scrollTo({
      top: 0,
      behavior: 'smooth'
    }));
  }

  /**
   * Initiate pURE cOUNTER
   */
  new PureCounter();

  /**
   * Init swiper slider with 1 slide at once in desktop view
   */
  new Swiper('.slides-1', {
    speed: 600,
    loop: true,
    autoplay: {
      delay: 5000,
      disableOnInteraction: false
    },
    slidesPerView: 'auto',
    pagination: {
      el: '.swiper-pagination',
      type: 'bullets',
      clickable: true
    },
    navigation: {
      nextEl: '.swiper-button-next',
      prevEl: '.swiper-button-prev',
    }
  });

  /**
   * Init swiper slider with 3 slides at once in desktop view
   */
  new Swiper('.slides-3', {
    speed: 600,
    loop: true,
    autoplay: {
      delay: 5000,
      disableOnInteraction: false
    },
    slidesPerView: 'auto',
    pagination: {
      el: '.swiper-pagination',
      type: 'bullets',
      clickable: true
    },
    navigation: {
      nextEl: '.swiper-button-next',
      prevEl: '.swiper-button-prev',
    },
    breakpoints: {
      320: {
        slidesPerView: 1,
        spaceBetween: 40
      },

      1200: {
        slidesPerView: 3,
      }
    }
  });

  /**
   * Gallery Slider
   */
  new Swiper('.gallery-slider', {
    speed: 400,
    loop: true,
    centeredSlides: true,
    autoplay: {
      delay: 5000,
      disableOnInteraction: false
    },
    slidesPerView: 'auto',
    pagination: {
      el: '.swiper-pagination',
      type: 'bullets',
      clickable: true
    },
    breakpoints: {
      320: {
        slidesPerView: 1,
        spaceBetween: 20
      },
      640: {
        slidesPerView: 3,
        spaceBetween: 20
      },
      992: {
        slidesPerView: 5,
        spaceBetween: 20
      }
    }
  });

  /**
   * Animation on scroll function and init
   */
  function aos_init() {
    AOS.init({
      duration: 1000,
      easing: 'ease-in-out',
      once: true,
      mirror: false
    });
  }
  window.addEventListener('load', () => {

    /**
     * Initiate glightbox
     */
    const glightbox = GLightbox({
      selector: '.glightbox'
    });

    aos_init();
  });

  $("#deoIndexaPostavke").on("click", function () {
    let urlParts = window.location.href.split("/");
    let index = urlParts.length - 1;
    while (index > 0 && urlParts[index] != "ArtGallery") {
      urlParts.pop();
      index--;
    }
    urlParts.push("index.html#novePostavke");
    window.location.href = urlParts.join("/");
  });

  $("#deoIndexaUmetnici").on("click", function () {
    let urlParts = window.location.href.split("/");
    let index = urlParts.length - 1;
    while (index > 0 && urlParts[index] != "ArtGallery") {
      urlParts.pop();
      index--;
    }
    urlParts.push("index.html#noviUmetnici");
    window.location.href = urlParts.join("/");
  });

  $("#deoIndexaPonude").on("click", function () {
    let urlParts = window.location.href.split("/");
    let index = urlParts.length - 1;
    while (index > 0 && urlParts[index] != "ArtGallery") {
      urlParts.pop();
      index--;
    }
    urlParts.push("index.html#najnovijePonude");
    window.location.href = urlParts.join("/");
  });

  $("#deoIndexaPostavkeEng").on("click", function () {
    let urlParts = window.location.href.split("/");
    let index = urlParts.length - 1;
    while (index > 0 && urlParts[index] != "ArtGallery") {
      urlParts.pop();
      index--;
    }
    urlParts.push("index_engleski.html#novePostavke");
    window.location.href = urlParts.join("/");
  });

  $("#deoIndexaUmetniciEng").on("click", function () {
    let urlParts = window.location.href.split("/");
    let index = urlParts.length - 1;
    while (index > 0 && urlParts[index] != "ArtGallery") {
      urlParts.pop();
      index--;
    }
    urlParts.push("index_engleski.html#noviUmetnici");
    window.location.href = urlParts.join("/");
  });

  $("#deoIndexaPonudeEng").on("click", function () {
    let urlParts = window.location.href.split("/");
    let index = urlParts.length - 1;
    while (index > 0 && urlParts[index] != "ArtGallery") {
      urlParts.pop();
      index--;
    }
    urlParts.push("index_engleski.html#najnovijePonude");
    window.location.href = urlParts.join("/");
  });

  let language;

  // set red colour text in navbar in the sections of the website
  // that represent a breadcrumb
  function initNavbar() {
    const navbar = document.getElementById('navbar');
    const red_colour = "var(--color-primary)";

    function colourSerbianLanguage() {
      // color Serbian language in navbar
      // <a href="./index.html">Српски</a>
      navbar.children[0].children[5].children[1].children[0].children[0].style.color = red_colour;
    }

    function colourEnglishLanguage() {
      // color English language in navbar
      // <a href="./index_engleski.html">English</a>
      navbar.children[0].children[5].children[1].children[1].children[0].style.color = red_colour;
    }

    let relative_url = window.location.pathname;

    // handle separately in index/home pages from other pages
    if (!relative_url.includes('/pages/')) {
      if (relative_url.includes('index.html')) {  // in Serbian
        // colour Home
        navbar.children[0].children[0].children[0].style.color = red_colour;
        // colour language
        language = "serbian";
        colourSerbianLanguage();
      }
      // in English
      else if (relative_url.includes('index_engleski.html')) {
        // colour Home
        navbar.children[0].children[0].children[0].style.color = red_colour;
        // colour language
        language = "english";
        colourEnglishLanguage();
      }

      return;
    }
    // else: not in index/home page
    // Colour the subpages of the url in navbar:

    // remove `/pages/` from the url
    relative_url = relative_url.slice(
      relative_url.indexOf('/pages/')
      + "/pages/".length
    );

    const subpages = relative_url.split('/');
    language = subpages.shift();

    if (language === "engleski") {
      colourEnglishLanguage();
    } else if (language === "srpski") {
      colourSerbianLanguage();
    }

    const this_page = subpages.pop();
    // <a href="./david.html">David</a>
    const this_page_a_tag = navbar.querySelector(`a[href="./${this_page}"]`);

    // colour the path to this page's a_tag
    for (let cur = this_page_a_tag.parentNode;
      cur !== navbar;
      cur = cur.parentNode) {
      // colour only a tags that are direct children of li tags
      if (cur.tagName !== "LI") { continue; }
      const a_tag = cur.children[0];
      if (a_tag.tagName !== "A") { continue; }
      a_tag.style.color = red_colour;
    }
  }

  initNavbar();

  let currURL = window.location.href;
  let arrCurrURL = currURL.split("/");
  if (arrCurrURL[arrCurrURL.length - 1] == "index.html") {
    language = "srpski";
  } else if (arrCurrURL[arrCurrURL.length - 1] == "index_engleski.html") {
    language = "engleski";
  }

  language = (language === "srpski") ? "serbian" : "english";
  localStorage.setItem("language", language);

  localStorage.setItem("currentArtwork", $("#offer-artwork").text());

  let serbianToEnglishArtworkMappings = {
    "Пољубац": "The Kiss",
    "Мона Лиза": "Mona Lisa",
    "Тајна вечера": "The Last Supper",
    "Сеоба Срба": "The Migration of the Serbs",
    "Сунцокрети": "Sunflowers",

    "Давид": "David",
    "Мислилац": "The Thinker",
    "Кип слободе": "The Statue of Liberty",
    "Мала Сирена": "The Little Mermaid",
    "Велика Сфинга": "The Great Sphinx",

    "Бекство из Шошенка": "The Shawshank Redemption",
    "Кум": "The Godfather",
    "12 Љутих Мушкараца": "12 Angry Men",
    "Престиж": "The Prestige",
    "Америчка Лепота": "American Beauty"
  };

  let serbianToEnglishUserMappings = {
    "Павле Шаренац": "Pavle Sarenac",
    "Александар Раденковић": "Aleksandar Radenkovic"
  };

  function translateArtworkSerbToEng(artwork) {
    for (let key in serbianToEnglishArtworkMappings) {
      if (serbianToEnglishArtworkMappings.hasOwnProperty(key)) {
        let value = serbianToEnglishArtworkMappings[key];
        if (key == artwork) {
          return value;
        }
      }
    }
  }

  function translateArtworkEngToSerb(artwork) {
    for (let key in serbianToEnglishArtworkMappings) {
      if (serbianToEnglishArtworkMappings.hasOwnProperty(key)) {
        let value = serbianToEnglishArtworkMappings[key];
        if (value === artwork) {
          return key;
        }
      }
    }
  }

  function translateUserSerbToEng(user) {
    for (let key in serbianToEnglishUserMappings) {
      if (serbianToEnglishUserMappings.hasOwnProperty(key)) {
        let value = serbianToEnglishUserMappings[key];
        if (key == user) {
          return value;
        }
      }
    }
  }

  function translateUserEngToSerb(user) {
    for (let key in serbianToEnglishUserMappings) {
      if (serbianToEnglishUserMappings.hasOwnProperty(key)) {
        let value = serbianToEnglishUserMappings[key];
        if (value == user) {
          return key;
        }
      }
    }
  }

  function convertRSDtoUSD(dinars) {
    return parseFloat(dinars * 0.0093);
  }

  function convertUSDtoRSD(dolars) {
    return parseInt(dolars * 107.63);
  }

  // Popunjavanje localStorage sa korisnicima.
  let users = [];
  users.push(
    {
      name: "Павле Шаренац",
      engName: "Pavle Sarenac",
      email: "sarenac.pavle@gmail.com",
      password: "pavle123"
    },
    {
      name: "Александар Раденковић",
      engName: "Aleksandar Radenkovic",
      email: "sale@gmail.com",
      password: "sale123"
    }
  );
  localStorage.setItem("users", JSON.stringify(users));

  function check(name, email) {
    if (language == "english") {
      name = translateUserEngToSerb(name);
    }
    let allUsers = JSON.parse(localStorage.getItem("users"));
    for (let i = 0; i < allUsers.length; i++) {
      if (name == allUsers[i].name && email == allUsers[i].email) {
        return true;
      }
    }
    return false;
  }

  function checkOfferExists(offers, artwork, offer) {
    if (language == "english") {
      artwork = translateArtworkEngToSerb(artwork);
      offer = convertUSDtoRSD(offer);
    }
    for (let i = 0; i < offers.length; i++) {
      if (offers[i].artwork == artwork && offers[i].userOffer == offer) {
        return true;
      }
    }
    return false;
  }

  // Ostavljanje ponude.
  $("#offer-btn").on("click", function () {
    $("#nameError").text("");
    $("#emailError").text("");
    $("#phoneError").text("");
    $("#offerError").text("");
    $("#poruka").text("");

    let name = $("#name").val();
    let email = $("#email").val();
    let phone = $("#phone").val();
    let offer = $("#offer").val();

    let somethingMissing = false;

    if (name == "" || email == "" || phone == "" || offer == "") {
      somethingMissing = true;
    }

    if (name == "") {
      if (language == "serbian") {
        $("#nameError").text("Морате унети име!");
      } else {
        $("#nameError").text("You didn't give us your name!");
      }
    }
    if (email == "") {
      if (language == "serbian") {
        $("#emailError").text("Морате унети мејл адресу!");
      } else {
        $("#emailError").text("You didn't give us your email!");
      }
    }
    if (phone == "") {
      if (language == "serbian") {
        $("#phoneError").text("Морате унети број телефона!");
      } else {
        $("#phoneError").text("You didn't give us your phone number!");
      }
    }
    if (offer == "") {
      if (language == "serbian") {
        $("#offerError").text("Морате унети понуду!");
      } else {
        $("#offerError").text("You didn't give us your offer!");
      }
    }

    if (somethingMissing) {
      return;
    }

    let errorPresent = false;

    if (language == "serbian") {
      if (/^[абвгдђежзијклљмнњопрстћуфхцчџшАБВГДЂЕЖЗИЈКЛЉМНЊОПРСТЋУФХЦЧЏШ]+[ ]*[абвгдђежзијклљмнњопрстћуфхцчџшАБВГДЂЕЖЗИЈКЛЉМНЊОПРСТЋУФХЦЧЏШ]+$/.test(name) == false) {
        $("#nameError").text("Само мала и велика слова!");
        errorPresent = true;
      }
    } else {
      if (/^[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]+[ ]*[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]+$/.test(name) == false) {
        $("#nameError").text("Only letters of the alphabet!");
        errorPresent = true;
      }
    }


    if (/^[a-z]+[\.]*[a-z]+@[a-z]+\.[a-z]{2,3}$/.test(email) == false) {
      if (language == "serbian") {
        $("#emailError").text("Мејл је у лошем формату!");
        errorPresent = true;
      } else {
        $("#emailError").text("Bad mail format!");
        errorPresent = true;
      }
    }

    if (/^\+381 6\d \d{3}-\d{3,4}$/.test(phone) == false) {
      if (language == "serbian") {
        $("#phoneError").text("Мора формат +381 6# ###-###[#]!");
        errorPresent = true;
      } else {
        $("#phoneError").text("Format is +381 6# ###-###[#]!");
        errorPresent = true;
      }
    }

    if (/^[0-9]+$/.test(offer) == false) {
      if (language == "serbian") {
        $("#offerError").text("Само цифре унесите!");
        errorPresent = true;
      } else {
        $("#offerError").text("Only numbers!");
        errorPresent = true;
      }
    }

    if (errorPresent) {
      return;
    }

    if (check(name, email) == false) {
      if (language == "serbian") {
        $("#poruka").text("Нисте у систему!");
        errorPresent = true;
      } else {
        $("#poruka").text("You are not in the system!");
        errorPresent = true;
      }
    }

    if (errorPresent) {
      return;
    }

    let offers;

    if (localStorage.getItem("offers") == null) {
      offers = [];
    } else {
      offers = JSON.parse(localStorage.getItem("offers"));
    }

    if (checkOfferExists(offers, $("#offer-artwork").text(), parseInt($("#offer").val()))) {
      if (language == "serbian") {
        $("#offerError").text("Већ постоји ова понуда!");
      } else {
        $("#offerError").text("This offer already exists!");
      }
      return;
    }

    offers.push({
      artwork: language == "serbian" ? $("#offer-artwork").text() : translateArtworkEngToSerb($("#offer-artwork").text()),
      userName: language == "serbian" ? $("#name").val() : translateUserEngToSerb($("#name").val()),
      userMail: $("#email").val(),
      userPhone: $("#phone").val(),
      userOffer: language == "serbian" ? parseInt($("#offer").val()) : convertUSDtoRSD(parseInt($("#offer").val())),
      userMessage: $("#message").val()
    });

    localStorage.setItem("offers", JSON.stringify(offers));

    updateAllOffers();
  });

  function updateAllOffers() {
    if (localStorage.getItem("offers") == null) return;
    $("#offers").empty();
    let allOffers = JSON.parse(localStorage.getItem("offers"));
    let currentArtwork = localStorage.getItem("language") === "english" ? translateArtworkEngToSerb(localStorage.getItem("currentArtwork")) : localStorage.getItem("currentArtwork");

    let noOffers = true;

    for (let i = allOffers.length - 1; i >= 0; i--) {
      if (i == allOffers.length - 1) {
        if (localStorage.getItem("language") == "serbian") {
          $("#offers").append("<div class='section-header text-center'><p>Све понуде<span> за ову уметнину</span></p></div>")
            .append("<div class='alert alert-light' role='alert'><div class= 'row' ><div class='col text-center'><h3>Име</h3></div><div class= 'col text-center'><h3>Понуда</h3>  </div >  <div class='col text-center'>      <h3>Порука</h3>  </div></div > </div > ");
        } else {
          $("#offers").append("<div class='section-header text-center'><p>All offers<span> for this artwork</span></p></div>")
            .append("<div class='alert alert-light' role='alert'><div class= 'row' ><div class='col text-center'><h3>Name</h3></div><div class= 'col text-center'><h3>Offer</h3>  </div >  <div class='col text-center'>      <h3>Note</h3>  </div></div > </div > ");
        }
      }
      if (allOffers[i].artwork == currentArtwork) {
        noOffers = false;
        if (localStorage.getItem("language") == "serbian") {
          $("#offers").append("<div class='alert alert-light' role='alert'><div class='row'>" +
            "<div class='col text-center'>" + allOffers[i].userName + "</div>"
            + "<div class='col text-center'>" + allOffers[i].userOffer + " РСД</div>"
            + "<div class='col text-center long-text'>" + allOffers[i].userMessage + "</div></div></div>"
          );
        } else {
          $("#offers").append("<div class='alert alert-light' role='alert'><div class='row'>" +
            "<div class='col text-center'>" + translateUserSerbToEng(allOffers[i].userName) + "</div>"
            + "<div class='col text-center'>" + convertRSDtoUSD(allOffers[i].userOffer).toFixed(2) + " USD</div>"
            + "<div class='col text-center long-text'>" + allOffers[i].userMessage + "</div></div></div>"
          );
        }
      }
    }

    if (noOffers) {
      $("#offers").empty();
    }
  }

  updateAllOffers();

  function threeNewestOffers() {
    if (localStorage.getItem("offers") == null) return;
    $("#newestThreeOffers").empty();
    let allOffers = JSON.parse(localStorage.getItem("offers"));

    let cnt = 0;
    for (let i = allOffers.length - 1; i >= 0 && cnt < 3; i--) {
      if (i == allOffers.length - 1) {
        if (localStorage.getItem("language") == "serbian") {
          $("#newestThreeOffers").append("<div class='section-header text-center'><p>Најновије понуде<span> у нашој галерији</span></p></div>")
            .append("<div class='alert alert-light' role='alert'><div class= 'row' ><div class='col text-center'><h3>Име</h3></div><div class= 'col text-center'><h3>Понуда</h3>  </div >  <div class='col text-center'>      <h3>Порука</h3>  </div><div class='col text-center'><h3>Уметнина</h3></div></div > </div > ");
        } else {
          $("#newestThreeOffers").append("<div class='section-header text-center'><p>Latest offers<span> in our gallery</span></p></div>")
            .append("<div class='alert alert-light' role='alert'><div class= 'row' ><div class='col text-center'><h3>Name</h3></div><div class= 'col text-center'><h3>Offer</h3>  </div >  <div class='col text-center'>      <h3>Note</h3>  </div><div class='col text-center'><h3>Artwork</h3></div></div > </div > ");
        }
      }
      if (localStorage.getItem("language") == "serbian") {
        $("#newestThreeOffers").append("<div class='alert alert-light' role='alert'><div class='row'>" +
          "<div class='col text-center'>" + allOffers[i].userName + "</div>"
          + "<div class='col text-center'>" + allOffers[i].userOffer + " РСД</div>"
          + "<div class='col text-center long-text'>" + allOffers[i].userMessage + "</div>" +
          "<div class='col text-center'>" + allOffers[i].artwork +
          "</div></div></div>");
      } else {
        $("#newestThreeOffers").append("<div class='alert alert-light' role='alert'><div class='row'>" +
          "<div class='col text-center'>" + translateUserSerbToEng(allOffers[i].userName) + "</div>"
          + "<div class='col text-center'>" + convertRSDtoUSD(allOffers[i].userOffer).toFixed(2) + " USD</div>"
          + "<div class='col text-center long-text'>" + allOffers[i].userMessage + "</div>" +
          "<div class='col text-center'>" + translateArtworkSerbToEng(allOffers[i].artwork) +
          "</div></div></div>");
      }
      cnt++;
    }
  }

  threeNewestOffers();

  let loginDiv;

  $("#login-div").on("click", "#login-btn", function () {
    $("#emailLoginError").text("");
    $("#passwordLoginError").text("");

    let email = $("#emailLogin").val();
    let password = $("#passwordLogin").val();

    let somethingMissing = false;

    if (email == "" || password == "") {
      somethingMissing = true;
    }

    if (email == "") {
      if (language == "serbian") {
        $("#emailLoginError").text("Морате унети мејл!");
      } else {
        $("#emailLoginError").text("You didn't give us your mail!");
      }
    }
    if (password == "") {
      if (language == "serbian") {
        $("#passwordLoginError").text("Морате унети лозинку!");
      } else {
        $("#passwordLoginError").text("You didn't give us your password!");
      }
    }

    let userFound = false;
    let allUsers = JSON.parse(localStorage.getItem("users"));

    for (let i = 0; i < allUsers.length; i++) {
      if (allUsers[i].email == email) {
        if (allUsers[i].password == password) {
          userFound = true;
          localStorage.setItem("loggedInUser", email);
          loginDiv = $("#login-div").html();
          $("#login-div").empty();
          showUsersOffers(email);
        }
      }
    }

    if (userFound == false) {
      if (language == "serbian") {
        $("#passwordLoginError").text("Погрешни подаци!");
      } else {
        $("#passwordLoginError").text("Wrong data!");
      }
    }
  });

  function showUsersOffers(email) {
    if (localStorage.getItem("offers") == null) {
      return;
    }
    let allOffers = JSON.parse(localStorage.getItem("offers"));
    for (let i = allOffers.length - 1; i >= 0; i--) {
      if (i == allOffers.length - 1) {
        if (localStorage.getItem("language") == "serbian") {
          $("#offers").append("<div class='section-header text-center'><p><span>Све Ваше понуде</span></p></div>")
            .append("<div class='alert alert-light' role='alert'><div class= 'row' ><div class='col text-center'><h3>Име</h3></div><div class= 'col text-center'><h3>Понуда</h3>  </div >  <div class='col text-center'>      <h3>Порука</h3>  </div><div class='col text-center'>      <h3>Обриши</h3>  </div></div > </div > ");
        } else {
          $("#offers").append("<div class='section-header text-center'><p><span>All of your offers</span></p></div>")
            .append("<div class='alert alert-light' role='alert'><div class= 'row' ><div class='col text-center'><h3>Name</h3></div><div class= 'col text-center'><h3>Offer</h3>  </div >  <div class='col text-center'>      <h3>Note</h3>  </div><div class='col text-center'>      <h3>Delete</h3>  </div></div > </div > ");
        }
      }
      if (allOffers[i].userMail == email) {
        if (localStorage.getItem("language") == "serbian") {
          $("#offers").append("<div class='alert alert-light' role='alert'><div class='row'>" +
            "<div class='col text-center'>" + allOffers[i].userName + "</div>"
            + "<div class='col text-center'>" + allOffers[i].userOffer + " РСД</div>"
            + "<div class='col text-center long-text'>" + allOffers[i].userMessage + "</div>" +
            "<div class='col text-center'><button id='deleteOffer' class='btn btn-dark'>Бриши понуду " + i + "</button>" +
            "</div></div></div>"
          );
        } else {
          $("#offers").append("<div class='alert alert-light' role='alert'><div class='row'>" +
            "<div class='col text-center'>" + translateUserSerbToEng(allOffers[i].userName) + "</div>"
            + "<div class='col text-center'>" + convertRSDtoUSD(allOffers[i].userOffer).toFixed(2) + " USD</div>"
            + "<div class='col text-center long-text'>" + allOffers[i].userMessage + "</div>" +
            "<div class='col text-center'><button id='deleteOffer' class='btn btn-dark'>Delete offer " + i + "</button>" +
            "</div></div></div>"
          );
        }
      }
    }
    $("#offers").append("<div class='text-center'><button class='btn btn-dark' id='logout-btn'>Logout</button></div>");
  }

  $("#offers").on("click", "#logout-btn", function () {
    localStorage.removeItem("loggedInUser");
    $("#offers").empty();
    $("#login-div").html(loginDiv);
  });

  $("#offers").on("click", "#deleteOffer", function () {
    let offerId = parseInt($(this).text().split(" ")[2]);
    let allOffers = JSON.parse(localStorage.getItem("offers"));
    let newOffers = [];
    for (let i = 0; i < allOffers.length; i++) {
      if (i != offerId) {
        newOffers.push(allOffers[i]);
      }
    }
    localStorage.setItem("offers", JSON.stringify(newOffers));
    $("#offers").empty();
    showUsersOffers(localStorage.getItem("loggedInUser"));
  });

});