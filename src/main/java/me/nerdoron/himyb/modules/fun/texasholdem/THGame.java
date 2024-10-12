package me.nerdoron.himyb.modules.fun.texasholdem;

import java.util.List;

public class THGame {
    private final List<THCard> dealerCards;
    private final List<THCard> playerCards;
    private final List<THCard> deck;
    private final int blind;
    private final int ante;
    private final int trips;
    private final List<THCard> table;
    private int bet;
    private boolean hasFlop, hasRiver;

    public THGame(List<THCard> dealerCards, List<THCard> playerCards, List<THCard> table, List<THCard> deck, int bet, int blind, int ante, int trips) {
        this.dealerCards = dealerCards;
        this.table = table;
        this.playerCards = playerCards;
        this.deck = deck;
        this.bet = bet;
        this.blind = blind;
        this.ante = ante;
        this.trips = trips;
        this.hasFlop = false;
        this.hasRiver = false;
    }

    public void dealFlop() {
        if (!hasFlop) {
            table.add(deck.remove(0));
            table.add(deck.remove(0));
            table.add(deck.remove(0));
            hasFlop = true;
        }
    }

    public void dealRiver() {
        if (hasFlop && !hasRiver) {
            table.add(deck.remove(0));
            table.add(deck.remove(0));
            hasRiver = true;
        }
    }

    public List<THCard> getPlayerCards() {
        return playerCards;
    }

    public List<THCard> getDealerCards() {
        return dealerCards;
    }

    public List<THCard> getTable() {
        return table;
    }

    public int getBlind() {
        return blind;
    }

    public int getAnte() {
        return ante;
    }

    public int getTrips() {
        return trips;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}
