package com.formsense.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.ui.components.PrimaryButton
import com.formsense.app.ui.theme.Danger
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.theme.SurfaceBorder

data class WorkoutExerciseItem(
    val exercise: ExerciseType,
    var setsCount: Int = 3,
    var repsCount: Int = 10,
)

@Composable
fun CreateWorkoutScreen(
    onBack: () -> Unit,
    onSaveWorkout: (workoutTitle: String, exercises: List<WorkoutExerciseItem>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var workoutTitle by remember { mutableStateOf("") }
    var restBetweenSetsSec by remember { mutableStateOf(60) }
    var restBetweenExercisesSec by remember { mutableStateOf(120) }

    val workoutExercises = remember {
        mutableStateListOf(
            WorkoutExerciseItem(exercise = ExerciseType.Squat, setsCount = 3, repsCount = 12),
        )
    }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Create Custom Workout",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            // Workout Name Input
            item {
                OutlinedTextField(
                    value = workoutTitle,
                    onValueChange = { workoutTitle = it },
                    label = { Text("Workout Name") },
                    placeholder = { Text("e.g. Leg & Core Power Session", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                )
            }

            // Global Workout Rest Timers Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, SurfaceBorder),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Workout Rest Timers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            // Global Rest Between Sets
                            StepperBox(
                                label = "Rest / Set",
                                valueText = "${restBetweenSetsSec}s",
                                onDecrease = { if (restBetweenSetsSec > 15) restBetweenSetsSec -= 15 },
                                onIncrease = { if (restBetweenSetsSec < 300) restBetweenSetsSec += 15 },
                                modifier = Modifier.weight(1f),
                            )

                            // Global Rest Between Exercises
                            val restMin = restBetweenExercisesSec / 60
                            val restSec = restBetweenExercisesSec % 60
                            val restExText = if (restSec == 0) "${restMin}m" else "${restMin}m ${restSec}s"

                            StepperBox(
                                label = "Rest / Exercise",
                                valueText = restExText,
                                onDecrease = { if (restBetweenExercisesSec > 30) restBetweenExercisesSec -= 30 },
                                onIncrease = { if (restBetweenExercisesSec < 600) restBetweenExercisesSec += 30 },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // Exercises Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Exercises Included (${workoutExercises.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    OutlinedButton(onClick = { showAddExerciseDialog = true }) {
                        Icon(Icons.Rounded.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Add Exercise")
                    }
                }
            }

            // Added Exercises List
            if (workoutExercises.isEmpty()) {
                item {
                    Text(
                        text = "No exercises added yet. Tap '+ Add Exercise' to build your workout.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                itemsIndexed(workoutExercises) { index, item ->
                    ExerciseBuilderCard(
                        item = item,
                        onSetsChange = { newSets ->
                            workoutExercises[index] = item.copy(setsCount = newSets)
                        },
                        onRepsChange = { newReps ->
                            workoutExercises[index] = item.copy(repsCount = newReps)
                        },
                        onRemove = {
                            workoutExercises.removeAt(index)
                        },
                    )
                }
            }

            // Save Workout Button
            item {
                Spacer(Modifier.height(8.dp))
                PrimaryButton(
                    text = "Save Workout",
                    onClick = {
                        val finalTitle = workoutTitle.ifBlank { "Custom Workout" }
                        onSaveWorkout(finalTitle, workoutExercises)
                    },
                )
            }
        }
    }

    // Exercise Picker Dialog
    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Select Exercise to Add") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExerciseType.entries.forEach { exerciseType ->
                        OutlinedButton(
                            onClick = {
                                workoutExercises.add(WorkoutExerciseItem(exercise = exerciseType))
                                showAddExerciseDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Rounded.FitnessCenter, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("${exerciseType.displayName} (${exerciseType.focusArea})")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun ExerciseBuilderCard(
    item: WorkoutExerciseItem,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Exercise Title and Remove Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = item.exercise.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = item.exercise.focusArea,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Remove exercise",
                        tint = Danger,
                    )
                }
            }

            // Sets & Reps Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StepperBox(
                    label = "Target Sets",
                    valueText = "${item.setsCount} Sets",
                    onDecrease = { if (item.setsCount > 1) onSetsChange(item.setsCount - 1) },
                    onIncrease = { if (item.setsCount < 10) onSetsChange(item.setsCount + 1) },
                    modifier = Modifier.weight(1f),
                )

                StepperBox(
                    label = "Target Reps",
                    valueText = "${item.repsCount} Reps",
                    onDecrease = { if (item.repsCount > 1) onRepsChange(item.repsCount - 1) },
                    onIncrease = { if (item.repsCount < 50) onRepsChange(item.repsCount + 1) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StepperBox(
    label: String,
    valueText: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = onDecrease) {
                Icon(Icons.Rounded.Remove, contentDescription = "Decrease $label", tint = FormBlue)
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onIncrease) {
                Icon(Icons.Rounded.Add, contentDescription = "Increase $label", tint = FormBlue)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun CreateWorkoutScreenPreview() {
    FormSenseTheme {
        CreateWorkoutScreen(
            onBack = {},
            onSaveWorkout = { _, _ -> },
        )
    }
}