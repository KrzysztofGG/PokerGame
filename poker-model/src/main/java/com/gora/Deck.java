package com.gora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class holding a card deckOfCards
 * @author krzys
 * @since 01.11.2022
 */
public class Deck{
    private final ArrayList<Card> deckOfCards = new ArrayList<>();
    public Deck(){
        this.factory();
        this.shuffle();
    }

    /**
     * creates a sorted deck
     */
    public void factory(){
        for(Card.Rank r : Card.Rank.values()){
            for(Card.Suit s : Card.Suit.values()){
                this.deckOfCards.add(new Card(r, s));
            }
        }
    }
    /**
     * shuffles deck
     */
    public void shuffle(){
        Collections.shuffle(deckOfCards);
    }
    public Card getCard(int i){
        return deckOfCards.get(i);
    }

    public Card getTopCard(){
        return  deckOfCards.remove(0);
    }


    public List<Card> getDeck() {
        return deckOfCards;
    }
}
