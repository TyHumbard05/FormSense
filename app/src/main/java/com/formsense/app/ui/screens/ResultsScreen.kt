package com.formsense.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.RecentSession
import com.formsense.app.ui.components.RecentSessionCard
import com.formsense.app.ui.components.SectionHeader
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.FormBlueSoft
import com.formsense.app.ui.theme.Success
import com.formsense.app.ui.theme.SurfaceBorder
import com.formsense.app.ui.theme.Warning
import com.formsense.app.ui.theme.Danger
import com.formsense.app.ui.results.ResultsUiState

@Composable
fun ResultsScreen(
    state: ResultsUiState,
    onTryAgain: () -> Unit,
    onReturnHome: () -> Unit,
    onSessionSelected: (Long) -> Unit,
    onDeleteSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (state.isLoading) {
        ResultMessage("Loading session results…", modifier)
        return
    }

    val session = state.session
    if (session == null) {
        ResultMessage("This session could not be found.", modifier)
        return
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Text(
                    text = "Session Results",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            item {
                Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(92.dp).background(FormBlueSoft, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = FormBlue,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Great Work!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "You've completed your ${session.exercise.displayName.lowercase()} session.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            item { SummaryGrid(session) }
            item { ScoreTrendCard(session.repScores) }
            item {
                SectionHeader(title = "Recent Sessions")
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.recentSessions
                        .filterNot { it.id == session.id }
                        .take(3)
                        .forEach { recentSession ->
                            RecentSessionCard(
                                session = recentSession,
                                onClick = { onSessionSelected(recentSession.id) },
                            )
                        }
                    if (state.recentSessions.none { it.id != session.id }) {
                        Text(
                            text = "Complete another session to compare your progress.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onTryAgain,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(1.dp, FormBlue),
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Try Again")
                    }
                    Button(
                        onClick = onReturnHome,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Icon(Icons.Rounded.Home, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Return Home")
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = true },
                    enabled = !state.isDeleting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
                ) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (state.isDeleting) "Deleting…" else "Delete this session")
                }
                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete this session?") },
            text = { Text("This removes the saved workout and its repetition scores from this device.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDeleteSession()
                    },
                ) {
                    Text("Delete", color = Danger)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showDeleteConfirmation = false },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun ResultMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SummaryGrid(session: RecentSession) {
    val goodFormReps = session.repScores.count { it >= 80 }
    val needsImprovement = (session.completedReps - goodFormReps).coerceAtLeast(0)
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryStat(
                    session.completedReps.toString(),
                    "Reps Completed",
                    Icons.Rounded.Refresh,
                    FormBlue,
                    Modifier.weight(1f),
                )
                SummaryStat(
                    goodFormReps.toString(),
                    "Good-Form Reps",
                    Icons.Rounded.CheckCircle,
                    Success,
                    Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryStat(
                    needsImprovement.toString(),
                    "Need Improvement",
                    Icons.Rounded.Error,
                    Warning,
                    Modifier.weight(1f),
                )
                SummaryStat(
                    "${session.averageScore}%",
                    "Average Score",
                    Icons.Rounded.Star,
                    Warning,
                    Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SummaryStat(
    value: String,
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(42.dp).background(tint.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(23.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ScoreTrendCard(scores: List<Int>) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "Form Score Trend")
            if (scores.isEmpty()) {
                Text(
                    text = "No completed repetitions were recorded.",
                    modifier = Modifier.padding(vertical = 24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                return@Column
            }
            Canvas(modifier = Modifier.fillMaxWidth().height(160.dp).padding(top = 12.dp)) {
                val left = 8.dp.toPx()
                val right = size.width - 8.dp.toPx()
                val top = 10.dp.toPx()
                val bottom = size.height - 12.dp.toPx()

                repeat(4) { index ->
                    val y = top + ((bottom - top) * index / 3f)
                    drawLine(
                        color = SurfaceBorder,
                        start = Offset(left, y),
                        end = Offset(right, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }

                val denominator = scores.lastIndex.coerceAtLeast(1).toFloat()
                val points = scores.mapIndexed { index, score ->
                    val x = if (scores.size == 1) {
                        (left + right) / 2f
                    } else {
                        left + (right - left) * index / denominator
                    }
                    val y = bottom - (score / 100f) * (bottom - top)
                    Offset(x, y)
                }
                val fillPath = Path().apply {
                    moveTo(points.first().x, bottom)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, bottom)
                    close()
                }
                drawPath(fillPath, color = FormBlue.copy(alpha = 0.10f))
                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    linePath,
                    color = FormBlue,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                )
                points.forEach { point ->
                    drawCircle(Color.White, radius = 6.dp.toPx(), center = point)
                    drawCircle(FormBlue, radius = 4.dp.toPx(), center = point)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                scores.indices.forEach { index ->
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun ResultsScreenPreview() {
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
        ResultsScreen(
            state = ResultsUiState(
                isLoading = false,
                session = sampleSession,
                recentSessions = listOf(sampleSession),
            ),
            onTryAgain = {},
            onReturnHome = {},
            onSessionSelected = {},
            onDeleteSession = {},
        )
    }
}