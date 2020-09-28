package com.pelotheban.hither;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int providerSelector; // 1 = google 2 = facebook 3 = email  4 = default

    // Google
    private MaterialButton btnLoginGoogleX;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

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

        btnActualFBLoginButtonX = findViewById(R.id.btnActualFBLoginButton);
        btnActualFBLoginButtonX.setReadPermissions(Arrays.asList("email", "public_profile"));

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

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

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
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(MainActivity.this, "FAILED TO LOG IN THROUGH GOOGLE", Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    //////////////////////// GOOGLE SIGN IN - END///////////////////////////////////
}