package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ContributeCommand extends SlashCommand {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MessageEmbed save = new EmbedBuilder().setTitle("Save how i met your bot")
                .setDescription("The bot costs $33.66 a year to run, we unfortunately can't keep paying for it on a yearly basis" +
                        ", so if you would like to keep the bot active, follow [this link](https://ko-fi.com/doronsegev) to contribute.\n\n" +
                        "We will not pocket any of the money, and full transparency will be provided.").setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
        event.replyEmbeds(save).queue();    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("contribute", "Look into how you can contribute to how i met your bot.");
    }
}
