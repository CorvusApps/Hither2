package com.pelotheban.hither;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int providerSelector; // 1 = google 2 = facebook 3 = email  4 = default
    private FirebaseAuth mAuth;

    // Google
    private MaterialButton btnLoginGoogleX;
    private GoogleSignInClient mGoogleSignInClient;


    //Email
    private TextView txtEmailLoginX;

    //Facebook
    private LoginButton btnActualFBLoginButtonX;
    private MaterialButton btnLoginFacebookX;
    private CallbackManager callbackManager;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(MainActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, MenuPage.class);
            startActivity(intent);
            finish();

        } else {

            Toast.makeText(MainActivity.this, "NEED TO LOG IN", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        providerSelector = 4;

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

        // Firebase test

        mAuth = FirebaseAuth.getInstance();

        //Google LOGIN
        creatGoogleLoginRequest();

        btnLoginGoogleX = findViewById(R.id.btnLoginGoogle);
        btnLoginGoogleX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                providerSelector = 1;

                signIn();

            }
        });

        //Facebook LOGIN

        FacebookSdk.sdkInitialize(getApplicationContext());

        btnActualFBLoginButtonX = findViewById(R.id.btnActualFBLoginButton);
        btnActualFBLoginButtonX.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));

        btnLoginFacebookX = findViewById(R.id.btnLoginFacebook);
        btnLoginFacebookX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                providerSelector = 2;
                btnActualFBLoginButtonX.performClick();

            }
        });

        callbackManager = CallbackManager.Factory.create();
        btnActualFBLoginButtonX.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.i("FBL", "success");
                handleFacebookToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.i("FBL", "cancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.i("FBL", "onError " + error.toString());

            }
        });


        //Email LOGIN

        txtEmailLoginX = findViewById(R.id.txtEmailLogin);
        txtEmailLoginX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                providerSelector = 3;
                Intent intent = new Intent(MainActivity.this, EmailLogin.class);
                startActivity(intent);
                finish();

            }
        });




    }


    //////////////////////// GOOGLE SIGN IN - START ///////////////////////////////////

    // THE CALL BACK MANAGER IS FOR BOTH FACEBOOK AND GOOGLE

    private void creatGoogleLoginRequest() {

        // Configure google sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //getString(R.string.default_web_client_id
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (providerSelector == 1) {

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == 101) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    // ...
                }
            }

        } else if (providerSelector == 2){

            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Logged on through Google", Toast.LENGTH_LONG).show();

                            String userUID = FirebaseAuth.getInstance().getUid();
                            Log.i("GoogleSign", userUID);
                            DatabaseReference userLoginReference = FirebaseDatabase.getInstance().getReference().child("my_users").child(userUID);
                            Log.i("GoogleSign", userLoginReference.toString());
                            userLoginReference.getRef().child("user").setValue(userUID);
                            userLoginReference.getRef().child("profilename").setValue("HitherCat");

                            Intent intent = new Intent(MainActivity.this, MenuPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(MainActivity.this, "FAILED TO LOG IN THROUGH GOOGLE", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    //////////////////////// GOOGLE SIGN IN - END///////////////////////////////////

    //////////////////////// FACEBOOK SIGN IN - BEGINNING /////////////////////////
    //callback manager shared with Google above in googgle section

    private void handleFacebookToken (AccessToken token) {
        Log.i("FBL", "handle the token " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(MainActivity.this, "Logged on through Facebook", Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();

                    String userUID = FirebaseAuth.getInstance().getUid();

                    DatabaseReference userLoginReference = FirebaseDatabase.getInstance().getReference().child("my_users").child(userUID);
                    userLoginReference.getRef().child("user").setValue(userUID);

                } else {

                    Toast.makeText(MainActivity.this, "FAILED TO LOG IN THROUGH Facebook", Toast.LENGTH_LONG).show();
                }

            }
        });


    }


    //////////////////////// FACEBOOK SIGN IN - END /////////////////////////



}