package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;

import static me.nerdoron.himyb.Global.HOUR_IN_SECONDS;

public class JailHelper {
    static final CooldownManager COOLDOWN_MANAGER = Global.COOLDOWN_MANAGER;
    private static final Logger logger = LoggingHandler.logger(JailHelper.class);

    public static MessageEmbed inJailEmbed(Member member) {
        String remaining = COOLDOWN_MANAGER.parseCooldown(CooldownManager.jailID(member));
        return new EmbedBuilder()
                .setTitle("\uD83D\uDC6E You're in jail!")
                .setDescription("You're serving time for: " + checkCharge(member) + ".\nYou will be released in " + remaining + ".")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed arrestedEmbed(String charge) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDEA8 \uD83D\uDE93 You're under arrest!")
                .setDescription(String.format("The cops placed you under arrest for `%s`!\nHow would you like to proceed?", charge))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed timeoutEmbed(int time) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDC6E Jailed!")
                .setDescription(String.format("You didn't react in time! the police have thrown you in jail for %d hours!", time / HOUR_IN_SECONDS))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed cooperateEmbed(int time) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDC6E Jailed!")
                .setDescription(String.format("You cooperated with the police, they've thrown you in jail for %d hours!", time / HOUR_IN_SECONDS))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed failedRunEmbed(int time) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDC6E Jailed!")
                .setDescription(String.format("You tried to run from the police, however, they caught up with you and thrown you in jail for %d hours!", time / HOUR_IN_SECONDS))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed successRunEmbed() {
        return new EmbedBuilder()
                .setTitle("⛓️\u200D\uD83D\uDCA5 A free man!")
                .setDescription("You've successfully fled the police! Go lay low for a while.")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed failedBribeEmbed(int time) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDC6E Jailed!")
                .setDescription(String.format("You tried to bribe the police, they weren't amused with your actions so they've thrown you in jail for %d hours!", time / HOUR_IN_SECONDS))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed bribeTooLow(int time, int coins) {
        return new EmbedBuilder()
                .setTitle("\uD83D\uDC6E Jailed!")
                .setDescription(String.format(String.format("\uD83D\uDC6E: \"Really? All you have is %%d %s? You're not worth my time!\"\n The police have thrown in jail for %%d hours!", Global.broCoin.getAsMention()), coins, time / HOUR_IN_SECONDS))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    public static MessageEmbed successBribeEmbed(int bribe) {
        return new EmbedBuilder()
                .setTitle("⛓️\u200D\uD83D\uDCA5 A free man!")
                .setDescription(String.format("You've bribed the police with %d %s! Go lay low for a while.", bribe, Global.broCoin.getAsMention()))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


    private static String checkCharge(Member member) {
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.jailID(member), "ROBBERY")) return "`Robbery`";
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.jailID(member), "BRIBERY")) return "`Bribery`";
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.jailID(member), "RESISTING")) return "`Resisting Arrest`";
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.jailID(member), "GENERAL")) return "`Commiting a crime`";

        logger.error("Unknown charge for member {}", member.getEffectiveName());
        return "ERROR";
    }

    public static boolean checkIfInJail(Member member) {
        return COOLDOWN_MANAGER.hasCooldown(CooldownManager.jailID(member));
    }

    public static void jailMember(Member member, int timeInSeconds, String charge) {
        COOLDOWN_MANAGER.addCooldown(CooldownManager.jailID(member), charge, timeInSeconds);
    }

    public static void unJailMember(Member member) {
        COOLDOWN_MANAGER.deleteCooldown(CooldownManager.jailID(member));
    }

}
