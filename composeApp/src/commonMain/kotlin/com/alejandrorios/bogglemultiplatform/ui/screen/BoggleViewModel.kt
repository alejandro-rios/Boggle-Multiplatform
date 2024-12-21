package com.alejandrorios.bogglemultiplatform.ui.screen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alejandrorios.bogglemultiplatform.data.BoardGenerator
import com.alejandrorios.bogglemultiplatform.data.Language
import com.alejandrorios.bogglemultiplatform.data.models.WordPair
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount
import com.alejandrorios.bogglemultiplatform.data.utils.CallResponse.Failure
import com.alejandrorios.bogglemultiplatform.data.utils.CallResponse.Success
import com.alejandrorios.bogglemultiplatform.domain.repository.BoggleRepository
import com.alejandrorios.bogglemultiplatform.domain.repository.LocalRepository
import com.alejandrorios.bogglemultiplatform.domain.utils.dispatchers.AppCoroutineDispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes
import kotlin.collections.set

class BoggleViewModel(
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
    private val repository: BoggleRepository,
    private val boardGenerator: BoardGenerator,
    private val localRepository: LocalRepository
) : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(BoggleUiState())
    val uiState: StateFlow<BoggleUiState> = _uiState.asStateFlow()

    private var board = mutableStateListOf<String>()
    private var positionsSet = mutableSetOf<Int>()

    fun gameStart() {
        viewModelScope.launch(appCoroutineDispatchers.io) {
            localRepository.getBoggleUiState().collect { gameState ->
                if (gameState != null && gameState.result.isNotEmpty()) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            words = gameState.words,
                            wordsGuessed = gameState.wordsGuessed,
                            result = gameState.result,
                            board = gameState.board,
                            boardMap = gameState.boardMap,
                            wordsCount = gameState.wordsCount,
                            score = gameState.score,
                            useAPI = gameState.useAPI,
                            isEnglish = gameState.isEnglish,
                            isLoading = false,
                        )
                    }
                    changeLanguage(gameState.isEnglish)
                } else {
                    createNewGame()
                }
            }
        }
    }

    fun changeLanguage(isEnglish: Boolean) {
        boardGenerator.language = if (isEnglish) Language.EN else Language.SPA

        _uiState.update { currentState ->
            currentState.copy(isEnglish = isEnglish)
        }
    }

    fun createNewGame() {
        viewModelScope.launch(appCoroutineDispatchers.io) {
            val boardMap: MutableMap<Int, String> = mutableMapOf()
            val useAPI = _uiState.value.useAPI
            val isEnglish = _uiState.value.isEnglish
            localRepository.clearData()
            _uiState.value = BoggleUiState(words = emptyList())
            board.clear()
            board.addAll(boardGenerator.generateBoard().toMutableList())
            board.forEachIndexed { index, letter ->
                boardMap[index] = letter
            }

            _uiState.update { currentState ->
                currentState.copy(
                    boardMap = boardMap,
                    board = board,
                    useAPI = useAPI,
                    isEnglish = isEnglish,
                    isLoading = true
                )
            }

            getSolution(board.toList())
        }
    }

    fun evaluateWord(dieKeys: List<Int>, isFromTap: Boolean) {
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

            if (isFromTap) {
                viewModelScope.launch {
                    delay(250)
                    addWord()
                }
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
        viewModelScope.launch(appCoroutineDispatchers.io) {
            if (_uiState.value.useAPI) {
                repository.fetchWordsFromAPI(board).collect { result ->
                    when (result) {
                        is Failure -> {}
                        is Success -> _uiState.update { currentState ->
                            currentState.copy(result = result.data.map { it.lowercase() })
                        }
                    }
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
            threeLetters = WordPair(results.count { it.length == 3 }, emptyList()),
            fourLetters = WordPair(results.count { it.length == 4 }, emptyList()),
            fiveLetters = WordPair(results.count { it.length == 5 }, emptyList()),
            sixLetters = WordPair(results.count { it.length == 6 }, emptyList()),
            sevenLetters = WordPair(results.count { it.length == 7 }, emptyList()),
            moreThanSevenLetters = WordPair(results.count { it.length > 7 }, emptyList())
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

        viewModelScope.launch(appCoroutineDispatchers.io) {
            localRepository.saveBoggleUiState(_uiState.value)
        }
    }

    fun getHint() {
        val word = _uiState.value.result.first { word -> !_uiState.value.wordsGuessed.contains(word.uppercase()) }

        getWordDefinition(word, true)
    }

    @OptIn(InternalResourceApi::class)
    private suspend fun getWordsFromLocal(board: List<String>): List<String> {
        val dictionary = readResourceBytes(boardGenerator.language.filePath).decodeToString().split("\r?\n|\r".toRegex()).toList()

        return boardGenerator.getBoardSolutionTwo(board = board, dictionary = dictionary)
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

    fun closeHintDefinitionDialog() {
        _uiState.update { currentState ->
            currentState.copy(hintDefinition = null)
        }
    }

    fun useAPI(useAPI: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(useAPI = useAPI)
        }
    }

    fun getWordDefinition(word: String, isFromHint: Boolean = false) {
        viewModelScope.launch(appCoroutineDispatchers.io) {
            repository.getDefinition(word).collect { result ->
                when (result) {
                    is Failure -> {}
                    is Success -> _uiState.update { currentState ->
                        if (isFromHint) {
                            currentState.copy(hintDefinition = result.data[0])
                        } else {
                            currentState.copy(definition = result.data[0])
                        }
                    }
                }
            }
        }
    }
}
