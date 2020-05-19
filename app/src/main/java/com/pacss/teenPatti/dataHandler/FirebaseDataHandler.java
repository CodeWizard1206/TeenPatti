package com.pacss.teenPatti.dataHandler;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDataHandler extends Application {

    @Override
    public void onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }
}
