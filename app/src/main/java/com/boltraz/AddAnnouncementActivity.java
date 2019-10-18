package com.boltraz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.ListAdapter.FilesAdapter;
import com.boltraz.ListAdapter.ImageListAdapter;
import com.boltraz.Model.FileList_AddFiles;
import com.boltraz.Model.ImageViews_AddAnnouncement;
import com.codekidlabs.storagechooser.StorageChooser;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.enums.EPickType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddAnnouncementActivity extends AppCompatActivity {

    private static final String TAG = "AddAnnouncementActivity : ";
    @BindView(R.id.announ_type_spinner)
    MaterialSpinner announTypeSpinner;
    @BindView(R.id.image_recyclerView)
    RecyclerView imageRecyclerView;
    ArrayList<ImageViews_AddAnnouncement> uriArrayList;
    ArrayList<FileList_AddFiles> filesArrayList;
    ArrayList<FileList_AddFiles> filesTempList;

    FilesAdapter filesAdapter;
    ImageListAdapter adapter;

    String classx = "";
    String annountype = "None";
    String username = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase mDatabase;
    public StorageReference mStorage;

    @BindView(R.id.add_image_btn)
    MaterialButton addImageBtn;
    PickSetup setup;
    @BindView(R.id.files_recyclerView)
    RecyclerView filesRecyclerView;
    @BindView(R.id.announ_title_edittext)
    EditText announTitleEdittext;
    @BindView(R.id.announ_desc_editText)
    EditText announDescEditText;
    @BindView(R.id.add_file_btn)
    MaterialButton addFileBtn;

    @BindView(R.id.filesHeaderText)
    TextView filesHeaderText;
    @BindView(R.id.post_btn)
    Button postBtn;
    DatabaseReference databaseReference;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);

        sharedPreferences = this.getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getSharedPrefValues();

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        String[] announ_type = {"None", "Assignment"};


        announTypeSpinner.setItems(announ_type);
        announTypeSpinner.setTextColor(Color.WHITE);
        announTypeSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Toast.makeText(AddAnnouncementActivity.this, "View : " + view.getText().toString(), Toast.LENGTH_SHORT).show();

                if (view.getText().toString().equalsIgnoreCase("Assignment")) {
                    annountype = "Assignment";
                    addFileBtn.setVisibility(View.VISIBLE);
                    filesHeaderText.setVisibility(View.VISIBLE);
                } else {
                    annountype = "None";
                    addFileBtn.setVisibility(View.GONE);
                    filesHeaderText.setVisibility(View.GONE);
                }
            }
        });
        uriArrayList = new ArrayList<ImageViews_AddAnnouncement>();
        filesArrayList = new ArrayList<FileList_AddFiles>();
        filesTempList = new ArrayList<>();

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImageListAdapter(uriArrayList);
        imageRecyclerView.setAdapter(adapter);


        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        filesAdapter = new FilesAdapter(filesArrayList);
        filesRecyclerView.setAdapter(filesAdapter);


        //Image picker API
        setup = new PickSetup()
                .setTitle("Title")
                .setTitleColor(Color.BLACK)
                .setBackgroundColor(Color.WHITE)
                .setProgressText("On it")
                .setProgressTextColor(Color.GREEN)
                .setCancelText("Cancel")
                .setFlip(true)
                .setMaxSize(500)
                .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                .setCameraButtonText("Camera")
                .setGalleryButtonText("Gallery")
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayout.VERTICAL)
                .setSystemDialog(false);


        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(AddAnnouncementActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build();

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.create(AddAnnouncementActivity.this) // Activity or Fragment
                        .start();


                /*Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                imagePickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(imagePickerIntent, 1001);*/

                /*PickImageDialog.build(setup, new IPickResult() {

                    @SuppressLint("LongLogTag")
                    @Override
                    public void onPickResult(PickResult pickResult) {
                        if (pickResult.getError() == null) {
                            //  Toast.makeText(getContext(), "URI : " + pickResult.getUri().toString(), Toast.LENGTH_SHORT).show();
                            //uriArrayList.add(pickResult.getUri());
                            uriArrayList.add(new ImageViews_AddAnnouncement(pickResult.getUri()));
                            adapter.notifyDataSetChanged();

                            Log.d(TAG, "onPickResult: LIST URI : " + uriArrayList.toString());
                            //uploadImage = pickResult.getUri();

                        }
                    }
                }).show(getSupportFragmentManager());*/
            }
        });


        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                startActivityForResult(intent, 1002);


                /*chooserDialog = new ChooserDialog(AddAnnouncementActivity.this, R.style.FileChooserStyle_Dark)
                        //.withFilter(true, false)
                        .withStartFile(Environment.getExternalStorageState())
                        .enableMultiple(true)
                        .withOnBackPressedListener(new ChooserDialog.OnBackPressedListener() {
                            @Override
                            public void onBackPressed(AlertDialog dialog) {
                                chooserDialog.goBack();
                            }
                        })
                        // to handle the result(s)
                        .withChosenListener(new ChooserDialog.Result() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                Toast.makeText(getApplicationContext(), "FOLDER: " + path, Toast.LENGTH_SHORT).show();


                                Log.d(TAG, "onChoosePath: : File List : " + pathFile.getName() + "\n ");

                                //  FileList_AddFiles fileObj = new FileList_AddFiles(pathFile.getName(), Uri.fromFile(pathFile));

                                filesTempList.add(new FileList_AddFiles(pathFile.getName(), Uri.fromFile(pathFile)));
                             //   filesArrayList.add(filesTempList.get(filesTempList.size()-1));
                                filesArrayList.add(new FileList_AddFiles(pathFile.getName(), Uri.fromFile(pathFile)));


                                for (int i = 0; i < filesArrayList.size(); i++) {

                                    Log.d(TAG, "\n\n\nonChoosePath: filesArrayList : " + filesArrayList.get(i).getList_fileName() + "\n ");
                                }

                                //filesArrayList.remove(filesArrayList.size()-1);
                                filesAdapter.notifyDataSetChanged();

                                Log.d(TAG, "onChoosePath: SIZE : " + filesArrayList.size());


                            }
                        })
                        .build()
                        .show();
*/
            }


        });


        postBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "PostButton");

                String title = announTitleEdittext.getText().toString();
                String desc = announDescEditText.getText().toString();
                Log.d(TAG, "onClick: CLASSX : ALERT DIALOG" + classx);

                if (title.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Your Title or Description is missing. Say something!", Toast.LENGTH_LONG).show();
                } else {

                    String post_key = databaseReference.child("classAnnouncements").child(classx).push().getKey();
                    Toast.makeText(AddAnnouncementActivity.this, "POST KEY : " + post_key, Toast.LENGTH_SHORT).show();

                    // Figure out hashmaps
                    HashMap<String, Object> postValues = new HashMap<String, Object>();
                    postValues.put("title", title);
                    postValues.put("desc", desc);
                    postValues.put("author", username);
                    postValues.put("postID", post_key);
                    postValues.put("type", annountype);
                    postValues.put("date", getDate());
                    postValues.put("time", getTime());

                    Log.d(TAG, "onClick: ANNOUN TYPE : " + annountype);


                    //Post content with either images or files or both.

                    startPostUpload(postValues, post_key, classx, annountype);


                }

                for (int i = 0; i < filesArrayList.size() - 1; i++) {
                    Log.d(TAG, filesArrayList.get(i).getList_fileName() + " " + filesArrayList.get(i).getFile_uri() + "\n");

                }


            }
        });


    }

    private void startPostUpload(HashMap<String, Object> postValues, String key, String classx, String announcement_type) {
        mProgressDialog.setMessage("Posting....");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        if (announcement_type.equalsIgnoreCase("assignment")) {
            databaseReference.child("Assignments").child(classx).child(key).setValue(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        if (uriArrayList.isEmpty()) {
                            //Toast.makeText(getContext(), "You got some images boss", Toast.LENGTH_SHORT).show();
                            startImageUpload(key, classx, announcement_type);
                        }
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Post added successfully." + key, Toast.LENGTH_SHORT).show();


                        mProgressDialog.dismiss();

                    } else {
                        mProgressDialog.dismiss();
                    }
                }
            });
        } else {
            databaseReference.child("classAnnouncements").child(classx).child(key).setValue(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Post added successfully." + key, Toast.LENGTH_SHORT).show();

                        if (uriArrayList.isEmpty()) {
                            //Images and file upload goes here.
                            Toast.makeText(getApplicationContext(), "You got some images boss", Toast.LENGTH_SHORT).show();
                            startImageUpload(key, classx, announcement_type);
                        }

                        if (!filesArrayList.isEmpty()) {

                        }

                        mProgressDialog.dismiss();

                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Could not add this post. ERROR : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload failed! : " + e, Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

    @SuppressLint("LongLogTag")
    private void startImageUpload(String post_key, String classxxxVal, String announcement_type) {

        //  Log.d(TAG, "startImageUpload: Title : " + title_text + ": \n Desc : " + desc_text);
        mProgressDialog.setMessage("Posting...");
        mProgressDialog.show();

        if (announcement_type.equalsIgnoreCase("Assignment")) {
            StorageReference filePath = mStorage.child("Assignment").child(classxxxVal).child(post_key);
            //Log.d(TAG, "startPosting: Upload image : " + uploadImage.toString());


            /*
            ##### Image upload #####

            for (int i = 0; i < uriArrayList.size(); i++) {
                // work on multiple uploads. ImageURL in the database should have another node for itself like the todos list.

                Uri imgfile = uriArrayList.get(i).getImageButtonUri();

                Log.d(TAG, "startImageUpload: IMAGE URI AT uriList[" + i + "] is : " + imgfile.toString());
                filePath.child(imgfile.getLastPathSegment()).putFile(imgfile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.child(imgfile.getLastPathSegment()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().toString();

                                        Log.d(TAG, "onSuccess: downloadUrl : " + downloadUrl);

                                        HashMap<String, Object> downloadurlhash = new HashMap<>();
                                        downloadurlhash.put("imgUrl", downloadUrl);


                                        databaseReference.child("Assignments").child(classxxxVal).child(post_key).child("imgURLs").push().setValue(downloadurlhash)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "onSuccess: Download url's updated.");

                                                        // Toast.makeText(getContext(), "Download URLs updated : " + post_key, Toast.LENGTH_SHORT).show();



                                                        mProgressDialog.dismiss();

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "FAILED : " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            //String downloadUrl = filePath.getDownloadUrl().toString();


                        } else {
                            Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: FAILED : ", task.getException());
                        }
                    }
                });

            }*/

            for (int i = 0; i < filesArrayList.size(); i++) {

                Uri fileUri = filesArrayList.get(i).getFile_uri();
                String filename = filesArrayList.get(i).getList_fileName();

                //FilePath
                filePath.child("Files").child(fileUri.getLastPathSegment()).putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //Retrieve File URL
                            filePath.child("Files").child(fileUri.getLastPathSegment()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String downloadURL = task.getResult().toString();
                                        Log.d(TAG, "onComplete: DOWNLOAD URL of " + filename + " is : " + downloadURL);
                                    } else {
                                        Log.d(TAG, "onComplete: FAILED! : " + task.getException().getLocalizedMessage());
                                    }
                                }
                            });

                        } else if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: FAILED to upload file : " + task.getException().getLocalizedMessage());
                        }
                    }
                });
            }


        } else {
            StorageReference filePath = mStorage.child("classAnnouncements").child(classxxxVal).child(post_key);
            // Log.d(TAG, "startPosting: Upload image : " + uploadImage.toString());


            for (int i = 0; i < uriArrayList.size(); i++) {
                // work on multiple uploads. ImageURL in the database should have another node for itself like the todos list.

                Uri imgfile = uriArrayList.get(i).getImageButtonUri();

                Log.d(TAG, "startImageUpload: IMAGE URI AT uriList[" + i + "] is : " + imgfile.toString());
                filePath.child(imgfile.getLastPathSegment()).putFile(imgfile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.child(imgfile.getLastPathSegment()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().toString();

                                        Log.d(TAG, "onSuccess: downloadUrl : " + downloadUrl);


                                        HashMap<String, Object> downloadurlhash = new HashMap<>();
                                        downloadurlhash.put("imgUrl", downloadUrl);


                                        databaseReference.child("classAnnouncements").child(classxxxVal).child(post_key).child("imgURLs").push().setValue(downloadurlhash)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @SuppressLint("LongLogTag")
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "onSuccess: Download url's updated.");

                                                        // Toast.makeText(getContext(), "Download URLs updated : " + post_key, Toast.LENGTH_SHORT).show();


                                                        mProgressDialog.dismiss();

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "FAILED : " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: FAILED : ", task.getException());
                        }
                    }
                });

            }
        }


        uriArrayList.clear();

    }


    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = simpleDateFormat.format(calendar.getTime());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 4; i++) {

            sb.append(time.charAt(i));


        }

        String currentTime = sb.toString();

        return currentTime;
    }

    @SuppressLint("LongLogTag")
    private String getDate() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.d(TAG, "onClick: DATE : " + date);

        return date;
    }

    private void getSharedPrefValues() {

        classx = sharedPreferences.getString("classxx", "SSS");
        username = sharedPreferences.getString("name", "XXX");

    }


    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);

            File file;
            for (int i = 0; i < images.size(); i++) {
                file = new File(images.get(1).getPath());
                Log.d(TAG, "onActivityResult: IMAGE LIST : " + Uri.fromFile(new File(images.get(i).getPath())) + "\n");
                uriArrayList.add(new ImageViews_AddAnnouncement(Uri.fromFile(new File(images.get(i).getPath()))));
                adapter.notifyDataSetChanged();
            }
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == 1002) {
                if (data != null) {
                    if (data.getClipData() != null) {
                        // Getting the length of data and logging up the logs using index
                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {

                            // Getting the URIs of the selected files and logging them into logcat at debug level
                            Uri uri = data.getClipData().getItemAt(index).getUri();
                            String filename = "000";
                            String displayName = "";
                            File myFile = new File(uri.toString());


                            Log.d(TAG, "onActivityResult: filePath : " + uri.getPath());

                            if (uri.toString().startsWith("content://")) {
                                Cursor cursor = null;
                                try {
                                    cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
                                    if (cursor != null && cursor.moveToFirst()) {
                                        filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (uri.toString().startsWith("file://")) {
                                filename = myFile.getName();

                            }

                            Log.d(TAG, "\nonActivityResult: DISPLAY NAME " + displayName);
                            Log.d(TAG, "\nonActivityResult: DISPLAY NAME " + displayName);


                            filesArrayList.add(new FileList_AddFiles(filename, uri));

                            Log.d("filesUri [" + uri + "] : ", String.valueOf(uri) + " \nFile Name : " + filename + "\n\n");
                        }
                    } else {

                        // Getting the URI of the selected file and logging into logcat at debug level
                        Uri uri = data.getData();
                        String filenamesingle = "111";
                        filesArrayList.add(new FileList_AddFiles(filenamesingle, uri));

                        Log.d("filesUri [" + uri + "] : ", String.valueOf(uri) + " \nFile Name : " + filenamesingle + "\n\n");
                    }


                    filesAdapter.notifyDataSetChanged();

                }
            }
        }
    }



}
