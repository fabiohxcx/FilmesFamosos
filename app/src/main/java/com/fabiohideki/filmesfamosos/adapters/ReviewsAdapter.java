package com.fabiohideki.filmesfamosos.adapters;

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
import com.fabiohideki.filmesfamosos.model.ResultReviews;
import com.fabiohideki.filmesfamosos.model.Review;

/**
 * Created by fabio.lagoa on 26/12/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private ResultReviews resultReviews;
    private Context context;

    public ReviewsAdapter(Context context, ResultReviews resultReviews) {
        this.context = context;
        this.resultReviews = resultReviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_row, parent, false);

        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        Review review = resultReviews.getReviews().get(position);
        holder.auhor.setText(review.getAuthor());
        holder.comment.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        return resultReviews.getReviews().size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView auhor;
        public TextView comment;
        public ImageView urlLink;


        public ReviewViewHolder(View itemView) {
            super(itemView);

            auhor = itemView.findViewById(R.id.tv_review_author);
            comment = itemView.findViewById(R.id.tv_review_content);
            urlLink = itemView.findViewById(R.id.iv_review_url);
            urlLink.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intentUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(resultReviews.getReviews().get(getAdapterPosition()).getUrl()));
            context.startActivity(intentUrl);
        }
    }
}
