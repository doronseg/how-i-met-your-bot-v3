package me.nerdoron.himyb.modules.fun.blackjack;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

public class BJCard {
    static final CustomEmoji backOfCard = Global.BACK_OF_CARD;
    final String[] clubs = Global.CLUBS_CARDS;
    final String[] diamonds = Global.DIAMONDS_CARDS;
    final String[] hearts = Global.HEARTS_CARDS;
    final String[] spades = Global.SPADES_CARDS;
    private final int number;
    private final String type;

    public BJCard(int number, String type) {
        this.number = number;
        this.type = type;
    }

    public int getNumber() {
        return Math.min(number, 10);
    }


    public String getCard() {
        switch (type) {
            case "club":
                return "<:Card:" + clubs[number - 1] + ">";
            case "diamond":
                return "<:Card:" + diamonds[number - 1] + ">";
            case "heart":
                return "<:Card:" + hearts[number - 1] + ">";
            case "spade":
                return "<:Card:" + spades[number - 1] + ">";
            default:
                return "(" + type + ")";
        }
    }
}
