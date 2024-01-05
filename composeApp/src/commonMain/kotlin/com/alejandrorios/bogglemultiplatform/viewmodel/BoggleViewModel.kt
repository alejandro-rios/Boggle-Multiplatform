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
import org.jetbrains.compose.resources.resource

class BoggleViewModel : ViewModel() {

    private var boardGenerator = BoardGenerator(Language.ES)

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

    fun changeLanguage(isEnglish: Boolean) {
        boardGenerator = if (isEnglish) {
            BoardGenerator(Language.EN)
        } else {
            BoardGenerator(Language.ES)
        }
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
            updateWordsFound()
        }
    }

    private fun getSolution(board: List<String>) {
        viewModelScope.launch {
            try {
                val dictionary = resource("en_dictionary.txt").readBytes().decodeToString().split("\r?\n|\r".toRegex()).toList()
                val results = boardGenerator.solveBoard(ArrayList(board), dictionary)
                _uiState.value = _uiState.value.copy(result = results)
                println(results)
                getWordsByLetter()
            } catch (e: Exception) {
                println("exploit: $e")
            }

//            val words = getWordsFromBoard(board)
//            _uiState.value = _uiState.value.copy(result = words)
        }
    }

    private fun getWordsByLetter() {
        val results = _uiState.value.result
        val wordsCount = WordsCount(
            threeLetters = WordPair(results.filter { it.length == 3 }.size, 0),
            fourLetters = WordPair(results.filter { it.length == 4 }.size, 0),
            fiveLetters = WordPair(results.filter { it.length == 5 }.size, 0),
            sixLetters = WordPair(results.filter { it.length == 6 }.size, 0),
            sevenLetters = WordPair(results.filter { it.length == 7 }.size, 0),
            moreThanSevenLetters = WordPair(results.filter { it.length > 7 }.size, 0)
        )
        _uiState.value = _uiState.value.copy(wordsCount = wordsCount)
    }

    private fun updateWordsFound() {
        val wordsGuessed = _uiState.value.wordsGuessed
        val wordsCount = WordsCount(
            threeLetters = _uiState.value.wordsCount.threeLetters.copy(wordsFound = wordsGuessed.filter { it.length == 3 }.size),
            fourLetters = _uiState.value.wordsCount.fourLetters.copy(wordsFound = wordsGuessed.filter { it.length == 4 }.size),
            fiveLetters = _uiState.value.wordsCount.fiveLetters.copy(wordsFound = wordsGuessed.filter { it.length == 5 }.size),
            sixLetters = _uiState.value.wordsCount.sixLetters.copy(wordsFound = wordsGuessed.filter { it.length == 6 }.size),
            sevenLetters = _uiState.value.wordsCount.sevenLetters.copy(wordsFound = wordsGuessed.filter { it.length == 7 }.size),
            moreThanSevenLetters = _uiState.value.wordsCount.moreThanSevenLetters.copy(wordsFound = wordsGuessed.filter {
                it
                    .length > 7
            }.size)
        )

        _uiState.value = _uiState.value.copy(wordsCount = wordsCount)
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
    val wordsCount: WordsCount = WordsCount()
)

data class WordsCount(
    val threeLetters: WordPair = WordPair(0, 0),
    val fourLetters: WordPair = WordPair(0, 0),
    val fiveLetters: WordPair = WordPair(0, 0),
    val sixLetters: WordPair = WordPair(0, 0),
    val sevenLetters: WordPair = WordPair(0, 0),
    val moreThanSevenLetters: WordPair = WordPair(0, 0)
)

data class WordPair(
    val wordsTotal: Int,
    val wordsFound: Int
)
