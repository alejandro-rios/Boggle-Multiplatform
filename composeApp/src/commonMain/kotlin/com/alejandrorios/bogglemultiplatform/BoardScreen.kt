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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import com.alejandrorios.bogglemultiplatform.theme.md_theme_light_primary
import com.alejandrorios.bogglemultiplatform.viewmodel.BoggleViewModel
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun BoardScreen(modifier: Modifier = Modifier) {
    var tileSize by remember { mutableStateOf(0) }
    val boggleViewModel = getViewModel(Unit, viewModelFactory { BoggleViewModel() })
    val boggleUiState by boggleViewModel.uiState.collectAsState()
    val checkedState = remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = " ${boggleUiState.wordsGuessed.size} / ${boggleUiState.result.size} Words",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            BoggleBoard(
                board = boggleViewModel.board,
                modifier = Modifier
                    .size(350.dp)
                    .onGloballyPositioned {
                        tileSize = (it.size.width - 30) / 4
                    }
                    .background(
                        color = Color(0xFF1F4E78),
                        shape = RoundedCornerShape(28.dp)
                    ),
                isAWord = boggleUiState.isAWord,
                onDragEnded = { boggleViewModel.addWord() },
                updateKeys = { values ->
                    boggleViewModel.evaluateWord(values)
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
//            Switch(
//                checked = checkedState.value,
//                onCheckedChange = { checkedState.value = it }
//            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    boggleViewModel.reloadBoard()
                },
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = md_theme_light_primary)
            ) {
                Text(
                    text = "Reload",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                WordCounter(
                    numberOfLetters = "3", numberOfWords = boggleUiState.wordsCount.threeLetters.wordsTotal,
                    wordsFoundIt = boggleUiState.wordsCount.threeLetters.wordsFound
                )
                Spacer(modifier = Modifier.width(8.dp))
                WordCounter(
                    numberOfLetters = "4", numberOfWords = boggleUiState.wordsCount.fourLetters.wordsTotal,
                    wordsFoundIt = boggleUiState.wordsCount.fourLetters.wordsFound
                )
                Spacer(modifier = Modifier.width(8.dp))
                WordCounter(
                    numberOfLetters = "5", numberOfWords = boggleUiState.wordsCount.fiveLetters.wordsTotal,
                    wordsFoundIt = boggleUiState.wordsCount.fiveLetters.wordsFound
                )
                Spacer(modifier = Modifier.width(8.dp))
                WordCounter(
                    numberOfLetters = "6", numberOfWords = boggleUiState.wordsCount.sixLetters.wordsTotal,
                    wordsFoundIt = boggleUiState.wordsCount.sixLetters.wordsFound
                )
                Spacer(modifier = Modifier.width(8.dp))
                WordCounter(
                    numberOfLetters = "7", numberOfWords = boggleUiState.wordsCount.sevenLetters.wordsTotal,
                    wordsFoundIt = boggleUiState.wordsCount.sevenLetters.wordsFound
                )
                Spacer(modifier = Modifier.width(8.dp))
                WordCounter(
                    numberOfLetters = "8+", numberOfWords = boggleUiState.wordsCount.moreThanSevenLetters.wordsTotal,
                    wordsFoundIt = boggleUiState.wordsCount.moreThanSevenLetters.wordsFound
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        for (word in boggleUiState.wordsGuessed) {
            Text(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                text = word,
                textAlign = TextAlign.Start,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun WordCounter(numberOfLetters: String, numberOfWords: Int = 0, wordsFoundIt: Int = 0) {
    if (numberOfWords != 0) {
        val progress = wordsFoundIt/numberOfWords.toFloat()
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
                    text = numberOfWords.toString(),
                    fontSize = 14.sp
                )
                CircularProgressIndicator(progress = animatedProgress, backgroundColor = Color.LightGray)
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
        contentPadding = PaddingValues(18.dp),
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
