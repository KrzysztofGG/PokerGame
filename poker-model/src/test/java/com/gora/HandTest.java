package com.gora;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class HandTest  {
    @Test
    public void isHandHighCard(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.DEUCE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.JACK, Card.Suit.HEARTS));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(1, hand.getHandValue()[0]);
        assertEquals("HIGH CARD " + Card.Rank.values()[hand.getHandValue()[1]], hand.printFigure());
    }
    @Test
    void isHandPair(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.JACK, Card.Suit.HEARTS));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(2, hand.getHandValue()[0]);
        assertEquals("PAIR of " + Card.Rank.values()[hand.getHandValue()[1]] + "s", hand.printFigure());
        assertTrue(hand.isPair());

    }
    @Test
    void isHandTwoPair(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.JACK, Card.Suit.HEARTS));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(3, hand.getHandValue()[0]);
        assertEquals("TWO PAIR " + Card.Rank.values()[hand.getHandValue()[1]] + "s and " + Card.Rank.values()[hand.getHandValue()[2]] + "s" , hand.printFigure());
        assertTrue(hand.isTwoPair());
    }
    @Test
    void isHandThreeOfAKind(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.JACK, Card.Suit.HEARTS));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(4, hand.getHandValue()[0]);
        assertEquals("THREE OF A KIND " + Card.Rank.values()[hand.getHandValue()[1]] + "s, " + Card.Rank.values()[hand.getHandValue()[2]] + " kicker", hand.printFigure());
        assertTrue(hand.isThreeOfAKind());
    }
    @Test
    void isHandStraight(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.QUEEN, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.JACK, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.TEN, Card.Suit.HEARTS));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(5, hand.getHandValue()[0]);
        assertEquals("STRAIGHT " + Card.Rank.values()[hand.getHandValue()[1]] + " high", hand.printFigure());
        assertTrue(hand.isStraight());
    }
    @Test
    void isHandFlush(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.FOUR, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.DEUCE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.JACK, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.TEN, Card.Suit.SPADES));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(6, hand.getHandValue()[0]);
        assertEquals("FLUSH of " + Card.Suit.values()[hand.getHandValue()[1]]  , hand.printFigure());
        assertTrue(hand.isFlush());
    }
    @Test
    void isHandFullHouse(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.HEARTS));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(7, hand.getHandValue()[0]);
        assertEquals("FULL HOUSE " + Card.Rank.values()[hand.getHandValue()[1]] + "s full of " + Card.Rank.values()[hand.getHandValue()[2]] + "s", hand.printFigure());
        assertTrue(hand.isFullHouse());
    }
    @Test
    void isHandFourOfAKind(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.DIAMONDS));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.HEARTS));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(8, hand.getHandValue()[0]);
        assertEquals("FOUR OF A KIND " + Card.Rank.values()[hand.getHandValue()[1]] + "s, " + Card.Rank.values()[hand.getHandValue()[2]] + " kicker", hand.printFigure());
        assertTrue(hand.isFourOfAKind());
    }
    @Test
    void isHandStraightFlush(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.DEUCE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.THREE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.FOUR, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.FIVE, Card.Suit.SPADES));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(9, hand.getHandValue()[0]);
        assertEquals("STRAIGHT FLUSH " + Card.Rank.values()[hand.getHandValue()[1]] + " high, suit: " + Card.Suit.values()[hand.getHandValue()[2]] ,hand.printFigure());
        assertTrue(hand.isStraightFlush());
    }
    @Test
    void isHandRoyalFlush(){
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.KING, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.QUEEN, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.JACK, Card.Suit.SPADES));
        hand.addCard(new Card(Card.Rank.TEN, Card.Suit.SPADES));
        hand.prepareHand();
        hand.makeHandValue();
        assertEquals(10, hand.getHandValue()[0]);
        assertEquals("ROYAL FLUSH, suit: " + Card.Suit.values()[hand.getHandValue()[1]], hand.printFigure());
        assertTrue(hand.isRoyalFlush());
    }



}