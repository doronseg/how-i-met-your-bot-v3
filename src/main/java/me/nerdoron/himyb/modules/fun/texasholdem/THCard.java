package me.nerdoron.himyb.modules.fun.texasholdem;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;


public class THCard {
    static final CustomEmoji backOfCard = Global.BACK_OF_CARD;
    final String[] clubs = Global.CLUBS_CARDS;
    final String[] diamonds = Global.DIAMONDS_CARDS;
    final String[] hearts = Global.HEARTS_CARDS;
    final String[] spades = Global.SPADES_CARDS;

    private final int rank;
    private final String suit;

    public THCard(int rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getCard() {
        switch (suit) {
            case "Club":
                return "<:Card:" + clubs[rank - 1] + ">";
            case "Diamond":
                return "<:Card:" + diamonds[rank - 1] + ">";
            case "Heart":
                return "<:Card:" + hearts[rank - 1] + ">";
            case "Spade":
                return "<:Card:" + spades[rank - 1] + ">";
            default:
                return "(" + suit + ")";
        }
    }

    @Override
    public String toString() {
        String rankString;
        switch (rank) {
            case 11:
                rankString = "Jack";
                break;
            case 12:
                rankString = "Queen";
                break;
            case 13:
                rankString = "King";
                break;
            case 14:
            case 1:
                rankString = "Ace";
                break;
            default:
                rankString = String.valueOf(rank);
                break;
        }
        return rankString + " Of " + suit;
    }

}
