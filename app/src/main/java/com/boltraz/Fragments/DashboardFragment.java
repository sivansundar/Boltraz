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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.boltraz.LoginActivity;
import com.boltraz.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

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
    public String dp_url;
    public static final String TAG = "Dashboard Fragment";
    @BindView(R.id.logout_btn)
    Button logout_btn;

    public ProgressDialog progressDialog;

    public SharedPreferences preferences;
    public String day;
    //  @BindView(R.id.profile_picture_img)
    // CircularImageView profilePictureImg;
    @BindView(R.id.profile_name)
    TextView profileName;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    public String UID = "";
    public String userName = "";
    public String usn = "";
    @BindView(R.id.profile_picture)
    ImageView profile_picture;


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
        //  profilePictureImg = view.findViewById(R.id.profile_picture_img);
        profileName = view.findViewById(R.id.profile_name);

        progressDialog = new ProgressDialog(getContext());

        profile_picture = view.findViewById(R.id.profile_picture);
        preferences = getContext().getSharedPreferences("sharedpref", Context.MODE_PRIVATE);

        String name = preferences.getString("name", "XXX");
        String usn = preferences.getString("usn", "1NH16CSXXX");
        dp_url = preferences.getString("dp_url", "XXX");
        String dept = preferences.getString("dept", "N/a");

        Toast.makeText(getContext(), "DP URL : " + dp_url, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        if (UID.isEmpty()) {
            UID = mAuth.getUid();
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        usn_text = (TextView) view.findViewById(R.id.usn_text);
        profileName.setText(name);
        usn_text.setText(usn + " • " + dept);

        //getTimeOfDay();

        getDP(dp_url);

        //setupFirebaseListener();
        mUnbinder = ButterKnife.bind(this, view);
        return view;
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
