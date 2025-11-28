package com.alejandrorios.bogglemultiplatform.ui.screen

import com.alejandrorios.bogglemultiplatform.currentPlatform
import com.alejandrorios.bogglemultiplatform.data.models.DictionaryResponse
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount
import kotlinx.serialization.Serializable
import kotlin.math.floor

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
    val isLoading: Boolean = true,
    val useAPI: Boolean = true,
    val isEnglish: Boolean = true,
    val definition: DictionaryResponse? = null,
    val hintDefinition: DictionaryResponse? = null
) {

    val progress: String
        get() = if (wordsGuessed.isEmpty()) {
            "0"
        } else {
            val number = if (currentPlatform.isWasm) {
                (floor((wordsGuessed.size / result.size.toFloat()).times(100.0) * 10 + 0.5) / 10)
            } else {
                (floor((wordsGuessed.size / result.size.toFloat()).times(100.0) * 10 + 0.5) / 10).toFloat()
            }

            "${if (number.toFloat() % 1 == 0f) number.toInt() else number}"
        }

    val definitionWord: String
        get() = definition?.word ?: ""

    val firstDefinition: String
        get() = definition?.meanings?.first()?.definitions?.first()?.definition ?: ""
}
