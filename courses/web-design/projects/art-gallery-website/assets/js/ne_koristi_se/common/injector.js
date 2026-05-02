
class Injector {

    #data = null;

    constructor(data=new Map()) {
        this.#data = data;
    }

    /**
     * 
     * @param {Map} data 
     */
    init(data) {
        this.#data = data;
    }

    reinit() {
        this.#clear();
    }

    #clear() {
        if (this.#data !== undefined && this.#data !== null) {
            this.#data.clear();
        }
    }

    bind(key, value) {
        this.#data.set(key, value);
    }

    /**
     * 
     * @param {string} key 
     * @returns Returns last binded value to given key if
     *          key exists, else returns undefined.
     */
    fetch(key) {
        return this.#data.get(key);
    }

    injectAll(clear_when_done=true) {
        if (this.#data === undefined || this.#data === null) {
            return
        }

        for (let [key, val] of this.#data) {
            this.inject(key, val);
        }

        if (clear_when_done) {
            this.#clear();
        }
    }

    inject(key, val) {
        if (key.startsWith('data-inner')) {
            this.#injectInnerHTML(key, val);
        } else if (key.startsWith('data-append')) {
            this.#injectAppendChild(key, val);
        } else if (key.startsWith('data-txt')) {
            this.#injectTextContent(key, val);
        } else {
            console.log(`Invalid key: ${key}`);
        }
    }

    #injectInnerHTML(key, val) {
        for (let el of document.getElementsByClassName(key)) {
            el.innerHTML += val;
        }
    }

    #injectAppendChild(key, child) {
        for (let el of document.getElementsByClassName(key)) {
            el.appendChild(child);
        }
    }

    #injectTextContent(key, val) {
        for (let el of document.getElementsByClassName(key)) {
            el.textContent += val;
        }
    }

};
