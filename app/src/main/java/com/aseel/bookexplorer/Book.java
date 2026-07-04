package com.aseel.bookexplorer;

/**
 * Simple data model representing a single Book returned by the
 * Google Books API.
 */
public class Book {

    private final String id;
    private final String title;
    private final String authors;
    private final String description;
    private final String thumbnailUrl;
    private final String publishedDate;

    public Book(String id, String title, String authors, String description,
                String thumbnailUrl, String publishedDate) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedDate = publishedDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
}
