package com.boltraz.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.ListAdapter.SettingsAdapter;
import com.boltraz.LoginActivity;
import com.boltraz.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String dp_url;
    public static final String TAG = "Dashboard Fragment";


    public ProgressDialog progressDialog;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String day;
    //  @BindView(R.id.profile_picture_img)
    // CircularImageView profilePictureImg;
    @BindView(R.id.profile_name)
    TextView profileName;
    public ArrayList<String> settingsArrayList;
    public SettingsAdapter settingsAdapter;
    @BindView(R.id.settings_options_recyclerView)
    RecyclerView settingsOptionsRecyclerView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    public String UID = "";
    private String coe_url = "";
    public String userName = "";
    public String usn = "";
    @BindView(R.id.profile_picture)
    ImageView profile_picture;


    @BindView(R.id.usn_text)
    TextView usn_text;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public Toolbar toolbar;
    public PickSetup setup;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private OnFragmentInteractionListener mListener;
    private Unbinder mUnbinder;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //  profilePictureImg = view.findViewById(R.id.profile_picture_img);
        profileName = view.findViewById(R.id.profile_name);

        progressDialog = new ProgressDialog(getContext());

        profile_picture = view.findViewById(R.id.profile_picture);
        preferences = getContext().getSharedPreferences("sharedpref", Context.MODE_PRIVATE);

        String name = preferences.getString("name", "XXX");
        String usn = preferences.getString("usn", "1NH16CSXXX");
        dp_url = preferences.getString("dp_url", "XXX");
        String dept = preferences.getString("dept", "N/a");


        // Toast.makeText(getContext(), "DP URL : " + dp_url, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        if (UID.isEmpty()) {
            UID = mAuth.getUid();
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();


        usn_text = (TextView) view.findViewById(R.id.usn_text);
        profileName.setText(name);
        usn_text.setText(usn + " • " + dept);

        //getTimeOfDay();

        getDP(dp_url);

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

        String url = getCoeUrl();

        settingsArrayList = new ArrayList<>();
        settingsArrayList.add("Calender of Events");
        settingsArrayList.add("Faculty list");
        settingsAdapter = new SettingsAdapter(settingsArrayList);
        Log.d(TAG, "onCreateView: onCreateView : geTCOE : " + url);

        settingsOptionsRecyclerView = (RecyclerView) view.findViewById(R.id.settings_options_recyclerView);
        settingsOptionsRecyclerView.setHasFixedSize(true);
        settingsOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsOptionsRecyclerView.setAdapter(settingsAdapter);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //setupFirebaseListener();
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    private String getCoeUrl() {

        editor = preferences.edit();


        databaseReference.child("misc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coe_url = dataSnapshot.child("coe_url").getValue().toString();
                Log.d(TAG, "onDataChange: getCOEURL : " + coe_url);

                editor.putString("coe_url", coe_url);
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return coe_url;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.change_profile_pic_item:
                changeDPOption(UID);
                //   Toast.makeText(getContext(), "Edit Profile picture", Toast.LENGTH_SHORT).show();
                break;

            case R.id.about_item:
                Toast.makeText(getContext(), "About item", Toast.LENGTH_SHORT).show();
                break;

            case R.id.edit_password_item:
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setMessage("Are you sure you want to send an email reset link to your account?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editPassword();

                    }
                });
                alert.show();
                break;
            case R.id.logout_item:
                logout();
                //Toast.makeText(getContext(), "Logout item", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editPassword() {
        String currentEmail = preferences.getString("email", "");
        Log.d(TAG, "editPassword: currentEmail : " + currentEmail);

        mAuth.sendPasswordResetEmail(currentEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Password reset link sent");
                    alert.setMessage("A link to reset your password has been sent to " + currentEmail + ".");
                    alert.show();
                }
            }
        });
    }

    private void logout() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Logout?");
        alertDialogBuilder.setMessage("Are you sure you want to log out of Boltraz?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Log.d(TAG, "onAuthStateChanged: " + mAuth.getCurrentUser().getDisplayName() + " is signed out");
                progressDialog.setTitle("Sign out");
                progressDialog.setMessage("Signing you out");
                progressDialog.show();
                Toast.makeText(getContext(), "Signing you out", Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }, 3000);


            }
        });

        alertDialogBuilder.show();

    }

    private void changeDPOption(String uid) {

        PickImageDialog.build(setup, new IPickResult() {
            @Override
            public void onPickResult(PickResult pickResult) {

                Uri dp_imageUri = pickResult.getUri();
                StorageReference filePath = mStorage.getReference().child("users").child(uid).child("userpic.jpg");
                filePath.putFile(dp_imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setMessage("Updating your profile picture");
                        progressDialog.show();
                    }
                })
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                    Glide.with(getContext()).load(dp_imageUri).into(profile_picture);

                                    filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String dp_url = task.getResult().toString();
                                            updateDPUrl(UID, dp_url);

                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Failed to update : " + e, Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                } else {
                                    Toast.makeText(getContext(), "Failed! : " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }
        }).show(getFragmentManager());
    }

    private void updateDPUrl(String uid, String dp_url) {
        databaseReference.child("students").child(uid).child("dp_url").setValue(dp_url).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "DP Url updated! ", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to update url", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void getTimeOfDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        int a = c.get(Calendar.AM_PM);

        Log.d(TAG, "getTimeOfDay: " + timeOfDay + " : AMPM : " + a + " : Calender : ");

        if (timeOfDay >= 0 && timeOfDay < 12) {
            day = "Good Morning";
            Toast.makeText(getContext(), "Good Morning", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            day = "Good Afternoon";
            Toast.makeText(getContext(), "Good Afternoon", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            day = "Good Evening";
            Toast.makeText(getContext(), "Good Evening", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            day = "Good Night";
            Toast.makeText(getContext(), "Good Night", Toast.LENGTH_SHORT).show();
        }

        //good_day_text.setText(day);
    }

    public void getDP(String profile_pic_url) {
       /* Uri url = mAuth.getCurrentUser().getPhotoUrl();
        Glide.with(getContext()).load(url).into(profilePictureImg);
*/
        if (!profile_pic_url.isEmpty()) {
            Glide.with(getContext()).load(profile_pic_url).into(profile_picture);
        }

    }


    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onStart() {
        super.onStart();


        //Uri url = mAuth.getCurrentUser().getPhotoUrl();
        //Glide.with(getContext()).load(url).into(profilePictureImg);


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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
}
