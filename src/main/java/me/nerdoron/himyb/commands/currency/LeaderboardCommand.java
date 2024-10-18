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
import static me.nerdoron.himyb.Global.broCoin;

public class LeaderboardCommand extends SlashCommand {

    //TODO: find a way to make it more efficient, perhaps using a list and being able to get the index and then making the embed more clear
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
        int pos = 1;
        String emote;

        for (String userID : sorted.keySet()) {
            int coins = sorted.get(userID);
            if (coins > 0) {
                String textCoins = String.valueOf(coins);
                for (int i = textCoins.length(); i < longest; i++) {
                    textCoins = String.format(" %s", textCoins);
                }

                if (pos == 1) {
                    emote = " :first_place:";
                } else if (pos == 2) {
                    emote = " :second_place:";
                } else if (pos == 3) {
                    emote = " :third_place:";
                } else {
                    emote = " <:BroCoin:997169032522375230>";
                }
//                users.append(" <@").append(userID).append("> | Total balance: `").append(textCoins).append("` ").append(Global.broCoin.getAsMention()).append("\n");
                users.append(pos).append(". **`").append(textCoins).append("`** ").append(emote).append("\t").append("<@!").append(userID).append(">").append("\n");
                pos += 1;
            }
        }

        EmbedBuilder emb = new EmbedBuilder();

             emb.setTitle("Top 10 Leaderboard "+ broCoin.getAsMention())
                .setColor(Global.embedColor)
                .setDescription(users.toString())
                .setFooter(Global.footertext, Global.footerpfp)
                .build();

        event.replyEmbeds(emb.build()).setEphemeral(false).queue();

    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("leaderboard", "Check out the best people in the server.");
    }
}
