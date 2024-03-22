package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import com.alejandrorios.bogglemultiplatform.isAndroid
import com.alejandrorios.bogglemultiplatform.utils.photoGridDragHandler

@Composable
fun BoggleBoard(
    progress: Float = 0f,
    wordsGuessed: String = "0",
    color: Color,
    board: List<String>,
    modifier: Modifier = Modifier,
    isAWord: Boolean,
    onDragEnded: () -> Unit,
    updateKeys: (values: List<Int>) -> Unit,
    triggerRotation: Boolean = false,
    onRotatedTriggered: () -> Unit,
) {
    val selectedIds = rememberSaveable { mutableStateOf(emptySet<Int>()) }
    val state = rememberLazyGridState()
    val updatedTrigger = rememberUpdatedState(triggerRotation)

    // This is the progress path which wis changed using path measure
    val pathWithProgress by remember { mutableStateOf(Path()) }

    // using path
    val pathMeasure by remember { mutableStateOf(PathMeasure()) }

    val path = remember { Path() }

    //TODO: Need it for draw text
//    val textMeasurer = rememberTextMeasurer()
//
//    val style = TextStyle(
//        fontSize = 13.sp,
//        fontWeight = FontWeight.Bold,
//        color = Color.White
//    )


    val borderProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    val isRotated = rememberSaveable { mutableFloatStateOf(0f) }
    val dieRotated = rememberSaveable { mutableFloatStateOf(0f) }
    val boardRotationAngle by animateFloatAsState(
        targetValue = isRotated.value,
        animationSpec = tween(durationMillis = 1000, easing = FastOutLinearInEasing),
        finishedListener = {
            dieRotated.value += -90f
        }
    )

    val dieRotationAngle by animateFloatAsState(
        targetValue = dieRotated.value,
        animationSpec = tween(durationMillis = 800, easing = FastOutLinearInEasing),
        finishedListener = {
            onRotatedTriggered()
        }
    )

    DisposableEffect(updatedTrigger.value) {
        if (updatedTrigger.value) {
            val rotatedValue = isRotated.value
            isRotated.value = rotatedValue + 90f
        }
        onDispose { }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .rotate(boardRotationAngle)
            .drawWithCache {
                var progressEndPos = Offset.Zero
                //TODO: Need it for draw text
//                val textLayoutResult = textMeasurer.measure(wordsGuessed, style)

                onDrawBehind {
                    scale(1f, -1f, Offset(size.width / 2, size.height / 2)) {
                        rotate(if (isAndroid) -90f else 0f) {
                            if (path.isEmpty) {
                                path.addRoundRect(
                                    RoundRect(
                                        Rect(offset = Offset.Zero, size),
                                        cornerRadius = CornerRadius(16.dp.toPx())
                                    )
                                )
                            }
                            drawPath(path, color = color)

                            pathWithProgress.reset()

                            pathMeasure.setPath(path, forceClosed = false)
                            pathMeasure.getSegment(
                                startDistance = 0f,
                                stopDistance = pathMeasure.length * borderProgress,
                                pathWithProgress,
                                startWithMoveTo = true
                            )

                            drawPath(
                                path = pathWithProgress,
                                style = Stroke(5.dp.toPx()),
                                color = Color(0xFFD28B2D)
                            )

                            progressEndPos =
                                if (pathMeasure.getPosition(pathMeasure.length * progress) != Offset.Unspecified) {
                                    val (x, y) = pathMeasure.getPosition(pathMeasure.length * progress)
                                    Offset(x, y)
                                } else {
                                    Offset(0f, 0f)
                                }

                            drawCircle(
                                color = Color(0xFFD28B2D),
                                center = progressEndPos,
                                radius = 10.dp.toPx()
                            )
                        }

                        // TODO: text in this position looks cut
//                        drawText(
//                            textMeasurer = textMeasurer,
//                            text = wordsGuessed,
//                            style = style,
//                            topLeft = Offset(
//                                x = progressEndPos.x - textLayoutResult.size.width / 2 + (textLayoutResult.size.width / 2 * 0.25f),
//                                y = progressEndPos.y - 0.75f * textLayoutResult.size.height,
//                            )
//                        )
                    }
                }
            }
    ) {

        LazyVerticalGrid(
            state = state,
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = modifier.photoGridDragHandler(state, selectedIds, onDragEnded, updateKeys),
            userScrollEnabled = false
        ) {
            items(board.size, key = { it }) { index ->
                val selected = selectedIds.value.contains(index)

                BoggleDie(
                    letter = board[index],
                    selected = selected,
                    isAWord = isAWord,
                    modifier = Modifier
                        .rotate(dieRotationAngle)
                        .clickable {
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
}
