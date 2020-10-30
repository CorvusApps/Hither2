package com.pelotheban.hither;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

public class Profile extends AppCompatActivity implements View.OnFocusChangeListener {

    private ImageView imgProfileImageX;
    private EditText edtNameX, edtHitherX;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        imgProfileImageX = findViewById(R.id.imgProfileImage);

        edtNameX = findViewById(R.id.edtName);
        edtNameX.setOnFocusChangeListener(this);
        edtNameX.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtNameX.setRawInputType(InputType.TYPE_CLASS_TEXT);

        edtHitherX = findViewById(R.id.edtHither);
        edtNameX.setOnFocusChangeListener(this);
        edtNameX.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtNameX.setRawInputType(InputType.TYPE_CLASS_TEXT);


    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {

    }
}