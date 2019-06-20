package com.boltraz;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.boltraz.Fragments.DashboardFragment;
import com.boltraz.Fragments.MainFragment;
import com.boltraz.Fragments.NotesFragment;
import com.boltraz.Fragments.TimetableFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        TimetableFragment.OnFragmentInteractionListener,
        NotesFragment.OnFragmentInteractionListener{


    @BindView(R.id.mainFrame)
    FrameLayout mainFrame;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirebaseFirestoredb;


    private FragmentTransaction fragmentTransaction;

    private MainFragment mainFragment;
    private DashboardFragment dashboardFragment;
    private TimetableFragment timetableFragment;
    private NotesFragment notesFragment;

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
        notesFragment = new NotesFragment();

        setFragment(mainFragment);

        startListeningNotifications();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home :
                        setFragment(mainFragment);
                        break;

                    case R.id.nav_timetable :
                        setFragment(timetableFragment);
                        break;

                    case R.id.nav_notes :
                        setFragment(notesFragment);
                        break;

                    case R.id.nav_dashboard :
                        setFragment(dashboardFragment);
                        break;

                        default:
                            return false;
                }

                return true;
            }

        });


    }

    private void startListeningNotifications() {

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("Class7A_Announcements");
    }

    private void setFragment(Fragment fragment) {

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