package com.example.cinemafound;

import android.graphics.Bitmap;

public class Review {
    public String UserName;
    public String Review;
    public float Rate;
    public Bitmap UserPhoto;
    public int MarkerId;
    public int UserId;
    public int ReviewId;

    public Review(String userName, String review, float rate, Bitmap userPhoto, int markerId, int userId, int reviewId) {
        UserName = userName;
        Review = review;
        Rate = rate;
        UserPhoto = userPhoto;
        MarkerId = markerId;
        UserId = userId;
        ReviewId = reviewId;
    }

    public int getReviewId() {
        return ReviewId;
    }

    public void setReviewId(int reviewId) {
        ReviewId = reviewId;
    }

    public int getMarkerId() {
        return MarkerId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setMarkerId(int markerId) {
        MarkerId = markerId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public Bitmap getUserPhoto() {
        return UserPhoto;
    }

    public float getRate() {
        return Rate;
    }

    public String getReview() {
        return Review;
    }

    public String getUserName() {
        return UserName;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public void setReview(String review) {
        Review = review;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserPhoto(Bitmap userPhoto) {
        UserPhoto = userPhoto;
    }
}
