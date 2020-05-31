package com.pacss.teenPatti;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.gameHandler.gamePlayManager;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar loader;
    private SharedPreferences sharedPreferences;
    private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_splash);

        sharedPreferences = this.getSharedPreferences("LOGIN_DATA", MODE_PRIVATE);
        loader = findViewById(R.id.loaderView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.VISIBLE);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseManager.isConnectionAvailable(SplashActivity.this)) {
                    if (sharedPreferences.getBoolean("LOG_STATUS", false)) {
                        gamePlayManager Manager = gamePlayManager.getInstance();
                        Manager.resetInstance();
                        firebaseManager.getGuestUser(sharedPreferences.getString("USER_ID", "0000"));
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    finishAffinity();
                    Toast.makeText(SplashActivity.this, "Network Problem Occurred, Please Resolve All Network Issues & Try Again!!!", Toast.LENGTH_LONG).show();
                }
            }
        }, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
