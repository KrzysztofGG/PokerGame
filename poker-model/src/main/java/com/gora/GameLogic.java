package com.gora;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class holding logic, rules and current game states
 * @author krzys
 *
 */
public class GameLogic {
    protected Deck deck;
    boolean cardSwapping = false;
    boolean shouldServerSendSwapInfo = true;

    int turnMaxBet = 0;
    int moneyOnTable = 0;
    int bettingTurn = 0;

    int roundNumber = 0;
    Player roundWinner;
    Player gameWinner;
    int playersInGame;
    /**
     * map of clients in game
     */
    private final Map<SocketChannel, Player> clients;
    private ArrayList<Player> players;
    public GameLogic(){
        this.clients = new HashMap<>();
        this.players = new ArrayList<>();
        this.deck = new Deck();
    }
    public void addPlayer(Player p){
        this.players.add(p);
    }
    public List<Player> getPlayers(){
        return this.players;
    }
    public GameLogic(Map<SocketChannel, String> clientsMap){
        this.clients = new HashMap<>();
        for(Map.Entry<SocketChannel, String> entry : clientsMap.entrySet()){
            this.clients.put(entry.getKey(), new Player(entry.getValue()));
        }
        playersInGame = clients.size();
        this.players = new ArrayList<>(this.clients.values());
        createRoles();
    }
    public Map<SocketChannel, Player> getClients() {
        return clients;
    }

    /**
     * cheks if player is all in
     * @return true if player is all in
     */
    public boolean isAllIn(){
        Player allIn = null;
        for (Player p: clients.values()){
            if (p.getState() == Player.State.ALLIN)
                allIn = p;
        }
        if(allIn != null){
            for(Player p: clients.values()){
                if(p.getCurrentBet() != this.turnMaxBet)
                    return false;

            }
            return true;
        }
        return false;
    }

    /**
     * removes bankrupt players from game
     */
    void removeBankruptPlayers(){
        for(Player p : clients.values()){
            p.nextRoundStarts();
            if(p.getBalance() == 0){
                p.setState(Player.State.FOLD);
            }
        }
    }

    /**
     * gives cards to players at beggining of round
     */
    void giveCards(){
        for(int i=0; i<5; i++){
            for(Map.Entry<SocketChannel, Player> entry : clients.entrySet()){
                entry.getValue().addCardToHand(deck.getTopCard());
            }
        }
    }

    /**
     * prepares next round
     */
    public void nextRound(){
        this.roundNumber++;
        this.deck = new Deck();
        removeBankruptPlayers();
        giveCards();

        this.moneyOnTable = 0;
        this.roundWinner = null;
        this.bettingTurn = 0;
        this.turnMaxBet = 0;
    }

    /**
     * checks if round should end
     * @return true if round should end
     */

    public boolean shouldTurnEnd(){
        int checks = 0;
        int folds = 0;
        for(Player p : clients.values()){
            if(p.getState() == Player.State.CHECK)
                checks++;
            else if (p.getState() == Player.State.FOLD)
                folds++;
        }
        if (checks == clients.size() - folds)
            return true;
        for(Map.Entry<SocketChannel, Player> entry : clients.entrySet()){
            if(entry.getValue().getState() == Player.State.EMPTY)
                return false;
            else if(entry.getValue().getState() == Player.State.CHECK && turnMaxBet != 0)
                return false;
            else if(entry.getValue().getState() == Player.State.BET && entry.getValue().getCurrentBet() != turnMaxBet){
                return false;
            }

        }
        return true;
    }

    /**
     * checks whether swapping cards should end
     * @return true if swapping cards should end
     */
    public boolean shouldSwappingEnd(){
        for(Map.Entry<SocketChannel, Player> entry : clients.entrySet()){
            if(entry.getValue().getIsSwapping())
                return false;
        }
        return true;
    }

    /**
     * checks if game should end
     * @return true if game should end
     */
    public boolean shouldGameEnd(){
        if(bettingTurn != 4)
            return false;

        int playersBankrupt = 0;
        for(Player p : clients.values()){
            if(p.getBalance() == 0)
                playersBankrupt++;
        }
        if(playersBankrupt == clients.size()-1){
            for(Player p : clients.values()){
                if(p.getBalance() != 0 && p.getState() != Player.State.FOLD)
                    this.gameWinner = p;
            }
            return true;
        }
        else
            return false;
    }

    /**
     * checks should round end
     * @return true if round should end
     */
    public boolean shouldRoundEnd(){
        if(bettingTurn == 4 || isAllIn()){
            evaluateRoundWinner();
            this.roundWinner.addToBalance(this.moneyOnTable);
            return true;
        }
        else if(isOnlyOneLeft()){
            this.roundWinner.addToBalance(this.moneyOnTable);
            return true;
        }
        return false;
    }
    /**
     * checks if only one player is left in turn
     * @return true if only one player is left in turn
     */
    public boolean isOnlyOneLeft(){
        int numberOfFolds = 0;
        for(Player p : clients.values()){
            if(p.getState() == Player.State.FOLD) numberOfFolds++;
        }
        if(numberOfFolds == clients.size() - 1){
            for(Player p : clients.values()){
                if(p.getState() != Player.State.FOLD) roundWinner = p;
            }
            return true;
        }

        return false;
    }
    /**
     * evaluates round winner
     */
    public void evaluateRoundWinner(){
        int wins = 0;
        for(Player p1 : clients.values()){
            for(Player p2 : clients.values()){
                if(!p1.equals(p2) && p1.getHand().compareHands(p2.getHand()) > 0){
                    wins++;
                }
            }
            if(wins == this.clients.size() - 1){
                this.roundWinner = p1; //no draws ://
                break;
            }
        }
    }

    /**
     * prepares swapping stage in game
     */
    void setUpSwapping(){
        if(bettingTurn == 2) {
            this.cardSwapping = true;
            clients.forEach((x, player) -> {
                if(player.getState() != Player.State.FOLD){
                    player.setIsSwapping(true);
                }

            });
        }
    }
    /**
     * prepares next turn
     */
    public void nextTurn(){
        bettingTurn++;
        setUpSwapping();
        for(Map.Entry<SocketChannel, Player> entry : clients.entrySet()){
            if(entry.getValue().getState() != Player.State.FOLD){
                entry.getValue().setState(Player.State.EMPTY);
                entry.getValue().setCurrentBet(0);
            }
        }
        turnMaxBet = 0;
    }

    /**
     * swaps cards for player
     * @param client - connected client
     * @param cardsToSwap indexes of cards to swap
     */
    public void swapCards(SocketChannel client, List<Integer> cardsToSwap){
        Player player = clients.get(client);
        for (Integer integer : cardsToSwap) {
            player.swapCard(integer, deck.getTopCard());
        }

        player.setIsSwapping(false);
    }

    /**
     * makes roles for players at beginning of game
     */
    public void createRoles(){
        int i=0;
        for(Map.Entry<SocketChannel, Player> entry : clients.entrySet()){
            this.clients.get(entry.getKey()).setRole(Player.Role.values()[i]);
            i++;
        }
    }


    public int getMoneyOnTable() {
        return moneyOnTable;
    }

    public String getWinner() {
        return this.gameWinner.getName();
    }
}

