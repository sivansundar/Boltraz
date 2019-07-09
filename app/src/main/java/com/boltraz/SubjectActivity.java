package com.boltraz;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.Model.NoteModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

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

    public FirebaseStorage mStorage;

    private static final String TAG = "Boltraz SubjectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        ButterKnife.bind(this);

        subjectID = getIntent().getStringExtra("subject_id");

        notesRecyclerView = (RecyclerView) findViewById(R.id.notes_recyclerView);
        notesRecyclerView.setHasFixedSize(true);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mStorage = FirebaseStorage.getInstance();

        Toast.makeText(this, "Subject ID : " + subjectID, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        getNotes();

    }

    private void getNotes() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("subjects")
                .child("cse")
                .child("sem7")
                .child(subjectID)
                .child("notes");

        FirebaseRecyclerOptions<NoteModel> options =
                new FirebaseRecyclerOptions.Builder<NoteModel>()
                        .setQuery(query, NoteModel.class)
                        .build();

        FirebaseRecyclerAdapter<NoteModel, SubjectActivity.NoteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoteModel, SubjectActivity.NoteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SubjectActivity.NoteViewHolder noteViewHolder, int i, @NonNull NoteModel noteModel) {
                noteViewHolder.setFileName(noteModel.getFileName());

                String fileURL = noteModel.getFileURL();
                Log.d(TAG, "onBindViewHolder: fileURL :  " + fileURL);
               StorageReference fileRef = mStorage.getReferenceFromUrl(fileURL);
                fileRef.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                    @Override
                    public void onComplete(@NonNull Task<StorageMetadata> task) {
                        if (task.isSuccessful()) {
                            long size = task.getResult().getSizeBytes();
                            long sizeinMb = size / (1024 * 1024);
                            noteViewHolder.setFileSize(sizeinMb);
                        }
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

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFileName(String filename) {
            TextView fileName_textView = mView.findViewById(R.id.fileName_text);
            fileName_textView.setText(filename);
        }

        public void setFileSize(long fileSize) {
            TextView fileSize_textView = mView.findViewById(R.id.fileSize_text);
            fileSize_textView.setText("" + fileSize + " MB");
        }

    }
}
