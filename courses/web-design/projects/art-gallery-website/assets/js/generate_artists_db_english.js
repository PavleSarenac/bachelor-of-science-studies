
function initDatabase() {
	//let artworks = localStorage.getItem("artworks-by-artist");
	//if (artworks !== undefined && artworks !== null) {
	//	return;
	//}

	// initialize for the 1st time:
	artworks = new Map();

	artworks.set(
		"Gustav Klimt",
		[
			"Der Kuss"
		]
	);
	artworks.set(
		"Auguste Rodin",
		[
			"The Tinker"
		]
	);
	artworks.set(
		"Gustav Klimt",
		[
			"Der Kuss"
		]
	);
	artworks.set(
		"Paja Jovanovic",
		[
			"Migration of the Serbs"
		]
	);
	artworks.set(
		"Vincent van Gogh",
		[
			"Sunflowers"
		]
	);
	artworks.set(
		"Leonardo da Vinci",
		[
			"Mona Lisa",
			"The Last Supper"
		]
	);
	artworks.set(
		"Michelangelo Buonarotti",
		[
			"David"
		]
	);
	artworks.set(
		"Gustave Eiffel",
		[
			"Statue of Liberty"
		]
	);
	artworks.set(
		"Edvard Eriksen",
		[
			"The Little Mermaid"
		]
	);
	artworks.set(
		"Ancient Egyptians",
		[
			"The Great Sphynx"
		]
	);
	artworks.set(
		"Sydney Lumet",
		[
			"12 Angry Men"
		]
	);
	artworks.set(
		"Samuel Mendes",
		[
			"American Beauty"
		]
	);
	artworks.set(
		"Frank Darabont",
		[
			"The Shawshank Redemption"
		]
	);
	artworks.set(
		"Francis Ford Coppola",
		[
			"The Godfather"
		]
	);
	artworks.set(
		"Christopher Nolan",
		[
			"The Prestige"
		]
	);

	//localStorage.setItem("artworks-by-artist", JSON.stringify(artworks));
	return artworks;
}
