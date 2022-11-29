package com.example.cinemafound;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RouteFragment extends Fragment {
    public static PlaceSelectingFragment placeSelectingFragment;
    public static final String TAG = "aboutFragment";
    TextView selectPlaceFrom, selectPlaceTo;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.route_frame, container, false);
        placeSelectingFragment = new PlaceSelectingFragment();
        selectPlaceFrom = (TextView) rootView.findViewById(R.id.routeSelectedPlaceFrom);
        selectPlaceTo = (TextView) rootView.findViewById(R.id.routeSelectedPlaceTo);
        selectPlaceTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.isSelectPlaceTo= true;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                        .replace(R.id.mapFrameLayout, placeSelectingFragment, PlaceSelectingFragment.TAG)
                        .commit();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                        .remove(RouteFragment.this)
                        .commit();
            }
        });
        selectPlaceFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity.isSelectPlaceFrom = true;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                        .replace(R.id.mapFrameLayout, placeSelectingFragment, PlaceSelectingFragment.TAG)
                        .commit();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                        .remove(RouteFragment.this)
                        .commit();
            }
        });
        return rootView;
    }
}
