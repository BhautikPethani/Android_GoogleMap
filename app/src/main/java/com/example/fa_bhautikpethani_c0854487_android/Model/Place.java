package com.example.fa_bhautikpethani_c0854487_android.Model;

public class Place {
    private int id;
    private String placeAddress;
    private double latitude;
    private double longitude;
    private int status;

    public Place(int id, String placeAddress, double latitude, double longitude, int status){
        this.id = id;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public int getId(){return id;}

    public void setPlaceAddress(String address){this.placeAddress = address;}
    public String getPlaceAddress(){return placeAddress;}

    public void setLatitude(Double latitude){this.latitude = latitude;}
    public double getLatitude() {return latitude;}

    public void setLongitude(Double longitude){this.longitude = longitude;}
    public double getLongitude(){return longitude;}

    public void setStatus(int status){this.status = status;}
    public int getStatus() {return status;}
}
