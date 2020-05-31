package com.pacss.teenPatti.gameHandler;

class Hand {
    private Card[] cards;
    private int[] value;

    Hand(Card[] cards) {
        value = new int[6];
        this.cards = cards;
        CardEvaluator();
    }

    Hand(Deck d) {
        value = new int[6];
        cards = new Card[3];
        for (int x = 0; x < 3; x++) {
            cards[x] = d.drawFromDeck();
        }
        CardEvaluator();
    }

    private void CardEvaluator() {

        int[] ranks = new int[14];
        int[] orderedRanks = new int[3];
        boolean flush=true, straight=false;
        int sameCards=1,sameCards2=1;
        int largeGroupRank=0,smallGroupRank=0;
        int index=0;
        int topStraightValue=0;

        for (int x=0; x<=13; x++) {
            ranks[x]=0;
        }
        for (int x=0; x<=2; x++) {
            ranks[ cards[x].getRank() ]++;
        }
        for (int x=0; x<2; x++) {
            if ( cards[x].getSuit() != cards[x+1].getSuit() )
                flush=false;
        }

        for (int x=13; x>=1; x--) {
             if (ranks[x] > sameCards) {
                 if (sameCards != 1) {
                 //if sameCards was not the default value
                     sameCards2 = sameCards;
                     smallGroupRank = largeGroupRank;
                 }
                 sameCards = ranks[x];
                 largeGroupRank = x;
             } else if (ranks[x] > sameCards2) {
                 sameCards2 = ranks[x];
                 smallGroupRank = x;
             }
        }

        if (ranks[1]==1) { //if ace, run this before because ace is highest card
            orderedRanks[index]=14;
            index++;
        }

        for (int x=13; x>=2; x--) {
            if (ranks[x]==1) {
                orderedRanks[index]=x; //If ACE
                index++;
            }
        }
        
        for (int x=1; x<=9; x++) {
        //can't have straight with lowest value of more than 10
            if (ranks[x]==1 && ranks[x+1]==1 && ranks[x+2]==1 && ranks[x+3]==1 && ranks[x+4]==1) {
                straight=true;
                topStraightValue=x+4; //4 above bottom value
                break;
            }
        }

        if (ranks[10]==1 && ranks[11]==1 && ranks[12]==1 && ranks[13]==1 && ranks[1]==1) {   //Ace High 
            straight=true;
            topStraightValue=14; //Higher than King
        }
        
        for (int x=0; x<=5; x++) {
            value[x]=0;
        }

        //start hand evaluation
        if (sameCards==3 && sameCards2!=2) {
            value[0]=1;
            value[1]= largeGroupRank;
            value[2]=orderedRanks[0];
            value[3]=orderedRanks[1];
        }
        if (straight && flush) {
            value[0]=2;
            value[1]=orderedRanks[0]; //tie determined by ranks of cards
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
        }
        if (straight && !flush) {
            value[0]=3;
            value[1]=orderedRanks[0]; //tie determined by ranks of cards
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
        }
        if (flush && !straight) {
		    value[0]=4;
		    value[1]=orderedRanks[0]; //tie determined by ranks of cards
		    value[2]=orderedRanks[1];
		    value[3]=orderedRanks[2];
		}
        if (sameCards==2 && sameCards2==1) {
            value[0]=5;                //pair ranked higher than high card
            value[1]=largeGroupRank;   //rank of pair
            value[2]=orderedRanks[0];  //next highest cards.
            value[3]=orderedRanks[1];
            value[4]=orderedRanks[2];
        }
        if ( sameCards==1 ) {
            value[0]=6;          //this is the lowest type of hand, so it gets the lowest value
            value[1]=orderedRanks[0];  //the first determining factor is the highest card,
            value[2]=orderedRanks[1];  //then the next highest card,
            value[3]=orderedRanks[2];  
        }
    }

    String getWinningSequence() {
        String s;
        switch( value[0] ) {
            case 1:
                s = "Three of a Kind " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 2:
                s = "Pure Sequence " + Card.rankAsString(value[1]);
                break;
            case 3:
                s = "Sequence " + Card.rankAsString(value[1]);
                break;
            case 4:
            	s = "Color Sequence";
            	break;
            case 5:
                s = "Pair of " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 6:
                s = "High Card";
                break;
            default:
                s = "error in Hand.display: value[0] contains invalid value";
        }
        return s;
    }

    Card[] getCards() {
        return cards;
    }

    int compareTo(Hand that) {
        if (this.value[0] < that.value[0]) {        //If Players have different Card Sequences.
            return 1;
        } else if (this.value[0] > this.value[0]) {
            return 0;
        } else {        //If Players have same Card Sequences.
            for (int x = 1; x < 6; x++) {
                if (this.value[x] > that.value[x]) {
                    return 1;
                } else if (this.value[x] < that.value[x]) {
                    return 0;
                }
            }
        }
        return -1; //if hands are equal
    }
}