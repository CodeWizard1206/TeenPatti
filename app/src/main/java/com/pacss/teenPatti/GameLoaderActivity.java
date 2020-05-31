package com.pacss.teenPatti;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.gameHandler.gamePlayManager;

public class GameLoaderActivity extends AppCompatActivity {

    private final gamePlayManager Manager = gamePlayManager.getInstance();
    private final FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
    private String gameId, gameType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game_loader);

        if (getIntent().hasExtra("GameType") && getIntent().hasExtra("GameID")) {
            gameType = getIntent().getStringExtra("GameType");
            gameId = getIntent().getStringExtra("GameID");
        }


        firebaseManager.updateGameStatus(gameId);
        new ValueFetcher(gameId).execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Manager.checkServer(gameId);
                if (gamePlayManager.isServer) {
                    Manager.dealCards(gameId);
                }
            }
        }, 8000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GameLoaderActivity.this, GamePlayActivity.class);
                intent.putExtra("GameType", gameType);
                intent.putExtra("GameID", gameId);
                startActivity(intent);
                finishAffinity();
            }
        }, 12000);
    }

    @SuppressLint("StaticFieldLeak")
    class ValueFetcher extends AsyncTask {

        private String gameId;
        private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();

        ValueFetcher(String gameId) {
            this.gameId = gameId;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            firebaseManager.getBetAmount(gameId);
            return null;
        }
    }
}
