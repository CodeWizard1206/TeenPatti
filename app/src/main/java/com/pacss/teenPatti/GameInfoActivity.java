package com.pacss.teenPatti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.gameHandler.Card;
import com.pacss.teenPatti.gameHandler.PlayerDataHolder;
import java.util.Objects;

public class GameInfoActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
    private AppDialogHandler dialog;
    private PlayerDataHolder[] playerData;
    private TextView botValue, gameStatus, potValue;
    private TextView WinSequence, WinnerName;
    private MaterialCardView backButton, refreshButton;
    private AppCompatImageView winCardOne, winCardTwo, winCardThree;
    private String gameId, GameStatus;
    private RecyclerView playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game_info);

        dialog = new AppDialogHandler(GameInfoActivity.this);
        dialog.setCustomDialog(R.layout.loader_view, AppDialogHandler.WRAP_CONTENT);
        botValue = findViewById(R.id.botValue);
        gameStatus = findViewById(R.id.GameStatus);
        potValue = findViewById(R.id.PotValue);
        playerList = findViewById(R.id.playerList);
        WinSequence = findViewById(R.id.winSequence);
        WinnerName = findViewById(R.id.winnerName);
        winCardOne = this.findViewById(R.id.winCardOne);
        winCardTwo = this.findViewById(R.id.winCardTwo);
        winCardThree = this.findViewById(R.id.winCardThree);
        backButton = findViewById(R.id.backCardBtn);
        refreshButton = findViewById(R.id.refreshBtn);

        if(getIntent().hasExtra("ID") && getIntent().hasExtra("BOT") && getIntent().hasExtra("STATUS") && getIntent().hasExtra("POT")) {
            gameId = getIntent().getStringExtra("ID");
            botValue.setText(getIntent().getStringExtra("BOT"));
            GameStatus = getIntent().getStringExtra("STATUS");
            gameStatus.setText(GameStatus);
            potValue.setText(getIntent().getStringExtra("POT"));
        }

        LoadGameData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadGameData();
            }
        });

    }

    private void LoadGameData() {
        dialog.showDialog();
        if (GameStatus.equals("COMPLETED")) {
            playerList.setVisibility(View.INVISIBLE);
            new FetchGameWinnerThread().execute();
        } else {
            playerList.setVisibility(View.VISIBLE);
            new PlayerDataFetcherThread().execute();
            new CountDownTimer(30000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (playerData != null && playerData.length >= 1) {
                        PlayerListAdapter adapter = new PlayerListAdapter(GameInfoActivity.this, playerData);
                        playerList.setAdapter(adapter);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(GameInfoActivity.this);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        playerList.setLayoutManager(layoutManager);
                        cancel();
                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();
            new FetchGameWinnerThread().execute();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.clear();
            }
        }, 4000);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    class PlayerDataFetcherThread extends AsyncTask {

        private String PlayerTokens = "";
        private String seenStat;

        @Override
        protected Object doInBackground(Object[] objects) {
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameId).child("Players");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() >= 1) {
                        playerData = new PlayerDataHolder[(int) dataSnapshot.getChildrenCount()];
                        int i = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.e("GAMEINFO", "FETCHING GAME DATA!!!");
                            if (ds.child("playerName").getValue() != null
                                    && ds.child("isSeen").getValue() != null
                                    && ds.child("Cards").child("CardRank_0").getValue() != null
                                    && ds.child("Cards").child("CardSuit_0").getValue() != null
                                    && ds.child("Cards").child("CardRank_1").getValue() != null
                                    && ds.child("Cards").child("CardSuit_1").getValue() != null
                                    && ds.child("Cards").child("CardRank_2").getValue() != null
                                    && ds.child("Cards").child("CardSuit_2").getValue() != null) {

                                fetchUserTokens(ds.getKey());
                                String playerName = Objects.requireNonNull(ds.child("playerName").getValue()).toString();
                                if (Objects.requireNonNull(ds.child("isSeen").getValue()).toString().equals("true")) {
                                    seenStat = "SEEN";
                                } else {
                                    seenStat = "UNSEEN";
                                }
                                Card[] cards = new Card[3];
                                for (int x = 0; x < 3; x++) {
                                    short rank = Short.parseShort(Objects.requireNonNull(ds.child("Cards").child("CardRank_" + x).getValue()).toString());
                                    short suit = Short.parseShort(Objects.requireNonNull(ds.child("Cards").child("CardSuit_" + x).getValue()).toString());
                                    cards[x] = new Card(suit, rank);
                                }

                                new CountDownTimer(10000, 2000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        if (!PlayerTokens.equals("")) {
                                            cancel();
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                    }
                                };
                                playerData[i] = new PlayerDataHolder(playerName, PlayerTokens, seenStat, cards);
                                i++;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            return null;
        }

        private void fetchUserTokens(String userId) {
            final DatabaseReference dbRef = firebaseManager.getUserDB().child(userId).child("totalTokens");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        PlayerTokens = dataSnapshot.getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

        }
    }

    class FetchGameWinnerThread extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameId).child("gameWinner");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("winningSequence").getValue() != null
                            && dataSnapshot.child("Cards").child("cardOne").getValue() != null
                            && dataSnapshot.child("Cards").child("cardTwo").getValue() != null
                            && dataSnapshot.child("Cards").child("cardThree").getValue() != null) {

                        final String winSequence = Objects.requireNonNull(dataSnapshot.child("winningSequence").getValue()).toString();
                        final String CardOne = Objects.requireNonNull(dataSnapshot.child("Cards").child("cardOne").getValue()).toString();
                        final String CardTwo = Objects.requireNonNull(dataSnapshot.child("Cards").child("cardTwo").getValue()).toString();
                        final String CardThree = Objects.requireNonNull(dataSnapshot.child("Cards").child("cardThree").getValue()).toString();

                        if (dataSnapshot.child("winnerName").exists()) {
                            if (dataSnapshot.child("winnerName").getValue() != null) {
                                final String winnerName = Objects.requireNonNull(dataSnapshot.child("winnerName").getValue()).toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        WinnerName.setText(winnerName);
                                    }
                                });
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WinSequence.setText(winSequence);
                                winCardOne.setBackground(getDrawable(getResources().getIdentifier("c" + CardOne
                                        , "drawable", getPackageName())));
                                winCardTwo.setBackground(getDrawable(getResources().getIdentifier("c" + CardTwo
                                        , "drawable", getPackageName())));
                                winCardThree.setBackground(getDrawable(getResources().getIdentifier("c" + CardThree
                                        , "drawable", getPackageName())));
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            return null;
        }
    }
}
