
function initDatabase() {
	//let artworks = localStorage.getItem("artworks-by-artist");
	//if (artworks !== undefined && artworks !== null) {
	//	return;
	//}

	// initialize for the 1st time:
	artworks = new Map();

	artworks.set(
		"Густав Климт",
		[
			"Пољубац"
		]
	);
	artworks.set(
		"Огист Роден",
		[
			"Мислилац"
		]
	);
	artworks.set(
		"Паја Јовановић",
		[
			"Сеоба Срба"
		]
	);
	artworks.set(
		"Винсент ван Гог",
		[
			"Сунцокрети"
		]
	);
	artworks.set(
		"Леонардо да Винчи",
		[
			"Мона Лиза",
			"Тајна Вечера"
		]
	);
	artworks.set(
		"Микеланђело Буонароти",
		[
			"Давид"
		]
	);
	artworks.set(
		"Гистав Ајфел",
		[
			"Кип Слободе"
		]
	);
	artworks.set(
		"Едвард Ериксен",
		[
			"Мала Сирена"
		]
	);
	artworks.set(
		"Стари Египћани",
		[
			"Велика Сфинга"
		]
	);
	artworks.set(
		"Сидни Лумет",
		[
			"12 Љутих Мушкараца"
		]
	);
	artworks.set(
		"Сем Мендес",
		[
			"Америчка Лепота"
		]
	);
	artworks.set(
		"Френк Дарабонт",
		[
			"Бекство из Шошенка"
		]
	);
	artworks.set(
		"Френсис Форд Копола",
		[
			"Кум"
		]
	);
	artworks.set(
		"Кристофер Нолан",
		[
			"Престиж"
		]
	);

	//localStorage.setItem("artworks-by-artist", JSON.stringify(artworks));
	return artworks;
}
