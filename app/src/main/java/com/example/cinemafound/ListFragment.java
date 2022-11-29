package com.example.cinemafound;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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

public class ListFragment extends Fragment {
    public static final String TAG = "listFragment";
    ReviewListFragment reviewListFragment;
    DBHelper dbHelper;
    ListView list;
    Bundle bundle;
    String userType;
    int userId;
    Cursor cursor;

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbHelper = new DBHelper(getActivity());
        bundle = getArguments();
        userId = Integer.parseInt(bundle.getString("userId"));
        userType = bundle.getString("userType");
        SQLiteDatabase databaseMarkerAdd = dbHelper.getReadableDatabase();
        final List<Marker> places = new ArrayList<Marker>();
        reviewListFragment = new ReviewListFragment();
        MarkerAdapter markerAdapter = new MarkerAdapter(getActivity(), places);
        cursor = databaseMarkerAdd.rawQuery("select * from " + TABLE_MARKERS, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_MARKER_ID);
            int nameIndex = cursor.getColumnIndex(KEY_MARKER_NAME);
            int latIndex = cursor.getColumnIndex(KEY_LAT);
            int lngIndex = cursor.getColumnIndex(KEY_LNG);
            int photoIndex = cursor.getColumnIndex(KEY_MARKER_PHOTO);

            do {
                Cursor data = databaseMarkerAdd.rawQuery("select * from " + TABLE_REVIEWS + " where " + KEY_MARKER_REVIEW + " = " + cursor.getInt(idIndex), null);
                float midRate = 0.0f;
                int count = 0;
                if (data.moveToFirst()){
                    int rateIndex = data.getColumnIndex(KEY_RATE);
                    do {
                        midRate += data.getFloat(rateIndex);
                        count++;
                    }while(data.moveToNext());
                    midRate /= count;
                }
                data.close();
                Bitmap bitmap = getImage(cursor.getBlob(photoIndex));
                Bitmap scaled;
                scaled = scaleDown(bitmap, 800, false);
                Log.d("markers", cursor.getString(nameIndex) + " - " + cursor.getFloat(latIndex) + " : " + cursor.getFloat(lngIndex));
                places.add(new Marker(cursor.getInt(idIndex), cursor.getString(nameIndex), midRate, cursor.getFloat(latIndex), cursor.getFloat(lngIndex), scaled));
            }
            while (cursor.moveToNext());
        }

        if (userType.equals("Администратор")) {
            cursor = databaseMarkerAdd.rawQuery("select * from " + TABLE_INQUIRIES, null);
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_INQUIRE_ID);
                int nameIndex = cursor.getColumnIndex(KEY_INQUIRE_NAME);
                int latIndex = cursor.getColumnIndex(KEY_LAT_INQUIRE);
                int lngIndex = cursor.getColumnIndex(KEY_LNG_INQUIRE);
                int photoIndex = cursor.getColumnIndex(KEY_INQUIRE_PHOTO);
                do {
                    Bitmap bitmap = getImage(cursor.getBlob(photoIndex));
                    Bitmap scaled;
                    scaled = scaleDown(bitmap, 800, false);
                    Log.d("inquires", cursor.getString(nameIndex) + " - " + cursor.getFloat(latIndex) + " : " + cursor.getFloat(lngIndex));
                    places.add(new Marker(cursor.getInt(idIndex), cursor.getString(nameIndex) + " [*]", 0.0f, cursor.getFloat(latIndex), cursor.getFloat(lngIndex), scaled));
                } while (cursor.moveToNext());
            }
        }
        rootView = inflater.inflate(R.layout.places_list_frame, null);
        list  = rootView.findViewById(R.id.place_list_listView);
        list.setAdapter(markerAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase databaseMarkerAdd = dbHelper.getReadableDatabase();
                Cursor cursor = databaseMarkerAdd.rawQuery("select * from " + TABLE_MARKERS + " where " +
                        KEY_MARKER_ID + " = " + places.get(position).getId(), null);
                if (cursor.moveToFirst()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("markerId", places.get(position).getId());
                    bundle.putString("markerName", places.get(position).getName());
                    bundle.putString("userId", String.valueOf(userId));
                    bundle.putByteArray("scaled", getBytes(places.get(position).getPhoto()));
                    reviewListFragment.setArguments(bundle);
                    Log.d(TAG, "adding review");
                    if (getActivity().getSupportFragmentManager().findFragmentByTag(ReviewListFragment.TAG) == null) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                                .replace(R.id.mapFrameLayout, reviewListFragment, ReviewListFragment.TAG)
                                .commit();
                        Log.d(TAG, "adding was true");

                        //ft.replace(R.id.mapFrameLayout, addReviewFragment, AddReviewFragment.TAG);
                    }
                    Log.d(TAG, "end of transaction");
                }
                else if (userType.equals("Администратор")){

                }
            }
        });
        return rootView;

    }

    public static byte[] getBytes(Bitmap bitmap) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
//        return stream.toByteArray();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                80,
                bos);
        return bos.toByteArray();
    }


    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min((float) maxImageSize / realImage.getWidth(), (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }
}
