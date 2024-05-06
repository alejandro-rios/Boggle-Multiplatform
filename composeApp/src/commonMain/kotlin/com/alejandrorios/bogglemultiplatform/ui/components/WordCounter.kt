package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alejandrorios.bogglemultiplatform.data.models.WordPair

@Composable
fun WordCounter(
    numberOfLetters: String,
    wordPair: WordPair,
    onWordClick: (String) -> Unit,
) {
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
                text = numberOfLetters,
                fontSize = 14.sp
            )
            VerticalSpacer()
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = wordPair.wordsTotal.toString(),
                    fontSize = 14.sp
                )
                CircularProgressIndicator(progress = animatedProgress, strokeWidth = 6.dp, backgroundColor = Color.LightGray)
            }
            VerticalSpacer()
            for (word in wordPair.wordsFound) {
                ClickableText(
                    text = AnnotatedString(word),
                    style = TextStyle(
                        textAlign = TextAlign.Start,
                        fontSize = 13.sp,
                        color = Color.Black
                    ),
                    onClick = {
                        onWordClick(word)
                    }
                )
            }
        }
    }
}
