package com.boltraz;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

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

    ChooserDialog chooserDialog;
    @BindView(R.id.filesHeaderText)
    TextView filesHeaderText;


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

        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ImageListAdapter adapter = new ImageListAdapter(uriArrayList);
        imageRecyclerView.setAdapter(adapter);


        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FilesAdapter filesAdapter = new FilesAdapter(filesArrayList);
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
                PickImageDialog.build(setup, new IPickResult() {

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
                }).show(getSupportFragmentManager());
            }
        });


        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                chooserDialog = new ChooserDialog(AddAnnouncementActivity.this, R.style.FileChooserStyle_Dark)
                        //.withFilter(true, false)
                        .withStartFile("/sdcard")
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

                                filesArrayList.add(new FileList_AddFiles(pathFile.getName(), Uri.fromFile(pathFile)));


                                for (int i = 0; i < filesArrayList.size(); i++) {

                                    Log.d(TAG, "\n\n\nonChoosePath: filesArrayList : " + filesArrayList.get(i).getList_fileName() + "\n ");
                                }

                                //filesArrayList.remove(filesArrayList.size()-1);
                                filesAdapter.notifyDataSetChanged();

                                Log.d(TAG, "onChoosePath: SIZE : " + filesArrayList.size());

                                //  filesArrayList.remove(filesArrayList.size()-1);

                                Log.d(TAG, "onChoosePath: SIZE after removal : " + filesArrayList.size());

                                for (int i = 0; i < filesArrayList.size(); i++) {
                                    Log.d(TAG, "onChoosePath: FILES : \n " + i + " : " + filesArrayList.get(i).getList_fileName() + "\n : " + filesArrayList.get(i).getFile_uri());
                                }


                            }
                        })
                        .build()
                        .show();

            }


        });


    }
}
