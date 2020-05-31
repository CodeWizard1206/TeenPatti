package com.pacss.teenPatti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pacss.teenPatti.dataHandler.FirebaseManager;
import com.pacss.teenPatti.dataHandler.UserHandler;
import com.pacss.teenPatti.gameHandler.Card;
import com.pacss.teenPatti.gameHandler.CircularQueue;
import com.pacss.teenPatti.gameHandler.gamePlayManager;
import com.pacss.teenPatti.gameHandler.playerDataHandler;
import java.util.Arrays;
import java.util.Objects;

public class GamePlayActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
    private UserHandler User = UserHandler.UserHandlerReference();
    private gamePlayManager Manager = gamePlayManager.getInstance();
    private AppDialogHandler resultDialog;
    private CountDownTimer timer;
    private Card[] cards = new Card[3];
    private playerDataHandler playerDataSelf;
    private String gameType, gameID;
    private String TAG = GamePlayActivity.class.getSimpleName();
    private AppCompatTextView amountShow;
    private TextView startAmount, winSequence, resultTxt;
    private MaterialCardView showCard, showReply;
    private MaterialButton seeButton;
    private MaterialButton packButton;
    private MaterialButton chalButton;
    private MaterialButton showButton;
    private ProgressBar timerProgress;
    private RelativeLayout betLayout;
    private AppCompatImageView showCardOne, showCardTwo, showCardThree, winCardOne, winCardTwo, winCardThree, winResult;
    private RelativeLayout resultCard;
    private static boolean playerPacked = false;
    private boolean playerSeen = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game_play);

        resultDialog = new AppDialogHandler(GamePlayActivity.this);

        if (getIntent().hasExtra("GameType") && getIntent().hasExtra("GameID")) {
            gameType = getIntent().getStringExtra("GameType");
            gameID = getIntent().getStringExtra("GameID");
        }

        ConstraintLayout gameBackground = findViewById(R.id.tableLayout);

        amountShow = findViewById(R.id.amountShow);
        startAmount = findViewById(R.id.startAmount);
        betLayout = findViewById(R.id.chalLayout);
        seeButton = findViewById(R.id.seeButton);
        packButton = findViewById(R.id.packButton);
        chalButton = findViewById(R.id.chalButton);
        showButton = findViewById(R.id.showButton);
        showCard = findViewById(R.id.sideShowRequest);
        showReply = findViewById(R.id.sideShowReply);
        showCardOne = findViewById(R.id.showCardOne);
        showCardTwo = findViewById(R.id.showCardTwo);
        showCardThree = findViewById(R.id.showCardThree);
        resultCard = findViewById(R.id.gameResultCard);
        timerProgress = findViewById(R.id.timerProgress);

        resultDialog.setCustomDialog(R.layout.game_result_dialog, AppDialogHandler.MATCH_PARENT);
        winSequence = resultDialog.getCustomDialog().findViewById(R.id.winSequence);
        resultTxt = resultDialog.getCustomDialog().findViewById(R.id.resultTxt);
        winCardOne = resultDialog.getCustomDialog().findViewById(R.id.winCardOne);
        winCardTwo = resultDialog.getCustomDialog().findViewById(R.id.winCardTwo);
        winCardThree = resultDialog.getCustomDialog().findViewById(R.id.winCardThree);
        winResult = resultDialog.getCustomDialog().findViewById(R.id.winResultImg);

        MaterialButton showAccept = findViewById(R.id.acceptButton);
        MaterialButton showDeny = findViewById(R.id.denyButton);

        final AppCompatImageView cardOne = findViewById(R.id.cardOne);
        final AppCompatImageView cardTwo = findViewById(R.id.cardTwo);
        final AppCompatImageView cardThree = findViewById(R.id.cardThree);

        // Conditional Login to Set Table Theme as Per Game Type
        if (gameType.equals("No Limit")) {
            Drawable drawable = getDrawable(getResources().getIdentifier("card_back"
                    , "drawable", getPackageName()));
            gameBackground.setBackground(getDrawable(getResources().getIdentifier("no_limit_table_image"
                    , "drawable", getPackageName())));
            cardOne.setBackground(drawable);
            cardTwo.setBackground(drawable);
            cardThree.setBackground(drawable);
            User.setTotalTokens(User.getTotalTokens() - 200);
        } else if (gameType.equals("Joker")) {
            Drawable drawable = getDrawable(getResources().getIdentifier("joker_card_back"
                    , "drawable", getPackageName()));
            gameBackground.setBackground(getDrawable(getResources().getIdentifier("joker_table_image"
                    , "drawable", getPackageName())));
            cardOne.setBackground(drawable);
            cardTwo.setBackground(drawable);
            cardThree.setBackground(drawable);
            User.setTotalTokens(User.getTotalTokens() - 500);
        }

        firebaseManager.updateTokens();

        startAmount.setText(Long.toString(FirebaseManager.potAmount));
        amountShow.setText(Long.toString(FirebaseManager.betAmount));

        final ExtendedFloatingActionButton minusBtn = findViewById(R.id.minusBtn);
        final ExtendedFloatingActionButton addBtn = findViewById(R.id.addBtn);

        final TextView playerSelf = findViewById(R.id.player_name);
        final TextView playerOne = findViewById(R.id.player_one_name);
        final TextView playerTwo = findViewById(R.id.player_two_name);
        final TextView playerThree = findViewById(R.id.player_three_name);
        final TextView playerFour = findViewById(R.id.player_four_name);
        final TextView playerFive = findViewById(R.id.player_five_name);
        final TextView playerSelfToken = findViewById(R.id.token_self);

        new CardFetcher(gameID).execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playerDataSelf = new playerDataHandler(FirebaseManager.UserID, cards);
            }
        }, 5000);

        String[] list = Manager.setPlayer(); //Requesting Player list in an Order that, List will always Start from Your ID on your Device

        System.out.println(Arrays.toString(list));
        //Setting Player's Layout on Table
        playerSelf.setText(list[0]);
        playerOne.setText(list[1]);
        playerTwo.setText(list[2]);
        /*playerThree.setText(list[3]);
        playerFour.setText(list[4]);
        playerFive.setText(list[5]);*/

        playerSelfToken.setText(Long.toString(User.getTotalTokens()));

        // Button to decrease Bet Amount if Possible
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(amountShow.getText().toString()) > FirebaseManager.betAmount && !gamePlayManager.isSeen) {
                    long amount = Integer.parseInt(amountShow.getText().toString()) / 2;
                    amountShow.setText(Long.toString(amount));
                } else if (Integer.parseInt(amountShow.getText().toString()) > (FirebaseManager.betAmount * 2) && gamePlayManager.isSeen) {
                    long amount = Integer.parseInt(amountShow.getText().toString()) / 2;
                    amountShow.setText(Long.toString(amount));
                }
            }
        });

        // Button to Increase the Bet Amount if Possible
        addBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(amountShow.getText().toString()) >= FirebaseManager.betAmount && !gamePlayManager.isSeen) {
                    long amount = Integer.parseInt(amountShow.getText().toString()) * 2;
                    amountShow.setText(Long.toString(amount));
                } else if (Integer.parseInt(amountShow.getText().toString()) >= (FirebaseManager.betAmount * 2) && gamePlayManager.isSeen) {
                    long amount = Integer.parseInt(amountShow.getText().toString()) * 2;
                    amountShow.setText(Long.toString(amount));
                }
            }
        });

        timer = new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerProgress.setProgress(timerProgress.getProgress() - 2);
            }

            @Override
            public void onFinish() {
                Toast.makeText(GamePlayActivity.this, "Time is Up!!!", Toast.LENGTH_LONG).show();
                gamePlayManager.isPack = true;
                resultCard.setVisibility(View.VISIBLE);
                firebaseManager.removePlayer(gameID);
                firebaseManager.updateIsPack(gameID);
                firebaseManager.updatePlay(gameID, Manager.nextToMe());
                Manager.serverQuitCheck(gameID);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resultCard.setVisibility(View.GONE);
                        startActivity(new Intent(GamePlayActivity.this, HomeActivity.class));
                        finishAffinity();
                    }
                }, 4000);
            }
        };

        // Button to View Your Cards
        seeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gamePlayManager.isSeen) {
                    playerSeen = true;
                    firebaseManager.updateIsSeen(gameID);
                    gamePlayManager.isSeen = true;
                    cardOne.setBackground(getDrawable(getResources().getIdentifier("c" + cards[0].toString()
                            , "drawable", getPackageName())));
                    cardTwo.setBackground(getDrawable(getResources().getIdentifier("c" + cards[1].toString()
                            , "drawable", getPackageName())));
                    cardThree.setBackground(getDrawable(getResources().getIdentifier("c" + cards[2].toString()
                            , "drawable", getPackageName())));
                    chalButton.setText("Chaal");
                    amountShow.setText(Integer.toString((int) FirebaseManager.betAmount * 2));
                    seeButton.setText("Side\nShow");
                } else {
                    new SideShowThread(gameID).execute();
                }
            }
        });

        // Button to Pack the Game
        packButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gamePlayManager.isPack = true;
                resultCard.setVisibility(View.VISIBLE);
                firebaseManager.removePlayer(gameID);
                firebaseManager.updateIsPack(gameID);
                firebaseManager.updatePlay(gameID, Manager.nextToMe());
                Manager.serverQuitCheck(gameID);
                timer.cancel();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resultCard.setVisibility(View.GONE);
                        startActivity(new Intent(GamePlayActivity.this, HomeActivity.class));
                        finishAffinity();
                    }
                }, 4000);
            }
        });

        // Button to Play your Bet
        chalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountShow.getText().toString();
                if (User.getTotalTokens() >= Integer.parseInt(amount)) {
                    User.setTotalTokens(User.getTotalTokens() - Integer.parseInt(amount));
                    firebaseManager.updateTokens();
                    firebaseManager.checkBetAmount(gameID, Integer.parseInt(amount), FirebaseManager.potAmount + Integer.parseInt(amount));
                    firebaseManager.updatePlay(gameID, gamePlayManager.idQueue.nextToMe());
                    playerSelfToken.setText(Long.toString(User.getTotalTokens()));
                } else {
                    Toast.makeText(GamePlayActivity.this, "You Can't Continue to Play, You Don't Have Enough Coins Left!!!", Toast.LENGTH_SHORT).show();
                    gamePlayManager.isPack = true;
                    resultCard.setVisibility(View.VISIBLE);
                    firebaseManager.removePlayer(gameID);
                    firebaseManager.updateIsPack(gameID);
                    firebaseManager.updatePlay(gameID, Manager.nextToMe());
                    Manager.serverQuitCheck(gameID);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resultCard.setVisibility(View.GONE);
                            startActivity(new Intent(GamePlayActivity.this, HomeActivity.class));
                            finishAffinity();
                        }
                    }, 4000);
                }
                timer.cancel();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseManager.requestShow(gameID);
                timer.cancel();
            }
        });

        showAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseManager.writeSideShowRequestResult(gameID, "true", cards);
                Toast toast = Toast.makeText(GamePlayActivity.this, "Side Show Request Accepted", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                showCard.setVisibility(View.GONE);
            }
        });

        showDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseManager.writeSideShowRequestResult(gameID, "false", null);
                Toast toast = Toast.makeText(GamePlayActivity.this, "Side Show Request Denied", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                showCard.setVisibility(View.GONE);
            }
        });

        // Starting Background Process to Monitor Player Activities like Seen, Pack , Chance of Playing
        StartThreadExecution();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Do Nothing
    }

    private void StartThreadExecution() {
        new gameListenerThread(gameID).execute();
        new NotifierListeners(gameID).execute();
        new playerControlMonitorThread(gameID, seeButton, packButton, chalButton, betLayout, amountShow).execute();
    }

    //Thread to Fetch Cards of Player
    @SuppressLint("StaticFieldLeak")
    class CardFetcher extends AsyncTask {

        private String gameID;

        CardFetcher(String gameID) {
            this.gameID = gameID;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            firebaseManager.getPlayerCards(gameID, FirebaseManager.UserID, cards);
            return null;
        }
    }

    //Background Thread to Request for Server Side Show
    @SuppressLint("StaticFieldLeak")
    class SideShowThread extends AsyncTask {

        private String gameID;

        SideShowThread(String gameID) {
            this.gameID = gameID;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            sideShowCheck(gameID);
            return null;
        }

        private void sideShowCheck(final String gameID) {
            Log.e(TAG, "Side Show Check Requested");
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("Players").child(Manager.previousToMe());
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.child("isSeen").getValue() != null) {
                            if (Objects.requireNonNull(dataSnapshot.child("isSeen").getValue()).toString().equals("true")) {
                                firebaseManager.setSideShowRequest(gameID, Manager.previousToMe());
                                StartSideShowRequestListener(gameID);
                            } else {
                                Toast toast = Toast.makeText(GamePlayActivity.this, "User hasn't Seen the Cards Yet!!!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        private void StartSideShowRequestListener(final String gameID) {
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("SideShowRequest");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.child("requestFrom").getValue() != null
                                && dataSnapshot.child("requestStatus").getValue() != null) {

                            if (Objects.requireNonNull(dataSnapshot.child("requestFrom").getValue()).toString().equals(FirebaseManager.UserID)
                                    && Objects.requireNonNull(dataSnapshot.child("requestStatus").getValue()).toString().equals("true")
                                    && dataSnapshot.child("shownCards").child("CardOne").getValue() != null
                                    && dataSnapshot.child("shownCards").child("CardTwo").getValue() != null
                                    && dataSnapshot.child("shownCards").child("CardThree").getValue() != null) {

                                final String cardOne = Objects.requireNonNull(dataSnapshot.child("shownCards").child("CardOne").getValue()).toString();
                                final String cardTwo = Objects.requireNonNull(dataSnapshot.child("shownCards").child("CardTwo").getValue()).toString();
                                final String cardThree = Objects.requireNonNull(dataSnapshot.child("shownCards").child("CardThree").getValue()).toString();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showCardOne.setBackground(getDrawable(getResources().getIdentifier(cardOne
                                                , "drawable", getPackageName())));
                                        showCardTwo.setBackground(getDrawable(getResources().getIdentifier(cardTwo
                                                , "drawable", getPackageName())));
                                        showCardThree.setBackground(getDrawable(getResources().getIdentifier(cardThree
                                                , "drawable", getPackageName())));
                                        showReply.setVisibility(View.VISIBLE);

                                    }
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showReply.setVisibility(View.GONE);

                                            }
                                        });
                                    }
                                }, 8000);

                                Toast toast = Toast.makeText(GamePlayActivity.this, "Side Show Request Accepted...", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        firebaseManager.deleteSideShowRequest(gameID);
                                    }
                                }, 3000);

                            } else if (Objects.requireNonNull(dataSnapshot.child("requestFrom").getValue()).toString().equals(FirebaseManager.UserID)
                                    && Objects.requireNonNull(dataSnapshot.child("requestStatus").getValue()).toString().equals("false")) {

                                Toast toast = Toast.makeText(GamePlayActivity.this, "Side Show Request Denied!!!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        firebaseManager.deleteSideShowRequest(gameID);
                                    }
                                }, 3000);

                            }
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    //Background Thread to Monitor Player Count Changes during Game and Server Switching
    @SuppressLint("StaticFieldLeak")
    class gameListenerThread extends AsyncTask {

        private String gameID;

        gameListenerThread(final String gameID) {
            this.gameID = gameID;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            StartPlayerListUpdateListener(gameID);
            return null;
        }

        void StartPlayerListUpdateListener(final String gameID) {
            Log.v(TAG, "PlayerListUpdate Listener Started");
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("Players");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (!playerSeen) {
                            Log.v(TAG, "PlayerListUpdate Listener Accessed");
                            gamePlayManager.LIST_SIZE = (int) dataSnapshot.getChildrenCount();
                            gamePlayManager.idQueue = null;
                            gamePlayManager.idQueue = new CircularQueue(gamePlayManager.LIST_SIZE);
                            FirebaseManager.idList = null;
                            FirebaseManager.idList = new String[gamePlayManager.LIST_SIZE];
                            firebaseManager.fetchPlayerList(gameID);
                            if (gamePlayManager.LIST_SIZE > 1) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        gamePlayManager.idQueue.addListData(FirebaseManager.idList);
                                        System.out.println(Arrays.toString(gamePlayManager.idQueue.returnString()));
                                        gamePlayManager.pointer = gamePlayManager.idQueue.setPointer();
                                        if (!gamePlayManager.isServer) {
                                            StartServerListener(gameID);
                                        } else {
                                            String[] playerIdList = gamePlayManager.idQueue.returnString();
                                            UpdateGameWinner(gameID, playerIdList);
                                        }
                                    }
                                }, 6000);
                            } else if (gamePlayManager.LIST_SIZE == 1) {    //Only One Player Left that's Me

                                gamePlayManager.isPack = true;
                                timer.cancel();

                                firebaseManager.setGameWinner(gameID, playerDataSelf, "true");
                                winSequence.setText(playerDataSelf.getWinningSequence());
                                winCardOne.setBackground(getDrawable(getResources().getIdentifier("c" + cards[0].toString()
                                        , "drawable", getPackageName())));
                                winCardTwo.setBackground(getDrawable(getResources().getIdentifier("c" + cards[1].toString()
                                        , "drawable", getPackageName())));
                                winCardThree.setBackground(getDrawable(getResources().getIdentifier("c" + cards[2].toString()
                                        , "drawable", getPackageName())));
                                winResult.setBackground(getDrawable(getResources().getIdentifier("winner"
                                        , "drawable", getPackageName())));
                                resultTxt.setText("You Won");

                                resultDialog.showDialog();
                                firebaseManager.removePlayer(gameID);
                                User.setTotalTokens(User.getTotalTokens() + FirebaseManager.potAmount);
                                firebaseManager.updateTokens();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        firebaseManager.EndGame(gameID);
                                        resultDialog.clear();
                                        startActivity(new Intent(GamePlayActivity.this, HomeActivity.class));
                                        finishAffinity();
                                    }
                                }, 4000);
                            }
                        } else {
                            playerSeen = false;
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        private void StartServerListener(final String gameID) {
            Log.v(TAG, "Sever Listener Started");
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("serverData");
            final DatabaseReference tempDbRef = firebaseManager.getGameDB().child(gameID).child("serverData");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        Log.v(TAG, "Sever Listener Accessed");
                        if (gamePlayManager.LIST_SIZE > 1) {
                            if (Objects.requireNonNull(dataSnapshot.child("nextServer").getValue()).toString().equals(FirebaseManager.UserID)
                                    && Objects.requireNonNull(dataSnapshot.child("serverQuit").getValue()).toString().equals("true")) {

                                String nextServer = gamePlayManager.idQueue.nextToMe();
                                tempDbRef.child("currentServer").setValue(FirebaseManager.UserID);
                                tempDbRef.child("nextServer").setValue(nextServer);
                                tempDbRef.child("serverQuit").setValue("false");
                                gamePlayManager.isServer = true;
                                String[] playerIdList = gamePlayManager.idQueue.returnString();
                                UpdateGameWinner(gameID, playerIdList);
                            }
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        private void UpdateGameWinner(final String gameID, final String[] playerIdList) {
            final playerDataHandler[] players = new playerDataHandler[gamePlayManager.LIST_SIZE];
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("Players");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (playerIdList.length != 0) {
                            if (dataSnapshot.child(playerIdList[gamePlayManager.LIST_SIZE - 1]).child("Cards").child("CardSuit_2").getValue() != null) {
                                Log.v(TAG, "Updating Game Winner");
                                for (int i = 0; i < gamePlayManager.LIST_SIZE; i++) {
                                    Card[] cards = new Card[3];
                                    for (int x = 0; x < 3; x++) {
                                        short rank = Short.parseShort(Objects.requireNonNull(dataSnapshot.child(playerIdList[i]).child("Cards").child("CardRank_" + x).getValue()).toString());
                                        short suit = Short.parseShort(Objects.requireNonNull(dataSnapshot.child(playerIdList[i]).child("Cards").child("CardSuit_" + x).getValue()).toString());
                                        cards[x] = new Card(suit, rank);
                                    }
                                    players[i] = new playerDataHandler(playerIdList[i], cards);
                                }
                                final playerDataHandler newWinPlayer = Manager.getWinnerPriorities(players);
                                new CountDownTimer(6000, 2000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        if (newWinPlayer.getCards() != null) {
                                            cancel();
                                        }
                                    }

                                    @Override
                                    public void onFinish() { }
                                }.start();
                                firebaseManager.setGameWinner(gameID, newWinPlayer, "false");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //Background Thread to Monitor Control Transfer to Current Player
    @SuppressLint("StaticFieldLeak")
    class playerControlMonitorThread extends AsyncTask {

        @SuppressLint("StaticFieldLeak")
        private FirebaseManager firebaseManager = FirebaseManager.getObjectReference();
        private String gameID;
        @SuppressLint("StaticFieldLeak")
        private MaterialButton seeBtn, packBtn, chalBtn;
        @SuppressLint("StaticFieldLeak")
        private RelativeLayout betLayout;
        private AppCompatTextView amountShow;

        playerControlMonitorThread(String gameID, MaterialButton seeBtn, MaterialButton packBtn, MaterialButton chalBtn, RelativeLayout betLayout, AppCompatTextView amountShow) {
            this.gameID = gameID;
            this.seeBtn = seeBtn;
            this.packBtn = packBtn;
            this.chalBtn = chalBtn;
            this.betLayout = betLayout;
            this.amountShow = amountShow;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.v(TAG, "Control Monitor Thread Started");
            firebaseManager.getBetAmount(gameID);
            StartPlayControlListener(gameID);
            StartPlayerMoveListener(gameID);
            return null;
        }

        //Listener Function to Monitor Player Control Transfer from One to Other Player
        void StartPlayControlListener(final String gameID) {
            Log.v(TAG, "PlayControl Listener Started");
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("currentPlaying");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    int delayTime = 0;
                    if (!gamePlayManager.isPack) {

                        if (playerPacked) {
                            delayTime = 10000;
                        } else {
                            delayTime = 2000;
                        }

                        Log.v(TAG, "PlayControl Listener Accessed");
                        final int[] StartAmount = new int[1];

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StartAmount[0] = Integer.parseInt(startAmount.getText().toString());
                            }
                        });

                        if (StartAmount[0] != FirebaseManager.potAmount) {
                            runOnUiThread(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    Log.v(TAG, "Pot Updated");
                                    startAmount.setText(Long.toString(FirebaseManager.potAmount));
                                }
                            });
                        }

                        if (firebaseManager.potAmount >= 20000) {
                            showCardToWin(gameID);
                            Toast toast = Toast.makeText(GamePlayActivity.this, "Pot Limit Reached", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                        if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(FirebaseManager.UserID)) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void run() {
                                            seeBtn.setVisibility(View.VISIBLE);
                                            packBtn.setVisibility(View.VISIBLE);
                                            betLayout.setVisibility(View.VISIBLE);
                                            chalBtn.setVisibility(View.VISIBLE);
                                            timerProgress.setProgress(100);
                                            timerProgress.setVisibility(View.VISIBLE);
                                            if (gamePlayManager.isSeen) {
                                                amountShow.setText(Integer.toString((int) FirebaseManager.betAmount * 2));
                                            } else {
                                                amountShow.setText(Integer.toString((int) FirebaseManager.betAmount));
                                            }
                                            timer.start();
                                            if (firebaseManager.potAmount >= 20000) {
                                                if (gameType.equals("No Limit")) {
                                                    showButton.setVisibility(View.VISIBLE);
                                                } else if (gameType.equals("Joker")) {
                                                    firebaseManager.requestShow(gameID);
                                                }
                                            }
                                        }
                                    });
                                    playerPacked = false;
                                }
                            }, delayTime);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seeBtn.setVisibility(View.INVISIBLE);
                                    packBtn.setVisibility(View.INVISIBLE);
                                    betLayout.setVisibility(View.INVISIBLE);
                                    chalBtn.setVisibility(View.INVISIBLE);
                                    showButton.setVisibility(View.INVISIBLE);
                                    timerProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        //Listener Function to Monitor Player's Move
        void StartPlayerMoveListener(final String gameID) {
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("playerMove");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("playerID").getValue() != null
                                    && dataSnapshot.child("coinValue").getValue() != null
                                    && dataSnapshot.child("playerName").getValue() != null
                                    && dataSnapshot.child("betRaise").getValue() != null && dataSnapshot.child("moveType").getValue() != null) {
                                if (!Objects.requireNonNull(dataSnapshot.child("playerID").getValue()).toString().equals(FirebaseManager.UserID)) {
                                    String coinValue = Objects.requireNonNull(dataSnapshot.child("coinValue").getValue()).toString();
                                    String playerName = Objects.requireNonNull(dataSnapshot.child("playerName").getValue()).toString();
                                    if (Objects.requireNonNull(dataSnapshot.child("betRaise").getValue()).toString().equals("true")) {
                                        Toast toast = Toast.makeText(GamePlayActivity.this, coinValue + " Coins, " + "Raise from " + playerName, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    } else if (Objects.requireNonNull(dataSnapshot.child("moveType").getValue()).toString().equals("Blind")) {
                                        Toast toast = Toast.makeText(GamePlayActivity.this, coinValue + " Coins, " + "Blind Play by " + playerName, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    } else if (Objects.requireNonNull(dataSnapshot.child("moveType").getValue()).toString().equals("Chaal")) {
                                        Toast toast = Toast.makeText(GamePlayActivity.this, coinValue + " Coins, " + "Chaal by " + playerName, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }
                                } else {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            firebaseManager.removePlayerMove(gameID);
                                        }
                                    }, 4000);
                                }
                            }
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void showCardToWin(final String gameID) {
            final DatabaseReference dbref = firebaseManager.getGameDB().child(gameID).child("showRequested");
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "Showing Cards");
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.exists()) {
                            if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("true")) {
                                declareGameWinner(gameID);
                            }
                        }
                    } else {
                        dbref.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        void declareGameWinner(final String gameID) {
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("gameWinner");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e(TAG, "Declaring Winner");
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.child("winnerID").getValue() != null
                                && dataSnapshot.child("winningSequence").getValue() != null
                                && dataSnapshot.child("Cards").child("cardOne").getValue() != null
                                && dataSnapshot.child("Cards").child("cardTwo").getValue() !=  null
                                && dataSnapshot.child("Cards").child("cardThree").getValue() != null) {

                            timer.cancel();

                            if (Objects.requireNonNull(dataSnapshot.child("winnerID").getValue()).toString().equals(FirebaseManager.UserID)) {

                                winSequence.setText(Objects.requireNonNull(dataSnapshot.child("winningSequence").getValue()).toString());
                                winCardOne.setBackground(getDrawable(getResources().getIdentifier("c" + cards[0].toString()
                                        , "drawable", getPackageName())));
                                winCardTwo.setBackground(getDrawable(getResources().getIdentifier("c" + cards[1].toString()
                                        , "drawable", getPackageName())));
                                winCardThree.setBackground(getDrawable(getResources().getIdentifier("c" + cards[2].toString()
                                        , "drawable", getPackageName())));
                                winResult.setBackground(getDrawable(getResources().getIdentifier("winner"
                                        , "drawable", getPackageName())));
                                resultTxt.setText("You Won");

                                resultDialog.showDialog();
                                gamePlayManager.isPack = true;
                                firebaseManager.removePlayer(gameID);
                                firebaseManager.UpdateWinStatus(gameID);
                                User.setTotalTokens(User.getTotalTokens() + FirebaseManager.potAmount);
                                firebaseManager.updateTokens();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        firebaseManager.EndGame(gameID);
                                        resultDialog.clear();
                                        startActivity(new Intent(GamePlayActivity.this, HomeActivity.class));
                                        finishAffinity();
                                    }
                                }, 4000);
                            } else {
                                winSequence.setText(Objects.requireNonNull(dataSnapshot.child("winningSequence").getValue()).toString());
                                winCardOne.setBackground(getDrawable(getResources().getIdentifier("c" + dataSnapshot.child("Cards").child("cardOne").getValue().toString()
                                        , "drawable", getPackageName())));
                                winCardTwo.setBackground(getDrawable(getResources().getIdentifier("c" + dataSnapshot.child("Cards").child("cardTwo").getValue().toString()
                                        , "drawable", getPackageName())));
                                winCardThree.setBackground(getDrawable(getResources().getIdentifier("c" + dataSnapshot.child("Cards").child("cardThree").getValue().toString()
                                        , "drawable", getPackageName())));
                                winResult.setBackground(getDrawable(getResources().getIdentifier("pay_fail"
                                        , "drawable", getPackageName())));
                                resultTxt.setText("You Lost");

                                resultDialog.showDialog();
                                gamePlayManager.isPack = true;
                                firebaseManager.removePlayer(gameID);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        resultDialog.clear();
                                        startActivity(new Intent(GamePlayActivity.this, HomeActivity.class));
                                        finishAffinity();
                                    }
                                }, 4000);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    //Background Thread to Monitor Player Seen, Side Show and Pack Status
    @SuppressLint("StaticFieldLeak")
    class NotifierListeners extends AsyncTask {

        private String gameID;
        private int dataChangeCount = 0;

        NotifierListeners(String gameID) {
            this.gameID = gameID;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            StartIncomingSideShowRequestListener();
            StartSeenNotifierListener();
            StartPackNotifierListener();
            return null;
        }

        //Listener Function to Monitor Whether Any Player has Seen his/her Cards or Not
        private void StartSeenNotifierListener() {
            Log.v(TAG, "Seen Notifier Listener Started");
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("playerSeen");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.exists()) {
                            if (!Objects.requireNonNull(dataSnapshot.child("playerID").getValue()).toString().equals(FirebaseManager.UserID)) {
                                if (dataChangeCount == 1) {
                                    String playerName = Objects.requireNonNull(dataSnapshot.child("playerName").getValue()).toString();
                                    Toast toast = Toast.makeText(GamePlayActivity.this, playerName + " has seen the Cards.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                    dataChangeCount = 0;
                                } else {
                                    dataChangeCount++;
                                }
                            }
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        //Listener Function to Monitor Whether Any Player has Packed or Not
        private void StartPackNotifierListener() {
            Log.v(TAG, "Pack Notifier Listener Started");
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("playerPack");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.exists()) {
                            if (!Objects.requireNonNull(dataSnapshot.child("playerID").getValue()).toString().equals(FirebaseManager.UserID)) {
                                if (dataChangeCount == 1) {
                                    String playerName = Objects.requireNonNull(dataSnapshot.child("playerName").getValue()).toString();
                                    Toast toast = Toast.makeText(GamePlayActivity.this, playerName + " has Packed.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                    dataChangeCount = 0;
                                    playerPacked = true;
                                } else {
                                    dataChangeCount++;
                                }
                            }
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        //Listener Function for Checking are there any Side Show Requests
        private void StartIncomingSideShowRequestListener() {
            final DatabaseReference dbRef = firebaseManager.getGameDB().child(gameID).child("SideShowRequest");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!gamePlayManager.isPack) {
                        if (dataSnapshot.child("requestFrom").getValue() != null
                                && dataSnapshot.child("requestTo").getValue() != null) {

                            if (!Objects.requireNonNull(dataSnapshot.child("requestFrom").getValue()).toString().equals(FirebaseManager.UserID)
                                    && Objects.requireNonNull(dataSnapshot.child("requestTo").getValue()).toString().equals(FirebaseManager.UserID)) {

                                if (!dataSnapshot.child("requestStatus").exists()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showCard.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        dbRef.removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }
}
