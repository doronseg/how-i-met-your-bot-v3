package me.nerdoron.himyb.modules.fun.texasholdem;

import java.util.List;

public class THFormatter {

    /**
     * Formats a list of THCard objects into a string with both card emojis and card names.
     *
     * @param cards The list of cards (e.g., player or dealer's hand).
     * @return A formatted string with card emojis and names.
     */
    public static String formatCardNoName(List<THCard> cards) {
        StringBuilder formattedCards = new StringBuilder();

        for (THCard card : cards) {
            formattedCards.append(card.getCard());

        }

        return formattedCards.toString().trim();
    }

    public static String formatCardWithName(List<THCard> cards) {
        StringBuilder formattedCards = new StringBuilder();

        for (THCard card : cards) {
            formattedCards
                    .append(" `")
                    .append(card)
                    .append("s`");

        }

        return formattedCards.toString().trim();
    }
}
