package com.example.cinemafound;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RegionIterator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.cinemafound.DBHelper.KEY_IMAGE;
import static com.example.cinemafound.DBHelper.KEY_LOGIN;
import static com.example.cinemafound.DBHelper.KEY_PASSWORD;
import static com.example.cinemafound.DBHelper.KEY_USERTYPE;
import static com.example.cinemafound.DBHelper.TABLE_CONTACTS;

public class RegistrationActivity extends AppCompatActivity {
    private AnimationDrawable animationDrawable;
    private ScrollView scrollView;

    private static final String TABLE_NAME = "contacts";
    DBHelper dbHelper;

    private static String TAG = "Compressing";
    EditText editLogin;
    TextInputLayout editLoginLayout;
    EditText editPassword;
    TextInputLayout editPasswordLayout;
    TextView textView;
    Button photoButton;
    Button authorizationButton;
    MaterialBetterSpinner spinnerUserType;
    ImageView userPhotoImageView;
    ImageView logoImageView;
    Animation fromtop;

    Boolean isUniqLogin = true;
    private boolean isImageScaled = false;

    String userType = "";
    String password;
    String login;

    static final int GALLERY_REQUEST = 1;
    boolean isGallery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        dbHelper = new DBHelper(this);
        photoButton = (Button)findViewById(R.id.photoButton);
        editPasswordLayout = (TextInputLayout)findViewById(R.id.registrationPasswordEditTextLayout);
        editLoginLayout = (TextInputLayout)findViewById(R.id.registrationLoginEditTextLayout);
        authorizationButton = (Button)findViewById(R.id.authorizationButton2);
        textView = (TextView)findViewById(R.id.textView4);
        editLogin = (EditText) findViewById(R.id.registrationLoginEditText);
        editPassword = (EditText) findViewById(R.id.registrationPasswordEditText);
        userPhotoImageView = (ImageView) findViewById(R.id.registrationUserPhotoImageView);
        logoImageView = (ImageView)findViewById(R.id.registrationLogoImageView);
        spinnerUserType = (MaterialBetterSpinner) findViewById(R.id.userTypeSpinner);
        final ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.userTypes));
        spinnerUserType.setAdapter(spinnerAdapter);
        spinnerUserType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userType = spinnerAdapter.getItem(position).toString();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrationActivity.this, AuthorizationActivity.class);
        Bundle bundle = null;
        Activity activity = RegistrationActivity.this;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //Animation anim = AnimationUtils.loadAnimation(this, R.anim.size_anim);
            //registrationTextView.startAnimation(anim);
            View view = activity.findViewById(R.id.registrationLogoImageView);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, view, "cinemaFoundLogo");
            bundle = options.toBundle();
        }
        if (bundle == null) {
            activity.startActivity(intent);
        } else {
            activity.startActivity(intent, bundle);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void onUserImageButtonClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void onRegistrationButtonClick(View v) {
        //Получение данных из EditText пароля и логина
        login = editLogin.getText().toString().replaceAll(" ", "").toLowerCase();
        password = editPassword.getText().toString().replaceAll(" ", "").toLowerCase();

        //Алгоритм проверки на уникальность логина
        SQLiteDatabase databaseLoginCheck = dbHelper.getReadableDatabase();
        Cursor cursor = databaseLoginCheck.rawQuery("select * from " + TABLE_CONTACTS, null);
        if (cursor.moveToFirst()) {
            int loginIndex = cursor.getColumnIndex(KEY_LOGIN);
            do {
                if (login.toLowerCase().equals(cursor.getString(loginIndex).toLowerCase()))
                    isUniqLogin = false;
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        //Проверка на присутствие изображения
        Bitmap bitmap = null;
        if (userPhotoImageView.getDrawable() != null) {
            bitmap = ((BitmapDrawable) userPhotoImageView.getDrawable()).getBitmap();
        }



        //Проверка на заполненность полей
        if (!login.equals("") && !password.equals("") && !userType.equals("") && bitmap != null) {
            //Проверка на длину пароля и логина
            if (login.length() >= 5 && password.length() >= 5) {
                //Проверка на уникальность логин
                if (isUniqLogin) {
                    //Запись данных в SQLite
                    Intent intent = new Intent(RegistrationActivity.this, AuthorizationActivity.class);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(KEY_LOGIN, login);
                    contentValues.put(KEY_PASSWORD, password);
                    contentValues.put(KEY_USERTYPE, userType);
                    contentValues.put(KEY_IMAGE, getBytes(bitmap));

                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    database.insert(TABLE_NAME, null, contentValues);
                    Log.d("insertData", "login = " + login + " password = " + password + getBytes(bitmap).length);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Пользователь с таким логином уже существует", Toast.LENGTH_LONG).show();
                    isUniqLogin = true;
                }
            } else
                Toast.makeText(RegistrationActivity.this, "Логин и пароль должны быть не менее 5 символов", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(RegistrationActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();

    }

    //Загрузка изображения в ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageView imageView = (ImageView) findViewById(R.id.registrationUserPhotoImageView);

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
                        imageView = (ImageView) findViewById(R.id.registrationUserPhotoImageView);
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    //Конвертация изображения в байты
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        return stream.toByteArray();
    }
}
