package com.example.cinemafound;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import static com.example.cinemafound.DBHelper.KEY_ID;
import static com.example.cinemafound.DBHelper.KEY_LOGIN;
import static com.example.cinemafound.DBHelper.KEY_PASSWORD;
import static com.example.cinemafound.DBHelper.KEY_USERTYPE;
import static com.example.cinemafound.DBHelper.TABLE_CONTACTS;

public class AuthorizationActivity extends AppCompatActivity {
    private AnimationDrawable animationDrawable;
    private ScrollView scrollView;

    Cursor cursor;
    DBHelper dbHelper;

    Boolean successAuth = false;
    private static final String TAG = "authorizationTag";
    EditText loginEdit;
    EditText passwordEdit;
    TextView registrationTextView;
    TextView textView;
    CardView cardView;
    ImageView logoImageView;
    TextView logoTextView;
    Animation fromtop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        cardView = (CardView) findViewById(R.id.cardView);
        loginEdit = (EditText) findViewById(R.id.loginEditText);
        passwordEdit = (EditText) findViewById(R.id.passwordEditText);
        registrationTextView = (TextView) findViewById(R.id.registrationTextView);
        //textView = (TextView) findViewById(R.id.textViewAuthorization);
        logoImageView = (ImageView) findViewById(R.id.authorizationLogo);
        logoTextView = (TextView) findViewById(R.id.logoTextView);
//        if(savedInstanceState == null) {
//            fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
//            if (logoImageView != null) logoImageView.setAnimation(fromtop);
//        }

        dbHelper = new DBHelper(this);
        if (logoTextView != null) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
            logoTextView.startAnimation(anim);
        }
    }

    public void onLoginEditTextClick(View v) {

    }

    //Переход к регистрации
    public void onGoToRegistrationClick(View v) {
        Bundle bundle = null;
        Activity activity = AuthorizationActivity.this;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //Animation anim = AnimationUtils.loadAnimation(this, R.anim.size_anim);
            //registrationTextView.startAnimation(anim);
            View view = activity.findViewById(R.id.authorizationLogo);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, view, "cinemaFoundLogo");
            bundle = options.toBundle();
        }
        Intent intent = new Intent(AuthorizationActivity.this, RegistrationActivity.class);
        if (bundle == null) {
            activity.startActivity(intent);
        } else {
            activity.startActivity(intent, bundle);
        }
    }

    //Авторизация
    public void onAuthorizationButtonClick(View v) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        cursor = database.rawQuery("select * from " + TABLE_CONTACTS, null);


        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int loginIndex = cursor.getColumnIndex(KEY_LOGIN);
            int passwordIndex = cursor.getColumnIndex(KEY_PASSWORD);
            int userTypeIndex = cursor.getColumnIndex(KEY_USERTYPE);
            do {
                if (cursor.getString(loginIndex).equals(loginEdit.getText().toString().replaceAll(" ", "").toLowerCase())
                        && cursor.getString(passwordIndex).equals(passwordEdit.getText().toString().replaceAll(" ", "").toLowerCase())) {
                    successAuth = true;
                    Intent intent = new Intent(AuthorizationActivity.this, MapActivity.class);
                    intent.putExtra("userType", cursor.getString(userTypeIndex));
                    Log.d(TAG, "userId: " + cursor.getInt(idIndex));
                    intent.putExtra("userId", String.valueOf(cursor.getInt(idIndex)));
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    break;
                } else successAuth = false;
                Log.d(TAG, "ID = " + cursor.getInt(idIndex) +
                        ", login = " + cursor.getString(loginIndex) +
                        ", password = " + cursor.getString(passwordIndex) +
                        ", user type = " + cursor.getString(userTypeIndex));
            } while (cursor.moveToNext());
            if (!successAuth)
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "0 rows");
            Toast.makeText(this, "Зарегистрированныых пользователей не найдено", Toast.LENGTH_SHORT).show();
        }
    }
}
