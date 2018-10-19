package com.example.android.newswatch;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

public class NewsWatchLoader extends AsyncTaskLoader<List<NewsWatch>> {

    private String mUrl;

    public NewsWatchLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<NewsWatch> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<NewsWatch> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}
