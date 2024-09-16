package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class OddsCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MessageEmbed odds = new EmbedBuilder().setTitle(Global.broCoin.getAsMention() + " RNG Odds for each minigame")
                .setDescription("For transparency, below you can find the winning odds of each minigame:")
                .addField("Gambling Games", "[Click Here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/Gambling-Odds)", true)
                .addField("Non-Gambling Games", "[Click Here!](https://github.com/doronseg/how-i-met-your-bot-v3/wiki/RNG-Chances-for-Commands-(Non%E2%80%90Gambling))", true)
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
        event.replyEmbeds(odds).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("odds", "Learn about the odds of each mini-game.");
    }
}
