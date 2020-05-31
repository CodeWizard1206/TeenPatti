package com.pacss.teenPatti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.dataHandler.UserHandler;
import com.pacss.teenPatti.gameHandler.gamePlayManager;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private final UserHandler User = UserHandler.UserHandlerReference();
    private final FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
    private String TAG = HomeActivity.class.getSimpleName();
    private SharedPreferences.Editor editor;
    private MaterialButton done;
    private MaterialCardView copyButton;
    private static AppCompatTextView chipCount;
    private AppCompatEditText gameIdTxt;
    private AppDialogHandler adminPanel;
    private AppDialogHandler gameID;
    private ClipboardManager clipboardManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = this.getSharedPreferences("LOGIN_DATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        adminPanel = new AppDialogHandler(HomeActivity.this);
        gameID = new AppDialogHandler(HomeActivity.this);
        MaterialCardView admincard = findViewById(R.id.adminCard);
        MaterialCardView exitCard = findViewById(R.id.exitCard);
        MaterialCardView chipCard = findViewById(R.id.buyChipCard);
        CardView noLimitPlay = findViewById(R.id.noLimitPlay);
        CardView jokerPlay = findViewById(R.id.jokerPlay);
        AppCompatTextView userName = findViewById(R.id.userName);
        chipCount = findViewById(R.id.chipAmount);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        adminPanel.setCustomDialog(R.layout.admin_panel, AppDialogHandler.MATCH_PARENT);
        gameID.setCustomDialog(R.layout.login_name_dialog, AppDialogHandler.MATCH_PARENT);
        gameIdTxt = gameID.getCustomDialog().findViewById(R.id.userNameInput);
        done = gameID.getCustomDialog().findViewById(R.id.acceptNameButton);
        MaterialButton cancel = gameID.getCustomDialog().findViewById(R.id.cancelButton);
        gameIdTxt.setHint("Enter Game Room ID");

        final String[] Games = new String[] {"No Limit", "Joker"};

        if (User.getUserType() != null) {
            if (User.getUserType().equals("Admin")) {
                admincard.setVisibility(View.VISIBLE);

                admincard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adminPanel.showDialog();

                        final MaterialButton close, newGame, viewGame, getID, back;
                        final ConstraintLayout gameController, gameGenerator;
                        final AppCompatEditText gameIdBox;

                        close = adminPanel.getCustomDialog().findViewById(R.id.closeButton);
                        back = adminPanel.getCustomDialog().findViewById(R.id.backButton);
                        newGame = adminPanel.getCustomDialog().findViewById(R.id.newGame);
                        viewGame = adminPanel.getCustomDialog().findViewById(R.id.viewGame);
                        gameController = adminPanel.getCustomDialog().findViewById(R.id.gameControl);
                        gameGenerator = adminPanel.getCustomDialog().findViewById(R.id.newGamePanel);
                        getID = adminPanel.getCustomDialog().findViewById(R.id.getIdButton);
                        gameIdBox = adminPanel.getCustomDialog().findViewById(R.id.gameIdText);
                        copyButton = adminPanel.getCustomDialog().findViewById(R.id.copyButton);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(adminPanel.getCustomDialog().getContext(), R.layout.drop_menu_pop, Games);

                        final AutoCompleteTextView dropMenu = adminPanel.getCustomDialog().findViewById(R.id.gameList);
                        dropMenu.setAdapter(adapter);

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adminPanel.clear();
                                gameController.setVisibility(View.VISIBLE);
                                gameGenerator.setVisibility(View.GONE);
                                gameIdBox.setText("");
                                gameIdBox.setVisibility(View.GONE);
                                back.setVisibility(View.GONE);
                                copyButton.setVisibility(View.GONE);
                                dropMenu.setText(null);
                                HomeActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                            }
                        });

                        newGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gameController.setVisibility(View.GONE);
                                gameGenerator.setVisibility(View.VISIBLE);
                                back.setVisibility(View.VISIBLE);

                                getID.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (dropMenu.getText().toString().equals("Joker")) {
                                            gameIdBox.setText(firebaseManager.getGameID(dropMenu.getText().toString(), 500));
                                        } else if (dropMenu.getText().toString().equals("No Limit")) {
                                            gameIdBox.setText(firebaseManager.getGameID(dropMenu.getText().toString(), 200));
                                        }
                                        Toast.makeText(HomeActivity.this, "Game Room Created Successfully", Toast.LENGTH_LONG).show();
                                        gameIdBox.setVisibility(View.VISIBLE);
                                        copyButton.setVisibility(View.VISIBLE);

                                        copyButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ClipData clipData = ClipData.newPlainText("GameID", Objects.requireNonNull(gameIdBox.getText()).toString());
                                                clipboardManager.setPrimaryClip(clipData);
                                                Toast.makeText(HomeActivity.this, "Game ID Copied", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });

                                back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gameController.setVisibility(View.VISIBLE);
                                        gameGenerator.setVisibility(View.GONE);
                                        gameIdBox.setText("");
                                        gameIdBox.setVisibility(View.GONE);
                                        back.setVisibility(View.GONE);
                                        copyButton.setVisibility(View.GONE);
                                        dropMenu.setText(null);
                                    }
                                });
                            }
                        });

                        viewGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adminPanel.clear();
                                gameController.setVisibility(View.VISIBLE);
                                gameGenerator.setVisibility(View.GONE);
                                gameIdBox.setText("");
                                gameIdBox.setVisibility(View.GONE);
                                back.setVisibility(View.GONE);
                                copyButton.setVisibility(View.GONE);
                                dropMenu.setText(null);
                                startActivity(new Intent(HomeActivity.this, ViewGamesActivity.class));
                            }
                        });

                    }
                });
            }
        }
        userName.setText(User.getUserName());
        chipCount.setText(Long.toString(User.getTotalTokens()));

        exitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.apply();
                firebaseManager.deleteGuest();
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameIdTxt.setText(null);
                gameID.clear();
            }
        });

        noLimitPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getTotalTokens() >= 200) {
                    gameID.showDialog();
                    new gameCheckThread("No Limit").execute();
                } else {
                    Toast.makeText(HomeActivity.this, "You Don't Have Enough Coins to Play!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        jokerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getTotalTokens() >= 500) {
                    gameID.showDialog();
                    new gameCheckThread( "Joker").execute();
                } else {
                    Toast.makeText(HomeActivity.this, "You Don't Have Enough Coins to Play!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        chipCount.setText(Long.toString(User.getTotalTokens()));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @SuppressLint("StaticFieldLeak")
    class gameCheckThread extends AsyncTask {

        private String gameType, gameId;
        private boolean error_flag = false;
        private final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        private final FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
        private final gamePlayManager Manager = gamePlayManager.getInstance();

        gameCheckThread(final String gameType) {
            this.gameType = gameType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Waiting for Other Players to Join Room...");
            progressDialog.setMessage("Current Player Count : 1");
            progressDialog.setCancelable(false);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            checkGameId();
            return null;
        }


        private void checkGameId() {
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Manager.resetInstance();
                    if (firebaseManager.isConnectionAvailable(HomeActivity.this)) {
                        progressDialog.show();
                        gameId = Objects.requireNonNull(gameIdTxt.getText()).toString();
                        boolean gameJoined = letMeJoinTheGame(gameId, gameType);
                        if (gameJoined) {
                            gamePlayerStatus();
                        } else {
                            firebaseManager.removePlayer(gameId);
                            Toast.makeText(HomeActivity.this, "Failed to Join Game Room, Try Again!!!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        gameIdTxt.setText(null);
                        gameID.clear();
                    } else {
                        gameIdTxt.setText(null);
                        gameID.clear();
                        Toast.makeText(HomeActivity.this, "Network Problem Occurred, Please Resolve All Network Issues & Try Again!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        void gamePlayerStatus() {
            final long[] currentPlayerCount = new long[1];
            currentPlayerCount[0] = 1;
            new CountDownTimer(60000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (currentPlayerCount[0] < gamePlayManager.LIST_SIZE) {
                        currentPlayerCount[0] = firebaseManager.getPlayerCount(gameId);
                        progressDialog.setMessage("Current Player Count : " + currentPlayerCount[0]);
                    } else {
                        firebaseManager.fetchPlayerList(gameId);
                        Toast.makeText(HomeActivity.this, "Welcome, Let's Start the Game", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this, GameLoaderActivity.class);
                        intent.putExtra("GameType", gameType);
                        intent.putExtra("GameID", gameId);
                        startActivity(intent);
                        progressDialog.dismiss();
                        cancel();
                    }
                }

                @Override
                public void onFinish() {
                    firebaseManager.removePlayer(gameId);
                    Toast.makeText(HomeActivity.this, "Waiting TimeOut, No New Player Joined, try Again!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    currentPlayerCount[0] = 0;
                }
            }.start();
        }

        boolean letMeJoinTheGame(final String gameID, final String gameType) {
            DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID);
            final String UserID = FirebaseManager.UserID;
            final DatabaseReference finalDbRef = dbRef;
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.v(TAG, "Checking Room Details, ROOM_CHECK:SET");
                        if (Objects.requireNonNull(dataSnapshot.child("GameType").getValue()).toString().equals(gameType)) {
                            if (!dataSnapshot.child("Players").exists() && Objects.requireNonNull(dataSnapshot.child("GameStatus").getValue()).toString().equals("WAITING")) {
                                if (!FirebaseManager.gameJoined) {
                                    finalDbRef.child("Players").child(UserID).child("playerName").setValue(User.getUserName());
                                    finalDbRef.child("Players").child(UserID).child("isSeen").setValue("false");
                                    finalDbRef.child("Players").child(UserID).child("isPack").setValue("false");
                                    FirebaseManager.gameJoined = true;
                                    Log.v(TAG, "Player Joined a Game, JOIN_GAME:TRUE");
                                }
                            } else if (dataSnapshot.child("Players").getChildrenCount() < gamePlayManager.LIST_SIZE && Objects.requireNonNull(dataSnapshot.child("GameStatus").getValue()).toString().equals("WAITING")) {
                                if (!FirebaseManager.gameJoined) {
                                    finalDbRef.child("Players").child(UserID).child("playerName").setValue(User.getUserName());
                                    finalDbRef.child("Players").child(UserID).child("isSeen").setValue("false");
                                    finalDbRef.child("Players").child(UserID).child("isPack").setValue("false");
                                    FirebaseManager.gameJoined = true;
                                    Log.v(TAG, "Player Joined a Game, JOIN_GAME:TRUE");
                                }
                            } else {
                                error_flag = true;
                            }
                        } else {
                            error_flag = true;
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    error_flag = true;
                }
            });

            new CountDownTimer(20000, 4000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (error_flag || FirebaseManager.gameJoined) {
                        cancel();
                    }
                }

                @Override
                public void onFinish() { }
            }.start();

            if (error_flag) {
                error_flag = false;
                return false;
            } else {
                return true;
            }
        }
    }
}
