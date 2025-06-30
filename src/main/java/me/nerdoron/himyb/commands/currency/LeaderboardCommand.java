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

    public int getPositionByUserId(String userId) {
        Map<String, Integer> brocoins = BROCOINS_SQL.getBroCoins();
        Map<String, Integer> sorted = new Sorter().sortMapNoLimit(brocoins);
        int position = 1;
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            String id = entry.getKey();
            if (id.equals(userId)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Map<String, Integer> brocoins = BROCOINS_SQL.getBroCoins();
        Map<String, Integer> sorted = new Sorter().sortMapMaxLow(brocoins, 10);

        String userId = event.getUser().getId();
        int userPosition = getPositionByUserId(userId);

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

        for (String sortedUserId : sorted.keySet()) {
            int coins = sorted.get(sortedUserId);
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
                    emote = " " + broCoin.getAsMention();
                }

                users.append("`").append(pos).append(".` **").append(textCoins).append("** ").append(emote).append("\t").append("<@!").append(sortedUserId).append(">").append("\n");
                pos += 1;
            }
        }

        EmbedBuilder emb = new EmbedBuilder();

        emb.setTitle("Top 10 Leaderboard " + broCoin.getAsMention())
                .setColor(Global.embedColor)
                .setDescription(users.toString())
                .setFooter(Global.footertext, Global.footerpfp);

        if (userPosition > 9) {
            emb.addField("Your Position", String.valueOf(userPosition), false);
        }

        event.replyEmbeds(emb.build()).setEphemeral(false).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("leaderboard", "Check out the richest people in the server.");
    }
}
