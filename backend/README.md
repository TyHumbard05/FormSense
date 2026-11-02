# FormSense Flask API

A small Flask API starter for the FormSense Android app.

## Set up on Windows

From the repository root, create and activate a virtual environment, then install
the backend dependencies:

```powershell
py -m venv backend\.venv
backend\.venv\Scripts\Activate.ps1
pip install -r backend\requirements.txt
```

Start the development server from the `backend` directory:

```powershell
cd backend
flask --app app run --host 0.0.0.0 --port 5000
```

The API is then available at `http://127.0.0.1:5000`. The Flask development
server is for local development only; use a production WSGI server when
deploying.

## Endpoints

- `GET /api/health` — returns the service health.
- `GET /api/v1/exercises` — returns the exercise catalog used by the app.

Example:

```json
{
  "exercises": [
    { "id": "squat", "name": "Squat", "focusArea": "Lower Body" }
  ]
}
```

The exercise catalog is currently static. Session persistence, authentication,
and cloud synchronization are not implemented.

## Connect from Android

- Android emulator: use `http://10.0.2.2:5000` instead of `localhost`.
- Physical device: use the development computer's LAN IP address, with both
  devices on the same network.

The Android app does not currently call this API. Add the Android `INTERNET`
permission and configure a development-only cleartext HTTP policy before making
requests over local HTTP. Use HTTPS for deployed APIs.

## Run tests

From the `backend` directory, with the virtual environment activated:

```powershell
python -m unittest discover -s tests
```
