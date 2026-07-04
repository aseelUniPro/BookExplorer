package com.aseel.bookexplorer;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches book data from the Google Books API in a background thread,
 * as required by the project ("AsyncTask must be used to fetch API data
 * in the background").
 */
public class FetchBooksTask extends AsyncTask<String, Void, List<Book>> {

    public interface Listener {
        void onStart();
        void onSuccess(List<Book> books);
        void onError(String message);
    }

    private final Listener listener;
    private String errorMessage = null;

    public FetchBooksTask(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onStart();
        }
    }

    @Override
    protected List<Book> doInBackground(String... query) {
        List<Book> results = new ArrayList<>();
        HttpURLConnection connection = null;
        try {
            String searchTerm = (query.length > 0 && query[0] != null && !query[0].isEmpty())
                    ? query[0] : "android development";
            String urlString = "https://www.googleapis.com/books/v1/volumes?q="
                    + java.net.URLEncoder.encode(searchTerm, "UTF-8") + "&maxResults=25";

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                errorMessage = "Server returned code " + responseCode;
                return results;
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            results = parseBooks(builder.toString());

        } catch (Exception e) {
            errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown network error";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return results;
    }

    private List<Book> parseBooks(String json) throws Exception {
        List<Book> books = new ArrayList<>();
        JSONObject root = new JSONObject(json);
        if (!root.has("items")) {
            return books;
        }
        JSONArray items = root.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String id = item.optString("id", String.valueOf(i));
            JSONObject volumeInfo = item.optJSONObject("volumeInfo");
            if (volumeInfo == null) continue;

            String title = volumeInfo.optString("title", "Untitled");

            String authors = "Unknown author";
            if (volumeInfo.has("authors")) {
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                StringBuilder authorsBuilder = new StringBuilder();
                for (int j = 0; j < authorsArray.length(); j++) {
                    if (j > 0) authorsBuilder.append(", ");
                    authorsBuilder.append(authorsArray.getString(j));
                }
                authors = authorsBuilder.toString();
            }

            String description = volumeInfo.optString("description", "No description available.");
            String publishedDate = volumeInfo.optString("publishedDate", "Unknown");

            String thumbnailUrl = "";
            JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
            if (imageLinks != null) {
                thumbnailUrl = imageLinks.optString("thumbnail", "");
                thumbnailUrl = thumbnailUrl.replace("http://", "https://");
            }

            books.add(new Book(id, title, authors, description, thumbnailUrl, publishedDate));
        }
        return books;
    }

    @Override
    protected void onPostExecute(List<Book> books) {
        if (listener == null) return;
        if (errorMessage != null) {
            listener.onError(errorMessage);
        } else {
            listener.onSuccess(books);
        }
    }
}
