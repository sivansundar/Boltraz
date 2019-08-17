package com.boltraz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.Model.ClassAnnouncementsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssignmentListActivity extends AppCompatActivity {

    private static final String TAG = "AssignmentListActivity";
    public FirebaseDatabase mDatabase;
    public DatabaseReference databaseReference;
    public SharedPreferences preferences;
    public FirebaseAuth mAuth;
    @BindView(R.id.assignment_recyclerView)
    RecyclerView assignmentRecyclerView;
    String UID = "";
    String classxx = "";
    private FirebaseRecyclerAdapter<ClassAnnouncementsModel, AssignmentViewHolder> firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);
        ButterKnife.bind(this);


        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        assignmentRecyclerView = (RecyclerView) findViewById(R.id.assignment_recyclerView);
        assignmentRecyclerView.setHasFixedSize(true);
        assignmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        preferences = this.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);


    }

    @Override
    protected void onStart() {
        super.onStart();

        getAssignmentList();
    }

    private void getAssignmentList() {

        UID = mAuth.getUid();
        classxx = preferences.getString("classxx", "CCC");

        Log.d(TAG, "getAssignmentList: CLASS : " + classxx);

        Query query = databaseReference.child("Assignments").child(classxx);

        FirebaseRecyclerOptions<ClassAnnouncementsModel> options =
                new FirebaseRecyclerOptions.Builder<ClassAnnouncementsModel>()
                        .setQuery(query, ClassAnnouncementsModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClassAnnouncementsModel, AssignmentViewHolder>(options) {
            @NonNull
            @Override
            public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.assignmentlist_item, parent, false);


                return new AssignmentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AssignmentViewHolder assignmentViewHolder, int i, @NonNull ClassAnnouncementsModel model) {

                String title = model.getTitle();
                assignmentViewHolder.getTitle(title);
                assignmentViewHolder.getDesc(model.getDesc());
                assignmentViewHolder.mView.setTag(model.getPostID());

                assignmentViewHolder.delete_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(AssignmentListActivity.this);
                        alert.setTitle("Delete assignment?");
                        alert.setMessage("Are you sure you want to delete '" + title + "' ?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseRecyclerAdapter.getRef(i).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        View contextView = findViewById(R.id.rootView);


                                        Snackbar snackbar = Snackbar
                                                .make(contextView, "Assignment '" + title + "' was removed successfully", Snackbar.LENGTH_LONG);

                                        snackbar.show();

                                        firebaseRecyclerAdapter.notifyItemRemoved(i);

                                    }
                                });
                            }
                        });
                        alert.show();


                    }
                });

                assignmentViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        };

        assignmentRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    public class AssignmentViewHolder extends RecyclerView.ViewHolder {

        View mView;


        ImageView delete_icon;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            delete_icon = mView.findViewById(R.id.delete_icon);

        }

        public void getTitle(String title) {
            TextView titleText = mView.findViewById(R.id.assignment_title_text);
            titleText.setText(title);
        }

        public void getDesc(String desc) {
            TextView descText = mView.findViewById(R.id.assignment_desc_list);
            descText.setText(desc);
        }
    }
}
