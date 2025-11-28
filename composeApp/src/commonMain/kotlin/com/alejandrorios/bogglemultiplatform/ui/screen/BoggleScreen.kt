package com.alejandrorios.bogglemultiplatform.ui.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Plagiarism
import androidx.compose.material.icons.outlined.Rotate90DegreesCw
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import boggle_multiplatform.composeapp.generated.resources.Res
import boggle_multiplatform.composeapp.generated.resources.definition_body
import boggle_multiplatform.composeapp.generated.resources.game_progress
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
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme
import com.alejandrorios.bogglemultiplatform.utils.BoggleBoardSize
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
                    stringResource(
                        Res.string.definition_body,
                        uiState.definitionWord,
                        uiState.firstDefinition
                    )
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
                    CircularProgressIndicator(modifier = Modifier.width(BoggleTheme.dimensions.spacing.xxxxxxl))
                }
            } else {
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        VerticalSpacer(spacing = BoggleTheme.dimensions.spacing.md)
                        Row {
                            Text(
                                text = stringResource(Res.string.total_words, uiState.result.size),
                                style = BoggleTheme.typography.gameStatsLarge
                            )
                            HorizontalSpacer(spacing = BoggleTheme.dimensions.spacing.lg)
                            Text(
                                text = stringResource(Res.string.score, uiState.score),
                                style = BoggleTheme.typography.gameStatsLarge
                            )
                        }
                        VerticalSpacer(spacing = BoggleTheme.dimensions.spacing.md)
                        Text(
                            text = stringResource(Res.string.game_progress, uiState.progress),
                            style = BoggleTheme.typography.gameStatsMedium
                        )
                        VerticalSpacer(spacing = BoggleTheme.dimensions.spacing.lg)
                        BoggleBoard(
                            state = uiState,
                            modifier = Modifier.size(BoggleBoardSize.dp),
                            onDragEnded = onAddWord,
                            updateKeys = onEvaluateWord,
                            triggerRotation = onRotateTriggered.value,
                        ) {
                            onRotateTriggered.value = false
                        }
                        VerticalSpacer(spacing = BoggleTheme.dimensions.spacing.xl)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(BoggleTheme.dimensions.spacing.sm),
                        ) {
                            Checkbox(
                                modifier = Modifier.size(BoggleTheme.dimensions.spacing.lg),
                                checked = uiState.useAPI,
                                onCheckedChange = onUseAPI
                            )
                            Text(
                                text = stringResource(Res.string.use_api),
                                style = BoggleTheme.typography.button,
                            )
                            Switch(
                                checked = uiState.isEnglish,
                                enabled = !uiState.useAPI,
                                onCheckedChange = onChangeLanguage
                            )
                            Text(
                                text = stringResource(Res.string.language),
                                style = BoggleTheme.typography.button,
                            )
                        }
                        VerticalSpacer()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(BoggleTheme.dimensions.spacing.sm),
                        ) {
                            Button(
                                onClick = onCreateNewGame,
                                contentPadding = PaddingValues(
                                    vertical = BoggleTheme.dimensions.spacing.sm,
                                    horizontal = BoggleTheme.dimensions.spacing.md,
                                ),
                            ) {
                                Text(
                                    text = stringResource(Res.string.new_game),
                                    style = BoggleTheme.typography.button,
                                    color = Color.White
                                )
                            }
                            FilledIconButton(
                                enabled = !onRotateTriggered.value,
                                onClick = {
                                    onRotateTriggered.value = true
                                },
                                content = {
                                    Icon(
                                        Icons.Outlined.Rotate90DegreesCw,
                                        stringResource(Res.string.rotate),
                                    )
                                }
                            )
                            FilledIconButton(
                                onClick = onGetHint,
                                content = {
                                    Icon(
                                        Icons.Outlined.Plagiarism,
                                        stringResource(Res.string.label_hint),
                                    )
                                }
                            )
                        }
                        VerticalSpacer(spacing = BoggleTheme.dimensions.spacing.lg)
                        WordCounterRow(
                            wordsCount = uiState.wordsCount,
                            onWordClick = onGetWordDefinition,
                        )
                    }
                    VerticalSpacer(spacing = BoggleTheme.dimensions.spacing.lg)
                }
            }
        }
    )
}
