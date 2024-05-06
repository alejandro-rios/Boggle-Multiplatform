package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(spacing: Dp = 10.dp) = Spacer(modifier = Modifier.height(spacing))

@Composable
fun HorizontalSpacer(spacing: Dp = 10.dp) = Spacer(modifier = Modifier.width(spacing))
