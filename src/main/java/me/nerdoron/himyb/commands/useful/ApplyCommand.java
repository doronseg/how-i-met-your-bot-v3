package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.applications.EventModal;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class ApplyCommand extends SlashCommand {

    EventModal eventModal = new EventModal();

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
    }

    @Override
    public SlashCommandData getSlash() {
        SlashCommandData cmd = Commands.slash("apply", "Apply for an open staff position.")
                .addOption(OptionType.STRING, "position", "What position would you like to apply to?",
                        true, true);


        return cmd;
    }

}
