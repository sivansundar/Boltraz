package com.boltraz.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.boltraz.ClassAnnouncementsActivity;
import com.boltraz.Model.ClassAnnouncementsModel;
import com.boltraz.Model.TimetableModel;
import com.boltraz.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;

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


    @BindView(R.id.monday_tab)
    TabItem mondayTab;
    @BindView(R.id.tuesday_tab)
    TabItem tuesdayTab;
    @BindView(R.id.wednesday_tab)
    TabItem wednesdayTab;
    @BindView(R.id.thursday_tab)
    TabItem thursdayTab;
    @BindView(R.id.friday_tab)
    TabItem fridayTab;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public String day = "Monday";

    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

    private RecyclerView timeTableRecyclerView;
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
        View v = inflater.inflate(R.layout.fragment_timetable, container, false);
        TabLayout tabLayout;
        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);

        timeTableRecyclerView = (RecyclerView) v.findViewById(R.id.timetable_recyclerView);
        timeTableRecyclerView.setHasFixedSize(true);
        timeTableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        //timeTableRecyclerView.addItemDecoration(itemDecor);

        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference().child("timetable").child("Class6B");

        // Sends a firebase query to pull according to day.

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String day = tab.getText().toString();

                switch (day) {
                    case "Monday" :
                       getTimeTable("Monday");
                        Toast.makeText(getContext(), "Monday", Toast.LENGTH_SHORT).show();
                        break;

                    case "Tuesday" :
                        getTimeTable("Tuesday");
                        break;

                    case "Wednesday" :
                        //getTimeTable("Wednesday");
                        break;
                    case "Thursday" :
                      //  getTimeTable("Thursday");
                        break;

                    case "Friday" :
                       // getTimeTable("Friday");
                        break;

                    default:
                        Toast.makeText(getContext(), "Nothing happens", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        getTimeTable("Monday");
    }

    private void getTimeTable(String day) {

        Query query = databaseReference.child(day);

        FirebaseRecyclerOptions<TimetableModel> options =
                new FirebaseRecyclerOptions.Builder<TimetableModel>()
                        .setQuery(query, TimetableModel.class)
                        .build();

        FirebaseRecyclerAdapter<TimetableModel, TimeTableViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TimetableModel, TimeTableViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull TimeTableViewHolder timeTableViewHolder, int i, @NonNull TimetableModel timetableModel) {

                timeTableViewHolder.setTitle(timetableModel.getTitle());
                timeTableViewHolder.setProf(timetableModel.getProf());
                timeTableViewHolder.setHour(i);
                Log.d(TAG, "onClick: position value " + i);


            }

            @NonNull
            @Override
            public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timetable_list_item, parent, false);


                return new TimeTableViewHolder(view);
            }
        };

        timeTableRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        //Implement Firebase Recycler Adapter

    }

    public static class TimeTableViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TimeTableViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        public void setTitle(String title) {
            TextView titleText = (TextView)mView.findViewById(R.id.subjectName_txt);
            titleText.setText(title);
        }


        public void setProf(String prof) {
            TextView profText = (TextView) mView.findViewById(R.id.profName_txt);
            profText.setText("Prof. " +prof);
        }

        public void setHour(int hour) {
            hour++;
            TextView houtText = (TextView) mView.findViewById(R.id.hour_txt);
            houtText.setText("" + hour);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

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
