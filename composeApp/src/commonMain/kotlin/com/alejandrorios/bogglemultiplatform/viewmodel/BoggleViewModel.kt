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

    private var _guessedWords = ""
    private val guessedWords: String
        get() = _guessedWords

    private val httpClient = HttpClient {
        install(HttpTimeout)
        install(ContentNegotiation) {
            json()
        }
    }

    init {
        board.addAll(boardGenerator.generateBoard().toMutableList())
        getSolution(board.toList())
    }

    override fun onCleared() {
        httpClient.close()
    }

    fun reloadBoard() {
        _guessedWords = ""
        _uiState.value = BoggleUiState(words = emptyList())
        board.clear()
        board.addAll(boardGenerator.generateBoard().toMutableList())
        getSolution(board.toList())
    }

    fun onBoardSwipe(value: String) {
        if (!guessedWords.contains(value)) {
            _guessedWords += value
        }
    }

    fun onDragEnded() {
        val newWords: MutableList<String> = _uiState.value.wordsGuessed.toMutableList()
        if (_uiState.value.result.contains(_guessedWords.lowercase())) {
            newWords.add(_guessedWords)
            _uiState.value = _uiState.value.copy(wordsGuessed = newWords.toList())
        }
        _guessedWords = ""
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
)
