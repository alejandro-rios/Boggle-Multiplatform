package com.alejandrorios.bogglemultiplatform.models

import kotlinx.serialization.Serializable

@Serializable
data class WordsCount(
    val threeLetters: WordPair = WordPair(0, emptyList()),
    val fourLetters: WordPair = WordPair(0, emptyList()),
    val fiveLetters: WordPair = WordPair(0, emptyList()),
    val sixLetters: WordPair = WordPair(0, emptyList()),
    val sevenLetters: WordPair = WordPair(0, emptyList()),
    val moreThanSevenLetters: WordPair = WordPair(0, emptyList())
)
