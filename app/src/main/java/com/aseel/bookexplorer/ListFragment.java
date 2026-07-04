package com.aseel.bookexplorer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tab 1: shows the list of books fetched from the API inside a RecyclerView.
 */
public class ListFragment extends Fragment {

    private SharedBookViewModel viewModel;
    private BooksAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Book> fullList = new ArrayList<>();

    public ListFragment() {
        super(R.layout.fragment_list);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SharedBookViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerBooks);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        adapter = new BooksAdapter(book -> viewModel.selectBook(book));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
            fullList = books;
            adapter.setBooks(books);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadBooks("android development");
        });

        NotificationHelper.createChannel(requireContext());

        // Only load on first entry (avoid re-fetching every time the tab is revisited)
        if (fullList.isEmpty()) {
            loadBooks("android development");
        }
    }

    private void loadBooks(String query) {
        new FetchBooksTask(new FetchBooksTask.Listener() {
            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(List<Book> books) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                viewModel.setBooks(books);
                NotificationHelper.showSuccess(requireContext(), books.size());
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                NotificationHelper.showError(requireContext(), message);
            }
        }).execute(query);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void filterList(String query) {
        if (query == null || query.isEmpty()) {
            adapter.setBooks(fullList);
            return;
        }
        List<Book> filtered = new ArrayList<>();
        String lower = query.toLowerCase(Locale.getDefault());
        for (Book b : fullList) {
            if (b.getTitle().toLowerCase(Locale.getDefault()).contains(lower)
                    || b.getAuthors().toLowerCase(Locale.getDefault()).contains(lower)) {
                filtered.add(b);
            }
        }
        adapter.setBooks(filtered);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            loadBooks("android development");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
