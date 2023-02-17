package com.gora;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {
    GameLogic game;
    SocketChannel a = null;
    SocketChannel b = null;
    @BeforeEach
    public void setUp() {
        game = new GameLogic();

        try {
            a = SocketChannel.open();
            b = SocketChannel.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        game.getClients().put(a, new Player("a"));
        game.getClients().put(b, new Player("b"));
    }

    @Test
    void addPlayer() {
        game.addPlayer(new Player("test"));
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    void getPlayers() {
        GameLogic game = new GameLogic();
        game.addPlayer(new Player("test"));
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    void isAllIn() {
        assertFalse(game.isAllIn());
        game.getClients().get(a).setState(Player.State.ALLIN);
        game.turnMaxBet = 200;
        game.getClients().get(b).setCurrentBet(100);
        assertFalse(game.isAllIn());

    }

    @Test
    void nextRound() {
        int round = game.roundNumber;
        game.nextRound();
        assertEquals(round + 1, game.roundNumber);
        assertEquals(0 ,game.getMoneyOnTable());
        game.giveCards();
        game.evaluateRoundWinner();
        assertEquals(0, game.bettingTurn);
        assertEquals(0, game.turnMaxBet);
    }

    @Test
    void shouldTurnEnd() {
        GameLogic game = new GameLogic();
        game.addPlayer(new Player("test"));
        game.addPlayer(new Player("test2"));
        game.getPlayers().get(0).setState(Player.State.CHECK);
        game.getPlayers().get(1).setState(Player.State.CHECK);
        assertTrue(game.shouldTurnEnd());
    }

    @Test
    void shouldSwappingEnd() {
        game.setUpSwapping();
        game.getClients().get(a).setIsSwapping(false);
        game.getClients().get(b).setIsSwapping(false);
        assertTrue(game.shouldSwappingEnd());
        game.bettingTurn = 2;
        game.setUpSwapping();
        assertTrue(game.cardSwapping);
        assertTrue(game.getClients().get(a).getIsSwapping());
        assertTrue(game.getClients().get(b).getIsSwapping());
    }

    @Test
    void shouldGameEnd() {
        assertFalse(game.shouldGameEnd());
        game.getClients().get(a).removeFromBalance(1000);
        assertTrue(game.shouldGameEnd());
    }

        @Test
    void shouldRoundEnd() {
        game.giveCards();
        game.bettingTurn = 4;
        assertTrue(game.shouldRoundEnd());
        game.bettingTurn = 2;
        game.getClients().get(a).setState(Player.State.FOLD);
        assertTrue(game.shouldRoundEnd());
        game.getClients().get(a).setState(Player.State.ALLIN);
        assertTrue(game.shouldRoundEnd());
    }
    @Test
    void shouldRoundEnd2() {
        GameLogic game = new GameLogic();
        game.addPlayer(new Player("test"));
        game.addPlayer(new Player("test2"));
        game.getPlayers().get(0).setState(Player.State.ALLIN);
        assertFalse(game.shouldRoundEnd());
    }

    @Test
    void isOnlyOneLeft() {
        GameLogic game = new GameLogic();
        game.addPlayer(new Player("test"));
        game.addPlayer(new Player("test2"));
        game.getPlayers().get(0).setState(Player.State.FOLD);
        assertFalse(game.isOnlyOneLeft());
    }

    @Test
    void isAllIn2() {
        GameLogic game = new GameLogic();
        game.addPlayer(new Player("test"));
        game.addPlayer(new Player("test2"));
        game.getPlayers().get(0).setState(Player.State.ALLIN);
        assertFalse(game.isAllIn());
    }


    @Test
    void nextTurn() {
        game.nextTurn();
        assertEquals(1, game.bettingTurn);
        assertFalse(game.cardSwapping);
        assertEquals(0, game.turnMaxBet);
        assertEquals(0, game.getClients().get(a).getCurrentBet());
        assertEquals(0, game.getClients().get(b).getCurrentBet());
        assertEquals(Player.State.EMPTY, game.getClients().get(a).getState());
        assertEquals(Player.State.EMPTY, game.getClients().get(b).getState());
    }
    @Test
    void swapCards(){
        game.bettingTurn = 2;
        game.giveCards();
        game.setUpSwapping();
        assertTrue(game.getClients().get(a).getIsSwapping());
        assertTrue(game.getClients().get(b).getIsSwapping());
        ArrayList<Integer> x = new ArrayList<>();
        x.add(0);
        game.swapCards(a, x);
        game.swapCards(b, x);
        assertFalse(game.getClients().get(a).getIsSwapping());
        assertFalse(game.getClients().get(b).getIsSwapping());
    }




    @Test
    void getMoneyOnTable() {
        assertEquals(0, game.getMoneyOnTable());
    }
    @AfterEach
    public void tearDown() {
        game = null;
        try {
            a.close();
            b.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}