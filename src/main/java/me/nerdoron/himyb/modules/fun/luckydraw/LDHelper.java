package me.nerdoron.himyb.modules.fun.luckydraw;

import me.nerdoron.himyb.Global;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LDHelper {
    public static MessageEmbed ldMain(int bet, int number) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF40 Lucky Draw")
                .setDescription(String.format("You bet %d %s on %d. Drawing the first number...", bet, Global.broCoin.getAsMention(), number))
                .addField("Draw", "❓ ❓ ❓", false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


    public static MessageEmbed stageOneLose(int bet, int number, int no1) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF40 Lucky Draw")
                .setDescription(String.format("You bet %d %s on %d. The first draw isn't a match to the first digit of your number, so you lose!", bet, Global.broCoin.getAsMention(), number))
                .addField("Draw", String.format("%s ❓ ❓", returnEmojiByNumber(no1)), false)
                .addField("Losings", String.format("%d %s", bet, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


    public static MessageEmbed stageTwo(int bet, int number, int no1, int no2) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF40 Lucky Draw")
                .setDescription(String.format("You bet %d %s on %d. Drawing the final number...", bet, Global.broCoin.getAsMention(), number))
                .addField("Draw", String.format("%s %s ❓", returnEmojiByNumber(no1), returnEmojiByNumber(no2)), false)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed stageTwoLose(int bet, int number, int no1, int no2) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF40 Lucky Draw")
                .setDescription(String.format("You bet %d %s on %d. The second draw isn't a match to the first digit of your number, so you lose!", bet, Global.broCoin.getAsMention(), number))
                .addField("Draw", String.format("%s %s ❓", returnEmojiByNumber(no1), returnEmojiByNumber(no2)), false)
                .addField("Losings", String.format("%d %s", bet, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


    public static MessageEmbed stageThreeWin(int bet, int number, int no1, int no2, int no3) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF40 Lucky Draw")
                .setDescription(String.format("You bet %d %s on %d, and won the lucky draw!", bet, Global.broCoin.getAsMention(), number))
                .addField("Draw", String.format("%s %s %s", returnEmojiByNumber(no1), returnEmojiByNumber(no2), returnEmojiByNumber(no3)), false)
                .addField("Winnings", String.format("%d %s", bet * 30, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed stageThreeLose(int bet, int number, int no1, int no2, int no3) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF40 Lucky Draw")
                .setDescription(String.format("You bet %d %s on %d, and lost the lucky draw!", bet, Global.broCoin.getAsMention(), number))
                .addField("Draw", String.format("%s %s %s", returnEmojiByNumber(no1), returnEmojiByNumber(no2), returnEmojiByNumber(no3)), false)
                .addField("Losings", String.format("%d %s", bet, Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    private static String returnEmojiByNumber(int number) {
        switch (number) {
            case 0:
                return "0️⃣";
            case 1:
                return "1️⃣";
            case 2:
                return "2️⃣";
            case 3:
                return "3️⃣";
            case 4:
                return "4️⃣";
            case 5:
                return "5️⃣";
            case 6:
                return "6️⃣";
            case 7:
                return "7️⃣";
            case 8:
                return "8️⃣";
            case 9:
                return "9️⃣";
            default:
                return String.valueOf(number);
        }
    }


}
