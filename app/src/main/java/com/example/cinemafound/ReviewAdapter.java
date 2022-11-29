package com.example.cinemafound;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends BaseAdapter {
    private List<Review> reviewList;
    private LayoutInflater layoutInflater;

    public ReviewAdapter(Context context, List<Review> dateList){
        this.reviewList = dateList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            view = layoutInflater.inflate(R.layout.review_layout, viewGroup, false);
        }
        Review review = (Review) getItem(position);
        TextView textViewUserName = (TextView)view.findViewById(R.id.userNameTextView);
        TextView textViewReview = (TextView)view.findViewById(R.id.otzTextView);
        TextView textViewRating = (TextView)view.findViewById(R.id.ratingTextView);
        CircleImageView imageViewPhoto = (CircleImageView) view.findViewById(R.id.userPhotoImageView);
        textViewUserName.setText(review.getUserName());

        textViewRating.setText(String.valueOf(review.getRate()));
        if (review.getRate() >= 4.00) textViewRating.setTextColor(Color.parseColor("#00b894"));
        else if (review.getRate() < 4.00 && review.getRate() >= 3.00) textViewRating.setTextColor(Color.parseColor("#fdcb6e"));
        else if (review.getRate() > 0.0 && review.getRate() < 3.00) textViewRating.setTextColor(Color.parseColor("#d63031"));
        else textViewRating.setTextColor(Color.parseColor("#2d3436"));

        textViewReview.setText(review.getReview());

        if(review.getUserPhoto() != null)
            imageViewPhoto.setImageBitmap(review.getUserPhoto());
        return view;
    }
}
