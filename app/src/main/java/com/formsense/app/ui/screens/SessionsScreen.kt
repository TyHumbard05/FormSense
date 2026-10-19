package com.formsense.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.formsense.app.navigation.Routes
import com.formsense.app.ui.components.FormSenseBottomBar
import com.formsense.app.ui.components.RecentSessionCard
import com.formsense.app.ui.history.HistoryUiState
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.RecentSession

@Composable
fun SessionsScreen(
    state: HistoryUiState,
    onSessionSelected: (Long) -> Unit,
    onBottomDestination: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            FormSenseBottomBar(
                selectedRoute = Routes.Sessions,
                onDestinationSelected = onBottomDestination,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Review your completed workouts and form scores.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            when {
                state.isLoading -> item { Text("Loading sessions…") }
                state.sessions.isEmpty() -> item { EmptyHistory() }
                else -> items(state.sessions.size, key = { state.sessions[it].id }) { index ->
                    val session = state.sessions[index]
                    RecentSessionCard(
                        session = session,
                        onClick = { onSessionSelected(session.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory() {
    Column(
        modifier = Modifier.padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.History,
            contentDescription = null,
            tint = FormBlue,
        )
        Text(
            text = "No completed sessions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Your workout history will appear here after your first session.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun SessionsScreenPreview() {
    val sampleSession = RecentSession(
        id = 1L,
        exercise = ExerciseType.Squat,
        startedAtMillis = System.currentTimeMillis() - 300_000,
        endedAtMillis = System.currentTimeMillis(),
        targetReps = 12,
        completedReps = 12,
        averageScore = 89,
        repScores = listOf(88, 90, 89),
        modelVersion = "v1.0",
    )
    FormSenseTheme {
        SessionsScreen(
            state = HistoryUiState(
                isLoading = false,
                sessions = listOf(sampleSession, sampleSession.copy(id = 2L, averageScore = 82)),
            ),
            onSessionSelected = {},
            onBottomDestination = {},
        )
    }
}