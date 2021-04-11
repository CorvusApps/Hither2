package com.pelotheban.hither;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;



public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private TextView txtUserLocationX, txtDistanceX;
    private FloatingActionButton fab;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest; // this and callback needed for when location is null because no app generated a last location
    private LocationCallback locationCallback;


    private DatabaseReference homePageRef, userHomePageRef;
    private String userID;
    private FirebaseAuth hpAuth;

    private double lat, longit;

    private Button btnLogoutX, btnStartLocationServiceX, btnStopLocationServiceX;

    // recycler view elements

    private RecyclerView rcvProfilesX;
    int cardToggle;
    private LinearLayoutManager layoutManager;
    private Query sortProfiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i("LocFun", "log check");

        btnLogoutX = findViewById(R.id.btnLogout);
        btnLogoutX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logout();

            }
        });

        //////////////////// LOCATION CALLBACK /////////////////////////////////

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                Log.i("LocFun", " in location callback");

                if (locationResult != null) {

                    Log.i("LocFun", " last location success 3");

                   for (Location location: locationResult.getLocations()) {

                       lat = location.getLatitude();
                       longit = location.getLongitude();

                       txtUserLocationX.setText("Lat: " + lat + "  Long: " + longit);
                       userHomePageRef = FirebaseDatabase.getInstance().getReference().child("my_users").child(userID);
                       //homePageRef.getRef().child("lastlocation").setValue(location); // this just created a last location not linked to a user
                       userHomePageRef.getRef().child("lastlocation").setValue(location);

                       calculateDistance();

                   }


                } else {

                    Log.i("LocFun", "location result null ");

                    //TBD what to do with this
                }



            }
        };


        ////////////////// end location callback ////////////////////////////////////

        //////////////////// FIREBASE BASICS /////////////////////////////////////

        userID = FirebaseAuth.getInstance().getUid();
        homePageRef = FirebaseDatabase.getInstance().getReference().child("my_users");

        hpAuth = FirebaseAuth.getInstance();
        homePageRef.keepSynced(true);

        ///////////////////////////////////////////////////////////////////////////////////

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        txtUserLocationX = findViewById(R.id.txtUserLocation);
        txtDistanceX = findViewById(R.id.txtDistance);

        btnStartLocationServiceX = findViewById(R.id.btnStartLocService);
        btnStartLocationServiceX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startService(new Intent(HomePage.this,  JClocationService.class));
            }
        });

        btnStopLocationServiceX = findViewById(R.id.btnStopLocService);
        btnStopLocationServiceX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(new Intent(HomePage.this, JClocationService.class));

            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LocFun", "button press");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                        Log.i("LocFun", "Already has peremission 1");

                        // get last location if available MOTHBOLLING LAST LOCATION functionality but keeping as // for now
//                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(HomePage.this, new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//
//
//
//                                if (location != null) {
//
//                                    Log.i("LocFun", " last location success 1");
//
//                                    lat = location.getLatitude();
//                                    longit = location.getLongitude();
//
//                                    txtUserLocationX.setText("Lat: " + lat + "  Long: " + longit);
//                                    userHomePageRef = FirebaseDatabase.getInstance().getReference().child("my_users").child(userID);
//                                    //homePageRef.getRef().child("lastlocation").setValue(location); // this just created a last location not linked to a user
//                                    userHomePageRef.getRef().child("lastlocation").setValue(location);
//
//                                    calculateDistance();

//
//                                } else {
//
//                                    Log.i("LocFun", "last location null 1");



                                    checkSettingsAndStartLocationUpdates();



                               // }

                           // }
                       // });

                    } else {

                        Log.i("LocFun", "need permission");
                        // go to request permission which applies to last location only

                        askLocationPermissionX();


                    }

                } else {
                    // this else means earlier veresion of Android no need to check permissions
                    checkSettingsAndStartLocationUpdates();

                }

            }
        });


        ///////////////////////////RECCLER VIEW METHODS inside onCreate - START - //////////////////////////

        sortProfiles = homePageRef.orderByChild("profilename");
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);

        //UI Components
        rcvProfilesX = findViewById(R.id.rcvProfiles);
        rcvProfilesX.setHasFixedSize(true); //Not sure this applies or why it is here
        rcvProfilesX.setLayoutManager(layoutManager);

        cardToggle = 0; // allows for onclick expansion and deflation of coin cards



        // The Code setting out recycler view /////////////////////////////////////////////////////////////////
// The tutorial had this section of code through to setAdapter in separate on Start Method but for StaggeredGrid that seemed to cause the recycler view to be destroyed and not come back once we moved off the screen works fine here


        final FirebaseRecyclerAdapter<ZZZjcProfiles, ZZZjcProfilesViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ZZZjcProfiles, ZZZjcProfilesViewHolder>
                (ZZZjcProfiles.class,R.layout.yyy_card_profile, ZZZjcProfilesViewHolder.class,sortProfiles) {


            @Override
            protected void populateViewHolder(ZZZjcProfilesViewHolder viewHolder, ZZZjcProfiles model, int position) {
                viewHolder.setProfilename(model.getProfilename());
                viewHolder.setLatitude(model.getLatitude());
                viewHolder.setLongitude(model.getLongitude());

            }

            // The Code setting out recycler view /////////////////////////////////////////////////////////////////

            // This method is part of the onItemClick AND onLongItem Click code NOT to populate the recycler view /////////////////////////////////////////////////////
            @Override
            public ZZZjcProfilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                ZZZjcProfilesViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                viewHolder.setOnItemClickListener(new ZZZjcProfilesViewHolder.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {


                    }

                });

                return viewHolder;
            }

        };

        ///////////////////////////////////////////////////////

        // The onclick methods were in the broader recycler view methods - this calls for the adapter on everything
        rcvProfilesX.setAdapter(firebaseRecyclerAdapter);




        ///////////////////////////RECCLER VIEW METHODS inside onCreate - END - //////////////////////////


    } ////////////////////// END OF ON CREATE ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStop() {
        super.onStop();


        stopLocationUpdates();
    }

    // when the onClics were in onCreate this code was throwing error likely related to the context this and view did not work
//    @Override
//    public void onClick(View view) {
//
//        if (view == btnStartLocationServiceX) {
//
//            startService(new Intent(this,  JClocationService.class));
//        }
//
//        if (view == btnStopLocationServiceX) {
//
//            stopService(new Intent(this,  JClocationService.class));
//        }
//
//
//
//    }


    //////////////////////// START ------> RECYCLER VIEW COMPONENTS /////////////////////////////////////////////////////
    /////////////////// includes: viewholder, sort, expoloding card view dialog, functions from dialog //////////////////

    // View holder for the recycler view
    public static class ZZZjcProfilesViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ZZZjcProfilesViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

            // Custom built onItemClickListener for the recycler view
            ////////////////////////////////////////////////////////////////////////////////////////
            //Listen to the video as this is a bit confusing - also added the OnTimeclick listener above to the parameters NOT

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    hpListener.onItemClick(view, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    hpListener.onItemLongClick(view, getAdapterPosition());

                    return true;
                }
            });

            ////////////////////////////////////////////////////////////////////////////////////////

        }

        //setting all the info from collection add to cardview;some will be hidden and passed on to expanded coin view or other activities


        // Recycler view reuses inflaed layouts; so when you click on a card and scroll down that layout is reused and if you clicked it the card 6 or 7 places down will be inflated
        // So First had to put the set layouts function in the populate view holder method - this way this method gets called for each coins card; then here reset the layouts to gone
        // that way any inflated cards get snapped back before they are shown

        public void setProfilename (String profilename){

            TextView txtProfileNameX = (TextView)mView.findViewById(R.id.txtCardProfileName);
            txtProfileNameX.setText(profilename);
        }


        public void setLatitude(double latitude) {

            TextView txtLatitudeX = (TextView)mView.findViewById(R.id.txtCardLatitude);
            txtLatitudeX.setText(String.valueOf(latitude));

        }

        public void setLongitude(double longitude) {

            TextView txtLongitudeX = (TextView)mView.findViewById(R.id.txtCardLongitude);
            txtLongitudeX.setText(String.valueOf(longitude));

        }



        // Custom built onItemClickListener for the recycler view; seems to cover LongClick as well
        ////////////////////////////////////////////////////////////////////////////////////////
        //Listen to the video as this is a bit confusing

        private ZZZjcProfilesViewHolder.OnItemClickListener hpListener;

        public interface OnItemClickListener {

            void onItemClick(View view, int position);
            void onItemLongClick (View view, int position);
        }

        public void setOnItemClickListener(ZZZjcProfilesViewHolder.OnItemClickListener listener) {

            hpListener = listener;
        }

        ///////////////////////////////////////////////////////////////////////////////////////

    }

    private void calculateDistance() {

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat);
        startPoint.setLongitude(longit);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(43.364605);
        endPoint.setLongitude(-80.3127999);

        double distance=startPoint.distanceTo(endPoint);

        String distance2 = String.valueOf(distance);
        txtDistanceX.setText(distance2);


    }

    private void Logout() {

        hpAuth.signOut();
        LoginManager.getInstance().logOut();
        transitionBackToLogin ();
    }

    private void transitionBackToLogin () {

        Intent intent = new Intent(HomePage.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void checkSettingsAndStartLocationUpdates() {

        Log.i("LocFun", "in check settings");

        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();
        SettingsClient client= LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                startLocationUpdates();

            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {

                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(HomePage.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }

                // ask user to change setting

            }
        });


    }

    private void startLocationUpdates() {

        Log.i("LocFun", "in start location updates");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.i("LocFun", "location already granted 2 ");

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                // need to actually build in

            } else {

                Log.i("LocFun", "somehow no permission in startlocation updates");

            //This should never happen beecause it goes through permission before it gets here... but just in case

                askLocationPermissionX();

            }
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);


    }

    // ask permission for the use last location // the call to this method is mothballed as only using contiuous
    private void askLocationPermission() {

        Log.i("LocFun", "in askLocationPermission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            Log.i("LocFun", "show permission rationale");

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 101);

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 101);

            Log.i("LocFun", "actually asking for permission for last location");

        }


    }

    // ask permission for ongoing location checking
    private void askLocationPermissionX() {

        Log.i("LocFun", "inaskLocationPermissionX");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            Log.i("LocFun", "show permission rationale");

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 102);

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 102);

            Log.i("LocFun", "actually asking for permission for last location");

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 101) {
            Log.i("LocFun", "in request 101");
           if ( grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

               Log.i("LocFun", "101 granted");

               // Permission granted for last locations
               // but for some reason have to ask for it again here

               if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                       == PackageManager.PERMISSION_GRANTED) {


                   // get location
                   fusedLocationProviderClient.getLastLocation().addOnSuccessListener(HomePage.this, new OnSuccessListener<Location>() {
                       @Override
                       public void onSuccess(Location location) {

                           Log.i("LocFun", "getting location 2");

                           if (location != null) {

                               Log.i("LocFun", "success");

                               lat = location.getLatitude();
                               longit = location.getLongitude();

                               txtUserLocationX.setText("Lat: " + lat + "  Long: " + longit);
                               userHomePageRef = FirebaseDatabase.getInstance().getReference().child("my_users").child(userID);
                               //homePageRef.getRef().child("lastlocation").setValue(location); // this just created a last location not linked to a user
                               userHomePageRef.getRef().child("lastlocation").setValue(location);

                               calculateDistance();


                           } else {

                               checkSettingsAndStartLocationUpdates();

                               Log.i("LocFun", "sending to checksettings 2");
                           }
                       }
                   });

               }




           }   else {

               //Permission not granted - some dialog box

           }


        }
        // get permission for ongoing location check
        if (requestCode == 102) {
            if ( grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                // Permission granted

                Log.i("LocFun", "in 102 - sending to checksettings");

                checkSettingsAndStartLocationUpdates();


            }   else {

                //Permission not granted - some dialog box

            }


        }



    }





}