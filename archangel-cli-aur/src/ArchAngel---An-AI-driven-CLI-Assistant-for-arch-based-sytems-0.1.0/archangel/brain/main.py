from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests

app = FastAPI()

OLLAMA_URL = "http://localhost:11434/api/generate"
OLLAMA_MODEL = "qwen3.5:9b"   # or any installed model


class LogRequest(BaseModel):
    logs: str


class GeneratedResponse(BaseModel):
    severity: str
    summary: str
    recommendedAction: str
    analysisValid: bool


def parse_ai_response(text: str):
    try:
        severity = "UNKNOWN"
        summary = ""
        action = ""

        for line in text.split("\n"):
            l = line.lower()

            if "severity" in l:
                severity = line.split(":")[-1].strip().upper()
            elif "summary" in l:
                summary = line.split(":", 1)[-1].strip()
            elif "action" in l:
                action = line.split(":", 1)[-1].strip()

        return GeneratedResponse(
            severity=severity or "UNKNOWN",
            summary=summary or "Could not parse summary",
            recommendedAction=action or "No action parsed",
            analysisValid=True
        )

    except Exception:
        return None


def analyze_with_ollama(logs: str):
    try:
        prompt = f"""
Analyze these Linux logs.

Return EXACTLY in this format:
Severity: <LOW/MEDIUM/HIGH/CRITICAL>
Summary: <short explanation>
Action: <what to do>

Logs:
{logs}
"""

        res = requests.post(
            OLLAMA_URL,
            json={
                "model": OLLAMA_MODEL,
                "prompt": prompt,
                "stream": False
            },
            timeout=30
        )

        res.raise_for_status()
        data = res.json()

        content = data.get("response", "")

        if content:
            return parse_ai_response(content)

    except Exception as e:
        print("Ollama error:", e)

    return None


@app.post("/analyze", response_model=GeneratedResponse)
def analyze(req: LogRequest):
    logs = req.logs.strip()

    if not logs:
        raise HTTPException(status_code=400, detail="Logs cannot be empty")

    result = analyze_with_ollama(logs)
    if result:
        return result

    return GeneratedResponse(
        severity="UNKNOWN",
        summary="Ollama failed to generate response",
        recommendedAction="Check if Ollama is running: ollama serve",
        analysisValid=False
    )


@app.post("/analysis/logs", response_model=GeneratedResponse)
def analyze_logs_compat(req: LogRequest):
    return analyze(req)


@app.get("/health")
def health():
    return {"status": "brain alive (ollama)"}