package com.pacss.teenPatti.gameHandler;

public class PlayerDataHolder {
    private String playerName;
    private String playerTokens;
    private String playerSeenStatus;
    private Card[] playerCards;

    public PlayerDataHolder(String playerName, String playerTokens, String playerSeenStatus, Card[] playerCards) {
        this.playerName = playerName;
        this.playerTokens = playerTokens;
        this.playerSeenStatus = playerSeenStatus;
        this.playerCards = playerCards;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerTokens() {
        return playerTokens;
    }

    public void setPlayerTokens(String playerTokens) {
        this.playerTokens = playerTokens;
    }

    public String getPlayerSeenStatus() {
        return playerSeenStatus;
    }

    public void setPlayerSeenStatus(String playerSeenStatus) {
        this.playerSeenStatus = playerSeenStatus;
    }

    public Card[] getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(Card[] playerCards) {
        this.playerCards = playerCards;
    }
}
