package com.formsense.app.data.remote

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClient @Inject constructor(private val client: OkHttpClient) {
    @Volatile private var webSocket: WebSocket? = null
    // Android emulator -> host machine. For a physical device, use the host's LAN IP.
    private val socketUrl = "ws://localhost:5000/api/camera_socket"
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    @Synchronized fun connect() {
        if (_isConnected.value || webSocket != null) return
        Log.d(TAG, "Connecting to Java backend at $socketUrl")
        webSocket = client.newWebSocket(Request.Builder().url(socketUrl).build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "Connected to Java backend (HTTP ${response.code})")
                _isConnected.value = true
                _messages.tryEmit("{\"type\":\"socket_open\"}")
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Backend message: $text"); _messages.tryEmit(text)
            }
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Backend binary message: ${bytes.size} bytes"); _messages.tryEmit(bytes.utf8())
            }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                _isConnected.value=false; Log.d(TAG,"Socket closing: $code / $reason")
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _isConnected.value=false; this@WebSocketClient.webSocket=null
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG,"Socket connection failed: ${t.message}",t)
                _isConnected.value=false; this@WebSocketClient.webSocket=null
                _messages.tryEmit("{\"type\":\"socket_error\",\"message\":\"${t.message}\"}")
            }
        })
    }

    fun sendMessage(text: String): Boolean = webSocket?.send(text) ?: false
    /** Sends one JPEG-encoded CameraX frame as a WebSocket binary message. */
    fun sendCameraFrame(jpegBytes: ByteArray): Boolean {
        if (!_isConnected.value || jpegBytes.isEmpty()) return false
        return webSocket?.send(jpegBytes.toByteString()) ?: false
    }
    fun sendBytes(bytes: ByteArray): Boolean = webSocket?.send(bytes.toByteString()) ?: false
    @Synchronized fun disconnect() {
        webSocket?.close(1000,"Session ended"); webSocket=null; _isConnected.value=false
    }
    private companion object { const val TAG="WebSocketClient" }
}
