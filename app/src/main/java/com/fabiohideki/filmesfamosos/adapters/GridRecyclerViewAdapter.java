package com.fabiohideki.filmesfamosos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fabiohideki.filmesfamosos.R;
import com.fabiohideki.filmesfamosos.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fabio.lagoa on 22/08/2017.
 */

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<GridRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<Movie> movies;
    private Context context;

    public GridRecyclerViewAdapter(Context context, List<Movie> movies) {
        this.mInflater = LayoutInflater.from(context);
        this.movies = movies;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean shouldAttachToParentImmediately = false;

        View view = mInflater.inflate(R.layout.item_grid, parent, shouldAttachToParentImmediately);

        int height = parent.getMeasuredHeight() / 2;
        int width = parent.getMeasuredWidth() / 2;
        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.with(context).load("http://image.tmdb.org/t/p/" + "w500" + movie.getPosterPath()).into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(String title);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView posterImage;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(movies.get(getAdapterPosition()).getTitle());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
