package com.example.cinemafound;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class MarkerAdapter extends BaseAdapter {
    private List<Marker> placeList;
    private LayoutInflater layoutInflater;

    public MarkerAdapter(Context context, List<Marker> dateList){
        this.placeList = dateList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return placeList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return placeList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            view = layoutInflater.inflate(R.layout.place_layout, viewGroup, false);
        }
        Marker place = (Marker) getItem(position);
        TextView textViewName = (TextView)view.findViewById(R.id.place_name_textView);
        TextView textViewRate = (TextView)view.findViewById(R.id.place_rate_textView);
        TextView textViewLatLng = (TextView)view.findViewById(R.id.place_lat_lng_textView);
        ImageView imageViewPhoto = (ImageView)view.findViewById(R.id.place_photo_imageView);
        textViewName.setText(place.getName());

        textViewRate.setText(String.valueOf(place.getRate()));
        if (place.getRate() >= 4.00) textViewRate.setTextColor(Color.parseColor("#41E194"));
        else if (place.getRate() < 4.00 && place.getRate() >= 3.00) textViewRate.setTextColor(Color.parseColor("#E1C741"));
        else if (place.getRate() > 0.0 && place.getRate() < 3.00) textViewRate.setTextColor(Color.parseColor("#636e72"));

        String lat_lng = String.valueOf(place.getLatitude()) + " : " + String.valueOf(place.getLongitude());
        textViewLatLng.setText(lat_lng);

        if(place.getPhoto() != null)
            imageViewPhoto.setImageBitmap(place.getPhoto());
        return view;
    }
}
