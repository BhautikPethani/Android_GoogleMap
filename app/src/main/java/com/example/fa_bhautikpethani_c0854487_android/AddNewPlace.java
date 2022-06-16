package com.example.fa_bhautikpethani_c0854487_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddNewPlace extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;

    LocationManager locationManager;
    LocationListener locationListener;

    Location currentLocation;

    LatLng searchedLocation;
    private Marker searchedPlace;

    EditText txtSearchPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtSearchPlace = findViewById(R.id.txtMapSearch);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                startUpdateLocation();
//                if(currentLocation != null){
//                    homeMarker.remove();
//                    LatLng northAmerica = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                    setHomeMarker(northAmerica);
//                }
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
            if(currentLocation != null){
                LatLng northAmerica = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                setHomeMarker(northAmerica);
            }
        }
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

                if(searchedPlace != null) searchedPlace.remove();

                searchedLocation = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions options = new MarkerOptions().position(searchedLocation)
                        .title(placeName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                searchedPlace = mMap.addMarker(options);
                searchedPlace.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 14));
            }else{
                Toast.makeText(this, "Address can't be find.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}