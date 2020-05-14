package com.pacss.teenPatti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;
import com.pacss.teenPatti.dataHandler.UserHandler;

public class HomeActivity extends AppCompatActivity {

    private final UserHandler User = UserHandler.getUserHandlerReference();
    private MaterialCardView admincard;
    private MaterialCardView exitCard;
    private MaterialCardView chipCard;
    private AppCompatTextView userName;
    private static AppCompatTextView chipCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        admincard = findViewById(R.id.adminCard);
        exitCard = findViewById(R.id.exitCard);
        chipCard = findViewById(R.id.buyChipCard);
        userName = findViewById(R.id.userName);
        chipCount = findViewById(R.id.chipAmount);

        if (User.getUserType() == UserHandler.Admin) {
            admincard.setVisibility(View.VISIBLE);
        }
        userName.setText(User.getUserName());
        chipCount.setText(Integer.toString(User.getChipAmount()));

        Toast.makeText(this, "Welcome, "+User.getUserName(), Toast.LENGTH_SHORT).show();

        exitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        chipCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ChipBuyerActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        chipCount.setText(Integer.toString(User.getChipAmount()));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
