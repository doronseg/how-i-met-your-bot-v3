package me.nerdoron.himyb.modules.fun.hilo;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.HashMap;
import java.util.Map;

public class HiLoHelper {
    private static final Map<Member, HiLoGame> activeGames = new HashMap<>();

    public static MessageEmbed hiLoStarting() {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Hi-Lo")
                .setDescription("Starting game...")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed hiLoInit(int bet) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Hi-Lo")
                .setDescription("Welcome to Hi-Lo. Click Start Game when you're ready to start.")
                .addField("Initial bet:", String.format("%d %s", bet, Global.broCoin.getAsMention()), true)
                .addField("How the game works", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/HiLo-Explanation)", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed hiLoFirst(int bet, HiLoCard card) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Hi-Lo")
                .setDescription("Round 1 of HiLo. Here's your first card, will the next card be higher or lower?")
                .addField("Your Card", formatCardWithName(card), true)
                .addField("Next card", HiLoCard.backOfCard.getAsMention(), true)
                .addField("Initial bet:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
                .addField("How the game works", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/HiLo-Explanation)", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed hiLoGame(int bet, int noOfRounds, HiLoCard card, String table) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Hi-Lo")
                .setDescription(String.format("Correct!\n Round %d of HiLo. Here's the next card, will the next card be higher or lower?", noOfRounds))
                .addField("Your Card", formatCardWithName(card), true)
                .addField("Next card", HiLoCard.backOfCard.getAsMention(), true)
                .addField("Previous Cards", table, true)
                .addField("Current winnings:", String.format("%d %s", bet, Global.broCoin.getAsMention()), false)
                .addField("How the game works", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/HiLo-Explanation)", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed hiLoLose(int bet, HiLoCard newCard, HiLoCard oldCard) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ You lost!")
                .setDescription("Incorrect guess! You lose!")
                .addField("New Card", formatCardWithName(newCard), true)
                .addField("Your Card", formatCardWithName(oldCard), true)
                .addField("Amount lost:", String.format("%d %s", bet, Global.broCoin.getAsMention()), true)
                .addField("How the game works", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/HiLo-Explanation)", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed hiLoCashOut(int bet) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ Cashed out")
                .setDescription("You cashed out.")
                .addField("Amount won:", String.format("%d %s", bet, Global.broCoin.getAsMention()), true)
                .addField("How the game works", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/HiLo-Explanation)", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed tie(int bet, HiLoCard newCard, HiLoCard oldCard) {
        return new EmbedBuilder()
                .setTitle("♣️♦️ It's a tie!")
                .setDescription("The new card and old card are the same. Automatically cashing out.")
                .addField("New Card", formatCardWithName(newCard), true)
                .addField("Your Card", formatCardWithName(oldCard), true)
                .addField("Amount won:", String.format("%d %s", bet, Global.broCoin.getAsMention()), true)
                .addField("How the game works", "[Click here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/HiLo-Explanation)", false)
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

    static String formatCardWithName(HiLoCard card) {
        return card.getCard() + " `(" + card.getCardName() + ")`";
    }

    public static void startHiLoGame(Member member, HiLoGame game) {
        activeGames.put(member, game);
    }

    public static void endHiLoGame(Member member) {
        activeGames.remove(member);
    }

    public static boolean hasOnGoingHiLoGame(Member member) {
        return activeGames.containsKey(member);
    }

}
