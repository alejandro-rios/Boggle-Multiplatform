package com.alejandrorios.bogglemultiplatform

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alejandrorios.bogglemultiplatform.theme.md_theme_light_primary
import com.alejandrorios.bogglemultiplatform.viewmodel.BoggleViewModel
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun BoardScreen(modifier: Modifier = Modifier) {
    var start by remember { mutableStateOf(Offset.Zero) }
    var tileSize by remember { mutableStateOf(0) }
    val boggleViewModel = getViewModel(Unit, viewModelFactory { BoggleViewModel() })
    val boggleUiState by boggleViewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = " ${boggleUiState.wordsGuessed.size} / ${boggleUiState.result.size - boggleUiState.wordsGuessed.size} Words",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            BoggleBoard(
                board = boggleViewModel.board,
                modifier = Modifier
                    .size(350.dp)
                    .onGloballyPositioned {
                        // (Board width - padding) / 4
                        tileSize = (it.size.width - 8) / 4
                    }
                    .background(
                        color = Color(0xFF1F4E78),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                start = change.position
                                // Calculate the index of the letter being touched
                                val index = (start.y / tileSize).toInt() * 4 + (start.x / tileSize).toInt()
                                if (index >= 0 && index < boggleViewModel.board.size) {
                                    boggleViewModel.onBoardSwipe(index)
                                }
                            },
                            onDragEnd = {
                                boggleViewModel.evaluateWord()
                            }
                        )
                    },
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    boggleViewModel.reloadBoard()
                },
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = md_theme_light_primary)
            ) {
                Text(
                    text = "Reload",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        for (word in boggleUiState.wordsGuessed) {
            Text(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                text = word,
                textAlign = TextAlign.Start,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BoggleBoard(
    board: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp),
        userScrollEnabled = false
    ) {
        items(board.size) { index ->
            BoggleDie(
                letter = board[index],
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}
