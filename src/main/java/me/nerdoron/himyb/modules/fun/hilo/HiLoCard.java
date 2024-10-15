package me.nerdoron.himyb.modules.fun.hilo;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

public class HiLoCard {
    static final CustomEmoji backOfCard = Global.BACK_OF_CARD;
    final String[] clubs = Global.CLUBS_CARDS;
    final String[] diamonds = Global.DIAMONDS_CARDS;
    final String[] hearts = Global.HEARTS_CARDS;
    final String[] spades = Global.SPADES_CARDS;
    private final int number;
    private final String type;

    public HiLoCard(int number, String type) {
        this.number = number;
        this.type = type;
    }

    public int getNumber() {
        return Math.min(number, 13);
    }

    public String getCardName() {
        String cardValue = number == 1 ? "Ace" : (number == 11 ? "Jack" : (number == 12 ? "Queen" : (number == 13 ? "King" : String.valueOf(number))));
        return cardValue + " of " + type + "s";
    }

    public String getCard() {
        switch (type) {
            case "Club":
                return "<:Card:" + clubs[number - 1] + ">";
            case "Diamond":
                return "<:Card:" + diamonds[number - 1] + ">";
            case "Heart":
                return "<:Card:" + hearts[number - 1] + ">";
            case "Spade":
                return "<:Card:" + spades[number - 1] + ">";
            default:
                return "(\"Unknown card\") " + type;
        }
    }
}

