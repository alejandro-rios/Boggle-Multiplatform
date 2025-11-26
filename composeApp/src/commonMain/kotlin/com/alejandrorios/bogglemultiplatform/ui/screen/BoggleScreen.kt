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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Plagiarism
import androidx.compose.material.icons.outlined.Rotate90DegreesCw
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun BoggleScreen(
    uiState: BoggleUiState,
    modifier: Modifier = Modifier,
    onGameStart: () -> Unit,
    onCreateNewGame: () -> Unit,
    onCloseDialog: () -> Unit,
    onCloseDefinitionDialog: () -> Unit,
    onCloseHintDefinitionDialog: () -> Unit,
    onAddWord: () -> Unit,
    onEvaluateWord: (dieKeys: List<Int>, isFromTap: Boolean) -> Unit,
    onUseAPI: (useAPI: Boolean) -> Unit,
    onChangeLanguage: (useAPI: Boolean) -> Unit,
    onGetHint: () -> Unit,
    onGetWordDefinition: (word: String) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val onRotateTriggered = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onGameStart()
    }

    if (uiState.isFinish) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = onCloseDialog) {
                    Text(stringResource(Res.string.label_ok))
                }
            },
            title = { Text(stringResource(Res.string.label_success)) },
            text = { Text(stringResource(Res.string.label_game_finished)) },
        )
    }

    if (uiState.definition != null) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = onCloseDefinitionDialog) {
                    Text(stringResource(Res.string.label_ok))
                }
            },
            title = { Text(stringResource(Res.string.label_definition)) },
            text = {
                Text(
                    "${uiState.definition.word}: ${
                        uiState.definition.meanings.first().definitions.first().definition
                    }"
                )
            },
        )
    }

    if (uiState.hintDefinition != null) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = onCloseHintDefinitionDialog) {
                    Text(stringResource(Res.string.label_ok))
                }
            },
            title = { Text(uiState.hintDefinition.getWordAsHint()) },
            text = {
                Text(
                    "${
                        uiState.hintDefinition.meanings.first().definitions.first().definition
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
            if (uiState.isLoading) {
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
                                text = stringResource(Res.string.total_words, uiState.result.size),
                                fontSize = 24.sp
                            )
                            HorizontalSpacer(spacing = 20.dp)
                            Text(
                                text = stringResource(Res.string.score, uiState.score),
                                fontSize = 24.sp
                            )
                        }
                        VerticalSpacer(spacing = 16.dp)
                        Text(
                            text = "${uiState.progress}%",
                            fontSize = 20.sp
                        )
                        VerticalSpacer(spacing = 20.dp)
                        BoggleBoard(
                            state = uiState,
                            modifier = Modifier.size(350.dp),
                            onDragEnded = onAddWord,
                            updateKeys = onEvaluateWord,
                            triggerRotation = onRotateTriggered.value,
                        ) {
                            onRotateTriggered.value = false
                        }
                        VerticalSpacer(spacing = 24.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                modifier = Modifier.size(20.dp),
                                checked = uiState.useAPI,
                                onCheckedChange = onUseAPI
                            )
                            HorizontalSpacer()
                            Text(text = stringResource(Res.string.use_api), fontSize = 20.sp)
                            HorizontalSpacer()
                            Switch(
                                checked = uiState.isEnglish,
                                enabled = !uiState.useAPI,
                                onCheckedChange = onChangeLanguage
                            )
                            Text(text = stringResource(Res.string.language), fontSize = 20.sp)
                        }
                        VerticalSpacer()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = onCreateNewGame,
                                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = md_theme_light_primary)
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
                                onClick = onGetHint,
                                content = { Icon(Icons.Outlined.Plagiarism, stringResource(Res.string.label_hint)) }
                            )
                        }
                        VerticalSpacer(spacing = 20.dp)
                        WordCounterRow(
                            wordsCount = uiState.wordsCount,
                            onWordClick = onGetWordDefinition,
                        )
                    }
                    VerticalSpacer(spacing = 20.dp)
                }
            }
        }
    )
}
