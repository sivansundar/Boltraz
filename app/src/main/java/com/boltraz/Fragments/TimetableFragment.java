package com.boltraz.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.boltraz.ListAdapters.ClassAnnouncementsListAdapter;
import com.boltraz.ListAdapters.TimetableListAdapter;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.Model.TimetableModel;
import com.boltraz.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimetableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimetableFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView timetableRecyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TimetableListAdapter timetableListAdapter;
    private List<TimetableModel> timetableList;
    private FirebaseFirestore mFirebaseFirestoredb;
    private OnFragmentInteractionListener mListener;

    private static final String TAG = "Boltraz TimeTableFragment";

    public TimetableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimetableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimetableFragment newInstance(String param1, String param2) {
        TimetableFragment fragment = new TimetableFragment();
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

        View v = inflater.inflate(R.layout.fragment_timetable, container, false);

        timetableRecyclerView = (RecyclerView) v.findViewById(R.id.timetable_recyclerView);

        timetableList = new ArrayList<>();
        timetableListAdapter = new TimetableListAdapter(timetableList);

        timetableRecyclerView.setHasFixedSize(true);
        timetableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        timetableRecyclerView.setAdapter(timetableListAdapter);



        Spinner spinner = v.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> daysList = new ArrayList<String>();
        daysList.add("Monday");
        daysList.add("Tuesday");
        daysList.add("Wednesday");
        daysList.add("Thursday");
        daysList.add("Friday");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, daysList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

       //    getTimeTable("Monday"); // Sends a firebase query to pull according to day.

        return v;
    }

    private void getTimeTable(String day) {

        timetableList.clear();

        mFirebaseFirestoredb.collection("Timetable").document("Timetable_6B_root").collection("Days").document(day).collection("Hours").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                //String hour = mFirebaseFirestoredb.collection("Timetable").document("Timetable_6B_root").collection("Days").document(day).collection("Hours").getPath().toString();
                if (e != null) {
                    Log.e(TAG, "onEvent: getClassAnnouncements : ", e);
                } else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED || doc.getType() == DocumentChange.Type.MODIFIED) {
                            String title = doc.getDocument().getString("Title");
                            String hour = doc.getDocument().getId();
                            Toast.makeText(getContext(), "" + hour, Toast.LENGTH_SHORT).show();

                            Log.d(TAG, "onEvent: Docs : " + title);


                            TimetableModel timetableModel = doc.getDocument().toObject(TimetableModel.class);
                            timetableList.add(timetableModel);

                            timetableListAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }
        });
        
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String day = parent.getItemAtPosition(position).toString();
        getTimeTable(day);
        Toast.makeText(getContext(), "Selected item : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
