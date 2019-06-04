package com.boltraz.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boltraz.ClassAnnouncementsActivity;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirebaseFirestoredb;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "Boltraz MainActivity";
    String userID;


    public FirebaseDatabase mDatabase;
    public DatabaseReference databaseReference;
    private Unbinder mUnbinder;

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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mFirebaseFirestoredb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();



        classAnnouncementsRecyclerView = (RecyclerView) rootView.findViewById(R.id.classAnnouncements_RecyclerView);
        classAnnouncementsRecyclerView.setHasFixedSize(true);
        classAnnouncementsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshAnnouncements();

        getDP();
    }

    private void getDP() {

        Uri url = mAuth.getCurrentUser().getPhotoUrl();
        Glide.with(getContext()).load(url).into(circleImageView);

    }

    private void refreshAnnouncements() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("classAnnouncements")
                .child("Class6B");

        FirebaseRecyclerOptions<ClassAnnouncementsModel> options =
                new FirebaseRecyclerOptions.Builder<ClassAnnouncementsModel>()
                        .setQuery(query, ClassAnnouncementsModel.class)
                        .build();

        FirebaseRecyclerAdapter<ClassAnnouncementsModel, ClassAnnouncementsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClassAnnouncementsModel, ClassAnnouncementsViewHolder>(
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
