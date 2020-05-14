package com.pacss.teenPatti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.pacss.teenPatti.dataHandler.UserHandler;

public class paymentGateway extends AppCompatActivity {

    private UserHandler User = UserHandler.getUserHandlerReference();
    private MaterialCardView backCard;
    private MaterialCardView googlePayCard;
    private MaterialCardView paytmCard;
    private MaterialCardView phonePeCard;
    private TextView userName;
    private AppCompatTextView moneyValue;
    private AppCompatTextView chipValue;
    private static String chipAmount;
    private static String moneyAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);
        incomingIntent();

        userName = findViewById(R.id.userName);
        backCard = findViewById(R.id.backCard);
        googlePayCard = findViewById(R.id.gPayCard);
        paytmCard = findViewById(R.id.payTmCard);
        phonePeCard = findViewById(R.id.phonePayCard);
        moneyValue = findViewById(R.id.moneyValue);
        chipValue = findViewById(R.id.chipAmount);

        userName.setText(User.getUserName());
        moneyValue.setText(moneyAmount);
        chipValue.setText(chipAmount);

        backCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Google Pay Payment Gateway
        googlePayCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //Paytm Payment Gateway
        paytmCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //PhonePe Payment Gateway
        phonePeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void incomingIntent() {
        if(getIntent().hasExtra("CHIPS") && getIntent().hasExtra("MONEY")) {
            chipAmount = getIntent().getStringExtra("CHIPS");
            moneyAmount = getIntent().getStringExtra("MONEY");
        }
    }
}
