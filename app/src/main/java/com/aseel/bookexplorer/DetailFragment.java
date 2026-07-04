package com.aseel.bookexplorer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

/**
 * Tab 2: shows details of the currently selected book.
 * Defaults to the first item in the list if nothing has been tapped yet.
 */
public class DetailFragment extends Fragment {

    private TextView textTitle;
    private TextView textAuthors;
    private TextView textDate;
    private TextView textDescription;
    private ImageView imageCover;

    public DetailFragment() {
        super(R.layout.fragment_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textTitle = view.findViewById(R.id.detailTitle);
        textAuthors = view.findViewById(R.id.detailAuthors);
        textDate = view.findViewById(R.id.detailDate);
        textDescription = view.findViewById(R.id.detailDescription);
        imageCover = view.findViewById(R.id.detailImage);

        SharedBookViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(SharedBookViewModel.class);

        viewModel.getSelectedBook().observe(getViewLifecycleOwner(), this::bind);
    }

    private void bind(Book book) {
        if (book == null) return;
        textTitle.setText(book.getTitle());
        textAuthors.setText(book.getAuthors());
        textDate.setText(getString(R.string.published_format, book.getPublishedDate()));
        textDescription.setText(book.getDescription());

        if (book.getThumbnailUrl() != null && !book.getThumbnailUrl().isEmpty()) {
            Picasso.get().load(book.getThumbnailUrl())
                    .placeholder(R.drawable.cover_placeholder)
                    .error(R.drawable.cover_placeholder)
                    .into(imageCover);
        } else {
            imageCover.setImageResource(R.drawable.cover_placeholder);
        }
    }
}
