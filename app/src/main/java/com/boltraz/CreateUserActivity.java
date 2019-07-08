package com.boltraz;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateUserActivity extends AppCompatActivity {

  @BindView(R.id.createHeadertxt)
  TextView createHeadertxt;
  @BindView(R.id.name_EditText)
  TextInputEditText nameEditText;
  @BindView(R.id.name_holder)
  TextInputLayout nameHolder;
  @BindView(R.id.email_EditText)
  TextInputEditText emailEditText;
  @BindView(R.id.email_holder)
  TextInputLayout emailHolder;
  @BindView(R.id.password_EditText)
  TextInputEditText passwordEditText;
  @BindView(R.id.password_holder)
  TextInputLayout passwordHolder;
  @BindView(R.id.usn_EditText)
  TextInputEditText usnEditText;
  @BindView(R.id.usn_holder)
  TextInputLayout usnHolder;
  @BindView(R.id.class_EditText)
  TextInputEditText classEditText;
  @BindView(R.id.class_holder)
  TextInputLayout classHolder;
  @BindView(R.id.create_user_btn)
  MaterialButton createUserBtn;

  private FirebaseAuth mAuth;
  private FirebaseDatabase mDatabase;
  private DatabaseReference databaseReference;


  private String TAG = "Boltraz : Create User Activity : ";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_user);
    ButterKnife.bind(this);

    mAuth = FirebaseAuth.getInstance();
    mDatabase = FirebaseDatabase.getInstance();
    databaseReference = mDatabase.getReference();
  }

  @OnClick(R.id.create_user_btn)
  public void onViewClicked() {
    String name = nameEditText.getText().toString();
    String email = emailEditText.getText().toString();
    String password = passwordEditText.getText().toString();
    String usn = usnEditText.getText().toString();
    String class_text = classEditText.getText().toString();

    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || usn.isEmpty() || class_text.isEmpty()) {
      Toast.makeText(this, "Fill up all the fields please.", Toast.LENGTH_SHORT).show();
    } else {

      mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
          Toast.makeText(CreateUserActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
          String UID = authResult.getUser().getUid();

          HashMap<String, Object> student = new HashMap<>();
          student.put("Name", name);
          student.put("email", email);
          student.put("Class", class_text);
          student.put("USN", usn);
          student.put("UID", UID);

          databaseReference.child("students").child("semester6").child(UID).setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              Toast.makeText(CreateUserActivity.this, "Posted", Toast.LENGTH_SHORT).show();
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
                  Toast.makeText(CreateUserActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                  Log.e(TAG, "onFailure: ", e);
                }
              });

    }

    // Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();

  }
}