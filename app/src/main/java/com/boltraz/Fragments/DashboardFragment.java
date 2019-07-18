package com.boltraz.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.boltraz.LoginActivity;
import com.boltraz.Model.UserModel;
import com.boltraz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.profile_picture_img)
    CircularImageView profilePictureImg;
    @BindView(R.id.profile_name)
    TextView profileName;
    public static final String TAG = "Dashboard Fragment";
    @BindView(R.id.logout_btn)
    Button logout_btn;

    public ProgressDialog progressDialog;

    public SharedPreferences preferences;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    public String UID = "";
    public String userName = "";
    public String usn = "";
    @BindView(R.id.usn_text)
    TextView usn_text;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        profilePictureImg = view.findViewById(R.id.profile_picture_img);
        profileName = view.findViewById(R.id.profile_name);

        progressDialog = new ProgressDialog(getContext());

        preferences = getContext().getSharedPreferences("sharedpref", Context.MODE_PRIVATE);

        String name = preferences.getString("name", "XXX");
        String usn = preferences.getString("usn", "1NH16CSXXX");

        Toast.makeText(getContext(), "XXX : " + name, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        if (UID.isEmpty()) {
            UID = mAuth.getUid();
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        usn_text = (TextView) view.findViewById(R.id.usn_text);
        profileName.setText(name);
        usn_text.setText(usn);

        //  getUserDetails();

        //setupFirebaseListener();
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void getUserDetails() {
       /* Uri url = mAuth.getCurrentUser().getPhotoUrl();
        Glide.with(getContext()).load(url).into(profilePictureImg);
*/
        if (userName.isEmpty()) {
            databaseReference.child("students/semester7/").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    usn = user.getUSN();
                    userName = user.getName();

                    profileName.setText(userName);
                    usn_text.setText(usn);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(getContext(), "We have a problem! Check the log", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onCancelled: onStart() : ", databaseError.toException());
                }
            });

        }
    }

    @OnClick(R.id.logout_btn)
    public void onLogout_btnClicked() {
        //TODO: add click handling

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Logout?");
        alertDialogBuilder.setMessage("Are you sure you want to log out of Boltraz?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Log.d(TAG, "onAuthStateChanged: " + mAuth.getCurrentUser().getDisplayName() + " is signed out");
                progressDialog.setTitle("Sign out");
                progressDialog.setMessage("Signing you out " + mAuth.getCurrentUser().getDisplayName());
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

   /* public void setupFirebaseListener() {
        Log.d(TAG, "setupFirebaseListener: Setting up Firebase AuthStateListener");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null) {
                    Log.d(TAG, "onAuthStateChanged: " + user.getDisplayName() + " is signed in");
                }
                else {
                    Log.d(TAG, "onAuthStateChanged: " + user.getDisplayName() + " is signed out");
                    progressDialog.setTitle("Sign out");
                    progressDialog.setMessage("Signing you out " + user.getDisplayName());
                    progressDialog.show();
                    Toast.makeText(getContext(), "Signing you out", Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }, 3000);
                }
            }
        };
    }*/
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
