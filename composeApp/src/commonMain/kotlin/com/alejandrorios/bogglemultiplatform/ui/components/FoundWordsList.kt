package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme

@Composable
fun FoundWordsList(
    words: List<String>,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxHeight: Int = 300
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight.dp),
        verticalArrangement = Arrangement.spacedBy(BoggleTheme.dimensions.spacing.xs)
    ) {
        items(
            items = words,
            key = { word -> word }
        ) { word ->
            FoundWordItem(
                word = word,
                onClick = { onWordClick(word) }
            )
        }
    }
}

@Composable
private fun FoundWordItem(
    word: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = word,
        style = BoggleTheme.typography.foundWord,
        color = Color.Black,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    )
}

