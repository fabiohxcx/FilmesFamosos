package com.fabiohideki.filmesfamosos.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fabiohideki.filmesfamosos.R;
import com.fabiohideki.filmesfamosos.model.ResultTrailers;
import com.fabiohideki.filmesfamosos.model.Trailer;
import com.squareup.picasso.Picasso;

/**
 * Created by fabio.lagoa on 18/12/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private ResultTrailers resultTrailers;
    private Context context;

    public TrailersAdapter(Context context, ResultTrailers resultTrailers) {
        this.resultTrailers = resultTrailers;
        this.context = context;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer_row, parent, false);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Trailer trailer = resultTrailers.getTrailers().get(position);
        holder.name.setText(trailer.getName());
        Picasso.with(context).load(getThumbURL(trailer.getKey())).into(holder.ivTrailerThumb);
    }

    @Override
    public int getItemCount() {
        return resultTrailers.getTrailers().size();
    }


    public String getThumbURL(String key) {
        return "https://img.youtube.com/vi/" + key + "/hqdefault.jpg";
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView id, name;
        public ImageView ivTrailerThumb;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_trailer_name);
            ivTrailerThumb = itemView.findViewById(R.id.iv_trailer_thumb);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            watchYoutubeVideo(context, resultTrailers.getTrailers().get(getAdapterPosition()).getKey());

        }

        public void watchYoutubeVideo(Context context, String key) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + key));
            try {
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }
        }


    }
}
