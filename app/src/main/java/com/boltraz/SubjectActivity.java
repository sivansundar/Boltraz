package com.boltraz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.Model.NoteModel;
import com.boltraz.Model.SubjectModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectActivity extends AppCompatActivity {

    String subjectID;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.ccode_chip)
    Chip ccodeChip;
    @BindView(R.id.credit_chip)
    Chip creditChip;
    @BindView(R.id.notes_recyclerView)
    RecyclerView notesRecyclerView;

    public SharedPreferences sharedPreferences;



    public ProgressDialog progressDialog;

    private static final String TAG = "Boltraz SubjectActivity";

    public long sizeinMb;

    public String format;

    public int semester;

    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

    public String contentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        ButterKnife.bind(this);

        subjectID = getIntent().getStringExtra("subject_id");


        notesRecyclerView = (RecyclerView) findViewById(R.id.notes_recyclerView);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();

        //mStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

        sharedPreferences = this.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);

        semester = sharedPreferences.getInt("semester", 6);


        // Toast.makeText(this, "Subject ID : " + subjectID, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        getSubjectDetails();
        getNotes();

    }

    private void getSubjectDetails() {
        databaseReference.child("subjects/cse/").child(String.valueOf(TextUtils.concat("sem", String.valueOf(semester)))).child(subjectID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SubjectModel subjectModel = dataSnapshot.getValue(SubjectModel.class);

                String title = subjectModel.getTitle().toString();
                String ccode = subjectModel.getCcode().toString();
                int credits = subjectModel.getCredits();

                titleText.setText(TextUtils.concat("", title));
                ccodeChip.setText(TextUtils.concat("", ccode));
                creditChip.setText(TextUtils.concat(String.valueOf(credits), " credits"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getNotes() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("subjects")
                .child("cse")
                .child(String.valueOf(TextUtils.concat("sem", String.valueOf(semester))))
                .child(subjectID)
                .child("notes");

        FirebaseRecyclerOptions<NoteModel> options =
                new FirebaseRecyclerOptions.Builder<NoteModel>()
                        .setQuery(query, NoteModel.class)
                        .build();

        FirebaseRecyclerAdapter<NoteModel, SubjectActivity.NoteViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<NoteModel, SubjectActivity.NoteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SubjectActivity.NoteViewHolder noteViewHolder, int i, @NonNull NoteModel noteModel) {
                noteViewHolder.setFileName(noteModel.getFileName());
                String fileURL = noteModel.getFileURL();
                String fileName = noteModel.getFileName();


                Log.d(TAG, "onBindViewHolder: fileURL :  " + fileURL + "\n FileName : " + fileName);

                noteViewHolder.setFileSize(noteModel.getFileSize());
                noteViewHolder.setfileDesc(noteModel.getFileDescription());

                noteViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_QUICK_VIEW, Uri.parse(fileURL));
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SubjectActivity.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.subject_note_item, parent, false);

                return new NoteViewHolder(view);
            }
        };

        notesRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FirebaseStorage mStorage;



        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            mStorage = FirebaseStorage.getInstance();
        }

        public void setFileName(String filename) {
            TextView fileName_textView = mView.findViewById(R.id.fileName_text);
            fileName_textView.setText(filename);
        }

        public void setfileDesc(String desc) {
            TextView mDesctext = (TextView) mView.findViewById(R.id.description_text_list);
            mDesctext.setText(String.valueOf(desc));
        }

        public void setFileSize(String fileSize) {
            TextView fileSize_textView = mView.findViewById(R.id.fileSize_text);
            fileSize_textView.setText(TextUtils.concat("", fileSize));
        }

    }
}
