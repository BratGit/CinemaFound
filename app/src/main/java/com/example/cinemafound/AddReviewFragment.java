package com.example.cinemafound;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.melnykov.fab.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.example.cinemafound.DBHelper.KEY_LAT;
import static com.example.cinemafound.DBHelper.KEY_LNG;
import static com.example.cinemafound.DBHelper.KEY_MARKER_NAME;
import static com.example.cinemafound.DBHelper.KEY_MARKER_REVIEW;
import static com.example.cinemafound.DBHelper.KEY_PHOTO;
import static com.example.cinemafound.DBHelper.KEY_PHOTO_ID;
import static com.example.cinemafound.DBHelper.KEY_PHOTO_MARKER_ID;
import static com.example.cinemafound.DBHelper.KEY_PHOTO_REVIEW_ID;
import static com.example.cinemafound.DBHelper.KEY_PHOTO_USER_ID;
import static com.example.cinemafound.DBHelper.KEY_RATE;
import static com.example.cinemafound.DBHelper.KEY_REVIEW;
import static com.example.cinemafound.DBHelper.KEY_REVIEW_ID;
import static com.example.cinemafound.DBHelper.KEY_USER_REVIEW;
import static com.example.cinemafound.DBHelper.TABLE_INQUIRIES;
import static com.example.cinemafound.DBHelper.TABLE_MARKERS;
import static com.example.cinemafound.DBHelper.TABLE_PHOTOS;
import static com.example.cinemafound.DBHelper.TABLE_REVIEWS;

public class AddReviewFragment extends Fragment {
    static final int GALLERY_REQUEST = 1;
    boolean isGallery;
    private View rootView;
    DBHelper dbHelper;
    Cursor cursor;
    RatingBar rate;
    EditText reviewText;
    Bundle bundle;
    ViewPager viewPager;
    Button addPhotoReview;
    Button deletePhotoButton;
    FloatingActionButton publicatePhotoButton;
    FloatingActionButton closeReviewFrame;
    ImageAdapter adapter;
    int reviewId;
    public static final String TAG = "addReviewFragment";
    int markerId;
    int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new ImageAdapter(getActivity());
        bundle = this.getArguments();
        markerId = bundle.getInt("markerId", 0);
        userId = Integer.parseInt(bundle.getString("userId"));
        rootView = inflater.inflate(R.layout.add_review_frame, null);
        rate = rootView.findViewById(R.id.reviewRating);
        reviewText = rootView.findViewById(R.id.reviewText);
        viewPager = rootView.findViewById(R.id.addReviewImages);
        addPhotoReview = rootView.findViewById(R.id.addPhotoReview);
        deletePhotoButton = rootView.findViewById(R.id.deletePhotoButton);
        publicatePhotoButton = rootView.findViewById(R.id.publicateButton);
        closeReviewFrame = rootView.findViewById(R.id.closeReviewFrame);
        dbHelper = new DBHelper(getActivity());
        final SQLiteDatabase databaseReviewAdd = dbHelper.getWritableDatabase();
        final SQLiteDatabase readableDatabaseReviewAdd = dbHelper.getReadableDatabase();
        addPhotoReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Откуда взять фото?")
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
        });
        deletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.images.size() > 0) {
                    adapter.images.remove(viewPager.getCurrentItem());
                    adapter.notifyDataSetChanged();
                    viewPager.setAdapter(adapter);
                    Log.d(TAG, "images = " + adapter.images.size());
                }
            }
        });
        publicatePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ContentValues contentValues = new ContentValues();
                    //MapActivity mapActivity = new MapActivity();
                    int userid = Integer.parseInt(getActivity().getIntent().getStringExtra("userId"));
                    Log.d(TAG, "userId: " + userid);
                    contentValues.put(KEY_USER_REVIEW, userid);
                    Log.d(TAG, "markerID: " + markerId);
                    contentValues.put(KEY_MARKER_REVIEW, markerId);
                    contentValues.put(KEY_REVIEW, reviewText.getText().toString());
                    contentValues.put(KEY_RATE, rate.getRating());
                    databaseReviewAdd.insert(TABLE_REVIEWS, null, contentValues);
                    Log.d(TAG, "review added");

                    cursor = readableDatabaseReviewAdd.rawQuery("select * from " + TABLE_REVIEWS, null);
                    if (cursor.moveToFirst()) {
                        do {
                            int idIndex = cursor.getColumnIndex(KEY_REVIEW_ID);
                            reviewId = cursor.getInt(idIndex);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    ContentValues photoValues = new ContentValues();
                    if (adapter.images.size() != 0) {
                        for (int i = 0; i < adapter.images.size(); i++) {
                            photoValues.put(KEY_PHOTO_REVIEW_ID, reviewId);
                            Log.d(TAG, "select from table photos: userId: " + userId + " markerId: " + markerId + " reviewId: " + reviewId);
                            photoValues.put(KEY_PHOTO_USER_ID, userId);
                            photoValues.put(KEY_PHOTO_MARKER_ID, markerId);
                            photoValues.put(KEY_PHOTO, getBytes(adapter.images.get(i)));
                            Log.d(TAG, "photo length" + getBytes(adapter.images.get(i)).length);
                            databaseReviewAdd.insert(TABLE_PHOTOS, null, photoValues);
                        }
                    }
                    Cursor photoSelect = readableDatabaseReviewAdd.rawQuery("select * from " + TABLE_PHOTOS, null);
                    if (photoSelect.moveToFirst()){
                        do{
                            Log.d(TAG, "photoId: " + photoSelect.getInt(photoSelect.getColumnIndex(KEY_PHOTO_ID))
                                    + " markerId: " + photoSelect.getInt(photoSelect.getColumnIndex(KEY_PHOTO_MARKER_ID))
                            + "userId: " + photoSelect.getInt(photoSelect.getColumnIndex(KEY_PHOTO_USER_ID)));
                        }while(photoSelect.moveToNext());
                    }
                    Log.d(TAG, "photos added");
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                            .remove(AddReviewFragment.this)
                            .commit();
                    Log.d(TAG, "fragment closed");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        closeReviewFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                        .remove(AddReviewFragment.this)
                        .commit();
                Log.d(TAG, "fragment closed");
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

    public void setImagesInPager(Bitmap b) {
        adapter.images.add(b);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "images = " + adapter.images.size());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = null;
        switch (requestCode) {
            //Загрузка изображения из галереи
            case GALLERY_REQUEST: {
                if (resultCode == RESULT_OK) {
                    if (isGallery) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                            setImagesInPager(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //Получение изображения с камеры устройства
                    else {
                        bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        setImagesInPager(bitmap);
                    }
                }
            }
        }
    }
}
