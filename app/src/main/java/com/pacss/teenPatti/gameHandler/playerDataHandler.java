package com.pacss.teenPatti.gameHandler;

public class playerDataHandler {

    private String PlayerId = "";
    private Hand PlayerCards;
    private int winningPriority = 0;

    playerDataHandler() { }

    public playerDataHandler(String playerId, Card[] cards) {
        PlayerId = playerId;
        PlayerCards = new Hand(cards);
    }

    void setPlayerDataHandler(String playerId, Deck deck) {
        PlayerId = playerId;
        PlayerCards = new Hand(deck);
    }

    public Card[] getCards() {
        return PlayerCards.getCards();
    }

    void setPlayerId(String playerId) {
        PlayerId = playerId;
    }

    public String getPlayerId() {
        return PlayerId;
    }

    Hand getPlayerCards() {
        return PlayerCards;
    }

    void setPlayerCards(Hand playerCards) {
        PlayerCards = playerCards;
    }

    int checkWin(Hand hand) {
        return PlayerCards.compareTo(hand);
    }

    int getWinningPriority() {
        return winningPriority;
    }

    void setWinningPriority(int winningPriority) {
        this.winningPriority = winningPriority;
    }

    public String getWinningSequence() {
        return PlayerCards.getWinningSequence();
    }
}
