package com.pelotheban.hither;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class Profile extends AppCompatActivity implements View.OnFocusChangeListener {

    private ImageView imgProfileImageX;
    private EditText edtNameX, edtHitherX;
    private Bitmap profileImageBitmap;

    private TextView txtNameX, txtHitherX;
    private FloatingActionButton fabEditX, fabSaveX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfileImageX = findViewById(R.id.imgProfileImage);

        edtNameX = findViewById(R.id.edtName);
        edtNameX.setOnFocusChangeListener(this);
        edtNameX.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtNameX.setRawInputType(InputType.TYPE_CLASS_TEXT);

        edtHitherX = findViewById(R.id.edtHither);
        edtNameX.setOnFocusChangeListener(this);
        edtNameX.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtNameX.setRawInputType(InputType.TYPE_CLASS_TEXT);

        txtNameX = findViewById(R.id.txtNameB);
        txtHitherX = findViewById(R.id.txtHitherB);

        fabEditX = findViewById(R.id.fabEdit);
        fabEditX.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                fabEditX.setVisibility(View.GONE);
                fabSaveX.setVisibility(View.VISIBLE);
                txtNameX.setVisibility(View.GONE);
                txtHitherX.setVisibility(View.GONE);
                edtNameX.setVisibility(View.VISIBLE);
                edtHitherX.setVisibility(View.VISIBLE);
                Log.i("IFTEST" , "in edit click");

            }
        });

        fabSaveX = findViewById(R.id.fabSave);
        fabSaveX.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                fabEditX.setVisibility(View.VISIBLE);
                fabSaveX.setVisibility(View.GONE);
                txtNameX.setVisibility(View.VISIBLE);
                txtHitherX.setVisibility(View.VISIBLE);
                edtNameX.setVisibility(View.GONE);
                edtHitherX.setVisibility(View.GONE);
            }
        });

        imgProfileImageX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("IFTEST" , "in image click");

                if(ActivityCompat.checkSelfPermission(Profile.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]
                                    {Manifest.permission.READ_EXTERNAL_STORAGE},
                            1000);
                }else {

                    getChosenImage();

                }

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000) {

            if(grantResults.length >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){

                getChosenImage();

            }
        }
    }

    private void getChosenImage() {

        // gets image from internal storage - GALLERY
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2000);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2000) {

            if (resultCode == Activity.RESULT_OK) {

                //Do something with captured image

                try {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex); // path to the Bitmap created from pulled image
                    cursor.close();
                    profileImageBitmap = BitmapFactory.decodeFile(picturePath);

                    // getting to the rotation wierdness from large files using the picturePath to id the file
                    int degree = 0;
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(picturePath);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (exif != null) {
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                        if (orientation != -1) {
                            // We only recognise a subset of orientation tag values.
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    degree = 90;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    degree = 180;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    degree = 270;
                                    break;
                            }

                        }
                    }

                    // resizing the image to a standard size that is easy on the storage
                    profileImageBitmap = Bitmap.createScaledBitmap(profileImageBitmap, 400, 400, true);

                    // correcting the rotation on the resized file using the degree variable of how much to fix we got above
                    Bitmap bitmap = profileImageBitmap;
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


                    imgProfileImageX.setImageBitmap(bitmap);

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }

        }
    }


    // add this later - UI methods to make things nice when the keyboard is up
    @Override
    public void onFocusChange(View view, boolean hasFocus) {

    }

    //onClick set up in XML; gets rid of keyboard when background tapped
    public void loginLayoutTapped (View view) {

        try { // we need this because if you tap with no keyboard up the app will crash

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}