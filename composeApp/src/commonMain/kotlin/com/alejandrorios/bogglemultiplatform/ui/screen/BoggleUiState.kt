package com.alejandrorios.bogglemultiplatform.ui.screen

import com.alejandrorios.bogglemultiplatform.data.models.DictionaryResponse
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount
import kotlinx.serialization.Serializable

@Serializable
data class BoggleUiState(
    val words: List<String> = emptyList(),
    val wordsGuessed: List<String> = emptyList(),
    val result: List<String> = emptyList(),
    val board: List<String> = emptyList(),
    val boardMap: Map<Int, String> = emptyMap(),
    val isAWord: Boolean = false,
    val word: String = "",
    val wordsCount: WordsCount = WordsCount(),
    val score: Int = 0,
    val isFinish: Boolean = false,
    val isLoading: Boolean = false,
    val useAPI: Boolean = true,
    val definition: DictionaryResponse? = null
)
