package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme
import com.alejandrorios.bogglemultiplatform.utils.BoggleBoardGridSize
import com.alejandrorios.bogglemultiplatform.utils.boggleBoardDragHandler
import com.alejandrorios.bogglemultiplatform.utils.boggleDieModifier

@Composable
fun BoggleBoard(
    state: BoggleUiState,
    modifier: Modifier = Modifier,
    onDragEnded: () -> Unit,
    updateKeys: (values: List<Int>, isFromTap: Boolean) -> Unit,
    triggerRotation: Boolean = false,
    onRotatedTriggered: () -> Unit,
) {
    val selectedIds = rememberSaveable { mutableStateOf(emptySet<Int>()) }
    val listState = rememberLazyGridState()

    val borderProgress = remember(state.wordsGuessed.size, state.result.size) {
        if (state.result.isEmpty()) {
            0f
        } else {
            state.wordsGuessed.size / state.result.size.toFloat()
        }
    }

    AnimatedBorderBox(
        borderProgress = borderProgress,
        triggerRotation = triggerRotation,
        onProgressComplete = {
            selectedIds.value = emptySet()
        },
        onRotationComplete = onRotatedTriggered,
        modifier = modifier
    ) { dieRotationAngle ->

        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(BoggleBoardGridSize),
            verticalArrangement = Arrangement.spacedBy(BoggleTheme.dimensions.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(BoggleTheme.dimensions.spacing.md),
            contentPadding = PaddingValues(BoggleTheme.dimensions.spacing.lg),
            modifier = modifier.boggleBoardDragHandler(listState, selectedIds, onDragEnded, updateKeys),
            userScrollEnabled = false
        ) {
            items(state.board.size, key = { it }) { index ->
                val selected = selectedIds.value.contains(index)

                BoggleDie(
                    letter = state.board[index],
                    selected = selected,
                    isAWord = state.isAWord,
                    modifier = Modifier
                        .rotate(dieRotationAngle)
                        .boggleDieModifier(
                            onClick = {
                                if (selected) {
                                    selectedIds.value = emptySet()
                                    updateKeys(emptyList(), true)
                                } else {
                                    selectedIds.value = selectedIds.value.plus(index)
                                    updateKeys(selectedIds.value.toList(), true)
                                }
                            },
                            selectedKeys = selectedIds.value.toList(),
                            index = index
                        )
                )
            }
        }
    }
}
