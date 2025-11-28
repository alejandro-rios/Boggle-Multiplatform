package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme
import com.alejandrorios.bogglemultiplatform.ui.theme.boardLetter
import com.alejandrorios.bogglemultiplatform.utils.cardElevationZero
import com.alejandrorios.bogglemultiplatform.utils.isQ
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BoggleDie(
    letter: String,
    selected: Boolean,
    isAWord: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(BoggleTheme.dimensions.cornerRadius.md),
        elevation = CardDefaults.cardElevation(cardElevationZero.dp),
        colors = CardDefaults.cardColors(
            containerColor = getCardColor(selected, isAWord),
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(BoggleTheme.dimensions.spacing.xxs)
                .clip(CircleShape)
                .background(color = getBoxColor(selected, isAWord))
                .fillMaxSize()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    // Calculate the largest dimension to make a perfect circle
                    val currentHeight = placeable.height
                    var heightCircle = currentHeight
                    if (placeable.width > heightCircle)
                        heightCircle = placeable.width

                    layout(heightCircle, heightCircle) {
                        placeable.placeRelative(0, (heightCircle - currentHeight) / 2)
                    }
                }
        ) {
            Text(
                text = letter,
                style = BoggleTheme.typography.boardLetter(letter.isQ()),
                color = getLetterColor(selected),
                modifier = Modifier.padding(BoggleTheme.dimensions.spacing.xxs)
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

@Preview(showBackground = true)
@Composable
fun NormalDie() {
    BoggleDie(letter = "Qu", selected = false, modifier = Modifier.padding(BoggleTheme.dimensions.spacing.sm))
}

@Preview(showBackground = true)
@Composable
fun SelectedDie() {
    BoggleDie(letter = "B", selected = true, modifier = Modifier.padding(BoggleTheme.dimensions.spacing.sm))
}

@Preview(showBackground = true)
@Composable
fun WordDie() {
    BoggleDie(letter = "B", selected = true, isAWord = true, modifier = Modifier.padding(BoggleTheme.dimensions.spacing.sm))
}
