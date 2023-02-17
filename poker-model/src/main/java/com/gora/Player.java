package com.gora;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class representing player of cards
 * @author krzys
 *
 */
public class Player implements Comparable<Player>{
    int startBalance = 1000;

    protected final ArrayList<Card> cardsInHand;
    protected Hand hand;
    protected int balance;
    protected int currentBet;
    protected Role role;

    boolean isBankrupt = false;

    protected boolean isSwapping = false;

    protected State state;


    public enum Role{
        DEALER, SMALL_BLIND, BIG_BLIND, NO_ROLE
    }
    public enum State{
        CHECK, BET, FOLD, ALLIN, EMPTY
    }

    protected final String name;

    public Player(){
        this.name = "Player";
        this.cardsInHand = new ArrayList<>();
        this.balance = startBalance;
        this.currentBet = 0;
        this.state = State.EMPTY;
        this.role = Role.NO_ROLE;
    }
    /**
     * Constructor for player
     * @param name name of player
     */
    public Player( String name) {
        this.cardsInHand = new ArrayList<>();
        this.balance = this.startBalance;
        this.name = name;
        this.currentBet = 0;
        this.state = State.EMPTY;
        this.role = Role.NO_ROLE;
    }
    public State getState(){
        return this.state;
    }
    public void setState(State state){
        this.state = state;
    }
    public void setRole(Role r){
        this.role = r;
    }
    public Role getRole(){
        return this.role;
    }

    /**
     * Mehthod for updating roles
     */
    public void updateRole(){
        this.role = Role.values()[(this.role.ordinal()+1) % Role.values().length];
    }
    public void setCurrentBet(int bet){
        this.currentBet = bet;
    }
    public int getCurrentBet(){
        return this.currentBet;
    }
    public Hand getHand() {
        return this.hand;
    }
    public Card getCard(int i) {
        return cardsInHand.get(i);
    }
    public ArrayList<Card> getCardsInHand(){
        return this.cardsInHand;
    }
    public String getName() {
        return this.name;
    }
    public int getBalance() {
        return this.balance;
    }
    public boolean getIsSwapping(){
        return isSwapping;
    }
    public void setIsSwapping(boolean isSwapping){
        this.isSwapping = isSwapping;
    }

    /**
     * Method for swapping card in
     * @param i - index
     * @param card - card to swap in
     */
    public void swapCard(int i, Card card){
        cardsInHand.set(i, card);
        this.sortHand();
        this.hand = new Hand(cardsInHand);
    }
    /**
     * Method for adding card to hand
     * @param card - card to add
     */

    public void addCardToHand(Card card) {
        this.cardsInHand.add(card);
        if(cardsInHand.size() == 5) {
            this.sortHand();
            this.hand = new Hand(this.cardsInHand);
        }
    }
    /**
     * Method for removing card from hand
     * @param i - index of card to remove
     */
    public void removeCardFromHand(int i) {
        this.cardsInHand.remove(i);
    }

    /**
     * Method preparing next round for player
     */
    public void nextRoundStarts(){
        this.cardsInHand.clear();
        this.hand = null;
        this.currentBet = 0;
        this.updateRole();
        this.state = State.EMPTY;
        this.isSwapping = false;
    }

    /**
     * Method checking id equals
     * @param o - object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return startBalance == player.startBalance && balance == player.balance && currentBet == player.currentBet &&
                isSwapping == player.isSwapping && Objects.equals(cardsInHand, player.cardsInHand)
                && Objects.equals(hand, player.hand) && role == player.role && state == player.state
                && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startBalance, cardsInHand, hand, balance, currentBet, role, isSwapping, state, name);
    }

    /**
     * prints player's hand
     * @return - players' hand
     */
    public String printHand() {
        StringBuilder message = new StringBuilder("Player " + this.name + " cards:\n");
        for (int j = 0; j < 5; j++) {
            message.append(this.getCard(j).rank).append(" ").append(this.getCard(j).suit).append("\n");
        }
        return message.toString();
    }
    /**
     * Method for sorting hand
     */
    public void sortHand() {
        this.cardsInHand.sort(Card::compareTo);
    }

    /**
     * adding money to balance
     * @param amount - how much
     */
    public void addToBalance(int amount) {
        this.balance += amount;
    }
    public void removeFromBalance(int amount) {
        this.balance -= amount;
    }
    public int compareTo(Player other){
        return this.role.compareTo(other.role);
    }


}


