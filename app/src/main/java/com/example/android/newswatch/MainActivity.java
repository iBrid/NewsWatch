package com.example.android.newswatch;

        import android.app.LoaderManager;
        import android.content.Context;
        import android.content.Intent;
        import android.content.Loader;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsWatch>> {
    SwipeRefreshLayout swipeLayout;

    private TextView mEmptyTextView;
    private static final int LOADER_NEWS_ID = 1;
    private static final String GUARDIAN_REQUEST_URL = "content.guardianapis.com";

    private NewsWatchAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting SwipeContainerLayout
        swipeLayout = findViewById(R.id.swipe_container);

        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {

                // Your code here
                Toast.makeText(getApplicationContext(), "Reloading...", Toast.LENGTH_LONG).show();

                // To keep animation for 6 seconds
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        // Stop animation (This will be after 3 seconds)
                        swipeLayout.setRefreshing(false);
                    }

                }, 6000); // Delay in millis

            }

        });



        // Scheme colors for animation
        swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );


        ListView listView = findViewById(R.id.list);
        mEmptyTextView = findViewById(R.id.empty_textView);
        listView.setEmptyView(mEmptyTextView);



        // Create a new adapter that takes an empty list of news items as input
        newsAdapter = new NewsWatchAdapter(this, new ArrayList<NewsWatch>());

        listView.setAdapter(newsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsWatch currentNews = newsAdapter.getItem(position);
                Uri uri = Uri.parse(currentNews.getWebAddress());

                // Create a new intent to view the news item URI
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                // Send the intent to launch a new activity
                startActivity(intent);
            }
        });


    // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_NEWS_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    @NonNull
    public Loader<List<NewsWatch>> onCreateLoader(int id, @Nullable Bundle args) {
        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.scheme("https")
                .encodedAuthority(GUARDIAN_REQUEST_URL)
                .appendQueryParameter("api-key", "01948f9d-138d-4d18-85b4-b8f594df0718")
                .appendPath("search")
                .appendQueryParameter("order-by", "newest")
                .appendQueryParameter("q", getString(R.string.search))
                .appendQueryParameter("section", getString(R.string.section))
                .appendQueryParameter("show-tags", getString(R.string.show_tags));

        return new NewsWatchLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsWatch>> loader, List<NewsWatch> news) {
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news available."
        mEmptyTextView.setText(R.string.no_news_found);

        // Clear the adapter of previous news data
        newsAdapter.clear();

        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsWatch>> loader) {
        newsAdapter.clear();
    }
}
