//JAVA 17+
//DEPS org.eclipse.jetty.websocket:jetty-websocket-jetty-server:12.1.15
//DEPS org.slf4j:slf4j-simple:2.0.17

// From the repository root: jbang backend/App.java

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

public class App {
    private static final String CAMERA_SOCKET_PATH = "/api/camera_socket";

    public static void main(String[] args) throws Exception {
        Server server = createApp();
        server.setStopAtShutdown(true);
        server.start();
        server.join();
    }

    public static Server createApp() {
        Server server = new Server(new InetSocketAddress("0.0.0.0", 5000));
        WebSocketUpgradeHandler webSockets = WebSocketUpgradeHandler.from(server, container -> {
            // Match the Flask socket's lack of an idle timeout or message size limit.
            container.setIdleTimeout(Duration.ZERO);
            container.setMaxFrameSize(0);
            container.setMaxTextMessageSize(0);
            container.setMaxBinaryMessageSize(0);
            container.addMapping(CAMERA_SOCKET_PATH, (request, response, callback) -> new CameraSocket());
        });
        webSockets.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(Request request, Response response, org.eclipse.jetty.util.Callback callback) {
                if (!CAMERA_SOCKET_PATH.equals(request.getHttpURI().getCanonicalPath())) {
                    return false;
                }
                switch (request.getMethod()) {
                    case "GET", "HEAD", "OPTIONS" ->
                        Response.writeError(request, response, callback, HttpStatus.BAD_REQUEST_400);
                    default -> {
                        response.getHeaders().put(HttpHeader.ALLOW, "GET, HEAD, OPTIONS");
                        Response.writeError(request, response, callback, HttpStatus.METHOD_NOT_ALLOWED_405);
                    }
                }
                return true;
            }
        });
        server.setHandler(webSockets);
        return server;
    }

    public static final class CameraSocket implements Session.Listener {
        private Session session;

        @Override
        public void onWebSocketOpen(Session session) {
            this.session = session;
            session.demand();
        }

        @Override
        public void onWebSocketText(String message) {
            if (message.isEmpty()) {
                session.demand();
                return;
            }
            session.sendText(message, Callback.from(session::demand, failure -> session.disconnect()));
        }

        @Override
        public void onWebSocketBinary(ByteBuffer message, Callback received) {
            if (!message.hasRemaining()) {
                received.succeed();
                session.demand();
                return;
            }

            // Keep Jetty's receive buffer valid until the asynchronous echo completes.
            session.sendBinary(message, Callback.from(() -> {
                received.succeed();
                session.demand();
            }, received::fail));
        }
    }
}
