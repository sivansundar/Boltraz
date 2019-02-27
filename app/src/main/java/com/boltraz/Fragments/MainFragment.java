package com.boltraz.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.boltraz.ListAdapters.ClassAnnouncementsListAdapter;
import com.boltraz.MainActivity;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

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

    RecyclerView classAnnouncementsRecyclerView;
    @BindView(R.id.logout)
    Button logout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ClassAnnouncementsListAdapter classAnnouncementsListAdapter;
    private List<ClassAnnouncementsModel> classAnnouncementsList;

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: Fragment home view destroyed");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Fragment home destroyed");
        super.onDestroy();
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirebaseFirestoredb;

    private static final String TAG = "Boltraz MainActivity";
    String userID;

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

        mFirebaseFirestoredb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();




        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        classAnnouncementsRecyclerView = (RecyclerView) rootView.findViewById(R.id.classAnnouncements_RecyclerView);

        classAnnouncementsList = new ArrayList<>();
        classAnnouncementsListAdapter = new ClassAnnouncementsListAdapter(classAnnouncementsList);

        classAnnouncementsRecyclerView.setHasFixedSize(true);
        classAnnouncementsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAnnouncementsRecyclerView.setAdapter(classAnnouncementsListAdapter);

        getClassAnnouncemets();


        return rootView;
    }

    private void getClassAnnouncemets() {
        mFirebaseFirestoredb.collection("ClassAnouncements6B").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.e(TAG, "onEvent: getClassAnnouncements : ", e);
                } else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED || doc.getType() == DocumentChange.Type.MODIFIED) {
                            String title = doc.getDocument().getString("Title");
                            Log.d(TAG, "onEvent: Docs : " + title);


                            ClassAnnouncementsModel classAnouncements = doc.getDocument().toObject(ClassAnnouncementsModel.class);
                            classAnnouncementsList.add(classAnouncements);

                            classAnnouncementsListAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }
        });
    }

       @OnClick(R.id.logout)
    public void onViewClicked() {

        mAuth.signOut();


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
