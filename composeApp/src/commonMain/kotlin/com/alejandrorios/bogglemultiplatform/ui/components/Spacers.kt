package com.alejandrorios.bogglemultiplatform.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.alejandrorios.bogglemultiplatform.ui.theme.BoggleTheme

@Composable
fun VerticalSpacer(spacing: Dp = BoggleTheme.dimensions.spacing.sm) = Spacer(modifier = Modifier.height(spacing))

@Composable
fun HorizontalSpacer(spacing: Dp = BoggleTheme.dimensions.spacing.sm) = Spacer(modifier = Modifier.width(spacing))
