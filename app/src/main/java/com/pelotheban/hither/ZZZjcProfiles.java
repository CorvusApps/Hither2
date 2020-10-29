package com.pelotheban.hither;

public class ZZZjcProfiles {



    private String profilename;
    private double latitude;
    private double longitude;


    public ZZZjcProfiles(String profilename, double latitude, double longitude) {
        this.profilename = profilename;
        this.latitude = latitude;
        this.longitude = longitude;



    }

    public String getProfilename() {
        return profilename;

    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }


    public double getLatitude() {

        return latitude;
    }

    public void setLatitude (double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;

    }


    public ZZZjcProfiles(){

    }

}
