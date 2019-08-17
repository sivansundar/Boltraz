package com.boltraz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ToDoListActivity extends AppCompatActivity {

    private static final String TAG = "ToDoListActivity";
    public FirebaseDatabase mDatabase;
    public DatabaseReference databaseReference;
    public SharedPreferences preferences;
    public FirebaseAuth mAuth;
    @BindView(R.id.todoList_recyclerView)
    RecyclerView todoListRecyclerView;
    FirebaseRecyclerAdapter<ClassAnnouncementsModel, ToDoViewHolder> firebaseRecyclerAdapter;
    String UID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        todoListRecyclerView = (RecyclerView) findViewById(R.id.todoList_recyclerView);
        todoListRecyclerView.setHasFixedSize(true);
        todoListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        preferences = this.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        getTodoLists();
    }

    private void getTodoLists() {

        UID = mAuth.getUid();

        Query query = databaseReference.child("students").child(UID).child("todos");

        FirebaseRecyclerOptions<ClassAnnouncementsModel> options =
                new FirebaseRecyclerOptions.Builder<ClassAnnouncementsModel>()
                        .setQuery(query, ClassAnnouncementsModel.class)
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
                <ClassAnnouncementsModel, ToDoViewHolder>(
                options) {

            @NonNull
            @Override
            public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todolist_item, parent, false);


                return new ToDoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ToDoViewHolder toDoViewHolder, int i, @NonNull ClassAnnouncementsModel classAnnouncementsModel) {

                String title = classAnnouncementsModel.getTitle();
                toDoViewHolder.getTitle(classAnnouncementsModel.getTitle());
                toDoViewHolder.getDesc(classAnnouncementsModel.getDesc());
                toDoViewHolder.mView.setTag(classAnnouncementsModel.getPostID());

                String postKey = classAnnouncementsModel.getPostKey();
                Log.d(TAG, "onBindViewHolder: postKey : " + postKey);
                toDoViewHolder.delete_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(ToDoListActivity.this);
                        alert.setTitle("Delete your todo item?");
                        alert.setMessage("Are you sure you want to delete '" + title + "' ?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                firebaseRecyclerAdapter.getRef(i).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        View contextView = findViewById(R.id.rootView);


                                        Snackbar snackbar = Snackbar
                                                .make(contextView, "Todo item '" + title + "' was removed successfully", Snackbar.LENGTH_LONG);

                                        snackbar.show();

                                        Toast.makeText(ToDoListActivity.this, "REMOVED ", Toast.LENGTH_SHORT).show();
                                        firebaseRecyclerAdapter.notifyItemRemoved(i);
                                        todoListRecyclerView.invalidate();
                                    }
                                });
                            }
                        });
                        alert.show();
                    }
                });

                // toDoViewHolder.deleteItem(classAnnouncementsModel.getPostID());

                toDoViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ToDoListActivity.this, ClassAnnouncementsActivity.class);
                        intent.putExtra("postKey", postKey);
                        startActivity(intent);
                    }
                });

            }

        };


        todoListRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    public class ToDoViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageView delete_icon;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;


        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            delete_icon = mView.findViewById(R.id.delete_icon);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("students").child(UID).child("todos");


            delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) mView.getTag();
                    Toast.makeText(ToDoListActivity.this, "Click item " + id, Toast.LENGTH_SHORT).show();


                }
            });

        }

        public void getTitle(String title) {
            TextView titleText = mView.findViewById(R.id.todo_title_text);
            titleText.setText(title);
        }

        public void getDesc(String desc) {
            TextView descText = mView.findViewById(R.id.todo_desc_list);
            descText.setText(desc);
        }

        public void deleteItem(String item_ID) {

        }
    }
}
