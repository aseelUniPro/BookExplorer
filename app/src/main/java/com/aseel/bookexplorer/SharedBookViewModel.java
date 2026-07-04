package com.aseel.bookexplorer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared between ListFragment and DetailFragment (scoped to the Activity)
 * so that selecting a book in Tab 1 immediately updates Tab 2.
 * This is how data is passed between the two fragments.
 */
public class SharedBookViewModel extends ViewModel {

    private final MutableLiveData<List<Book>> books = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Book> selectedBook = new MutableLiveData<>();

    public LiveData<List<Book>> getBooks() {
        return books;
    }

    public LiveData<Book> getSelectedBook() {
        return selectedBook;
    }

    public void setBooks(List<Book> newBooks) {
        books.setValue(newBooks);
        // Default Tab 2 to the first item if nothing has been selected yet
        if (selectedBook.getValue() == null && newBooks != null && !newBooks.isEmpty()) {
            selectedBook.setValue(newBooks.get(0));
        }
    }

    public void selectBook(Book book) {
        selectedBook.setValue(book);
    }
}
