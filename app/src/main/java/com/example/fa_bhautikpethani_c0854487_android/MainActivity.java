package com.example.fa_bhautikpethani_c0854487_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    public void goToAddNewPlace(View view) {
        startActivity(new Intent(this, AddNewPlace.class));
    }

    public void goBack(View view) {
        finish();
    }

    public void searchPlace(View view) {
        
    }
}