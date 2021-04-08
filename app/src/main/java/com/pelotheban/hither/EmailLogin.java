package com.pelotheban.hither;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EmailLogin extends AppCompatActivity {

    private EditText edtUserNameX, edtPasswordX;
    private String corvusEmail;
    private ConstraintLayout loutEmailLoginX;

    private MaterialButton btnRegisterX, btnLoginX;

    private FirebaseAuth mAuth;
    private DatabaseReference userLoginReference;
    private Query sortUsersLoginQuery;
    private String userUID;

    private String tempUserId;

    private SharedPreferences UserPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        mAuth = FirebaseAuth.getInstance();
        userLoginReference = FirebaseDatabase.getInstance().getReference().child("my_users");

        try {

            UserPref = getSharedPreferences("sharedidsetting", MODE_PRIVATE);
            tempUserId = UserPref.getString("shuserid", "");

        } catch(Exception e) {

        }


        edtUserNameX = findViewById(R.id.edtUserName);
        edtUserNameX.setText(tempUserId);
        edtUserNameX.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        edtPasswordX = findViewById(R.id.edtPassword);
        edtPasswordX.setImeOptions(EditorInfo.IME_ACTION_DONE);

        loutEmailLoginX = findViewById(R.id.loutEmailLogin);
        loutEmailLoginX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try { // we need this because if you tap with no keyboard up the app will crash

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        });

        btnLoginX = findViewById(R.id.btnLogin);
        btnRegisterX = findViewById(R.id.btnRegister);

        btnRegisterX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                tempUserId = edtUserNameX.getText().toString();
                String tempPass = edtPasswordX.getText().toString();
                int tempPassLength = tempPass.length();


                Log.i("LOGINCHECK", "button pressed: " + tempUserId);

                if (tempUserId.contains("@") || tempPassLength < 6) {

                    properCredsSnackbar();
                } else {

                    Log.i("LOGINCHECK", "in the else: " + tempUserId);
                    /// HAD TO CHANGE FIREBASE RULES TO ALLOW READ BEFORE LOGGING
                    sortUsersLoginQuery = FirebaseDatabase.getInstance().getReference().child("my_users").orderByChild("profilename").equalTo(tempUserId);

                    sortUsersLoginQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Log.i("LOGINCHECK", "on onDataChange:");

                            if (dataSnapshot.exists()) {

                                duplicateIDSnackbar();
                            } else {

                                SharedPreferences.Editor editor = UserPref.edit();
                                editor.putString("shuserid", tempUserId);
                                editor.apply(); // saves the value

                                corvusEmail = edtUserNameX.getText().toString() + "@corvus.com";
                                signUp();

                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Log.i("LOGINCHECK", "in the on cancelledd: " + corvusEmail);

                        }
                    });

                    Log.i("LOGINCHECK", "outside the query " + corvusEmail);


                }

            }
        });

        btnLoginX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                tempUserId = edtUserNameX.getText().toString();
                corvusEmail = tempUserId + "@corvus.com";
                signIn();

            }
        });

    }

    private void signUp () {

        mAuth.createUserWithEmailAndPassword(corvusEmail,
                edtPasswordX.getText().toString()).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            FirebaseDatabase.getInstance().getReference().child("my_users").
                                    child(task.getResult().getUser().getUid()).child("profilename").
                                    setValue(edtUserNameX.getText().toString());


                            userUID = FirebaseAuth.getInstance().getUid();
                            loginSnackbar();

                            CountDownTimer loginTimer = new CountDownTimer(3000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    addFirebaseProperties();
                                }
                            }.start();






                        }else{

                            Toast.makeText(EmailLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    private void signIn () {

        mAuth.signInWithEmailAndPassword(corvusEmail,
                edtPasswordX.getText().toString()).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {


                            loginSnackbar();

                            CountDownTimer loginTimer = new CountDownTimer(3000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {

                                    transitionToHome();

                                }
                            }.start();



                        }else{

                            loginFailedSnackbar();

                        }

                    }
                });

    }

    private void  addFirebaseProperties () {


        userLoginReference = FirebaseDatabase.getInstance().getReference().child("my_users").child(userUID);
        userLoginReference.getRef().child("user").setValue(userUID);


        transitionToHome();
    }

    private void transitionToHome(){

        Intent intent = new Intent(EmailLogin.this, HomePage.class);
        startActivity(intent);
        Log.i("HPSTARTS", "Email intent");


    }

    private void loginSnackbar(){

        Snackbar snackbar;

        snackbar = Snackbar.make(loutEmailLoginX, "Welcome: " + tempUserId, Snackbar.LENGTH_INDEFINITE);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getColor(R.color.colorPrimaryDark));

        snackbar.show();


        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextSize(18);

    }

    private void properCredsSnackbar(){

        Snackbar snackbar;

        snackbar = Snackbar.make(loutEmailLoginX, "The User Name cannot contain @ and the Password must have at least 6 characters.", Snackbar.LENGTH_INDEFINITE);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getColor(R.color.colorAccent));

        snackbar.show();


        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextSize(15);

    }

    private void duplicateIDSnackbar(){

        Snackbar snackbar;

        snackbar = Snackbar.make(loutEmailLoginX, "This User Name is taken. Please choose another User Name.", Snackbar.LENGTH_INDEFINITE);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getColor(R.color.colorAccent));

        snackbar.show();


        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextSize(15);

    }

    private void loginFailedSnackbar(){

        Snackbar snackbar;

        snackbar = Snackbar.make(loutEmailLoginX, "The User Name or Password is incorrect. Please try again.", Snackbar.LENGTH_INDEFINITE);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getColor(R.color.colorAccent));

        snackbar.show();


        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextSize(15);

    }
}

