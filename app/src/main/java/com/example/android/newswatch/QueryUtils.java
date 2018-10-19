package com.example.android.newswatch;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem parsing the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON news results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the Guardian data set and return a list of {@link NewsWatch} objects.
     */
    public static List<NewsWatch> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link news}s
        List<NewsWatch> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link news}s
        return news;
    }

    private static List<NewsWatch> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        List<NewsWatch> news = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject newsJSONObject = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = newsJSONObject.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNews = newsArray.getJSONObject(i);

                String section = currentNews.getString("sectionId");
                String webTitle = currentNews.getString("webTitle");
                String URL = currentNews.getString("webUrl");

                String date = formattedDate(currentNews.getString("webPublicationDate"));
                String author = "";
                String[] mySplitString = author.split(" ");
                JSONArray tagsArray = currentNews.getJSONArray("tags");
                if (tagsArray.length() > 0) {
                    JSONObject tagObject = tagsArray.getJSONObject(0);
                    mySplitString[0] = tagObject.getString("webTitle");
                }


                NewsWatch newswatch = new NewsWatch(date, section, webTitle, mySplitString[0], URL);

                news.add(newswatch);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON news results", e);
        }
        return news;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private static String formattedDate(String date) {
        String pattern = "dd-MM-yyyy";
        Date newDate = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat(pattern);
        try {
            newDate = inputFormat.parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing the date", e);
        }

        String formattedDate = outputFormat.format(newDate);
        return formattedDate;
    }
}