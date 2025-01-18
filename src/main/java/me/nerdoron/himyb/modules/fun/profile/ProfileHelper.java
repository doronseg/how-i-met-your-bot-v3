package me.nerdoron.himyb.modules.fun.profile;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.commands.currency.LeaderboardCommand;
import me.nerdoron.himyb.modules.bot.AccessAmariAPI;
import me.nerdoron.himyb.modules.bot.CooldownManager;
import me.nerdoron.himyb.modules.fun.brocoins.BroCoinsSQL;
import me.nerdoron.himyb.modules.fun.brocoins.JailHelper;
import me.nerdoron.himyb.modules.fun.broofthemonth.BroOfTheMonthSQL;
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
        AccessAmariAPI api = new AccessAmariAPI();
        BroOfTheMonthSQL broOfTheMonthSQL = new BroOfTheMonthSQL();

        String jailStatus = JailHelper.checkIfInJail(member)
                ? String.format("%s remaining", COOLDOWN_MANAGER.parseCooldown(CooldownManager.jailID(member)))
                : "Not in jail";

        int position = leaderboardCommand.getPositionByUserId(member.getId());
        String positionText = (position == -1) ? "Not in leaderboard" : String.valueOf(position);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String joinDate = formatter.format(member.getTimeJoined());
        String creationDate = formatter.format(member.getTimeCreated());
        String id = member.getId();

        return new EmbedBuilder()
                .setTitle(String.format("%s's user profile", member.getUser().getEffectiveName()))
                .setThumbnail(member.getEffectiveAvatarUrl())
                .addField("ID", id, true)
                .addField("Account created", creationDate, true)
                .addField("Joined server on", joinDate, true)
                .addField("BroCash balance", String.format("%s %s", broCoinsSQL.getBroCash(member), Global.broCoin.getAsMention()), true)
                .addField("BroBank balance", String.format("%s %s", broCoinsSQL.getBroBank(member), Global.broCoin.getAsMention()), true)
                .addField("BroCoins Rank", positionText, true)
                .addField("Is in jail?", JailHelper.checkIfInJail(member) ? "Yes" : "No", true)
                .addField("Jail Status", jailStatus, true)
                .addField("Birthday", birthdayChecks.getBirthday(id), true)
                .addField("Level", api.getAmariLevel(id), true)
                .addField("Experience", api.getAmariExp(id), true)
                .addField("Server Rank", api.getLeaderboardPosition(id), true)
                .addField("Bro Of The Month Stats", "Stats for Bro Of The Month", false)
                .addField("Zitch Dogs", String.valueOf(broOfTheMonthSQL.getZitch(id)), true)
                .addField("Successful Crimes", String.valueOf(broOfTheMonthSQL.getCrimes(id)), true)
                .addField("Commendations", String.valueOf(broOfTheMonthSQL.getCommendation(id)), true)
                .addField("Coins Gained", String.format("%d %s", broOfTheMonthSQL.getCoins(id), Global.broCoin.getAsMention()), true)
                .addField("Coins Lost", String.format("%d %s", broOfTheMonthSQL.getLost(id), Global.broCoin.getAsMention()), true)
                .addField("Coins Spent", String.format("%d %s", broOfTheMonthSQL.getSpent(id), Global.broCoin.getAsMention()), true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }
}
