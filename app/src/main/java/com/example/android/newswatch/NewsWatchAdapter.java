package com.example.android.newswatch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsWatchAdapter extends ArrayAdapter<NewsWatch> {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public NewsWatchAdapter(@NonNull Context context, ArrayList<NewsWatch> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_watch_list_item, parent, false);
        }
        NewsWatch currentNews = getItem(position);

        TextView authorNameView = listItemView.findViewById(R.id.author);
        if (currentNews.getAuthor() != "") {
            authorNameView.setText(currentNews.getAuthor());
        } else {
            authorNameView.setText("No author");
        }

        TextView sectionTitleView = listItemView.findViewById(R.id.section_title);
        sectionTitleView.setText(currentNews.getSectionTitle());

        TextView newsDetails = listItemView.findViewById(R.id.news_details);
        newsDetails.setText(currentNews.getSectionDetails());


        TextView dateView = listItemView.findViewById(R.id.date);
        dateView.setText(currentNews.getPublishDate());

        return listItemView;
    }

}
