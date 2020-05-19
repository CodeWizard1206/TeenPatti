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
    private boolean error_flag = false;
    private static boolean gameJoined = false;
    private static long playerCount;

    private FirebaseManager() {
        final FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();
        payDB = firebaseDB.getReference("adminPayDetail");
        guestDB = firebaseDB.getReference("GuestUsers");
        gameDB = firebaseDB.getReference("GamesDatabase");

        getAdminPayDetail();
    }

    public static FirebaseManager getObjectReference() {
        return Object;
    }

    private void getAdminPayDetail() {
        payDB.addValueEventListener(new ValueEventListener() {
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

    public void guestUserLogin(final UserHandler User) {
        UserID = guestDB.push().getKey();
        assert UserID != null;
        DatabaseReference dataInsert = guestDB.child(UserID);
        dataInsert.setValue(User);
        Log.v(TAG, "NEW USER LOGGED IN, NEW_LOGIN:SET");
    }

    public void getGuestUser(final String user) {
        guestDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user)) {
                    UserID = user;
                    User.setUser(Objects.requireNonNull(dataSnapshot.child(user).child("userName").getValue()).toString()
                            ,Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(user).child("totalTokens").getValue()).toString())
                            ,Objects.requireNonNull(dataSnapshot.child(user).child("userType").getValue()).toString()
                            ,Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(user).child("totalGamesPlayed").getValue()).toString())
                            ,Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(user).child("totalLosses").getValue()).toString())
                            ,Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(user).child("totalWins").getValue()).toString())
                    );
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Log.v(TAG, "USER DETAILS FETCHED, USER_FETCH:DONE");
    }

    public void updateTokens() {
        DatabaseReference dbRef;
        if (User.getUserType().equals("Guest")) {
            dbRef = guestDB.child(UserID);
        } else {
            dbRef = guestDB.child(UserID);
        }
        dbRef.child("totalTokens").setValue(Integer.toString(User.getTotalTokens()));
    }

    public void deleteGuest() {
        if (User.getUserType().equals("Guest")) {
            guestDB.child(UserID).removeValue();
        }
        Log.v(TAG, "GUEST USER DELETED, DEL_GUEST:DONE");
    }

    public String getGameID(final String gameType) {
        String gameId = gameDB.push().getKey();
        gameDB.child(Objects.requireNonNull(gameId)).child("GameType").setValue(gameType);
        gameDB.child(gameId).child("GameStatus").setValue("WAITING");
        return  gameId;
    }

    public boolean gotoGame(final String gameID, final String gameType) {
        DatabaseReference dbRef = gameDB.child(gameID);
        final DatabaseReference finalDbRef = dbRef;
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "Checking Room Details, ROOM_CHECK:SET");
                    if (Objects.requireNonNull(dataSnapshot.child("GameType").getValue()).toString().equals(gameType)) {
                        if (dataSnapshot.child("Players").getChildrenCount() < 6) {
                            if (!gameJoined) {
                                finalDbRef.child("Players").child(UserID).child("playerName").setValue(User.getUserName());
                                finalDbRef.child("Players").child(UserID).child("playerTokens").setValue(User.getTotalTokens());
                                gameJoined = true;
                                Log.v(TAG, "Player Joined a Game, JOIN_GAME:TRUE");
                            }
                        } else if (dataSnapshot.child("Players").getChildrenCount() == 6 && gameJoined) {
                            finalDbRef.child("GameStatus").setValue("ONGOING");
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

        if (error_flag) {
            error_flag = false;
            Log.v(TAG, "Fail to Join Game, JOIN_GAME:FALSE");
            return false;
        } else {
            return true;
        }
    }

    public long getPlayerCount(final String gameID) {
        gameDB.child(gameID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerCount = dataSnapshot.child("Players").getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.v(TAG, "Player Count Retrieved");
        return playerCount;
    }

    public void removePlayer(final String gameID) {
        gameDB.child(gameID).child("Players").child(UserID).removeValue();
        gameJoined = false;
        Log.v(TAG, "Player Removed");
    }

    public static boolean isConnectionAvailable(Context context) {
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

