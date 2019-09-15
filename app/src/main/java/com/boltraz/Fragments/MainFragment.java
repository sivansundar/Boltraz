package com.boltraz.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.AssignmentListActivity;
import com.boltraz.ClassAnnouncementsActivity;
import com.boltraz.ListAdapter.ImageListAdapter;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.Model.ImageViews_AddAnnouncement;
import com.boltraz.Model.UserModel;
import com.boltraz.R;
import com.boltraz.ToDoListActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public RecyclerView classAnnouncementsRecyclerView;
    @BindView(R.id.userdp_circleImageView)
    CircularImageView circleImageView;
    public UserModel user;
    @BindView(R.id.toDoNumber_label)
    TextView todoNumber_label;
    @BindView(R.id.name_label)
    TextView labelName;
    @BindView(R.id.todoList_holder)
    MaterialCardView todoListHolder;
    @BindView(R.id.assignments_holder)
    MaterialCardView assignmentsHolder;
    @BindView(R.id.assignmentNumber_label)
    TextView assignmentNumberLabel;


    private ArrayList<ImageViews_AddAnnouncement> uriArrayList;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @BindView(R.id.addAnnouncement_fab)
    FloatingActionButton addAnnouncement_fab;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "Boltraz MainActivity";
    String userID;
    String name = "";
    public Uri uploadImage;

    public PickSetup setup;
    public StorageReference mStorage;
    public Query query_announcements;


    int todoSize = 0;
    int assignmentSize;

    public ChooserDialog chooserDialog;

    String announcement_type;
    String CLASSXX = "";


    public FirebaseDatabase mDatabase;
    public DatabaseReference databaseReference;
    View rootView;
    private ProgressDialog mProgressBar;

    private Unbinder mUnbinder;
    String[] classxxarr = {"Class7A", "Class7B", "Class7C"};

    String classxxVal = "";

    String classxx;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: Fragment home view destroyed");
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Fragment home destroyed");
        super.onDestroy();
    }


    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View customLayout = getLayoutInflater().inflate(R.layout.add_announcement_view, null);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        userID = mAuth.getUid();

        uriArrayList = new ArrayList<ImageViews_AddAnnouncement>();

        classAnnouncementsRecyclerView = (RecyclerView) rootView.findViewById(R.id.classAnnouncements_RecyclerView);
        classAnnouncementsRecyclerView.setHasFixedSize(true);
        classAnnouncementsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        todoNumber_label = rootView.findViewById(R.id.toDoNumber_label);

        mUnbinder = ButterKnife.bind(this, rootView);
        // mUnbinder2 = ButterKnife.bind(this, customLayout);

        todoNumber_label.setText("" + todoSize);
        labelName.setText("" + name);

        mProgressBar = new ProgressDialog(getContext());

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

        sharedPreferences = getContext().getSharedPreferences("sharedpref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        todoListHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "TodoList", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getContext(), ToDoListActivity.class));
            }
        });

        assignmentsHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Assignment", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), AssignmentListActivity.class));


            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


        databaseReference.child("students").child(userID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                String classrep = userModel.getClassrep();
                CLASSXX = "Class" + userModel.getClasssection();

                String usn = userModel.getUSN();

                String username = userModel.getName();

                String dp_url = userModel.getDp_url();

                String dept = userModel.getDept();

                String email = userModel.getEmail();

                int semester = userModel.getSemester();

                // The shared prefs work perfectly fine. Use that to save values.
                String classxx = "Class" + userModel.getClasssection();


                editor.putString("name", username);
                editor.putString("classxx", classxx);
                editor.putString("usn", usn);
                editor.putString("dp_url", dp_url);
                editor.putString("dept", dept);
                editor.putString("email", email);
                editor.putInt("semester", semester);
                editor.apply();


                Log.d(TAG, "onDataChange: CLASSXX Values : " + classxxVal);

                refreshAnnouncements(sharedPreferences.getString("classxx", "XXX"));


                addAnnouncement_fab = rootView.findViewById(R.id.addAnnouncement_fab);

                Log.d(TAG, "onDataChange: " + classrep + " : CLASSXX : " + classxx);

                if (classrep.equalsIgnoreCase("yes")) {
                    addAnnouncement_fab.setVisibility(View.VISIBLE);
                } else {
                    addAnnouncement_fab.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        getToDoCount();
        getAssignmentCount();

        getDP();


    }

    private void getToDoCount() {


        databaseReference.child("students").child(userID).child("todos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoSize = (int) dataSnapshot.getChildrenCount();


                todoNumber_label.setText("" + todoSize);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getAssignmentCount() {

        String classxxx = sharedPreferences.getString("classxx", "XXX");

        databaseReference.child("Assignments").child(classxxx).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignmentSize = (int) dataSnapshot.getChildrenCount();

                if (assignmentSize == 0) {
                    assignmentNumberLabel.setText("0");

                } else {


                    assignmentNumberLabel.setText(String.valueOf(assignmentSize));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDP() {

       /* Uri url = mAuth.getCurrentUser().getPhotoUrl();
        Glide.with(getContext()).load(url).into(circleImageView);*/

        if (name.isEmpty()) {
            mProgressBar.setMessage("Loading");
            mProgressBar.setCancelable(false);
            mProgressBar.show();

            databaseReference.child("students").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    user = dataSnapshot.getValue(UserModel.class);


                    //Toast.makeText(getContext(), "Class rep : " + classrep, Toast.LENGTH_SHORT).show();
                    name = user.getName();
                    labelName.setText(name);

                    mProgressBar.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mProgressBar.dismiss();

                    Toast.makeText(getContext(), "We have a problem! Check the log", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onCancelled: getDP() : ", databaseError.toException());
                }
            });
        }


    }

    public void refreshAnnouncements(String string) {

        Log.d(TAG, "refreshAnnouncements: " + CLASSXX);


        String classxx = sharedPreferences.getString("classxx", "XXX");

        Log.d(TAG, "refreshAnnouncements: classxx : " + string);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("classAnnouncements")
                .child(string);


        FirebaseRecyclerOptions<ClassAnnouncementsModel> options =
                new FirebaseRecyclerOptions.Builder<ClassAnnouncementsModel>()
                        .setQuery(query, ClassAnnouncementsModel.class)
                        .build();

        FirebaseRecyclerAdapter<ClassAnnouncementsModel, ClassAnnouncementsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
                <ClassAnnouncementsModel, ClassAnnouncementsViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull ClassAnnouncementsViewHolder classAnnouncementsViewHolder, int i, @NonNull ClassAnnouncementsModel timetableModel) {

                String post_key = timetableModel.getPostID();
                String title = timetableModel.getTitle();
                String desc = timetableModel.getDesc();
                String author = timetableModel.getauthor();
                String imageUrl = timetableModel.getImgUrl();

                classAnnouncementsViewHolder.setTitle(timetableModel.getTitle());
                classAnnouncementsViewHolder.setDescription(timetableModel.getDesc());

                classAnnouncementsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(getContext(), ClassAnnouncementsActivity.class);
                        intent.putExtra("title", title);
                        intent.putExtra("desc", desc);
                        intent.putExtra("author", author);
                        intent.putExtra("classxx", string);
                        intent.putExtra("imageUrl", imageUrl);
                        intent.putExtra("postID", post_key);

                        Log.d(TAG, "onClick: IMAGE URL OnClick : " + imageUrl);
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public ClassAnnouncementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.classanoun_list_item, parent, false);
                return new ClassAnnouncementsViewHolder(view);
            }
        };

        classAnnouncementsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @OnClick(R.id.addAnnouncement_fab)
    public void onAddAnnouncement_fabClicked() {
        //TODO: add click handling

        View customLayout = getLayoutInflater().inflate(R.layout.add_announcement_view, null);
        MaterialButton add_image_btn = (MaterialButton) customLayout.findViewById(R.id.add_image_btn);
        MaterialButton add_file_btn = customLayout.findViewById(R.id.add_file_btn);

        ImageButton imageButton = customLayout.findViewById(R.id.add_imageButton);

        Spinner announ_type_spinner = customLayout.findViewById(R.id.announ_type_spinner);
        RecyclerView image_recyclerView = customLayout.findViewById(R.id.image_recyclerView);


        image_recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        ImageListAdapter adapter = new ImageListAdapter(uriArrayList);
        image_recyclerView.setAdapter(adapter);


        String[] announ_type = {"None", "Assignment"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, announ_type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        announ_type_spinner.setAdapter(arrayAdapter);

        announ_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Spinner Value : " + announ_type[position], Toast.LENGTH_SHORT).show();

                announcement_type = announ_type[position];

                if (TextUtils.equals(announcement_type, "Assignment")) {
                    add_file_btn.setVisibility(View.VISIBLE);
                } else {
                    add_file_btn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PickImageDialog.build(setup, new IPickResult() {
                    @Override
                    public void onPickResult(PickResult pickResult) {
                        if (pickResult.getError() == null) {
                            //  Toast.makeText(getContext(), "URI : " + pickResult.getUri().toString(), Toast.LENGTH_SHORT).show();
                            //uriArrayList.add(pickResult.getUri());
                            uriArrayList.add(new ImageViews_AddAnnouncement(pickResult.getUri()));

                            Log.d(TAG, "onPickResult: LIST URI : " + uriArrayList.toString());
                            uploadImage = pickResult.getUri();
                            imageButton.setImageURI(pickResult.getUri());


                        } else {

                            Toast.makeText(getContext(), "Sorry. Could not select this image. ERROR Status : " + pickResult.getError(), Toast.LENGTH_LONG).show();

                        }
                    }
                }).show(getFragmentManager());
            }


        });

        add_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooserDialog = new ChooserDialog(getActivity(), R.style.FileChooserStyle_Dark)
                        //.withFilter(true, false)
                        .withStartFile("/sdcard")
                        .withOnBackPressedListener(new ChooserDialog.OnBackPressedListener() {
                            @Override
                            public void onBackPressed(androidx.appcompat.app.AlertDialog dialog) {
                                chooserDialog.goBack();
                            }
                        })
                        // to handle the result(s)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                Toast.makeText(getContext(), "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build()
                        .show();
            }
        });

        EditText title_editText = customLayout.findViewById(R.id.announ_title_edittext);
        EditText desc_editText = customLayout.findViewById(R.id.announ_desc_editText);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(customLayout);
        alert.setTitle("Add an announcement");
        alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String title = title_editText.getText().toString();
                String desc = desc_editText.getText().toString();
                String classx = sharedPreferences.getString("classxx", "SSS");


                Log.d(TAG, "onClick: CLASSX : ALERT DIALOG" + classx);

                if (title.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(getContext(), "Your Title or Description is missing. Say something!", Toast.LENGTH_LONG).show();
                } else {

                    String post_key = databaseReference.child("classAnnouncements").child(classx).push().getKey();

                    HashMap<String, Object> postValues = new HashMap<String, Object>();
                    postValues.put("title", title);
                    postValues.put("desc", desc);
                    postValues.put("author", name);
                    postValues.put("postID", post_key);
                    postValues.put("type", announcement_type);
                    postValues.put("date", getDate());
                    postValues.put("time", getTime());

                    startPostUpload(postValues, post_key, classx, announcement_type);

                    //startImageUpload(title, desc, classx);

                }

            }
        });
        alert.show();


    }

    private void startPostUpload(HashMap<String, Object> postValues, String key, String classx, String announcement_type) {

        if (announcement_type.equalsIgnoreCase("assignment")) {
            databaseReference.child("Assignments").child(classx).child(key).setValue(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (!uriArrayList.isEmpty()) {
                        //Toast.makeText(getContext(), "You got some images boss", Toast.LENGTH_SHORT).show();
                        startImageUpload(key, classx, announcement_type);
                    }
                    Toast.makeText(getContext(), "Post added successfully." + key, Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            databaseReference.child("classAnnouncements").child(classx).child(key).setValue(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Post added successfully." + key, Toast.LENGTH_SHORT).show();

                        if (!uriArrayList.isEmpty()) {
                            //Toast.makeText(getContext(), "You got some images boss", Toast.LENGTH_SHORT).show();
                            startImageUpload(key, classx, announcement_type);
                        }

                    } else {
                        Toast.makeText(getContext(), "Could not add this post. ERROR : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Upload failed! : " + e, Toast.LENGTH_SHORT).show();
                        }
                    });

        }


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

    private String getDate() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.d(TAG, "onClick: DATE : " + date);

        return date;
    }

    private void startImageUpload(String post_key, String classxxxVal, String announcement_type) {

        //  Log.d(TAG, "startImageUpload: Title : " + title_text + ": \n Desc : " + desc_text);
        mProgressBar.setMessage("Posting...");
        mProgressBar.show();

        if (announcement_type.equalsIgnoreCase("Assignment")) {
            StorageReference filePath = mStorage.child("Assignment").child(classxxxVal).child(post_key);
            Log.d(TAG, "startPosting: Upload image : " + uploadImage.toString());


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

                                                        Snackbar snackbar = Snackbar
                                                                .make(rootView, "Image uploaded successfully", Snackbar.LENGTH_LONG);

                                                        snackbar.show();

                                                        mProgressBar.dismiss();

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getContext(), "FAILED : " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            //String downloadUrl = filePath.getDownloadUrl().toString();


                        } else {
                            Toast.makeText(getContext(), "Failed ", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: FAILED : ", task.getException());
                        }
                    }
                });

            }

        } else {
            StorageReference filePath = mStorage.child("classAnnouncements").child(classxxxVal).child(post_key);
            Log.d(TAG, "startPosting: Upload image : " + uploadImage.toString());


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


                                        databaseReference.child("classAnnouncements").child(classxxxVal).child(post_key).child("imgURLs").push().setValue(downloadurlhash)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "onSuccess: Download url's updated.");

                                                        // Toast.makeText(getContext(), "Download URLs updated : " + post_key, Toast.LENGTH_SHORT).show();

                                                        Snackbar snackbar = Snackbar
                                                                .make(rootView, "Image uploaded successfully", Snackbar.LENGTH_LONG);

                                                        snackbar.show();

                                                        mProgressBar.dismiss();

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(getContext(), "FAILED : " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        } else {
                            Toast.makeText(getContext(), "Failed ", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onComplete: FAILED : ", task.getException());
                        }
                    }
                });

            }
        }


        Log.d(TAG, "startPosting: Upload image : " + uploadImage.toString());

        uriArrayList.clear();

    }

    public static class ClassAnnouncementsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ClassAnnouncementsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView mTitleText = (TextView) mView.findViewById(R.id.title_txt_list);
            mTitleText.setText(title);
        }


        public void setDescription(String description) {
            TextView mDescText = (TextView) mView.findViewById(R.id.description_txt_list);
            mDescText.setText(description);
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setInteractionListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }


}
