package com.example.fa_bhautikpethani_c0854487_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fa_bhautikpethani_c0854487_android.Model.Place;
import com.example.fa_bhautikpethani_c0854487_android.services.DBHelper;
import com.example.fa_bhautikpethani_c0854487_android.services.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdatePlace extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;

    LocationManager locationManager;
    LocationListener locationListener;

    Location currentLocation;

    LatLng searchedLocation;
    private Marker searchedPlaceMarker;

    EditText txtSearchPlace;
    String placeTitle = "";

    Place destination;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_place);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtSearchPlace = findViewById(R.id.txtUpdateMapSearch);
        dbHelper = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("placeId");
            destination = dbHelper.getPlace(value);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else {
            startUpdateLocation();
            LatLng northAmerica = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            setHomeMarker(northAmerica);

            setSearchedPlaceMarker(new LatLng(destination.getLatitude(), destination.getLongitude()), destination.getPlaceAddress());
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                String address = getAddressByLocation(latLng);
                setSearchedPlaceMarker(latLng, address);

            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                searchedLocation = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
            }
        });
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(LatLng currentLocation) {
        LatLng userLocation = currentLocation;
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You're here.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        homeMarker = mMap.addMarker(options);
        homeMarker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
    }

    private void setSearchedPlaceMarker(LatLng location, String placeName) {
        if(searchedPlaceMarker != null) searchedPlaceMarker.remove();

        searchedLocation = location;
        placeTitle = placeName;
        MarkerOptions options = new MarkerOptions().position(location)
                .title(placeName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).draggable(true);
        searchedPlaceMarker = mMap.addMarker(options);
        searchedPlaceMarker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);

                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
    }

    public String getAddressByLocation(LatLng location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getLocality() != null)
                    return addressList.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Not Found";
    }

    public void searchPlace(View view) {
        String placeName = txtSearchPlace.getText().toString().trim();

        if (placeName.isEmpty()) {
            txtSearchPlace.setError("Please type something.");
            txtSearchPlace.requestFocus();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(placeName, 1);

            if(addressList.size()>0){
                Address address = addressList.get(0);
                searchedLocation = new LatLng(address.getLatitude(), address.getLongitude());
                setSearchedPlaceMarker(searchedLocation, placeName);
                Utilities.hideKeyboard(this);
            }else{
                Toast.makeText(this, "Address can't be find.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBack(View view) {
        Intent intent=new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
    }

    public void addPlaceToDB(View view) {
        if(searchedLocation != null){
            if(placeTitle.equals("")){
                Date date = new Date();
                SimpleDateFormat simpleFormat = new SimpleDateFormat("MMMM dd, yyyy");
                placeTitle = simpleFormat.format(date.getTime());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set "+ placeTitle+" as visited or not visited");
            builder.setPositiveButton("Visited", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Place place = new Place(destination.getId(), placeTitle, searchedLocation.latitude, searchedLocation.longitude, 1);
                    if(dbHelper.updatePlace(place)){
                        Toast.makeText(getApplicationContext(), placeTitle + " has been updated to place list", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), placeTitle + " couldn't add", Toast.LENGTH_SHORT).show();
                    };
                }
            });
            builder.setNegativeButton("Not Visited", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Place place = new Place( destination.getId(), placeTitle, searchedLocation.latitude, searchedLocation.longitude, 0);
                    if(dbHelper.updatePlace(place)){
                        Toast.makeText(getApplicationContext(), placeTitle + " has been updated to place list", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), placeTitle + " couldn't add", Toast.LENGTH_SHORT).show();
                    };
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}