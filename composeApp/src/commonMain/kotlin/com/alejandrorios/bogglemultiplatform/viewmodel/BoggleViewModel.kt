package com.alejandrorios.bogglemultiplatform.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.alejandrorios.bogglemultiplatform.BoardGenerator
import com.alejandrorios.bogglemultiplatform.Language
import com.alejandrorios.bogglemultiplatform.isAndroid
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BoggleViewModel : ViewModel() {

    private val boardGenerator = BoardGenerator(Language.EN)

    // Game UI state
    private val _uiState = MutableStateFlow(BoggleUiState())
    val uiState: StateFlow<BoggleUiState> = _uiState.asStateFlow()

    var board = mutableStateListOf<String>()
        private set

    private var positionsSet = mutableSetOf<Int>()

    private val httpClient = HttpClient {
        install(HttpTimeout)
        install(ContentNegotiation) {
            json()
        }
    }

    init {
        reloadBoard()
    }

    override fun onCleared() {
        httpClient.close()
    }

    fun reloadBoard() {
        val boardMap: MutableMap<Int, String> = mutableMapOf()
        _uiState.value = BoggleUiState(words = emptyList())
        board.clear()
        board.addAll(boardGenerator.generateBoard().toMutableList())
        board.forEachIndexed { index, letter ->
            boardMap[index] = letter
        }
        _uiState.value = _uiState.value.copy(boardMap = boardMap)
        getSolution(board.toList())
    }

    fun evaluateWord(dieKeys: List<Int>) {
        positionsSet.clear()
        positionsSet.addAll(dieKeys)
        var wordToEvaluate = ""

        positionsSet.forEach { index ->
            wordToEvaluate += _uiState.value.boardMap[index]
        }

        if (_uiState.value.result.contains(wordToEvaluate.lowercase()) && !_uiState.value.wordsGuessed.contains(wordToEvaluate)) {
            _uiState.value = _uiState.value.copy(isAWord = true, word = wordToEvaluate)
        } else {
            _uiState.value = _uiState.value.copy(isAWord = false, word = "")
        }
    }

    fun addWord() {
        if (_uiState.value.word.isNotBlank()) {
            val newWordsGuessed: MutableList<String> = _uiState.value.wordsGuessed.toMutableList()
            newWordsGuessed.add(_uiState.value.word)
            positionsSet.clear()
            _uiState.value = _uiState.value.copy(isAWord = false, word = "", wordsGuessed = newWordsGuessed.toList())
        }
    }

    private fun getSolution(board: List<String>) {
        viewModelScope.launch {
//            try {
//                val configString = resource("en_dictionary.json").readBytes().decodeToString()
//                println(configString)
//                _masterModelState.update { s -> s.copy(config = Json.decodeFromString(configString)) }
//            } catch (e: Exception) {
//                println("exploit: $e")
//            }


            val words = getWordsFromBoard(board)
            _uiState.value = _uiState.value.copy(result = words)
            println(words)
        }
    }

    private suspend fun getWordsFromBoard(board: List<String>): List<String> {
        val url = if (isAndroid) {
            "10.0.2.2"
        } else {
            "0.0.0.0"
        }

        val response: String = httpClient
            .get("http://$url:8080/solver") {
                timeout {
                    requestTimeoutMillis = 3000
                }
                url {
                    parameters.append("board", Json.encodeToString(board))
                }
            }
            .body()
        return Json.decodeFromString(response)
    }
}

data class BoggleUiState(
    val words: List<String> = emptyList(),
    val wordsGuessed: List<String> = emptyList(),
    val result: List<String> = emptyList(),
    val boardMap: Map<Int, String> = emptyMap(),
    val isAWord: Boolean = false,
    val word: String = "",
)
