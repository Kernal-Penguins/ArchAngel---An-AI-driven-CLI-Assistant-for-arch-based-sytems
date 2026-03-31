package org.archangel.state;

import jakarta.enterprise.context.ApplicationScoped;
import org.archangel.model.NewsItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NewsCacheService {

    // 30 minutes — if cache is older than this, warn callers
    private static final long STALE_THRESHOLD_SECONDS = 1800;

    private List<NewsItem> cachedNews = new ArrayList<>();
    private Instant lastSuccessfulFetch = null;
    private int consecutiveFailures = 0;

    public synchronized void updateNews(List<NewsItem> news) {
        this.cachedNews = new ArrayList<>(news);
        this.lastSuccessfulFetch = Instant.now();
        this.consecutiveFailures = 0;
    }

    public synchronized void recordFetchFailure() {
        this.consecutiveFailures++;
    }

    public synchronized List<NewsItem> getNews() {
        return new ArrayList<>(cachedNews);
    }

    public synchronized Instant getLastSuccessfulFetch() {
        return lastSuccessfulFetch;
    }

    public synchronized boolean isStale() {
        if (lastSuccessfulFetch == null) return true;
        return Instant.now().getEpochSecond() - lastSuccessfulFetch.getEpochSecond() > STALE_THRESHOLD_SECONDS;
    }

    public synchronized int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public synchronized boolean hasData() {
        return !cachedNews.isEmpty();
    }
}