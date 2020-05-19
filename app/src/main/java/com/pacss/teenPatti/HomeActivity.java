package com.pacss.teenPatti;

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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.dataHandler.UserHandler;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private final UserHandler User = UserHandler.UserHandlerReference();
    private final FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
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
        gameID.setCustomDialog(R.layout.login_name_dialog, AppDialogHandler.WRAP_CONTENT);
        gameIdTxt = gameID.getCustomDialog().findViewById(R.id.userNameInput);
        done = gameID.getCustomDialog().findViewById(R.id.acceptNameButton);
        MaterialButton cancel = gameID.getCustomDialog().findViewById(R.id.cancelButton);
        gameIdTxt.setHint("Enter Game Room ID");

        final String[] Games = new String[] {"No Limit", "Joker"};

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
                                    gameIdBox.setText(firebaseManager.getGameID(dropMenu.getText().toString()));
                                    Toast.makeText(HomeActivity.this, "Game Room Created Successfully", Toast.LENGTH_LONG).show();
                                    gameIdBox.setVisibility(View.VISIBLE);
                                    copyButton.setVisibility(View.VISIBLE);

                                    copyButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ClipData clipData = ClipData.newPlainText("GameID", gameIdBox.getText().toString());
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

                }
            });
        }
        userName.setText(User.getUserName());
        chipCount.setText(Integer.toString(User.getTotalTokens()));

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
                gameID.showDialog();
                checkGameId("No Limit");
            }
        });

        jokerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameID.showDialog();
                checkGameId("Joker");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        chipCount.setText(Integer.toString(User.getTotalTokens()));
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void checkGameId(final String gameType) {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseManager.gotoGame(Objects.requireNonNull(gameIdTxt.getText()).toString(), gameType)) {
                    Toast.makeText(HomeActivity.this, "Game Room Joined", Toast.LENGTH_SHORT).show();
                    gamePlayerStatus(gameIdTxt.getText().toString());
                } else {
                    firebaseManager.removePlayer(gameIdTxt.getText().toString());
                    Toast.makeText(HomeActivity.this, "Failed to Join Game Room, Try Again!!!", Toast.LENGTH_SHORT).show();
                }
                gameIdTxt.setText(null);
                gameID.clear();
            }
        });
    }

    public void gamePlayerStatus(final String gameID) {
        final long[] currentPlayerCount = new long[1];
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setTitle("Waiting for Other Players to Join Room...");
        progressDialog.setMessage("Current Player Count : 1");
        progressDialog.setCancelable(false);
        progressDialog.show();

        currentPlayerCount[0] = firebaseManager.getPlayerCount(gameID);
        new CountDownTimer(30000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (currentPlayerCount[0] != 6) {
                    currentPlayerCount[0] = firebaseManager.getPlayerCount(gameID);
                    progressDialog.setMessage("Current Player Count : " + currentPlayerCount[0]);
                } else if (currentPlayerCount[0] == 6) {
                    Toast.makeText(HomeActivity.this, "Welcome, Let's Start the Game", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFinish() {
                firebaseManager.removePlayer(gameID);
                Toast.makeText(HomeActivity.this, "Waiting TimeOut, No New Player Joined, try Again!!!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }.start();
    }
}
