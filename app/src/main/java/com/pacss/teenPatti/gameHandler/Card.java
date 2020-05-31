package com.pacss.teenPatti.gameHandler;

public class Card {
    private short rank, suit;

    private static String[] suits = { "hearts", "spades", "diamonds", "clubs" };
    private static String[] ranks  = { "ace", "2", "3", "4", "5", "6", "7",
                                       "8", "9", "10", "jack", "queen", "king" };

    static String rankAsString(int __rank) {
        return ranks[__rank];
    }

    public Card(short suit, short rank) {
        this.rank=rank;
        this.suit=suit;
    }

    public @Override String toString() {
          return ranks[rank] + "_of_" + suits[suit];
    }

    public short getRank() {
         return rank;
    }

    public short getSuit() {
        return suit;
    }
}
