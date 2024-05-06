package com.alejandrorios.bogglemultiplatform.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Plagiarism
import androidx.compose.material.icons.outlined.Rotate90DegreesCw
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boggle_multiplatform.composeapp.generated.resources.Res
import boggle_multiplatform.composeapp.generated.resources.label_definition
import boggle_multiplatform.composeapp.generated.resources.label_game_finished
import boggle_multiplatform.composeapp.generated.resources.label_hint
import boggle_multiplatform.composeapp.generated.resources.label_ok
import boggle_multiplatform.composeapp.generated.resources.label_success
import boggle_multiplatform.composeapp.generated.resources.language
import boggle_multiplatform.composeapp.generated.resources.new_game
import boggle_multiplatform.composeapp.generated.resources.rotate
import boggle_multiplatform.composeapp.generated.resources.score
import boggle_multiplatform.composeapp.generated.resources.total_words
import boggle_multiplatform.composeapp.generated.resources.use_api
import com.alejandrorios.bogglemultiplatform.ui.components.BoggleBoard
import com.alejandrorios.bogglemultiplatform.ui.components.HorizontalSpacer
import com.alejandrorios.bogglemultiplatform.ui.components.VerticalSpacer
import com.alejandrorios.bogglemultiplatform.ui.components.WordCounterRow
import com.alejandrorios.bogglemultiplatform.ui.theme.md_theme_light_onPrimary
import com.alejandrorios.bogglemultiplatform.ui.theme.md_theme_light_primary
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun BoggleScreen(modifier: Modifier = Modifier, boggleViewModel: BoggleViewModel = koinInject()) {
    val boggleUiState by boggleViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val onRotateTriggered = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        boggleViewModel.gameStart()
    }

    if (boggleUiState.isFinish) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = boggleViewModel::closeDialog) {
                    Text(stringResource(Res.string.label_ok))
                }
            },
            title = { Text(stringResource(Res.string.label_success)) },
            text = { Text(stringResource(Res.string.label_game_finished)) },
        )
    }

    if (boggleUiState.definition != null) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = boggleViewModel::closeDefinitionDialog) {
                    Text(stringResource(Res.string.label_ok))
                }
            },
            title = { Text(stringResource(Res.string.label_definition)) },
            text = {
                Text(
                    "${boggleUiState.definition?.word}: ${
                        boggleUiState.definition?.meanings?.first()?.definitions?.first()
                            ?.definition
                    }"
                )
            },
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        content = { innerPadding ->
            if (boggleUiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.width(64.dp))
                }
            } else {
                Column(
                    modifier = modifier.padding(innerPadding).fillMaxWidth().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        VerticalSpacer(spacing = 16.dp)
                        Row {
                            Text(
                                text = stringResource(Res.string.total_words, boggleUiState.result.size),
                                fontSize = 24.sp
                            )
                            HorizontalSpacer(spacing = 20.dp)
                            Text(
                                text = stringResource(Res.string.score, boggleUiState.score),
                                fontSize = 24.sp
                            )
                        }
                        VerticalSpacer(spacing = 20.dp)
                        BoggleBoard(
                            progress = boggleUiState.wordsGuessed.size / boggleUiState.result.size.toFloat(),
                            wordsGuessed = boggleUiState.wordsGuessed.size.toString(),
                            color = Color(0xFF1F4E78),
                            board = boggleUiState.board,
                            modifier = Modifier.size(350.dp),
                            isAWord = boggleUiState.isAWord,
                            onDragEnded = boggleViewModel::addWord,
                            updateKeys = boggleViewModel::evaluateWord,
                            triggerRotation = onRotateTriggered.value,
                        ) {
                            onRotateTriggered.value = false
                        }
                        VerticalSpacer(spacing = 24.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                modifier = Modifier.size(20.dp),
                                checked = boggleUiState.useAPI,
                                onCheckedChange = boggleViewModel::useAPI
                            )
                            HorizontalSpacer()
                            Text(text = stringResource(Res.string.use_api), fontSize = 20.sp)
                            HorizontalSpacer()
                            Switch(
                                checked = boggleUiState.isEnglish,
                                enabled = !boggleUiState.useAPI,
                                onCheckedChange = boggleViewModel::changeLanguage
                            )
                            Text(text = stringResource(Res.string.language), fontSize = 20.sp)
                        }
                        VerticalSpacer()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = boggleViewModel::createNewGame,
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = md_theme_light_primary)
                            ) {
                                Text(
                                    text = stringResource(Res.string.new_game),
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                            HorizontalSpacer()
                            FilledIconButton(
                                enabled = !onRotateTriggered.value,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = md_theme_light_primary,
                                    contentColor = md_theme_light_onPrimary,
                                ),
                                onClick = {
                                    onRotateTriggered.value = true
                                },
                                content = { Icon(Icons.Outlined.Rotate90DegreesCw, stringResource(Res.string.rotate)) }
                            )
                            HorizontalSpacer()
                            FilledIconButton(
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = md_theme_light_primary,
                                    contentColor = md_theme_light_onPrimary,
                                ),
                                onClick = {
                                    scope.launch {
                                        val hint = boggleViewModel.getHint()
                                        snackBarHostState.showSnackbar("Try with: $hint")
                                    }
                                },
                                content = { Icon(Icons.Outlined.Plagiarism, stringResource(Res.string.label_hint)) }
                            )
                        }
                        VerticalSpacer(spacing = 20.dp)
                        WordCounterRow(
                            wordsCount = boggleUiState.wordsCount,
                            onWordClick = boggleViewModel::getWordDefinition,
                        )
                    }
                    VerticalSpacer(spacing = 20.dp)
                }
            }
        }
    )
}
