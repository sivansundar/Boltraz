package com.boltraz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.boltraz.Model.SubjectModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

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


    public ProgressDialog progressDialog;

    private static final String TAG = "Boltraz SubjectActivity";

    public long sizeinMb;

    public String format;

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


        Toast.makeText(this, "Subject ID : " + subjectID, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        getSubjectDetails();
        getNotes();

    }

    private void getSubjectDetails() {
        databaseReference.child("subjects/cse/sem7").child(subjectID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SubjectModel subjectModel = dataSnapshot.getValue(SubjectModel.class);

                titleText.setText("" + subjectModel.getTitle());
                ccodeChip.setText("" + subjectModel.getCcode());
                creditChip.setText("" + subjectModel.getCredits() + " credits");
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
                .child("sem7")
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

                noteViewHolder.getMetaData(fileURL);





                /*noteViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: mView : File name : " + fileRef.getName());

                        fileRef.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                            @Override
                            public void onComplete(@NonNull Task<StorageMetadata> task) {
                                if (task.isSuccessful()) {

                                    String formatType = "";
                                    sizeinMb = ( Math.round((task.getResult().getSizeBytes()/ (1024 * 1024)) * 10) / 10);
                                    String contentType = fileRef.getName();
                                    Log.d(TAG, "onComplete: FileName : " + contentType);
                                    if (contentType.contains(".pdf")) {
                                        formatType = "pdf";
                                    }
                                    if (contentType.contains(".docx")) {
                                        formatType = "docx";
                                    }
                                    Log.d(TAG, "onComplete: File format : " + formatType);
                                    Log.d(TAG, "onComplete: Size in MB : " + sizeinMb);


                                }
                            }
                        });

                        if (sizeinMb>15) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SubjectActivity.this);
                            alert.setTitle("Warning");
                            alert.setMessage("This file is greater than 15 MB. Are you sure you want to download this file?");
                            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //startDownload(fileRef, fileName,);


                                }
                            });

                            alert.setNegativeButton("View the file", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_QUICK_VIEW);
                                    intent.setData(Uri.parse(fileURL));
                                    startActivity(intent);
                                }
                            });
                            alert.show();

                        }


                        }

                });*/
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

        public StorageReference fileRef;
        public FirebaseStorage mStorage;


        public String fileName;
        public long sizeinBytes;
        public String fileFormat;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            mStorage = FirebaseStorage.getInstance();
        }

        public void setFileName(String filename) {
            TextView fileName_textView = mView.findViewById(R.id.fileName_text);
            fileName_textView.setText(filename);
        }

        public void setFileSize(String fileSize) {
            TextView fileSize_textView = mView.findViewById(R.id.fileSize_text);
            fileSize_textView.setText("" + fileSize);
        }

        public void getMetaData(String referenceFromUrl) {
            fileRef = mStorage.getReferenceFromUrl(referenceFromUrl);

            fileRef.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                @Override
                public void onComplete(@NonNull Task<StorageMetadata> task) {
                    Log.d(TAG, "onComplete: From ViewHolder : FileName : " + task.getResult().getName() + "\n FileSize : " + task.getResult().getSizeBytes()
                            + "\n FileURL : " + referenceFromUrl);

                    fileName = task.getResult().getName();
                    sizeinBytes = task.getResult().getSizeBytes();

                    if (fileName.contains(".pdf")) {
                        fileFormat = "pdf";
                    }

                    if (fileName.contains(".docx")) {
                        fileFormat = "docx";
                    }
                    int sizeinMb = ( Math.round((task.getResult().getSizeBytes()/ (1024 * 1024)) * 10) / 10);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                            if (sizeinMb>15) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(SubjectActivity.this);
                                alert.setTitle("Warning");
                                alert.setMessage("This file is greater than 15 MB. Are you sure you want to download this file?");
                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            startDownload(fileRef, fileName,fileFormat);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });

                                alert.setNegativeButton("View the file", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_QUICK_VIEW);
                                        intent.setData(Uri.parse(referenceFromUrl));
                                        startActivity(intent);
                                    }
                                });
                                alert.show();

                            }

                            else {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_QUICK_VIEW);
                                intent.setData(Uri.parse(referenceFromUrl));
                                startActivity(intent);
                            }
                        }
                    });


                }
            });

        }

    }
    public void startDownload(StorageReference fileRef, String fileName, String format) throws IOException {
        Log.d(TAG, "startDownload: File details : " + fileName + "\n " + format);

        File localFile = File.createTempFile(fileName, "pdf");



        fileRef.getFile(localFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                int progress = ( Math.round((taskSnapshot.getBytesTransferred()/ (1024 * 1024)) * 10) / 10);
                progressDialog.setTitle("Downloading");
                progressDialog.setMessage(progress + "% downloaded...");
                progressDialog.show();



            }
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                progressDialog.dismiss();
            }
        });

    }
}
