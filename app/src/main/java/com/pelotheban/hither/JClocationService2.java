package com.pelotheban.hither;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JClocationService2 extends Service {

    private double latitude, longitude;

    private String Hub1, Hub2, Hub3, Hub4, Hub5;
    private double dHub1Lat, dHub1Lon, dHub2Lat, dHub2Lon, dHub3Lat, dHub3Lon, dHub4Lat,dHub4Lon, dHub5Lat, dHub5Lon;
    private double dHub1Dist, dHub2Dist, dHub3Dist, dHub4Dist, dHub5Dist;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null){

                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                Log.i("LocNewApproach", latitude +", " + longitude);
                Log.i("LocNewApproach", JCconstants.consHub1);

                DatabaseReference userJCLoc2Ref;
                String userID;
                userID = FirebaseAuth.getInstance().getUid();
                //FirebaseAuth hpAuth;

                for (Location location: locationResult.getLocations()) {

                    userJCLoc2Ref = FirebaseDatabase.getInstance().getReference().child("my_users").child(userID);

                    userJCLoc2Ref.getRef().child("lastlocation").setValue(location);
                }

                calculateDistance();

            }

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService (){

        Log.i("LocNewApproach", "in startLocationService");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("LocNewApproach", "Already has permission 1");


                LocationServices.getFusedLocationProviderClient(this)
                        .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//               startForeground(JCconstants.LOCATION_SERVICE_ID,builder.build());


            } else {

              //  askLocationPermission2X();

            }

        }

    }

    private void stopLocationService() {

        Log.i("LocNewApproach", "in stopLocationService");
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();

    }

    private void calculateDistance() {

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(43.364605);
        endPoint.setLongitude(-80.3127999);

        double distance=startPoint.distanceTo(endPoint);

        String distance2 = String.valueOf(distance);
        Log.i("LocNewApproach", "Distance: " + distance2);
        //txtDistanceX.setText(distance2);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocNewApproach", "onStart Command");


        String channelID = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelID
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(notificationManager != null && notificationManager.getNotificationChannel(channelID) == null){
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelID,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH

                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            } else {


            }

        }



        if(intent !=null) {
            String action = intent.getAction();
            Log.i("LocNewApproach", "action: " + action);
            if (action != null) {
                if(action.equals(JCconstants.ACTION_START_LOCATION_SERVICE)) {

                    Log.i("LocNewApproach", "go to start location service");

                    startLocationService();
                } else if (action.equals(JCconstants.ACTION_STOP_LOCATION_SERVICE)) {

                    Log.i("LocNewApproach", "go to stop location service");

                    stopLocationService();
                }

            }
        }

        startForeground(JCconstants.LOCATION_SERVICE_ID,builder.build());
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY; // this may be better for background; still doesn't really work with all the restriction
    }
}
