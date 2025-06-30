package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.broofthemonth.BroOfTheMonthSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class BOTMLeaderboard extends SlashCommand {
    private final BroOfTheMonthSQL botmSQL = new BroOfTheMonthSQL();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<BroOfTheMonthSQL.LeaderboardEntry> leaderboard = botmSQL.getBroScoreLeaderboard();

        int maxEntries = 10;
        int size = Math.min(maxEntries, leaderboard.size());

        StringBuilder desc = new StringBuilder();

        for (int i = 0; i < size; i++) {
            BroOfTheMonthSQL.LeaderboardEntry entry = leaderboard.get(i);

            String emote;
            switch (i) {
                case 0:
                    emote = ":first_place:";
                    break;
                case 1:
                    emote = ":second_place:";
                    break;
                case 3:
                    emote = ":third_place: ";
                    break;
                default:
                    emote = "";
                    break;
            }
            desc.append("`").append(entry.rank).append(".` <@").append(entry.uid).append("> - **").append(entry.totalPoints).append(" BroPoints** ").append(emote).append("\n");
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Bro Of The Month Leaderboard")
                .setDescription(desc.toString())
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp);

        String userId = event.getUser().getId();
        int userPosition = 0;
        int userPoints = 0;
        for (BroOfTheMonthSQL.LeaderboardEntry lbentry : leaderboard) {
            if (lbentry.uid.equals(userId)) {
                userPosition = lbentry.rank;
                userPoints = lbentry.totalPoints;
                break;
            }
        }

        if (userPosition > maxEntries) {
            embed.addField("Your Position", userPosition + ". " + userPoints + " BroPoints", false);

        }
        event.replyEmbeds(embed.build()).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("monthstats", "Check out the best people in the server based on BroPoints.");
    }
}
