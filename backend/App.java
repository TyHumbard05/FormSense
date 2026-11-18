//JAVA 17+
//DEPS org.java-websocket:Java-WebSocket:1.5.7
// Run from repository root: jbang backend/App.java

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import javax.imageio.ImageIO;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public final class App extends WebSocketServer {
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("FORMSENSE_PORT", "5000"));
    private final AtomicLong frameCounter = new AtomicLong();

    public App() { super(new InetSocketAddress("0.0.0.0", PORT)); setReuseAddr(true); }

    public static void main(String[] args) throws Exception {
        App server = new App();
        server.start();
        System.out.println("FormSense Java WebSocket backend listening on port " + PORT);
        System.out.println("Endpoint: ws://0.0.0.0:" + PORT + "/api/camera_socket");
        System.out.println("Android emulator: ws://10.0.2.2:" + PORT + "/api/camera_socket");
        System.out.println("Press Ctrl+C to stop.");
    }

    @Override public void onOpen(WebSocket connection, ClientHandshake handshake) {
        if (!"/api/camera_socket".equals(handshake.getResourceDescriptor())) {
            connection.close(1008, "Unknown WebSocket endpoint"); return;
        }
        System.out.println("[CONNECT] " + connection.getRemoteSocketAddress());
        sendJson(connection, "{\"type\":\"connected\",\"message\":\"FormSense Java backend connected\"}");
    }

    @Override public void onMessage(WebSocket connection, String message) {
        System.out.println("[ANDROID -> BACKEND] " + message);
        sendJson(connection, "{\"type\":\"message_received\",\"message\":\"" + jsonEscape(message) + "\"}");
    }

    @Override public void onMessage(WebSocket connection, ByteBuffer bytes) {
        ByteBuffer data = bytes.slice();
        byte[] jpeg = new byte[data.remaining()]; data.get(jpeg);
        long frameId = frameCounter.incrementAndGet();
        int width = -1, height = -1; boolean validJpeg = false;
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(jpeg));
            if (image != null) { width = image.getWidth(); height = image.getHeight(); validJpeg = true; }
        } catch (Exception error) {
            System.err.println("[FRAME " + frameId + "] JPEG decode failed: " + error.getMessage());
        }
        System.out.printf("[CAMERA FRAME %d] %s, %d bytes, %dx%d%n", frameId,
            validJpeg ? "valid JPEG" : "unrecognized image", jpeg.length, width, height);
        sendJson(connection, String.format(
            "{\"type\":\"camera_frame_received\",\"frameId\":%d,\"validJpeg\":%s,\"byteCount\":%d,\"width\":%d,\"height\":%d}",
            frameId, validJpeg, jpeg.length, width, height));
    }

    @Override public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        System.out.printf("[DISCONNECT] code=%d reason=%s remote=%s%n", code, reason, remote);
    }
    @Override public void onError(WebSocket connection, Exception error) {
        System.err.println("[WEBSOCKET ERROR] " + error.getMessage());
        if (error != null) error.printStackTrace(System.err);
    }
    @Override public void onStart() {
        System.out.println("[READY] ws://0.0.0.0:" + PORT + "/api/camera_socket");
    }
    private static void sendJson(WebSocket socket, String json) {
        if (socket != null && socket.isOpen()) socket.send(json);
    }
    private static String jsonEscape(String value) {
        StringBuilder out = new StringBuilder();
        for (int i=0; i<value.length(); i++) {
            char c=value.charAt(i);
            switch(c) {
                case '"' -> out.append("\\\""); case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b"); case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n"); case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> { if(c<0x20) out.append(String.format("\\u%04x", (int)c)); else out.append(c); }
            }
        }
        return out.toString();
    }
}
