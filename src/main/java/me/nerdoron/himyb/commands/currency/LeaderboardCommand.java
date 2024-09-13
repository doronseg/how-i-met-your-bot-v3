package me.nerdoron.himyb.commands.currency;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.fun.brocoins.Sorter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Map;

import static me.nerdoron.himyb.Global.BROCOINS_SQL;

public class LeaderboardCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Map<String, Integer> brocoins = BROCOINS_SQL.getBroCoins();
        Map<String, Integer> sorted = new Sorter().sortMapMaxLow(brocoins, 10);
        int longest = 0;
        for (Integer p : sorted.values()) {

            String a = p + "";
            if (a.length() > longest) {
                longest = a.length();
            }
        }

        StringBuilder users = new StringBuilder();
        for (String userID : sorted.keySet()) {
            int coins = sorted.get(userID);
            if (coins > 0) {
                String textCoins = String.valueOf(coins);
                for (int i = textCoins.length(); i < longest; i++) {
                    textCoins = String.format(" %s", textCoins);
                }
                users.append(" <@").append(userID).append("> | Total balance: `").append(textCoins).append("` ").append(Global.broCoin.getAsMention()).append("\n");

            }
        }

        EmbedBuilder emb = new EmbedBuilder();
        emb.setColor(Global.embedColor);
        emb.setTitle("Brocoins Leaderboard");
        emb.setDescription(users.toString());

        event.replyEmbeds(emb.build()).setEphemeral(false).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("leaderboard", "Check out the richest people in the server.");
    }
}
