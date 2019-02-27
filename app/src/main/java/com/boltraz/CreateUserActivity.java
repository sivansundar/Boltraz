package com.boltraz;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateUserActivity extends AppCompatActivity {

    @BindView(R.id.createHeadertxt)
    TextView createHeadertxt;
    @BindView(R.id.name_text)
    EditText nameText;
    @BindView(R.id.new_emailid_text)
    EditText newEmailidText;
    @BindView(R.id.new_password_text)
    EditText newPasswordText;
    @BindView(R.id.sem_text)
    EditText semText;
    @BindView(R.id.create_user_btn)
    Button createUserBtn;
    @BindView(R.id.classsec)
    EditText classsec;
    @BindView(R.id.usn_txt)
    EditText usnTxt;

    private FirebaseAuth mAuth;
    private FirebaseFirestore dbFirestore;
    private String TAG = "Boltraz : Create User Activity : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        dbFirestore = FirebaseFirestore.getInstance();

    }

    @OnClick(R.id.create_user_btn)
    public void onViewClicked() {

        String name = nameText.getText().toString().trim();
        String emailid = newEmailidText.getText().toString().trim();
        String password = newPasswordText.getText().toString().trim();
        String semester = semText.getText().toString().trim();
        String classsection = classsec.getText().toString().trim();
        String usn = usnTxt.getText().toString().trim();


        if (name.isEmpty() || emailid.isEmpty() || password.isEmpty() || semester.isEmpty() || usn.isEmpty()) {

            Toast.makeText(this, "Some fields are missing", Toast.LENGTH_SHORT).show();

        } else {
            mAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateUserActivity.this, "Account created! Welcome " + name, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: Account created with name = " + name);

                        Map<String, Object> studentDetails = new HashMap<>();
                        studentDetails.put("Name", name);
                        studentDetails.put("EmailID", emailid);
                        studentDetails.put("Password", password);
                        studentDetails.put("Semester", semester);
                        studentDetails.put("Class", classsection);
                        studentDetails.put("USN", usn);

                        String uID = task.getResult().getUser().getUid();
                        dbFirestore.collection("Students")
                                .document(uID)
                                .set(studentDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Recorded added to Firestore Students : " + uID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: ", e);
                            }
                        });
                    } else {
                        Log.d(TAG, "onFail: Failed = " + task.getException().getMessage());

                    }
                }
            });
        }
    }
}
