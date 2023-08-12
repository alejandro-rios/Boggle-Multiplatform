package com.alejandrorios.bogglemultiplatform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val WHITE_ALPHA = 0.8f
private const val SHADOW_ALPHA = 0.24f
private const val OFFSET = 16
private const val BLUR_RADIUS = 40f

@Composable
fun BoggleDie(text: String, modifier: Modifier = Modifier) {

    Card(
        modifier = modifier,
        backgroundColor = Color.LightGray
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(3.dp)
                .coloredShadow(
                    color = Color.White
                        .copy(alpha = WHITE_ALPHA),
                    offsetX = -OFFSET.dp,
                    offsetY = -OFFSET.dp,
                    blurRadius = BLUR_RADIUS,
                )
                .coloredShadow(
                    color = Color.DarkGray
                        .copy(alpha = SHADOW_ALPHA),
                    offsetX = OFFSET.dp,
                    offsetY = OFFSET.dp,
                    blurRadius = BLUR_RADIUS
                )
                .clip(CircleShape)
                .background(color = Color.LightGray)
                .layout { measurable, constraints ->
                    // Measure the composable
                    val placeable = measurable.measure(constraints)

                    //get the current max dimension to assign width=height
                    val currentHeight = placeable.height
                    var heightCircle = currentHeight
                    if (placeable.width > heightCircle)
                        heightCircle = placeable.width

                    //assign the dimension and the center position
                    layout(heightCircle, heightCircle) {
                        // Where the composable gets placed
                        placeable.placeRelative(0, (heightCircle - currentHeight) / 2)
                    }
                }
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

/**
 * Code implementation taken from:
 * https://blog.apter.tech/creating-colored-blurred-shadows-in-kotlin-multiplatform-mobile-with-compose-multiplatform-d48c53c68166
 */
fun Modifier.coloredShadow(
    color: Color,
    blurRadius: Float,
    offsetY: Dp,
    offsetX: Dp,
) = then(
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()

            if (blurRadius != 0f) {
                frameworkPaint.setMaskFilter(blurRadius)
            }

            frameworkPaint.color = color.toArgb()

            val centerX = size.width / 2 + offsetX.toPx()
            val centerY = size.height / 2 + offsetY.toPx()
            val radius = size.width.coerceAtLeast(size.height) / 2

            canvas.drawCircle(Offset(centerX, centerY), radius, paint)
        }
    }
)


//@Preview
@Composable
fun BoggleDiePreview() {
    BoggleDie(text = "B", modifier = Modifier.padding(4.dp))
}
