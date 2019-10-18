package com.boltraz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.Model.ImageURLModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.chrisbanes.photoview.PhotoView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClassAnnouncementsActivity extends AppCompatActivity {

    private static final String TAG = "Class Announcements";
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
    FloatingActionButton addAnnoun_fab;

    @BindView(R.id.time_textView)
    TextView timeTextView;
    @BindView(R.id.img_recyclerView)
    RecyclerView imgRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference, todoReference;
    String UID, imgUrl;

    String title, desc, author;
    public SharedPreferences preferences;

    String todoPostKey;
    ArrayList<String> imageArrayList;
    FirebaseStorage mStorage;
    private ProgressDialog mProgressBar;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_announcements);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        imageArrayList = new ArrayList<>();

        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        post_key = getIntent().getStringExtra("postID");
        classxxval = getIntent().getStringExtra("classxx");
        imgUrl = getIntent().getStringExtra("imageUrl");

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("classAnnouncements");
        mStorage = FirebaseStorage.getInstance();
        todoReference = mDatabase.getReference().child("students");

        preferences = this.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);


        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(desc) && TextUtils.isEmpty(post_key) && TextUtils.isEmpty(classxxval) && TextUtils.isEmpty(imgUrl)) {

            todoPostKey = getIntent().getStringExtra("postKey");

            if (!TextUtils.isEmpty(todoPostKey)) {

                Toast.makeText(this, "All of this is empty : ToDoPostKey : " + todoPostKey, Toast.LENGTH_SHORT).show();

                String classVal = preferences.getString("classxx", "XXX");

                getPostImages(todoPostKey, classVal);

                getPostDetails(todoPostKey, classVal);

                addAnnoun_fab.setVisibility(View.GONE);

            }
        } else {


            Log.d("CA", "onCreate: classxxval : " + classxxval
                    + "\nTitle : " + title + "\ndesc : " + desc + "\npostkey : " + post_key + "\nimgUrl : " + imgUrl);


            getPostImages(post_key, classxxval);

            getPostDetails(post_key, classxxval);
            Toast.makeText(this, "Post ID : " + post_key, Toast.LENGTH_SHORT).show();


        }
// post_imageView = findViewById(R.id.imageView);

        imgRecyclerView.setHasFixedSize(true);
        imgRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        mProgressBar = new ProgressDialog(this);


        // Glide.with(getApplicationContext()).load(imgUrl).into(post_imageView);


        UID = FirebaseAuth.getInstance().getUid();


    }


    private void getPostImages(String post_key, String classxxval) {

        //  mProgressBar.setMessage("Loading.....");


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("classAnnouncements")
                .child(classxxval)
                .child(post_key)
                .child("imgURLs");


        FirebaseRecyclerOptions<ImageURLModel> options =
                new FirebaseRecyclerOptions.Builder<ImageURLModel>()
                        .setQuery(query, ImageURLModel.class)
                        .build();

        FirebaseRecyclerAdapter<ImageURLModel, ImageURLViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ImageURLModel, ImageURLViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ImageURLViewHolder imageURLViewHolder, int i, @NonNull ImageURLModel imageURLModel) {

                String imageUrl = imageURLModel.getImgUrl();
                imageURLViewHolder.setImageUrl(imageUrl);


            }

            @NonNull
            @Override
            public ImageURLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_class_announcements_imagerecyclerview_item, parent, false);
                return new ImageURLViewHolder(view);

            }
        };


        imgRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }

    @SuppressLint("LongLogTag")
    private void getPostDetails(String PostKey, String classVal) {

        Log.d("values getPostDetails : ", "onDataChange: getPostDetails : " + classxxval + " : postKey : " + post_key);


        databaseReference.child(classVal).child(PostKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassAnnouncementsModel model;
                model = dataSnapshot.getValue(ClassAnnouncementsModel.class);
                // imgUrl = String.valueOf(dataSnapshot.child("imgUrl").getValue());

                String title_text = model.getTitle();
                String desc_text = model.getDesc();
                String author_text = model.getauthor();
                String time = model.getTime();
                String date = model.getDate();


                Log.d("CLASSANNOUNCEMENTS : ", "onDataChange: VAL : " + title);
                //Glide.with(getApplicationContext()).load(imgUrl).into(post_imageView);

                // String title = String.valueOf(model.getTitle());
                titleText.setText(title_text);
                descText.setText(desc_text);
                authorChip.setText(author_text);
                timeTextView.setText(time);
                dateText.setText(date);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ClassAnnouncementsActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                //todoMap.put("author", todo_author);
                //todoMap.put("imgUrl", imgUrl);
                todoMap.put("postKey", post_key);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (preferences.getString("classrep", "XXX").equalsIgnoreCase("yes")) {
            getMenuInflater().inflate(R.menu.post_menu, menu);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_delete_post) {

          /* for (int i = 0 ; i<imageArrayList.size()+1; i++) {
                Log.d(TAG, "onOptionsItemSelected: IMAGES : " + imageArrayList.get(i));
          *//*
             *//*

            }*/
//            Log.d(TAG, "Size: IMAGES : " + imageArrayList.size());


            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(ClassAnnouncementsActivity.this);
            alert.setTitle("Delete this post?");
            alert.setMessage("Are you sure you want to delete '" + title + "' ?");
            alert.setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deletePost();
                        }
                    })
                    .show();


        }

        return super.onOptionsItemSelected(item);
    }

    public void deletePost() {

        databaseReference.child(classxxval).child(post_key).child("imgURLs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {

                    String imageURL = item_snapshot.child("imgUrl").getValue().toString();
                    Log.d("item id ", item_snapshot.child("imgUrl").getValue().toString());
                    StorageReference photoRef = mStorage.getReferenceFromUrl(imageURL);
                    photoRef.delete();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child(classxxval).child(post_key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(ClassAnnouncementsActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();

                Log.d(TAG, "onComplete: Post Deleted successfully : " + post_key);
                startActivity(new Intent(ClassAnnouncementsActivity.this, MainActivity.class));

            }
        });
    }

    public class ImageURLViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public ImageURLViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }


        public void setImageUrl(String imageUrl) {

            PhotoView imageView = mView.findViewById(R.id.imageView_item);
            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);

            imageArrayList.add(imageUrl);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(ClassAnnouncementsActivity.this, "URL : " + imageUrl, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: URL : " + imageUrl);


                    Intent intent = new Intent(ClassAnnouncementsActivity.this, ClassAnnouncement_ImageItem_FullscreenActivity.class);
                    intent.putExtra("imageURL", imageUrl);
                    startActivity(intent);


                }
            });


        }


    }

}
