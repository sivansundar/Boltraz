package com.boltraz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "BOLTRAZ - MainActivity";

    @BindView(R.id.userIDTxt)
    EditText userIDTxt;
    @BindView(R.id.passwordTxt)
    EditText passwordTxt;
    @BindView(R.id.loginButton)
    Button loginBtn;
    public RelativeLayout rootView;


    private GoogleApiClient mGoogleApiClient;
    public ProgressDialog mProgressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mDatabaseReference;
    @BindView(R.id.forgotPassword_textView)
    TextView forgotPasswordTextView;


    String userEmail;
    String password;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        rootView = findViewById(R.id.rootView_login);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mProgressBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this, "Connection failed. Couldn't connect to Boltraz", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View alertLayout = layoutInflater.inflate(R.layout.resetpassword_alertdialog_view, null);

                EditText resetEmailEditText = alertLayout.findViewById(R.id.resetPassword_edittext);


                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);


                alert.setTitle("Password reset");
                alert.setMessage("Enter your email address to which your Boltraz account is linked to");
                alert.setView(alertLayout);
                alert.setPositiveButton("Send reset link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String resetEmail_text = resetEmailEditText.getText().toString();

                        if (resetEmail_text.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Your email address cannot be empty.", Toast.LENGTH_SHORT).show();
                        } else {
                            editPassword(resetEmail_text);
                        }
                    }
                });
                alert.show();
            }
        });

    }

    private void editPassword(String resetEmail_text) {
        //String currentEmail = preferences.getString("email", "");
        //Log.d(TAG, "editPassword: currentEmail : " + currentEmail);

        mAuth.sendPasswordResetEmail(resetEmail_text).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgressBar.setMessage("Sending you a password reset link");
                mProgressBar.show();
                if (task.isSuccessful()) {


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.dismiss();

                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                            alert.setTitle("Password reset link sent");
                            alert.setMessage("A link to reset your password has been sent to " + resetEmail_text + ".");
                            alert.show();

                        }
                    }, 3000);

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }


    @OnClick({R.id.loginButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.loginButton:

                emailLogin();

                break;

        }
    }

    private void emailLogin() {

        userEmail = userIDTxt.getText().toString().trim();
        password = passwordTxt.getText().toString().trim();

        mProgressBar.setTitle("Login");
        mProgressBar.setMessage("Logging you in. Please wait.");
        mProgressBar.show();

        mAuth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //   getLoginDocument();

                    Log.d(TAG, "onComplete:Logged in successfully by " + userEmail);
                    //getTokenAndUpdate(mAuth.getUid());
                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.dismiss();


                        }
                    }, 3000);

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {

                    Log.d(TAG, "Failed : Some error : " + task.getException().getMessage());
                    // Toast.makeText(LoginActivity.this, "Couldn't log in. Incorrect credentials. ", Toast.LENGTH_LONG).show();

                    Snackbar snackbar = Snackbar
                            .make(rootView, "Couldn't log in. Incorrect credentials.", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    mProgressBar.dismiss();
                }
            }
        });




    }


}
