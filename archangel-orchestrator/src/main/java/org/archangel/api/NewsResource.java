package org.archangel.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.archangel.model.NewsItem;
import org.archangel.rss.ArchNewsFetcher;

import java.util.List;

@Path("news")
public class NewsResource
{
  @Inject
   ArchNewsFetcher archNewsFetcher;
  @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<NewsItem> getNews() throws Exception
    {
        return archNewsFetcher.fetchRSSNews();
    }
}
