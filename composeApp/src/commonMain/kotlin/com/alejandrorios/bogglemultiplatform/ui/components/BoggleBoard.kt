package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.currentPlatform
import com.alejandrorios.bogglemultiplatform.ui.screen.BoggleUiState
import com.alejandrorios.bogglemultiplatform.utils.boggleDieModifier
import com.alejandrorios.bogglemultiplatform.utils.boggleBoardDragHandler

@Composable
fun BoggleBoard(
    state: BoggleUiState,
    color: Color = Color(0xFF1F4E78),
    modifier: Modifier = Modifier,
    onDragEnded: () -> Unit,
    updateKeys: (values: List<Int>, isFromTap: Boolean) -> Unit,
    triggerRotation: Boolean = false,
    onRotatedTriggered: () -> Unit,
) {
    val selectedIds = rememberSaveable { mutableStateOf(emptySet<Int>()) }
    val listState = rememberLazyGridState()

    // State holders
    val updatedTrigger = rememberUpdatedState(triggerRotation)
    val pathWithProgress = remember { Path() }
    val fullPath = remember { Path() }
    val pathMeasure = remember { PathMeasure() }
    val path = remember { Path() }

    // Animation states
    val borderProgress by animateFloatAsState(
        targetValue = state.wordsGuessed.size / state.result.size.toFloat(),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        finishedListener = {
            selectedIds.value = emptySet()
        }
    )

    // Rotation states with their animations
    val (boardRotation, setBoardRotation) = rememberSaveable { mutableFloatStateOf(0f) }
    val (dieRotation, setDieRotation) = rememberSaveable { mutableFloatStateOf(0f) }

    val boardRotationAngle by animateFloatAsState(
        targetValue = boardRotation,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutLinearInEasing
        ),
        finishedListener = {
            setDieRotation(dieRotation - 90f)
        }
    )

    val dieRotationAngle by animateFloatAsState(
        targetValue = dieRotation,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutLinearInEasing
        ),
        finishedListener = { onRotatedTriggered() }
    )

    // Handle rotation trigger
    DisposableEffect(updatedTrigger.value) {
        if (updatedTrigger.value) {
            setBoardRotation(boardRotation + 90f)
        }
        onDispose { }
    }

    // Custom drawing
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .rotate(boardRotationAngle)
            .drawWithCache {
                // Create the path only once during cache creation
                val cornerRadiusPx = 16.dp.toPx()
                val strokeWidthPx = 10.dp.toPx()

                path.reset()
                path.addRoundRect(
                    RoundRect(
                        Rect(offset = Offset.Zero, size),
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                )

                onDrawBehind {
                    scale(1f, -1f, Offset(size.width / 2, size.height / 2)) {
                        rotate(if (currentPlatform.isAndroid) -90f else 0f) {
                            // Draw base rectangle
                            drawPath(path, color = color)

                            // Update progress path
                            pathWithProgress.reset()
                            pathMeasure.setPath(path, forceClosed = false)

                            // Calculate progress segments
                            pathMeasure.getSegment(
                                startDistance = 0f,
                                stopDistance = pathMeasure.length * borderProgress,
                                pathWithProgress,
                                startWithMoveTo = true
                            )

                            // Update full path
                            fullPath.reset()
                            pathMeasure.getSegment(
                                startDistance = 0f,
                                stopDistance = pathMeasure.length,
                                fullPath,
                                startWithMoveTo = true
                            )

                            // Draw background path
                            drawPath(
                                path = fullPath,
                                style = Stroke(strokeWidthPx),
                                color = Color(0xFF9C9C9C)
                            )

                            // Draw progress path
                            drawPath(
                                path = pathWithProgress,
                                style = Stroke(strokeWidthPx),
                                color = Color(0xFFD28B2D)
                            )
                        }
                    }
                }
            }
    ) {
        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(20.dp),
            modifier = modifier.boggleBoardDragHandler(listState, selectedIds, onDragEnded, updateKeys),
            userScrollEnabled = false
        ) {
            items(state.board.size, key = { it }) { index ->
                val selected = selectedIds.value.contains(index)
                val interactionSource = remember { MutableInteractionSource() }

                BoggleDie(
                    letter = state.board[index],
                    selected = selected,
                    isAWord = state.isAWord,
                    modifier = Modifier
                        .rotate(dieRotationAngle)
                        .boggleDieModifier(
                            interactionSource = interactionSource,
                            onClick = {
                                selectedIds.value = if (selected) {
                                    selectedIds.value.minus(index)
                                } else {
                                    selectedIds.value.plus(index)
                                }
                                updateKeys(selectedIds.value.toList(), true)
                            },
                            selectedKeys = selectedIds.value.toList(),
                            index = index
                        )
                )
            }
        }
    }
}
