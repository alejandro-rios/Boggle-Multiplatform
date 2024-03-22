package com.alejandrorios.bogglemultiplatform.ui.screen

import androidx.compose.runtime.mutableStateListOf
import com.alejandrorios.bogglemultiplatform.data.BoardGenerator
import com.alejandrorios.bogglemultiplatform.data.Language
import com.alejandrorios.bogglemultiplatform.data.models.DictionaryResponse
import com.alejandrorios.bogglemultiplatform.data.models.WordPair
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.xxfast.kstore.KStore
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.resource

@OptIn(ExperimentalSerializationApi::class)
class BoggleViewModel(private val boggleStore: KStore<BoggleUiState>) : ViewModel() {
    private var boardGenerator = BoardGenerator(Language.EN)
    private var boardDictionary = "files/en_dictionary.txt"

    // Game UI state
    private val _uiState = MutableStateFlow(BoggleUiState())
    val uiState: StateFlow<BoggleUiState> = _uiState.asStateFlow()

    private var board = mutableStateListOf<String>()
    private var positionsSet = mutableSetOf<Int>()

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        install(HttpTimeout)
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false })
        }
    }

    init {
        viewModelScope.launch {
            val boggleGameState: BoggleUiState? = boggleStore.get()

            if (boggleGameState != null && boggleGameState.result.isNotEmpty()) {
                _uiState.update { currentState ->
                    currentState.copy(
                        words = boggleGameState.words,
                        wordsGuessed = boggleGameState.wordsGuessed,
                        result = boggleGameState.result,
                        board = boggleGameState.board,
                        boardMap = boggleGameState.boardMap,
                        wordsCount = boggleGameState.wordsCount,
                        score = boggleGameState.score,
                        isLoading = false,
                    )
                }
            } else {
                reloadBoard()
            }
        }
    }

    override fun onCleared() {
        httpClient.close()
    }

    fun changeLanguage(isEnglish: Boolean) {
        boardGenerator = if (isEnglish) {
            boardDictionary = "files/en_dictionary.txt"
            BoardGenerator(Language.EN)
        } else {
            boardDictionary = "files/es_dictionary.txt"
            BoardGenerator(Language.ES)
        }
    }

    fun reloadBoard() {
        viewModelScope.launch {
            val boardMap: MutableMap<Int, String> = mutableMapOf()
            val useAPI = _uiState.value.useAPI
            boggleStore.delete()
            _uiState.value = BoggleUiState(words = emptyList())
            board.clear()
            board.addAll(boardGenerator.generateBoard().toMutableList())
            board.forEachIndexed { index, letter ->
                boardMap[index] = letter
            }

            _uiState.update { currentState ->
                currentState.copy(boardMap = boardMap, board = board, useAPI = useAPI, isLoading = true)
            }

            getSolution(board.toList())
        }
    }

    fun evaluateWord(dieKeys: List<Int>) {
        positionsSet.clear()
        positionsSet.addAll(dieKeys)
        var wordToEvaluate = ""

        positionsSet.forEach { index ->
            wordToEvaluate += _uiState.value.boardMap[index]
        }

        if (_uiState.value.result.contains(wordToEvaluate.lowercase()) && !_uiState.value.wordsGuessed.contains(wordToEvaluate)) {
            _uiState.update { currentState ->
                currentState.copy(isAWord = true, word = wordToEvaluate)
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(isAWord = false, word = "")
            }
        }
    }

    fun addWord() {
        if (_uiState.value.word.isNotBlank()) {
            val newWordsGuessed: MutableList<String> = _uiState.value.wordsGuessed.toMutableList()
            newWordsGuessed.add(_uiState.value.word)
            positionsSet.clear()

            _uiState.update { currentState ->
                currentState.copy(isAWord = false, word = "", wordsGuessed = newWordsGuessed.toList())
            }

            updateWordsFound()
        }
    }

    private fun getSolution(board: List<String>) {
        viewModelScope.launch {
            if (_uiState.value.useAPI) {
                val words = getWordsFromAPI(board).map {
                    it.lowercase()
                }

                _uiState.update { currentState ->
                    currentState.copy(result = words)
                }

                getWordsByLetter()
            } else {
                try {
                    val words = getWordsFromLocal(board)

                    _uiState.update { currentState ->
                        currentState.copy(result = words)
                    }

                    getWordsByLetter()
                } catch (e: Exception) {
                    println("exploit: $e")
                }
            }
        }
    }

    private fun getWordsByLetter() {
        val results = _uiState.value.result
        val wordsCount = WordsCount(
            threeLetters = WordPair(results.filter { it.length == 3 }.size, emptyList()),
            fourLetters = WordPair(results.filter { it.length == 4 }.size, emptyList()),
            fiveLetters = WordPair(results.filter { it.length == 5 }.size, emptyList()),
            sixLetters = WordPair(results.filter { it.length == 6 }.size, emptyList()),
            sevenLetters = WordPair(results.filter { it.length == 7 }.size, emptyList()),
            moreThanSevenLetters = WordPair(results.filter { it.length > 7 }.size, emptyList())
        )

        _uiState.update { currentState ->
            currentState.copy(wordsCount = wordsCount, isLoading = false)
        }

    }

    private fun updateWordsFound() {
        val wordsGuessed = _uiState.value.wordsGuessed
        val wordsCount = WordsCount(
            threeLetters = _uiState.value.wordsCount.threeLetters.copy(wordsFound = wordsGuessed.filter { it.length == 3 }),
            fourLetters = _uiState.value.wordsCount.fourLetters.copy(wordsFound = wordsGuessed.filter { it.length == 4 }),
            fiveLetters = _uiState.value.wordsCount.fiveLetters.copy(wordsFound = wordsGuessed.filter { it.length == 5 }),
            sixLetters = _uiState.value.wordsCount.sixLetters.copy(wordsFound = wordsGuessed.filter { it.length == 6 }),
            sevenLetters = _uiState.value.wordsCount.sevenLetters.copy(wordsFound = wordsGuessed.filter { it.length == 7 }),
            moreThanSevenLetters = _uiState.value.wordsCount.moreThanSevenLetters.copy(wordsFound = wordsGuessed.filter {
                it
                    .length > 7
            })
        )
        val score = (wordsCount.threeLetters.wordsFound.size * 1) + (wordsCount.fourLetters.wordsFound.size * 1) + (wordsCount
            .fiveLetters.wordsFound.size * 2) + (wordsCount.sixLetters.wordsFound.size * 3) + (wordsCount.sevenLetters
            .wordsFound.size * 5) + (wordsCount.moreThanSevenLetters.wordsFound.size * 11)

        _uiState.update { currentState ->
            currentState.copy(
                wordsCount = wordsCount,
                score = score,
                isFinish = _uiState.value.wordsGuessed.size == _uiState.value.result.size,
            )
        }

        viewModelScope.launch {
            boggleStore.set(_uiState.value)
        }
    }

    fun getHint(): String = _uiState.value.result.first { word -> !_uiState.value.wordsGuessed.contains(word.uppercase()) }

    private suspend fun getWordsFromAPI(board: List<String>): List<String> {
        val response: String = httpClient
            .get("https://api.codebox.org.uk/boggle/${board.joinToString(separator = "")}") {
                timeout {
                    requestTimeoutMillis = 30000
                    connectTimeoutMillis = 30000
                }
            }
            .body()
        return Json.decodeFromString(response)
    }
// TODO: Use when 1.6.0 has viewmodel support
//    @OptIn(InternalResourceApi::class)
    private suspend fun getWordsFromLocal(board: List<String>): List<String> {
//    val dictionary = readResourceBytes(boardDictionary).decodeToString().split("\r?\n|\r".toRegex()).toList()
        val dictionary = resource(boardDictionary).readBytes().decodeToString().split("\r?\n|\r".toRegex()).toList()

        return boardGenerator.getBoardSolution(ArrayList(board), dictionary)
    }

    fun closeDialog() {
        _uiState.update { currentState ->
            currentState.copy(isFinish = false)
        }
    }

    fun closeDefinitionDialog() {
        _uiState.update { currentState ->
            currentState.copy(definition = null)
        }
    }

    fun useAPI(useAPI: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(useAPI = useAPI)
        }
    }

    fun getWordDefinition(word: String) {
        viewModelScope.launch {
            val definition = searchWord(word)

            _uiState.update { currentState ->
                currentState.copy(definition = definition?.get(0))
            }
        }
    }

    private suspend fun searchWord(word: String): List<DictionaryResponse>? {
        val response: List<DictionaryResponse>? = httpClient
            .get("https://api.dictionaryapi.dev/api/v2/entries/en/$word") {
                timeout {
                    requestTimeoutMillis = 30000
                }
            }
            .body<List<DictionaryResponse>?>()
        return response
    }
}
