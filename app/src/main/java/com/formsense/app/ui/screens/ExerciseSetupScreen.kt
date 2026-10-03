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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.ui.components.PrimaryButton
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormBlueSoft
import com.formsense.app.ui.theme.SurfaceBorder

@Composable
fun ExerciseSetupScreen(
    exercise: ExerciseType,
    onBack: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text(
                            text = "Prepare for ${exercise.displayName}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = exercise.focusArea,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                SetupStep(
                    number = "1",
                    title = "Position your phone",
                    message = "Place the phone on a stable surface where your full body stays visible.",
                    icon = { Icon(Icons.Rounded.PhotoCamera, contentDescription = null) },
                )
            }
            item {
                SetupStep(
                    number = "2",
                    title = "Check your space",
                    message = "Use a clear, well-lit area with enough room to complete the movement safely.",
                    icon = { Icon(Icons.Rounded.Visibility, contentDescription = null) },
                )
            }
            item {
                SetupStep(
                    number = "3",
                    title = "Move at a controlled pace",
                    message = "The current app uses simulated feedback while the production posture model is integrated.",
                    icon = { Icon(Icons.Rounded.Check, contentDescription = null) },
                )
            }
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, SurfaceBorder),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HealthAndSafety,
                            contentDescription = null,
                            tint = FormBlue,
                        )
                        Text(
                            text = "Stop if you feel pain or dizziness. FormSense is an exercise-coaching prototype, not medical advice.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
                PrimaryButton(text = "Start ${exercise.displayName} Session", onClick = onStart)
            }
        }
    }
}

@Composable
private fun SetupStep(
    number: String,
    title: String,
    message: String,
    icon: @Composable () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = FormBlueSoft,
                contentColor = FormBlue,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    icon()
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$number. $title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun ExerciseSetupScreenPreview() {
    FormSenseTheme {
        ExerciseSetupScreen(
            exercise = ExerciseType.Squat,
            onBack = {},
            onStart = {},
        )
    }
}