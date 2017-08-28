package com.fabiohideki.filmesfamosos.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fabiohideki.filmesfamosos.R;
import com.fabiohideki.filmesfamosos.model.Movie;
import com.fabiohideki.filmesfamosos.utils.MovieUrlUtils;
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

        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        int height = (int) ((parent.getMeasuredHeight() - actionBarHeight) / 2);

        int widthDivider = 400;
        int width = parent.getMeasuredWidth();

        int nColumns = width / widthDivider;
        if (nColumns < 2) {
            nColumns = 2;
        }

        width = parent.getMeasuredWidth() / nColumns;

        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.with(context).load(MovieUrlUtils.buildUrlPoster(movie.getPosterPath())).into(holder.posterImage);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemPosterClick(View view, Movie movie);
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
                mClickListener.onItemPosterClick(view, movies.get(getAdapterPosition()));
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
