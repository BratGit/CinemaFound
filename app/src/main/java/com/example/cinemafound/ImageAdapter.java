package com.example.cinemafound;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    //public int count = 5;
    public ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    //public Bitmap[] mImageIds = new Bitmap[count];


    ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if(!images.isEmpty()) {
            imageView.setImageBitmap(images.get(position));
            container.addView(imageView, 0);
        }
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //container.removeViewAt(position);
        container.removeView((ImageView) object);
    }
}
