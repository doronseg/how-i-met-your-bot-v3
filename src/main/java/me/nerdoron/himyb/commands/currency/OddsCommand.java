package me.nerdoron.himyb.commands.currency;

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
        MessageEmbed ping = new EmbedBuilder().setTitle(Global.broCoin.getAsMention() + " Winning odds for each mini-game")
                .setDescription("For transparency, [here are the odds to each minigame](https://github.com/doronseg/how-i-met-your-bot-v3/wiki)")
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
        event.replyEmbeds(ping).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("odds", "Shows you the odds of each mini-game..");
    }
}
