SELECT * FROM  `letter` JOIN lettertype ON letter.letterType_Letterset_code = lettertype.letterset_code AND letter.lettertype_karakter = lettertype.karakter
			WHERE  `Spel_ID` = ? AND letter.id = ?
