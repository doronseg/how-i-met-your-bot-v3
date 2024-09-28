package me.nerdoron.himyb.modules.fun.blackjack;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;

@SuppressWarnings("LoggingSimilarMessage")
public class BJHelper {

    final static BJHandler bjHandler = new BJHandler();
    private static final Map<Member, BlackjackGame> activeGames = new HashMap<>();
    private static final Logger logger = LoggingHandler.logger(BJHandler.class);


    public static MessageEmbed blackJackMain(int bet) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Blackjack")
                .setDescription("Starting a new Blackjack game for you...")
                .addField("Your bet:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
                .addField("Explanation", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/Blackjack-Explanation)", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed blackJackTimeOut(int bet) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Timeout!")
                .setDescription("You failed to respond in 5 minutes, therefore you automatically lost.")
                .addField("Amount lost:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed blackJackGame(int bet, List<BJCard> userCards, List<BJCard> dealerCards, boolean showDealerFullHand) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("♣️♦️ Blackjack")
                .setDescription("You have 5 minutes to respond.")
                .addField("Your cards:", formatCardListWithValues(userCards, true), false) // Show full hand and individual card values
                .addField("My cards:", formatCardListWithValues(dealerCards, showDealerFullHand), false) // Show dealer's cards (one hidden if phase 1)
                .addField("Your bet:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp);

        return embedBuilder.build();
    }

    public static MessageEmbed resolveGame(int bet, List<BJCard> userCards, List<BJCard> dealerCards, List<BJCard> deck, Member member) throws SQLException {
        String result;
        int userHand = getHandValue(userCards);
        int botHand;

        if (userHand == 21) {
            endBjGame(member);
            result = String.format("You win! You earned %d %s.", bet, Global.broCoin.getAsMention());
            logger.info("{}(ID:{}) played blackjack for {} and won.", member.getUser().getName(), member.getId(), bet);
            return new EmbedBuilder()
                    .setTitle("♣️♦️ Blackjack - Game Over")
                    .setDescription(result)
                    .addField("Your cards:", formatCardListWithValues(userCards, true), false)
                    .addField("My cards:", formatCardListWithValues(dealerCards, false), false)
                    .setColor(Global.embedColor)
                    .setFooter(Global.footertext, Global.footerpfp)
                    .build();
        }

        while (getHandValue(dealerCards) < 17) {
            BJCard newCard = bjHandler.drawCard(deck);
            dealerCards.add(newCard);
        }
        botHand = getHandValue(dealerCards);


        if (userHand > 21) {
            result = String.format("You busted! You lose %d %s.", bet, Global.broCoin.getAsMention());
            BROCOINS_SQL.updateCash(member, -bet);
            logger.info("{}(ID:{}) played blackjack for {} and lost.", member.getUser().getName(), member.getId(), bet);
            endBjGame(member);
            return new EmbedBuilder()
                    .setTitle("♣️♦️ Blackjack - Game Over")
                    .setDescription(result)
                    .addField("Your cards:", formatCardListWithValues(userCards, true), false) // Show full hand and individual card values
                    .addField("My cards:", formatCardListWithValues(dealerCards, false), false)
                    .setColor(Global.embedColor)
                    .setFooter(Global.footertext, Global.footerpfp)
                    .build();
        } else if (userHand > botHand) {
            result = String.format("You win! You earned %d %s.", bet, Global.broCoin.getAsMention());
            logger.info("{}(ID:{}) played blackjack for {} and won.", member.getUser().getName(), member.getId(), bet);
            BROCOINS_SQL.updateCash(member, bet);
        } else if (botHand > 21) {
            result = String.format("You win! You earned %d %s.", bet, Global.broCoin.getAsMention());
            logger.info("{}(ID:{}) played blackjack for {} and won.", member.getUser().getName(), member.getId(), bet);
            BROCOINS_SQL.updateCash(member, bet);
        } else if (userHand == botHand) {
            result = String.format("It's a tie. You get your bet back of %d %s back.", bet, Global.broCoin.getAsMention());
            logger.info("{}(ID:{}) played blackjack for {} and tied.", member.getUser().getName(), member.getId(), bet);
        } else if (botHand == 21) {
            result = String.format("Bot wins. You lose %d %s.", bet, Global.broCoin.getAsMention());
            logger.info("{}(ID:{}) played blackjack for {} and lost.", member.getUser().getName(), member.getId(), bet);
            BROCOINS_SQL.updateCash(member, -bet);
        } else {
            result = String.format("Bot wins. You lose %d %s.", bet, Global.broCoin.getAsMention());
            logger.info("{}(ID:{}) played blackjack for {} and lost.", member.getUser().getName(), member.getId(), bet);
            BROCOINS_SQL.updateCash(member, -bet);
        }

        endBjGame(member);
        return new EmbedBuilder()
                .setTitle("♣️♦️ Blackjack - Game Over")
                .setDescription(result)
                .addField("Your cards:", formatCardListWithValues(userCards, true), false) // Show full hand and individual card values
                .addField("My cards:", formatCardListWithValues(dealerCards, true), false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed createErrorEmbed(String errorMessage) {
        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription("An error has occurred, don't worry though, you didn't lose your bet. Error message: `" + errorMessage + "`.")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static int getHandValue(List<BJCard> hand) {
        int totalValue = 0;
        int aces = 0;

        for (BJCard card : hand) {
            int value = card.getNumber();

            if (value == 1) {
                aces++;
                totalValue += 11;
            } else {
                totalValue += Math.min(value, 10);
            }
        }

        while (totalValue > 21 && aces > 0) {
            totalValue -= 10;
            aces--;
        }

        return totalValue;
    }


    private static String formatCardListWithValues(List<BJCard> cards, boolean showFullHand) {
        StringBuilder sb = new StringBuilder();
        int totalValue = 0;
        int aceCount = 0;

        if (!showFullHand && cards.size() > 1) {
            BJCard firstCard = cards.get(0);
            int firstCardValue = firstCard.getNumber();
            sb.append(firstCard.getCard()).append(" (").append(firstCardValue).append(") ");
            return sb.append(BJCard.backOfCard.getAsMention()).append(" (Total: ?)").toString().trim();
        }

        for (BJCard card : cards) {
            int cardValue = card.getNumber();

            if (cardValue == 1) {
                aceCount++;
                cardValue = 11;
            } else if (cardValue > 10) {
                cardValue = 10;
            }

            sb.append(card.getCard()).append(" (").append(cardValue).append(") ");
            totalValue += cardValue;
        }

        while (aceCount > 0 && totalValue > 21) {
            totalValue -= 10;
            aceCount--;
        }

        return sb.append("(Total: ").append(totalValue).append(")").toString().trim();
    }


    public static void startNewBjGame(Member member, BlackjackGame game) {
        activeGames.put(member, game);
    }

    public static void endBjGame(Member member) {
        activeGames.remove(member);
    }

    public static boolean hasOngoingBjGame(Member member) {
        return activeGames.containsKey(member);
    }


}