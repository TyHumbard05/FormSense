package com.formsense.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.ui.components.ExerciseCard
import com.formsense.app.ui.theme.FormSenseTheme

@Composable
fun ExerciseSelectionScreen(
    onBack: () -> Unit,
    onExerciseSelected: (ExerciseType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Choose an Exercise",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Start with squats for the full prototype experience. Other exercises use the same UI shell and will gain motion logic later.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(ExerciseType.entries.size) { index ->
                val exercise = ExerciseType.entries[index]
                Column {
                    ExerciseCard(
                        exercise = exercise,
                        onClick = { onExerciseSelected(exercise) },
                        emphasized = exercise == ExerciseType.Squat,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun ExerciseSelectionScreenPreview() {
    FormSenseTheme {
        ExerciseSelectionScreen(
            onBack = {},
            onExerciseSelected = {},
        )
    }
}

