package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.selfpromo.SelfPromoModal;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class SelfPromoCommand extends SlashCommand {

    final SelfPromoModal selfPromoModal = new SelfPromoModal();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        assert member != null;

        if (!(member.getRoles())
                .contains(Objects.requireNonNull(event.getGuild()).getRoleById("850464921040846928"))) {
            event.deferReply().setEphemeral(true).setContent(
                            "Sorry, only members who have reached level 10 are able to submit self promotion links. To check your rank use `=r` in <#850437596487483443>")
                    .queue();
            return;
        }

        event.replyModal(selfPromoModal.modal).queue();
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("selfpromo", "Submit a self promotion link");
    }
}
