package com.boltraz;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
