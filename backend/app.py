from flask import Flask
from flask_sock import Sock


def create_app():
    app = Flask(__name__)
    sock = Sock(app)

    @sock.route("/api/camera_socket")
    def websocket(ws):
        while True:
            message = ws.receive()
            if message is None:
                break
            ws.send(message)

    return app


if __name__ == "__main__":
    create_app().run(host="0.0.0.0", port=5000)
