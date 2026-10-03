package com.formsense.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import com.formsense.app.ui.theme.FormSenseTheme
import androidx.compose.ui.unit.dp
import com.formsense.app.ui.components.FormSenseBottomBar
import com.formsense.app.ui.theme.FormBlue

@Composable
fun PlaceholderScreen(
    title: String,
    message: String,
    selectedRoute: String,
    onBottomDestination: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            FormSenseBottomBar(
                selectedRoute = selectedRoute,
                onDestinationSelected = onBottomDestination,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Construction,
                contentDescription = null,
                tint = FormBlue,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun PlaceholderScreenPreview() {
    FormSenseTheme {
        PlaceholderScreen(
            title = "Under Construction",
            message = "This feature is coming soon to FormSense.",
            selectedRoute = "placeholder",
            onBottomDestination = {},
        )
    }
}