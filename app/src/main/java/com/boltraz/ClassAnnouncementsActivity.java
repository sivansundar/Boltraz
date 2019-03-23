package com.boltraz;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.boltraz.Model.ClassAnnouncementsModel;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassAnnouncementsActivity extends AppCompatActivity {

    String post_key;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.date_text)
    TextView dateText;
    @BindView(R.id.desc_text)
    TextView descText;
    @BindView(R.id.author_chip)
    Chip authorChip;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_announcements);
        ButterKnife.bind(this);

        post_key = getIntent().getStringExtra("postID");
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("classAnnouncements");

        getPostDetails(post_key);
        Toast.makeText(this, "Post ID : " + post_key, Toast.LENGTH_SHORT).show();
    }

    private void getPostDetails(String post_key) {
        databaseReference.child("Class6B").child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassAnnouncementsModel model;
                model = dataSnapshot.getValue(ClassAnnouncementsModel.class);
                titleText.setText(model.getTitle());
                descText.setText(model.getDesc());
                authorChip.setText(model.getPostAuthor());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ClassAnnouncementsActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
