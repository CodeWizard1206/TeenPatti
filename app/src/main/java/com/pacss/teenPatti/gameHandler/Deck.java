package com.pacss.teenPatti.gameHandler;

import java.util.Random;
import java.util.ArrayList;

class Deck {
    private ArrayList<Card> cards;

     Deck() {
        cards = new ArrayList<>();
        int index_1, index_2;
        Random generator = new Random();
        Card temp;

        for (short a=0; a<=3; a++) {
            for (short b=0; b<=12; b++) {
               cards.add( new Card(a,b) );
            }
        }

        int size = cards.size() -1;

        for (short i=0; i<100; i++) {
            index_1 = generator.nextInt( size );
            index_2 = generator.nextInt( size );
            temp = cards.get( index_2 );
            cards.set( index_2 , cards.get( index_1 ) );
            cards.set( index_1, temp );
        }
    }

    Card drawFromDeck() { return cards.remove( cards.size()-1 ); }

}