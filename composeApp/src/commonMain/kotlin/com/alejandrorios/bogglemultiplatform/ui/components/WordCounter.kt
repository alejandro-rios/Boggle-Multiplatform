package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.alejandrorios.bogglemultiplatform.data.models.WordPair
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme

@Composable
fun WordCounter(
    numberOfLetters: String,
    wordPair: WordPair,
    onWordClick: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = wordPair.wordsTotal != 0,
    ) {
        val progress = wordPair.wordsFound.size / wordPair.wordsTotal.toFloat()
        val animatedProgress = animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        Column(
            modifier = Modifier.padding(horizontal = BoggleTheme.dimensions.spacing.md),
            verticalArrangement = Arrangement.spacedBy(BoggleTheme.dimensions.spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = numberOfLetters,
                style = BoggleTheme.typography.counter,
            )
            Box(
                modifier = Modifier.background(
                    color = Color(0xFFEBEBEB),
                    shape = RoundedCornerShape(BoggleTheme.dimensions.cornerRadius.full)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${wordPair.wordsTotal - wordPair.wordsFound.size}",
                    style = BoggleTheme.typography.counter,
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    strokeWidth = BoggleTheme.dimensions.strokeWidth.wordCounterProgress,
                    trackColor = Color(0xFFEBEBEB),
                    strokeCap = StrokeCap.Round
                )
            }
            FoundWordsList(
                words = wordPair.wordsFound,
                onWordClick = onWordClick,
            )
        }
    }
}
