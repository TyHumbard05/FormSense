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
class WebSocketClient @Inject constructor(
    private val client: OkHttpClient,
) {
    private var webSocket: WebSocket? = null

    // Connects to Flask socket route on emulator host
    private val socketUrl = "ws://10.0.2.2:5000/api/camera_socket"

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun connect() {

        Log.d("WebSocketClient", "Connecting to Flask socket at $socketUrl")

        if (_isConnected.value) return

        val request = Request.Builder()
            .url(socketUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocketClient", "Connected to Flask socket at $socketUrl")
                _isConnected.value = true
                webSocket.send("Handshake: Android client connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocketClient", "Received text message: $text")
                _messages.tryEmit(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val message = bytes.utf8()
                Log.d("WebSocketClient", "Received bytes message: $message")
                _messages.tryEmit(message)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocketClient", "Socket closing: $code / $reason")
                _isConnected.value = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocketClient", "Socket closed: $code / $reason")
                _isConnected.value = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocketClient", "Socket error: ${t.message}", t)
                _isConnected.value = false
            }
        })
    }

    fun sendMessage(text: String): Boolean {
        return webSocket?.send(text) ?: false
    }

    fun sendBytes(bytes: ByteArray): Boolean {
        return webSocket?.send(bytes.toByteString(0, bytes.size)) ?: false
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnect requested by app")
        webSocket = null
        _isConnected.value = false
    }
}
