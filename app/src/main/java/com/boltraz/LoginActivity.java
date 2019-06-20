package com.boltraz;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "BOLTRAZ - MainActivity";
    @BindView(R.id.googleSignInBtn)
    SignInButton googleSignInBtn;
    @BindView(R.id.userIDTxt)
    EditText userIDTxt;
    @BindView(R.id.passwordTxt)
    EditText passwordTxt;
    @BindView(R.id.Login_btn)
    Button LoginBtn;
    @BindView(R.id.signUp_btn)
    Button signUpBtn;

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mDatabaseReference;

    String userEmail;
    String password;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

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
                .requestIdToken(getString(R.string.client_id))
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

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.googleSignInBtn)
    public void onViewClicked() {

        signIn();

    }

    //           Log.w(TAG, "Google sign in failed", e);
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success : "+ task.getResult().getUser().getUid());
                            //FirebaseUser user = mAuth.getCurrentUser();
                            userID = task.getResult().getUser().getUid();

                            //Takes snapshot of all the documents


                            Map<String, Object> studentDetails = new HashMap<>();
                            studentDetails.put("Name", task.getResult().getUser().getDisplayName());
                            studentDetails.put("Email", task.getResult().getUser().getEmail());
                            studentDetails.put("UID", task.getResult().getUser().getUid());
                            studentDetails.put("Semester", 7);
                            studentDetails.put("Class", "7A");
                            studentDetails.put("token_id", "empty");

                            String uid = task.getResult().getUser().getUid();

                           mDatabaseReference.child("students").child("semester7").child(uid).setValue(studentDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(LoginActivity.this, "Welcome " + task.getResult().getUser().getDisplayName(), Toast.LENGTH_SHORT).show();


                                   mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                       @Override
                                       public void onSuccess(GetTokenResult getTokenResult) {
                                          String token_id = getTokenResult.getToken();

                                          Map<String, Object> tokenMap = new HashMap<>();
                                          tokenMap.put("token_id", token_id);
                                          
                                          mDatabaseReference.child("students").child("semester7").child(uid).updateChildren(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
                                                  Toast.makeText(LoginActivity.this, "Token updated", Toast.LENGTH_SHORT).show();
                                              }
                                          })
                                                  
                                                  .addOnFailureListener(new OnFailureListener() {
                                                      @Override
                                                      public void onFailure(@NonNull Exception e) {
                                                          Toast.makeText(LoginActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                                      }
                                                  });

                                       }
                                   })

                                           .addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {

                                               }
                                           });

                               }
                           })
                                   .addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(LoginActivity.this, "Check LOG for details.", Toast.LENGTH_SHORT).show();
                                           Log.e(TAG, "onFailure: ", e);
                                       }
                                   });


                          

                            // Create dialog to enter details like sem etc. Then feed it to cloud firestore





                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login Failed. Couldn't log into your Boltraz account", Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });
    }



    @OnClick({R.id.Login_btn, R.id.signUp_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Login_btn:

                emailLogin();

                break;
            case R.id.signUp_btn:

                startActivity(new Intent(LoginActivity.this, CreateUserActivity.class));
                break;
        }
    }

    private void emailLogin() {
         userEmail = userIDTxt.getText().toString();
         password = passwordTxt.getText().toString();

        mAuth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                 //   getLoginDocument();

                    Log.d(TAG, "onComplete:Logged in successfully by " + userEmail);
                    Toast.makeText(LoginActivity.this, "Welcome " + task.getResult().getUser().getDisplayName(), Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {

                    Log.d(TAG, "Failed : Some error : " + task.getException().getMessage());
                    Toast.makeText(LoginActivity.this, "Couldn't log in ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
