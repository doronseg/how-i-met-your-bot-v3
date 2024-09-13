package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class BugReportCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MessageEmbed bug = new EmbedBuilder().setTitle("⚠\uFE0F Bug report")
                .setDescription("Click [here](https://github.com/doronseg/how-i-met-your-bot-v3/wiki) to report a bug.")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
        event.replyEmbeds(bug).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("bugreport", "Report an issue with the bot.");
    }
}
