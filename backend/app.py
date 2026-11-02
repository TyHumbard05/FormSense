from flask import Flask
from flask_sock import Sock


def create_app():
    app = Flask(__name__)
    sock = Sock(app)

    @sock.route("/api/camera_socket")
    def websocket(ws):
        print("🟢 WebSocket Client Connected!", flush=True)
        try:
            while True:
                message = ws.receive()
                if message is None:
                    break
                print(f"📩 Received: {message}", flush=True)
                ws.send(f"Echo from Flask: {message}")
        except Exception as e:
            print(f"⚠️ WebSocket Error: {e}", flush=True)
        finally:
            print("🔴 WebSocket Client Disconnected", flush=True)

    return app


if __name__ == "__main__":
    create_app().run(host="0.0.0.0", port=5000, debug=False, threaded=True)
