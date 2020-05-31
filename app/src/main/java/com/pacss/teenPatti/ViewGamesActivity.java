package com.pacss.teenPatti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.gameHandler.GameDataHandler;
import java.util.Objects;

public class ViewGamesActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
    private GameDataHandler[] data;
    private boolean emptyDataFlag = false;
    private boolean dataFetchedFlag = false;
    private AppDialogHandler dialog;
    private RelativeLayout noDataMsg;
    private RecyclerView gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_view_games);

        dialog = new AppDialogHandler(ViewGamesActivity.this);
        dialog.setCustomDialog(R.layout.loader_view, AppDialogHandler.WRAP_CONTENT);
        noDataMsg = findViewById(R.id.noResponseMsg);
        gameList = findViewById(R.id.gameList);
        final MaterialCardView backButton = findViewById(R.id.backCardButton);
        final MaterialCardView refreshButton = findViewById(R.id.refreshButton);

        new GetGameDataThread().execute();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetGameDataThread().execute();
                showData();
            }
        });

        showData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetGameDataThread().execute();
        showData();
    }

    private void showData() {
        new CountDownTimer(30000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (emptyDataFlag) {
                    noDataMsg.setVisibility(View.VISIBLE);
                    dialog.clear();
                    cancel();
                } else if (dataFetchedFlag) {
                    noDataMsg.setVisibility(View.INVISIBLE);
                    if (data != null && data.length >= 1) {
                        GameListAdapter adapter = new GameListAdapter(data, ViewGamesActivity.this);
                        gameList.setAdapter(adapter);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(ViewGamesActivity.this);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        gameList.setLayoutManager(layoutManager);
                        dialog.clear();
                        cancel();
                    }
                }
            }

            @Override
            public void onFinish() {
                noDataMsg.setVisibility(View.VISIBLE);
                dialog.clear();
            }
        }.start();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    class GetGameDataThread extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.showDialog();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            final DatabaseReference dbRef = firebaseManager.getGameDB();
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (dataSnapshot.getChildrenCount() == 0) {
                        emptyDataFlag = true;
                    } else {
                        int count = (int) dataSnapshot.getChildrenCount();
                        data = new GameDataHandler[count];
                        int x = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("GameStatus").getValue() != null
                                    && ds.child("GameType").getValue() != null
                                    && ds.child("potAmount").getValue() != null
                                    && ds.child("betAmount").getValue() != null) {

                                String id = ds.getKey();
                                String status = Objects.requireNonNull(ds.child("GameStatus").getValue()).toString();
                                String type = Objects.requireNonNull(ds.child("GameType").getValue()).toString();
                                String pot = Objects.requireNonNull(ds.child("potAmount").getValue()).toString();
                                String bot = Objects.requireNonNull(ds.child("betAmount").getValue()).toString();
                                data[x] = new GameDataHandler(id, type, pot, status, bot);
                                x++;
                            }
                        }
                        dataFetchedFlag = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            return null;
        }
    }
}
