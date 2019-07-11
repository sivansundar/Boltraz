package com.boltraz.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import com.boltraz.ClassAnnouncementsActivity;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.Model.UserModel;
import com.boltraz.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.HashMap;

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
    public PickSetup setup;
    public StorageReference mStorage;
    Uri uploadImage;


    int todoSize = 0;

    String announcement_type;


    public FirebaseDatabase mDatabase;
    public DatabaseReference databaseReference;
    View rootView;
    private ProgressDialog mProgressBar;

    private Unbinder mUnbinder;
    private Unbinder mUnbinder2;

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




        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshAnnouncements();
        getToDoCount();

        getDP();

        databaseReference.child("students/semester7/").child(userID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                String classrep = userModel.getClassrep();

                Log.d(TAG, "onDataChange: " + classrep);
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

    }

    private void getToDoCount() {

        databaseReference.child("students/semester7/").child(userID).child("todos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todoSize = (int) dataSnapshot.getChildrenCount();

                if (todoSize > 0) {
                    todoNumber_label.setText("" + todoSize);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDP() {

        Uri url = mAuth.getCurrentUser().getPhotoUrl();
        Glide.with(getContext()).load(url).into(circleImageView);

        if (name.isEmpty()) {
            databaseReference.child("students/semester7/").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    user = dataSnapshot.getValue(UserModel.class);


                    //Toast.makeText(getContext(), "Class rep : " + classrep, Toast.LENGTH_SHORT).show();
                    name = user.getName();
                    labelName.setText(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(getContext(), "We have a problem! Check the log", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onCancelled: getDP() : ", databaseError.toException());
                }
            });
        }


    }

    private void refreshAnnouncements() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("classAnnouncements")
                .child("Class7A");

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

                classAnnouncementsViewHolder.setTitle(timetableModel.getTitle());
                classAnnouncementsViewHolder.setDescription(timetableModel.getDesc());

                classAnnouncementsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ClassAnnouncementsActivity.class);
                        intent.putExtra("postID", post_key);
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
        ImageButton imageButton = customLayout.findViewById(R.id.add_imageButton);
        Spinner announ_type_spinner = customLayout.findViewById(R.id.announ_type_spinner);



        String [] announ_type = { "None", "Assignment"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, announ_type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        announ_type_spinner.setAdapter(arrayAdapter);

        announ_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Spinner Value : " + announ_type[position], Toast.LENGTH_SHORT).show();

                announcement_type = announ_type[position];
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
                            Toast.makeText(getContext(), "URI : " + pickResult.getUri().toString(), Toast.LENGTH_SHORT).show();
                            uploadImage = pickResult.getUri();
                            imageButton.setImageURI(pickResult.getUri());


                        }
                    }
                }).show(getFragmentManager());
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
                startImageUpload(title, desc);

            }
        });
        alert.show();


    }

    private void startImageUpload(String title_text, String desc_text) {

        Log.d(TAG, "startImageUpload: Title : " + title_text + ": \n Desc : " + desc_text);


        if (title_text != null && desc_text != null) {

            mProgressBar.setMessage("Posting...");
            mProgressBar.show();

            String key = databaseReference.child("classAnnouncements/Class7A").push().getKey();
            StorageReference filePath = mStorage.child("classAnnouncements/Class7A/").child(key).child(uploadImage.getLastPathSegment());

            Log.d(TAG, "startPosting: Upload image : " + uploadImage.toString());
            filePath.putFile(uploadImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();

                                    Log.d(TAG, "onSuccess: downloadUrl : " + downloadUrl);
                                    mProgressBar.dismiss();

                                    Snackbar snackbar = Snackbar
                                            .make(rootView, "Announcement posted successfully", Snackbar.LENGTH_LONG);

                                    snackbar.show();

                                    HashMap<String, Object> postValues = new HashMap<String, Object>();
                                    postValues.put("title", title_text);
                                    postValues.put("desc", desc_text);
                                    postValues.put("author", name);
                                    postValues.put("postID", key);
                                    postValues.put("imgUrl", downloadUrl);
                                    postValues.put("type", announcement_type);

                                    startPostUpload(postValues, key);
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


    }

    private void startPostUpload(HashMap<String, Object> postValues, String key) {

        databaseReference.child("classAnnouncements/Class7A").child(key).setValue(postValues).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Post added successfully : " + key, Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to add post : " + key, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: failed to add post : ", e);
                    }
                });

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
