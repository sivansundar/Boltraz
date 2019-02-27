package com.boltraz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boltraz.Fragments.DashboardFragment;
import com.boltraz.Fragments.MainFragment;
import com.boltraz.Fragments.TimetableFragment;
import com.boltraz.ListAdapters.ClassAnnouncementsListAdapter;
import com.boltraz.Model.ClassAnnouncementsModel;
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
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        TimetableFragment.OnFragmentInteractionListener{

    @BindView(R.id.space)
    SpaceNavigationView space;
    @BindView(R.id.mainFrame)
    FrameLayout mainFrame;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirebaseFirestoredb;



    private androidx.fragment.app.FragmentTransaction fragmentTransaction;

    private MainFragment mainFragment;
    private DashboardFragment dashboardFragment;
    private TimetableFragment timetableFragment;


    String userID;
    private static final String TAG = "Boltraz MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainFragment = new MainFragment();
        dashboardFragment = new DashboardFragment();
        timetableFragment = new TimetableFragment();

        setFragment(mainFragment);

        space.initWithSaveInstanceState(savedInstanceState);
        space.addSpaceItem(new SpaceItem("", R.drawable.round_home_black_36)); //0
        space.addSpaceItem(new SpaceItem("", R.drawable.round_event_note_black_36));//1
        space.addSpaceItem(new SpaceItem("", R.drawable.ic_launcher_background));//2
        space.addSpaceItem(new SpaceItem("", R.drawable.round_account_circle_black_48));//3

        space.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (itemIndex==0) {
                    setFragment(mainFragment);
                }

                if (itemIndex==1) {
                    setFragment(timetableFragment);
                }

                if (itemIndex==3) {
                    setFragment(dashboardFragment);
                }


                Toast.makeText((Context) MainActivity.this, itemIndex + "" + itemName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });



    }

    private void setFragment(androidx.fragment.app.Fragment fragment) {

        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, fragment);
        fragmentTransaction1.commit();


    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();


    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}