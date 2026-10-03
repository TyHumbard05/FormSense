package com.formsense.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.navigation.Routes
import com.formsense.app.ui.components.FormSenseBottomBar
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.profile.ProfileUiState
import com.formsense.app.ui.theme.Danger
import com.formsense.app.ui.theme.SurfaceBorder

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onDeleteAllSessions: () -> Unit,
    onDefaultTargetRepsChanged: (Int) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit,
    onKeepScreenAwakeChanged: (Boolean) -> Unit,
    onBottomDestination: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
            item {
                Text(
                    text = "Profile & Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "FormSense currently stores workout history only on this device.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Session defaults",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Target repetitions", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Used when a new session starts",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            IconButton(
                                onClick = { onDefaultTargetRepsChanged(state.defaultTargetReps - 1) },
                                enabled = state.defaultTargetReps > 1,
                            ) {
                                Icon(Icons.Rounded.Remove, contentDescription = "Decrease target repetitions")
                            }
                            Text(
                                state.defaultTargetReps.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            IconButton(
                                onClick = { onDefaultTargetRepsChanged(state.defaultTargetReps + 1) },
                                enabled = state.defaultTargetReps < 50,
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Increase target repetitions")
                            }
                        }
                        SettingSwitchRow(
                            title = "Haptic rep feedback",
                            description = "Vibrate briefly when a repetition completes",
                            checked = state.hapticFeedbackEnabled,
                            onCheckedChange = onHapticFeedbackChanged,
                        )
                        SettingSwitchRow(
                            title = "Keep screen awake",
                            description = "Prevent the display from sleeping during a session",
                            checked = state.keepScreenAwake,
                            onCheckedChange = onKeepScreenAwakeChanged,
                        )
                    }
                }
            }
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Rounded.Lock, contentDescription = null)
                        Text(
                            text = "Privacy by default",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "The current prototype saves session scores and repetition counts. It does not store camera frames or videos.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                Text(
                    text = "FormSense is an academic exercise-coaching prototype. It does not provide medical diagnosis or replace professional care.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isDeleting,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
                ) {
                    Icon(Icons.Rounded.DeleteForever, contentDescription = null)
                    Text(if (state.isDeleting) "Deleting…" else "Delete all session data")
                }
            }
            state.message?.let { message ->
                item {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete all sessions?") },
            text = { Text("This permanently removes the locally saved workout history from this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteAllSessions()
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

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun ProfileScreenPreview() {
    FormSenseTheme {
        ProfileScreen(
            state = ProfileUiState(
                defaultTargetReps = 10,
                hapticFeedbackEnabled = true,
                keepScreenAwake = true,
            ),
            onDeleteAllSessions = {},
            onDefaultTargetRepsChanged = {},
            onHapticFeedbackChanged = {},
            onKeepScreenAwakeChanged = {},
            onBottomDestination = {},
        )
    }
}