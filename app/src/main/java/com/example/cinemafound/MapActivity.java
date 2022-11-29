package com.example.cinemafound;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import static com.example.cinemafound.DBHelper.KEY_INQUIRE_ID;
import static com.example.cinemafound.DBHelper.KEY_INQUIRE_NAME;
import static com.example.cinemafound.DBHelper.KEY_INQUIRE_PHOTO;
import static com.example.cinemafound.DBHelper.KEY_LAT;
import static com.example.cinemafound.DBHelper.KEY_LAT_INQUIRE;
import static com.example.cinemafound.DBHelper.KEY_LNG;
import static com.example.cinemafound.DBHelper.KEY_LNG_INQUIRE;
import static com.example.cinemafound.DBHelper.KEY_MARKER_ID;
import static com.example.cinemafound.DBHelper.KEY_MARKER_NAME;
import static com.example.cinemafound.DBHelper.KEY_MARKER_PHOTO;
import static com.example.cinemafound.DBHelper.KEY_MARKER_REVIEW;
import static com.example.cinemafound.DBHelper.KEY_RATE;
import static com.example.cinemafound.DBHelper.TABLE_INQUIRIES;
import static com.example.cinemafound.DBHelper.TABLE_MARKERS;
import static com.example.cinemafound.DBHelper.TABLE_REVIEWS;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, TaskLoadedCallback {
    private static final String TAG = "mapActivityTag";
    public static GoogleMap mMap;
    public int userId;
    public int markerId;
    BottomNavigationView bottomNavigationView;
    Marker temp;
    MarkerOptions tempOptions;
    private CameraPosition mCameraPosition;
    TextView rateTextView;
    Fragment aboutFragment;
    Fragment listFragment;
    Fragment selectingFragment;
    Fragment searchFragment;
    Fragment addReviewFragment;
    Fragment reviewListFragment;
    Fragment profileFragment;
    DBHelper dbHelper;
    String userType;
    Button addReviewButton;
    Button confirmMarkerButton;
    LinearLayout mapViewLinearLayout;
    Polyline currentPolyline;
    Button createRouteButton;
    Dialog routeDialog;

    // The entry point to the Places API.
    private PlacesClient mPlacesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final int GALLERY_REQUEST = 1;
    boolean isGallery;
    public static boolean isSelectPlaceFrom = false, isSelectPlaceTo = false;
    private boolean mLocationPermissionGranted;

    boolean isInquireTemp = false;
    MarkerOptions placeFrom, placeTo;
    ImageView reviewImageView;
    LinearLayout reviewLinearLayout;
    TextView reviewTextView;
    SlidingUpPanelLayout slidingUpPanelLayout;
    LinearLayout slidingUpReviewLayout;
    LinearLayout slidingUpEditLayout;
    EditText placeName;
    ImageView placePhotoImageView;


    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    MarkerOptions place1, place2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        dbHelper = new DBHelper(this);
        createRouteButton = (Button) findViewById(R.id.createRouteButton);
        mapViewLinearLayout = (LinearLayout) findViewById(R.id.mapViewLinearLayout);
        reviewTextView = (TextView) findViewById(R.id.reviewTextView);
        reviewLinearLayout = (LinearLayout) findViewById(R.id.reviewLinearLayout);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingUpPanel);
        slidingUpEditLayout = (LinearLayout) findViewById(R.id.slidingAddingMarkerLinearLayout);
        slidingUpReviewLayout = (LinearLayout) findViewById(R.id.slidingReviewLinearLayout);
        reviewImageView = (ImageView) findViewById(R.id.reviewImageView);
        placeName = (EditText) findViewById(R.id.markerNameEditText);
        placePhotoImageView = (ImageView) findViewById(R.id.placePhotoImageView);
        addReviewButton = (Button) findViewById(R.id.addReviewButton);
        confirmMarkerButton = (Button) findViewById(R.id.confirmMarkerButton);
        rateTextView = (TextView) findViewById(R.id.rateTextView);

        userType = getIntent().getStringExtra("userType");
        userId = Integer.parseInt(getIntent().getStringExtra("userId"));
        Log.d(TAG, "userID: " + userId);

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        aboutFragment = new RouteFragment();
        listFragment = new ListFragment();
        searchFragment = new SearchFragment();
        addReviewFragment = new AddReviewFragment();
        reviewListFragment = new ReviewListFragment();
        profileFragment = new ProfileFragment();
        selectingFragment = new PlaceSelectingFragment();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment aboutFragmentManager = getSupportFragmentManager().findFragmentByTag(AboutFragment.TAG);
//                Fragment listFragmentManager = getSupportFragmentManager().findFragmentByTag(ListFragment.TAG);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right);
                switch (item.getItemId()) {
                    case R.id.mapsList:
                        if (getSupportFragmentManager().findFragmentByTag(ListFragment.TAG) == null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("userType", userType);
                            bundle.putString("userId", String.valueOf(userId));
                            listFragment.setArguments(bundle);
                            ft.replace(R.id.mapFrameLayout, listFragment, ListFragment.TAG);
                        }
                        //else getSupportFragmentManager().beginTransaction().remove(listFragmentManager).commit();
                        break;
                    case R.id.about:
                        if (getSupportFragmentManager().findFragmentByTag(RouteFragment.TAG) == null) {
                            ft.replace(R.id.mapFrameLayout, aboutFragment, RouteFragment.TAG);
                        }
                        //else getSupportFragmentManager().beginTransaction().remove(aboutFragmentManager).commit();
                        //getSupportFragmentManager().beginTransaction().add(R.id., new ListFragment(), ListFragment.TAG).commit();
                        break;
                    case R.id.search:
                        if (getSupportFragmentManager().findFragmentByTag(SearchFragment.TAG) == null) {
                            ft.replace(R.id.mapFrameLayout, searchFragment, SearchFragment.TAG);
                        }
                        break;
                    case R.id.profile:
                        if (getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG) == null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("userId", String.valueOf(userId));
                            profileFragment.setArguments(bundle);
                            ft.replace(R.id.mapFrameLayout, profileFragment, ProfileFragment.TAG);
                        }
                        break;
                    case R.id.mapItem:
                        if (getSupportFragmentManager().findFragmentByTag(ListFragment.TAG) != null)
                            ft.remove(listFragment);
                        if (getSupportFragmentManager().findFragmentByTag(RouteFragment.TAG) != null)
                            ft.remove(aboutFragment);
                        if (getSupportFragmentManager().findFragmentByTag(SearchFragment.TAG) != null)
                            ft.remove(searchFragment);
                        if (getSupportFragmentManager().findFragmentByTag(ProfileFragment.TAG) != null)
                            ft.remove(profileFragment);
                        break;

                }
                ft.commit();
                return true;
            }
        });

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.map_key));
        mPlacesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    public void onCreateRouteButtonClick(View v) {
        TextView placeFromTextView, placeToTextView;
        routeDialog = new Dialog(this);
        routeDialog.setContentView(R.layout.route_frame);
        routeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        placeFromTextView = routeDialog.findViewById(R.id.routeSelectedPlaceFrom);
        placeToTextView = routeDialog.findViewById(R.id.routeSelectedPlaceTo);
        placeFromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPlaceFromClickListener();
            }
        });
        placeToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPlaceToClickListener();
            }
        });
//        createRouteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (placeFrom != null && placeTo != null){
//                    String url = getUrl(placeFrom.getPosition(), placeTo.getPosition(), "driving");
//                    new FetchURL(MapActivity.this).execute(url, "driving");
//                }
//            }
//        });
//        createRouteButton = v.findViewById(R.id.createRouteButton);
//        createRouteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        routeDialog.show();
    }

    private void SelectPlaceToClickListener() {
        isSelectPlaceTo = true;
        isSelectPlaceFrom = false;
        Selecting();
    }

    private void SelectPlaceFromClickListener() {
        isSelectPlaceFrom = true;
        isSelectPlaceTo = false;
        Selecting();
    }

    private void Selecting() {
        if (routeDialog != null)
            routeDialog.dismiss();
        if (getSupportFragmentManager().findFragmentByTag(RouteFragment.TAG) != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(aboutFragment);
        }
        if (getSupportFragmentManager().findFragmentByTag(PlaceSelectingFragment.TAG) == null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                    .replace(R.id.mapFrameLayout, selectingFragment, PlaceSelectingFragment.TAG)
                    .commit();
            Log.d(TAG, "adding was true");
        }
    }

    public void onSubmitButtonClickListener(View v) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_INQUIRIES + " where round(" +
                KEY_LAT_INQUIRE + ", 4) = round(" + temp.getPosition().latitude + ", 4) and round(" + KEY_LNG_INQUIRE + ", 4) = round(" + temp.getPosition().longitude + ", 4)", null);
        Log.d(TAG, "marker: " + temp.getPosition().latitude + " : " + temp.getPosition().longitude);
        if (cursor.moveToFirst()) {
            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
            int inquireIdIndex = cursor.getColumnIndex(KEY_INQUIRE_ID);
            int nameIndex = cursor.getColumnIndex(KEY_INQUIRE_NAME);
            int latIndex = cursor.getColumnIndex(KEY_LAT_INQUIRE);
            int lngIndex = cursor.getColumnIndex(KEY_LNG_INQUIRE);
            int photoIndex = cursor.getColumnIndex(KEY_INQUIRE_PHOTO);
            do {
                //Log.d(TAG, "inquier marker: " + cursor.getDouble(latIndex) + ":" + cursor.getDouble(lngIndex));
                //if (cursor.getDouble(latIndex) == temp.getPosition().latitude && cursor.getDouble(lngIndex) == temp.getPosition().longitude) {
                Log.d(TAG, "rightUsl");

                ContentValues markerValues = new ContentValues();
                //Log.d(TAG, "submit mark name: " + cursor.getString())
                markerValues.put(KEY_LAT, cursor.getFloat(latIndex));
                markerValues.put(KEY_LNG, cursor.getFloat(lngIndex));
                markerValues.put(KEY_MARKER_NAME, cursor.getString(nameIndex));
                markerValues.put(KEY_MARKER_PHOTO, cursor.getBlob(photoIndex));
                dbWrite.insert(TABLE_MARKERS, null, markerValues);

                //}
            } while (cursor.moveToNext());
            dbWrite.execSQL("delete from " + TABLE_INQUIRIES + " where round(" +
                    KEY_LAT_INQUIRE + ", 4) = round(" + temp.getPosition().latitude + ", 4) and round(" + KEY_LNG_INQUIRE + ", 4) = round(" + temp.getPosition().longitude + ", 4)");
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        final SQLiteDatabase databaseLoginCheck = dbHelper.getReadableDatabase();
        if (userType.equals("Пользователь")) {
            try {
                Cursor cursor = databaseLoginCheck.rawQuery("select * from " + TABLE_MARKERS, null);
                if (cursor.moveToFirst()) {
                    int latIndex = cursor.getColumnIndex(KEY_LAT);
                    int lngIndex = cursor.getColumnIndex(KEY_LNG);
                    int nameIndex = cursor.getColumnIndex(KEY_MARKER_NAME);
                    do {
                        Log.d(TAG, cursor.getString(nameIndex) + " - " + cursor.getFloat(latIndex) + " : " + cursor.getFloat(lngIndex));
                    }
                    while (cursor.moveToNext());
                }
                if (cursor.moveToFirst()) {
                    LatLng markerFromDB;
                    int nameIndex = cursor.getColumnIndex(KEY_MARKER_NAME);
                    int latIndex = cursor.getColumnIndex(KEY_LAT);
                    int lngIndex = cursor.getColumnIndex(KEY_LNG);
                    Log.d(TAG, "markName: " + cursor.getString(nameIndex) + " = " + cursor.getDouble(latIndex) + " : " + cursor.getDouble(lngIndex));
                    do {
                        markerFromDB = new LatLng(cursor.getFloat(latIndex), cursor.getFloat(lngIndex));
                        mMap.addMarker(new MarkerOptions().position(markerFromDB).title(cursor.getString(nameIndex))
                                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker)));

                    }
                    while (cursor.moveToNext());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerFromDB, 14));
                }
                cursor.close();

            } catch (Exception e) {
                Log.d(TAG, " " + e.getMessage());
            }
        } else if (userType.equals("Администратор")) {
            try {
                Cursor cursor = databaseLoginCheck.rawQuery("select * from " + TABLE_MARKERS, null);
                if (cursor.moveToFirst()) {
                    LatLng markerFromDB;
                    int nameIndex = cursor.getColumnIndex(KEY_MARKER_NAME);
                    int latIndex = cursor.getColumnIndex(KEY_LAT);
                    int lngIndex = cursor.getColumnIndex(KEY_LNG);
                    do {
                        Log.d(TAG, "markName: " + cursor.getString(nameIndex) + " = " + cursor.getDouble(latIndex) + " : " + cursor.getDouble(lngIndex));
                        markerFromDB = new LatLng(cursor.getFloat(latIndex), cursor.getFloat(lngIndex));
                        mMap.addMarker(new MarkerOptions().position(markerFromDB).title(cursor.getString(nameIndex))
                                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker)));
                        Log.d(TAG, cursor.getString(nameIndex) + " - " + cursor.getFloat(latIndex) + " : " + cursor.getFloat(lngIndex));
                    }
                    while (cursor.moveToNext());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerFromDB, 14));
                }
                cursor = databaseLoginCheck.rawQuery("select * from " + TABLE_INQUIRIES, null);
                if (cursor.moveToFirst()) {
                    LatLng markerFromDB;
                    int nameIndex = cursor.getColumnIndex(KEY_INQUIRE_NAME);
                    int latIndex = cursor.getColumnIndex(KEY_LAT_INQUIRE);
                    int lngIndex = cursor.getColumnIndex(KEY_LNG_INQUIRE);
                    do {
                        markerFromDB = new LatLng(cursor.getFloat(latIndex), cursor.getFloat(lngIndex));
                        mMap.addMarker(new MarkerOptions().position(markerFromDB).title(cursor.getString(nameIndex))
                                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.inquire_logo)));
                        Log.d(TAG, cursor.getString(nameIndex) + " - " + cursor.getFloat(latIndex) + " : " + cursor.getFloat(lngIndex));
                    }
                    while (cursor.moveToNext());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerFromDB, 14));
                } else Log.d(TAG, " undefind markers");
                cursor.close();

            } catch (Exception e) {
                Log.d(TAG, " " + e.getMessage());
            }
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                if (isSelectPlaceFrom || isSelectPlaceTo) {
                    if (isSelectPlaceFrom) {
                        placeFrom = markerOptions;
                        mMap.addMarker(markerOptions);
                        if (PlaceSelectingFragment.placeFromSelectingFragment != null) {
                            PlaceSelectingFragment.placeFromSelectingFragment.setText(markerOptions.getTitle());
                        } else {
                            Toast.makeText(MapActivity.this, "PlaceSelectingFragment.placeFromSelectingFragment was null", Toast.LENGTH_LONG).show();
                        }
                        isSelectPlaceFrom = false;
                        isSelectPlaceTo = true;
                        if (placeFrom != null && placeTo != null) {
                            String url = getUrl(placeFrom.getPosition(), placeTo.getPosition(), "driving");
                            new FetchURL(MapActivity.this).execute(url, "driving");
                            FragmentManager fm = getSupportFragmentManager();
                            placeFrom = null;
                            placeTo = null;
                            if (RouteFragment.placeSelectingFragment != null){
                                fm.beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                        .remove(RouteFragment.placeSelectingFragment)
                                        .commit();
                            }
                            fm.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                    .remove(selectingFragment)
                                    .commit();
                        }
                    } else if (isSelectPlaceTo) {
                        placeTo = markerOptions;
                        mMap.addMarker(markerOptions);
                        if (PlaceSelectingFragment.placeToSelectingFragment != null) {
                            PlaceSelectingFragment.placeToSelectingFragment.setTextColor(Color.parseColor("#45CAB1"));
                            PlaceSelectingFragment.placeToSelectingFragment.setText(markerOptions.getTitle());
                        } else {
                            Toast.makeText(MapActivity.this, "PlaceSelectingFragment.placeToSelectingFragment was null", Toast.LENGTH_LONG).show();
                        }
                        isSelectPlaceFrom = false;
                        isSelectPlaceTo = false;
                        if (placeFrom != null && placeTo != null) {
                            String url = getUrl(placeFrom.getPosition(), placeTo.getPosition(), "driving");
                            new FetchURL(MapActivity.this).execute(url, "driving");
                            FragmentManager fm = getSupportFragmentManager();
                            placeFrom = null;
                            placeTo = null;
                            if (RouteFragment.placeSelectingFragment != null){
                                fm.beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                        .remove(RouteFragment.placeSelectingFragment)
                                        .commit();
                            }
                            fm.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                    .remove(selectingFragment)
                                    .commit();
                        }
                    }
                } else {
                    markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker));
                    tempOptions = markerOptions;
                    mMap.addMarker(markerOptions);
                    if (temp != null) {
                        temp.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker));
                    }
                    slidingUpPanelLayout.setVisibility(View.VISIBLE);
                    slidingUpPanelLayout.setPanelHeight(200);
                    slidingUpEditLayout.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                    slidingUpReviewLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (isSelectPlaceFrom || isSelectPlaceTo) {
                    if (isSelectPlaceFrom || isSelectPlaceTo) {
                        if (isSelectPlaceFrom) {
                            MarkerOptions tmp = new MarkerOptions();
                            tmp.position(marker.getPosition());
                            tmp.title(marker.getTitle());
                            placeFrom = tmp;
                            if (PlaceSelectingFragment.placeFromSelectingFragment != null) {
                                PlaceSelectingFragment.placeFromSelectingFragment.setText(tmp.getTitle());
                            } else {
                                Toast.makeText(MapActivity.this, "PlaceSelectingFragment.placeFromSelectingFragment was null", Toast.LENGTH_LONG).show();
                            }
                            isSelectPlaceFrom = false;
                            isSelectPlaceTo = true;
                            if (placeFrom != null && placeTo != null) {
                                String url = getUrl(placeFrom.getPosition(), placeTo.getPosition(), "driving");
                                new FetchURL(MapActivity.this).execute(url, "driving");
                                placeFrom = null;
                                placeTo = null;
                                FragmentManager fm = getSupportFragmentManager();
                                if (RouteFragment.placeSelectingFragment != null){
                                    fm.beginTransaction()
                                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                            .remove(RouteFragment.placeSelectingFragment)
                                            .commit();
                                }
                                fm.beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                        .remove(selectingFragment)
                                        .commit();
                            }
                        } else if (isSelectPlaceTo) {
                            MarkerOptions tmp = new MarkerOptions();
                            tmp.position(marker.getPosition());
                            tmp.title(marker.getTitle());
                            placeTo = tmp;
                            if (PlaceSelectingFragment.placeToSelectingFragment != null) {
                                PlaceSelectingFragment.placeToSelectingFragment.setTextColor(Color.parseColor("#45CAB1"));
                                PlaceSelectingFragment.placeToSelectingFragment.setText(tmp.getTitle());
                            } else {
                                Toast.makeText(MapActivity.this, "PlaceSelectingFragment.placeToSelectingFragment was null", Toast.LENGTH_LONG).show();
                            }
                            isSelectPlaceFrom = false;
                            isSelectPlaceTo = false;
                            if (placeFrom != null && placeTo != null) {
                                String url = getUrl(placeFrom.getPosition(), placeTo.getPosition(), "driving");
                                placeFrom = null;
                                placeTo = null;
                                new FetchURL(MapActivity.this).execute(url, "driving");
                                FragmentManager fm = getSupportFragmentManager();
                                if (RouteFragment.placeSelectingFragment != null){
                                    fm.beginTransaction()
                                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                            .remove(RouteFragment.placeSelectingFragment)
                                            .commit();
                                }
                                fm.beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                        .remove(selectingFragment)
                                        .commit();
                            }
                        }
                    }
                }
                else {
                    reviewImageView.setImageDrawable(null);
                    if (temp != null) {
                        if (!isInquireTemp) {
                            temp.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker));
                        } else {
                            temp.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.inquire_logo));
                        }
                    }
                    Log.d(TAG, "LatLng: " + marker.getTitle());
                    boolean isInquire = false;
                    slidingUpPanelLayout.setVisibility(View.VISIBLE);
                    slidingUpEditLayout.setVisibility(View.INVISIBLE);
                    slidingUpReviewLayout.setVisibility(View.VISIBLE);
                    slidingUpPanelLayout.setPanelHeight(150);
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                    reviewTextView.setText(marker.getTitle());
                    final char dm = (char) 34;
                    Cursor cursor = databaseLoginCheck.rawQuery("select * from " + TABLE_MARKERS + " where round(" +
                            KEY_LAT + ", 4) = round(" + marker.getPosition().latitude + ", 4) and round(" + KEY_LNG + ", 4) = round(" + marker.getPosition().longitude + ", 4)", null);
                    Bitmap bitmap = null;
                    Bundle bundle = new Bundle();
                    String markerName = "";
                    if (cursor.moveToFirst()) {
                        do {
                            int idIndex = cursor.getColumnIndex(KEY_MARKER_ID);
                            int latIndex = cursor.getColumnIndex(KEY_LAT);
                            int lngIndex = cursor.getColumnIndex(KEY_LNG);
                            int nameIndex = cursor.getColumnIndex(KEY_MARKER_NAME);
                            int markerPhotoIndex = cursor.getColumnIndex(KEY_MARKER_PHOTO);
                            if (cursor.getFloat(latIndex) == marker.getPosition().latitude && cursor.getFloat(lngIndex) == marker.getPosition().longitude) {
                                markerId = cursor.getInt(idIndex);
                                markerName = cursor.getString(nameIndex);
                                Log.d("MarkerPhoto", cursor.getBlob(markerPhotoIndex) != null ? " have photo" : " photo was null 1");
                                isInquire = false;
                                bitmap = getImage(cursor.getBlob(markerPhotoIndex));
                            }
                        } while (cursor.moveToNext());
                        float midRate = 0.0f;
                        int count = 0;
                        Cursor midRateCalculate = databaseLoginCheck.rawQuery("select * from " + TABLE_REVIEWS, null);
                        Log.d(TAG, "markerId = " + markerId);
                        if (midRateCalculate.moveToFirst()) {
                            Log.d(TAG, "calculating");
                            int rateIndex = midRateCalculate.getColumnIndex(KEY_RATE);
                            int markerIndex = midRateCalculate.getColumnIndex(KEY_MARKER_REVIEW);
                            do {
                                Log.d(TAG, "markerId: " + midRateCalculate.getInt(markerIndex));
                                if (midRateCalculate.getInt(markerIndex) == markerId) {
                                    count++;
                                    midRate += midRateCalculate.getFloat(rateIndex);
                                    Log.d(TAG, "midRate: " + midRate + " count: " + count);
                                }

                            } while (midRateCalculate.moveToNext());
                        }
                        midRate /= count;
                        rateTextView.setText(String.valueOf(midRate));
                        if (midRate >= 4.00) rateTextView.setTextColor(Color.parseColor("#00b894"));
                        else if (midRate < 4.00 && midRate >= 3.00)
                            rateTextView.setTextColor(Color.parseColor("#fdcb6e"));
                        else if (midRate > 0.0 && midRate < 3.00)
                            rateTextView.setTextColor(Color.parseColor("#d63031"));
                        else rateTextView.setTextColor(Color.parseColor("#2d3436"));
                        midRateCalculate.close();
                    }
                    if (bitmap != null) {
                        Bitmap scaled;
                        scaled = scaleDown(bitmap, 800, false);
                        if (scaled != null) {
                            reviewImageView.setImageBitmap(scaled);
                            bundle.putByteArray("scaled", getBytes(scaled));
                            Log.d(TAG, "scaled 1");
                        } else {
                            reviewImageView.setImageBitmap(bitmap);
                            bundle.putByteArray("scaled", getBytes(bitmap));
                            Log.d(TAG, "original 1");
                        }
                    } else {
                        cursor = databaseLoginCheck.rawQuery("select * from " + TABLE_INQUIRIES, null);

                        if (cursor.moveToFirst()) {
                            do {
                                int latIndex = cursor.getColumnIndex(KEY_LAT_INQUIRE);
                                int lngIndex = cursor.getColumnIndex(KEY_LNG_INQUIRE);
                                int inquirePhotoIndex = cursor.getColumnIndex(KEY_INQUIRE_PHOTO);
                                if (cursor.getFloat(latIndex) == marker.getPosition().latitude && cursor.getFloat(lngIndex) == marker.getPosition().longitude) {
                                    bitmap = getImage(cursor.getBlob(inquirePhotoIndex));
                                    rateTextView.setText("0.0");
                                    rateTextView.setTextColor(Color.parseColor("#2d3436"));
                                    isInquire = true;
                                }
                            } while (cursor.moveToNext());
                        }
                        if (bitmap != null) {
                            Bitmap scaled;
                            scaled = scaleDown(bitmap, 800, false);
                            if (scaled != null) {
                                reviewImageView.setImageBitmap(scaled);
                                Log.d(TAG, "scaled 2");
                            } else {
                                reviewImageView.setImageBitmap(bitmap);
                                Log.d(TAG, "original 2");
                            }
                        } else
                            Log.d(TAG, " photo was null 2");
                    }
                    cursor.close();
                    temp = marker;
                    bundle.putString("markerName", markerName);
                    bundle.putInt("markerId", markerId);
                    bundle.putString("userId", String.valueOf(userId));
                    reviewListFragment.setArguments(bundle);
                    if (isInquire) {
                        isInquireTemp = true;
                        addReviewButton.setVisibility(View.INVISIBLE);
                        confirmMarkerButton.setVisibility(View.VISIBLE);
                        marker.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.inquire_marker));
                    } else {
                        isInquireTemp = false;
                        addReviewButton.setVisibility(View.VISIBLE);
                        confirmMarkerButton.setVisibility(View.INVISIBLE);
                        marker.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker_selected));
                    }
                }
                return true;
            }
        });


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
        return url;
    }

    public void onAddReviewClick(View v) {
        Log.d(TAG, "adding review");
        if (getSupportFragmentManager().findFragmentByTag(ReviewListFragment.TAG) == null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                    .replace(R.id.mapFrameLayout, reviewListFragment, ReviewListFragment.TAG)
                    .commit();
            Log.d(TAG, "adding was true");
        }
        Log.d(TAG, "end of transaction");
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapActivity.this, AuthorizationActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void onAddMarkerButtonClickListener(View v) {
        if (userType.equals("Администратор")) {
            Bitmap bitmap = null;
            Log.d(TAG, tempOptions.getPosition().latitude + " : " + tempOptions.getPosition().longitude);
            if (placePhotoImageView.getDrawable() != null)
                bitmap = ((BitmapDrawable) placePhotoImageView.getDrawable()).getBitmap();
            else Log.d(TAG, " imageView was null");
            if (tempOptions != null && !placeName.getText().toString().equals("") && placeName.getText().toString().length() >= 4) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(KEY_MARKER_NAME, placeName.getText().toString());
                contentValues.put(KEY_LAT, tempOptions.getPosition().latitude);
                contentValues.put(KEY_LNG, tempOptions.getPosition().longitude);
                if (bitmap != null) {
                    contentValues.put(KEY_MARKER_PHOTO, getBytes(bitmap));
                    Log.d(TAG, "Image length: " + getBytes(bitmap).length);
                } else Log.d(TAG, " bitmap was null");

                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.insert(TABLE_MARKERS, null, contentValues);
                tempOptions.title(placeName.getText().toString());
                reviewTextView.setText(tempOptions.getTitle());
                bottomNavigationView.setVisibility(View.VISIBLE);
                slidingUpPanelLayout.setPanelHeight(0);

            } else {
                Toast.makeText(this, "Добавление не выполнено", Toast.LENGTH_SHORT).show();
                Log.d(TAG, !placeName.getText().toString().equals("") ? placeName.getText().toString() : " Название не введено");
            }
        } else if (userType.equals("Пользователь")) {
            Bitmap bitmap = null;
            Log.d(TAG, tempOptions.getPosition().latitude + " : " + tempOptions.getPosition().longitude);
            if (placePhotoImageView.getDrawable() != null)
                bitmap = ((BitmapDrawable) placePhotoImageView.getDrawable()).getBitmap();
            else Log.d(TAG, " imageView was null");
            if (tempOptions != null && !placeName.getText().toString().equals("") && placeName.getText().toString().length() >= 4) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(KEY_INQUIRE_NAME, placeName.getText().toString());
                contentValues.put(KEY_LAT_INQUIRE, tempOptions.getPosition().latitude);
                contentValues.put(KEY_LNG_INQUIRE, tempOptions.getPosition().longitude);
                if (bitmap != null) {
                    contentValues.put(KEY_INQUIRE_PHOTO, getBytes(bitmap));
                } else Log.d(TAG, " bitmap was null");

                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.insert(TABLE_INQUIRIES, null, contentValues);
                tempOptions.title(placeName.getText().toString());
                reviewTextView.setText(tempOptions.getTitle());
                bottomNavigationView.setVisibility(View.VISIBLE);
                slidingUpPanelLayout.setPanelHeight(0);

            } else {
                Toast.makeText(this, "Добавление не выполнено", Toast.LENGTH_SHORT).show();
                Log.d(TAG, !placeName.getText().toString().equals("") ? placeName.getText().toString() : " Название не введено");
            }
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min((float) maxImageSize / realImage.getWidth(), (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public void onPlaceImageButtonClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Откуда взять фото?")
                .setMessage("Фото")
                .setCancelable(true)
                .setPositiveButton("Галерея", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                        isGallery = true;
                    }
                })
                .setNegativeButton("Камера", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        isGallery = false;
                        startActivityForResult(intent, 1);
                    }
                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageView imageView = (ImageView) findViewById(R.id.placePhotoImageView);

        switch (requestCode) {
            //Загрузка изображения из галереи
            case GALLERY_REQUEST: {
                if (resultCode == RESULT_OK) {
                    if (isGallery) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageView.setImageBitmap(bitmap);
                    }
                    //Получение изображения с камеры устройства
                    else {
                        bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        imageView = (ImageView) findViewById(R.id.placePhotoImageView);
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    //Конвертация изображения в байты
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                80,
                bos);
        return bos.toByteArray();
    }


    public static byte[] getCompressedBitmapData(Bitmap bitmap, int maxFileSize, int maxDimensions) {
        Bitmap resizedBitmap;
        Log.d(TAG, " start compress");
        if (bitmap.getWidth() > maxDimensions || bitmap.getHeight() > maxDimensions) {
            Log.d(TAG, " size compressing");
            resizedBitmap = getResizedBitmap(bitmap,
                    maxDimensions);
        } else {
            Log.d(TAG, " pic is small");
            resizedBitmap = bitmap;
        }

        byte[] bitmapData = getBytes(resizedBitmap);

        Log.d(TAG, "start loop");
        while (bitmapData.length > maxFileSize) {
            bitmapData = getBytes(resizedBitmap);
        }
        Log.d(TAG, "end of loop");
        return bitmapData;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,
                width,
                height,
                true);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        Bitmap small = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
        return BitmapDescriptorFactory.fromBitmap(small);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void onCloseButtonClick(View v) {
        if (temp != null) {
            if (!isInquireTemp)
                temp.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.marker));
            else
                temp.setIcon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.inquire_logo));
        }
        reviewImageView.setImageDrawable(null);
        bottomNavigationView.setVisibility(View.VISIBLE);
        slidingUpPanelLayout.setPanelHeight(0);
        rateTextView.setText("0.0");
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
