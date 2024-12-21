package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoggleDie(
    letter: String,
    selected: Boolean,
    isAWord: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = getCardColor(selected, isAWord)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(5.dp)
                .clip(CircleShape)
                .background(color = getBoxColor(selected, isAWord))
                // If this is removed you'll see the difference only on Android
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
                text = letter,
                fontSize = if (letter == "Qu") 32.sp else 38.sp,
                color = getLetterColor(selected),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

fun getCardColor(selected: Boolean, isAWord: Boolean): Color = when {
    selected && isAWord -> Color(0xFFD28B2D)
    selected -> Color(0xFF1F4E78)
    else -> Color.LightGray
}

fun getBoxColor(selected: Boolean, isAWord: Boolean): Color = when {
    selected && isAWord -> Color(0xFFD28B2D)
    selected -> Color(0xFF5F666E)
    else -> Color.White
}

fun getLetterColor(selected: Boolean): Color = when {
    selected -> Color.White
    else -> Color.Black
}
