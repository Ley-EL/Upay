package com.example.upay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class WishList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        // get toolbar id
        Toolbar toolbar = findViewById(R.id.toolbar);

        // set toolbar
        setSupportActionBar(toolbar);

        // set toolbar title
        getSupportActionBar().setTitle("Wishlist");

        // add back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}