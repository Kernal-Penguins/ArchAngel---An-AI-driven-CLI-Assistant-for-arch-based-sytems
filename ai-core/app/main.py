from fastapi import FastAPI, HTTPException
import httpx
import feedparser

app = FastAPI()

JAVA_RSS_URL = "http://127.0.0.1:8080/rss-test"

@app.get("/archnews")
async def archnews():
    async with httpx.AsyncClient(timeout=10) as client:
        try:
            r = await client.get(JAVA_RSS_URL)
            r.raise_for_status()
        except httpx.HTTPError as e:
            raise HTTPException(status_code=502, detail=str(e))

    feed = feedparser.parse(r.text)

    return {
        "title": feed.feed.get("title"),
        "link": feed.feed.get("link"),
        "description": feed.feed.get("description"),
        "last_updated": feed.feed.get("updated"),
        "items": [
            {
                "title": entry.get("title"),
                "link": entry.get("link"),
                "published": entry.get("published"),
                "summary": entry.get("summary"),
            }
            for entry in feed.entries
        ],
    }