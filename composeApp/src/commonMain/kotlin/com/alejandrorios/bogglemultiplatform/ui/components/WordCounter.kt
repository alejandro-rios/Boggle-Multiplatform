package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alejandrorios.bogglemultiplatform.currentPlatform
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
            modifier = Modifier.padding(horizontal = if (currentPlatform.isWeb) 32.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = numberOfLetters,
                fontSize = 14.sp
            )
            VerticalSpacer()
            Box(
                modifier = Modifier.background(color = Color(0xFFEBEBEB), shape = RoundedCornerShape(100)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${wordPair.wordsTotal - wordPair.wordsFound.size}",
                    fontSize = 14.sp
                )
                CircularProgressIndicator(
                    progress = animatedProgress,
                    strokeWidth = 6.dp,
                    backgroundColor = Color(0xFFEBEBEB),
                    strokeCap = StrokeCap.Round
                )
            }
            VerticalSpacer()
            for (word in wordPair.wordsFound) {
                BasicText(
                    text = buildAnnotatedString {
                        pushStringAnnotation(tag = "get_word_definition", annotation = word)
                        withStyle(style = SpanStyle(fontSize = 13.sp, color = Color.Black)) {
                            append(word)
                        }
                        pop()
                    },
                    style = TextStyle(textAlign = TextAlign.Start),
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onWordClick(word) }
                )
            }
        }
    }
}
