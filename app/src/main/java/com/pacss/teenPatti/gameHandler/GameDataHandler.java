package com.pacss.teenPatti.gameHandler;

public class GameDataHandler {
    private String gameID;
    private String gameType;
    private String potValue;
    private String botAmount;
    private String gameStatus;

    public GameDataHandler(String gameID, String gameType, String potValue, String gameStatus, String botAmount) {
        this.gameID = gameID;
        this.gameType = gameType;
        this.potValue = potValue;
        this.gameStatus = gameStatus;
        this.botAmount = botAmount;
    }

    public String getBotAmount() {
        return botAmount;
    }

    public void setBotAmount(String botAmount) {
        this.botAmount = botAmount;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getPotValue() {
        return potValue;
    }

    public void setPotValue(String potValue) {
        this.potValue = potValue;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
