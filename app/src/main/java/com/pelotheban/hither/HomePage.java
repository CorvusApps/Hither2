package com.pelotheban.hither;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;



public class HomePage extends AppCompatActivity {

    private TextView txtUserLocationX, txtDistanceX;
    private FloatingActionButton fab;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private DatabaseReference homePageRef;
    private String userID;
    private FirebaseAuth hpAuth;

    private double lat, longit;

    private Button btnLogoutX;

    // recycler view elements

    private RecyclerView rcvProfilesX;
    int cardToggle;
    private LinearLayoutManager layoutManager;
    private Query sortProfiles;

    // for git

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnLogoutX = findViewById(R.id.btnLogout);
        btnLogoutX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logout();

            }
        });

        //////////////////// FIREBASE BASICS /////////////////////////////////////

        userID = FirebaseAuth.getInstance().getUid();
        homePageRef = FirebaseDatabase.getInstance().getReference().child("my_users");
        hpAuth = FirebaseAuth.getInstance();
        homePageRef.keepSynced(true);

        ///////////////////////////////////////////////////////////////////////////////////

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        txtUserLocationX = findViewById(R.id.txtUserLocation);
        txtDistanceX = findViewById(R.id.txtDistance);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                        // get location
                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if (location != null) {

                                    Log.i("LocFun", "success");

                                    lat = location.getLatitude();
                                    longit = location.getLongitude();

                                    txtUserLocationX.setText("Lat: " + lat + "  Long: " + longit);
                                    homePageRef.getRef().child("lastlocation").setValue(location);

                                    calculateDistance();


                                } else {

                                    Log.i("LocFun", "null");

                                }

                            }
                        });

                    } else {

                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);

                    }

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

}