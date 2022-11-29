package com.example.cinemafound;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.cinemafound.DBHelper.KEY_ID;
import static com.example.cinemafound.DBHelper.KEY_IMAGE;
import static com.example.cinemafound.DBHelper.KEY_LOGIN;
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
import static com.example.cinemafound.DBHelper.TABLE_CONTACTS;
import static com.example.cinemafound.DBHelper.TABLE_PHOTOS;
import static com.example.cinemafound.DBHelper.TABLE_REVIEWS;

public class UserReviewFragment extends Fragment {
    public static final String TAG = "userReviewFragment";
    View rootView;
    CircleImageView reviewUserPhoto;
    TextView userReviewName;
    TextView reviewRate;
    ViewPager reviewPhotos;
    ImageAdapter adapter;
    DBHelper dbHelper;
    SQLiteDatabase reviewDateBase;
    Button closeUserReviewButton;
    TextView userReviewText;
    Cursor cursor;

    int markerId;
    int userId;
    int reviewId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_reveiw_frame, null);
        closeUserReviewButton = (Button)rootView.findViewById(R.id.closeUserReviewButton);
        Bundle bundle = getArguments();
        userReviewText = (TextView)rootView.findViewById(R.id.userReviewText);
        markerId = Integer.valueOf(bundle.getString("markerId"));
        userId = Integer.valueOf(bundle.getString("userId"));
        reviewId = Integer.valueOf(bundle.getString("reviewId"));
        adapter = new ImageAdapter(getActivity());
        reviewUserPhoto = (CircleImageView) rootView.findViewById(R.id.reviewUserPhoto);
        userReviewName = (TextView)rootView.findViewById(R.id.userReviewName);
        reviewRate = (TextView) rootView.findViewById(R.id.reviewRate);
        reviewPhotos = (ViewPager)rootView.findViewById(R.id.reviewPhotos);
        reviewPhotos.setAdapter(adapter);

        dbHelper = new DBHelper(getActivity());
        reviewDateBase = dbHelper.getReadableDatabase();

        Cursor photoCursor = reviewDateBase.rawQuery("select * from " + TABLE_CONTACTS + " where " + KEY_ID + " = " + userId, null);
        if (photoCursor.moveToFirst()){
            int photoIndex = photoCursor.getColumnIndex(KEY_IMAGE);
            int loginIndex = photoCursor.getColumnIndex(KEY_LOGIN);
            reviewUserPhoto.setImageBitmap(scaleDown(getImage(photoCursor.getBlob(photoIndex)), 800, false));
            userReviewName.setText(photoCursor.getString(loginIndex));
        }
        Cursor rateCursor = reviewDateBase.rawQuery("select * from " + TABLE_REVIEWS + " where "
                + KEY_USER_REVIEW + " = " + userId + " and " + KEY_REVIEW_ID + " = " + reviewId, null);
        if (rateCursor.moveToFirst()){
            int rateIndex = rateCursor.getColumnIndex(KEY_RATE);
            int reviewIndex = rateCursor.getColumnIndex(KEY_REVIEW);
            reviewRate.setText(String.valueOf(rateCursor.getFloat(rateIndex)));
            userReviewText.setText(rateCursor.getString(reviewIndex));
        }
        Log.d(TAG, "userId: " + userId + " markerId: " + markerId + " reviewId: " + reviewId);
        cursor = reviewDateBase.rawQuery("select * from " + TABLE_PHOTOS + " where " + KEY_PHOTO_USER_ID
                + " = " + userId + " and " + KEY_PHOTO_MARKER_ID + " = " + markerId + " and " + KEY_PHOTO_REVIEW_ID + " = " + reviewId, null);
        //cursor = reviewDateBase.rawQuery("select * from " + TABLE_PHOTOS, null);
        if (cursor.moveToFirst()){
            do{
                int idReview = cursor.getColumnIndex(KEY_PHOTO_REVIEW_ID);
                int idMarker = cursor.getColumnIndex(KEY_PHOTO_MARKER_ID);
                int idUser = cursor.getColumnIndex(KEY_PHOTO_USER_ID);
                Log.d(TAG, "adding photos userId: " + cursor.getInt(idUser) + " markerId: " + cursor.getInt(idMarker) + " reviewId: " + cursor.getInt(idReview));
                int photoIndex = cursor.getColumnIndex(KEY_PHOTO);
                setImagesInPager(scaleDown(getImage(cursor.getBlob(photoIndex)), 800, false));
//                adapter.images.add(scaleDown(getImage(photoCursor.getBlob(photoIndex)), 800, false));
//                reviewPhotos.setAdapter(adapter);

            }while (cursor.moveToNext());
        }
        closeUserReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                        .remove(UserReviewFragment.this)
                        .commit();
            }
        });
        return rootView;
    }

    public void setImagesInPager(Bitmap b) {
        adapter.images.add(b);
        reviewPhotos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "images = " + adapter.images.size());
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min((float) maxImageSize / realImage.getWidth(), (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }
}
