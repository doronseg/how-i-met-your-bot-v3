package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.bot.AccessAmariAPI;
import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.applications.EventModal;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class ApplyCommand extends SlashCommand {

    final EventModal eventModal = new EventModal();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String position = Objects.requireNonNull(event.getOption("position")).getAsString();
        if (!(event.isFromGuild())) {
            event.deferReply().setEphemeral(true).setContent("This command can only be executed in the server.")
                    .queue();
            return;
        }

        if (position.equals("Event Manager")) {
            event.replyModal(eventModal.modal).queue();
        }
        AccessAmariAPI api = new AccessAmariAPI();
        if (position.equals("Moderator")) {
            if (!(api.getAmariLevel(event.getMember().getId()).equals(String.valueOf(5)))) {
                event.reply("You must be at least level 5 to apply for moderator.").queue();
                return;
            }
            MessageEmbed mod = new EmbedBuilder().setTitle("ℹ️ Moderator application")
                    .setDescription("Click [here](https://doron.cc/himydapps) to apply for Moderator.")
                    .setColor(Global.embedColor)
                    .setFooter(Global.footertext, Global.footerpfp)
                    .build();
            event.replyEmbeds(mod).queue();
        }
    }

    @Override
    public SlashCommandData getSlash() {


        return Commands.slash("apply", "Apply for an open staff position.")
                .addOption(OptionType.STRING, "position", "What position would you like to apply to?",
                        true, true);
    }

}
