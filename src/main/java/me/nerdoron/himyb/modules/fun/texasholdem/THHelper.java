package me.nerdoron.himyb.modules.fun.texasholdem;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class THHelper {
    private static final Map<Member, THGame> activeGames = new HashMap<>();

    public static MessageEmbed texasStarting() {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Ultimate Texas Hold'em")
                .setDescription("Starting game...")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed texasMain(int blind, int ante, int trips) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Ultimate Texas Hold'em")
                .setDescription("Welcome to Ultimate Texas Hold'em. Click Start Game when you're ready to start." +
                        "\n For optimal gameplay, make sure you have at least thrice your initial bet available in cash before you begin.")
                .addField("Blind:", String.format("%d %s", blind, Global.broCoin.getAsMention()), true)
                .addField("Ante:", String.format("%d %s", ante, Global.broCoin.getAsMention()), true)
                .addField("Trips:", String.format("%d %s", trips, Global.broCoin.getAsMention()), true)
                .addField("How the game works", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/Blackjack-Explanation)", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed preFlop(List<THCard> playerCards, int blind, int ante, int trips) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ Ultimate Texas Hold'em - Pre-Flop")
                .addField("Your cards", String.format("%s\n%s", playerCardsEmotes, playerCardsNames), true)
                .addField("Dealer's cards", String.format("%s %s", THCard.backOfCard, THCard.backOfCard), true)
                .addField("Table", String.format("%s %s %s %s %s", THCard.backOfCard, THCard.backOfCard, THCard.backOfCard, THCard.backOfCard, THCard.backOfCard), false)
                .addField("Blind:", String.format("%d %s", blind, Global.broCoin.getAsMention()), true)
                .addField("Ante:", String.format("%d %s", ante, Global.broCoin.getAsMention()), true)
                .addField("Trips:", String.format("%d %s", trips, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed flop(List<THCard> playerCards, List<THCard> tableCards, int blind, int ante, int trips) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);
        String tableCardsEmotes = THFormatter.formatCardNoName(tableCards);
        String tableCardsNames = THFormatter.formatCardWithName(tableCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ Ultimate Texas Hold'em - Flop")
                .addField("Your cards", String.format("%s\n%s", playerCardsEmotes, playerCardsNames), true)
                .addField("Dealer's cards", String.format("%s %s", THCard.backOfCard, THCard.backOfCard), true)
                .addField("Table", String.format("%s %s %s\n%s", tableCardsEmotes, THCard.backOfCard, THCard.backOfCard, tableCardsNames), false)
                .addField("Blind:", String.format("%d %s", blind, Global.broCoin.getAsMention()), true)
                .addField("Ante:", String.format("%d %s", ante, Global.broCoin.getAsMention()), true)
                .addField("Trips:", String.format("%d %s", trips, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed river(List<THCard> playerCards, List<THCard> tableCards, int blind, int ante, int trips) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);
        String tableCardsEmotes = THFormatter.formatCardNoName(tableCards);
        String tableCardsNames = THFormatter.formatCardWithName(tableCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ Ultimate Texas Hold'em - River")
                .addField("Your cards", String.format("%s\n%s", playerCardsEmotes, playerCardsNames), true)
                .addField("Dealer's cards", String.format("%s %s", THCard.backOfCard, THCard.backOfCard), true)
                .addField("Table", String.format("%s\n%s", tableCardsEmotes, tableCardsNames), false)
                .addField("Blind:", String.format("%d %s", blind, Global.broCoin.getAsMention()), true)
                .addField("Ante:", String.format("%d %s", ante, Global.broCoin.getAsMention()), true)
                .addField("Trips:", String.format("%d %s", trips, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed revealDealerCards(List<THCard> playerCards, List<THCard> tableCards, List<THCard> dealerCards, int blind, int ante, int trips, int play) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);
        String tableCardsEmotes = THFormatter.formatCardNoName(tableCards);
        String tableCardsNames = THFormatter.formatCardWithName(tableCards);
        String dealerCardEmotes = THFormatter.formatCardNoName(dealerCards);
        String dealerCardNames = THFormatter.formatCardWithName(dealerCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ Ultimate Texas Hold'em - Post-game")
                .setDescription("Proceeding in 5 seconds...")
                .addField("Your cards", String.format("%s\n%s", playerCardsEmotes, playerCardsNames), true)
                .addField("Dealer's cards", String.format("%s \n%s", dealerCardEmotes, dealerCardNames), true)
                .addField("Table", String.format("%s\n%s", tableCardsEmotes, tableCardsNames), false)
                .addField("Blind:", String.format("%d %s", blind, Global.broCoin.getAsMention()), true)
                .addField("Ante:", String.format("%d %s", ante, Global.broCoin.getAsMention()), true)
                .addField("Trips:", String.format("%d %s", trips, Global.broCoin.getAsMention()), true)
                .addField("Play:", String.format("%d %s", play, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed fold(List<THCard> playerCards, List<THCard> tableCards, int bet) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);
        String tableCardsEmotes = THFormatter.formatCardNoName(tableCards);
        String tableCardsNames = THFormatter.formatCardWithName(tableCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ Fold!")
                .setDescription("You folded.")
                .addField("Your cards", String.format("%s\n%s", playerCardsEmotes, playerCardsNames), true)
                .addField("Dealer's cards", String.format("%s %s", THCard.backOfCard, THCard.backOfCard), true)
                .addField("Table", String.format("%s\n%s", tableCardsEmotes, tableCardsNames), false)
                .addField("Amount lost:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed playerWin(List<THCard> playerCards, List<THCard> tableCards, List<THCard> dealerCards, String dealerRank, String playerRank, int blindReturns, int tripReturns, int total) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);
        String tableCardsEmotes = THFormatter.formatCardNoName(tableCards);
        String tableCardsNames = THFormatter.formatCardWithName(tableCards);
        String dealerCardEmotes = THFormatter.formatCardNoName(dealerCards);
        String dealerCardNames = THFormatter.formatCardWithName(dealerCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ You win!")
                .setDescription("Result: player win.")
                .addField("Your cards", String.format("%s\n%s\nRank: %s", playerCardsEmotes, playerCardsNames, playerRank), true)
                .addField("Dealer's cards", String.format("%s \n%s\nRank: %s", dealerCardEmotes, dealerCardNames, dealerRank), true)
                .addField("Table", String.format("%s\n%s", tableCardsEmotes, tableCardsNames), false)
                .addField("Trips Returns:", String.format("%d %s", tripReturns, Global.broCoin.getAsMention()), true)
                .addField("Blind returns:", String.format("%d %s", blindReturns, Global.broCoin.getAsMention()), true)
                .addField("Total winnings:", String.format("%d %s", total, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed playerLose(List<THCard> playerCards, List<THCard> tableCards, List<THCard> dealerCards, String dealerRank, String playerRank, int bet) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);
        String tableCardsEmotes = THFormatter.formatCardNoName(tableCards);
        String tableCardsNames = THFormatter.formatCardWithName(tableCards);
        String dealerCardEmotes = THFormatter.formatCardNoName(dealerCards);
        String dealerCardNames = THFormatter.formatCardWithName(dealerCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ You lose!")
                .setDescription("Result: dealer win.")
                .addField("Your cards", String.format("%s\n%s\nRank: %s", playerCardsEmotes, playerCardsNames, playerRank), true)
                .addField("Dealer's cards", String.format("%s \n%s\nRank: %s", dealerCardEmotes, dealerCardNames, dealerRank), true)
                .addField("Table", String.format("%s\n%s", tableCardsEmotes, tableCardsNames), false)
                .addField("Total losings:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed tie(List<THCard> playerCards, List<THCard> tableCards, List<THCard> dealerCards, String dealerRank, String playerRank) {
        String playerCardsEmotes = THFormatter.formatCardNoName(playerCards);
        String playerCardsNames = THFormatter.formatCardWithName(playerCards);
        String tableCardsEmotes = THFormatter.formatCardNoName(tableCards);
        String tableCardsNames = THFormatter.formatCardWithName(tableCards);
        String dealerCardEmotes = THFormatter.formatCardNoName(dealerCards);
        String dealerCardNames = THFormatter.formatCardWithName(dealerCards);

        return new EmbedBuilder()
                .setTitle("♣️♦️ It's a tie!")
                .setDescription("Result: tie.\nYou get your money back.")
                .addField("Your cards", String.format("%s\n%s\nRank: %s", playerCardsEmotes, playerCardsNames, playerRank), true)
                .addField("Dealer's cards", String.format("%s \n%s\nRank: %s", dealerCardEmotes, dealerCardNames, dealerRank), true)
                .addField("Table", String.format("%s\n%s", tableCardsEmotes, tableCardsNames), false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


    public static MessageEmbed failureToStart() {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Timeout!")
                .setDescription("You failed to start the game in 5 minutes, the game terminated itself. Don't worry, you didn't lose your bet.")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed quitGame() {
        return new EmbedBuilder()
                .setTitle("♣️♦️ You quit the game.")
                .setDescription("The game is terminated. Don't worry, you didn't lose your bet.")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed THTimeout(int bet) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Timeout!")
                .setDescription("You failed to respond in 5 minutes, therefore you automatically lost.")
                .addField("Amount lost:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
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


    public static void startTHGame(Member member, THGame game) {
        activeGames.put(member, game);
    }

    public static void endTHGame(Member member) {
        activeGames.remove(member);
    }


    public static boolean hasOnGoingThGame(Member member) {
        return activeGames.containsKey(member);
    }

}
