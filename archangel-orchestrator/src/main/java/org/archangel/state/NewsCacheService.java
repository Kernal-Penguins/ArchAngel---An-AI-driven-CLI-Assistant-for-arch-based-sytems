package org.archangel.state;

import jakarta.enterprise.context.ApplicationScoped;
import org.archangel.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NewsCacheService
{
    public List<NewsItem> cachedNews = new ArrayList<>();
    public synchronized void updateNews(List<NewsItem> news)
    {
        this.cachedNews = news;
    }

    public synchronized List<NewsItem> getNews()
    {
        return cachedNews;
    }
}
