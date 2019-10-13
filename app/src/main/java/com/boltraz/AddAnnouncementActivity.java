package com.boltraz;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.ListAdapter.FilesAdapter;
import com.boltraz.ListAdapter.ImageListAdapter;
import com.boltraz.Model.FileList_AddFiles;
import com.boltraz.Model.ImageViews_AddAnnouncement;
import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.button.MaterialButton;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.enums.EPickType;

import java.io.File;
import java.util.ArrayList;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
        ButterKnife.bind(this);

        String[] announ_type = {"None", "Assignment"};

        announTypeSpinner.setItems(announ_type);
        announTypeSpinner.setTextColor(Color.WHITE);
        announTypeSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Toast.makeText(AddAnnouncementActivity.this, "View : " + view.getText().toString(), Toast.LENGTH_SHORT).show();

                if (view.getText().toString().equalsIgnoreCase("Assignment")) {
                    addFileBtn.setVisibility(View.VISIBLE);
                    filesHeaderText.setVisibility(View.VISIBLE);
                } else {
                    addFileBtn.setVisibility(View.GONE);
                    filesHeaderText.setVisibility(View.GONE);
                }
            }
        });
        uriArrayList = new ArrayList<ImageViews_AddAnnouncement>();
        filesArrayList = new ArrayList<FileList_AddFiles>();
        filesTempList = new ArrayList<>();

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ImageListAdapter adapter = new ImageListAdapter(uriArrayList);
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


                Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imagePickerIntent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                imagePickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(imagePickerIntent, 1001);

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

                for (int i = 0; i < filesArrayList.size() - 1; i++) {
                    Log.d(TAG, filesArrayList.get(i).getList_fileName() + " " + filesArrayList.get(i).getFile_uri() + "\n");

                }
            }
        });


    }


    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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
