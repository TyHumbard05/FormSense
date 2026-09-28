package com.formsense.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.navigation.Routes
import com.formsense.app.ui.components.BrandMark
import com.formsense.app.ui.components.ExerciseCard
import com.formsense.app.ui.components.FormSenseBottomBar
import com.formsense.app.ui.components.PrimaryButton
import com.formsense.app.ui.components.RecentSessionCard
import com.formsense.app.ui.components.SectionHeader
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormBlueDark
import com.formsense.app.ui.theme.FormBlueSoft
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.home.HomeUiState

@Composable
fun HomeScreen(
    state: HomeUiState,
    onStartSession: () -> Unit,
    onExerciseSelected: (ExerciseType) -> Unit,
    onBottomDestination: (String) -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            FormSenseBottomBar(
                selectedRoute = Routes.Home,
                onDestinationSelected = onBottomDestination,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 16.dp,
                end = 20.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrandMark(modifier = Modifier.weight(1f))
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
            item { HeroPanel() }
            item {
                PrimaryButton(
                    text = "Start a Session",
                    onClick = onStartSession,
                )
            }
            item {
                SectionHeader(
                    title = "Choose an Exercise",
                    actionLabel = "See All",
                    onAction = onStartSession,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.exercises.forEachIndexed { index, exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { onExerciseSelected(exercise) },
                            emphasized = index == 0,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            item {
                SectionHeader(
                    title = "Recent Activity",
                    actionLabel = "See All",
                    onAction = { onBottomDestination(Routes.Sessions) },
                )
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    when {
                        state.isLoading -> Text(
                            text = "Loading recent activity…",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        state.errorMessage != null -> Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                        state.recentSessions.isEmpty() -> EmptyRecentActivity()
                        else -> state.recentSessions.forEach { session ->
                            RecentSessionCard(session = session)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRecentActivity() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.History,
            contentDescription = null,
            tint = FormBlue,
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "No sessions yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Complete a session and it will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HeroPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color.White, FormBlueSoft, Color(0xFFD8E9FF)),
                ),
            )
            .padding(start = 20.dp, top = 24.dp, end = 16.dp, bottom = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI-powered feedback for better form.",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Train smarter. Move safer.\nBuild a healthier, stronger you.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .background(
                        Brush.radialGradient(listOf(FormBlue, FormBlueDark)),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(58.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun HomeScreenPreview() {
    FormSenseTheme {
        HomeScreen(
            state = HomeUiState(isLoading = false),
            onStartSession = {},
            onExerciseSelected = {},
            onBottomDestination = {},
            onSettings = {},
        )
    }
}
