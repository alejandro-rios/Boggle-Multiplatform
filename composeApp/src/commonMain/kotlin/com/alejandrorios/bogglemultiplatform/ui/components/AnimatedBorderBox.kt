package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.alejandrorios.bogglemultiplatform.currentPlatform
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme

@Composable
fun AnimatedBorderBox(
    borderProgress: Float,
    triggerRotation: Boolean,
    backgroundColor: Color = Color(0xFF1F4E78),
    borderColor: Color = Color(0xFF9C9C9C),
    progressColor: Color = Color(0xFFD28B2D),
    cornerRadius: Dp = BoggleTheme.dimensions.cornerRadius.md,
    strokeWidth: Dp = BoggleTheme.dimensions.strokeWidth.borderBox,
    onProgressComplete: () -> Unit = {},
    onRotationComplete: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(dieRotationAngle: Float) -> Unit
) {
    // Rotation states
    var boardRotation by rememberSaveable { mutableFloatStateOf(0f) }
    var dieRotation by rememberSaveable { mutableFloatStateOf(0f) }

    val animatedBorderProgress by animateFloatAsState(
        targetValue = borderProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "borderProgress",
        finishedListener = { onProgressComplete() }
    )

    val boardRotationAngle by animateFloatAsState(
        targetValue = boardRotation,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutLinearInEasing
        ),
        label = "boardRotation",
        finishedListener = {
            dieRotation -= 90f
        }
    )

    val dieRotationAngle by animateFloatAsState(
        targetValue = dieRotation,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutLinearInEasing
        ),
        label = "dieRotation",
        finishedListener = { onRotationComplete() }
    )

    LaunchedEffect(triggerRotation) {
        if (triggerRotation) {
            boardRotation += 90f
        }
    }

    // Drawing constants
    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }
    val isAndroid = currentPlatform.isAndroid

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .drawWithCache {
                val mainPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(offset = Offset.Zero, size = size),
                            cornerRadius = CornerRadius(cornerRadiusPx)
                        )
                    )
                }

                val pathMeasure = PathMeasure().apply {
                    setPath(mainPath, forceClosed = false)
                }
                val totalLength = pathMeasure.length

                onDrawBehind {
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                    val baseRotation = if (isAndroid) -90f else 0f

                    scale(1f, -1f, centerOffset) {
                        rotate(baseRotation, centerOffset) {
                            // Base box
                            drawPath(mainPath, color = backgroundColor)

                            // Background border
                            drawPath(
                                path = mainPath,
                                style = Stroke(width = strokeWidthPx),
                                color = borderColor
                            )

                            // Progress border rounded corners
                            if (animatedBorderProgress > 0f) {
                                val progressPath = Path()
                                pathMeasure.getSegment(
                                    startDistance = 0f,
                                    stopDistance = totalLength * animatedBorderProgress,
                                    destination = progressPath,
                                    startWithMoveTo = true
                                )

                                drawPath(
                                    path = progressPath,
                                    style = Stroke(
                                        width = strokeWidthPx,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    ),
                                    color = progressColor
                                )
                            }
                        }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .rotate(boardRotationAngle),
            contentAlignment = Alignment.Center
        ) {
            content(dieRotationAngle)
        }
    }
}
