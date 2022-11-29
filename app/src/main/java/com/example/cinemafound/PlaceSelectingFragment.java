package com.example.cinemafound;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlaceSelectingFragment extends Fragment {
    public static final String TAG = "PlaceSelectingFragment";
    public static TextView placeToSelectingFragment, placeFromSelectingFragment;
    View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.selecting_frame, container, false);
        placeToSelectingFragment = rootView.findViewById(R.id.placeToSelectingFragment);
        placeFromSelectingFragment = rootView.findViewById(R.id.placeFromSelectingFragment);
        return rootView;
    }
}
