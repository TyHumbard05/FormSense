package com.formsense.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.RecentSession
import com.formsense.app.ui.components.PrimaryButton
import com.formsense.app.ui.components.RecentSessionCard
import com.formsense.app.ui.theme.Danger
import com.formsense.app.ui.theme.FormSenseTheme

@Composable
fun WorkoutDetailScreen(
    workoutTitle: String,
    workoutDateText: String,
    exercisesInWorkout: List<RecentSession>,
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    onExerciseSelected: (Long) -> Unit,
    onDeleteWorkout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text(
                            text = workoutTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = workoutDateText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                PrimaryButton(
                    text = "New Exercise",
                    leadingIcon = Icons.Rounded.Add,
                    onClick = onAddExercise,
                )
            }

            item {
                Text(
                    text = "Exercises Recorded",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (exercisesInWorkout.isEmpty()) {
                item {
                    Text(
                        text = "No exercises recorded for this workout yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(exercisesInWorkout.size, key = { exercisesInWorkout[it].id }) { index ->
                    val session = exercisesInWorkout[index]
                    RecentSessionCard(
                        session = session,
                        onClick = { onExerciseSelected(session.id) },
                    )
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
                ) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Delete Workout")
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete this workout?") },
            text = { Text("This will delete all exercises recorded within this workout session.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteWorkout()
                    },
                ) {
                    Text("Delete", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun WorkoutDetailScreenPreview() {
    val sampleSession = RecentSession(
        id = 1L,
        exercise = ExerciseType.Squat,
        startedAtMillis = System.currentTimeMillis() - 300_000,
        endedAtMillis = System.currentTimeMillis(),
        targetReps = 12,
        completedReps = 12,
        averageScore = 88,
        repScores = listOf(85, 90, 88, 82, 92, 86, 90, 88, 85, 90, 94, 88),
        modelVersion = "v1.0",
    )
    FormSenseTheme {
        WorkoutDetailScreen(
            workoutTitle = "Leg Day Workout",
            workoutDateText = "Today at 2:30 PM",
            exercisesInWorkout = listOf(sampleSession),
            onBack = {},
            onAddExercise = {},
            onExerciseSelected = {},
            onDeleteWorkout = {},
        )
    }
}