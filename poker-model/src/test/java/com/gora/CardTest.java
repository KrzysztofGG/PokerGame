package com.gora;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void getRank() {
        Card card = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        assertEquals(Card.Rank.ACE, card.getRank());
    }

    @Test
    void getSuit() {
        Card card = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        assertEquals(Card.Suit.CLUBS, card.getSuit());
    }

    @Test
    void compareTo() {
        Card card1 = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        Card card2 = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        assertEquals(0, card1.compareTo(card2));
    }

    @Test
    void testEquals() {
        Card card1 = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        Card card2 = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        assertEquals(card1, card2);
    }

    @Test
    void testHashCode() {
        Card card1 = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        Card card2 = new Card(Card.Rank.ACE, Card.Suit.CLUBS);
        assertEquals(card1.hashCode(), card2.hashCode());
    }
}