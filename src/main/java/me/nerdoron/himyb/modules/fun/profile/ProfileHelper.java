package me.nerdoron.himyb.modules.fun.profile;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.commands.currency.LeaderboardCommand;
import me.nerdoron.himyb.modules.bot.AccessAmariAPI;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.fun.brocoins.BroCoinsSQL;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import me.nerdoron.himyb.modules.useful.birthdays.BirthdayChecks;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.format.DateTimeFormatter;

import static me.nerdoron.himyb.Global.COOLDOWN_MANAGER;

public class ProfileHelper {

    public static MessageEmbed userProfile(Member member) {
        BroCoinsSQL broCoinsSQL = new BroCoinsSQL();
        BirthdayChecks birthdayChecks = new BirthdayChecks();
        LeaderboardCommand leaderboardCommand = new LeaderboardCommand();

        String jailStatus = JailHelper.checkIfInJail(member)
                ? String.format("%s remaining", COOLDOWN_MANAGER.parseCooldown(CooldownManager.jailID(member)))
                : "Not in jail";

        int position = leaderboardCommand.getPositionByUserId(member.getId());
        String positionText = (position == -1) ? "Not in leaderboard" : String.valueOf(position);

        AccessAmariAPI api = new AccessAmariAPI();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String joinDate = formatter.format(member.getTimeJoined());
        String creationDate = formatter.format(member.getTimeCreated());

        return new EmbedBuilder()
                .setTitle(String.format("%s's user profile", member.getUser().getEffectiveName()))
                .setThumbnail(member.getEffectiveAvatarUrl())
                .addField("ID", member.getId(), true)
                .addField("Account created", creationDate, true)
                .addField("Joined server on", joinDate, true)
                .addField("BroCash balance", String.format("%s %s", broCoinsSQL.getBroCash(member), Global.broCoin.getAsMention()), true)
                .addField("BroBank balance", String.format("%s %s", broCoinsSQL.getBroBank(member), Global.broCoin.getAsMention()), true)
                .addField("BroCoins Rank", positionText, true)
                .addField("Is in jail?", JailHelper.checkIfInJail(member) ? "Yes" : "No", true)
                .addField("Jail Status", jailStatus, true)
                .addField("Birthday", birthdayChecks.getBirthday(member.getId()), true)
                .addField("Level", api.getAmariLevel(member.getId()), true)
                .addField("Experience", api.getAmariExp(member.getId()), true)
                .addField("Server Rank", api.getLeaderboardPosition(member.getId()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }
}
