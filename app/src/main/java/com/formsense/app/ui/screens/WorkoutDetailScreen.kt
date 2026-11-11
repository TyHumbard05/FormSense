package com.formsense.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.ui.components.PrimaryButton
import com.formsense.app.ui.theme.Danger
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormBlueSoft
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.theme.SurfaceBorder

data class CustomWorkoutPlan(
    val id: Long,
    val title: String,
    val dateCreatedText: String,
    val exercises: List<WorkoutExerciseItem>,
) {
    val exerciseCount: Int get() = exercises.size
    val totalSets: Int get() = exercises.sumOf { it.setsCount }
    val totalReps: Int get() = exercises.sumOf { it.setsCount * it.repsCount }
}

@Composable
fun WorkoutDetailScreen(
    onBack: () -> Unit,
    onCreateWorkout: () -> Unit,
    onStartWorkoutSession: (ExerciseType) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // Storage list of created custom workouts
    val customWorkouts = remember {
        mutableStateListOf(
            CustomWorkoutPlan(
                id = 1L,
                title = "Leg & Core Power Routine",
                dateCreatedText = "Created Yesterday",
                exercises = listOf(
                    WorkoutExerciseItem(exercise = ExerciseType.Squat, setsCount = 3, repsCount = 12),
                    WorkoutExerciseItem(exercise = ExerciseType.ShoulderRaise, setsCount = 3, repsCount = 10),
                    WorkoutExerciseItem(exercise = ExerciseType.BicepCurl, setsCount = 3, repsCount = 10),
                ),
            ),
            CustomWorkoutPlan(
                id = 2L,
                title = "Upper Body Pump & Posture",
                dateCreatedText = "Created 3 days ago",
                exercises = listOf(
                    WorkoutExerciseItem(exercise = ExerciseType.BicepCurl, setsCount = 3, repsCount = 12),
                    WorkoutExerciseItem(exercise = ExerciseType.ShoulderRaise, setsCount = 3, repsCount = 10),
                ),
            ),
        )
    }

    var selectedWorkoutForDetail by remember { mutableStateOf<CustomWorkoutPlan?>(null) }
    var workoutToEdit by remember { mutableStateOf<CustomWorkoutPlan?>(null) }
    var workoutToDelete by remember { mutableStateOf<CustomWorkoutPlan?>(null) }

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Saved Custom Workouts",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Your custom workout plans & routines storage.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Create Workout Action Button
            item {
                PrimaryButton(
                    text = "Create New Custom Workout",
                    leadingIcon = Icons.Rounded.Add,
                    onClick = onCreateWorkout,
                )
            }

            item {
                Text(
                    text = "Stored Workouts (${customWorkouts.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            // EMPTY STATE (If no workouts created yet)
            if (customWorkouts.isEmpty()) {
                item {
                    EmptyWorkoutStorage(onCreateWorkout = onCreateWorkout)
                }
            } else {
                items(customWorkouts, key = { it.id }) { workout ->
                    SavedWorkoutCard(
                        workout = workout,
                        onClick = { selectedWorkoutForDetail = workout },
                        onEdit = { workoutToEdit = workout },
                        onDelete = { workoutToDelete = workout },
                    )
                }
            }
        }
    }

    // Workout Details & Exercises Dialog
    selectedWorkoutForDetail?.let { workout ->
        AlertDialog(
            onDismissRequest = { selectedWorkoutForDetail = null },
            title = {
                Column {
                    Text(
                        text = workout.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${workout.exerciseCount} Exercises • ${workout.totalSets} Total Sets",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    workout.exercises.forEachIndexed { index, item ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(FormBlueSoft, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FormBlue,
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.exercise.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                    Text(
                                        text = "${item.exercise.focusArea} • ${item.setsCount} sets × ${item.repsCount} reps",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                PrimaryButton(
                    text = "Start Workout",
                    leadingIcon = Icons.Rounded.PlayArrow,
                    onClick = {
                        val firstExercise = workout.exercises.firstOrNull()?.exercise ?: ExerciseType.Squat
                        selectedWorkoutForDetail = null
                        onStartWorkoutSession(firstExercise)
                    },
                )
            },
            dismissButton = {
                TextButton(onClick = { selectedWorkoutForDetail = null }) {
                    Text("Close")
                }
            },
        )
    }

    // Edit Workout Dialog
    workoutToEdit?.let { workout ->
        EditWorkoutDialog(
            workout = workout,
            onDismiss = { workoutToEdit = null },
            onSave = { updatedWorkout ->
                val index = customWorkouts.indexOfFirst { it.id == updatedWorkout.id }
                if (index != -1) {
                    customWorkouts[index] = updatedWorkout
                }
                workoutToEdit = null
            },
        )
    }

    // Delete Confirmation Dialog
    workoutToDelete?.let { workout ->
        AlertDialog(
            onDismissRequest = { workoutToDelete = null },
            title = { Text("Delete '${workout.title}'?") },
            text = { Text("This will permanently remove this custom workout plan from your stored workouts.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        customWorkouts.remove(workout)
                        workoutToDelete = null
                    },
                ) {
                    Text("Delete", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun EditWorkoutDialog(
    workout: CustomWorkoutPlan,
    onDismiss: () -> Unit,
    onSave: (CustomWorkoutPlan) -> Unit,
) {
    var editedTitle by remember { mutableStateOf(workout.title) }
    val editedExercises = remember {
        mutableStateListOf<WorkoutExerciseItem>().apply {
            addAll(workout.exercises.map { it.copy() })
        }
    }
    var showAddExercisePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Workout Plan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Workout Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                    )
                }

                item {
                    Text(
                        text = "Exercises",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                items(editedExercises) { item ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = item.exercise.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = {
                                        if (editedExercises.size > 1) {
                                            editedExercises.remove(item)
                                        }
                                    },
                                ) {
                                    Icon(
                                        Icons.Rounded.DeleteOutline,
                                        contentDescription = "Remove exercise",
                                        tint = Danger,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // Sets Stepper
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (item.setsCount > 1) {
                                                val idx = editedExercises.indexOf(item)
                                                if (idx != -1) editedExercises[idx] = item.copy(setsCount = item.setsCount - 1)
                                            }
                                        },
                                    ) {
                                        Icon(Icons.Rounded.Remove, contentDescription = null, tint = FormBlue)
                                    }
                                    Text(
                                        text = "${item.setsCount} sets",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    IconButton(
                                        onClick = {
                                            if (item.setsCount < 20) {
                                                val idx = editedExercises.indexOf(item)
                                                if (idx != -1) editedExercises[idx] = item.copy(setsCount = item.setsCount + 1)
                                            }
                                        },
                                    ) {
                                        Icon(Icons.Rounded.Add, contentDescription = null, tint = FormBlue)
                                    }
                                }

                                // Reps Stepper
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (item.repsCount > 1) {
                                                val idx = editedExercises.indexOf(item)
                                                if (idx != -1) editedExercises[idx] = item.copy(repsCount = item.repsCount - 1)
                                            }
                                        },
                                    ) {
                                        Icon(Icons.Rounded.Remove, contentDescription = null, tint = FormBlue)
                                    }
                                    Text(
                                        text = "${item.repsCount} reps",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    IconButton(
                                        onClick = {
                                            if (item.repsCount < 50) {
                                                val idx = editedExercises.indexOf(item)
                                                if (idx != -1) editedExercises[idx] = item.copy(repsCount = item.repsCount + 1)
                                            }
                                        },
                                    ) {
                                        Icon(Icons.Rounded.Add, contentDescription = null, tint = FormBlue)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { showAddExercisePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add Exercise")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val title = editedTitle.ifBlank { "Custom Workout" }
                    onSave(
                        workout.copy(
                            title = title,
                            exercises = editedExercises.toList(),
                        ),
                    )
                },
            ) {
                Text("Save Changes", color = FormBlue, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )

    if (showAddExercisePicker) {
        AlertDialog(
            onDismissRequest = { showAddExercisePicker = false },
            title = { Text("Add Exercise") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExerciseType.entries.forEach { exerciseType ->
                        OutlinedButton(
                            onClick = {
                                editedExercises.add(WorkoutExerciseItem(exercise = exerciseType))
                                showAddExercisePicker = false
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
                TextButton(onClick = { showAddExercisePicker = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun EmptyWorkoutStorage(onCreateWorkout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(FormBlueSoft, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.FolderSpecial,
                contentDescription = null,
                tint = FormBlue,
                modifier = Modifier.size(38.dp),
            )
        }
        Text(
            text = "No Custom Workouts Saved",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Build and store custom workout routines with your target exercises, sets, and reps.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedButton(onClick = onCreateWorkout) {
            Icon(Icons.Rounded.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Create Your First Workout")
        }
    }
}

@Composable
private fun SavedWorkoutCard(
    workout: CustomWorkoutPlan,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(FormBlueSoft, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = FormBlue,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${workout.exerciseCount} exercises • ${workout.totalSets} sets • ${workout.totalReps} total reps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = workout.dateCreatedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit custom workout",
                    tint = FormBlue,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete custom workout",
                    tint = Danger,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun WorkoutDetailScreenPreview() {
    FormSenseTheme {
        WorkoutDetailScreen(
            onBack = {},
            onCreateWorkout = {},
        )
    }
}