package com.pacss.teenPatti.gameHandler;

import com.pacss.teenPatti.dataHandler.FirebaseManager;

public class gamePlayManager {

    public static int LIST_SIZE;

    private FirebaseManager firebaseManager;
    public static CircularQueue idQueue;
    private CircularQueue nameQueue;
    public static int pointer;
    public static boolean isServer;
    public static boolean isSeen;
    public static boolean isPack;
    private static gamePlayManager Manager = new gamePlayManager();

    private gamePlayManager() {
        LIST_SIZE = 3;
        firebaseManager = FirebaseManager.getObjectReference();
        idQueue = new CircularQueue(LIST_SIZE);
        nameQueue = new CircularQueue(LIST_SIZE);
        isServer = false;
        isSeen = false;
        isPack = false;
    }

    public static gamePlayManager getInstance() {
        return gamePlayManager.Manager;
    }

    public void resetInstance() {
        gamePlayManager.Manager = null;
        gamePlayManager.Manager = new gamePlayManager();
        FirebaseManager.idList = new String[LIST_SIZE];
        FirebaseManager.nameList = new String[LIST_SIZE];
        firebaseManager.resetLocalStat();
    }

    public void checkServer(final String gameID) {
        idQueue.addListData(FirebaseManager.idList);
        nameQueue.addListData(FirebaseManager.nameList);
        pointer = idQueue.setPointer();
        if (idQueue.getStart().equals(FirebaseManager.UserID)) {
            isServer = true;
            firebaseManager.setServerDetail(gameID, FirebaseManager.UserID, idQueue.getNextToStart());
            firebaseManager.setPlay(gameID);
        } else {
            isServer = false;
        }
    }

    public void dealCards(final String gameID) {
        String[] list = setID();
        Deck deck = new Deck();
        playerDataHandler[] playerHandler = new playerDataHandler[LIST_SIZE];
        for (int x = 0; x < LIST_SIZE; x++) {
            playerHandler[x] = new playerDataHandler();
            playerHandler[x].setPlayerDataHandler(list[x], deck);
        }
        firebaseManager.setPlayerCards(gameID, playerHandler);
        playerDataHandler player = getWinnerPriorities(playerHandler);
        firebaseManager.setGameWinner(gameID, player, "false");
    }

    private String[] setID() {
        idQueue.setPointer(pointer);
        return idQueue.returnString();
    }

    public String[] setPlayer() {
        String[] playerList = new String[LIST_SIZE];
        nameQueue.setPointer(pointer);
        for (int i = 0; i < LIST_SIZE; i++) {
            playerList[i] = nameQueue.get();
        }
        return playerList;
    }

    public String nextToMe() {
        return idQueue.nextToMe();
    }

    public String previousToMe() { return idQueue.previousToMe(); }

    public void serverQuitCheck(final String gameID) {
        if (isServer) {
            firebaseManager.serverQuit(gameID);
        } else {
            firebaseManager.updateNextServer(gameID, idQueue.nextToMe());
        }
    }

    public playerDataHandler getWinnerPriorities(final playerDataHandler[] player) {
        for (int i = 0; i < LIST_SIZE; i++) {
            int winPriority = 0;
            if (i != LIST_SIZE-1) {
                for (int j = i + 1; j < LIST_SIZE; j++) {
                    winPriority = winPriority + player[i].checkWin(player[j].getPlayerCards());
                }
                player[i].setWinningPriority(winPriority);
            } else {
                for (int j = LIST_SIZE-2 + 1; j >= 0; j--) {
                    winPriority = winPriority + player[i].checkWin(player[j].getPlayerCards());
                }
                player[i].setWinningPriority(winPriority);
            }
        }
        playerDataHandler tempPlayer = new playerDataHandler();
        tempPlayer.setWinningPriority(0);
        for (int i = 0; i < LIST_SIZE; i++) {
            if (tempPlayer.getWinningPriority() < player[i].getWinningPriority()) {
                tempPlayer.setPlayerId(player[i].getPlayerId());
                tempPlayer.setWinningPriority(player[i].getWinningPriority());
                tempPlayer.setPlayerCards(player[i].getPlayerCards());
            }
        }

        return tempPlayer;
    }
}
