package com.pacss.teenPatti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.material.card.MaterialCardView;
import com.pacss.teenPatti.dataHandler.UserHandler;

public class ChipBuyerActivity extends AppCompatActivity {

    private static UserHandler User = UserHandler.UserHandlerReference();
    private RecyclerView chipList;
    private chipBuyerAdapter chipAdapter;
    private String[] chipAmount;
    private String[] moneyAmount;
    private MaterialCardView backCard;
    private AppCompatTextView userName;
    private static AppCompatTextView chipCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_chip_buyer);

        chipList = findViewById(R.id.chipList);
        backCard = findViewById(R.id.backCard);
        userName = findViewById(R.id.userName);
        chipCount = findViewById(R.id.chipAmount);
        chipAmount = new String[]{"10","100","1K","3K","5K","10K","15K","20K","30K","40K","50K","1L","2L","5L","1M","10M"};
        moneyAmount = new String[]{"1","10","100","300","500","1,000","1,500","2,000","3,000","4,000","5,000","15,000","20,000","30,000","50,000","80,000"};

        chipAdapter = new chipBuyerAdapter(chipAmount, moneyAmount, ChipBuyerActivity.this);
        if (chipAdapter.getItemCount() > 0) {
            chipList.setAdapter(chipAdapter);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(ChipBuyerActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            chipList.setLayoutManager(layoutManager);
        }

        userName.setText(User.getUserName());
        chipCount.setText(Integer.toString(User.getTotalTokens()));

        backCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        chipCount.setText(Integer.toString(User.getTotalTokens()));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
