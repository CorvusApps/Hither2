package com.pelotheban.hither;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuPage extends AppCompatActivity {

    ImageView btnHitherX, btnHotspotX, btnProfileX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnHitherX = findViewById(R.id.btnHither);
        btnHotspotX = findViewById(R.id.btnHotspot);
        btnProfileX = findViewById(R.id.btnProfile);

        btnHitherX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuPage.this, HomePage.class);
                startActivity(intent);

            }
        });

        btnHotspotX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuPage.this, Hotspot.class);
                startActivity(intent);

            }
        });

        btnProfileX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuPage.this, Profile.class);
                startActivity(intent);

            }
        });




    }
}