package com.boltraz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
    String classxxval = "";
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
    @BindView(R.id.time_textView)
    TextView timeTextView;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference, todoReference;
    String UID, imgUrl;

    String title, desc, author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_announcements);
        ButterKnife.bind(this);

        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        post_key = getIntent().getStringExtra("postID");
        classxxval = getIntent().getStringExtra("classxx");
        imgUrl = getIntent().getStringExtra("imageUrl");

        Log.d("CA", "onCreate: classxxval : " + classxxval
                + "\nTitle : " + title + "\ndesc : " + desc + "\npostkey : " + post_key + "\nimgUrl : " + imgUrl);

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

        Glide.with(getApplicationContext()).load(imgUrl).into(post_imageView);

        getPostDetails();
        Toast.makeText(this, "Post ID : " + post_key, Toast.LENGTH_SHORT).show();

        post_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                alertDialogBuilder.setTitle("Add to your To-Do List?");
                alertDialogBuilder.show();
            }
        });
        UID = FirebaseAuth.getInstance().getUid();


    }

    private void getPostDetails2() {
        databaseReference.child(classxxval).child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassAnnouncementsModel classAnnouncementsModel = dataSnapshot.getValue(ClassAnnouncementsModel.class);
                String url = classAnnouncementsModel.getTitle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostDetails() {

        Log.d("values getPostDetails : ", "onDataChange: getPostDetails : " + classxxval + " : postKey : " + post_key);


        databaseReference.child(classxxval).child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassAnnouncementsModel model;
                model = dataSnapshot.getValue(ClassAnnouncementsModel.class);
                // imgUrl = String.valueOf(dataSnapshot.child("imgUrl").getValue());

                String title_text = model.getTitle();
                String desc_text = model.getDesc();
                String author_text = model.getauthor();
                String time = model.getTime();

                Log.d("CLASSANNOUNCEMENTS : ", "onDataChange: VAL : " + title);
                Glide.with(getApplicationContext()).load(imgUrl).into(post_imageView);

                // String title = String.valueOf(model.getTitle());
                titleText.setText(title_text);
                descText.setText(desc_text);
                authorChip.setText(author_text);
                timeTextView.setText(time);


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

                todoReference.child(UID).child("todos").child(key).setValue(todoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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
