package me.nerdoron.himyb.modules.fun.hilo;

import java.util.List;

public class HiLoGame {

    private final List<HiLoCard> table;
    private final List<HiLoCard> deck;
    private int noOfRounds;
    private int bet;
    private int winnings;

    public HiLoGame(List<HiLoCard> table, int bet, List<HiLoCard> deck) {
        this.table = table;
        this.bet = bet;
        this.deck = deck;
    }


    public List<HiLoCard> getTable() {
        return table;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }


    public List<HiLoCard> getDeck() {
        return deck;
    }

    public int getNoOfRounds() {
        return noOfRounds;
    }

    public void setNoOfRounds(int noOfRounds) {
        this.noOfRounds = noOfRounds;
    }

    public int getWinnings() {
        return winnings;
    }

    public void setWinnings(int winnings) {
        this.winnings = winnings;
    }
}
