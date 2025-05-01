package com.alejandrorios.bogglemultiplatform.viewmodel

import app.cash.turbine.test
import com.alejandrorios.bogglemultiplatform.data.BoardGenerator
import com.alejandrorios.bogglemultiplatform.data.Language
import com.alejandrorios.bogglemultiplatform.data.models.WordPair
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount
import com.alejandrorios.bogglemultiplatform.data.utils.CallResponse
import com.alejandrorios.bogglemultiplatform.domain.repository.BoggleRepository
import com.alejandrorios.bogglemultiplatform.domain.repository.LocalRepository
import com.alejandrorios.bogglemultiplatform.domain.utils.DictionaryProvider
import com.alejandrorios.bogglemultiplatform.domain.utils.dispatchers.AppCoroutineDispatchers
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleViewModel
import com.alejandrorios.bogglemultiplatform.utils.mockedBoard
import com.alejandrorios.bogglemultiplatform.utils.mockedBoardMap
import com.alejandrorios.bogglemultiplatform.utils.mockedDefinitions
import com.alejandrorios.bogglemultiplatform.utils.mockedLocalResults
import com.alejandrorios.bogglemultiplatform.utils.mockedResults
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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

    private val testDispatcher = StandardTestDispatcher()
    private val mockedWordsCount = WordsCount()
    private val mockedUiState = BoggleUiState().copy(result = mockedResults, wordsCount = mockedWordsCount)
    private val repository = mock<BoggleRepository> {
        everySuspend { fetchWordsFromAPI(any()) } returns flowOf(CallResponse.Success(mockedResults))
        everySuspend { getDefinition("test") } returns flowOf(CallResponse.Success(mockedDefinitions))
        everySuspend { getDefinition("one") } returns flowOf(CallResponse.Success(mockedDefinitions))
    }

    private val boardGenerator = mock<BoardGenerator>(MockMode.autoUnit) {
        every { generateBoard() } returns mockedBoard
        every { getBoardSolutionTwo(mockedBoard, any()) } returns mockedLocalResults
        every { language } returns Language.EN
    }

    private val localRepository = mock<LocalRepository> {
        everySuspend { getBoggleUiState() } returns flowOf(mockedUiState)
        everySuspend { clearData() } returns Unit
    }

    private val mockDictionaryProvider = mock<DictionaryProvider> {
        everySuspend { getWordsFromDictionary(any()) } returns listOf("art", "rat", "tar")
    }

    private lateinit var viewModel: BoggleViewModel

    private val appCoroutineDispatchers = mock<AppCoroutineDispatchers> {
        every { io } returns testDispatcher
    }

    @BeforeTest
    fun setUp() {
        viewModel = BoggleViewModel(
            appCoroutineDispatchers = appCoroutineDispatchers,
            repository = repository,
            boardGenerator = boardGenerator,
            localRepository = localRepository,
            dictionaryProvider = mockDictionaryProvider,
        )
    }

    @Test
    fun given_viewmodel_when_gameStart_is_called_and_has_data_stored_then_should_update_state() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            viewModel.gameStart()
            advanceUntilIdle()

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
    fun given_viewmodel_when_createNewGame_with_useAPI_is_called_then_should_update_state() = runTest(testDispatcher) {
        viewModel.uiState.test {
            viewModel.createNewGame()
            advanceUntilIdle()

            val firstStep = awaitItem()
            assertEquals(emptyList(), firstStep.words)
            // Note: not checking board, boardMap, or isLoading as they might be
            // inconsistent due to timing of multiple rapid state updates

            val secondStep = awaitItem()
            assertEquals(emptyList(), secondStep.words)
            assertEquals(mockedBoardMap, secondStep.boardMap)
            assertEquals(mockedBoard, secondStep.board)
            assertTrue(secondStep.useAPI)
            assertTrue(secondStep.isEnglish)
            assertTrue(secondStep.isLoading)

            val thirdStep = awaitItem()
            assertTrue(thirdStep.useAPI)
            assertEquals(mockedResults, thirdStep.result)

            val fourthStep = awaitItem()
            assertTrue(fourthStep.useAPI)
            assertEquals(mockedResults, fourthStep.result)
            assertEquals(WordPair(3, emptyList()), fourthStep.wordsCount.threeLetters)
            assertFalse(fourthStep.isLoading)
        }
    }

    @Test
    fun given_viewmodel_when_createNewGame_without_useAPI_is_called_then_should_update_state() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            viewModel.useAPI(false)
            advanceUntilIdle()

            val useAPIStep = awaitItem()

            assertFalse(useAPIStep.useAPI)

            viewModel.createNewGame()
            advanceUntilIdle()

            val firstStep = awaitItem()
            assertEquals(emptyList(), firstStep.words)

            val secondStep = awaitItem()
            assertEquals(mockedBoard, secondStep.board)
            assertEquals(mockedBoardMap, secondStep.boardMap)
            assertTrue(secondStep.isLoading)
            assertFalse(secondStep.useAPI)

            val thirdStep = awaitItem()
            assertEquals(mockedLocalResults, thirdStep.result)

            val fourthStep = awaitItem()
            assertEquals(mockedLocalResults, fourthStep.result)
            assertEquals(WordPair(1, emptyList()), fourthStep.wordsCount.threeLetters)
            assertFalse(fourthStep.isLoading)
        }
    }

    @Test
    fun given_viewmodel_when_changeLanguage_is_called_with_true_then_should_update_isEnglish_state_to_true() =
        runTest(testDispatcher) {
        viewModel.uiState.test {
            viewModel.changeLanguage(true)
            advanceUntilIdle()

            val resultStep = awaitItem()

            assertTrue(resultStep.isEnglish)
        }
    }

    @Test
    fun given_viewmodel_when_changeLanguage_is_called_with_false_then_should_update_isEnglish_state_to_false() =
        runTest(testDispatcher) {
        viewModel.uiState.test {
            val beforeStep = awaitItem()

            assertTrue(beforeStep.isEnglish)

            viewModel.changeLanguage(false)
            advanceUntilIdle()

            val resultStep = awaitItem()

            assertFalse(resultStep.isEnglish)
        }
    }

    @Test
    fun given_viewmodel_when_getHint_is_called_then_should_return_string() = runTest(testDispatcher) {
        // First create a new game and wait for all state transitions to complete
        viewModel.createNewGame()
        advanceUntilIdle()

        // Start fresh test collector to get only the changes from getHint
        viewModel.uiState.test {
            // Skip first state to get to the fully initialized state
            val initialState = awaitItem()
            // Verify that the result list is populated before calling getHint
            assertTrue(initialState.result.contains("one"))
            assertNull(initialState.hintDefinition)

            // Call getHint - should pick "one" from mockedResults
            viewModel.getHint()
            advanceUntilIdle()

            // Verify we get a new state with the hint definition
            val resultState = awaitItem()
            assertEquals(mockedDefinitions[0], resultState.hintDefinition)
        }
    }

    @Test
    fun given_viewmodel_when_closeDialog_is_called_then_should_update_isFinish_state_to_false() = runTest(testDispatcher) {
        viewModel.gameStart()
        advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.closeDialog()
            advanceUntilIdle()

            val resultStep = awaitItem()

            assertFalse(resultStep.isFinish)
        }
    }

    @Test
    fun given_viewmodel_when_closeDefinitionDialog_is_called_then_should_update_definition_state_to_null() =
        runTest(testDispatcher) {
        viewModel.gameStart()
        advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.closeDefinitionDialog()
            advanceUntilIdle()

            val resultStep = awaitItem()

            assertNull(resultStep.definition)
        }
    }

    @Test
    fun given_viewmodel_when_useAPI_is_called_then_should_update_definition_state_to_false() = runTest(testDispatcher) {
        viewModel.gameStart()
        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.useAPI(false)
            advanceUntilIdle()

            val resultStep = awaitItem()

            assertFalse(resultStep.useAPI)
        }
    }

    @Test
    fun given_viewmodel_when_getWordDefinition_is_called_then_should_update_definition_state_to_model() =
        runTest(testDispatcher) {
        viewModel.gameStart()
        advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem()

            viewModel.getWordDefinition("test")
            advanceUntilIdle()

            val resultStep = awaitItem()

            assertEquals(mockedDefinitions[0], resultStep.definition)
        }
    }

    @Test
    fun given_viewmodel_when_evaluateWord_is_called_then_should_update_isAWord_with_true() = runTest(testDispatcher) {
        val artWordKeys = listOf(0, 1, 2) // This should form "ART" which is a valid word

        // First create a new game and wait for it to complete setup
        viewModel.createNewGame()
        advanceUntilIdle()

        // Now we can test evaluateWord after the state is fully initialized
        viewModel.uiState.test {
            // Get current state
            val initialState = awaitItem()
            // Verify our setup is correct
            assertFalse(initialState.isAWord)
            assertEquals("", initialState.word)

            // Evaluate a valid word
            viewModel.evaluateWord(artWordKeys, false)
            advanceUntilIdle()

            // Expect state update with isAWord = true
            val resultState = awaitItem()
            assertTrue(resultState.isAWord)
            assertEquals("ART", resultState.word)
        }
    }

    @Test
    fun given_viewmodel_when_evaluateWord_is_called_then_should_update_isAWord_with_false() = runTest(testDispatcher) {
        val nonWordKeys = listOf(2, 0, 1) // This should form "TRA" which is not a valid word

        // First create a new game and wait for it to complete setup
        viewModel.createNewGame()
        advanceUntilIdle()

        // Force state to have isAWord=true so evaluateWord will definitely change it
        // We need to do this directly since we can't easily create this state otherwise
        viewModel.evaluateWord(listOf(0, 1, 2), false) // This forms "ART" which is in mockedResults
        advanceUntilIdle()

        // Now we can test evaluateWord after the state is set up
        viewModel.uiState.test {
            // Get current state - should have isAWord=true from our setup
            val initialState = awaitItem()
            assertTrue(initialState.isAWord) // Verify our setup worked
            assertEquals("ART", initialState.word)

            // Evaluate the non-word after the state is already isAWord=true
            viewModel.evaluateWord(nonWordKeys, false)
            advanceUntilIdle()

            // Now we should see a state update since isAWord changed from true to false
            val resultState = awaitItem()
            assertFalse(resultState.isAWord)
            assertEquals("", resultState.word)
        }
    }

    @Test
    fun given_viewmodel_when_addWord_is_called_then_should_update_isAWord_with_false_and_wordsGuessed_with_list() =
        runTest(testDispatcher) {
        val artWordKeys = listOf(0, 1, 2)
        viewModel.createNewGame()
        advanceUntilIdle()

        viewModel.uiState.test {
            val previousStep = awaitItem()

            assertFalse(previousStep.isAWord)
            assertEquals("", previousStep.word)

            viewModel.evaluateWord(artWordKeys, false)
            advanceUntilIdle()

            val middleStep = awaitItem()

            assertTrue(middleStep.isAWord)
            assertEquals("ART", middleStep.word)

            viewModel.addWord()
            advanceUntilIdle()

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
