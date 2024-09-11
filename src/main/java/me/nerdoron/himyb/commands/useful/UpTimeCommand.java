package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.lang.management.ManagementFactory;

public class UpTimeCommand extends SlashCommand {
    public static String getUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeSec = uptime / 1000;
        long hours = uptimeSec / (60 * 60);
        long minutes = (uptimeSec / 60) - (hours * 60);
        long seconds = uptimeSec % 60;

        return (hours + " hours, " + minutes + " minutes, "
                + seconds + " seconds.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MessageEmbed uptime = new EmbedBuilder().setTitle("Bot Uptime")
                .setDescription("My uptime is " + getUptime()).setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
        event.replyEmbeds(uptime).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("uptime", "Show the bot's uptime.");
    }

}
