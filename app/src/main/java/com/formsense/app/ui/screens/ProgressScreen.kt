package com.formsense.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.formsense.app.navigation.Routes
import com.formsense.app.ui.components.FormSenseBottomBar
import com.formsense.app.ui.components.MetricCard
import com.formsense.app.ui.components.SectionHeader
import com.formsense.app.ui.progress.ProgressUiState

@Composable
fun ProgressScreen(
    state: ProgressUiState,
    onBottomDestination: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            FormSenseBottomBar(
                selectedRoute = Routes.Progress,
                onDestinationSelected = onBottomDestination,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Your locally saved workout trends.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (state.isLoading) {
                item { Text("Calculating progress…") }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        MetricCard(
                            label = "Sessions",
                            value = state.sessionCount.toString(),
                            icon = Icons.Rounded.FitnessCenter,
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            label = "Total Reps",
                            value = state.totalReps.toString(),
                            icon = Icons.Rounded.Repeat,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        MetricCard(
                            label = "Average",
                            value = "${state.averageScore}%",
                            icon = Icons.Rounded.Analytics,
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            label = "Best Score",
                            value = "${state.bestScore}%",
                            icon = Icons.Rounded.EmojiEvents,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                item {
                    SectionHeader(title = "Recent Scores")
                    if (state.sessions.isEmpty()) {
                        Text(
                            text = "Complete a session to begin building your progress history.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            state.sessions.take(8).forEach { session ->
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = session.exercise.displayName,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text("${session.averageScore}%")
                                    }
                                    LinearProgressIndicator(
                                        progress = { session.averageScore / 100f },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
