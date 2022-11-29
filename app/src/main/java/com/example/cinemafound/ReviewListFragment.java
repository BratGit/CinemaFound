package com.example.cinemafound;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.cinemafound.DBHelper.KEY_ID;
import static com.example.cinemafound.DBHelper.KEY_IMAGE;
import static com.example.cinemafound.DBHelper.KEY_LOGIN;
import static com.example.cinemafound.DBHelper.KEY_MARKER_REVIEW;
import static com.example.cinemafound.DBHelper.KEY_PHOTO_REVIEW_ID;
import static com.example.cinemafound.DBHelper.KEY_RATE;
import static com.example.cinemafound.DBHelper.KEY_REVIEW;
import static com.example.cinemafound.DBHelper.KEY_REVIEW_ID;
import static com.example.cinemafound.DBHelper.KEY_USER_REVIEW;
import static com.example.cinemafound.DBHelper.TABLE_CONTACTS;
import static com.example.cinemafound.DBHelper.TABLE_MARKERS;
import static com.example.cinemafound.DBHelper.TABLE_REVIEWS;

public class ReviewListFragment extends Fragment {
    public static final String TAG = "reviewListFragmentTag";
    Bundle bundle;

    View rootView;
    ListView reviewsListView;
    DBHelper dbHelper;
    Fragment addReviewFragment;
    com.melnykov.fab.FloatingActionButton addReviewButton;
    com.melnykov.fab.FloatingActionButton closeReviewListFrame;
    String markerName;
    Bitmap bitmap;
    TextView reviewsPlaceName;
    ImageView reviewsImageView;
    Fragment userReviewFragment;
    Cursor cursor;
    int markerId;
    int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.reviews_frame, null);
        reviewsImageView = (ImageView) rootView.findViewById(R.id.reviewsImageView);
        reviewsPlaceName = (TextView) rootView.findViewById(R.id.reviewsPlaceName);
        userReviewFragment = new UserReviewFragment();
        bundle = this.getArguments();
        markerId = bundle.getInt("markerId", 0);
        userId = Integer.parseInt(bundle.getString("userId"));
        markerName = bundle.getString("markerName");
        bitmap = scaleDown(getImage(bundle.getByteArray("scaled")), 800, false);
        reviewsImageView.setImageBitmap(bitmap);
        reviewsPlaceName.setText(markerName);
        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase databaseMarkerAdd = dbHelper.getReadableDatabase();
        final List<Review> reviews = new ArrayList<Review>();
        final ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), reviews);
        Activity activity = getActivity();
        final View view = activity.findViewById(R.id.reviewsImageView);
        reviewsListView = (ListView) rootView.findViewById(R.id.reviewsListView);
        addReviewButton = (com.melnykov.fab.FloatingActionButton) rootView.findViewById(R.id.addReviewFromList);
        closeReviewListFrame = (com.melnykov.fab.FloatingActionButton) rootView.findViewById(R.id.closeReviewListFrame);
        closeReviewListFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
//                        .addSharedElement(reviewsImageView, "placePhotoTransition")
//                        .setReorderingAllowed(true)
//                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                        .remove(ReviewListFragment.this)
                        .commit();
                Log.d(TAG, "fragment closed");
            }
        });


        addReviewFragment = new AddReviewFragment();
        reviewsListView.setAdapter(reviewAdapter);
        Log.d(TAG, "markerId" + markerId);
        cursor = databaseMarkerAdd.rawQuery("select * from " + TABLE_REVIEWS + " where " + KEY_MARKER_REVIEW + " = " + markerId, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "first loop");
                int idUser = cursor.getColumnIndex(KEY_USER_REVIEW);
                int idRate = cursor.getColumnIndex(KEY_RATE);
                int idReview = cursor.getColumnIndex(KEY_REVIEW);
                int reviewIdIndex=  cursor.getColumnIndex(KEY_REVIEW_ID);
                Cursor photoCursor = databaseMarkerAdd.rawQuery("select * from " + TABLE_CONTACTS, null);
                if (photoCursor.moveToFirst()) {
                    Log.d(TAG, "second loop");
                    //int idUserContact = photoCursor.getColumnIndex(KEY_ID);
                    int idUserReview = photoCursor.getColumnIndex(KEY_ID);
                    int idPhoto = photoCursor.getColumnIndex(KEY_IMAGE);
                    int idUserName = photoCursor.getColumnIndex(KEY_LOGIN);

                    do {
                        Log.d(TAG, "userId = " + photoCursor.getInt(idUserReview) + " userReviewId = " + cursor.getInt(idUser));
                        Bitmap bitmap = getImage(photoCursor.getBlob(idPhoto));
                        Bitmap scaled;
                        scaled = scaleDown(bitmap, 800, false);
                        if (cursor.getInt(idUser) == photoCursor.getInt(idUserReview)) {
                            reviews.add(new Review(photoCursor.getString(idUserName), cursor.getString(idReview), cursor.getFloat(idRate),
                                    scaled, markerId, cursor.getInt(idUser), cursor.getInt(reviewIdIndex)));
                            Log.d(TAG, "database values: userName: " + photoCursor.getString(idUserName) + " Review: " + cursor.getString(idReview)
                                    + " Rating: " + cursor.getFloat(idRate) + (scaled == null ? " scaled" : " not scaled"));
                        }

                    } while (photoCursor.moveToNext());
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "adding review");
                if (getActivity().getSupportFragmentManager().findFragmentByTag(AddReviewFragment.TAG) == null) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slie_in_right)
                            .replace(R.id.mapFrameLayout, addReviewFragment, AddReviewFragment.TAG)
                            .commit();
                    Log.d(TAG, "adding was true");
                    Bundle addReviewBundle = new Bundle();
                    addReviewBundle.putString("userId", String.valueOf(userId));
                    addReviewBundle.putInt("markerId", markerId);
                    addReviewFragment.setArguments(addReviewBundle);
                    //ft.replace(R.id.mapFrameLayout, addReviewFragment, AddReviewFragment.TAG);
                }
                Log.d(TAG, "end of transaction");
            }
        });
        reviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        addReviewButton.attachToListView(reviewsListView);
        closeReviewListFrame.attachToListView(reviewsListView);
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
