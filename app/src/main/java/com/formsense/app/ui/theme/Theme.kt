package com.formsense.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val FormSenseColors = lightColorScheme(
    primary = FormBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = FormBlueSoft,
    onPrimaryContainer = Ink,
    secondary = FormBlueDark,
    background = Canvas,
    onBackground = Ink,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Ink,
    surfaceVariant = FormBluePale,
    onSurfaceVariant = Slate,
    outline = SurfaceBorder,
    error = Danger,
)

private val FormSenseShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun FormSenseTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FormSenseColors,
        typography = FormSenseTypography,
        shapes = FormSenseShapes,
        content = content,
    )
}
