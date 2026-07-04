package com.aseel.bookexplorer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    private List<Book> books = new ArrayList<>();
    private final OnBookClickListener listener;

    public BooksAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void setBooks(List<Book> newBooks) {
        this.books = newBooks != null ? newBooks : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void filter(String query) {
        // used by search functionality in ListFragment via a full list copy
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.authors.setText(book.getAuthors());

        if (book.getThumbnailUrl() != null && !book.getThumbnailUrl().isEmpty()) {
            Picasso.get().load(book.getThumbnailUrl())
                    .placeholder(R.drawable.cover_placeholder)
                    .error(R.drawable.cover_placeholder)
                    .into(holder.cover);
        } else {
            holder.cover.setImageResource(R.drawable.cover_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(book);
        });

        // Context/Popup menu requirement: long-press to show quick actions
        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(v, book);
            return true;
        });
    }

    private void showPopupMenu(View anchor, Book book) {
        PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
        popup.getMenu().add("View details");
        popup.getMenu().add("Copy title");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("View details")) {
                if (listener != null) listener.onBookClick(book);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        anchor.getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("title", book.getTitle());
                if (clipboard != null) clipboard.setPrimaryClip(clip);
                Toast.makeText(anchor.getContext(), "Title copied", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView authors;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imageCover);
            title = itemView.findViewById(R.id.textTitle);
            authors = itemView.findViewById(R.id.textAuthors);
        }
    }
}
