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

import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

public class JClocationService2 extends Service {

    private double latitude, longitude;

    static String closestHub;

    private double dHub1Dist, dHub2Dist, dHub3Dist, dHub4Dist, dHub5Dist;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);


            if (locationResult != null && locationResult.getLastLocation() != null){

                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                Log.i("LocNewApproach", latitude +", " + longitude);


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

        Location userPoint=new Location("locationA");
        userPoint.setLatitude(latitude);
        userPoint.setLongitude(longitude);

        Location hub1Point=new Location("locationB");
        hub1Point.setLatitude(JCconstants.consHub1_Lat);
        hub1Point.setLongitude(JCconstants.consHub1_Long);

        Location hub2Point=new Location("locationC");
        hub2Point.setLatitude(JCconstants.consHub2_Lat);
        hub2Point.setLongitude(JCconstants.consHub2_Long);

        Location hub3Point=new Location("locationD");
        hub3Point.setLatitude(JCconstants.consHub3_Lat);
        hub3Point.setLongitude(JCconstants.consHub3_Long);

        Location hub4Point=new Location("locationE");
        hub4Point.setLatitude(JCconstants.consHub4_Lat);
        hub4Point.setLongitude(JCconstants.consHub4_Long);

        Location hub5Point=new Location("locationF");
        hub5Point.setLatitude(JCconstants.consHub5_Lat);
        hub5Point.setLongitude(JCconstants.consHub5_Long);

        Log.i("LocNewApproach","Lat Hub 1 " + JCconstants.consHub1_Lat);
        Log.i("LocNewApproach","Long Hub 1 " + JCconstants.consHub1_Long);

        Log.i("LocNewApproach","Lat Hub 2 " + JCconstants.consHub2_Lat);
        Log.i("LocNewApproach","Long Hub 2 " + JCconstants.consHub2_Long);

        Log.i("LocNewApproach","Lat Hub 5 " + JCconstants.consHub5_Lat);
        Log.i("LocNewApproach","Long Hub 5 " + JCconstants.consHub5_Long);


        dHub1Dist=userPoint.distanceTo(hub1Point);
        dHub2Dist=userPoint.distanceTo(hub2Point);
        dHub3Dist=userPoint.distanceTo(hub3Point);
        dHub4Dist=userPoint.distanceTo(hub4Point);
        dHub5Dist=userPoint.distanceTo(hub5Point);

        OptionalDouble shortestDist = DoubleStream.of(dHub1Dist, dHub2Dist).min();

        double shortestDistB = shortestDist.orElseThrow(IllegalAccessError::new);


        //need to convert this to some sort of array selector but for not just testing if can get the hub to transfer to HP an work with recycler view
        if (shortestDistB == dHub1Dist){

            closestHub = JCconstants.consHub1;

        }
        if (shortestDistB == dHub2Dist){

            closestHub = JCconstants.consHub2;

        }

        if (shortestDistB == dHub3Dist){

            closestHub = JCconstants.consHub3;

        }
        if (shortestDistB == dHub4Dist){

            closestHub = JCconstants.consHub4;

        }
        if (shortestDistB == dHub5Dist){

            closestHub = JCconstants.consHub5;

        }

        //String distance2 = String.valueOf(distance);
        Log.i("LocNewApproach","Closest Hub " + closestHub);
        Log.i("LocNewApproach", "DistanceHub1: " + dHub1Dist);
        Log.i("LocNewApproach", "DistanceHub2: " + dHub2Dist);
        Log.i("LocNewApproach", "DistanceHub3: " + dHub3Dist);
        Log.i("LocNewApproach", "DistanceHub4: " + dHub4Dist);
        Log.i("LocNewApproach", "DistanceHub5: " + dHub5Dist);

        //txtDistanceX.setText(distance2);

        // hubs
        // create location


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
