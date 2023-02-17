package com.gora;

import java.util.Objects;

/**
 * Class responsible for representing a single card
 * @author krzys
 */
public class Card implements Comparable<Card>{
    protected Rank rank;
    protected Suit suit;
    public Card(Rank r, Suit s){
        this.rank = r;
        this.suit = s;
    }

    public enum Rank {
        DEUCE, THREE, FOUR, FIVE, SIX,
        SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }
    public enum Suit {CLUBS, DIAMONDS, HEARTS, SPADES}

    public Rank getRank(){
        return this.rank;
    }
    public Suit getSuit() {
        return this.suit;
    }

    @Override
    public int compareTo(Card c){
        if(this.rank.compareTo(c.rank) != 0){
            return this.rank.compareTo(c.rank);
        }
        else{
            return this.suit.compareTo(c.suit);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

}

