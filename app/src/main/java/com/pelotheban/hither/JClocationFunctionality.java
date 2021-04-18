package com.pelotheban.hither;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JClocationFunctionality extends AppCompatActivity {

//    private MediaPlayer player;
//
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private LocationRequest locationRequest; // this and callback needed for when location is null because no app generated a last location
//    private LocationCallback locationCallback;
//
//
//    private DatabaseReference homePageRef, userHomePageRef;
//    private String userID;
//    private FirebaseAuth hpAuth;
//
//    private double lat, longit;
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//       // playerFunction();
//        Log.i("Classes", "Functionality on Start");
//
//        //////////////////// LOCATION CALLBACK /////////////////////////////////
//
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(4000);
//        locationRequest.setFastestInterval(2000);
//
//        locationCallback = new LocationCallback() {
//
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//
//                Log.i("LocFun", " in location callback");
//
//                if (locationResult != null) {
//
//                    Log.i("LocFun", " last location success 3");
//
//                    for (Location location: locationResult.getLocations()) {
//
//                        lat = location.getLatitude();
//                        longit = location.getLongitude();
//
//                        //txtUserLocationX.setText("Lat: " + lat + "  Long: " + longit);
//                        userHomePageRef = FirebaseDatabase.getInstance().getReference().child("my_users").child(userID);
//                        //homePageRef.getRef().child("lastlocation").setValue(location); // this just created a last location not linked to a user
//                        userHomePageRef.getRef().child("lastlocation").setValue(location);
//
//                       // calculateDistance();
//
//                    }
//
//
//                } else {
//
//                    Log.i("LocFun", "location result null ");
//
//                    //TBD what to do with this
//                }
//
//
//
//            }
//        };
//
//
//        ////////////////// end location callback ////////////////////////////////////
//
//        //////////////////// FIREBASE BASICS /////////////////////////////////////
//
//        userID = FirebaseAuth.getInstance().getUid();
//        homePageRef = FirebaseDatabase.getInstance().getReference().child("my_users");
//
//        hpAuth = FirebaseAuth.getInstance();
//        homePageRef.keepSynced(true);
//
//        ///////////////////////////////////////////////////////////////////////////////////
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        firstLocationFunction();
//
//    }
//
//    public void firstLocationFunction(){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                Log.i("LocFun", "Already has peremission 1");
//
//
//
//                checkSettingsAndStartLocationUpdatesjc();
//
//
//            } else {
//
//                Log.i("LocFun", "need permission");
//                // go to request permission which applies to last location only
//
//                askLocationPermissionX();
//
//
//            }
//
//        } else {
//            // this else means earlier veresion of Android no need to check permissions
//            checkSettingsAndStartLocationUpdatesjc();
//
//        }
//
//
//
//
//    }
//
//    private void checkSettingsAndStartLocationUpdatesjc() {
//
//        Log.i("LocFun", "in check settings");
//
//        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
//        SettingsClient client= LocationServices.getSettingsClient(this);
//
//        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
//        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//
//                startLocationUpdatesjc();
//
//            }
//        });
//
//        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                if (e instanceof ResolvableApiException) {
//
//                    ResolvableApiException apiException = (ResolvableApiException) e;
//                    try {
//                        apiException.startResolutionForResult(JClocationFunctionality.this, 1001);
//                    } catch (IntentSender.SendIntentException sendIntentException) {
//                        sendIntentException.printStackTrace();
//                    }
//                }
//
//                // ask user to change setting
//
//            }
//        });
//
//
//    }
//
//    private void startLocationUpdatesjc() {
//
//        Log.i("LocFun", "in start location updates");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//
//                Log.i("LocFun", "location already granted 2 ");
//
//                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//                // need to actually build in
//
//            } else {
//
//                Log.i("LocFun", "somehow no permission in startlocation updates");
//
//                //This should never happen beecause it goes through permission before it gets here... but just in case
//
//                askLocationPermissionX();
//
//            }
//        }
//    }
//
//    private void stopLocationUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//
//
//    }
//
//    private void askLocationPermissionX() {
//
//        Log.i("LocFun", "inaskLocationPermissionX");
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
//
//            Log.i("LocFun", "show permission rationale");
//
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1022);
//
//        } else {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1022);
//
//            Log.i("LocFun", "actually asking for permission for last location");
//
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//
//        // get permission for ongoing location check
//        if (requestCode == 1022) {
//            if ( grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//
//                // Permission granted
//
//                Log.i("LocFun", "in 102 - sending to checksettings");
//
//                checkSettingsAndStartLocationUpdatesjc();
//
//
//            }   else {
//
//                //Permission not granted - some dialog box
//
//            }
//
//
//        }
//
//
//
//    }
//
//
//
//    public void playerFunction () {
//
//        Log.i("Classes", "playerFunction");
//
//        player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
//        player.setLooping(true);
//        player.start();
//        Intent intent3 = new Intent(JClocationFunctionality.this, HomePage.class);
//        startActivity(intent3);
//
//    }
//

}
