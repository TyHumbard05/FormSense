package com.formsense.app.domain.analyzer

import androidx.camera.core.ImageProxy
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.FeedbackSeverity
import com.formsense.app.data.model.FormFeedback
import com.formsense.app.data.remote.CameraFrameEncoder
import com.formsense.app.data.remote.WebSocketClient
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.json.JSONObject

@Singleton
class WebSocketPostureAnalyzer @Inject constructor(
    private val webSocketClient: WebSocketClient,
) : PostureAnalyzer {

    override fun submitFrame(frame: ImageProxy) {
        runCatching {
            val jpeg = CameraFrameEncoder.toJpeg(frame)
            if (jpeg != null) {
                webSocketClient.sendCameraFrame(jpeg)
            }
        }
        frame.close()
    }

    override fun results(exercise: ExerciseType): Flow<PostureResult> {
        return webSocketClient.messages
            .map { rawMessage ->
                try {
                    val json = JSONObject(rawMessage)
                    if (json.optString("type") == "posture_result") {
                        val phaseStr = json.optString("phase", "Ready")
                        val phase = runCatching { MovementPhase.valueOf(phaseStr) }.getOrDefault(MovementPhase.Ready)
                        val repCount = json.optInt("repCount", 0)
                        val formScore = json.optInt("formScore", 0)
                        val kneeAngle = if (json.has("kneeAngleDegrees") && !json.isNull("kneeAngleDegrees")) json.optInt("kneeAngleDegrees") else null
                        val hipAngle = if (json.has("hipAngleDegrees") && !json.isNull("hipAngleDegrees")) json.optInt("hipAngleDegrees") else null
                        val modelVersion = json.optString("modelVersion", "java-backend-1.0")

                        val feedbackArray = json.optJSONArray("feedback")
                        val feedbackList = mutableListOf<FormFeedback>()
                        if (feedbackArray != null) {
                            for (i in 0 until feedbackArray.length()) {
                                val fbObj = feedbackArray.getJSONObject(i)
                                val severityStr = fbObj.optString("severity", "Coaching")
                                val severity = runCatching { FeedbackSeverity.valueOf(severityStr) }.getOrDefault(FeedbackSeverity.Coaching)
                                feedbackList.add(
                                    FormFeedback(
                                        code = fbObj.optString("code", "info"),
                                        title = fbObj.optString("title", ""),
                                        message = fbObj.optString("message", ""),
                                        severity = severity,
                                    )
                                )
                            }
                        }

                        val completedRepScore = if (json.has("completedRepScore") && !json.isNull("completedRepScore")) {
                            json.optInt("completedRepScore")
                        } else null

                        PostureResult(
                            timestampMillis = System.currentTimeMillis(),
                            exercise = exercise,
                            phase = phase,
                            repCount = repCount,
                            formScore = formScore,
                            kneeAngleDegrees = kneeAngle,
                            hipAngleDegrees = hipAngle,
                            feedback = feedbackList,
                            completedRepScore = completedRepScore,
                            modelVersion = modelVersion,
                        )
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            .filterNotNull()
    }
}
