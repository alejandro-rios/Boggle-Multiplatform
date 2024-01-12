package com.alejandrorios.bogglemultiplatform

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import com.alejandrorios.bogglemultiplatform.theme.md_theme_light_primary
import com.alejandrorios.bogglemultiplatform.viewmodel.BoggleViewModel
import com.alejandrorios.bogglemultiplatform.viewmodel.WordPair
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import kotlinx.coroutines.launch

@Composable
fun BoardScreen(modifier: Modifier = Modifier) {
    val boggleViewModel = getViewModel(Unit, viewModelFactory { BoggleViewModel() })
    val boggleUiState by boggleViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val checkedState = remember { mutableStateOf(true) }

    if (boggleUiState.isFinish) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = { boggleViewModel.closeDialog() }) {
                    Text("OK")
                }
            },
            title = { Text("Success") },
            text = { Text("You finished the game!") },
        )
    }

    if (boggleUiState.definition != null) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = { boggleViewModel.closeDefinitionDialog() }) {
                    Text("OK")
                }
            },
            title = { Text("Definition") },
            text = { Text("${boggleUiState.definition?.word}: ${boggleUiState.definition?.meanings?.first()?.definitions?.first()
                ?.definition}") },
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        content = { innerPadding ->
            if (boggleUiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(64.dp))
                }
            } else {
                Column(
                    modifier = modifier.padding(innerPadding).fillMaxWidth().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Text(
                                text = " ${boggleUiState.wordsGuessed.size} / ${boggleUiState.result.size} Words",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(
                                text = "Score: ${boggleUiState.score}",
                                fontSize = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        BoggleBoard(
                            board = boggleViewModel.board,
                            modifier = Modifier
                                .size(350.dp)
                                .background(
                                    color = Color(0xFF1F4E78),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            isAWord = boggleUiState.isAWord,
                            onDragEnded = { boggleViewModel.addWord() },
                            updateKeys = { values ->
                                boggleViewModel.evaluateWord(values)
                            }
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                modifier = Modifier.size(20.dp),
                                checked = boggleUiState.useAPI,
                                onCheckedChange = { isChecked -> boggleViewModel.useAPI(isChecked) }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Use API solver", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = checkedState.value,
                                enabled = !boggleUiState.useAPI,
                                onCheckedChange = {
                                    checkedState.value = it
                                    boggleViewModel.changeLanguage(it)
                                }
                            )
                            Text(text = "English", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                boggleViewModel.reloadBoard()
                            },
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = md_theme_light_primary)
                        ) {
                            Text(
                                text = "New game",
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {

                                scope.launch {
                                    val hint = boggleViewModel.getHint()
                                    snackBarHostState.showSnackbar("Try with: $hint")
                                }
                            },
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = md_theme_light_primary)
                        ) {
                            Text(
                                text = "Give me a hint",
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WordCounter(
                                numberOfLetters = "3",
                                wordPair = boggleUiState.wordsCount.threeLetters,
                                viewModel = boggleViewModel,
                            )
                            WordCounter(
                                numberOfLetters = "4",
                                wordPair = boggleUiState.wordsCount.fourLetters,
                                viewModel = boggleViewModel,
                            )
                            WordCounter(
                                numberOfLetters = "5",
                                wordPair = boggleUiState.wordsCount.fiveLetters,
                                viewModel = boggleViewModel,
                            )
                            WordCounter(
                                numberOfLetters = "6",
                                wordPair = boggleUiState.wordsCount.sixLetters,
                                viewModel = boggleViewModel,
                            )
                            WordCounter(
                                numberOfLetters = "7",
                                wordPair = boggleUiState.wordsCount.sevenLetters,
                                viewModel = boggleViewModel,
                            )
                            WordCounter(
                                numberOfLetters = "8+",
                                wordPair = boggleUiState.wordsCount.moreThanSevenLetters,
                                viewModel = boggleViewModel,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    )
}

@Composable
fun WordCounter(numberOfLetters: String, wordPair: WordPair, viewModel: BoggleViewModel) {
    if (wordPair.wordsTotal != 0) {
        val progress = wordPair.wordsFound.size / wordPair.wordsTotal.toFloat()
        val animatedProgress = animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "$numberOfLetters letters",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = wordPair.wordsTotal.toString(),
                    fontSize = 14.sp
                )
                CircularProgressIndicator(progress = animatedProgress, strokeWidth = 6.dp, backgroundColor = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            for (word in wordPair.wordsFound) {
                ClickableText(
                    text = AnnotatedString(word),
                    style = TextStyle(
                        textAlign = TextAlign.Start,
                        fontSize = 13.sp,
                        color = Color.Black
                    ),
                    onClick = {
                        viewModel.getWordDefinition(word)
                    }
                )
            }
        }
    }
}

@Composable
fun BoggleBoard(
    board: List<String>,
    modifier: Modifier = Modifier,
    isAWord: Boolean,
    onDragEnded: () -> Unit,
    updateKeys: (values: List<Int>) -> Unit
) {
    val selectedIds = rememberSaveable { mutableStateOf(emptySet<Int>()) }
    val state = rememberLazyGridState()

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(10.dp),
        modifier = modifier.photoGridDragHandler(state, selectedIds, onDragEnded, updateKeys),
        userScrollEnabled = false
    ) {
        items(board.size, key = { it }) { index ->
            val selected = selectedIds.value.contains(index)

            BoggleDie(
                letter = board[index],
                selected = selected,
                isAWord = isAWord,
                modifier = Modifier.clickable {
                    selectedIds.value = if (selected) {
                        selectedIds.value.minus(index)
                    } else {
                        selectedIds.value.plus(index)
                    }
                    updateKeys(selectedIds.value.toList())
                }
            )
        }
    }
}

/**
 * Code implementation taken and modified from:
 * https://medium.com/androiddevelopers/create-a-photo-grid-with-multiselect-behavior-using-jetpack-compose-9a8d588a9b63
 */

fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState,
    selectedIds: MutableState<Set<Int>>,
    onDragEnded: () -> Unit,
    update: (values: List<Int>) -> Unit
) = pointerInput(Unit) {
    var currentKey: Int? = null
    val selectedKeys = mutableListOf<Int>()

    detectDragGestures(
        onDragStart = { offset ->
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                currentKey = key
                selectedKeys.clear()
                selectedKeys.add(key)
                selectedIds.value = setOf(key)
            }
        },
        onDragCancel = {
            currentKey = null
            selectedKeys.clear()
            selectedIds.value = emptySet()
        },
        onDragEnd = {
            currentKey = null
            selectedKeys.clear()
            selectedIds.value = emptySet()
            onDragEnded()
        },
        onDrag = { change, _ ->
            lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                if (key != currentKey) {
                    if (selectedKeys.contains(key)) {
                        val keysToRemove = selectedKeys.subList(selectedKeys.indexOf(key) + 1, selectedKeys.size)
                        selectedKeys.removeAll(keysToRemove)
                        selectedIds.value = emptySet()
                        selectedIds.value = selectedKeys.toSet()
                    } else {
                        selectedKeys.add(key)
                        selectedIds.value += key
                    }
                    update(selectedIds.value.toList())
                    currentKey = key
                }
            }
        }
    )
}

fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
    }?.key as? Int
