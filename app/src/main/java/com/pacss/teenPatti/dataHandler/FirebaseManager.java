package com.pacss.teenPatti.dataHandler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pacss.teenPatti.gameHandler.Card;
import com.pacss.teenPatti.gameHandler.gamePlayManager;
import com.pacss.teenPatti.gameHandler.playerDataHandler;
import java.util.Objects;

public class FirebaseManager {

    private DatabaseReference payDB;
    private DatabaseReference guestDB;
    private DatabaseReference gameDB;
    private static FirebaseManager Object = new FirebaseManager();
    private UserHandler User = UserHandler.UserHandlerReference();
    public static String UserID;
    private  String payeeAccount;
    private String payeeName;
    private String TAG = FirebaseManager.class.getSimpleName();
    public  static boolean gameJoined = false;
    private static long playerCount;
    public static long betAmount;
    public static long potAmount;
    public static String[] idList = new String[gamePlayManager.LIST_SIZE];
    public static String[] nameList = new String[gamePlayManager.LIST_SIZE];

    private FirebaseManager() {
        final FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        payDB = firebaseDB.getReference("adminPayDetail");
        guestDB = firebaseDB.getReference("GuestUsers");
        gameDB = firebaseDB.getReference("GamesDatabase");

        getAdminPayDetail();
    }

    public void resetLocalStat() {
        playerCount = 0;
        gameJoined = false;
    }

    public static FirebaseManager getObjectReference() {
        return Object;
    }

    public DatabaseReference getGameDB() {
        return gameDB;
    }

    public DatabaseReference getUserDB() {
        return guestDB;
    }

    private void getAdminPayDetail() {
        payDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                payeeAccount = Objects.requireNonNull(dataSnapshot.child("payAccount").getValue()).toString();
                payeeName = Objects.requireNonNull(dataSnapshot.child("payName").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public String getPayeeAccount() { return payeeAccount; }

    public String getPayeeName() { return payeeName; }

    public void guestUserLogin(final UserHandler User, final String Uid) {
        if (Uid == null) {
            UserID = guestDB.push().getKey();
            assert UserID != null;
            DatabaseReference dataInsert = guestDB.child(UserID);
            assert UserID != null;
            dataInsert.setValue(User);
            Log.v(TAG, "NEW USER LOGGED IN, NEW_LOGIN:SET");
        } else {
            UserID = Uid;
            final DatabaseReference dataInsert = guestDB.child(UserID);
            dataInsert.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User.setTotalTokens(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("totalTokens").getValue()).toString()));
                    } else {
                        dataInsert.setValue(User);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            Log.v(TAG, "NEW USER LOGGED IN, NEW_LOGIN:SET");
        }
    }

    public void getGuestUser(final String user) {
        guestDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user)) {
                    UserID = user;
                    User.setUser(Objects.requireNonNull(dataSnapshot.child(user).child("userName").getValue()).toString()
                            ,Long.parseLong(Objects.requireNonNull(dataSnapshot.child(user).child("totalTokens").getValue()).toString())
                            ,Objects.requireNonNull(dataSnapshot.child(user).child("userType").getValue()).toString()
                    );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "USER DETAILS FETCHED, USER_FETCH:DONE");
    }

    public void updateTokens() {
        guestDB.child(UserID).child("totalTokens").setValue(Long.toString(User.getTotalTokens()));
    }

    public void deleteGuest() {
        if (User.getUserType().equals("Guest")) {
            guestDB.child(UserID).removeValue();
        }
        Log.v(TAG, "GUEST USER DELETED, DEL_GUEST:DONE");
    }

    public String getGameID(final String gameType, final int Bet) {
        String gameId = gameDB.push().getKey();
        gameDB.child(Objects.requireNonNull(gameId)).child("GameType").setValue(gameType);
        gameDB.child(gameId).child("GameStatus").setValue("WAITING");
        gameDB.child(gameId).child("betAmount").setValue(Integer.toString(Bet));
        gameDB.child(gameId).child("potAmount").setValue(Integer.toString((Bet*6)));
        return  gameId;
    }

    public void updateGameStatus(final String gameID) {
        gameDB.child(gameID).child("GameStatus").setValue("ONGOING");
        Log.v(TAG, "Game Status Updated, GAME_STAT:ONGOING");
    }

    public long getPlayerCount(final String gameID) {
        gameDB.child(gameID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!gamePlayManager.isPack) {
                    playerCount = dataSnapshot.child("Players").getChildrenCount();
                } else {
                    playerCount = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        return playerCount;
    }

    public void getBetAmount(final String gameID) {
        gameDB.child(gameID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!gamePlayManager.isPack) {
                    betAmount = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("betAmount").getValue()).toString());
                    potAmount = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("potAmount").getValue()).toString());
                } else {
                    gameDB.child(gameID).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void removePlayer(final String gameID) {
        gameDB.child(gameID).child("Players").child(UserID).removeValue();
        gameJoined = false;
        Log.v(TAG, "Player Removed");
    }

    public void setServerDetail(final String gameID, final String currentServer, final String nextServer) {
        gameDB.child(gameID).child("serverData").child("currentServer").setValue(currentServer);
        gameDB.child(gameID).child("serverData").child("nextServer").setValue(nextServer);
        gameDB.child(gameID).child("serverData").child("serverQuit").setValue("false");
    }

    public void serverQuit(final String gameID) {
        gameDB.child(gameID).child("serverData").child("serverQuit").setValue("true");
    }

    public void setPlay(final String gameID) {
        gameDB.child(gameID).child("currentPlaying").setValue(UserID);
    }

    public void updatePlay(final String gameID, final String UserId) {
        gameDB.child(gameID).child("currentPlaying").setValue(UserId);
    }

    public void updateIsSeen(final String gameID) {
        gameDB.child(gameID).child("playerSeen").child("playerID").setValue(UserID);
        gameDB.child(gameID).child("playerSeen").child("playerName").setValue(User.getUserName());
        gameDB.child(gameID).child("Players").child(UserID).child("isSeen").setValue("true");
    }

    public void updateIsPack(final String gameID) {
        gameDB.child(gameID).child("playerPack").child("playerID").setValue(UserID);
        gameDB.child(gameID).child("playerPack").child("playerName").setValue(User.getUserName());
    }

    public void fetchPlayerList(final String gameID) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    idList[i] = ds.getKey();
                    nameList[i] = Objects.requireNonNull(ds.child("playerName").getValue()).toString();
                    i++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        final DatabaseReference dbRef = gameDB.child(gameID).child("Players");
        dbRef.addValueEventListener(listener);
        if (gamePlayManager.isPack) {
            dbRef.removeEventListener(listener);
        }
        Log.v(TAG, "Players Data List Fetched");
    }

    public void checkBetAmount(final String gameID, final long myBet, final long newPotAmount) {
        if (!gamePlayManager.isSeen) {
            if (myBet > betAmount) {
                gameDB.child(gameID).child("betAmount").setValue(Long.toString(myBet));
                gameDB.child(gameID).child("playerMove").child("playerID").setValue(UserID);
                gameDB.child(gameID).child("playerMove").child("playerName").setValue(User.getUserName());
                gameDB.child(gameID).child("playerMove").child("betRaise").setValue("true");
                gameDB.child(gameID).child("playerMove").child("moveType").setValue("Blind");
                gameDB.child(gameID).child("playerMove").child("coinValue").setValue(myBet);
            } else {
                gameDB.child(gameID).child("playerMove").child("playerID").setValue(UserID);
                gameDB.child(gameID).child("playerMove").child("playerName").setValue(User.getUserName());
                gameDB.child(gameID).child("playerMove").child("betRaise").setValue("false");
                gameDB.child(gameID).child("playerMove").child("moveType").setValue("Blind");
                gameDB.child(gameID).child("playerMove").child("coinValue").setValue(myBet);
            }
        } else {
            if (myBet > betAmount * 2) {
                gameDB.child(gameID).child("betAmount").setValue(Long.toString(myBet));
                gameDB.child(gameID).child("playerMove").child("playerID").setValue(UserID);
                gameDB.child(gameID).child("playerMove").child("playerName").setValue(User.getUserName());
                gameDB.child(gameID).child("playerMove").child("betRaise").setValue("true");
                gameDB.child(gameID).child("playerMove").child("moveType").setValue("Chaal");
                gameDB.child(gameID).child("playerMove").child("coinValue").setValue(myBet);
            } else {
                gameDB.child(gameID).child("playerMove").child("playerID").setValue(UserID);
                gameDB.child(gameID).child("playerMove").child("playerName").setValue(User.getUserName());
                gameDB.child(gameID).child("playerMove").child("betRaise").setValue("false");
                gameDB.child(gameID).child("playerMove").child("moveType").setValue("Chaal");
                gameDB.child(gameID).child("playerMove").child("coinValue").setValue(myBet);
            }
        }
        gameDB.child(gameID).child("potAmount").setValue(newPotAmount);
        potAmount = newPotAmount;
    }

    public void removePlayerMove(final String gameID) {
        gameDB.child(gameID).child("playerMove").removeValue();
    }

    public void setPlayerCards(final String gameID, final playerDataHandler[] Object) {
        for (int x = 0; x < gamePlayManager.LIST_SIZE; x++) {
            Card[] cards = Object[x].getCards();
            for (int i = 0; i < 3; i++) {
                gameDB.child(gameID).child("Players").child(Object[x].getPlayerId()).child("Cards").child("CardRank_"+i).setValue(Short.toString(cards[i].getRank()));
                gameDB.child(gameID).child("Players").child(Object[x].getPlayerId()).child("Cards").child("CardSuit_"+i).setValue(Short.toString(cards[i].getSuit()));
            }

        }
    }

    public void setGameWinner(final String gameID, final playerDataHandler player, final String winStatus) {
        final Card[] cards = player.getCards();
        gameDB.child(gameID).child("gameWinner").child("winnerID").setValue(player.getPlayerId());
        gameDB.child(gameID).child("gameWinner").child("winningSequence").setValue(player.getWinningSequence());
        gameDB.child(gameID).child("gameWinner").child("Cards").child("cardOne").setValue(cards[0].toString());
        gameDB.child(gameID).child("gameWinner").child("Cards").child("cardTwo").setValue(cards[1].toString());
        gameDB.child(gameID).child("gameWinner").child("Cards").child("cardThree").setValue(cards[2].toString());
        gameDB.child(gameID).child("gameWinner").child("winningStatus").setValue(winStatus);
    }

    public void UpdateWinStatus(final String gameID) {
        gameDB.child(gameID).child("gameWinner").child("winningStatus").setValue("true");
    }

    public void getPlayerCards(final String gameID, final String userID, final Card[] cards) {
        gameDB.child(gameID).child("Players").child(userID).child("Cards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int x = 0; x < 3; x++) {
                    short rank = Short.parseShort(Objects.requireNonNull(dataSnapshot.child("CardRank_" + x).getValue()).toString());
                    short suit = Short.parseShort(Objects.requireNonNull(dataSnapshot.child("CardSuit_" + x).getValue()).toString());
                    cards[x] = new Card(suit, rank);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void updateNextServer(final String gameID, final String nextServer) {
        final DatabaseReference dbRef = gameDB.child(gameID).child("serverData").child("nextServer");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(UserID)) {
                    gameDB.child(gameID).child("serverData").child("nextServer").setValue(nextServer);
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void setSideShowRequest(final String gameID, final String previousUser) {
        gameDB.child(gameID).child("SideShowRequest").child("requestFrom").setValue(UserID);
        gameDB.child(gameID).child("SideShowRequest").child("requestTo").setValue(previousUser);
    }

    public void deleteSideShowRequest(final String gameID) {
        gameDB.child(gameID).child("SideShowRequest").removeValue();
    }

    public void writeSideShowRequestResult(final String gameID, final String requestResult, final Card[] cards) {
        gameDB.child(gameID).child("SideShowRequest").child("requestStatus").setValue(requestResult);
        if (cards != null) {
            gameDB.child(gameID).child("SideShowRequest").child("shownCards").child("CardOne").setValue("c" + cards[0].toString());
            gameDB.child(gameID).child("SideShowRequest").child("shownCards").child("CardTwo").setValue("c" + cards[1].toString());
            gameDB.child(gameID).child("SideShowRequest").child("shownCards").child("CardThree").setValue("c" + cards[2].toString());
        }
    }

    public void requestShow(final String gameID) {
        gameDB.child(gameID).child("showRequested").setValue("true");
    }

    public void EndGame(final String gameID) {
        gameDB.child(gameID).child("GameStatus").setValue("COMPLETED");
        gameDB.child(gameID).child("gameWinner").child("winnerName").setValue(User.getUserName());
        gameDB.child(gameID).child("currentPlaying").removeValue();
        gameDB.child(gameID).child("playerPack").removeValue();
        gameDB.child(gameID).child("playerSeen").removeValue();
        gameDB.child(gameID).child("serverData").removeValue();
        gameDB.child(gameID).child("showRequested").removeValue();
    }

    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }
}