package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class TosCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MessageEmbed odds = new EmbedBuilder().setTitle("ℹ️ Bot's TOS and Privacy Policy")
                .setDescription("Below you can find the bot's Terms of Service, and privacy policy:")
                .addField("Terms of Service", "[Click Here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/Terms-Of-Service)", true)
                .addField("Privacy Policy", "[Click Here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/Privacy-Policy)", true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
        event.replyEmbeds(odds).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("tos", "Bot's terms of service and privacy policy.");
    }
}
