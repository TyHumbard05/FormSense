from flask import Flask, jsonify


EXERCISES = [
    {"id": "squat", "name": "Squat", "focusArea": "Lower Body"},
    {"id": "bicep-curl", "name": "Bicep Curl", "focusArea": "Upper Body"},
    {"id": "shoulder-raise", "name": "Shoulder Raise", "focusArea": "Upper Body"},
]


def create_app():
    app = Flask(__name__)

    @app.get("/api/health")
    def health():
        return jsonify({"status": "ok", "service": "formsense-api"})

    @app.get("/api/v1/exercises")
    def list_exercises():
        return jsonify({"exercises": EXERCISES})

    @app.errorhandler(404)
    def not_found(_error):
        return jsonify({"error": "Not found"}), 404

    return app


if __name__ == "__main__":
    create_app().run()
