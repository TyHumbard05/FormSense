package com.formsense.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.navigation.Routes
import com.formsense.app.ui.components.FormSenseBottomBar
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormBlueSoft
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.theme.SurfaceBorder

@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit,
    onBottomDestination: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }

    // Exercise Max Weights
    var squatMaxWeight by remember { mutableStateOf("") }
    var bicepMaxWeight by remember { mutableStateOf("") }
    var shoulderMaxWeight by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            FormSenseBottomBar(
                selectedRoute = Routes.Profile,
                onDestinationSelected = onBottomDestination,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header with Settings Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My Profile",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Tap any box to update your details & PRs.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Open App Settings",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            // Editable User Name & Avatar Card
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(FormBlueSoft, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = FormBlue,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. John Doe", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                        )
                    }
                }
            }

            // Editable Physical Details Grid Cards
            item {
                Text(
                    text = "Physical Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProfileStatCard(
                        label = "Age",
                        value = age,
                        onValueChange = { age = it },
                        placeholder = "e.g. 24",
                        icon = Icons.Rounded.Person,
                        modifier = Modifier.weight(1f),
                    )
                    ProfileStatCard(
                        label = "Weight",
                        value = weight,
                        onValueChange = { weight = it },
                        placeholder = "e.g. 165 lbs",
                        icon = Icons.Rounded.MonitorWeight,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProfileStatCard(
                        label = "Height",
                        value = height,
                        onValueChange = { height = it },
                        placeholder = "e.g. 5' 10\"",
                        icon = Icons.Rounded.Height,
                        modifier = Modifier.weight(1f),
                    )
                    ProfileStatCard(
                        label = "Primary Goal",
                        value = goal,
                        onValueChange = { goal = it },
                        placeholder = "e.g. Squats",
                        icon = Icons.Rounded.FitnessCenter,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Exercise Max Weight / Personal Bests Section
            item {
                Text(
                    text = "Exercise Max Weights & PRs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExerciseRecordRow(
                        exerciseName = "Squat",
                        maxWeight = squatMaxWeight,
                        onMaxWeightChange = { squatMaxWeight = it },
                        placeholder = "e.g. 185 lbs",
                        bestScore = "94%",
                    )
                    ExerciseRecordRow(
                        exerciseName = "Bicep Curl",
                        maxWeight = bicepMaxWeight,
                        onMaxWeightChange = { bicepMaxWeight = it },
                        placeholder = "e.g. 45 lbs",
                        bestScore = "88%",
                    )
                    ExerciseRecordRow(
                        exerciseName = "Shoulder Raise",
                        maxWeight = shoulderMaxWeight,
                        onMaxWeightChange = { shoulderMaxWeight = it },
                        placeholder = "e.g. 30 lbs",
                        bestScore = "90%",
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(38.dp).background(FormBlueSoft, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = FormBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
            )
        }
    }
}

@Composable
private fun ExerciseRecordRow(
    exerciseName: String,
    maxWeight: String,
    onMaxWeightChange: (String) -> Unit,
    placeholder: String,
    bestScore: String,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(42.dp).background(FormBlueSoft, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = FormBlue,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Best Form: $bestScore",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            OutlinedTextField(
                value = maxWeight,
                onValueChange = onMaxWeightChange,
                label = { Text("Max Weight") },
                placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                singleLine = true,
                modifier = Modifier.width(130.dp),
                shape = MaterialTheme.shapes.small,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun ProfileScreenPreview() {
    FormSenseTheme {
        ProfileScreen(
            onOpenSettings = {},
            onBottomDestination = {},
        )
    }
}