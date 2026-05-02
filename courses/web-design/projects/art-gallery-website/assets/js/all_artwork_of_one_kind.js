document.addEventListener("DOMContentLoaded", () => {
	initWindow();
	initSortByButtons();
	initSearchbar();
});


function initWindow() {
	window.addEventListener("load", () => {
		// when all content has loaded sort by name
		// document.getElementById("sort-by-name").click();
	});
}


function initSortByButtons() {
	// do nothing when no sort is clicked, if it was sorted leave it
	// exists only not to waste resources when search is performed
	//document.getElementById("sort-no-sort").addEventListener("click", () => {

	//});

	document.getElementById("sort-by-name").addEventListener("click", () => {
		sort(cmpArtworkByName);
	});

	document.getElementById("sort-by-artist").addEventListener("click", () => {
		sort(cmpArtworkByArtistsName);
	});
}


/**
 * Displays artwork in sorted order determined by given
 * criterion if any.
 *
 * @param {int} sort_criterion - 0 for none,
 *					    - 1 for by name descending
 *						- 2 for by artists name descending		
*/
function sort(sort_criterion) {
	let artwork = document.getElementById("menu-items");

	let items = Array.from(document.querySelectorAll("#menu-items .menu-item"));
	items.sort(sort_criterion);  // desc.
	
	//items.push.apply(items, Array.from(document.querySelectorAll("#menu-items .menu-item:not(item-active)")));

	artwork.innerHTML = "";
	items.forEach((item) => {
		artwork.appendChild(item);
	});
}


/**
 * Returns negative value if name of artwork_item1 < name of artwork_item2
 * 		   positive value if name of artwork_item1 > name of artwork_item2
 *		   else 0.
*/
function cmpArtworkByName(artwork_item1, artwork_item2) {
	const name1 = getArtworkItemsName(artwork_item1);
	const name2 = getArtworkItemsName(artwork_item2);
	return name1.localeCompare(name2);
}


/**
 * Returns negative value if artists name of artwork_item1 < artists name of artwork_item2
 * 		   positive value if artists name of artwork_item1 > artists name of artwork_item2
 *		   else 0.
*/
function cmpArtworkByArtistsName(artwork_item1, artwork_item2) {
	const artist_name1 = getArtworkItemsArtistName(artwork_item1);
	const artist_name2 = getArtworkItemsArtistName(artwork_item2);
	return artist_name1.localeCompare(artist_name2);
}


function getArtworkItemsName(artwork_item) {
	const name = artwork_item.children[1].children[0].textContent.trim();
	return name;
}


function getArtworkItemsArtistName(artwork_item) {
	const artist_name = artwork_item.children[1].children[1].textContent.trim();
	return artist_name;
}


function initSearchbar() {
	const searchbar = document.getElementById('searchbar');
	let prev_search_val = "";

	searchbar.addEventListener("keyup", (e) => {
		if (searchbar.value === prev_search_val) {
			// no update do not search again
			return;
		}

		search(searchbar.value);
		prev_search_val = searchbar.value;

		// sort by criteria selected
		document.querySelector("#sort-buttons a.nav-link.active").click();
	});
}


/**
 * Filters and displays only the images that
 * match the given criteria and sorts them by
 * given criteria if any.
*/
function search(search_query) {
	search_query = search_query.toLowerCase();
	let items = document.querySelectorAll("#menu-items .menu-item");

	items.forEach((artwork_item) => {
		// match by artwork name:
		const artwork_name = getArtworkItemsName(artwork_item);

		if (artwork_name.toLowerCase().includes(search_query)) {
			artwork_item.classList.add('item-active');
			return;
		}

		// match by artists name:
		const artists_name = getArtworkItemsArtistName(artwork_item);
		if (artists_name.toLowerCase().includes(search_query)) {
			artwork_item.classList.add('item-active');
			return;
		}

		// else does not match the query:
		artwork_item.classList.remove('item-active');
	});
}
