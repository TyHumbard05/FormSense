package com.formsense.app.ui.screens

import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.SquareFoot
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.FeedbackSeverity
import com.formsense.app.data.model.FormFeedback
import com.formsense.app.ui.components.FeedbackCard
import com.formsense.app.ui.components.MetricCard
import com.formsense.app.ui.components.SectionHeader
import com.formsense.app.ui.camera.CameraPreviewSurface
import com.formsense.app.ui.theme.FormSenseTheme
import com.formsense.app.ui.theme.Danger
import com.formsense.app.ui.theme.FormBlue
import com.formsense.app.ui.theme.Success
import com.formsense.app.ui.session.SessionUiState

@Composable
fun SessionScreen(
    state: SessionUiState,
    onBack: () -> Unit,
    onEndSession: () -> Unit,
    onRetryAnalyzer: () -> Unit,
    onCameraFrame: (ImageProxy) -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val hapticFeedback = LocalHapticFeedback.current

    DisposableEffect(view, state.keepScreenAwake) {
        val previousValue = view.keepScreenOn
        view.keepScreenOn = state.keepScreenAwake
        onDispose { view.keepScreenOn = previousValue }
    }

    LaunchedEffect(state.repCount, state.hapticFeedbackEnabled) {
        if (state.hapticFeedbackEnabled && state.repCount > 0) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
        ) {
            item {
                SessionTopBar(
                    title = "${state.exercise.displayName} Session",
                    onBack = onBack,
                )
            }
            item {
                CameraPreviewPlaceholder(
                    elapsedSeconds = state.elapsedSeconds,
                    usesSimulatedAnalysis = state.usesSimulatedAnalysis,
                    onCameraFrame = onCameraFrame,
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        MetricCard(
                            label = "Rep Count",
                            value = state.repCount.toString(),
                            supportingText = "/ ${state.targetReps} target",
                            icon = Icons.Rounded.Repeat,
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            label = "Form Score",
                            value = state.formScore?.let { "$it%" } ?: "—",
                            supportingText = if (state.formScore == null) "Waiting for first rep" else "Live estimate",
                            icon = Icons.Rounded.Analytics,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        MetricCard(
                            label = "Knee Angle",
                            value = state.kneeAngleDegrees?.let { "$it°" } ?: "—",
                            supportingText = "Live measurement",
                            icon = Icons.Rounded.SquareFoot,
                            valueColor = Success,
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            label = "Hip Angle",
                            value = state.hipAngleDegrees?.let { "$it°" } ?: "—",
                            supportingText = "Live measurement",
                            icon = Icons.Rounded.SquareFoot,
                            valueColor = Success,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    SectionHeader(title = "Live Feedback")
                    state.feedback.forEach { feedback ->
                        FeedbackCard(
                            title = feedback.title,
                            message = feedback.message,
                            positive = feedback.severity == FeedbackSeverity.Positive,
                        )
                    }
                    state.errorMessage?.let { error ->
                        Column {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            TextButton(onClick = onRetryAnalyzer) {
                                Text("Retry analysis")
                            }
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Button(
                        onClick = onEndSession,
                        enabled = !state.isSaving,
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Danger),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Icon(Icons.Rounded.Stop, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            if (state.isSaving) "Saving…" else "End Session",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        IconButton(onClick = { }) {
            Icon(Icons.Rounded.MoreHoriz, contentDescription = "More options")
        }
    }
}

@Composable
private fun CameraPreviewPlaceholder(
    elapsedSeconds: Long,
    usesSimulatedAnalysis: Boolean,
    onCameraFrame: (ImageProxy) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.08f)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF4F5968), Color(0xFF202A38)),
                ),
            ),
    ) {
        CameraPreviewSurface(
            onFrameAvailable = onCameraFrame,
            modifier = Modifier.fillMaxSize(),
        )

        if (usesSimulatedAnalysis) Canvas(modifier = Modifier.fillMaxSize()) {
            val shoulder = Offset(size.width * 0.52f, size.height * 0.28f)
            val elbow = Offset(size.width * 0.64f, size.height * 0.39f)
            val wrist = Offset(size.width * 0.72f, size.height * 0.31f)
            val hip = Offset(size.width * 0.48f, size.height * 0.55f)
            val knee = Offset(size.width * 0.66f, size.height * 0.67f)
            val ankle = Offset(size.width * 0.58f, size.height * 0.88f)
            val points = listOf(shoulder, elbow, wrist, hip, knee, ankle)
            val lines = listOf(
                shoulder to elbow,
                elbow to wrist,
                shoulder to hip,
                hip to knee,
                knee to ankle,
            )
            lines.forEachIndexed { index, (start, end) ->
                drawLine(
                    color = if (index >= 3) Success else FormBlue,
                    start = start,
                    end = end,
                    strokeWidth = 7.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = if (index >= 3) Success else FormBlue,
                    radius = 9.dp.toPx(),
                    center = point,
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = point,
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xB31A2230))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(9.dp).background(FormBlue, CircleShape))
            Spacer(Modifier.width(7.dp))
            Text("Tracking…", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        Text(
            text = "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xB31A2230))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
        )

        Text(
            text = if (usesSimulatedAnalysis) {
                "Simulated analysis • camera integration next"
            } else {
                "Live camera analysis"
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(14.dp),
            color = Color.White.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun SessionScreenPreview() {
    FormSenseTheme {
        SessionScreen(
            state = SessionUiState(
                exercise = ExerciseType.Squat,
                targetReps = 12,
                repCount = 5,
                formScore = 88,
                kneeAngleDegrees = 95,
                hipAngleDegrees = 100,
                elapsedSeconds = 42,
                feedback = listOf(
                    FormFeedback(
                        code = "good_depth",
                        title = "Good depth",
                        message = "Maintain this hip alignment",
                        severity = FeedbackSeverity.Positive,
                    ),
                ),
            ),
            onBack = {},
            onEndSession = {},
            onRetryAnalyzer = {},
            onCameraFrame = {},
        )
    }
}