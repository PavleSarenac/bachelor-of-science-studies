document.addEventListener("DOMContentLoaded", () => {
    // new TemplateExtenderArtPiece().init(
    //     8,
    //     "Art Piece Description",
    //     "Artist Name",
    //     "Artist Description",
    //     "25000000",
    //     "181"
    // );
});


class TemplateExtenderArtPiece {

    static snakeCaseToCapitilizedWords(snake_case) {
        let words = snake_case.split('_');
        words = words.map((word) => {
            return word.charAt(0).toUpperCase() + word.slice(1);
        });

        const space_separated_words = words.join(' ');
        return space_separated_words;
    }

    static capitilizedWordsToSnakeCase(capitilized) {
        let words = capitilized.split(' ');
        words = words.map((word) => {
            return word.charAt(0).toLowerCase() + word.slice(1);
        });

        const snake_case = words.join('_');
        return snake_case;
    }

    #callers_path_to_root = ""
    #document = null;

    constructor(callers_path_to_root, document_) {
        this.#callers_path_to_root = callers_path_to_root;
        this.#document = document_;
    }

    init(
        gallery_num_imgs,
        art_piece_description,
        artists_name,
        artists_description,
        estimated_value,
        estimated_age
    ) {
        console.log(`Resolving base template data.`);

        // fetch key from URL with which the template data is fetched
        const page_id = this.getPageId();
        const injector = new Injector();

        this.injectTemplateData(
            injector,
            page_id,
            gallery_num_imgs,
            art_piece_description,
            artists_name,
            artists_description,
            estimated_value,
            estimated_age
        );

        injector.reinit();

        console.log(`Resolved base template data successfuly.`);
    }


    // ==============================================================
    // Fetching Template Data


    getPageId() {
        // endpoint = artwork/paintings/der_kuss
        const endpoint = window.location.pathname;
        // page_name = der_kuss
        const page_name = endpoint.split('/').pop();
        return page_name;
    }

    /**
     * Fetches the title for a given page.
     * 
     * @param {string} page_id Identificator of the page whose title
     *                         is to be returned.
     * @returns Title for the current page.
     */
    getArtPieceName(page_id) {
        const page_name = page_id.replace('.html', '');

        // capitilize the page name / page_id
        // der_kuss => Der Kuss
        const art_piece_name = TemplateExtenderArtPiece
                               .snakeCaseToCapitilizedWords(page_name);
        return art_piece_name;
    }

    getRelativeURL() {
        const relative_url = window.location.pathname.slice(
            window.location.pathname.indexOf('/pages/')
        );

        return relative_url;
    }


    // ==============================================================
    // Injecting Template Data:


    injectTemplateData(
        injector,
        page_id,
        gallery_num_imgs,
        art_piece_description,
        artists_name,
        artists_description,
        estimated_value,
        estimated_age
    ) {
        const art_piece_name = this.getArtPieceName(page_id);
        const relative_url = this.getRelativeURL();
        const relative_imgs_folder = this.getRelativeImagesFolder(relative_url);

        this.initTitle(injector, art_piece_name);
        this.initBreadcrumbs(relative_url);
        // inject art piece name wherever it is used
        this.injectArtPieceName(injector, art_piece_name);
        this.initArtPieceHeroImage(injector, relative_imgs_folder);

        this.initArtPieceGallery(injector, relative_imgs_folder, gallery_num_imgs);
        this.initArtPieceDescription(injector, art_piece_description);
        this.injectArtistHeroSection(injector, artists_name, artists_description);

        this.initEstimatedValue(estimated_value);
        this.initEstimatedAge(estimated_age);

        injector.injectAll(true);
    }

    initTitle(injector, art_piece_name) {
        injector.inject("data-txt-title", art_piece_name);
    }

    injectArtPieceName(injector, art_piece_name) {
        injector.inject("data-inner-art-piece-name", art_piece_name);
    }

    initBreadcrumbs(relative_url) {
        // relative_url = art_pieces/paintings/der_kuss
        // art_pieces_rel_url = paintings/der_kuss.html
        const art_pieces_rel_url = relative_url.slice(
            relative_url.indexOf('art_pieces/') + 'art_pieces/'.length
        );
        const folder_names = art_pieces_rel_url.replace('.html', '').split('/');
        const this_page = folder_names.pop();
        let breadcrumbs = this.#document.getElementById('breadcrumbs-list');
        // breadcrumbs.innerHTML = "";

        let doc = this.#document;
        function createBreadCrumb(path_url, path_display_txt) {
            let breadcrumb = doc.createElement('li');
            let link = doc.createElement('a');
            link.href = path_url;
            link.textContent = path_display_txt;
            breadcrumb.appendChild(link);

            return breadcrumb;
        }

        let cur_rel_path = "../../pages/art_pieces"
        for (const folder_name of folder_names) {
            cur_rel_path += '/' + folder_name;
            const subpage = TemplateExtenderArtPiece
                                .snakeCaseToCapitilizedWords(folder_name);
            const breadcrumb = createBreadCrumb(cur_rel_path + '.html',
                                                subpage);
            breadcrumbs.appendChild(breadcrumb);
        }

        let breadcrumb = this.#document.createElement('li');
        breadcrumb.textContent = TemplateExtenderArtPiece
                                     .snakeCaseToCapitilizedWords(this_page);
        breadcrumbs.appendChild(breadcrumb);

        this.#document.getElementById('art-piece-name').textContent
            = TemplateExtenderArtPiece
                  .snakeCaseToCapitilizedWords(this_page);
    }

    initArtPieceHeroImage(injector, relative_imgs_folder) {
        // relative_imgs_folder = ../../../assets/img/paintings/der_kuss
        // relative_url = ../../../assets/img/paintings/der_kuss/der_kuss.jpg
        const relative_url = relative_imgs_folder
                           + relative_imgs_folder
                                .substring(relative_imgs_folder.lastIndexOf('/'))
                           + '.jpg';
        injector.inject(
            "data-inner-art-piece-hero-img",
              '<img id="prvaSlika" src="'
            + relative_url
            + '" class="img-fluid" alt="" data-aos="zoom-out" data-aos-delay="300">'
        );
        console.log(`Art img: ${relative_url}`);
    }

    getRelativeImagesFolder(relative_page_url) {
        // returns ../../img/paintings/der_kuss
        return this.#callers_path_to_root
             + relative_page_url.replace('.html', '')
                                .replace('pages/', 'assets/img/');
    }

    initArtPieceGallery(injector, relative_imgs_folder, gallery_num_imgs) {
        // relative_imgs_folder = ../../img/paintings/der_kuss
        let entire_html = "";
        for (let i = 1; i <= gallery_num_imgs; ++i) {
            const url = relative_imgs_folder + '/gallery/gallery-' + i.toString() + '.jpg';
            entire_html += '<div class="swiper-slide"><a class="glightbox" data-gallery="images-gallery" href="'
                        +  url + '"><img src="' + url + '" class="img-fluid" alt=""></a></div>';
            // injector.bind(
            //     "data-inner-art-piece-gallery",

            //     '<div class="swiper-slide"><a class="glightbox" data-gallery="images-gallery" href="'
            //     + url + '"><img src="' + url + '" class="img-fluid" alt=""></a></div>'
            // );
        }

        injector.inject("data-inner-art-piece-gallery", entire_html);
    }

    initArtPieceDescription(injector, description) {
        injector.inject("data-txt-art-piece-description", description);
    }

    injectArtistHeroSection(injector, artists_name, description) {
        injector.inject("data-inner-artist-name", artists_name);
        injector.inject("data-txt-artist-description", description);

        // set author image
        const relative_url = this.#callers_path_to_root
                           + "/assets/img/artists/"
                           + TemplateExtenderArtPiece.capitilizedWordsToSnakeCase(
                                artists_name
                             )
                           + '.jpg';
        injector.inject(
            "data-inner-artist-hero-img",
              '<img id="prvaSlika" src="'
            + relative_url
            + '" class="img-fluid" alt="" data-aos="zoom-out" data-aos-delay="300">'
        );
        console.log(`Artist img: ${relative_url}`);
    }

    initEstimatedValue(estimated_value) {
        this.#document.getElementById('estimated-val')
                .setAttribute('data-purecounter-end', estimated_value);
    }

    initEstimatedAge(estimated_age) {
        this.#document.getElementById('estimated-age')
                .setAttribute('data-purecounter-end', estimated_age);
    }

}
