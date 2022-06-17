package com.example.fa_bhautikpethani_c0854487_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.fa_bhautikpethani_c0854487_android.Model.Place;
import com.example.fa_bhautikpethani_c0854487_android.services.DBHelper;
import com.example.fa_bhautikpethani_c0854487_android.services.Utilities;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lvVisited, lvNotVisited;
    EditText txtSearch;

    List<Place> visitedPlace;
    List<Place> notVisitedPlace;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        dbHelper = new DBHelper(this);
        lvNotVisited = findViewById(R.id.lvNotVisited);
        lvVisited = findViewById(R.id.lvVisited);
        txtSearch = findViewById(R.id.txtSearch);
        loadPlaces();

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                visitedPlace = dbHelper.searchPlace(s.toString(),1);
                notVisitedPlace = dbHelper.searchPlace(s.toString(),0);
                lvNotVisited.setAdapter(new PlaceListAdapter(getApplicationContext(), notVisitedPlace));
                lvVisited.setAdapter(new PlaceListAdapter(getApplicationContext(), visitedPlace));
                Utilities.getListViewSize(lvNotVisited);
                Utilities.getListViewSize(lvVisited);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadPlaces(){
        visitedPlace = dbHelper.getAllPlaces(1);
        notVisitedPlace = dbHelper.getAllPlaces(0);
        lvNotVisited.setAdapter(new PlaceListAdapter(this, notVisitedPlace));
        lvVisited.setAdapter(new PlaceListAdapter(this, visitedPlace));
        Utilities.getListViewSize(lvNotVisited);
        Utilities.getListViewSize(lvVisited);
    }

    public void goToAddNewPlace(View view) {
        startActivity(new Intent(this, AddNewPlace.class));
    }

}