package com.boltraz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.Model.ClassAnnouncementsModel;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssignmentActivity extends AppCompatActivity {

    private static final String TAG = "AssignmentActivity";
    public SharedPreferences preferences;
    String assignment_postKey;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.date_text)
    TextView dateText;
    @BindView(R.id.author_chip)
    Chip authorChip;
    @BindView(R.id.img_recyclerView)
    RecyclerView imgRecyclerView;
    @BindView(R.id.time_textView)
    TextView timeTextView;
    @BindView(R.id.desc_text)
    TextView descText;
    @BindView(R.id.content_scroll)
    ScrollView contentScroll;
    String title, desc, author, classxx;
    private android.app.ProgressDialog mProgressBar;


    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference, todoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        ButterKnife.bind(this);

        preferences = this.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);

        assignment_postKey = getIntent().getStringExtra("assignment_postKey");

        classxx = preferences.getString("classxx", "XXX");

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("Assignments");

        getAssignment(assignment_postKey);

    }

    private void getAssignment(String assignment_postKey) {

        Log.d(TAG, "getAssignment: classxx + " + classxx);

        databaseReference.child(classxx).child(assignment_postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassAnnouncementsModel model = dataSnapshot.getValue(ClassAnnouncementsModel.class);


                String title = model.getTitle();
                String desc = model.getDesc();
                String date = model.getDate();
                String author = model.getauthor();
                String postID = model.getPostID();
                String time = model.getTime();

                titleText.setText(title);
                descText.setText(desc);
                timeTextView.setText(time);
                authorChip.setText(author);
                dateText.setText(date);


                Log.d(TAG, "onDataChange: Assignment values : " + "\n" + title + "\n" + desc + "\n" + date + "\n" + author + "\n" + postID + "\n" + time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
