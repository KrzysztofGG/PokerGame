package com.gora;

import java.util.ArrayList;

/**
 * class responsible for holding player hand value,
 * printing it and
 * evaluating winner
 *
 */
public class Hand {
    protected int[] handValue;
    protected int[] ranks;
    protected final ArrayList<Card> cards;
    public Hand(){
        this.cards = new ArrayList<>();
        this.ranks = new int[13];
        this.handValue = new int[6];
    }

    /**
     * prepares hand for evaluation
     */
    public void prepareHand(){
        this.cards.sort(Card::compareTo);
        this.handValue = new int[6];
        this.ranks = makeRanks();
        this.handValue = makeHandValue();
    }

    public Hand(ArrayList<Card> cards){
        this.cards = cards;
        this.cards.sort(Card::compareTo);
        this.handValue = new int[6];
        this.ranks = makeRanks();
        this.handValue = makeHandValue();
    }

    public int[] getHandValue(){
        return this.handValue;
    }
    public void addCard(Card card){
        this.cards.add(card);
    }

    /**
     * prepares reansk of cards in hand
     * @return array of ranks
     */
    protected int[] makeRanks(){
        this.ranks = new int[13];
        for(int i=0; i<13; i++){
            ranks[i] = 0;
        }
        for(int i=0; i<5; i++){
            ranks[cards.get(i).rank.ordinal()]++;
        }
        return ranks;
    }

    /**
     * prepares same cards in hand
     * @return array of same cards
     */
    protected int[] sameCards(){
        int sameCards1 = 1, sameCards2 = 1; //sameCards1 - number of cards with the same rank, sameCards2 - number of cards with the same rank
        int smallGroupRank=0, largeGroupRank=0; //index of small and large group of cards with the same rank
        for(int i=12; i>=0; i--){
            if(this.ranks[i] > sameCards1){
                if(sameCards1 != 1){
                    sameCards2 = sameCards1;
                    smallGroupRank = largeGroupRank;
                }
                sameCards1 = this.ranks[i];
                largeGroupRank = i;
            }
            else if(this.ranks[i] > sameCards2){
                sameCards2 = this.ranks[i];
                smallGroupRank = i;
            }
        }
        int[] sameCards = new int[4];
        sameCards[0] = sameCards1;
        sameCards[1] = sameCards2;
        sameCards[2] = largeGroupRank;
        sameCards[3] = smallGroupRank;
        return sameCards;
    }

    /**
     * checks if hand is flush
     * @return true if hand is flush
     */
    protected boolean isFlush(){
        for (int i = 0; i < 4; i++) {
            if (cards.get(i).suit != cards.get(i + 1).suit) {
                return false;
            }
        }
        return true;
    }
    /**
     * checks if hand is straight
     * @return true if hand is straight
     */
    protected boolean isStraight() {
        for (int i = 0; i < 9; i++) {
            if (ranks[i] == 1 && ranks[i + 1] == 1 && ranks[i + 2] == 1 && ranks[i + 3] == 1 && ranks[i + 4] == 1) {
                return true;
            }
        }
        //special case for ace
        return ranks[0] == 1 && ranks[1] == 1 && ranks[2] == 1 && ranks[3] == 1 && ranks[12] == 1;
    }
    /**
     * checks if hand is straight flush
     * @return true if hand is straight flush
     */
    protected boolean isStraightFlush(){
        return isStraight() && isFlush();
    }
    /**
     * checks if hand is royal flush
     * @return true if hand is royal flush
     */
    protected boolean isRoyalFlush(){
        return isStraightFlush() && ranks[12] == 1 && ranks[11] == 1 && ranks[10] == 1 && ranks[9] == 1 && ranks[8] == 1;
    }
    /**
     * checks if hand is four of a kind
     * @return true if hand is four of a kind
     */
    protected boolean isFourOfAKind(){
        return sameCards()[0] == 4;
    }
    /**
     * checks if hand is full house
     * @return true if hand is full house
     */
    protected boolean isFullHouse(){
        return sameCards()[0] == 3 && sameCards()[1] == 2;
    }
    /**
     * checks if hand is three of a kind
     * @return true if hand is three of a kind
     */
    protected boolean isThreeOfAKind(){
        return sameCards()[0] == 3;
    }

    protected boolean isTwoPair(){
        return sameCards()[0] == 2 && sameCards()[1] == 2;
    }
    protected boolean isPair(){
        return sameCards()[0] == 2 && sameCards()[1] == 1;
    }
    /**
     * prepares hand value
     * @return array of hand value
     */
    public int[] makeHandValue(){
        int[] sameCards;
        sameCards = sameCards();
        if(isRoyalFlush()){
            handValue[0] = 10;
            handValue[1] = this.cards.get(4).suit.ordinal();
        }
        else if(isStraightFlush()){
            handValue[0] = 9;
            handValue[1] = this.cards.get(4).rank.ordinal();
            handValue[2] = this.cards.get(4).suit.ordinal();
        }
        else if(isFourOfAKind()){
            handValue[0] = 8;
            handValue[1] = sameCards[2];
            handValue[2] = this.cards.get(4).rank.ordinal();
        }
        else if(isFullHouse()){
            handValue[0] = 7;
            handValue[1] = sameCards[2];
            handValue[2] = sameCards[3];
        }
        else if(isFlush()){
            handValue[0] = 6;
            handValue[1] = this.cards.get(4).suit.ordinal();
        }
        else if(isStraight()){
            handValue[0] = 5;
            if(this.cards.get(4).rank.ordinal() == 12 && this.cards.get(3).rank.ordinal() == 3){
                handValue[1] = 3;
            }
            else{
                handValue[1] = this.cards.get(4).rank.ordinal();
            }
        }
        else if(isThreeOfAKind()){
            handValue[0] = 4;
            handValue[1] = sameCards[2];
            handValue[2] = sameCards[3];
        }
        else if(isTwoPair()){
            handValue[0] = 3;
            handValue[1] = Math.max(sameCards[2], sameCards[3]);
            handValue[2] = Math.min(sameCards[2], sameCards[3]);
        }
        else if(isPair()){
            handValue[0] = 2;
            handValue[1] = sameCards[2];
        }
        else{
            handValue[0] = 1;
            for(int i=0; i<5; i++){
                handValue[i+1] = cards.get(4-i).rank.ordinal();
            }
        }
        return handValue;
    }

    /**
     * compares two hands
     * @param hand - other hand
     * @return 1 if this hand is better, 0 if hands are equal, -1 if other hand is better
     */
    public int compareHands(Hand hand){
        for(int i=0; i<6; i++){
            if(this.handValue[i] > hand.handValue[i]){
                return 1;
            }
            else if(this.handValue[i] < hand.handValue[i]){
                return -1;
            }
        }
        return 0;
    }
    /**
     * prints hand
     */
    public String printFigure(){
        return switch (this.handValue[0]) {
            case 10 -> "ROYAL FLUSH, suit: " + Card.Suit.values()[this.handValue[1]];
            case 9 ->
                    "STRAIGHT FLUSH " + Card.Rank.values()[this.handValue[1]] + " high, suit: " + Card.Suit.values()[this.handValue[2]];
            case 8 ->
                    "FOUR OF A KIND " + Card.Rank.values()[this.handValue[1]] + "s, " + Card.Rank.values()[this.handValue[2]] + " kicker";
            case 7 ->
                    "FULL HOUSE " + Card.Rank.values()[this.handValue[1]] + "s full of " + Card.Rank.values()[this.handValue[2]] + "s";
            case 6 -> "FLUSH of " + Card.Suit.values()[this.handValue[1]];
            case 5 -> "STRAIGHT " + Card.Rank.values()[this.handValue[1]] + " high";
            case 4 ->
                    "THREE OF A KIND " + Card.Rank.values()[this.handValue[1]] + "s, " + Card.Rank.values()[this.handValue[2]] + " kicker";
            case 3 ->
                    "TWO PAIR " + Card.Rank.values()[this.handValue[1]] + "s and " + Card.Rank.values()[this.handValue[2]] + "s";
            case 2 -> "PAIR of " + Card.Rank.values()[this.handValue[1]] + "s";
            case 1 -> "HIGH CARD " + Card.Rank.values()[this.handValue[1]];
            default -> "something went wrong :/";
        };
    }
}
