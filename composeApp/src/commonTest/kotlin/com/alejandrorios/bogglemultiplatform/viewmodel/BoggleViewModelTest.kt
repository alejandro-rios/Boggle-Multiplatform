package com.alejandrorios.bogglemultiplatform.viewmodel

import app.cash.turbine.test
import com.alejandrorios.bogglemultiplatform.data.BoardGenerator
import com.alejandrorios.bogglemultiplatform.data.models.WordPair
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount
import com.alejandrorios.bogglemultiplatform.data.utils.CallResponse
import com.alejandrorios.bogglemultiplatform.domain.repository.BoggleRepository
import com.alejandrorios.bogglemultiplatform.domain.repository.LocalRepository
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleViewModel
import com.alejandrorios.bogglemultiplatform.utils.mockedBoard
import com.alejandrorios.bogglemultiplatform.utils.mockedBoardMap
import com.alejandrorios.bogglemultiplatform.utils.mockedDefinitions
import com.alejandrorios.bogglemultiplatform.utils.mockedLocalResults
import com.alejandrorios.bogglemultiplatform.utils.mockedResults
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BoggleViewModelTest {

    private val mockedWordsCount = WordsCount()
    private val mockedEmptyUiState = BoggleUiState().copy(words = emptyList())
    private val mockedUiState = BoggleUiState().copy(result = mockedResults, wordsCount = mockedWordsCount)
    private val repository = mock<BoggleRepository> {
        everySuspend { fetchWordsFromAPI(any()) } returns flowOf(CallResponse.Success(mockedResults))
        everySuspend { getDefinition("test") } returns flowOf(CallResponse.Success(mockedDefinitions))
    }

    private val boardGenerator = mock<BoardGenerator> {
        every { generateBoard() } returns mockedBoard
        every { getBoardSolution(mockedBoard, any()) } returns mockedLocalResults
    }

    private val localRepository = mock<LocalRepository> {
        everySuspend { getBoggleUiState() } returns flowOf(mockedUiState)
        everySuspend { clearData() } returns flowOf(Unit)
    }

    private lateinit var viewModel: BoggleViewModel

    @BeforeTest
    fun setUp() {
        viewModel = BoggleViewModel(repository, boardGenerator, localRepository)
    }

    /**
     * Mokkery can't mock data class(for now) so for [WordsCount] I need to create a const, luckily, the uiState model has
     * default values so it's "easier" to use.
     */
    @Test
    fun given_viewmodel_when_gameStart_is_called_and_has_data_stored_then_should_update_state() = runTest {
        viewModel.uiState.test {
            awaitItem()

            viewModel.gameStart()

            val loadStep = awaitItem()

            assertEquals(emptyList(), loadStep.words)
            assertEquals(emptyList(), loadStep.wordsGuessed)
            assertEquals(emptyList(), loadStep.board)
            assertEquals(emptyMap(), loadStep.boardMap)
            assertEquals("", loadStep.word)
            assertEquals(0, loadStep.score)
            assertNull(loadStep.definition)
            assertEquals(mockedResults, loadStep.result)
            assertEquals(mockedWordsCount, loadStep.wordsCount)
            assertFalse(loadStep.isAWord)
            assertFalse(loadStep.isFinish)
            assertFalse(loadStep.isLoading)
            assertTrue(loadStep.isEnglish)
            assertTrue(loadStep.useAPI)
        }
    }

    @Test
    fun given_viewmodel_when_createNewGame_with_useAPI_is_called_then_should_update_state() = runTest {
        viewModel.uiState.test {
            viewModel.createNewGame()

            val createNewGameStepStep = awaitItem()

            assertEquals(mockedEmptyUiState, createNewGameStepStep)
            assertEquals(emptyList(), createNewGameStepStep.words)
            assertEquals(emptyList(), createNewGameStepStep.board)
            assertEquals(emptyMap(), createNewGameStepStep.boardMap)
            assertFalse(createNewGameStepStep.isLoading)

            val resultStep = awaitItem()

            assertEquals(emptyList(), resultStep.words)
            assertEquals(mockedBoardMap, resultStep.boardMap)
            assertEquals(mockedBoard, resultStep.board)
            assertTrue(resultStep.useAPI)
            assertTrue(resultStep.isEnglish)
            assertTrue(resultStep.isLoading)

            val getSolutionStep = awaitItem()

            assertTrue(getSolutionStep.useAPI)
            assertEquals(mockedResults, getSolutionStep.result)

            val getWordsByLetterStep = awaitItem()

            assertTrue(getSolutionStep.useAPI)
            assertEquals(mockedResults, getWordsByLetterStep.result)
            assertEquals(WordPair(3, emptyList()), getWordsByLetterStep.wordsCount.threeLetters)
            assertFalse(getWordsByLetterStep.isLoading)
        }
    }

    @Test
    fun given_viewmodel_when_createNewGame_without_useAPI_is_called_then_should_update_state() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.useAPI(false)

            val useAPIStep = awaitItem()

            assertFalse(useAPIStep.useAPI)

            viewModel.createNewGame()

            val createNewGameStep = awaitItem()

            assertEquals(mockedEmptyUiState, createNewGameStep)
            assertEquals(emptyList(), createNewGameStep.words)
            assertEquals(emptyList(), createNewGameStep.board)
            assertEquals(emptyMap(), createNewGameStep.boardMap)
            assertFalse(createNewGameStep.isLoading)
            assertTrue(createNewGameStep.useAPI)

            val resultStep = awaitItem()

            assertEquals(emptyList(), resultStep.words)
            assertEquals(mockedBoardMap, resultStep.boardMap)
            assertEquals(mockedBoard, resultStep.board)
            assertFalse(resultStep.useAPI)
            assertTrue(resultStep.isEnglish)
            assertTrue(resultStep.isLoading)

            val getSolutionStep = awaitItem()

            assertFalse(getSolutionStep.useAPI)
            assertEquals(mockedLocalResults, getSolutionStep.result)

            val getWordsByLetterStep = awaitItem()

            assertFalse(getSolutionStep.useAPI)
            assertEquals(mockedLocalResults, getWordsByLetterStep.result)
            assertEquals(WordPair(1, emptyList()), getWordsByLetterStep.wordsCount.threeLetters)
            assertFalse(getWordsByLetterStep.isLoading)
        }
    }

    @Test
    fun given_viewmodel_when_changeLanguage_is_called_with_true_then_should_update_isEnglish_state_to_true() = runTest {
        viewModel.uiState.test {
            viewModel.changeLanguage(true)

            val resultStep = awaitItem()

            assertTrue(resultStep.isEnglish)
        }
    }

    @Test
    fun given_viewmodel_when_changeLanguage_is_called_with_false_then_should_update_isEnglish_state_to_false() = runTest {
        viewModel.uiState.test {
            val beforeStep = awaitItem()

            assertTrue(beforeStep.isEnglish)

            viewModel.changeLanguage(false)

            val resultStep = awaitItem()

            assertFalse(resultStep.isEnglish)
        }
    }

    @Test
    fun given_viewmodel_when_getHint_is_called_then_should_return_string() = runTest {
        viewModel.gameStart()

        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            val resultStep = viewModel.getHint()

            assertEquals("one", resultStep)
        }
    }

    @Test
    fun given_viewmodel_when_closeDialog_is_called_then_should_update_isFinish_state_to_false() = runTest {
        viewModel.gameStart()

        advanceUntilIdle()

        viewModel.uiState.test {

            viewModel.closeDialog()

            val resultStep = awaitItem()

            assertFalse(resultStep.isFinish)
        }
    }

    @Test
    fun given_viewmodel_when_closeDefinitionDialog_is_called_then_should_update_definition_state_to_null() = runTest {
        viewModel.gameStart()

        advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.closeDefinitionDialog()

            val resultStep = awaitItem()

            assertNull(resultStep.definition)
        }
    }

    @Test
    fun given_viewmodel_when_useAPI_is_called_then_should_update_definition_state_to_false() = runTest {
        viewModel.gameStart()

        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.useAPI(false)

            val resultStep = awaitItem()

            assertFalse(resultStep.useAPI)
        }
    }

    @Test
    fun given_viewmodel_when_getWordDefinition_is_called_then_should_update_definition_state_to_model() = runTest {
        viewModel.gameStart()

        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.getWordDefinition("test")

            val resultStep = awaitItem()

            assertEquals(mockedDefinitions[0], resultStep.definition)
        }
    }

    @Test
    fun given_viewmodel_when_evaluateWord_is_called_then_should_update_isAWord_with_true() = runTest {
        val artWordKeys = listOf(0, 1, 2)
        viewModel.createNewGame()

        viewModel.uiState.test {
            val previousStep = awaitItem()

            assertFalse(previousStep.isAWord)
            assertEquals("", previousStep.word)

            viewModel.evaluateWord(artWordKeys, false)

            val resultStep = awaitItem()

            assertTrue(resultStep.isAWord)
            assertEquals("ART", resultStep.word)
        }
    }

    @Test
    fun given_viewmodel_when_evaluateWord_is_called_then_should_update_isAWord_with_false() = runTest {
        val nonWordKeys = listOf(2, 0, 1)
        viewModel.createNewGame()

        viewModel.uiState.test {
            val previousStep = awaitItem()

            assertFalse(previousStep.isAWord)
            assertEquals("", previousStep.word)

            viewModel.evaluateWord(nonWordKeys, false)

            val resultStep = awaitItem()

            assertFalse(resultStep.isAWord)
            assertEquals("", resultStep.word)
        }
    }

    @Test
    fun given_viewmodel_when_addWord_is_called_then_should_update_isAWord_with_false_and_wordsGuessed_with_list() = runTest {
        val artWordKeys = listOf(0, 1, 2)
        viewModel.createNewGame()

        advanceUntilIdle()

        viewModel.uiState.test {
            val previousStep = awaitItem()

            assertFalse(previousStep.isAWord)
            assertEquals("", previousStep.word)

            viewModel.evaluateWord(artWordKeys, false)

            val middleStep = awaitItem()

            assertTrue(middleStep.isAWord)
            assertEquals("ART", middleStep.word)

            viewModel.addWord()

            val resultStep = awaitItem()

            assertFalse(resultStep.isAWord)
            assertEquals("", resultStep.word)
            assertEquals(listOf("ART"), resultStep.wordsGuessed)

            val updateWordsFoundStep = awaitItem()
            assertEquals(1, updateWordsFoundStep.score)
            assertEquals(WordPair(3, listOf("ART")), updateWordsFoundStep.wordsCount.threeLetters)
        }
    }
}
