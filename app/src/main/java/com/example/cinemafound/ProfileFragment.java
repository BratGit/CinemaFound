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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.cinemafound.DBHelper.KEY_ID;
import static com.example.cinemafound.DBHelper.KEY_IMAGE;
import static com.example.cinemafound.DBHelper.KEY_LOGIN;
import static com.example.cinemafound.DBHelper.KEY_MARKER_REVIEW;
import static com.example.cinemafound.DBHelper.KEY_RATE;
import static com.example.cinemafound.DBHelper.KEY_REVIEW;
import static com.example.cinemafound.DBHelper.KEY_REVIEW_ID;
import static com.example.cinemafound.DBHelper.KEY_USER_REVIEW;
import static com.example.cinemafound.DBHelper.TABLE_CONTACTS;
import static com.example.cinemafound.DBHelper.TABLE_REVIEWS;

public class ProfileFragment extends Fragment {
    public static final String TAG = "profileFragmentTag";
    View rootView;
    CircleImageView profileImageView;
    TextView profileNameTextView;
    ListView profileReviewList;
    ReviewAdapter reviewAdapter;
    DBHelper dbHelper;
    SQLiteDatabase profileDataBase;
    Bitmap bitmap;
    Fragment userReviewFragment;
    Cursor cursor;
    String userName;
    int userId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_frame, container, false);
        Bundle bundle = getArguments();
        userId = Integer.parseInt(bundle.getString("userId"));
        profileImageView = (CircleImageView) rootView.findViewById(R.id.profileImage);
        profileNameTextView = (TextView) rootView.findViewById(R.id.userProfileName);
        profileReviewList = (ListView)rootView.findViewById(R.id.userProfileReviews);
        userReviewFragment = new UserReviewFragment();
        final List<Review> reviews = new ArrayList<Review>();
        reviewAdapter = new ReviewAdapter(getActivity(), reviews);
        profileReviewList.setAdapter(reviewAdapter);
        dbHelper = new DBHelper(getActivity());
        profileDataBase = dbHelper.getReadableDatabase();
        Cursor photoCursor = profileDataBase.rawQuery("select * from " + TABLE_CONTACTS + " where " + KEY_ID + " = " + userId, null);
        if (photoCursor.moveToFirst()){
            int userPhotoIndex = photoCursor.getColumnIndex(KEY_IMAGE);
            int idUserName = photoCursor.getColumnIndex(KEY_LOGIN);
            bitmap = scaleDown(getImage(photoCursor.getBlob(userPhotoIndex)), 800, false);
            userName = photoCursor.getString(idUserName);
            profileNameTextView.setText(userName);
            profileImageView.setImageBitmap(bitmap);
        }
        cursor = profileDataBase.rawQuery("select * from " + TABLE_REVIEWS + " where " + KEY_USER_REVIEW + " = " + userId, null);
        if (cursor.moveToFirst()){
            do {
                int idRate = cursor.getColumnIndex(KEY_RATE);
                int idReview = cursor.getColumnIndex(KEY_REVIEW);
                int idMarker = cursor.getColumnIndex(KEY_MARKER_REVIEW);
                int reviewIdIndex=  cursor.getColumnIndex(KEY_REVIEW_ID);
                reviews.add(new Review(userName, cursor.getString(idReview), cursor.getFloat(idRate),
                        bitmap, cursor.getInt(idMarker), userId, cursor.getInt(reviewIdIndex)));

            }while (cursor.moveToNext());
        }
        profileReviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity().getSupportFragmentManager().findFragmentByTag(UserReviewFragment.TAG) == null) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("markerId", String.valueOf(reviews.get(position).getMarkerId()));
                    bundle.putString("userId", String.valueOf(reviews.get(position).getUserId()));
                    bundle.putString("reviewId", String.valueOf(reviews.get(position).getReviewId()));
                    Log.d(TAG, "bundle values: markerId " + reviews.get(position).getMarkerId() + " userId " + reviews.get(position).getUserId() + " reviewId " + reviews.get(position).getReviewId());
                    userReviewFragment.setArguments(bundle);
                    fm
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                            .replace(R.id.mapFrameLayout, userReviewFragment, ReviewListFragment.TAG)
                            .commit();
                }
            }
        });
        return rootView;
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
