package me.nerdoron.himyb.commands.useful;

import me.nerdoron.himyb.modules.bot.SlashCommand;
import me.nerdoron.himyb.modules.useful.birthdays.BirthdayChecks;
import me.nerdoron.himyb.modules.useful.birthdays.BirthdayModal;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class BirthdayCommand extends SlashCommand {

    final BirthdayModal birthdayModal = new BirthdayModal();
    BirthdayChecks birthdayChecks = new BirthdayChecks();

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();
        assert member != null;
        if (birthdayChecks.hasBirthday(member.getId())) {
            event.reply("You already have a birthday set, if you need help contact us using an admin ticket.").setEphemeral(true).queue();
            return;
        }

        event.replyModal(birthdayModal.bdayModal).queue();

    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("birthday", "Sets your birthday.");
    }


}
