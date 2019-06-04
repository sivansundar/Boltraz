package com.boltraz.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.Model.SubjectModel;
import com.boltraz.Model.TimetableModel;
import com.boltraz.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

    private RecyclerView subject_recyclerView;

    private OnFragmentInteractionListener mListener;

    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
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

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        subject_recyclerView = (RecyclerView) view.findViewById(R.id.sub_recyclerView);
        subject_recyclerView.setHasFixedSize(true);
        subject_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("subjects").child("cse");


        getSubjects();

        return view;
    }

    public void getSubjects() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("subjects")
                .child("cse")
                .child("sem7");

        FirebaseRecyclerOptions<SubjectModel> options =
                new FirebaseRecyclerOptions.Builder<SubjectModel>()
                        .setQuery(query, SubjectModel.class)
                        .build();

        FirebaseRecyclerAdapter<SubjectModel, NotesFragment.SubjectViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SubjectModel, NotesFragment.SubjectViewHolder>(
                options) {

            @NonNull
            @Override
            public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sub_list_item, parent, false);

                return new SubjectViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SubjectViewHolder subjectViewHolder, int i, @NonNull SubjectModel subjectModel) {


                subjectViewHolder.setTitle(subjectModel.getTitle());
                subjectViewHolder.setProf(subjectModel.getProf());

            }
        };


        subject_recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView mTitleText = (TextView) mView.findViewById(R.id.subject_title);
            mTitleText.setText(title);
        }

        public void setProf(String prof) {
            TextView mProfText = (TextView) mView.findViewById(R.id.subject_prof);
            mProfText.setText(prof);
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
}
