package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.currentPlatform
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount

@Composable
fun WordCounterRow(
    modifier: Modifier = Modifier,
    wordsCount: WordsCount,
    onWordClick: (String) -> Unit
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        horizontalArrangement = if(currentPlatform.isWeb) Arrangement.Center else Arrangement.SpaceEvenly
    ) {
        WordCounter(
            numberOfLetters = "3",
            wordPair = wordsCount.threeLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = "4",
            wordPair = wordsCount.fourLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = "5",
            wordPair = wordsCount.fiveLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = "6",
            wordPair = wordsCount.sixLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = "7",
            wordPair = wordsCount.sevenLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = "8+",
            wordPair = wordsCount.moreThanSevenLetters,
            onWordClick = { onWordClick(it) },
        )
    }
}
