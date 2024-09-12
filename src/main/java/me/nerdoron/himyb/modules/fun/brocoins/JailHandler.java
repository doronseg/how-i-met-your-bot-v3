package me.nerdoron.himyb.modules.fun.brocoins;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.bot.LoggingHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;

public class JailHandler {
    static final CooldownManager COOLDOWN_MANAGER = Global.COOLDOWN_MANAGER;
    private static final Logger logger = LoggingHandler.logger(JailHandler.class);

    public static MessageEmbed inJailEmbed(Member member) {
        String remaining = COOLDOWN_MANAGER.parseCooldown(CooldownManager.jailID(member));
        return new EmbedBuilder()
                .setTitle("\uD83D\uDC6E You're in jail!")
                .setDescription("You're serving time for: " + checkCharge(member) + ".\nYou will be released in " + remaining + ".")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }

    private static String checkCharge(Member member) {
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.jailID(member), "ROBBERY")) return "Robbery";
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.jailID(member), "BRIBERY")) return "Bribery";
        if (COOLDOWN_MANAGER.hasTag(CooldownManager.jailID(member), "RESISTING")) return "Resisting Arrest";

        logger.error("Unknown charge for member {}", member.getEffectiveName());
        return "ERROR";
    }

    public static boolean checkIfInJail(Member member) {
        return COOLDOWN_MANAGER.hasCooldown(CooldownManager.jailID(member));
    }

    public static void arrestMember(Member member, int timeInSeconds, String charge) {
        COOLDOWN_MANAGER.addCooldown(CooldownManager.jailID(member), charge, timeInSeconds);
    }

    public static void unJailMember(Member member) {
        COOLDOWN_MANAGER.deleteCooldown(CooldownManager.jailID(member));
    }

}
