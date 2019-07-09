package com.boltraz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassAnnouncementsActivity extends AppCompatActivity {

    String post_key = "";
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.date_text)
    TextView dateText;
    @BindView(R.id.desc_text)
    TextView descText;
    @BindView(R.id.author_chip)
    Chip authorChip;
    @BindView(R.id.addAlert_fab)
    FloatingActionButton FabaddAlert;
    @BindView(R.id.imageView)
    GestureImageView post_imageView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference, todoReference;
    String UID, imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_announcements);
        ButterKnife.bind(this);

        post_key = getIntent().getStringExtra("postID");
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("classAnnouncements");
        todoReference = mDatabase.getReference().child("students");
        post_imageView = findViewById(R.id.imageView);

        post_imageView.getController().getSettings()
                .setMaxZoom(2f)
                .setDoubleTapZoom(-1f) // Falls back to max zoom level
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(false)
                .setRestrictRotation(false)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f)
                .setFillViewport(false)
                .setFitMethod(Settings.Fit.INSIDE)
                .setGravity(Gravity.CENTER);

        getPostDetails();
        Toast.makeText(this, "Post ID : " + post_key, Toast.LENGTH_SHORT).show();

        UID = FirebaseAuth.getInstance().getUid();


    }

    private void getPostDetails() {
        databaseReference.child("Class7A").child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassAnnouncementsModel model;
                model = dataSnapshot.getValue(ClassAnnouncementsModel.class);
                imgUrl = model.getImgUrl();

                Glide.with(getApplicationContext()).load(imgUrl).into(post_imageView);


                titleText.setText(model.getTitle());
                descText.setText(model.getDesc());
                authorChip.setText(model.getauthor());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ClassAnnouncementsActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.addAlert_fab)
    public void onFabaddAlertClicked() {
        //TODO: add click handling

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Add to your To-Do List?");
        alertDialogBuilder.setMessage("Do you want to add '" + titleText.getText().toString() + "' to your To-Do List?");
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nothing happens.
            }
        });
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String key = databaseReference.push().getKey();

                String todo_title = titleText.getText().toString();
                String todo_desc = descText.getText().toString();
                String todo_author = authorChip.getText().toString();

                HashMap<String, Object> todoMap = new HashMap<>();
                todoMap.put("title", todo_title);
                todoMap.put("desc", todo_desc);
                todoMap.put("author", todo_author);
                todoMap.put("imgUrl", imgUrl);

                todoReference.child("semester7").child(UID).child("todos").child(key).setValue(todoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        View contextView = findViewById(R.id.mainLayout);

                        Snackbar snackbar = Snackbar
                                .make(contextView, "'" + todo_title + "' was successfully added to your To-Do List.", Snackbar.LENGTH_LONG);

                        snackbar.show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Announcement Activity", "onFailure: ", e);
                            }
                        });


            }
        });

        alertDialogBuilder.show();
    }

}
