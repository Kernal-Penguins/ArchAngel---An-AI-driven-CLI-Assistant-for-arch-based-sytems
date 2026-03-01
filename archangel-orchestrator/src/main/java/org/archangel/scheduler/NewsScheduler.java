package org.archangel.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.archangel.rss.ArchNewsFetcher;
import org.archangel.state.NewsCacheService;

@ApplicationScoped
public class NewsScheduler
{
    @Inject
    ArchNewsFetcher  archNewsFetcher;

    @Inject
    NewsCacheService  newsCacheService;

    @Scheduled(every = "10m" , delayed = "5s")
    public void refreshNews()
    {
        try {
            var news = archNewsFetcher.fetchRSSNews();
            newsCacheService.updateNews(news);
            System.out.println("News cache refreshed.");
        }
        catch (Exception e) {
            System.err.println("Failed to refresh news: " + e.getMessage());
        }
    }
}
