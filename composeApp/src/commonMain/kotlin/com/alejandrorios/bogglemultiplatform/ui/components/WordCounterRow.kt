package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import boggle_multiplatform.composeapp.generated.resources.Res
import boggle_multiplatform.composeapp.generated.resources.word_counter_3
import boggle_multiplatform.composeapp.generated.resources.word_counter_4
import boggle_multiplatform.composeapp.generated.resources.word_counter_5
import boggle_multiplatform.composeapp.generated.resources.word_counter_6
import boggle_multiplatform.composeapp.generated.resources.word_counter_7
import boggle_multiplatform.composeapp.generated.resources.word_counter_more
import com.alejandrorios.bogglemultiplatform.data.models.WordsCount
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun WordCounterRow(
    modifier: Modifier = Modifier,
    wordsCount: WordsCount,
    onWordClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = BoggleTheme.dimensions.spacing.md)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Center
    ) {
        WordCounter(
            numberOfLetters = stringResource(Res.string.word_counter_3),
            wordPair = wordsCount.threeLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = stringResource(Res.string.word_counter_4),
            wordPair = wordsCount.fourLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = stringResource(Res.string.word_counter_5),
            wordPair = wordsCount.fiveLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = stringResource(Res.string.word_counter_6),
            wordPair = wordsCount.sixLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = stringResource(Res.string.word_counter_7),
            wordPair = wordsCount.sevenLetters,
            onWordClick = { onWordClick(it) },
        )
        WordCounter(
            numberOfLetters = stringResource(Res.string.word_counter_more),
            wordPair = wordsCount.moreThanSevenLetters,
            onWordClick = { onWordClick(it) },
        )
    }
}
