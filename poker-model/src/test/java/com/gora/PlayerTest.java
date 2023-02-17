package com.gora;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void isPlayerHandAStraightFlush() {
        //given
        Player player = new Player( "test");
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        player.addCardToHand(new Card(Card.Rank.KING, Card.Suit.CLUBS));
        player.addCardToHand(new Card(Card.Rank.QUEEN, Card.Suit.CLUBS));
        player.addCardToHand(new Card(Card.Rank.JACK, Card.Suit.CLUBS));
        player.addCardToHand(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        //when
        player.getHand().makeHandValue();
        assertEquals(10, player.getHand().getHandValue()[0]);
    }
    @Test
    void isPlayerHandAFourOfAKind() {
        //given
        Player player = new Player( "test");
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.DIAMONDS));
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        player.addCardToHand(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        //when
        player.getHand().makeHandValue();
        assertEquals(8, player.getHand().getHandValue()[0]);
        player.getHand().printFigure();
        //assertEquals("FOUR OF A KIND " + Card.Rank.ACE + "s, " + Card.Rank.TEN + " kicker" );
    }
    @Test
    void isPlayerHandAFullHouse() {
        //given
        Player player = new Player( "test");
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.DIAMONDS));
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        player.addCardToHand(new Card(Card.Rank.TEN, Card.Suit.SPADES));
        player.addCardToHand(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        //when
        player.getHand().makeHandValue();
        assertEquals(7, player.getHand().getHandValue()[0]);
    }

    @Test
    void isRoleChangingRight(){
        //given
        Player player = new Player( "test");
        //when
        player.setRole(Player.Role.DEALER);
        player.updateRole();
        assertEquals(Player.Role.SMALL_BLIND, player.getRole());
        player.updateRole();
        assertEquals(Player.Role.BIG_BLIND, player.getRole());
    }
    @Test
    void isAddingToBalanceRight(){
        //given
        Player player = new Player( "test");
        //when
        player.addToBalance(100);
        assertEquals(1100, player.getBalance());
    }
    @Test
    void isRemovingFromBalanceRight(){
        //given
        Player player = new Player( "test");
        //when
        player.removeFromBalance(100);
        assertEquals(900, player.getBalance());
    }
    @Test
    void isSortingHandWorking(){
        Player player = new Player( "test");
        player.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        player.addCardToHand(new Card(Card.Rank.DEUCE, Card.Suit.DIAMONDS));
        player.addCardToHand(new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
        player.addCardToHand(new Card(Card.Rank.KING, Card.Suit.SPADES));
        player.addCardToHand(new Card(Card.Rank.SEVEN, Card.Suit.CLUBS));
        assertEquals( Card.Rank.ACE, player.getCard(4).getRank());
        assertEquals( Card.Suit.CLUBS, player.getCard(4).getSuit());
    }
    @Test
    void isNextRoundStartRight(){
        Player p = new Player();
        p.nextRoundStarts();
        assertEquals(0, p.getCardsInHand().size());
        assertNull(p.getHand());
        assertEquals(0 ,p.getCurrentBet());
        assertEquals(Player.State.EMPTY,p.getState() );
    }

    @Test
    void isAddCardToHandWorking(){
        Player p = new Player("test");
        Card c = new Card(Card.Rank.DEUCE, Card.Suit.SPADES);
        p.addCardToHand(c);
        assertEquals(c, p. getCard(0));
        c = new Card(Card.Rank.FOUR, Card.Suit.HEARTS);
        p.addCardToHand(c);
        assertEquals(c, p. getCard(1));
        c = new Card(Card.Rank.SIX, Card.Suit.CLUBS);
        p.addCardToHand(c);
        assertEquals(c, p. getCard(2));
        c = new Card(Card.Rank.FIVE, Card.Suit.HEARTS);
        p.addCardToHand(c);
        assertEquals(c, p. getCard(3));
        c = new Card(Card.Rank.KING, Card.Suit.CLUBS);
        p.addCardToHand(c);
        assertEquals(c, p. getCard(4));
        assertNotNull(p.getHand());
    }

    @Test
    void getState() {
        Player p = new Player("test");
        assertEquals(Player.State.EMPTY, p.getState());
    }

    @Test
    void setState() {
        Player p = new Player("test");
        p.setState(Player.State.FOLD);
        assertEquals(Player.State.FOLD, p.getState());
    }

    @Test
    void setRole() {
        Player p = new Player("test");
        p.setRole(Player.Role.SMALL_BLIND);
        assertEquals(Player.Role.SMALL_BLIND, p.getRole());
    }

    @Test
    void getRole() {
        Player p = new Player("test");
        p.setRole(Player.Role.SMALL_BLIND);
        assertEquals(Player.Role.SMALL_BLIND, p.getRole());
    }

    @Test
    void updateRole() {
        Player p = new Player("test");
        p.setRole(Player.Role.SMALL_BLIND);
        p.updateRole();
        assertEquals(Player.Role.BIG_BLIND, p.getRole());
    }

    @Test
    void setCurrentBet() {
        Player p = new Player("test");
        p.setCurrentBet(100);
        assertEquals(100, p.getCurrentBet());
    }

    @Test
    void getCurrentBet() {
        Player p = new Player("test");
        p.setCurrentBet(100);
        assertEquals(100, p.getCurrentBet());
    }

    @Test
    void getHand() {
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        p.addCardToHand(new Card(Card.Rank.DEUCE, Card.Suit.DIAMONDS));
        p.addCardToHand(new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
        p.addCardToHand(new Card(Card.Rank.KING, Card.Suit.SPADES));
        p.addCardToHand(new Card(Card.Rank.SEVEN, Card.Suit.CLUBS));
        assertNotNull(p.getHand());
    }

    @Test
    void getCard() {
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        assertEquals(Card.Rank.ACE, p.getCard(0).getRank());

    }

    @Test
    void getCardsInHand() {
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        p.addCardToHand(new Card(Card.Rank.DEUCE, Card.Suit.DIAMONDS));
        p.addCardToHand(new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
        p.addCardToHand(new Card(Card.Rank.KING, Card.Suit.SPADES));
        p.addCardToHand(new Card(Card.Rank.SEVEN, Card.Suit.CLUBS));
        assertEquals(5, p.getCardsInHand().size());
    }

    @Test
    void getName() {
        Player p = new Player("test");
        assertEquals("test", p.getName());
    }

    @Test
    void getBalance() {
        Player p = new Player("test");
        assertEquals(1000, p.getBalance());
    }

    @Test
    void getIsSwapping() {
        Player p = new Player("test");
        assertFalse(p.getIsSwapping());
    }

    @Test
    void setIsSwapping() {
        Player p = new Player("test");
        p.setIsSwapping(true);
        assertTrue(p.getIsSwapping());
    }


    @Test
    void swapCards(){
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        p.addCardToHand(new Card(Card.Rank.DEUCE, Card.Suit.DIAMONDS));
        p.addCardToHand(new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
        p.addCardToHand(new Card(Card.Rank.KING, Card.Suit.SPADES));
        p.addCardToHand(new Card(Card.Rank.SEVEN, Card.Suit.CLUBS));
        p.swapCard(0, new Card(Card.Rank.SEVEN, Card.Suit.SPADES));
        p.swapCard(1, new Card(Card.Rank.KING, Card.Suit.CLUBS));
        p.swapCard(2, new Card(Card.Rank.FOUR, Card.Suit.SPADES));
        p.swapCard(3, new Card(Card.Rank.DEUCE, Card.Suit.SPADES));
        p.swapCard(4, new Card(Card.Rank.ACE, Card.Suit.SPADES));
        assertEquals(5, p.getCardsInHand().size());
        assertNotNull(p.getHand());
    }
    @Test
    void printHand(){
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        p.addCardToHand(new Card(Card.Rank.DEUCE, Card.Suit.DIAMONDS));
        p.addCardToHand(new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
        p.addCardToHand(new Card(Card.Rank.KING, Card.Suit.SPADES));
        p.addCardToHand(new Card(Card.Rank.SEVEN, Card.Suit.CLUBS));
        assertNotNull(p.printHand());
    }
    void addCardToHand() {
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        assertEquals(Card.Rank.ACE, p.getCard(0).getRank());
    }

    @Test
    void removeCardFromHand() {
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        p.removeCardFromHand(0);
        assertEquals(0, p.getCardsInHand().size());
    }

    @Test
    void nextRoundStarts() {
        Player p = new Player("test");
        p.nextRoundStarts();
        assertEquals(0, p.getCardsInHand().size());
        assertEquals(0, p.getCurrentBet());
        assertEquals(Player.State.EMPTY, p.getState());
    }

    @Test
    void testEquals() {
        Player p = new Player("test");
        Player p2 = new Player("test");
        assertEquals(p, p2);
    }


    @Test
    void sortHand() {
        Player p = new Player("test");
        p.addCardToHand(new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        p.addCardToHand(new Card(Card.Rank.DEUCE, Card.Suit.DIAMONDS));
        p.addCardToHand(new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
        p.addCardToHand(new Card(Card.Rank.KING, Card.Suit.SPADES));
        p.addCardToHand(new Card(Card.Rank.SEVEN, Card.Suit.CLUBS));
        p.sortHand();
        assertEquals(Card.Rank.ACE, p.getCard(4).getRank());
        assertEquals(Card.Rank.KING, p.getCard(3).getRank());
        assertEquals(Card.Rank.SEVEN, p.getCard(2).getRank());
        assertEquals(Card.Rank.FOUR, p.getCard(1).getRank());
        assertEquals(Card.Rank.DEUCE, p.getCard(0).getRank());
    }

    @Test
    void addToBalance() {
        Player p = new Player("test");
        p.addToBalance(100);
        assertEquals(1100, p.getBalance());
    }

    @Test
    void removeFromBalance() {
        Player p = new Player("test");
        p.removeFromBalance(100);
        assertEquals(900, p.getBalance());
    }

    @Test
    void compareTo() {
        Player p = new Player("test");
        Player p2 = new Player("test2");
        p.setRole(Player.Role.DEALER);
        p2.setRole(Player.Role.BIG_BLIND);
        p2.addToBalance(100);
        assertEquals(-2, p.compareTo(p2));
    }
}